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

package org.iton.fido.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.iton.fido.Fido;
import org.iton.fido.R;
import org.iton.fido.core.DeleteRegistrationHandler;
import org.iton.fido.core.DeleteRegistrationListener;
import org.iton.fido.service.FidoService;
import org.iton.fido.ui.authenticate.AuthenticateActivity;
import org.iton.fido.ui.onboard.OnboardActivity;
import org.iton.fido.ui.register.RegisterActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private Button delete;
    private Button register;
    private Button authenticate;
    private Fido app;
    private FidoService service;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        app = (Fido) getApplication();
        service  = app.getService();

        delete = findViewById(R.id.delete);
        delete.setOnClickListener(new OnDeleteListener());

        register = findViewById(R.id.register);
        register.setOnClickListener(new OnRegisterListener());

        authenticate = findViewById(R.id.authenticate);
        authenticate.setOnClickListener(new OnAuthenticateListener());


    }

    class OnDeleteListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            DeleteRegistrationHandler handler = new DeleteRegistrationHandler(app,
                    "vladimir",
                    "https://192.168.1.32:8443/webauthn/api/v1/delete-account");
            handler.handle(new DeleteRegistrationListener() {
                @Override
                public void onDelete(String response) {
                    Log.d(TAG, String.format("Response: %s", response));
                }
            });
        }
    }

    class OnRegisterListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            startActivityAndFinish(new Intent(getBaseContext(), RegisterActivity.class));
        }
    }

    class OnAuthenticateListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            startActivityAndFinish(new Intent(getBaseContext(), AuthenticateActivity.class));
        }
    }

    private void startActivityAndFinish(Intent intent) {
        startActivity(intent);
        finish();
    }
}
