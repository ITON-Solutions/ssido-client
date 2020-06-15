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

package org.iton.fido.ui.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.iton.fido.Fido;
import org.iton.fido.R;
import org.iton.fido.service.FidoService;
import org.iton.fido.store.StoreService;
import org.iton.fido.ui.home.MainActivity;
import org.iton.fido.ui.onboard.OnboardActivity;
import org.iton.jssi.crypto.Keys;
import org.iton.jssi.did.Did;
import org.iton.jssi.wallet.record.WalletRecord;
import org.libsodium.jni.SodiumException;

import java.io.IOException;

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = LauncherActivity.class.getName();

    public static final String BROADCAST_ACTION = "WALLET_SERVICE";
    private ProgressBar progress;
    private ServiceBroadCastReceiver broadcast;
    private IntentFilter filter;
    private Fido app = null;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        app = (Fido) getApplication();
        setContentView(R.layout.activity_launcher);
        progress = findViewById(R.id.progress);
        broadcast = new ServiceBroadCastReceiver();
        filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION);
    }

    private class ServiceBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Wallet service started");
            progress.setVisibility(View.INVISIBLE);

            try {

                FidoService service  = app.getService();
                StoreService store = service.getStoreService();
                if(store.isFirstLaunch()){
                    startActivityAndFinish(new Intent(getBaseContext(), OnboardActivity.class));
                } else {
                    Did did = store.getDid();
                    WalletRecord record = service.getWalletService().getWallet().findRecord(Keys.TYPE, did.verkey);
                    Keys keys = new ObjectMapper().readValue(record.getValue(), Keys.class);
                    Log.d(TAG, String.format("Keys: verkey: %s signkey: %s", keys.verkey, keys.signkey));
//                    startActivityAndFinish(new Intent(getBaseContext(), QRCodeActivity.class));
                    startActivityAndFinish(new Intent(getBaseContext(), MainActivity.class));
                }

            } catch(SodiumException | IOException e){
                Log.e(TAG, String.format("Error: %s", e.getCause().getMessage()));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcast, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcast);
    }

    private void startActivityAndFinish(Intent intent) {
        startActivity(intent);
        finish();
    }
}
