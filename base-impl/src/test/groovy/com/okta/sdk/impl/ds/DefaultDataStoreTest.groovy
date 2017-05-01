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
package com.okta.sdk.impl.ds

import com.okta.sdk.api.ApiKey
import com.okta.sdk.impl.api.ApiKeyResolver
import com.okta.sdk.impl.api.DefaultApiKeyResolver
import com.okta.sdk.impl.authc.credentials.ApiKeyCredentials
import com.okta.sdk.impl.http.RequestExecutor
import com.okta.sdk.impl.query.DefaultOptions
import com.okta.sdk.query.Options
import com.okta.sdk.resource.Resource
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.*

/**
 * @since 1.0.beta
 */
class DefaultDataStoreTest {



    /**
     * @since 1.0.RC4.6
     */
    @Test
    void testGetResource_Expanded_InvalidArguments() {
        def requestExecutor = createStrictMock(RequestExecutor)
        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)
        def apiKeyResolver = createStrictMock(ApiKeyResolver)
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com/v1", apiKeyCredentials, apiKeyResolver)
        def emptyOptions = createStrictMock(DefaultOptions)
        def resourceData = Resource
        def href = "http://api.okta.com/v1/directories/2B6PLkZ8AGvWlziq18JJ62"

        try {
            defaultDataStore.getResource(null, resourceData, emptyOptions)
            fail("should have thrown due to empty href")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "href argument cannot be null or empty.")
        }

        try {
            defaultDataStore.getResource(href, null, emptyOptions)
            fail("should have thrown due to empty class")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Resource class argument cannot be null.")
        }

        try {
            defaultDataStore.getResource(href, resourceData, (Options) null)
            fail("should have thrown due to empty options")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "The com.okta.sdk.impl.ds.DefaultDataStore implementation only functions with com.okta.sdk.impl.query.DefaultOptions instances.Object of class [null] must be an instance of class com.okta.sdk.impl.query.DefaultOptions")
        }
    }

    @Test
    void testApiKeyResolverReturnsCorrectApiKey() {
        def requestExecutor = createStrictMock(RequestExecutor)
        def apiKeyForCredentials = createStrictMock(ApiKey)
        def apiKeyForResolver = createStrictMock(ApiKey)

        def apiKeyCredentials = new ApiKeyCredentials(apiKeyForCredentials)
        def apiKeyResolver = new DefaultApiKeyResolver(apiKeyForResolver)

        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com/v1", apiKeyCredentials, apiKeyResolver)

        assertNotEquals(defaultDataStore.getApiKey(), apiKeyForCredentials)
        assertEquals(defaultDataStore.getApiKey(), apiKeyForResolver)

    }
}
