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

package org.iton.fido.ui.register;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.bitcoinj.core.Base58;
import org.iton.fido.Fido;
import org.iton.fido.model.RegistrationRequest;
import org.iton.fido.wss.WSClient;
import org.iton.fido.wss.WSService;
import org.iton.fido.wss.event.Connected;
import org.iton.fido.wss.event.Event;
import org.iton.jssi.crypto.Keys;
import org.iton.jssi.did.Did;
import org.iton.jssi.wallet.record.WalletRecord;
import org.libsodium.jni.SodiumException;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by ITON Solutions on 07/10/2019.
 */
public class FinishRegistrationHandler {

    private static final String TAG = FinishRegistrationHandler.class.getName();

    private RegistrationRequest request;
    private Fido app;
    private String action;


    public FinishRegistrationHandler(@NonNull Fido app, @NonNull RegistrationRequest request, @NonNull String action){
        this.app = app;
        this.request = request;
        this.action = action;
    }

    public void handle(FinishRegistrationListener listener) {

        Request.Builder builder = new Request.Builder()
                .get()
                .url(action);


        OkHttpClient client = new WSClient().getClient();
        WSService ws = new WSService(client, builder.build());

        ws.open().subscribe(new Observer<Event>() {

            String origin = action.substring(0, action.lastIndexOf("/"));

            @Override
            public void onSubscribe(Disposable disposable) {
                Log.d(TAG, "Received SUBSCRIBED event");
            }

            @Override
            public void onNext(Event event) {

                Log.d(TAG, String.format("Received %s", event.toString()));
                if (event instanceof Connected) {
                    try {
                        RegistrationResponse response = new RegistrationResponse(request);
                        Did did = app.getService().getStoreService().getDid();
                        WalletRecord record = app.getService().getWalletService().getWallet().findRecord(Keys.TYPE, did.verkey);
                        Keys keys = new ObjectMapper().readValue(record.getValue(), Keys.class);
                        Log.d(TAG, String.format("Keys: verkey: %s signkey: %s", keys.verkey, keys.signkey));
                        String json = response.finish(Base58.decode(keys.verkey), Base58.decode(keys.signkey), origin);
                        ws.sendMessage(event.sender(), json).subscribe();
                        event.sender().close(1000, "Finish registration request close");
                    } catch (SodiumException | IOException e) {
                        Log.e(TAG, String.format("Error: %s", e.getCause().getMessage()));
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Received COMPLETED event");
                listener.onFinish();
            }
        });
    }
}
