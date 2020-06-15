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
package org.iton.fido.crypto.math;


import org.iton.fido.util.CryptoUtil;

final class Constants {
    static final byte[] ZERO = CryptoUtil.fromHex("0000000000000000000000000000000000000000000000000000000000000000");
    static final byte[] ONE = CryptoUtil.fromHex("0100000000000000000000000000000000000000000000000000000000000000");
    static final byte[] TWO = CryptoUtil.fromHex("0200000000000000000000000000000000000000000000000000000000000000");
    static final byte[] FOUR = CryptoUtil.fromHex("0400000000000000000000000000000000000000000000000000000000000000");
    static final byte[] FIVE = CryptoUtil.fromHex("0500000000000000000000000000000000000000000000000000000000000000");
    static final byte[] EIGHT = CryptoUtil.fromHex("0800000000000000000000000000000000000000000000000000000000000000");
}
