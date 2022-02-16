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
package com.okta.sdk.impl.resource.session

import com.okta.commons.http.RequestExecutor
import com.okta.commons.http.config.BaseUrlResolver
import com.okta.sdk.authc.credentials.TokenClientCredentials
import com.okta.sdk.cache.CacheManager
import com.okta.sdk.client.AuthenticationScheme
import com.okta.sdk.client.Client
import com.okta.sdk.impl.api.ClientCredentialsResolver
import com.okta.sdk.impl.api.DefaultClientCredentialsResolver
import com.okta.sdk.impl.cache.DisabledCacheManager
import com.okta.sdk.impl.client.DefaultClient
import com.okta.sdk.impl.config.ClientConfiguration
import com.okta.sdk.impl.ds.InternalDataStore
import com.okta.sdk.impl.ds.JacksonMapMarshaller
import com.okta.sdk.impl.resource.DefaultCreateSessionRequest
import com.okta.sdk.impl.resource.DefaultSession
import com.okta.sdk.impl.resource.DefaultSessionIdentityProvider
import com.okta.sdk.impl.util.DefaultBaseUrlResolver
import com.okta.sdk.resource.*
import org.testng.annotations.Test

import java.time.Instant
import java.time.format.DateTimeFormatter

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.mockito.ArgumentMatchers.*
import static org.mockito.Mockito.*

/**
 * Tests for the Sessions API.
 *
 * @since 0.10.0
 */
class SessionsTest {

    private static String MOCK_SESSION_ID = "101W_juydrDRByB7fUdRyE2JQ"

    @Test
    void simpleCreateSessionTest() {

        ClientConfiguration clientConfig = new ClientConfiguration()
        clientConfig.setBaseUrlResolver(new DefaultBaseUrlResolver("https://example.com"))
        clientConfig.setAuthenticationScheme(AuthenticationScheme.SSWS)
        clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(new TokenClientCredentials("foobar")))

        Client client = new DefaultClient(clientConfig, new DisabledCacheManager()) {
            protected InternalDataStore createDataStore(RequestExecutor requestExecutor,
                                                        BaseUrlResolver baseUrlResolver,
                                                        ClientCredentialsResolver clientCredentialsResolver,
                                                        CacheManager cacheManager) {

                Map data = dataFromJsonFile()
                final InternalDataStore dataStore = mock(InternalDataStore)
                DefaultSession session = new DefaultSession(dataStore, dataFromJsonFile())
                Map idpData = data.get("idp") as Map
                def sessionIdp = new DefaultSessionIdentityProvider(dataStore, idpData)
                when(dataStore.instantiate(SessionIdentityProvider, idpData)).thenReturn(sessionIdp)
                when(dataStore.create(
                    (String) eq("/api/v1/sessions"),
                    any(),
                    isNull(),
                    (Class) eq(Session.class),
                    eq(Collections.emptyMap()),
                    eq(Collections.emptyMap())))
                    .thenReturn(session)
                return dataStore
            }
        }

        Session createdSession = client.createSession(new DefaultCreateSessionRequest(null, null)
                                                                .setSessionToken(MOCK_SESSION_ID))
        assertSession(createdSession)

        client.endSession(createdSession.getId())
        verify(client.dataStore).delete("/api/v1/sessions/${MOCK_SESSION_ID}", Collections.emptyMap(), Collections.emptyMap())
    }

    @Test
    void simpleGetSessionTest() {

        ClientConfiguration clientConfig = new ClientConfiguration()
        clientConfig.setBaseUrlResolver(new DefaultBaseUrlResolver("https://example.com"))
        clientConfig.setAuthenticationScheme(AuthenticationScheme.SSWS)
        clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(new TokenClientCredentials("foobar")))

        Client client = new DefaultClient(clientConfig, new DisabledCacheManager()) {
            protected InternalDataStore createDataStore(RequestExecutor requestExecutor,
                                                        BaseUrlResolver baseUrlResolver,
                                                        ClientCredentialsResolver clientCredentialsResolver,
                                                        CacheManager cacheManager) {

                Map data = dataFromJsonFile()
                final InternalDataStore dataStore = mock(InternalDataStore)
                DefaultSession session = new DefaultSession(dataStore, data)
                Map idpData = data.get("idp") as Map
                def sessionIdp = new DefaultSessionIdentityProvider(dataStore, idpData)
                when(dataStore.instantiate(SessionIdentityProvider, idpData)).thenReturn(sessionIdp)
                when(dataStore.getResource("/api/v1/sessions/${MOCK_SESSION_ID}", Session, Collections.emptyMap(), Collections.emptyMap())).thenReturn(session)
                when(dataStore.create(
                        (String) eq("/api/v1/sessions/${MOCK_SESSION_ID}/lifecycle/refresh".toString()),
                        any(),
                        any(),
                        (Class) eq(Session.class),
                        eq(Collections.emptyMap()),
                        eq(Collections.emptyMap())))
                    .thenReturn(session)
                return dataStore
            }
        }

        Session session = client.getSession(MOCK_SESSION_ID)
        assertSession(session)

        assertThat client.refreshSession(session.getId()), notNullValue()
        verify(client.dataStore)
            .create(
                (String) eq("/api/v1/sessions/${MOCK_SESSION_ID}/lifecycle/refresh".toString()),
                any(),
                eq(null) as Resource,
                (Class) eq(Session.class),
                eq(Collections.emptyMap()),
                eq(Collections.emptyMap()))
    }

    @Test
    void deleteUserSessions() {

        ClientConfiguration clientConfig = new ClientConfiguration()
        clientConfig.setBaseUrlResolver(new DefaultBaseUrlResolver("https://example.com"))
        clientConfig.setAuthenticationScheme(AuthenticationScheme.SSWS)
        clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(new TokenClientCredentials("foobar")))

        Client client = new DefaultClient(clientConfig, new DisabledCacheManager()) {
            protected InternalDataStore createDataStore(RequestExecutor requestExecutor,
                                                        BaseUrlResolver baseUrlResolver,
                                                        ClientCredentialsResolver clientCredentialsResolver,
                                                        CacheManager cacheManager) {

                Map data = dataFromJsonFile()
                final InternalDataStore dataStore = mock(InternalDataStore)
                DefaultSession session = new DefaultSession(dataStore, data)
                Map idpData = data.get("idp") as Map
                def sessionIdp = new DefaultSessionIdentityProvider(dataStore, idpData)
                when(dataStore.instantiate(SessionIdentityProvider, idpData)).thenReturn(sessionIdp)
                when(dataStore.getResource("/api/v1/sessions/${MOCK_SESSION_ID}", Session, Collections.emptyMap(), Collections.emptyMap())).thenReturn(session)
                when(dataStore.create(
                    (String) eq("/api/v1/sessions/${MOCK_SESSION_ID}/lifecycle/refresh".toString()),
                    any(),
                    eq(session) as Resource,
                    (Class) eq(Session.class),
                    eq(Collections.emptyMap()),
                    eq(Collections.emptyMap())))
                    .thenReturn(session)
                return dataStore
            }
        }

        client.clearUserSessions("test_user_id")
        verify(client.dataStore).delete("/api/v1/users/test_user_id/sessions", Collections.emptyMap(),  Collections.emptyMap())
    }

    private static void assertSession(Session session) {

        assertThat session.getId(), equalTo(MOCK_SESSION_ID)
        assertThat session.getLogin(), equalTo("joe.coder@example.com")
        assertThat session.getUserId(), equalTo("00ubgaSARVOQDIOXMORI")
        assertThat session.getCreatedAt(), equalTo(parseDate("2015-08-30T18:41:35.818Z"))
        assertThat session.getExpiresAt(), equalTo(parseDate("2015-08-30T18:41:35.818Z"))
        assertThat session.getStatus(), equalTo(SessionStatus.ACTIVE)
        assertThat session.getLastPasswordVerification(), equalTo(parseDate("2015-08-30T18:41:35.818Z"))
        assertThat session.getLastFactorVerification(), equalTo(parseDate("2015-08-30T18:41:35.818Z"))
        assertThat session.getAmr(), equalTo([
                                    SessionAuthenticationMethod.PWD,
                                    SessionAuthenticationMethod.OTP,
                                    SessionAuthenticationMethod.MFA
                                ])
        assertThat session.getIdp(), notNullValue()
        assertThat session.getIdp().getId(), equalTo("00oi5cpnylv792IcF0g3")
        assertThat session.getIdp().getType(), equalTo(SessionIdentityProviderType.OKTA)
    }

    private static Date parseDate(String dateString) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME
        return Date.from(Instant.from(timeFormatter.parse(dateString)))
    }

    private Map dataFromJsonFile(String resourceFile = '/stubs/sessions.json') {

        return new JacksonMapMarshaller().unmarshal(
                this.getClass().getResource(resourceFile).openStream(),
            Collections.emptyMap())
    }
}