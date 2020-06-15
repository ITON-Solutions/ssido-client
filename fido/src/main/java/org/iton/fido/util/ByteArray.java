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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.jetbrains.annotations.NotNull;
import org.spongycastle.util.Arrays;

import android.util.Base64;

import androidx.annotation.NonNull;

/**
 * An immutable byte array with support for encoding/decoding to/from various encodings.
 */
@JsonSerialize(using = JsonStringSerializer.class)
public final class ByteArray implements Comparable<ByteArray>, JsonStringSerializable {

    @NonNull
    private final byte[] bytes;
    @NonNull
    private final String base64;

    /**
     * Create a new instance by copying the contents of <code>bytes</code>.
     * @param bytes
     */
    public ByteArray(@NonNull byte[] bytes) {
        this.bytes = BinaryUtil.copy(bytes);
        this.base64 = Base64.encodeToString(this.bytes, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
    }

    @JsonCreator
    private ByteArray(@NotNull String base64) throws Base64UrlException {
        try {
            this.bytes = Base64.decode(base64, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
        } catch (IllegalArgumentException e) {
            throw new Base64UrlException("Invalid Base64Url encoding: " + base64, e);
        }
        this.base64 = base64;
    }

    /**
     * Create a new instance by decoding <code>base64</code> as classic Base64 data.
     * @param base64
     * @return 
     */
    public static ByteArray fromBase64(@NonNull final String base64) {
        return new ByteArray(Base64.decode(base64, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));
    }

    /**
     * Create a new instance by decoding <code>base64</code> as Base64Url data.
     *
     * @param base64
     * @return 
     * @throws Base64UrlException if <code>base64</code> is not valid Base64Url data.
     */
    public static ByteArray fromBase64Url(@NonNull final String base64) throws Base64UrlException {
        return new ByteArray(base64);
    }

    /**
     * Create a new instance by decoding <code>hex</code> as hexadecimal data.
     *
     * @param hex
     * @return 
     * @throws HexException if <code>hex</code> is not valid hexadecimal data.
     */
    public static ByteArray fromHex(@NonNull final String hex) throws HexException {
        try {
            return new ByteArray(BinaryUtil.fromHex(hex));
        } catch (Exception e) {
            throw new HexException("Invalid hexadecimal encoding: " + hex, e);
        }
    }

    /**
     * @param tail
     * @return a new instance containing a copy of this instance followed by a copy of <code>tail</code>.
     */
    public ByteArray concat(@NonNull ByteArray tail) {
        return new ByteArray(Arrays.concatenate(this.bytes, tail.bytes));
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return this.bytes.length;
    }

    /**
     * @return a copy of the raw byte contents.
     */
    public byte[] getBytes() {
        return BinaryUtil.copy(bytes);
    }

    /**
     * @return the content bytes encoded as classic Base64 data.
     */
    public String getBase64() {
        return Base64.encodeToString(bytes, Base64.NO_PADDING | Base64.NO_WRAP);
    }

    /**
     * @return the content bytes encoded as Base64Url data.
     */
    public String getBase64Url() {
        return base64;
    }

    /**
     * @return the content bytes encoded as hexadecimal data.
     */
    public String getHex() {
        return BinaryUtil.toHex(bytes);
    }

    /**
     * Used by JSON serializer.
     * @return
     */
    @Override
    public String toJsonString() {
        return base64;
    }

    @Override
    public int compareTo(ByteArray other) {
        if (bytes.length != other.bytes.length) {
            return bytes.length - other.bytes.length;
        }
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] != other.bytes[i]) {
                return bytes[i] - other.bytes[i];
            }
        }
        return 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ByteArray)) return false;
        final ByteArray other = (ByteArray) o;
        if (!Arrays.areEqual(this.getBytes(), other.getBytes())) return false;
        return !(this.getBase64() == null ? other.getBase64() != null : !this.getBase64().equals(other.getBase64()));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getBytes());
    }

    @Override
    public String toString() {
        return "ByteArray(" + this.getHex() + ")";
    }
}
