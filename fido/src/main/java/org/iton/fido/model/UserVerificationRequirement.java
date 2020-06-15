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
 * A WebAuthn Relying Party may require <a href="https://www.w3.org/TR/webauthn/#user-verification">user
 * verification</a> for some of its operations but not for others, and may use this type to express its needs.
 *
 * @see <a href="https://www.w3.org/TR/webauthn/#enumdef-userverificationrequirement">§5.10.6. User
 * Verification Requirement Enumeration (enum UserVerificationRequirement)</a>
 */
@JsonSerialize(using = JsonStringSerializer.class)
public enum UserVerificationRequirement implements JsonStringSerializable {
    /**
     * This value indicates that the Relying Party does not want user verification employed during the operation (e.g.,
     * in the interest of minimizing disruption to the user interaction flow).
     */
    DISCOURAGED("discouraged"), /**
     * This value indicates that the Relying Party prefers user verification for the operation if possible, but will not
     * fail the operation if the response does not have the {@link AuthenticatorDataFlags#UV} flag set.
     */
    PREFERRED("preferred"), /**
     * Indicates that the Relying Party requires user verification for the operation and will fail the operation if the
     * response does not have the {@link AuthenticatorDataFlags#UV} flag set.
     */
    REQUIRED("required");
    @NonNull
    private final String id;

    private static Optional<UserVerificationRequirement> fromString(@NonNull String id) {
        Optional result = Optional.absent();
        for(UserVerificationRequirement item : values()){
            if(item.id.equals(id)){
                result = Optional.of(item);
            }
        }
        return result;
    }

    @JsonCreator
    private static UserVerificationRequirement fromJsonString(@NonNull String id) {
        Optional<UserVerificationRequirement> result = fromString(id);
        if(result.isPresent()){
            return result.get();
        }
        throw new IllegalArgumentException(String.format("Unknown %s value: %s", UserVerificationRequirement.class.getSimpleName(), id));
    }

    @Override
    public String toJsonString() {
        return id;
    }

    UserVerificationRequirement(@NonNull final String id) {
        this.id = id;
    }
}
