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

package org.iton.fido.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.iton.jssi.did.Did;
import org.iton.jssi.store.PreexistingEntityException;
import org.iton.jssi.wallet.Wallet;
import org.iton.jssi.wallet.record.WalletRecord;
import org.libsodium.jni.SodiumException;

import java.io.IOException;
import java.util.List;

/**
 * Created by ITON Solutions on 11/04/2019.
 */
public class StoreService {
    private static final String TAG = StoreService.class.getName();

    public static final String FIDO_STORE = "fido";
    private static final String FIDO_FIRST_LAUNCH = "first.launch";
    private static final String FIDO_USERNAME = "username";
    private static final String FIDO_SEEN_BACKUP_PASSPHRASE = "seen.backup.passphrase";
    private static final String FIDO_DID = "did";
    private static final String FIDO_COUNTER = "counter";

    private SharedPreferences preference;

    public StoreService(Context context) {
        preference = context.getSharedPreferences(FIDO_STORE, Context.MODE_PRIVATE);
    }

    public boolean isFirstLaunch() {
        return preference.getBoolean(FIDO_FIRST_LAUNCH, true);
    }

    public void setCounter(int counter) {
        preference.edit().putInt(FIDO_COUNTER, counter).apply();
    }

    public Integer getCounter() {
        return preference.getInt(FIDO_COUNTER, 0);
    }

    public void setUsername(String username) {
        preference.edit().putString(FIDO_USERNAME, username).apply();
    }

    public String getUsername() {
        return preference.getString(FIDO_USERNAME, "");
    }

    public void setDid(Did did) {
        try {
            String result = new ObjectMapper().writeValueAsString(did);
            preference.edit()
                    .putBoolean(FIDO_FIRST_LAUNCH, Boolean.FALSE)
                    .putInt(FIDO_COUNTER, 0)
                    .putString(FIDO_DID, result)
                    .apply();
        } catch(IOException e){
            Log.e(TAG, String.format("Error: %s", e.getCause().getMessage()));
        }
    }

    public Did getDid() {
        String did = preference.getString(FIDO_DID, null);
        try {
            return did == null ? null : new ObjectMapper().readValue(did, Did.class);
        } catch(IOException e){
            Log.e(TAG, String.format("Error: %s", e.getCause().getMessage()));
            return null;
        }
    }
//
//    public void setHasSeenBackupPassphraseBefore(boolean hasSeenBackupPassphraseBefore) {
//        preference.edit()
//                .putBoolean(PREF_SEEN_BACKUP_PASSPHRASE, hasSeenBackupPassphraseBefore)
//                .apply();
//    }
//
//    public boolean wasReturnUser() {
//        return preference.getBoolean(PREF_RETURN_USER, true);
//    }
//
//    public void setWasReturnUser(boolean returnUser) {
//        preference.edit()
//                .putBoolean(PREF_RETURN_USER, returnUser)
//                .apply();
//    }

//    public boolean shouldShowWelcomeBackUserFlow() {
//        // if this is not a first time user and we have not stored the special preference key
//        return isFirstLaunch() == false && preference.contains(PREF_SEEN_BACKUP_PASSPHRASE) == false;
//    }

    public static void addRecord(Wallet wallet, String type, String name, String value) {

        WalletRecord record = new WalletRecord(type, name, value);
        try{
            wallet.addRecord(record);
        } catch(SodiumException | PreexistingEntityException e){
            Log.e(TAG, String.format("Error: %s", e.getCause().getMessage()));
        }
    }

    public static WalletRecord getRecord(Wallet wallet, String type) {
        try{
            List<WalletRecord> records = wallet.findRecords(type);
            return records.get(0);
        } catch(SodiumException e){
            Log.e(TAG, String.format("Error: %s", e.getCause().getMessage()));
            return null;
        }
    }
}
