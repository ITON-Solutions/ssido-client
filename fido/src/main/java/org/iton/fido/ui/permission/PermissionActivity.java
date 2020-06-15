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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.iton.fido.R;


public class PermissionActivity extends AppCompatActivity {

    private static final String TAG = PermissionActivity.class.getName();

    private ResultReceiver resultReceiver;

    @Override
    protected void onCreate(Bundle boundle) {
        super.onCreate(boundle);
        setContentView(R.layout.activity_permission);

        if(getIntent() != null) {
            resultReceiver = getIntent().getParcelableExtra(Constants.RESULT_RECEIVER);
            String[] permissions = getIntent().getStringArrayExtra(Constants.PERMISSIONS_ARRAY);
            int requestCode = getIntent().getIntExtra(Constants.REQUEST_CODE, Constants.DEFAULT_REQUEST_CODE);
            if(!hasPermissions(permissions)) {
                ActivityCompat.requestPermissions(this, permissions, requestCode);
            }else {
                onComplete(requestCode, permissions, new int[]{PackageManager.PERMISSION_GRANTED});
            }
        }else {
            finish();
        }
    }

    @SuppressLint("RestrictedApi")
    private void onComplete(int requestCode, String[] permissions, int[] grantResults) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(Constants.PERMISSIONS_ARRAY, permissions);
        bundle.putIntArray(Constants.GRANT_RESULT, grantResults);
        bundle.putInt(Constants.REQUEST_CODE, requestCode);
        resultReceiver.send(requestCode, bundle);
        finish();

    }

    private boolean hasPermissions(String[] permissions) {
        boolean result = true;
        for(String permission: permissions) {
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onComplete(requestCode, permissions, grantResults);
    }
}
