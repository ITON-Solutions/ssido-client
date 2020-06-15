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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Optional;

import org.iton.fido.util.JsonStringSerializable;
import org.iton.fido.util.JsonStringSerializer;

/**
 * This enumeration’s values describe authenticators' <a href="https://www.w3.org/TR/webauthn/#authenticator-attachment-modality">attachment
 * modalities</a>. Relying Parties use this for two purposes:
 *
 * <ul>
 * <li>
 * to express a preferred authenticator attachment modality when calling <code>navigator.credentials.create()</code> to
 * create a credential, and
 * </li>
 *
 * <li>
 * to inform the client of the Relying Party's best belief about how to locate the managing authenticators of the
 * credentials listed in {@link PublicKeyCredentialRequestOptions#allowCredentials} when calling
 * <code>navigator.credentials.get()</code>.
 * </li>
 * </ul>
 *
 * @see <a href="https://www.w3.org/TR/webauthn/#enumdef-authenticatorattachment">§5.4.5. Authenticator
 * Attachment Enumeration (enum AuthenticatorAttachment)
 * </a>
 */
@JsonSerialize(using = JsonStringSerializer.class)
public enum AuthenticatorAttachment implements JsonStringSerializable {
    /**
     * Indicates <a href="https://www.w3.org/TR/webauthn/#cross-platform-attachment">cross-platform
     * attachment</a>.
     * <p>
     * Authenticators of this class are removable from, and can "roam" among, client platforms.
     * </p>
     */
    CROSS_PLATFORM("cross-platform"), /**
     * Indicates <a href="https://www.w3.org/TR/webauthn/#platform-attachment">platform
     * attachment</a>.
     * <p>
     * Usually, authenticators of this class are not removable from the platform.
     * </p>
     */
    PLATFORM("platform");
    @NonNull
    private final String id;


    private static Optional<AuthenticatorAttachment> fromString(@NonNull String id) {
        Optional<AuthenticatorAttachment> result = Optional.absent();
        for(AuthenticatorAttachment item : values()){
            if(item.id.equals(id)){
                result = Optional.of(item);
            }
        }
        return result;
    }

    @JsonCreator
    private static AuthenticatorAttachment fromJsonString(@NonNull String id) {
        Optional<AuthenticatorAttachment> result = fromString(id);
        if(result.isPresent()){
            return result.get();
        }
        throw new IllegalArgumentException(String.format("Unknown %s value: %s", AuthenticatorAttachment.class.getSimpleName(), id));
    }

    @Override
    public String toJsonString() {
        return id;
    }

    AuthenticatorAttachment(@NonNull final String id) {
        this.id = id;
    }
}
