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
import com.okta.sdk.impl.api.ClientCredentialsResolver
import com.okta.sdk.impl.http.authc.RequestAuthenticatorFactory
import com.okta.sdk.impl.test.RestoreSecurityManager
import com.okta.sdk.impl.util.BaseUrlResolver
import org.testng.annotations.Listeners
import org.testng.annotations.Test

import java.security.Permission

import static org.mockito.Mockito.*
import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

/**
 * @since 1.0.0
 */

@Listeners(RestoreSecurityManager.class)
class DefaultClientTest {

    @Test
    void testCreateRequestExecutor() {

        def apiKeyResolver = mock(ClientCredentialsResolver)
        def cacheManager = mock(CacheManager)
        def requestAuthenticatorFactory = mock(RequestAuthenticatorFactory)
        def baseUrlResolver = mock(BaseUrlResolver)

        def className = "com.okta.sdk.impl.http.httpclient.HttpClientRequestExecutor"

        System.setSecurityManager(new SecurityManager() {
            @Override
            void checkPermission(Permission perm) {}

            @Override
            void checkPermission(Permission perm, Object context) {}

            @Override
            void checkPackageAccess(String pkg) {
                if ("com.okta.sdk.impl.http.httpclient".equals(pkg)) {
                    throw new ClassNotFoundException("Thrown intentionally DefaultClientTest.testCreateRequestExecutor()")
                }
            }
        })

        try {
            new DefaultClient(apiKeyResolver, baseUrlResolver, null, cacheManager, AuthenticationScheme.SSWS, requestAuthenticatorFactory, 3600)
            fail("shouldn't be here")
        } catch (Exception e) {
            assertEquals e.getMessage(), "Unable to find the '" + className +
                "' implementation on the classpath.  Please ensure you " +
                "have added the okta-sdk-httpclient .jar file to your runtime classpath."
        }
    }
}
