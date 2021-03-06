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
package org.iton.fido.crypto.math.bigint;

import org.iton.fido.crypto.math.Field;
import org.iton.fido.crypto.math.FieldElement;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * A particular element of the field \Z/(2^255-19).
 * @author str4d
 *
 */
public class BigIntegerFieldElement extends FieldElement implements Serializable {
    /**
     * Variable is package private for encoding.
     */
    final BigInteger bi;

    public BigIntegerFieldElement(Field f, BigInteger bi) {
        super(f);
        this.bi = bi;
    }

    public boolean isNonZero() {
        return !bi.equals(BigInteger.ZERO);
    }

    public FieldElement add(FieldElement val) {
        return new BigIntegerFieldElement(f, bi.add(((BigIntegerFieldElement)val).bi)).mod(f.getQ());
    }

    @Override
    public FieldElement addOne() {
        return new BigIntegerFieldElement(f, bi.add(BigInteger.ONE)).mod(f.getQ());
    }

    public FieldElement subtract(FieldElement val) {
        return new BigIntegerFieldElement(f, bi.subtract(((BigIntegerFieldElement)val).bi)).mod(f.getQ());
    }

    @Override
    public FieldElement subtractOne() {
        return new BigIntegerFieldElement(f, bi.subtract(BigInteger.ONE)).mod(f.getQ());
    }

    public FieldElement negate() {
        return f.getQ().subtract(this);
    }

    @Override
    public FieldElement divide(FieldElement val) {
        return divide(((BigIntegerFieldElement)val).bi);
    }

    public FieldElement divide(BigInteger val) {
        return new BigIntegerFieldElement(f, bi.divide(val)).mod(f.getQ());
    }

    public FieldElement multiply(FieldElement val) {
        return new BigIntegerFieldElement(f, bi.multiply(((BigIntegerFieldElement)val).bi)).mod(f.getQ());
    }

    public FieldElement square() {
        return multiply(this);
    }

    public FieldElement squareAndDouble() {
        FieldElement sq = square();
        return sq.add(sq);
    }

    public FieldElement invert() {
        // Euler's theorem
        //return modPow(f.getQm2(), f.getQ());
        return new BigIntegerFieldElement(f, bi.modInverse(((BigIntegerFieldElement)f.getQ()).bi));
    }

    public FieldElement mod(FieldElement m) {
        return new BigIntegerFieldElement(f, bi.mod(((BigIntegerFieldElement)m).bi));
    }

    public FieldElement modPow(FieldElement e, FieldElement m) {
        return new BigIntegerFieldElement(f, bi.modPow(((BigIntegerFieldElement)e).bi, ((BigIntegerFieldElement)m).bi));
    }

    public FieldElement pow(FieldElement e){
        return modPow(e, f.getQ());
    }

    public FieldElement pow22523(){
        return pow(f.getQm5d8());
    }

    @Override
    public FieldElement cmov(FieldElement val, int b) {
        // Not constant-time, but it doesn't really matter because none of the underlying BigInteger operations
        // are either, so there's not much point in trying hard here ...
        return b == 0 ? this : val;
    }

    @Override
    public int hashCode() {
        return bi.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BigIntegerFieldElement))
            return false;
        BigIntegerFieldElement fe = (BigIntegerFieldElement) obj;
        return bi.equals(fe.bi);
    }

    @Override
    public String toString() {
        return "[BigIntegerFieldElement val=" + bi + "]";
    }
}
