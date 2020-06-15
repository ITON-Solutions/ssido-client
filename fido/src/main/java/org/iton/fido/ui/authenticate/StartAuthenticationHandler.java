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

package org.iton.fido.ui.authenticate;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.iton.fido.Fido;
import org.iton.fido.model.StartAuthenticationRequest;
import org.iton.fido.model.StartRegistrationRequest;
import org.iton.fido.model.User;
import org.iton.fido.ui.register.StartRegistrationListener;
import org.iton.fido.util.JacksonCodecs;
import org.iton.fido.wss.WSClient;
import org.iton.fido.wss.WSService;
import org.iton.fido.wss.event.Connected;
import org.iton.fido.wss.event.Disconnected;
import org.iton.fido.wss.event.Event;
import org.iton.fido.wss.event.StringMessage;
import org.iton.jssi.did.Did;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by ITON Solutions on 07/10/2019.
 */
public class StartAuthenticationHandler {

    private static final String TAG = StartAuthenticationHandler.class.getName();

    private Fido app;
    private String sessionId;
    private String action;

    public StartAuthenticationHandler(@NonNull Fido app, @NonNull String sessionId, @NonNull String action){
        this.app = app;
        this.sessionId = sessionId;
        this.action = action;
    }

    public void handle(StartAuthenticationListener listener){

        Request.Builder builder = new Request.Builder()
                .get()
                .url(action);

        OkHttpClient client = new WSClient().getClient();
        WSService ws = new WSService(client, builder.build());

        ws.open().subscribe(new Observer<Event>() {

            StartAuthenticationRequest request;

            @Override
            public void onSubscribe(Disposable disposable) {
                Log.d(TAG, "Received SUBSCRIBED event");
            }

            @Override
            public void onNext(Event event) {

                Log.d(TAG, String.format("Received %s" , event.toString()));
                if(event instanceof Connected){

                    Did did = app.getService().getStoreService().getDid();
                    String username = app.getService().getStoreService().getUsername();

                    try {
                        User user = new User(sessionId, username, "Vladimir", did.did);
                        ObjectMapper mapper = JacksonCodecs.json();
                        String json = mapper.writeValueAsString(user);
                        ws.sendMessage(event.sender(), json).subscribe();
                    } catch (JsonProcessingException e) {
                        Log.e(TAG, String.format("Error: %s", e.getMessage()));
                    }

                } else if(event instanceof StringMessage){

                    try {
                        ObjectMapper mapper = JacksonCodecs.json();
                        request = mapper.readValue(((StringMessage) event).message(), StartAuthenticationRequest.class);
                        if(request.isSuccess()){
                            event.sender().close(1000, "Start authentication request close");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, String.format("Error: %s", e.getMessage()));
                        event.sender().close(2000, e.getMessage());
                    }
                } else if(event instanceof Disconnected){
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, String.format("Error: %s", e.getMessage()));
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Received COMPLETED event");
                if(request != null) {
                    listener.onStart(request.getAction(), request.getRequest());
                }
            }
        });
    }
}
