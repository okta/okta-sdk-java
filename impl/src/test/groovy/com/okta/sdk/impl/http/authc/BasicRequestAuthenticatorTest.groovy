/*
 * Copyright 2014 Stormpath, Inc.
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
 */
package com.okta.sdk.impl.http.authc

import com.okta.sdk.impl.authc.credentials.ApiKeyCredentials
import com.okta.sdk.impl.http.HttpHeaders
import com.okta.sdk.impl.http.Request
import org.testng.annotations.Test

import java.text.SimpleDateFormat

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 0.9.3
 */
class BasicRequestAuthenticatorTest {

    private static final String TIMESTAMP_FORMAT = "yyyyMMdd'T'HHmmss'Z'";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String OKTA_DATE_HEADER = "X-Okta-Date";
    private static final String TIME_ZONE = "UTC";

    @Test
    public void test(){

        def id = "fooId"
        def secret = "barKey"
        String authorizationHeader = com.okta.sdk.impl.util.Base64.encodeBase64String((id + ":" + secret).getBytes("UTF-8"));

        def request = createStrictMock(Request)
        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)

        HttpHeaders headers = new HttpHeaders()

        assertNull(headers.get(AUTHORIZATION_HEADER))

        expect(request.getHeaders()).andReturn(headers) times(2)
        expect(apiKeyCredentials.getId()).andReturn(id)
        expect(apiKeyCredentials.getSecret()).andReturn(secret)

        replay(request, apiKeyCredentials)

        def requestAuthenticator = new BasicRequestAuthenticator(apiKeyCredentials);
        requestAuthenticator.authenticate(request)

        assertEquals(headers.get(AUTHORIZATION_HEADER).size(), 1)
        assertEquals(headers.get(AUTHORIZATION_HEADER).get(0), "Basic " + authorizationHeader)
        assertEquals(headers.get(OKTA_DATE_HEADER).size(), 1)

        try {
            def oktaDateHeader = headers.get(OKTA_DATE_HEADER).get(0)
            SimpleDateFormat timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT)
            timestampFormat.setTimeZone(new SimpleTimeZone(0, TIME_ZONE))
            Date oktaDate = timestampFormat.parse(oktaDateHeader)
            def date = new Date()
            assertTrue(oktaDate.before(date))
        } catch(Exception e) {
            fail("Exception validating generated date in header: " + e.getMessage())
        }

        verify(request, apiKeyCredentials)

    }

}
