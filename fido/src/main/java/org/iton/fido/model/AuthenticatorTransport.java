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
 * Authenticators may communicate with Clients using a variety of transports. This enumeration defines a hint as to how
 * Clients might communicate with a particular Authenticator in order to obtain an assertion for a specific credential.
 * Note that these hints represent the Relying Party's best belief as to how an Authenticator may be reached. A Relying
 * Party may obtain a list of transports hints from some attestation statement formats or via some out-of-band
 * mechanism; it is outside the scope of this specification to define that mechanism.
 * <p>
 * Authenticators may implement various transports for communicating with clients. This enumeration defines hints as to
 * how clients might communicate with a particular authenticator in order to obtain an assertion for a specific
 * credential. Note that these hints represent the WebAuthn Relying Party's best belief as to how an authenticator may
 * be reached. A Relying Party may obtain a list of transports hints from some attestation statement formats or via some
 * out-of-band mechanism; it is outside the scope of the Web Authentication specification to define that mechanism.
 * </p>
 *
 * @see <a href="https://www.w3.org/TR/webauthn/#enumdef-authenticatortransport">§5.10.4. Authenticator
 * Transport Enumeration (enum AuthenticatorTransport)</a>
 */
@JsonSerialize(using = JsonStringSerializer.class)
public enum AuthenticatorTransport implements JsonStringSerializable {
    /**
     * Indicates the respective authenticator can be contacted over removable USB.
     */
    USB("usb"),
    /**
     * Indicates the respective authenticator can be contacted over Near Field Communication (NFC).
     */
    NFC("nfc"),
    /**
     * Indicates the respective authenticator can be contacted over Bluetooth Smart (Bluetooth Low Energy / BLE).
     */
    BLE("ble"),
    /**
     * Indicates the respective authenticator is contacted using a client device-specific transport. These
     * authenticators are not removable from the client device.
     */
    INTERNAL("internal");
    @NonNull
    private final String id;

    private static Optional<AuthenticatorTransport> fromString(@NonNull String id) {
        Optional<AuthenticatorTransport> result = Optional.absent();
        for(AuthenticatorTransport item : values()){
            if(item.id.equals(id)){
                result = Optional.of(item);
            }
        }
        return result;
    }

    @JsonCreator
    private static AuthenticatorTransport fromJsonString(@NonNull String id) {
        Optional<AuthenticatorTransport> result = fromString(id);
        if(result.isPresent()){
            return result.get();
        }
        throw new IllegalArgumentException(String.format("Unknown %s value: %s", AuthenticatorTransport.class.getSimpleName(), id));
    }

    @Override
    public String toJsonString() {
        return id;
    }

    AuthenticatorTransport(@NonNull final String id) {
        this.id = id;
    }
}
