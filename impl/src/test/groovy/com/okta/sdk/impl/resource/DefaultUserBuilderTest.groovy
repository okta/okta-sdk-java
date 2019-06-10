/*
 * Copyright 2018-Present Okta, Inc.
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

import com.okta.sdk.client.Client
import com.okta.sdk.resource.user.PasswordCredential
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserCredentials
import com.okta.sdk.resource.user.UserNextLogin
import com.okta.sdk.resource.user.UserProfile
import org.mockito.ArgumentCaptor
import org.testng.annotations.Test

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

import static org.hamcrest.Matchers.aMapWithSize
import static org.hamcrest.Matchers.allOf
import static org.hamcrest.Matchers.hasEntry
import static org.hamcrest.MatcherAssert.assertThat
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

class DefaultUserBuilderTest {

    @Test
    void basicUsage() {

        def client = mock(Client)
        def user = mock(User)
        def profile = mock(UserProfile)
        when(client.instantiate(User)).thenReturn(user)
        when(client.instantiate(UserProfile)).thenReturn(profile)
        when(user.getProfile()).thenReturn(profile)

        new DefaultUserBuilder()
            .setFirstName("Joe")
            .setLastName("Coder")
            .setEmail("joe.coder@example.com")
            .setNextLogin(UserNextLogin.CHANGEPASSWORD)
            .buildAndCreate(client)

        verify(client).createUser(eq(user), eq(null), eq(null), eq(UserNextLogin.CHANGEPASSWORD))
        verify(profile).setFirstName("Joe")
        verify(profile).setLastName("Coder")
        verify(profile).setEmail("joe.coder@example.com")
    }

    @Test
    void importPasswordSha256() {
        def client = mock(Client)
        def user = mock(User)
        def profile = mock(UserProfile)
        def passwordCredential = mock(PasswordCredential)
        def userCredentials = mock(UserCredentials)
        when(client.instantiate(User)).thenReturn(user)
        when(client.instantiate(UserProfile)).thenReturn(profile)
        when(client.instantiate(UserCredentials)).thenReturn(userCredentials)
        when(client.instantiate(PasswordCredential)).thenReturn(passwordCredential)
        when(user.getProfile()).thenReturn(profile)
        when(user.getCredentials()).thenReturn(userCredentials)

        String salt = "some-salt"
        String hashedPassword = "a-hashed-password"

        new DefaultUserBuilder()
            .setFirstName("Joe")
            .setLastName("Coder")
            .setEmail("joe.coder@example.com")
            .setSha256PasswordHash(hashedPassword, salt, "PREFIX")
            .buildAndCreate(client)

        def hashCapture = ArgumentCaptor.forClass(Map.class)

        verify(userCredentials).setPassword(passwordCredential)
        verify(passwordCredential).put(eq("hash"), hashCapture.capture())

        assertThat hashCapture.value, allOf(
                    hasEntry("salt", salt),
                    hasEntry("saltOrder", "PREFIX"),
                    hasEntry("value", hashedPassword),
                    hasEntry("algorithm", "SHA-256"),
                    aMapWithSize(4))
    }

    @Test
    void importPasswordSha512() {
        def client = mock(Client)
        def user = mock(User)
        def profile = mock(UserProfile)
        def passwordCredential = mock(PasswordCredential)
        def userCredentials = mock(UserCredentials)
        when(client.instantiate(User)).thenReturn(user)
        when(client.instantiate(UserProfile)).thenReturn(profile)
        when(client.instantiate(UserCredentials)).thenReturn(userCredentials)
        when(client.instantiate(PasswordCredential)).thenReturn(passwordCredential)
        when(user.getProfile()).thenReturn(profile)
        when(user.getCredentials()).thenReturn(userCredentials)

        String salt = "some-salt"
        String hashedPassword = "a-hashed-password"

        new DefaultUserBuilder()
            .setFirstName("Joe")
            .setLastName("Coder")
            .setEmail("joe.coder@example.com")
            .setSha512PasswordHash(hashedPassword, salt, "PREFIX")
            .buildAndCreate(client)

        def hashCapture = ArgumentCaptor.forClass(Map.class)

        verify(userCredentials).setPassword(passwordCredential)
        verify(passwordCredential).put(eq("hash"), hashCapture.capture())

        assertThat hashCapture.value, allOf(
                    hasEntry("salt", salt),
                    hasEntry("saltOrder", "PREFIX"),
                    hasEntry("value", hashedPassword),
                    hasEntry("algorithm", "SHA-512"),
                    aMapWithSize(4))
    }

    @Test
    void importPasswordBcrypt() {
        def client = mock(Client)
        def user = mock(User)
        def profile = mock(UserProfile)
        def passwordCredential = mock(PasswordCredential)
        def userCredentials = mock(UserCredentials)
        when(client.instantiate(User)).thenReturn(user)
        when(client.instantiate(UserProfile)).thenReturn(profile)
        when(client.instantiate(UserCredentials)).thenReturn(userCredentials)
        when(client.instantiate(PasswordCredential)).thenReturn(passwordCredential)
        when(user.getProfile()).thenReturn(profile)
        when(user.getCredentials()).thenReturn(userCredentials)

        String salt = "some-salt"
        String hashedPassword = "a-hashed-password"

        new DefaultUserBuilder()
            .setFirstName("Joe")
            .setLastName("Coder")
            .setEmail("joe.coder@example.com")
            .setBcryptPasswordHash(hashedPassword, salt, 10)
            .buildAndCreate(client)

        def hashCapture = ArgumentCaptor.forClass(Map.class)

        verify(userCredentials).setPassword(passwordCredential)
        verify(passwordCredential).put(eq("hash"), hashCapture.capture())

        assertThat hashCapture.value, allOf(
                    hasEntry("salt", salt),
                    hasEntry("workFactor", 10),
                    hasEntry("value", hashedPassword),
                    hasEntry("algorithm", "BCRYPT"),
                    aMapWithSize(4))
    }
}
