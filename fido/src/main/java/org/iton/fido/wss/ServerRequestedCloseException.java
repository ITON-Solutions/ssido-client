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

package org.iton.fido.wss;

import androidx.annotation.NonNull;

import java.io.IOException;

/**
 * Created by ITON Solutions on 19/09/2019.
 */
public class ServerRequestedCloseException extends IOException {

    private final int code;
    @NonNull
    private final String reason;

    public ServerRequestedCloseException(int code, @NonNull String reason) {
        super(String.format("Server requested connection to close, code: %d, reason: %s", code, reason));
        this.code = code;
        this.reason = reason;
    }

    /**
     * Code why close requested
     * @return code why close requested
     */
    public int code() {
        return code;
    }

    /**
     * Reason why close requested
     * @return reason why close requested or empty string
     */
    @NonNull
    public String reason() {
        return reason;
    }

}
