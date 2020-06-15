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

package org.iton.fido.model;

import com.upokecenter.cbor.CBORObject;

import org.iton.fido.crypto.EdDSAPublicKey;

import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;


public final class AuthenticatorCodec {

    public static byte[] publicKeyToCose(PublicKey key) {
        Map<Long, Object> coseKey = new HashMap<>();

        coseKey.put(1L, 1L);  // Key type: octet key pair
        coseKey.put(3L, -8);  // EdDSA(-8)
        coseKey.put(-1L, 6L); // crv: Ed25519
        coseKey.put(-2L, key.getEncoded());

        return CBORObject.FromObject(coseKey).EncodeToBytes();
    }
    
    public static PublicKey coseToPublicKey(CBORObject cose) {
        byte[] encoded = cose.get(CBORObject.FromObject(-2)).GetByteString();

        try {
            X509EncodedKeySpec decoded = new X509EncodedKeySpec(encoded);
            return new EdDSAPublicKey(decoded);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
