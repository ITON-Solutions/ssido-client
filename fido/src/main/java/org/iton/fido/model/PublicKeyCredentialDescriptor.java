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

import org.iton.fido.util.ByteArray;
import org.iton.fido.util.CollectionUtil;
import org.iton.fido.util.EnumUtil;
import com.google.common.base.Optional;
import java.util.Set;
import java.util.TreeSet;


/**
 * The attributes that are specified by a caller when referring to a public key credential as an input parameter to the
 * <code>navigator.credentials.create()</code> or <code>navigator.credentials.get()</code> methods. It mirrors the
 * fields of the {@link PublicKeyCredential} object returned by the latter methods.
 *
 * @see <a href="https://www.w3.org/TR/webauthn/#dictdef-publickeycredentialdescriptor">§5.10.3.
 * Credential Descriptor (dictionary PublicKeyCredentialDescriptor)</a>
 */
public final class PublicKeyCredentialDescriptor implements Comparable<PublicKeyCredentialDescriptor> {
    /**
     * The type of the credential the caller is referring to.
     */
    @NonNull
    private final PublicKeyCredentialType type;
    /**
     * The credential ID of the public key credential the caller is referring to.
     */
    @NonNull
    private final ByteArray id;
    /**
     * An OPTIONAL hint as to how the client might communicate with the managing authenticator of the public key

     * credential the caller is referring to.
     */
    @NonNull
    private final Optional<Set<AuthenticatorTransport>> transports;

    private PublicKeyCredentialDescriptor(
            @NonNull PublicKeyCredentialType type,
            @NonNull ByteArray id,
            @NonNull Optional<Set<AuthenticatorTransport>> transports) {
        this.type = type;
        this.id = id;
        this.transports = transports.isPresent() ? Optional.of(CollectionUtil.immutableSortedSet(new TreeSet<>(transports.get()))) : Optional.absent();
    }

    @JsonCreator
    private PublicKeyCredentialDescriptor(
            @NonNull @JsonProperty("type") PublicKeyCredentialType type,
            @NonNull @JsonProperty("id") ByteArray id,
            @JsonProperty("transports") Set<AuthenticatorTransport> transports) {
        this(type, id, Optional.fromNullable(transports));
    }

    @Override
    public int compareTo(PublicKeyCredentialDescriptor other) {
        int idComparison = id.compareTo(other.id);
        if (idComparison != 0) {
            return idComparison;
        }
        if (type.compareTo(other.type) != 0) {
            return type.compareTo(other.type);
        }
        if (!transports.isPresent() && other.transports.isPresent()) {
            return -1;
        } else if (transports.isPresent() && !other.transports.isPresent()) {
            return 1;
        } else if (transports.isPresent() && other.transports.isPresent()) {
            int transportsComparison = EnumUtil.compareSets(transports.get(), other.transports.get(), AuthenticatorTransport.class);
            if (transportsComparison != 0) {
                return transportsComparison;
            }
        }
        return 0;
    }

    public static PublicKeyCredentialDescriptorBuilder.MandatoryStages builder() {
        return new PublicKeyCredentialDescriptorBuilder.MandatoryStages();
    }


    public static class PublicKeyCredentialDescriptorBuilder {
        private PublicKeyCredentialType type = PublicKeyCredentialType.PUBLIC_KEY;
        private ByteArray id;
        private Optional<Set<AuthenticatorTransport>> transports = Optional.absent();


        public static class MandatoryStages {
            private PublicKeyCredentialDescriptorBuilder builder = new PublicKeyCredentialDescriptorBuilder();

            public PublicKeyCredentialDescriptorBuilder id(ByteArray id) {
                return builder.id(id);
            }
        }

        /**
         * An OPTIONAL hint as to how the client might communicate with the managing authenticator of the public key
         * credential the caller is referring to.
         * @param transports
         * @return 
         */
        public PublicKeyCredentialDescriptorBuilder transports(@NonNull Optional<Set<AuthenticatorTransport>> transports) {
            this.transports = transports;
            return this;
        }

        /**Nonnull
         * An OPTIONAL hint as to how the client might communicate with the managing authenticator of the public key
         * credential the caller is referring to.
         * @param transports
         * @return 
         */
        public PublicKeyCredentialDescriptorBuilder transports(@NonNull Set<AuthenticatorTransport> transports) {
            return this.transports(Optional.of(transports));
        }

        PublicKeyCredentialDescriptorBuilder() {
        }

        /**
         * The type of the credential the caller is referring to.
         * @param type
         * @return 
         */
        public PublicKeyCredentialDescriptorBuilder type(@NonNull final PublicKeyCredentialType type) {
            this.type = type;
            return this;
        }

        /**
         * The credential ID of the public key credential the caller is referring to.
         * @param id
         * @return 
         */
        public PublicKeyCredentialDescriptorBuilder id(@NonNull final ByteArray id) {
            this.id = id;
            return this;
        }

        public PublicKeyCredentialDescriptor build() {
            return new PublicKeyCredentialDescriptor(type, id, transports);
        }

    }

    public PublicKeyCredentialDescriptorBuilder toBuilder() {
        return new PublicKeyCredentialDescriptorBuilder().type(this.type).id(this.id).transports(this.transports);
    }

    /**
     * The type of the credential the caller is referring to.
     * @return 
     */
    @NonNull
    public PublicKeyCredentialType getType() {
        return this.type;
    }

    /**
     * The credential ID of the public key credential the caller is referring to.
     * @return 
     */
    @NonNull
    public ByteArray getId() {
        return this.id;
    }

    /**
     * An OPTIONAL hint as to how the client might communicate with the managing authenticator of the public key

     * credential the caller is referring to.
     * @return 
     */
    @NonNull
    public Optional<Set<AuthenticatorTransport>> getTransports() {
        return this.transports;
    }
}
