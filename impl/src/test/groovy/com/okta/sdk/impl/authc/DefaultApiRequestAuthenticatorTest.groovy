/*
 * Copyright 2017 Okta
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
package com.okta.sdk.impl.authc

import com.okta.sdk.api.ApiAuthenticationResult
import com.okta.sdk.http.HttpRequest
import org.testng.annotations.Test

import static org.easymock.EasyMock.expect
import static org.powermock.api.easymock.PowerMock.*
import static org.testng.Assert.assertEquals

/**
 * @since 1.0.RC9
 */
class DefaultApiRequestAuthenticatorTest {

    @Test
    void testConstructWithHttpRequest() {

        def httpRequest = createStrictMock(HttpRequest)

        replayAll()

        new DefaultApiRequestAuthenticator(httpRequest)

        verifyAll()
    }

    @Test
    void testExecuteHttpRequest() {

        def httpRequest = createStrictMock(HttpRequest)
        def expectedResult = createStrictMock(ApiAuthenticationResult)

        def defaultApiRequestAuthenticator = createPartialMock(DefaultApiRequestAuthenticator, 'execute')

        expect(defaultApiRequestAuthenticator.execute()).andReturn(expectedResult)

        replayAll()

        def actualResult = defaultApiRequestAuthenticator.authenticate(httpRequest)

        assertEquals actualResult, expectedResult
    }
}
