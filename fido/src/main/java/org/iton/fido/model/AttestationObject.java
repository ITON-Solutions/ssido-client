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

import android.util.Base64;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.iton.fido.util.JacksonCodecs;

import java.io.IOException;

/**
 * Authenticators MUST provide some form of attestation. The basic requirement is that the authenticator can produce,
 * for each credential public key, an attestation statement verifiable by the WebAuthn Relying Party. Typically, this
 * attestation statement contains a signature by an attestation private key over the attested credential public key and
 * a challenge, as well as a certificate or similar data providing provenance information for the attestation public
 * key, enabling the Relying Party to make a trust decision. However, if an attestation key pair is not available, then
 * the authenticator MUST perform <a href="https://www.w3.org/TR/webauthn/#self-attestation">self
 * attestation</a> of the credential public key with the corresponding credential private key. All this information is
 * returned by authenticators any time a new public key credential is generated, in the overall form of an attestation
 * object. The relationship of the attestation object with authenticator data (containing attested credential data) and
 * the attestation statement is illustrated in <a href="https://www.w3.org/TR/webauthn//#fig-attStructs">figure 5</a>.
 *
 * @see <a href="https://www.w3.org/TR/webauthn/#sctn-attestation">§6.4. Attestation</a>
 */
@JsonSerialize(using = AttestationObject.JsonSerializer.class)
public final class AttestationObject {

    private static final String FORMAT = "packed";
    /**
     * The authenticator data embedded inside this attestation object. This is one part of the signed data that the
     * signature in the attestation statement (if any) is computed over.
     */
    @NonNull
    private final transient AuthenticatorData authenticatorData;
    
    /**
     * An important component of the attestation object is the attestation statement. This is a specific type of signed
     * data object, containing statements about a public key credential itself and the authenticator that created it. It
     * contains an attestation signature created using the key of the attesting authority (except for the case of self
     * attestation, when it is created using the credential private key).
     *
     * <p>
     * Users of this library should not need to access this value directly.
     * </p>
     */
    @NonNull
    private final transient ObjectNode attestationStatement;

    
    private AttestationObject(@NonNull AuthenticatorData authenticatorData, @NonNull ObjectNode attestationStatement) {
        this.authenticatorData = authenticatorData;
        this.attestationStatement = attestationStatement;
    }
    
    public static AttestationObjectBuilder builder() {
        return new AttestationObjectBuilder();
    }

    public static class AttestationObjectBuilder {
        private AuthenticatorData authenticatorData;
        private ObjectNode attestationStatement;

        AttestationObjectBuilder() {
        }
        
        /**
         * The AuthenticatorData of the authenticator.
         * @param authenticatorData
         * @return 
         */
        public AttestationObjectBuilder authenticatorData(@NonNull final AuthenticatorData authenticatorData) {
            this.authenticatorData = authenticatorData;
            return this;
        }
        
        /**
         * The ObjectNode of the authenticator.
         * @param attestationStatement
         * @return 
         */
        public AttestationObjectBuilder attestationStatement(@NonNull final ObjectNode attestationStatement) {
            this.attestationStatement = attestationStatement;
            return this;
        }
        
        public AttestationObject build() {
            return new AttestationObject(authenticatorData, attestationStatement);
        }
    }
    /**
     * The original raw byte array that this object is decoded from.
     *
     * @return
     * @see <a href="https://www.w3.org/TR/webauthn/#sctn-attestation">§6.4. Attestation</a>
     */
    @NonNull
    public byte[] getBytes() {
        
        byte[] bytes = new byte[0];
        
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode node = factory.objectNode();
        node.set("authData",  factory.binaryNode(authenticatorData.getBytes()));
        node.set("fmt",  factory.textNode(FORMAT));
        node.set("attStmt",  attestationStatement);
        
        try{
            bytes = JacksonCodecs.cbor().writeValueAsBytes(node);
        } catch(JsonProcessingException e){}
        return bytes;
    }
    
    static class JsonSerializer extends com.fasterxml.jackson.databind.JsonSerializer<AttestationObject> {
        @Override
        public void serialize(AttestationObject value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(Base64.encodeToString(value.getBytes(), Base64.URL_SAFE | Base64.NO_PADDING));
        }
    }

    /**
     * The authenticator data embedded inside this attestation object. This is one part of the signed data that the
     * signature in the attestation statement (if any) is computed over.
     * @return 
     */
    @NonNull
    public AuthenticatorData getAuthenticatorData() {
        return authenticatorData;
    }

    /**
     * The attestation statement format identifier of this attestation object.
     *
     * @return
     * @see <a href="https://www.w3.org/TR/webauthn/#defined-attestation-formats">§8. Defined
     * Attestation Statement Formats</a>
     *
     * <p>
     * Users of this library should not need to access this value directly.
     * </p>
     */
    @NonNull
    public String getFormat() {
        return FORMAT;
    }

    /**
     * An important component of the attestation object is the attestation statement. This is a specific type of signed
     * data object, containing statements about a public key credential itself and the authenticator that created it. It
     * contains an attestation signature created using the key of the attesting authority (except for the case of self
     * attestation, when it is created using the credential private key).
     *
     * <p>
     * Users of this library should not need to access this value directly.
     * </p>
     * @return 
     */
    @NonNull
    public ObjectNode getAttestationStatement() {
        return attestationStatement;
    }

}
