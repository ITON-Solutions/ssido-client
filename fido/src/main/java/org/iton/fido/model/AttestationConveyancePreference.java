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
 * Relying Parties may use this to specify their preference regarding attestation conveyance during credential
 * generation.
 *
 * @see <a href="https://www.w3.org/TR/webauthn/#attestation-conveyance">§5.4.6. Attestation Conveyance
 * Preference
 * Enumeration (enum AttestationConveyancePreference)
 * </a>
 */
@JsonSerialize(using = JsonStringSerializer.class)
public enum AttestationConveyancePreference implements JsonStringSerializable {
    /**
     * Indicates that the Relying Party is not interested in authenticator attestation.
     * <p>
     * For example, in order to potentially avoid having to obtain user consent to relay identifying information to the
     * Relying Party, or to save a roundtrip to an <a href="https://www.w3.org/TR/webauthn/#attestation-ca">Attestation
     * CA</a>.
     * </p>
     * <p>
     * This is the default value.
     * </p>
     */
    NONE("none"), /**
     * Indicates that the Relying Party prefers an attestation conveyance yielding verifiable attestation statements,
     * but allows the client to decide how to obtain such attestation statements. The client MAY replace the
     * authenticator-generated attestation statements with attestation statements generated by an <a
     * href="https://www.w3.org/TR/webauthn/#anonymization-ca">Anonymization CA</a>, in order to
     * protect the user’s privacy, or to assist Relying Parties with attestation verification in a heterogeneous
     * ecosystem.
     * <p>
     * Note: There is no guarantee that the Relying Party will obtain a verifiable attestation statement in this case.
     * For example, in the case that the authenticator employs self attestation.
     * </p>
     */
    INDIRECT("indirect"), /**
     * Indicates that the Relying Party wants to receive the attestation statement as generated by the authenticator.
     */
    DIRECT("direct");
    @NonNull
    private final String id;

    private static Optional<AttestationConveyancePreference> fromString(@NonNull String id) {
        Optional<AttestationConveyancePreference> result = Optional.absent();
        for(AttestationConveyancePreference item : values()){
            if(item.id.equals(id)){
                result = Optional.of(item);
            }
        }
        return result;
    }

    @JsonCreator
    private static AttestationConveyancePreference fromJsonString(@NonNull String id) {
        Optional<AttestationConveyancePreference> result = fromString(id);
        if(result.isPresent()){
            return result.get();
        }
        throw new IllegalArgumentException(String.format("Unknown %s value: %s", AttestationConveyancePreference.class.getSimpleName(), id));
    }

    @Override
    public String toJsonString() {
        return id;
    }

    AttestationConveyancePreference(@NonNull final String id) {
        this.id = id;
    }
}
