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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used to supply additional parameters when creating a new credential.
 *
 * @see <a href="https://www.w3.org/TR/webauthn/#dictdef-publickeycredentialparameters">§5.3.
 * Parameters for Credential Generation (dictionary PublicKeyCredentialParameters)
 * </a>
 */
public final class PublicKeyCredentialParameters {
    /**
     * Specifies the cryptographic signature algorithm with which the newly generated credential will be used, and thus
     * also the type of asymmetric key pair to be generated, e.g., RSA or Elliptic Curve.
     */
    @NonNull
    private final COSEAlgorithmIdentifier alg;
    /**
     * Specifies the type of credential to be created.
     */
    @NonNull
    private final PublicKeyCredentialType type;

    private PublicKeyCredentialParameters(@NonNull @JsonProperty("alg") COSEAlgorithmIdentifier alg, @NonNull @JsonProperty("type") PublicKeyCredentialType type) {
        this.alg = alg;
        this.type = type;
    }

    /**
     * Algorithm {@link COSEAlgorithmIdentifier#EdDSA} and type {@link PublicKeyCredentialType#PUBLIC_KEY}.
     */
    public static final PublicKeyCredentialParameters EdDSA = builder().alg(COSEAlgorithmIdentifier.EdDSA).build();
    /**
     * Algorithm {@link COSEAlgorithmIdentifier#ES256} and type {@link PublicKeyCredentialType#PUBLIC_KEY}.
     */
    public static final PublicKeyCredentialParameters ES256 = builder().alg(COSEAlgorithmIdentifier.ES256).build();
    /**
     * Algorithm {@link COSEAlgorithmIdentifier#RS256} and type {@link PublicKeyCredentialType#PUBLIC_KEY}.
     */
    public static final PublicKeyCredentialParameters RS256 = builder().alg(COSEAlgorithmIdentifier.RS256).build();

    public static PublicKeyCredentialParametersBuilder.MandatoryStages builder() {
        return new PublicKeyCredentialParametersBuilder.MandatoryStages();
    }

    public static class PublicKeyCredentialParametersBuilder {
        private COSEAlgorithmIdentifier alg;
        private PublicKeyCredentialType type = PublicKeyCredentialType.PUBLIC_KEY;


        static class MandatoryStages {
            private PublicKeyCredentialParametersBuilder builder = new PublicKeyCredentialParametersBuilder();

            PublicKeyCredentialParametersBuilder alg(COSEAlgorithmIdentifier alg) {
                return builder.alg(alg);
            }
        }

        PublicKeyCredentialParametersBuilder() {
        }

        /**
         * Specifies the cryptographic signature algorithm with which the newly generated credential will be used, and thus
         * also the type of asymmetric key pair to be generated, e.g., RSA or Elliptic Curve.
         * @param alg
         * @return 
         */
        public PublicKeyCredentialParametersBuilder alg(@NonNull final COSEAlgorithmIdentifier alg) {
            this.alg = alg;
            return this;
        }

        /**
         * Specifies the type of credential to be created.
         * @param type
         * @return 
         */
        public PublicKeyCredentialParametersBuilder type(@NonNull final PublicKeyCredentialType type) {
            this.type = type;
            return this;
        }

        public PublicKeyCredentialParameters build() {
            return new PublicKeyCredentialParameters(alg, type);
        }

        
    }

    public PublicKeyCredentialParametersBuilder toBuilder() {
        return new PublicKeyCredentialParametersBuilder().alg(this.alg).type(this.type);
    }

    /**
     * Specifies the cryptographic signature algorithm with which the newly generated credential will be used, and thus
     * also the type of asymmetric key pair to be generated, e.g., RSA or Elliptic Curve.
     * @return 
     */
    @NonNull
    public COSEAlgorithmIdentifier getAlg() {
        return this.alg;
    }

    /**
     * Specifies the type of credential to be created.
     * @return 
     */
    @NonNull
    public PublicKeyCredentialType getType() {
        return this.type;
    }
}