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

package org.iton.fido.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.spongycastle.util.encoders.Hex;


public class BinaryUtil {

    public static byte[] copy(byte[] bytes) {
        return Arrays.copyOf(bytes, bytes.length);
    }

    /**
     * @param bytes
     *     Bytes to encode
     * @return 
     */
    public static String toHex(byte[] bytes) {
        return Hex.toHexString(bytes);
    }

    /**
     * @param hex
     *     String of hexadecimal digits to decode as bytes.
     * @return 
     */
    public static byte[] fromHex(String hex) {
        return Hex.decode(hex);
    }

    /**
     * Parse a single byte from two hexadecimal characters.
     *
     * @param hex
     *     String of hexadecimal digits to decode as bytes.
     * @return 
     */
    public static byte singleFromHex(String hex) {
        ExceptionUtil.assure(hex.length() == 2, "Argument must be exactly 2 hexadecimal characters, was: %s", hex);
        return fromHex(hex)[0];
    }

        /**
         * Read one byte as an unsigned 8-bit integer.
         * <p>
         * Result is of type Short because Java don't have unsigned types.
         *
     * @param b
         * @return A value between 0 and 255, inclusive.
         */
    public static short getUint8(byte b) {
        // Prepend a zero so we can parse it as a signed int16 instead of a signed int8
        return ByteBuffer.wrap(new byte[]{ 0, b })
            .order(ByteOrder.BIG_ENDIAN)
            .getShort();
    }


    /**
     * Read 2 bytes as a big endian unsigned 16-bit integer.
     * <p>
     * Result is of type Int because Java don't have unsigned types.
     *
     * @param bytes
     * @return A value between 0 and 2^16- 1, inclusive.
     */
    public static int getUint16(byte[] bytes) {
        if (bytes.length == 2) {
            // Prepend zeroes so we can parse it as a signed int32 instead of a signed int16
            return ByteBuffer.wrap(new byte[] { 0, 0, bytes[0], bytes[1] })
                .order(ByteOrder.BIG_ENDIAN)
                .getInt();
        } else {
            throw new IllegalArgumentException("Argument must be 2 bytes, was: " + bytes.length);
        }
    }


    /**
     * Read 4 bytes as a big endian unsigned 32-bit integer.
     * <p>
     * Result is of type Long because Java don't have unsigned types.
     *
     * @param bytes
     * @return A value between 0 and 2^32 - 1, inclusive.
     */
    public static int getUint32(byte[] bytes) {
        if (bytes.length == 4) {
            // Prepend zeroes so we can parse it as a signed int32 instead of a signed int16
            return ByteBuffer.wrap(new byte[] { 0, 0, 0, 0, bytes[0], bytes[1], bytes[2], bytes[3] })
                .order(ByteOrder.BIG_ENDIAN)
                .getInt();
        } else {
            throw new IllegalArgumentException("Argument must be 4 bytes, was: " + bytes.length);
        }
    }

    public static byte[] encodeUint16(int value) {
        ExceptionUtil.assure(value >= 0, "Argument must be non-negative, was: %d", value);
        ExceptionUtil.assure(value < 65536, "Argument must be smaller than 2^15=65536, was: %d", value);

        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(ByteOrder.BIG_ENDIAN);
        b.putInt(value);
        b.rewind();
        return Arrays.copyOfRange(b.array(), 2, 4);
    }
    
    public static byte[] encodeUint32(int value) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(ByteOrder.BIG_ENDIAN);
        b.putInt(value);
        b.rewind();
        return b.array();
    }

    /**
     * Returns the values from each provided array combined into a single array. For example, {@code
     * concat(new byte[] {a, b}, new byte[] {}, new byte[] {c}} returns the array {@code {a, b, c}}.
     *
     * @param arrays zero or more {@code byte} arrays
     * @return a single array containing all the values from the source arrays, in order
     */
    public static byte[] concat(byte[]... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }
        byte[] result = new byte[length];
        int pos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, pos, array.length);
            pos += array.length;
        }
        return result;
    }


    /**
     * Get the i'th bit of a byte array.
     * @param h the byte array.
     * @param i the bit index.
     * @return 0 or 1, the value of the i'th bit in h
     */
    public static int bit(byte[] h, int i) {
        return (h[i >> 3] >> (i & 7)) & 1;
    }

    /**
     * Constant-time determine if byte is negative.
     * @param b the byte to check.
     * @return 1 if the byte is negative, 0 otherwise.
     */
    public static int negative(int b) {
        return (b >> 8) & 1;
    }
}
