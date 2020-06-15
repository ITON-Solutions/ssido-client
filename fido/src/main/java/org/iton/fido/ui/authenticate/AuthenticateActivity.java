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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.iton.fido.Fido;
import org.iton.fido.R;
import org.iton.fido.ui.home.MainActivity;
import org.iton.fido.ui.register.FinishRegistrationHandler;
import org.iton.fido.ui.register.StartRegistrationHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class AuthenticateActivity extends Activity implements ZXingScannerView.ResultHandler  {

    private static final String TAG = AuthenticateActivity.class.getName();

    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private ZXingScannerView scannerView;
    private boolean flash;
    private boolean autoFocus;
    private ArrayList<Integer> selectedIndices;
    private int cameraId = -1;
    private Fido app;

   @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_authenticate);
        app = (Fido) getApplication();
        ViewGroup contentFrame = findViewById(R.id.window);
        scannerView = new ZXingScannerView(this);
        contentFrame.addView(scannerView);

        if(state != null) {
            flash           = state.getBoolean(FLASH_STATE, false);
            autoFocus       = state.getBoolean(AUTO_FOCUS_STATE, true);
            selectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            cameraId        = state.getInt(CAMERA_ID, -1);
        } else {
            flash           = false;
            autoFocus       = true;
            selectedIndices = null;
            cameraId        = -1;
        }
        setupFormats();
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera(cameraId);
        scannerView.setFlash(flash);
        scannerView.setAutoFocus(autoFocus);
    }

    @Override
    public void handleResult(Result result) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(app, notification);
            ringtone.play();
            ceremony(result.getText());
        } catch (URISyntaxException e) {
            Log.e(TAG, String.format("Error: %s", e.getMessage()));
        }
    }

    public static boolean hasCamera(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void ceremony(String message) throws URISyntaxException {
        URI uri = new URI(message);
        if (!uri.getScheme().equals("wss")) {
            return;
        }

        String sessionId = message.substring(message.lastIndexOf("/") + 1);
        String action = message.replace(sessionId, "authenticate");
        Log.d(TAG, String.format("Received sessionId: %s from: %s", sessionId, uri.getAuthority()));

        StartAuthenticationHandler start = new StartAuthenticationHandler(app, sessionId, action);
        start.handle((authentication, request) -> {
            Log.d(TAG, String.format("Received action: %s", authentication));
            FinishAuthenticationHandler finish = new FinishAuthenticationHandler(app, request, authentication);
            finish.handle(() -> startActivityAndFinish(new Intent(getBaseContext(), MainActivity.class)));
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<>();
        if(selectedIndices == null || selectedIndices.isEmpty()) {
            selectedIndices = new ArrayList<>();
            for(int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                selectedIndices.add(i);
            }
        }

        for(int index : selectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if(scannerView != null) {
            scannerView.setFormats(formats);
        }
    }

    private void startActivityAndFinish(Intent intent) {
        startActivity(intent);
        finish();
    }
}
