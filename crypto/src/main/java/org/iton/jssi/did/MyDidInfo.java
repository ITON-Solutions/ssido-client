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

package org.iton.jssi.did;

import androidx.annotation.Nullable;

import org.iton.jssi.crypto.algorithm.ICrypto.CryptoType;

/**
 *
 * @author ITON Solutions
 */
public class MyDidInfo {
    
    public @Nullable String did;
    public @Nullable String seed;
    public String cryptoType;
    public boolean cid;
    
    
    public MyDidInfo(@Nullable String did, @Nullable String seed) {
        this.did = did;
        this.seed = seed;
        this.cryptoType = CryptoType.DEFAULT_CRYPTO_TYPE.getName();
        this.cid = false;
    }
    
    public MyDidInfo(@Nullable String did, @Nullable String seed, @Nullable String cryptoType, boolean cid) {
        this.did = did;
        this.seed = seed;
        this.cryptoType = cryptoType == null ? CryptoType.DEFAULT_CRYPTO_TYPE.getName() : cryptoType;
        this.cid = cid;
    }
    
    @Override
    public String toString(){
        return String.format("MyDidInfo: {%s, type: %s}", did, cryptoType);
    }
}
