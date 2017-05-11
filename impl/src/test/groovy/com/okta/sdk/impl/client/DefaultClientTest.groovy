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
package com.okta.sdk.impl.client

import com.okta.sdk.cache.CacheManager
import com.okta.sdk.client.AuthenticationScheme
import com.okta.sdk.impl.api.ApiKeyResolver
import com.okta.sdk.impl.authc.credentials.ApiKeyCredentials
import com.okta.sdk.impl.http.authc.RequestAuthenticatorFactory
import com.okta.sdk.impl.util.BaseUrlResolver
import com.okta.sdk.lang.Classes
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.testng.PowerMockTestCase
import org.testng.annotations.Test

import static org.easymock.EasyMock.expect
import static org.powermock.api.easymock.PowerMock.*
import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

/**
 * @since 1.0.RC9
 */
@PrepareForTest(Classes)
class DefaultClientTest extends PowerMockTestCase {

    @Test
    void testCreateRequestExecutor() {

        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)
        def apiKeyResolver = createStrictMock(ApiKeyResolver)
        def cacheManager = createStrictMock(CacheManager)
        def requestAuthenticatorFactory = createStrictMock(RequestAuthenticatorFactory)
        def baseUrlResolver = createStrictMock(BaseUrlResolver)

        def className = "com.okta.sdk.impl.http.httpclient.HttpClientRequestExecutor"

        mockStatic(Classes)
        expect(Classes.isAvailable(className)).andReturn(false)

        replayAll()

        try {
            new DefaultClient(apiKeyCredentials, apiKeyResolver, baseUrlResolver, null, cacheManager, AuthenticationScheme.BASIC, requestAuthenticatorFactory, 3600)
            fail("shouldn't be here")
        } catch (Exception e) {
            assertEquals e.getMessage(), "Unable to find the '" + className +
                "' implementation on the classpath.  Please ensure you " +
                "have added the okta-sdk-httpclient .jar file to your runtime classpath."
        }
    }
}
