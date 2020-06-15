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
package org.iton.fido.crypto.spec;

import org.iton.fido.crypto.math.GroupElement;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Arrays;


/**
 * @author str4d
 *
 */
public class EdDSAPrivateKeySpec implements KeySpec {
    private final byte[] seed;
    private final byte[] h;
    private final byte[] a;
    private final GroupElement A;
    private final EdDSAParameterSpec spec;

    /**
     *  @param seed the private key
     *  @param spec the parameter specification for this key
     *  @throws IllegalArgumentException if seed length is wrong or hash algorithm is unsupported
     */
    public EdDSAPrivateKeySpec(byte[] seed, EdDSAParameterSpec spec) {
        if (seed.length != spec.getCurve().getField().getb()/8)
            throw new IllegalArgumentException("seed length is wrong");

        this.spec = spec;
        this.seed = seed;

        try {
            MessageDigest hash = MessageDigest.getInstance(spec.getHashAlgorithm());
            int b = spec.getCurve().getField().getb();

            // H(k)
            h = hash.digest(seed);

            /*a = BigInteger.valueOf(2).pow(b-2);
            for (int i=3;i<(b-2);i++) {
                a = a.add(BigInteger.valueOf(2).pow(i).multiply(BigInteger.valueOf(Utils.bit(h,i))));
            }*/
            // Saves ~0.4ms per key when running signing tests.
            // TODO: are these bitflips the same for any hash function?
            h[0] &= 248;
            h[(b/8)-1] &= 63;
            h[(b/8)-1] |= 64;
            a = Arrays.copyOfRange(h, 0, b/8);

            A = spec.getB().scalarMultiply(a);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Unsupported hash algorithm");
        }
    }

    /**
     *  Initialize directly from the hash.
     *  getSeed() will return null if this constructor is used.
     *
     *  @param spec the parameter specification for this key
     *  @param h the private key
     *  @throws IllegalArgumentException if hash length is wrong
     *  @since 0.1.1
     */
    public EdDSAPrivateKeySpec(EdDSAParameterSpec spec, byte[] h) {
        if (h.length != spec.getCurve().getField().getb()/4)
            throw new IllegalArgumentException("hash length is wrong");

	this.seed = null;
	this.h = h;
	this.spec = spec;
	int b = spec.getCurve().getField().getb();

        h[0] &= 248;
        h[(b/8)-1] &= 63;
        h[(b/8)-1] |= 64;
        a = Arrays.copyOfRange(h, 0, b/8);

        A = spec.getB().scalarMultiply(a);
    }

    public EdDSAPrivateKeySpec(byte[] seed, byte[] h, byte[] a, GroupElement A, EdDSAParameterSpec spec) {
        this.seed = seed;
        this.h = h;
        this.a = a;
        this.A = A;
        this.spec = spec;
    }

    /**
     *  @return will be null if constructed directly from the private key
     */
    public byte[] getSeed() {
        return seed;
    }

    /**
     *  @return the hash
     */
    public byte[] getH() {
        return h;
    }

    /**
     *  @return the private key
     */
    public byte[] geta() {
        return a;
    }

    /**
     *  @return the public key
     */
    public GroupElement getA() {
        return A;
    }

    public EdDSAParameterSpec getParams() {
        return spec;
    }
}
