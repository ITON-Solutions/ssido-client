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


/**
 * The flags bit field of an authenticator data structure, decoded as a high-level object.
 *
 * @see <a href="https://www.w3.org/TR/webauthn/#flags">Table 1</a>
 */
public final class AuthenticatorDataFlags {
    private byte flag;
    /**
     * User present
     */
    public final boolean UP;
    /**
     * User verified
     */
    public final boolean UV;
    /**
     * Attested credential data present.
     *
     * <p>
     * Users of this library should not need to inspect this value directly.
     * </p>
     *
     * @see AuthenticatorData#getAttestedCredentialData()
     */
    public final boolean AT;
    /**
     * Extension data present.
     *
     * @see AuthenticatorData#getExtensions()
     */
    public final boolean ED;

    /**
     * Decode an {@link AuthenticatorDataFlags} object from a raw bit field byte.
     * @param flag
     */
    private AuthenticatorDataFlags(byte flag) {
        this.flag = flag;
        UP = (flag & Bitmasks.UP) != 0;
        UV = (flag & Bitmasks.UV) != 0;
        AT = (flag & Bitmasks.AT) != 0;
        ED = (flag & Bitmasks.ED) != 0;
    }


    private static final class Bitmasks {
        static final byte UP = (byte) 0x01;
        static final byte UV = (byte) 0x04;
        static final byte AT = (byte) 0x40;
        static final byte ED = (byte) 0x80;
        /* Reserved bits */
        // final boolean RFU1 = (value & 0x02) > 0;
        // final boolean RFU2_1 = (value & 0x08) > 0;
        // final boolean RFU2_2 = (value & 0x10) > 0;
        // static final boolean RFU2_3 = (value & 0x20) > 0;
    }
    
    public static AuthenticatorDataFlagsBuilder builder() {
        return new AuthenticatorDataFlagsBuilder();
    }

    public static class AuthenticatorDataFlagsBuilder {
        private int flag = Bitmasks.AT | Bitmasks.UP;

        AuthenticatorDataFlagsBuilder() {
        }

        /**
         * The user is verified.
         * @param UV
         * @return 
         */
        public AuthenticatorDataFlagsBuilder verified(boolean UV) {
            flag |= Bitmasks.UV;
            return this;
        }

        /**
         * The Attested credential data present.
         * @param AT
         * @return 
         */
        public AuthenticatorDataFlagsBuilder attested(boolean AT) {
            flag |= Bitmasks.AT;
            return this;
        }

        /**
         * Extension data present.
         * @param ED
         * @return 
         */
        public AuthenticatorDataFlagsBuilder extension(boolean ED) {
            flag |= Bitmasks.ED;
            return this;
        }

        public AuthenticatorDataFlags build() {
            return new AuthenticatorDataFlags((byte) flag);
        }
    }
    
    public byte[] getBytes(){
        return new byte[]{flag};
    }
}
