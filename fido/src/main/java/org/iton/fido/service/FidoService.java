/*
 *
 *  The MIT License
 *
 *  Copyright 2019 ITON Solutions.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package org.iton.fido.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.iton.fido.Fido;
import org.iton.fido.R;
import org.iton.fido.store.StoreService;
import org.iton.fido.ui.launcher.LauncherActivity;
import org.iton.fido.ui.permission.Constants;
import org.iton.fido.ui.permission.Permission;
import org.iton.fido.ui.permission.PermissionResponse;
import org.iton.fido.ui.permission.PermissionResultCallback;
import org.iton.jssi.crypto.CryptoService;
import org.iton.jssi.store.DatabaseHelper;
import org.iton.jssi.wallet.Wallet;
import org.iton.jssi.wallet.WalletCredential;
import org.iton.jssi.wallet.WalletService;
import org.libsodium.jni.NaCl;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ITON Solutions on 26/09/2019.
 */
public class FidoService extends Service {

    private static final String TAG = FidoService.class.getName();

    private final IBinder binder = new FidoBinder(this);
    private Fido app;
    private WalletService walletService;
    private CryptoService cryptoService;
    private StoreService storeService;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        Log.d(TAG, "Fido service unbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Start Fido service");
        app = (Fido) getApplication();

        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        requestPermissions(true, setPermissionOn(app, permissions));
    }

    private void requestPermissions(boolean async, List<String> permissions) {

        if (async) {

            AsyncTask task = new AsyncTask<Object, Object, Boolean>() {

                @Override
                protected Boolean doInBackground(Object... params) {
                    PermissionResponse response = null;
                    try {
                        response = Permission.getPermission(app,
                                permissions.toArray(new String[permissions.size()]),
                                Constants.DEFAULT_REQUEST_CODE,
                                getResources().getString(R.string.app_name),
                                getResources().getString(R.string.permission_needs),
                                R.drawable.ic_launcher).call();
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                    }

                    return response.isGranted();
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    super.onPostExecute(result);
                    Toast.makeText(FidoService.this, "Permissions Granted " + result, Toast.LENGTH_SHORT).show();
                    launch();
                }
            };
            task.execute();

        } else {

            Permission.getPermission(app,
                    permissions.toArray(new String[permissions.size()]),
                    Constants.DEFAULT_REQUEST_CODE,
                    getResources().getString(R.string.app_name),
                    getResources().getString(R.string.permission_needs),
                    R.drawable.ic_launcher).enqueue(new PermissionResultCallback() {
                @Override
                public void onComplete(PermissionResponse response) {
                    Toast.makeText(FidoService.this, "Permissions Granted " + response.isGranted(), Toast.LENGTH_SHORT).show();
                    launch();
                }
            });
        }
    }

    private void launch() {

        Log.d(TAG, "Start Wallet service");
        WalletCredential credential = new WalletCredential("ubicua", "wallet_key");
        DatabaseHelper helper = new DatabaseHelper("ubicua.db", app);
        walletService = new WalletService(app, credential, helper);

        walletService.open().subscribeOn(Schedulers.newThread()).subscribe(new Observer<Wallet>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                Log.d(TAG, "Received SUBSCRIBED event");
                NaCl.sodium();
            }

            @Override
            public void onNext(Wallet wallet) {
                Log.d(TAG, String.format("Wallet opened: id=%s", wallet.getId()));
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, String.format("Error: %s", e.getMessage()));
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Received COMPLETED event");
                Log.d(TAG, "Start Crypto service");
                cryptoService = new CryptoService();
                storeService = new StoreService(app);
                Intent intent = new Intent();
                intent.setAction(LauncherActivity.BROADCAST_ACTION);
                sendBroadcast(intent);
                Log.d(TAG, "Send broadcast");
            }
        });

    }

    private List setPermissionOn(Context context, List<String> permissions) {

        List<String> result = new ArrayList();

        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                result.add(permission);
            }
        }
        return result;
    }

    private boolean hasPermission(Context context, String permission) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public WalletService getWalletService() {
        return walletService;
    }

    public CryptoService getCryptoService() {
        return cryptoService;
    }

    public StoreService getStoreService() {
        return storeService;
    }
}
