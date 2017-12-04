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
package com.okta.sdk.impl.resource

import com.okta.sdk.authc.credentials.TokenClientCredentials
import com.okta.sdk.cache.CacheManager
import com.okta.sdk.client.Client
import com.okta.sdk.impl.api.ClientCredentialsResolver
import com.okta.sdk.impl.api.DefaultClientCredentialsResolver
import com.okta.sdk.impl.cache.DisabledCacheManager
import com.okta.sdk.impl.client.DefaultClient
import com.okta.sdk.impl.config.ClientConfiguration
import com.okta.sdk.impl.ds.InternalDataStore
import com.okta.sdk.impl.ds.JacksonMapMarshaller
import com.okta.sdk.impl.http.RequestExecutor
import com.okta.sdk.impl.resource.session.DefaultCreateSessionRequest
import com.okta.sdk.impl.resource.session.DefaultSession
import com.okta.sdk.impl.resource.user.DefaultUser
import com.okta.sdk.impl.util.BaseUrlResolver
import com.okta.sdk.impl.util.DefaultBaseUrlResolver
import com.okta.sdk.resource.session.Session
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

import static org.mockito.Mockito.*

/**
 * Tests for the Sessions API.
 *
 * @since 0.10.0
 */
class SessionsTest {

    @Test
    void simpleCreateSessionTest() {

        ClientConfiguration clientConfig = new ClientConfiguration()
        Client client = new DefaultClient(
                new DefaultClientCredentialsResolver(new TokenClientCredentials("foobar")),
                new DefaultBaseUrlResolver("https://example.com"),
                null,
                new DisabledCacheManager(),
                clientConfig.getAuthenticationScheme(),
                clientConfig.getRequestAuthenticatorFactory(),
                clientConfig.getConnectionTimeout()) {
            protected InternalDataStore createDataStore(RequestExecutor requestExecutor,
                                                        BaseUrlResolver baseUrlResolver,
                                                        ClientCredentialsResolver clientCredentialsResolver,
                                                        CacheManager cacheManager) {
                JacksonMapMarshaller mapMarshaller = new JacksonMapMarshaller()
                Map data = mapMarshaller.unmarshal(this.getClass().getResource( '/stubs/sessions.json' ).text)

                final InternalDataStore dataStore = mock(InternalDataStore)
                DefaultSession session = new DefaultSession(dataStore, data)
                when(dataStore.create((String) eq("/api/v1/sessions"), any(), (Class) eq(Session.class))).thenReturn(session)
                return dataStore
            }
        }

        Session createdSession = client.createSession(new DefaultCreateSessionRequest(null, null)
                                                                .setSessionToken("101W_juydrDRByB7fUdRyE2JQ"))

        assertThat createdSession.id, equalTo("101W_juydrDRByB7fUdRyE2JQ")

        createdSession.delete()
        verify(client.dataStore).delete("/api/v1/sessions/101W_juydrDRByB7fUdRyE2JQ")
    }

    @Test
    void simpleGetSessionTest() {

        ClientConfiguration clientConfig = new ClientConfiguration()
        Client client = new DefaultClient(
                new DefaultClientCredentialsResolver(new TokenClientCredentials("foobar")),
                new DefaultBaseUrlResolver("https://example.com"),
                null,
                new DisabledCacheManager(),
                clientConfig.getAuthenticationScheme(),
                clientConfig.getRequestAuthenticatorFactory(),
                clientConfig.getConnectionTimeout()) {
            protected InternalDataStore createDataStore(RequestExecutor requestExecutor,
                                                        BaseUrlResolver baseUrlResolver,
                                                        ClientCredentialsResolver clientCredentialsResolver,
                                                        CacheManager cacheManager) {
                JacksonMapMarshaller mapMarshaller = new JacksonMapMarshaller()
                Map data = mapMarshaller.unmarshal(this.getClass().getResource( '/stubs/sessions.json' ).text)

                final InternalDataStore dataStore = mock(InternalDataStore)
                DefaultSession session = new DefaultSession(dataStore, data)
                when(dataStore.getResource("/api/v1/sessions/101W_juydrDRByB7fUdRyE2JQ", Session)).thenReturn(session)
                when(dataStore.create((String) eq("/api/v1/sessions/101W_juydrDRByB7fUdRyE2JQ/lifecycle/refresh"), any(), (Class) eq(Session.class))).thenReturn(session)
                return dataStore
            }
        }

        Session session = client.getSession("101W_juydrDRByB7fUdRyE2JQ")
        assertThat session.id, equalTo("101W_juydrDRByB7fUdRyE2JQ")

        assertThat session.refresh(), notNullValue()
        verify(client.dataStore).create((String) eq("/api/v1/sessions/101W_juydrDRByB7fUdRyE2JQ/lifecycle/refresh"), any(), (Class) eq(Session.class))
    }

    @Test
    void deleteUserSessions() {

        InternalDataStore dataStore = mock(InternalDataStore)

        new DefaultUser(dataStore, [id: "test_user_id"]).endAllSessions()
        verify(dataStore).delete("/api/v1/users/test_user_id/sessions")
    }
}