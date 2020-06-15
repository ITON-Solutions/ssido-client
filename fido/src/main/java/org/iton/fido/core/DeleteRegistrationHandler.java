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

package org.iton.fido.core;

import android.util.Log;

import androidx.annotation.NonNull;

import org.iton.fido.Fido;
import org.iton.fido.http.HTTPClient;
import org.iton.fido.http.HTTPService;
import org.iton.fido.http.event.Event;
import org.iton.fido.http.event.Failure;
import org.iton.fido.http.event.Result;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by ITON Solutions on 07/10/2019.
 */
public class DeleteRegistrationHandler {

    private static final String TAG = DeleteRegistrationHandler.class.getName();

    private Fido app;
    private String username;
    private String action;

    public DeleteRegistrationHandler(@NonNull Fido app, @NonNull String username, @NonNull String action){
        this.app = app;
        this.username = username;
        this.action = action;
    }

    public void handle(DeleteRegistrationListener listener){

        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .build();

        Request.Builder builder = new Request.Builder()
                .url(action)
                .post(body);

        OkHttpClient client = new HTTPClient().getClient();
        HTTPService http = new HTTPService(client, builder.build());

        http.send().subscribe(new Observer<Event>() {

            String response;

            @Override
            public void onSubscribe(Disposable disposable) {
                Log.d(TAG, "Received SUBSCRIBED event");
            }

            @Override
            public void onNext(Event event) {

                Log.d(TAG, String.format("Received %s" , event.toString()));
                if(event instanceof Result){
                    response = ((Result) event).response();
                } else if(event instanceof Failure){
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, String.format("Error: %s", e.getMessage()));
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Received COMPLETED event");
                if(response != null) {
                    listener.onDelete(response);
                }
            }
        });
    }
}
