/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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

import com.okta.commons.http.RequestExecutorFactory
import com.okta.commons.http.config.BaseUrlResolver
import com.okta.sdk.cache.CacheManager
import com.okta.sdk.client.AuthenticationScheme
import com.okta.commons.lang.Classes
import com.okta.sdk.impl.api.ClientCredentialsResolver
import com.okta.sdk.impl.config.ClientConfiguration
import com.okta.sdk.impl.http.authc.RequestAuthenticatorFactory
import org.mockito.Mock
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.testng.PowerMockTestCase
import org.testng.annotations.Test


import static org.mockito.Mockito.*
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.testng.Assert.fail
import static org.powermock.api.mockito.PowerMockito.mockStatic

/**
 * @since 0.5.0
 */

@PrepareForTest(Classes.class)
class DefaultClientTest extends PowerMockTestCase {

    @Mock
    private ServiceLoader mockServiceLoader

    @Test
    void testCreateRequestExecutor() {

        mockStatic(Classes.class)
        when(Classes.loadFromService(eq(RequestExecutorFactory), any(String))).then(new Answer<Object>() {
            @Override
            Object answer(InvocationOnMock invocation) throws Throwable {
                throw new IllegalStateException(invocation.getArgument(1))
            }
        })

        def apiKeyResolver = mock(ClientCredentialsResolver)
        def cacheManager = mock(CacheManager)
        def requestAuthenticatorFactory = mock(RequestAuthenticatorFactory)
        def baseUrlResolver = mock(BaseUrlResolver)
        def className = RequestExecutorFactory.name

        ClientConfiguration clientConfiguration = new ClientConfiguration()
        clientConfiguration.setClientCredentialsResolver(apiKeyResolver)
        clientConfiguration.setRequestAuthenticatorFactory(requestAuthenticatorFactory)
        clientConfiguration.setBaseUrlResolver(baseUrlResolver)
        clientConfiguration.setConnectionTimeout(3600)
        clientConfiguration.setAuthenticationScheme(AuthenticationScheme.SSWS)

        try {
            new DefaultClient(clientConfiguration, cacheManager)
            fail("shouldn't be here")
        } catch (Exception e) {
            assertThat e.getMessage(), is(("Unable to find a '${className}' " +
                "implementation on the classpath.  Please ensure you " +
                "have added the okta-sdk-httpclient.jar file to your runtime classpath.").toString())
        }
    }
}
