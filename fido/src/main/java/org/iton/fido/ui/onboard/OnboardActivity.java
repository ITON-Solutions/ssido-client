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

package org.iton.fido.ui.onboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.iton.fido.Fido;
import org.iton.fido.R;
import org.iton.fido.service.FidoService;
import org.iton.fido.store.StoreService;
import org.iton.fido.ui.register.RegisterActivity;
import org.iton.jssi.did.Did;
import org.iton.jssi.wallet.WalletService;
import org.iton.jssi.wallet.record.WalletRecord;
import org.libsodium.jni.SodiumException;

import java.io.IOException;

public class OnboardActivity extends AppCompatActivity {

    private static final String TAG = OnboardActivity.class.getName();

    private Button done;
    private EditText username;
    private EditText display_name;
    private Fido app;
    private FidoService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (Fido) getApplication();
        service  = app.getService();
        setContentView(R.layout.activity_onboard);
        username = findViewById(R.id.username);
        display_name = findViewById(R.id.display_name);
        done = findViewById(R.id.button);
        done.setOnClickListener(new OnDoneListener());
    }

    class OnDoneListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            service.getStoreService().setUsername(username.getText().toString());

            try {
                WalletRecord record = service.getWalletService().getWallet().findRecord(Did.TYPE, "EjABoD8BV1mxQhfTccCKw4");
                Did did = new ObjectMapper().readValue(record.getValue(), Did.class);
                service.getStoreService().setDid(did);
                Log.d(TAG, String.format("Store Did: {did: %s, verkey: %s}", did.did, did.verkey));
                startActivityAndFinish(new Intent(getBaseContext(), RegisterActivity.class));
            } catch (SodiumException | IOException e){
                Log.e(TAG, String.format("Error: %s", e.getCause().getMessage()));
            }
        }
    }

    private void startActivityAndFinish(Intent intent) {
        startActivity(intent);
        finish();
    }
}
