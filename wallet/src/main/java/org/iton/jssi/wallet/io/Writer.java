/*
 * The MIT License
 *
 * Copyright 2019 ITON Solutions.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.iton.jssi.wallet.io;

import android.util.Log;

import org.iton.jssi.wallet.Wallet;
import org.iton.jssi.wallet.crypto.Crypto;
import org.iton.jssi.wallet.crypto.KeyDerivationData;
import org.iton.jssi.wallet.record.WalletRecord;
import org.iton.jssi.wallet.util.Utils;
import org.libsodium.jni.SodiumException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import io.reactivex.ObservableEmitter;

public class Writer implements Runnable {

    private static final String TAG = Writer.class.getName();
    
    private Wallet wallet;
    private ObservableEmitter<Integer> emitter;
    private IOConfig config;

    public Writer(Wallet wallet, IOConfig config, ObservableEmitter<Integer> emitter) {
        this.wallet = wallet;
        this.config = config;
        this.emitter = emitter;
    }

    @Override
    public void run() {

        try {
            File file = new File(config.path);
            if (!file.mkdirs()) {
                Log.e(TAG, "Directory not created");
            }

            int count = (int) wallet.count();
            Log.d(TAG, String.format("Total registers in database %d", count));

            KeyDerivationData data = new KeyDerivationData(config.key);
            Header header = new Header();
            byte[] header_bytes = header.serialize(data);
            List<WalletRecord> records = wallet.findAllRecords();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(Crypto.hash256(header_bytes));

            for (WalletRecord record : records) {
                byte[] decrypted = record.serialize();
                baos.write(Utils.toBytes(decrypted.length));
                baos.write(decrypted);
                emitter.onNext(count--);
            }

            baos.write(Utils.toBytes(0));

            byte[] encrypted = new Encrypter(header.getDerivationData().deriveMasterKey(),
                    header.getNonce(),
                    header.getChunkSize()).encrypt(ByteBuffer.wrap(baos.toByteArray()));

            baos = new ByteArrayOutputStream();
            baos.write(Utils.toBytes(header_bytes.length));
            baos.write(header_bytes);
            baos.write(encrypted);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                baos.writeTo(fos);
            }

            emitter.onComplete();

        } catch (IOException | SodiumException e) {
            Log.e(TAG, String.format("Error %s", e.getMessage()));
            emitter.onError(e);
        }
    }
}
