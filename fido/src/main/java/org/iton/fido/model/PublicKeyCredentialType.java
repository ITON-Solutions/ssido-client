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
 * Defines the valid credential types.
 * <p>
 * It is an extensions point; values may be added to it in the future, as more credential types are defined. The values
 * of this enumeration are used for versioning the Authentication Assertion and attestation structures according to the
 * type of the authenticator.
 * </p>
 * <p>
 * Currently one credential type is defined, namely {@link #PUBLIC_KEY}.
 * </p>
 *
 * @see <a href="https://www.w3.org/TR/webauthn/#enumdef-publickeycredentialtype">§5.10.2. Credential Type Enumeration
 * (enum PublicKeyCredentialType)</a>
 */
@JsonSerialize(using = JsonStringSerializer.class)
public enum PublicKeyCredentialType implements JsonStringSerializable {
    PUBLIC_KEY("public-key");
    @NonNull
    private final String id;

    private static Optional<PublicKeyCredentialType> fromString(@NonNull String id) {
        Optional<PublicKeyCredentialType> result = Optional.absent();
        for(PublicKeyCredentialType item : values()){
            if(item.id.equals(id)){
                result = Optional.of(item);
            }
        }
        return result;
    }

    @JsonCreator
    private static PublicKeyCredentialType fromJsonString(@NonNull String id) {
        Optional<PublicKeyCredentialType> result = fromString(id);
        if(result.isPresent()){
            return result.get();
        }
        throw new IllegalArgumentException(String.format("Unknown %s value: %s", PublicKeyCredentialType.class.getSimpleName(), id));
    }

    @Override
    public String toJsonString() {
        return id;
    }

    PublicKeyCredentialType(@NonNull final String id) {
        this.id = id;
    }
}
