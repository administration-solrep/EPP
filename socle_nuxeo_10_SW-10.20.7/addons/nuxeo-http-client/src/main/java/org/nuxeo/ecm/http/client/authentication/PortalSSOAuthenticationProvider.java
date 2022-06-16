/*
 * (C) Copyright 2006-2007 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id: JOOoConvertPluginImpl.java 18651 2007-05-13 20:28:53Z sfermigier $
 */

package org.nuxeo.ecm.http.client.authentication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PortalSSOAuthenticationProvider {

    private static final String TOKEN_SEP = ":";

    private static final String TS_HEADER = "NX_TS";

    private static final String RANDOM_HEADER = "NX_RD";

    private static final String TOKEN_HEADER = "NX_TOKEN";

    private static final String USER_HEADER = "NX_USER";

    protected static final Random RANDOM = new SecureRandom();

    public static Map<String, String> getHeaders(String secretKey, String userName) {

        Map<String, String> headers = new HashMap<String, String>();

        Date timestamp = new Date();
        int randomData = RANDOM.nextInt();

        String clearToken = timestamp.getTime() + TOKEN_SEP + randomData + TOKEN_SEP + secretKey + TOKEN_SEP + userName;

        byte[] hashedToken;

        try {
            hashedToken = MessageDigest.getInstance("MD5").digest(clearToken.getBytes());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        String base64HashedToken = Base64.getEncoder().encodeToString(hashedToken);

        headers.put(TS_HEADER, String.valueOf(timestamp.getTime()));
        headers.put(RANDOM_HEADER, String.valueOf(randomData));
        headers.put(TOKEN_HEADER, base64HashedToken);
        headers.put(USER_HEADER, userName);

        return headers;
    }

}
