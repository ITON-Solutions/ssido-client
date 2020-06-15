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

package org.iton.fido.ui.permission;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.os.ResultReceiver;

public class PermissionRequest {

    private Context context;
    private String[] permissions;
    private int requestCode;
    private String notificationTitle;
    private String notificationText;
    private int icon;

    private PermissionResponse response;

    PermissionRequest(Context context, String[] permissions, int requestCode, String notificationTitle, String notificationText, int icon) {

        this.context           = context;
        this.permissions       = permissions;
        this.requestCode       = requestCode;
        this.notificationTitle = notificationTitle;
        this.notificationText  = notificationText;
        this.icon              = icon;
    }

    public PermissionResponse call() throws InterruptedException {
        if (permissions.length != 0) {
            final Object lock = new Object();
            NotificationHelper.sendNotification(context, permissions, requestCode,
                    notificationTitle, notificationText, icon, new ResultReceiver(new Handler(Looper.getMainLooper())) {
                        @SuppressLint("RestrictedApi")
                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            super.onReceiveResult(resultCode, resultData);
                            int[] grantResult    = resultData.getIntArray(Constants.GRANT_RESULT);
                            String[] permissions = resultData.getStringArray(Constants.PERMISSIONS_ARRAY);
                            response = new PermissionResponse(permissions, grantResult, resultCode);

                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        }
                    });
            synchronized (lock) {
                lock.wait();
            }
        } else {
            response = new PermissionResponse(permissions, new int[]{PackageManager.PERMISSION_GRANTED}, requestCode);
        }
        return response;
    }

    public void enqueue(final PermissionResultCallback callback) {
        if (permissions.length != 0) {
            NotificationHelper.sendNotification(context, permissions, requestCode,
                    notificationTitle, notificationText, icon, new ResultReceiver(new Handler()) {
                        @Override
                        @SuppressLint("RestrictedApi")
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            super.onReceiveResult(resultCode, resultData);
                            int[] grantResult = resultData.getIntArray(Constants.GRANT_RESULT);
                            String[] permissions = resultData.getStringArray(Constants.PERMISSIONS_ARRAY);
                            response = new PermissionResponse(permissions, grantResult, resultCode);
                            callback.onComplete(new PermissionResponse(permissions, grantResult, resultCode));
                        }
                    });
        } else {
            callback.onComplete(new PermissionResponse(permissions, new int[]{PackageManager.PERMISSION_GRANTED}, requestCode));
        }
    }
}
