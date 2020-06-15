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
package org.iton.fido.model.extension;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.net.InetAddresses;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A FIDO AppID verified to be syntactically valid.
 *
 * @see <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-appid-and-facets-v2.0-id-20180227.html">FIDO AppID and Facet Specification</a>
 */
@JsonSerialize(using = AppId.JsonSerializer.class)
public final class AppId {
    /**
     * The underlying string representation of this AppID.
     */
    private final String id;

    /**
     * Verify that the <code>appId</code> is a valid FIDO AppID, and wrap it as an {@link AppId}.
     *
     * @param appid
     * @throws InvalidAppIdException if <code>appId</code> is not a valid FIDO AppID.
     * @see <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-appid-and-facets-v2.0-id-20180227.html">FIDO AppID and Facet Specification</a>
     */
    @JsonCreator
    private AppId(@JsonProperty("appid") String appid) throws InvalidAppIdException {
        checkIsValid(appid);
        this.id = appid;
    }

    /**
     * Throws {@link InvalidAppIdException} if the given App ID is found to be incompatible with the U2F specification or any major
     * U2F Client implementation.
     *
     * @param appId the App ID to be validated
     */
    private static void checkIsValid(String appId) throws InvalidAppIdException {
        if (!appId.contains(":")) {
            throw new InvalidAppIdException("App ID does not look like a valid facet or URL. Web facets must start with \'https://\'.");
        }
        if (appId.startsWith("http:")) {
            throw new InvalidAppIdException("HTTP is not supported for App IDs (by Chrome). Use HTTPS instead.");
        }
        if (appId.startsWith("https://")) {
            URI url = checkValidUrl(appId);
            checkPathIsNotSlash(url);
            checkNotIpAddress(url);
        }
    }

    private static void checkPathIsNotSlash(URI url) throws InvalidAppIdException {
        if ("/".equals(url.getPath())) {
            throw new InvalidAppIdException("The path of the URL set as App ID is \'/\'. This is probably not what you want -- remove the trailing slash of the App ID URL.");
        }
    }

    private static URI checkValidUrl(String appId) throws InvalidAppIdException {
        try {
            return new URI(appId);
        } catch (URISyntaxException e) {
            throw new InvalidAppIdException("App ID looks like a HTTPS URL, but has syntax errors.", e);
        }
    }

    private static void checkNotIpAddress(URI url) throws InvalidAppIdException {
        if (InetAddresses.isInetAddress(url.getAuthority()) || (url.getHost() != null && InetAddresses.isInetAddress(url.getHost()))) {
            throw new InvalidAppIdException("App ID must not be an IP-address, since it is not supported (by Chrome). Use a host name instead.");
        }
    }


    static class JsonSerializer extends com.fasterxml.jackson.databind.JsonSerializer<AppId> {
        @Override
        public void serialize(AppId value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.getId());
        }
    }

    /**
     * The underlying string representation of this AppID.
     * @return 
     */
    public String getId() {
        return this.id;
    }
}
