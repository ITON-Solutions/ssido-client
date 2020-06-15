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

import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;

import org.iton.fido.util.BinaryUtil;
import org.libsodium.api.Crypto_hash_sha256;
import org.libsodium.api.Crypto_sign_ed25519;
import org.libsodium.jni.SodiumException;
/**
 *
 * @author ITON Solutions
 */
public class PackedAttestationStatement{
    
    private static final String TAG = PackedAttestationStatement.class.getName();
  
    private PackedAttestationStatement() {
    }

    public static AttestationStatementBuilder builder() {
        return new AttestationStatementBuilder();
    }
    
    public static class AttestationStatementBuilder{
        byte[] authenticatorData;
        byte[] clientData;
        byte[] privateKey;
        
        AttestationStatementBuilder(){}
        
        public AttestationStatementBuilder privateKey(@NonNull byte[] privateKey){
            this.privateKey = privateKey;
            return this;
        }

        public AttestationStatementBuilder authenticatorData(@NonNull byte[] authenticatorData){
            this.authenticatorData = authenticatorData;
            return this;
        }

        public AttestationStatementBuilder clientData(@NonNull byte[] clientData){
            this.clientData = clientData;
            return this;
        }
        
        public AttestationStatement build() {
            
            JsonNodeFactory factory = JsonNodeFactory.instance;
            ObjectNode result = factory.objectNode();
            
            try {
                byte[] data = BinaryUtil.concat(authenticatorData, Crypto_hash_sha256.sha256(clientData));
                byte[] sig = Crypto_sign_ed25519.sign(data, privateKey);
                result.set("sig", factory.binaryNode(Arrays.copyOfRange(sig, 0, sig.length - data.length))); // detach data
                result.set("alg", factory.numberNode(-8)); // EdDSA(-8)

            } catch (SodiumException e) {
                Log.e("Sodium exception {}", e.getMessage());
            }

            return () -> result;
        }
    }
}
