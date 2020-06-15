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
package org.iton.fido.core;

import androidx.annotation.NonNull;

import org.iton.fido.model.AttestationObject;
import org.iton.fido.model.AttestationStatement;
import org.iton.fido.model.AttestedCredentialData;
import org.iton.fido.model.AuthenticatorData;
import org.iton.fido.model.PackedAttestationStatement;

/**
 *
 * @author ITON Solutions
 */
public class Authenticator {
    private static final String TAG = Authenticator.class.getName();
    
    public static final byte[] AAGUID = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}; // 16 bytes long
    
    private final AttestedCredentialData attestedCredentialData;
    private final AuthenticatorData authenticatorData;
    private final AttestationStatement attestationStatement;
    
   
    private Authenticator(AuthenticatorData authenticatorData, AttestationStatement attestationStatement, AttestedCredentialData attestedCredentialData){
        this.authenticatorData = authenticatorData;
        this.attestationStatement = attestationStatement;
        this.attestedCredentialData = attestedCredentialData;
    }
    
    public static AuthenticatorBuilder.MandatoryStages builder() {
        return new AuthenticatorBuilder.MandatoryStages();
    }

    public static class AuthenticatorBuilder {

        private AttestedCredentialData attestedCredentialData;
        private AuthenticatorData authenticatorData;
        private AttestationStatement attestationStatement;
        
        AuthenticatorBuilder(){}

        public static class MandatoryStages {
            private final AuthenticatorBuilder builder = new AuthenticatorBuilder();

            public AuthenticatorStage publicKey(@NonNull final byte[] publicKey) {
                builder.publicKey(publicKey);
                return new AuthenticatorStage();
            }

            public class AuthenticatorStage{
                public CounterStage rpId(@NonNull final byte[] rpId) {
                    builder.rpId(rpId);
                    return new CounterStage();
                }
            }

            public class CounterStage{
                public AttestationStatementStage counter(@NonNull final Integer counter) {
                    builder.counter(counter);
                    return new AttestationStatementStage ();
                }
            }


            public class AttestationStatementStage{
                public AuthenticatorBuilder attestationStatement(@NonNull byte[] privateKey, byte[] clientData){
                    return builder.attestationStatement(privateKey, clientData);
                }
            }
        }
        
        AuthenticatorBuilder publicKey(@NonNull byte[] publicKey){
        
            this.attestedCredentialData = AttestedCredentialData.builder()
                    .aaguid(AAGUID)
                    .publicKey(publicKey)
                    .build();

            return this;
        }
        
        AuthenticatorBuilder rpId(@NonNull byte[] rpId) {
            this.authenticatorData = AuthenticatorData.builder()
                    .rpId(rpId)
                    .attestedCredentialData(attestedCredentialData)
                    .build();
            return this;
        }

        AuthenticatorBuilder counter(@NonNull Integer counter) {
            this.authenticatorData.setCounter(counter);
            return this;
        }


        AuthenticatorBuilder attestationStatement(@NonNull byte[] privateKey, byte[] clientData) {
            this.attestationStatement = PackedAttestationStatement.builder()
                    .privateKey(privateKey)
                    .authenticatorData(authenticatorData.getBytes())
                    .clientData(clientData)
                    .build();
            return this;
        }
        
        public Authenticator build(){
            return new Authenticator(authenticatorData, attestationStatement, attestedCredentialData);
        }
    }

    public AttestedCredentialData getAttestedCredentialData() {
        return attestedCredentialData;
    }

    public AuthenticatorData getAuthenticatorData() {
        return authenticatorData;
    }

    public AttestationStatement getAttestationStatement() {
        return attestationStatement;
    }
    
    public AttestationObject getAttestationObject(){
        return AttestationObject.builder()
                .attestationStatement(attestationStatement.getObjectNode())
                .authenticatorData(authenticatorData)
                .build();
    }
}
