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

package org.iton.fido.ui.register;

import android.util.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.iton.fido.core.Authenticator;
import org.iton.fido.model.RegistrationRequest;
import org.iton.fido.model.RelyingPartyIdentity;
import org.iton.fido.util.ByteArray;

/**
 * Created by ITON Solutions on 03/10/2019.
 */
public class RegistrationResponse {

    private RegistrationRequest request;

    public RegistrationResponse(RegistrationRequest request){
        this.request = request;
    }

    public String finish(byte[] publicKey, byte[] privateKey, String origin) throws JsonProcessingException {

        String requestId = request.getRequestId().getBase64Url();
        ByteArray challenge = request.getPublicKeyCredentialCreationOptions().getChallenge();
        RelyingPartyIdentity identity = request.getPublicKeyCredentialCreationOptions().getRp();

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode tokenBinding = mapper.createObjectNode();
        tokenBinding.put("status", "supported");

        ObjectNode clientDataJson = mapper.createObjectNode();
        clientDataJson.put("challenge", challenge.getBase64Url());
        clientDataJson.put("origin", origin);
        clientDataJson.put("type", "webauthn.create");
        clientDataJson.set("tokenBinding", tokenBinding);
        clientDataJson.set("clientExtensions", mapper.createObjectNode());

        String clientDataString = new ObjectMapper().writeValueAsString(clientDataJson);

        Authenticator authenticator = Authenticator.builder()
                .publicKey(publicKey)
                .rpId(identity.getId().getBytes())
                .counter(0)
                .attestationStatement(privateKey, clientDataString.getBytes())
                .build();

        ObjectNode authenticationAttestationResponse = mapper.createObjectNode();
        authenticationAttestationResponse.put("attestationObject", Base64.encodeToString(authenticator.getAttestationObject().getBytes(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));
        authenticationAttestationResponse.put("clientDataJSON", new ByteArray(clientDataString.getBytes()).getBase64());


        ObjectNode publicKeyCredential = mapper.createObjectNode();
        publicKeyCredential.put("id", "iOOGPqcfeovZAXe2RSiCKUXKS5peMlPDfk7ib7Q1JfQ");
        publicKeyCredential.set("response", authenticationAttestationResponse);
        publicKeyCredential.set("clientExtensionResults", mapper.createObjectNode());
        publicKeyCredential.put("type", "public-key");

        ObjectNode response = mapper.createObjectNode();
        response.put("requestId", requestId);
        response.set("credential", publicKeyCredential);

        return mapper.writeValueAsString(response);
    }
}
