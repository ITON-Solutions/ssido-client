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
package org.iton.jssi.wallet;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.iton.jssi.store.DatabaseHelper;
import org.iton.jssi.store.MetadataDao;
import org.iton.jssi.store.model.Metadata;
import org.iton.jssi.wallet.crypto.KeyDerivationData;
import org.iton.jssi.wallet.crypto.Keys;
import org.iton.jssi.wallet.crypto.KeysMetadata;
import org.iton.jssi.wallet.io.IOConfig;
import org.libsodium.api.Crypto_randombytes;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static org.libsodium.jni.SodiumConstants.CRYPTO_AEAD_CHACHA20POLY1305_IETF_KEYBYTES;
/**
 *
 * @author ITON Solutions
 */
public class WalletService {
    
    private static final String TAG = WalletService.class.getName();
    
    private KeysMetadata keysMetadata;
    private KeyDerivationData keyDerivationData;
    private Keys keys;
    private final WalletCredential credential;
    private final Context context;
    private final DatabaseHelper helper;
    private Wallet wallet;
    
    public WalletService(final Context context, final WalletCredential credential, DatabaseHelper helper) {
        this.credential = credential;
        this.context = context;
        this.helper = helper;
    }
    
    public Observable<Wallet> open(){
        if(wallet == null) {
            Log.d(TAG, "Open wallet");
            return Observable.fromCallable(() -> {
                Metadata metadata = new MetadataDao(helper).getMetadata(1);
                keysMetadata = new ObjectMapper()
                        .readerFor(KeysMetadata.class)
                        .readValue(metadata.getValue());
                keyDerivationData = new KeyDerivationData(credential.key, keysMetadata);
                keys = new Keys().deserialize(keysMetadata.getKeys(), keyDerivationData.deriveMasterKey());
                wallet = new Wallet(credential.id, keys, helper);
                return wallet;
            });
        } else {
            Log.d(TAG, "Wallet already open");
            return Observable.just(wallet);
        }
    }

    public Observable<Boolean> close(){
        wallet = null;
        return Observable.just(Boolean.TRUE);
    }
    
    public Observable<Integer> export(final IOConfig config) {
        return open().flatMap((Function<Wallet, Observable<Integer>>) wallet -> {
            WalletExport export = new WalletExport(wallet);
            return export.export(config);
        });
    }
    
    public Observable<Integer> restore(final IOConfig config) {
        
        return open().flatMap((Function<Wallet, Observable<Integer>>) wallet -> {
            WalletImport restore = new WalletImport(wallet);
            return restore.restore(config);
        });
    }
    
    public Observable<Boolean> create() {
        return Observable.fromCallable(() -> {

            byte[] salt = new byte[CRYPTO_AEAD_CHACHA20POLY1305_IETF_KEYBYTES];
            Crypto_randombytes.buf(salt);
            keyDerivationData = new KeyDerivationData(credential.key, salt);
            keys = new Keys().init();
            keysMetadata = new KeysMetadata(keys.serialize(keyDerivationData.deriveMasterKey()), salt);

            Metadata metadata = new Metadata(keysMetadata.toString().getBytes());
            DatabaseHelper helper = new DatabaseHelper("backup", context);
            new MetadataDao(helper).create(metadata);
            return Boolean.TRUE;
        });
    }

    public Wallet getWallet() {
        return wallet;
    }
}
