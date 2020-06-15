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

import androidx.annotation.NonNull;

import java.security.PublicKey;

import org.iton.fido.crypto.EdDSAPublicKey;
import org.iton.fido.crypto.spec.EdDSANamedCurveTable;
import org.iton.fido.crypto.spec.EdDSAParameterSpec;
import org.iton.fido.crypto.spec.EdDSAPublicKeySpec;
import org.iton.fido.util.BinaryUtil;
import org.libsodium.api.Crypto_hash_sha256;
import org.libsodium.jni.SodiumException;

/**
 * Attested credential data is a variable-length byte array added to the authenticator data when generating an
 * attestation object for a given credential. This class provides access to the three data segments of that byte array.
 *
 * @see <a href="https://www.w3.org/TR/webauthn/#sec-attested-credential-data">6.4.1. Attested
 * Credential Data</a>
 */
public final class AttestedCredentialData {
    /**
     * The AAGUID of the authenticator.
     */
    @NonNull
    private final byte[] aaguid;
    /**
     * The credential ID of the attested credential.
     */
    @NonNull
    private final byte[] credentialId;
    // TODO: verify requirements https://www.w3.org/TR/webauthn/#sec-attestation-data
    /**
     * The credential public key encoded in COSE_Key format, as defined in Section 7 of <a
     * href="https://tools.ietf.org/html/rfc8152">RFC 8152</a>.
     */
    @NonNull
    private final byte[] publicKey;

    private AttestedCredentialData(@NonNull byte[] aaguid, @NonNull byte[] credentialId, @NonNull byte[] publicKey) {
        this.aaguid = aaguid;
        this.credentialId = credentialId;
        this.publicKey = publicKey;
    }

    public static AttestedCredentialDataBuilder builder() {
        return new AttestedCredentialDataBuilder();
    }

    public static class AttestedCredentialDataBuilder {
        private byte[] aaguid;
        private byte[] credentialId;
        private byte[] publicKey;

        AttestedCredentialDataBuilder() {
        }

        /**
         * The AAGUID of the authenticator.
         * @param aaguid
         * @return 
         */
        public AttestedCredentialDataBuilder aaguid(@NonNull final byte[] aaguid) {
            this.aaguid = aaguid;
            return this;
        }

        /**
         * The credential ID of the attested credential.
         * @param credentialId
         * @return 
         */
        public AttestedCredentialDataBuilder credentialId(@NonNull final byte[] credentialId) {
            this.credentialId = credentialId;
            return this;
        }

        /**
         * The credential public key encoded in COSE_Key format, as defined in Section 7 of <a
         * href="https://tools.ietf.org/html/rfc8152">RFC 8152</a>.
         * @param publicKey
         * @return 
         */
        public AttestedCredentialDataBuilder publicKey(@NonNull final byte[] publicKey) {
            
            EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName("Ed25519");
            PublicKey pk = new EdDSAPublicKey(new EdDSAPublicKeySpec(publicKey, spec));
            this.publicKey = AuthenticatorCodec.publicKeyToCose(pk);
            try {
                this.credentialId = Crypto_hash_sha256.sha256(publicKey);
            } catch(SodiumException e){
            }
            return this;
        }

        public AttestedCredentialData build() {
            return new AttestedCredentialData(aaguid, credentialId, publicKey);
        }
    }

    /**
     * The AAGUID of the authenticator.
     * @return 
     */
    @NonNull
    public byte[] getAaguid() {
        return this.aaguid;
    }

    /**
     * The credential ID of the attested credential.
     * @return 
     */
    @NonNull
    public byte[] getCredentialId() {
        return this.credentialId;
    }

    /**
     * The credential public key encoded in COSE_Key format, as defined in Section 7 of <a
     * href="https://tools.ietf.org/html/rfc8152">RFC 8152</a>.
     * @return 
     */
    @NonNull
    public byte[] publicKey() {
        return this.publicKey;
    }
    
    public byte[] getBytes(){
        
        byte[] result = BinaryUtil.concat(
                aaguid,
                BinaryUtil.encodeUint16((short)credentialId.length),
                credentialId, 
                publicKey);
        
        return result;
    }
}
