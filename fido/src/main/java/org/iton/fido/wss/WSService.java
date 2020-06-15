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

package org.iton.fido.wss;

/**
 * Created by ITON Solutions on 19/09/2019.
 */

import android.util.Log;

import androidx.annotation.NonNull;

import org.iton.fido.wss.event.BinaryMessage;
import org.iton.fido.wss.event.Connected;
import org.iton.fido.wss.event.Disconnected;
import org.iton.fido.wss.event.Event;
import org.iton.fido.wss.event.StringMessage;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * This class allows to retrieve messages from websocket
 */
public class WSService {

    private static final String TAG = WSService.class.getName();

    @NonNull
    private final OkHttpClient client;
    @NonNull
    private final Request request;

    /**
     * Create instance of {@link WSService}
     *
     * @param client  {@link OkHttpClient} instance
     * @param request request to connect to websocket
     */
    public WSService(@NonNull OkHttpClient client, @NonNull Request request) {
        this.client = client;
        this.request = request;
    }

    /**
     * Returns observable that connected to a websocket and returns {@link Event}'s
     *
     * @return Observable that connects to websocket
     */
    @NonNull
    public Observable<Event> open() {
        return Observable.create(new ObservableOnSubscribe<Event>() {

            @Override
            public void subscribe(final ObservableEmitter<Event> emitter) {

                client.newWebSocket(request, new WebSocketListener() {
                    @Override
                    public void onOpen(@NonNull WebSocket sender, @NonNull Response response) {
                        emitter.onNext(new Connected(sender));
                    }

                    @Override
                    public void onMessage(@NonNull WebSocket sender, @NonNull String message) {
                        emitter.onNext(new StringMessage(sender, message));
                    }

                    @Override
                    public void onMessage(@NonNull WebSocket sender, @NonNull ByteString bytes) {
                        emitter.onNext(new BinaryMessage(sender, bytes.toByteArray()));
                    }

                    @Override
                    public void onClosing(@NonNull WebSocket sender, int code, @NonNull String reason) {
                        super.onClosing(sender, code, reason);
                        final ServerRequestedCloseException exception = new ServerRequestedCloseException(code, reason);
                        emitter.onNext(new Disconnected(sender, exception));
                        emitter.onComplete();
                    }

                    @Override
                    public void onClosed(@NonNull WebSocket sender, int code, @NonNull String reason) {
                        final ServerRequestedCloseException exception = new ServerRequestedCloseException(code, reason);
                        emitter.onNext(new Disconnected(sender, exception));
                        emitter.onComplete();
                    }

                    @Override
                    public void onFailure(@NonNull WebSocket sender, @NonNull Throwable throwable, Response response) {
                        if (response != null) {
                            final ServerHttpException exception = new ServerHttpException(response);
                            emitter.onNext(new Disconnected(sender, exception));
                        } else {
                            emitter.onNext(new Disconnected(sender, throwable));
                        }
                        emitter.onComplete();
                    }
                });
            }
        });
    }

    /**
     * Enqueue message to send
     *
     * @param sender  connection event that is used to send message
     * @param message message to send
     * @return Single that returns true if message was enqueued
     */
    @NonNull
    public Single<Boolean> sendMessage(final @NonNull WebSocket sender, final @NonNull String message) {
        return Single.fromCallable(() -> {
            Log.d(TAG, String.format("Send message: %s", message));
            return sender.send(message);
        });
    }
}

