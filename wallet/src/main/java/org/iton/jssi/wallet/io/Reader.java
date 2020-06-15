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

import org.iton.jssi.store.PreexistingEntityException;
import org.iton.jssi.wallet.Wallet;
import org.iton.jssi.wallet.crypto.Crypto;
import org.iton.jssi.wallet.record.WalletRecord;
import org.iton.jssi.wallet.util.Utils;
import org.libsodium.jni.SodiumException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.ObservableEmitter;

/**
 *
 * @author ITON Solutions
 */
public class Reader implements Runnable {

    private static final String TAG = Reader.class.getName();

    private Wallet wallet;
    private ObservableEmitter<Integer> emitter;
    private IOConfig config;

    public Reader(Wallet wallet, IOConfig config, ObservableEmitter<Integer> emitter) {
        this.wallet = wallet;
        this.config = config;
        this.emitter = emitter;
    }

    @Override
    public void run() {
        try {
            File file = new File(config.path);
            FileInputStream fis = new FileInputStream(file);
            ByteBuffer buffer;

            try (FileChannel channel = fis.getChannel()) {
                buffer = ByteBuffer.allocate((int) channel.size());
                channel.read(buffer);
            }
            buffer.flip();

            byte[] headerBytes = new byte[buffer.order(ByteOrder.LITTLE_ENDIAN).getInt()];
            buffer.get(headerBytes);

            Header header = new Header().deserialize(headerBytes, config.key);

            if (header.getVersion() != 0) {
                IOException e = new IOException(String.format("Invalid version %d, mus be 0", header.getVersion()));
                emitter.onError(e);
            }

            buffer = new Decrypter(header.getDerivationData().deriveMasterKey(),
                    header.getNonce(),
                    header.getChunkSize()).decrypt(buffer);

            byte[] hashBytes = new byte[0x20];
            buffer.get(hashBytes);

            byte[] hash = Crypto.hash256(headerBytes);

            if (!Arrays.equals(hashBytes, hash)) {
                IOException e = new IOException(String.format("Invalid hash %s, expected %s", Utils.toHex(hash), Utils.toHex(hashBytes)));
                emitter.onError(e);
            }

            List<WalletRecord> records = new ArrayList<>();
            int recordSize = buffer.order(ByteOrder.LITTLE_ENDIAN).getInt();

            while (recordSize > 0) {
                byte[] record_bytes = new byte[recordSize];
                buffer.get(record_bytes);
                records.add(new WalletRecord().deserialize(record_bytes));
                recordSize = buffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
            }

            int count = 0;
            for (WalletRecord record : records) {
                wallet.addRecord(record);
                emitter.onNext(++count);
            }
            emitter.onComplete();

        } catch (IOException | PreexistingEntityException | SodiumException e) {
            Log.e(TAG, String.format("Error %s", e.getMessage()));
            emitter.onError(e);
        }
    }
}
