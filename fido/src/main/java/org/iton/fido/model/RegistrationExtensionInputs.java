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

import java.util.Collections;
import java.util.Set;

/**
 * Contains <a href="https://www.w3.org/TR/webauthn/#client-extension-input">client extension
 * inputs</a> to a
 * <code>navigator.credentials.create()</code> operation. All members are optional.
 *
 * <p>
 * The authenticator extension inputs are derived from these client extension inputs.
 * </p>
 *
 * @see <a href="https://www.w3.org/TR/webauthn/#extensions">§9. WebAuthn Extensions</a>
 */
public final class RegistrationExtensionInputs implements ExtensionInputs {
    @Override
    public Set<String> getExtensionIds() {
        return Collections.emptySet();
    }

    public static class RegistrationExtensionInputsBuilder {
        RegistrationExtensionInputsBuilder() {
        }

        public RegistrationExtensionInputs build() {
            return new RegistrationExtensionInputs();
        }
    }

      public static RegistrationExtensionInputsBuilder builder() {
        return new RegistrationExtensionInputsBuilder();
    }

    private RegistrationExtensionInputs() {
    }
}
