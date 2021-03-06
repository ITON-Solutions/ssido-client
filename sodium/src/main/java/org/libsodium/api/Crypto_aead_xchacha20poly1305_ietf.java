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

package org.libsodium.api;

import java.util.HashMap;
import java.util.Map;
import static org.libsodium.api.Crypto.exception;
import org.libsodium.jni.Sodium;
import static org.libsodium.jni.SodiumConstants.CRYPTO_AEAD_XCHACHA20POLY1305_IETF_ABYTES;
import static org.libsodium.jni.SodiumConstants.CRYPTO_AEAD_XCHACHA20POLY1305_IETF_KEYBYTES;
import static org.libsodium.jni.SodiumConstants.CRYPTO_AEAD_XCHACHA20POLY1305_IETF_TAGBYTES;
import org.libsodium.jni.SodiumException;

/**
 *
 * @author ITON Solutions
 */
public class Crypto_aead_xchacha20poly1305_ietf {
    
    public static byte[] encrypt(byte[] data, byte[] add, byte[] nonce, byte[] key) throws SodiumException {
        byte[] cipher = new byte[data.length + CRYPTO_AEAD_XCHACHA20POLY1305_IETF_ABYTES];

        if (add == null) {
            add = new byte[0];
        }

        exception(Sodium.crypto_aead_xchacha20poly1305_ietf_encrypt(cipher, new int[0], data, data.length, add, add.length, new byte[0], nonce, key), "crypto_aead_xchacha20poly1305_ietf_encrypt");
        return cipher;
    }

    public static byte[] decrypt(byte[] cipher, byte[] add, byte[] nonce, byte[] key) throws SodiumException {

        byte[] data = new byte[cipher.length - CRYPTO_AEAD_XCHACHA20POLY1305_IETF_ABYTES];
        if (add == null) {
            add = new byte[0];
        }

        exception(Sodium.crypto_aead_xchacha20poly1305_ietf_decrypt(data, new int[0], new byte[0], cipher, cipher.length, add, add.length, nonce, key), "crypto_aead_xchacha20poly1305_ietf_decrypt");
        return data;
    }

    public static Map<String, byte[]> encrypt_detached(byte[] data, byte[] add, byte[] nonce, byte[] key) throws SodiumException {
        byte[] cipher = new byte[data.length];
        byte[] tag = new byte[CRYPTO_AEAD_XCHACHA20POLY1305_IETF_TAGBYTES];
        if (add == null) {
            add = new byte[0];
        }

        exception(Sodium.crypto_aead_xchacha20poly1305_ietf_encrypt_detached(cipher, tag, new int[0], data, data.length, add, add.length, new byte[0], nonce, key), "crypto_aead_xchacha20poly1305_ietf_encrypt_detached");
        Map<String, byte[]> result = new HashMap<>();
        result.put("cipher", cipher);
        result.put("tag", tag);
        return result;
    }

    public static byte[] decrypt_detached(byte[] cipher, byte[] tag, byte[] add, byte[] nonce, byte[] key) throws SodiumException {
         byte[] data = new byte[cipher.length];
        if (add == null) {
            add = new byte[0];
        }

        exception(Sodium.crypto_aead_xchacha20poly1305_ietf_decrypt_detached(data, new byte[0], cipher, cipher.length, tag, add, add.length, nonce, key), "crypto_aead_xchacha20poly1305_ietf_decrypt_detached");
        return data;
    }

    public static byte[] keygen() throws SodiumException {
        byte[] key = new byte[CRYPTO_AEAD_XCHACHA20POLY1305_IETF_KEYBYTES];
        Sodium.crypto_aead_xchacha20poly1305_ietf_keygen(key);
        return key;
    }
}
