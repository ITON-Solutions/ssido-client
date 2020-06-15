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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Optional;

import org.iton.fido.util.JsonLongSerializable;
import org.iton.fido.util.JsonLongSerializer;

/**
 * A number identifying a cryptographic algorithm. The algorithm identifiers SHOULD be values registered in the IANA
 * COSE Algorithms registry, for instance, -7 for "ES256" and -257 for "RS256".
 *
 * @see <a href="https://www.w3.org/TR/webauthn/#typedefdef-cosealgorithmidentifier">§5.10.5.
 * Cryptographic Algorithm Identifier (typedef COSEAlgorithmIdentifier)</a>
 */
@JsonSerialize(using = JsonLongSerializer.class)
public enum COSEAlgorithmIdentifier implements JsonLongSerializable {
    EdDSA(-8), ES256(-7), RS256(-257);
    private final long id;

    COSEAlgorithmIdentifier(long id) {
        this.id = id;
    }

    private static Optional<COSEAlgorithmIdentifier> fromString(long id) {
        Optional<COSEAlgorithmIdentifier> result = Optional.absent();
        for(COSEAlgorithmIdentifier item : values()){
            if(item.id == id){
                result = Optional.of(item);
            }
        }
        return result;
    }

    @JsonCreator
    private static COSEAlgorithmIdentifier fromJsonString(long id) {
        Optional<COSEAlgorithmIdentifier> result = fromString(id);
        if(result.isPresent()){
            return result.get();
        }
        throw new IllegalArgumentException("Unknown COSE algorithm identifier: " + id);
    }

    @Override
    public long toJsonNumber() {
        return id;
    }

    public long getId() {
        return this.id;
    }
}
