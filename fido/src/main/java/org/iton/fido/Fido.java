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

package org.iton.fido;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;
import android.util.Log;

import org.iton.fido.service.FidoBinder;
import org.iton.fido.service.FidoService;
import org.iton.fido.util.FidoUtil;

/**
 * Created by ITON Solutions on 26/09/2019.
 */
public class Fido extends Application {

    private static final String TAG = Application.class.getName();

    private static Application app;
    private FidoService service;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Log.d(TAG, "Create Fido application");

        Intent intent = new Intent(this, FidoService.class);
        bindService(intent, new ServiceListener(), Context.BIND_AUTO_CREATE);
    }

    private class ServiceListener implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            Log.d(TAG,"Fido service started");
            service = ((FidoBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG,"Fido service destroyed");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        try {
            FidoUtil.checkDisplaySize(getApplicationContext(), config);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public FidoService getService() {
        return service;
    }
}
