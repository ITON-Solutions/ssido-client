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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;

import org.iton.fido.util.ByteArray;

/**
 * A combination of a {@link PublicKeyCredentialRequestOptions} and, optionally, a {@link #getUsername() username}.
 */
public final class AssertionRequest {
    /**
     * An object that can be serialized to JSON and passed as the <code>publicKey</code> argument to
     * <code>navigator.credentials.get()</code>.
     */
    @NonNull
    private final PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions;
    /**
     * The username of the user to authenticate, if the user has already been identified.
     * <p>
     * If this is absent, this indicates that this is a request for an assertion by a <a
     * href="https://www.w3.org/TR/webauthn/#client-side-resident-public-key-credential-source">client-side-resident
     * credential</a>, and identification of the user has been deferred until the response is received.
     * </p>
     */
    @NonNull
    private final Optional<String> username;
    private final ByteArray requestId;

    @JsonCreator
    private AssertionRequest(
            @JsonProperty("requestId") @NonNull ByteArray requestId,
            @JsonProperty("publicKeyCredentialRequestOptions") @NonNull PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions,
            @JsonProperty("username") String username) {
        this.requestId = requestId;
        this.username = Optional.of(username);
        this.publicKeyCredentialRequestOptions = publicKeyCredentialRequestOptions;

    }

    /**
     * An object that can be serialized to JSON and passed as the <code>publicKey</code> argument to

     * <code>navigator.credentials.get()</code>.
     * @return 
     */
    @NonNull
    public PublicKeyCredentialRequestOptions getPublicKeyCredentialRequestOptions() {
        return publicKeyCredentialRequestOptions;
    }

    public ByteArray getRequestId() {
        return requestId;
    }

    /**
     * The username of the user to authenticate, if the user has already been identified.
     * <p>
     * If this is absent, this indicates that this is a request for an assertion by a <a
     * href="https://www.w3.org/TR/webauthn/#client-side-resident-public-key-credential-source">client-side-resident
     * credential</a>, and identification of the user has been deferred until the response is received.
     * </p>
     * @return
     */
    @NonNull
    public Optional<String> getUsername() {
        return this.username;
    }
}
