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
import com.okta.sdk.impl.resource.user.DefaultUser
import com.okta.sdk.impl.util.BaseUrlResolver
import com.okta.sdk.impl.util.DefaultBaseUrlResolver
import com.okta.sdk.resource.Resource
import com.okta.sdk.resource.session.Session
import com.okta.sdk.resource.session.SessionAuthenticationMethod
import com.okta.sdk.resource.session.SessionIdentityProvider
import com.okta.sdk.resource.session.SessionIdentityProviderType
import com.okta.sdk.resource.session.SessionStatus
import org.testng.annotations.Test

import java.time.Instant
import java.time.format.DateTimeFormatter

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

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

                Map data = dataFromJsonFile()
                final InternalDataStore dataStore = mock(InternalDataStore)
                DefaultSession session = new DefaultSession(dataStore, dataFromJsonFile())
                Map idpData = data.get("idp")
                def sessionIdp = new DefaultSessionIdentityProvider(dataStore, idpData)
                when(dataStore.instantiate(SessionIdentityProvider, idpData)).thenReturn(sessionIdp)
                when(dataStore.create((String) eq("/api/v1/sessions"), any(), isNull(), (Class) eq(Session.class))).thenReturn(session)
                return dataStore
            }
        }

        Session createdSession = client.createSession(new DefaultCreateSessionRequest(null, null)
                                                                .setSessionToken(MOCK_SESSION_ID))
        assertSession(createdSession)

        createdSession.delete()
        verify(client.dataStore).delete("/api/v1/sessions/${MOCK_SESSION_ID}", (Resource) createdSession)
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

                Map data = dataFromJsonFile()
                final InternalDataStore dataStore = mock(InternalDataStore)
                DefaultSession session = new DefaultSession(dataStore, data)
                Map idpData = data.get("idp")
                def sessionIdp = new DefaultSessionIdentityProvider(dataStore, idpData)
                when(dataStore.instantiate(SessionIdentityProvider, idpData)).thenReturn(sessionIdp)
                when(dataStore.getResource("/api/v1/sessions/${MOCK_SESSION_ID}", Session)).thenReturn(session)
                when(dataStore.create((String) eq("/api/v1/sessions/${MOCK_SESSION_ID}/lifecycle/refresh".toString()), any(), eq(session), (Class) eq(Session.class))).thenReturn(session)
                return dataStore
            }
        }

        Session session = client.getSession(MOCK_SESSION_ID)
        assertSession(session)

        assertThat session.refresh(), notNullValue()
        verify(client.dataStore).create((String) eq("/api/v1/sessions/${MOCK_SESSION_ID}/lifecycle/refresh".toString()), any(), eq(session), (Class) eq(Session.class))
    }

    @Test
    void deleteUserSessions() {

        InternalDataStore dataStore = mock(InternalDataStore)

        new DefaultUser(dataStore, [id: "test_user_id"]).endAllSessions()
        verify(dataStore).delete("/api/v1/users/test_user_id/sessions")
    }

    void assertSession(Session session) {

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

    private Date parseDate(String dateString) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME
        return Date.from(Instant.from(timeFormatter.parse(dateString)))
    }

    private Map dataFromJsonFile(String resourceFile = '/stubs/sessions.json') {

        return new JacksonMapMarshaller().unmarshal(
                this.getClass().getResource(resourceFile).openStream(),
                Collections.emptyMap())
    }
}