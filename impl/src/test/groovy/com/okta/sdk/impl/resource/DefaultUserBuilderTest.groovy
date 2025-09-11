/*
 * Copyright 2023-Present Okta, Inc.
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

import com.okta.sdk.resource.api.UserApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import org.mockito.ArgumentCaptor
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.mockito.ArgumentMatchers.*
import static org.mockito.Mockito.*
import static org.testng.Assert.*

class DefaultUserBuilderTest {

    UserApi userApi

    var userBuilder = new DefaultUserBuilder()

    @BeforeMethod
    void setUp() {
        userApi = mock(UserApi)
        when(userApi.createUser(any(CreateUserRequest), any(), any(), any())).thenReturn(new User())
    }

    private CreateUserRequest buildAndCapture(DefaultUserBuilder b) {
        ArgumentCaptor<CreateUserRequest> reqCap = ArgumentCaptor.forClass(CreateUserRequest)
        b.buildAndCreate(userApi)
        verify(userApi).createUser(reqCap.capture(), any(), any(), any())
        return reqCap.value
    }

    @Test
    void testLoginFallbackToEmail() {
        def b = new DefaultUserBuilder()
            .setEmail("john.doe@example.com")
        def req = buildAndCapture(b)
        assertEquals(req.profile.email, "john.doe@example.com")
        assertEquals(req.profile.login, "john.doe@example.com")
    }

    @Test
    void testExplicitLoginOverridesEmail() {
        def b = new DefaultUserBuilder()
            .setEmail("john.doe@example.com")
            .setLogin("customLogin")
        def req = buildAndCapture(b)
        assertEquals(req.profile.login, "customLogin")
    }

    @Test
    void testSetBcryptPasswordHashParsesComponents() {
        def b = new DefaultUserBuilder()
            .setEmail("user@example.com")
            .setBcryptPasswordHash("\$2a\$10\$Ro0CUfOqk6cXEKf3dyaM7OhSCvnwM9s4wIX9JeLapehKK5YdLxKcm")
        def req = buildAndCapture(b)
        def hash = req.credentials.password.hash
        assertEquals(hash.algorithm, PasswordCredentialHashAlgorithm.BCRYPT)
        assertEquals(hash.workFactor, 10)
        assertEquals(hash.salt, "Ro0CUfOqk6cXEKf3dyaM7O")
        assertEquals(hash.value, "hSCvnwM9s4wIX9JeLapehKK5YdLxKcm")
    }

    @Test
    void testBcryptPasswordHashDirectParams() {
        def b = new DefaultUserBuilder()
            .setEmail("u@example.com")
            .setBcryptPasswordHash("hashValue", "saltValue", 12)
        def req = buildAndCapture(b)
        def hash = req.credentials.password.hash
        assertEquals(hash.algorithm, PasswordCredentialHashAlgorithm.BCRYPT)
        assertEquals(hash.salt, "saltValue")
        assertEquals(hash.value, "hashValue")
        assertEquals(hash.workFactor, 12)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testPasswordAndHashConflict() {
        def b = new DefaultUserBuilder()
            .setEmail("u@example.com")
            .setPassword("Secret123!".toCharArray())
            .setBcryptPasswordHash("hashValue", "saltValue", 4)
        b.buildAndCreate(userApi)
    }

    @Test
    void testPlainPasswordIncluded() {
        char[] pw = "Password1!".toCharArray()
        def b = new DefaultUserBuilder()
            .setEmail("u@example.com")
            .setPassword(pw)
        def req = buildAndCapture(b)
        assertEquals(req.credentials.password.value, "Password1!")
        // mutate original to ensure copy
        pw[0] = 'X' as char
        assertEquals(req.credentials.password.value, "Password1!")
    }

    @Test
    void testSha256PasswordHash() {
        def b = new DefaultUserBuilder()
            .setEmail("u@example.com")
            .setSha256PasswordHash("val256", "salt256", "APPEND")
        def req = buildAndCapture(b)
        def hash = req.credentials.password.hash
        assertEquals(hash.algorithm, PasswordCredentialHashAlgorithm.SHA_256)
        assertEquals(hash.salt, "salt256")
        assertEquals(hash.value, "val256")
        assertEquals(hash.saltOrder, "APPEND")
    }

    @Test
    void testPasswordHookImport() {
        def b = new DefaultUserBuilder()
            .setEmail("u@example.com")
            .usePasswordHookForImport()
        def req = buildAndCapture(b)
        assertNotNull(req.credentials.password.hook)
        assertEquals(req.credentials.password.hook.type, "default")
    }

    @Test
    void testSecurityQuestion() {
        def b = new DefaultUserBuilder()
            .setEmail("u@example.com")
            .setSecurityQuestion("Pet name?")
            .setSecurityQuestionAnswer("Fluffy")
        def req = buildAndCapture(b)
        assertEquals(req.credentials.recoveryQuestion.question, "Pet name?")
        assertEquals(req.credentials.recoveryQuestion.answer, "Fluffy")
    }

    @Test
    void testProviderSetsFlag() {
        def provider = new AuthenticationProvider() // model class, presence only
        def b = new DefaultUserBuilder()
            .setEmail("u@example.com")
            .setProvider(provider)
        ArgumentCaptor<Boolean> secondFlag = ArgumentCaptor.forClass(Boolean)
        b.buildAndCreate(userApi)
        verify(userApi).createUser(any(CreateUserRequest), any(), secondFlag.capture(), any())
        assertTrue(secondFlag.value)
    }

    @Test
    void testGroups() {
        def b = new DefaultUserBuilder()
            .setEmail("u@example.com")
            .setGroups(new ArrayList<>(["g1"]))
            .addGroup("g2")
        def req = buildAndCapture(b)
        assertEquals(req.groupIds.size(), 2)
        assertTrue(req.groupIds.containsAll(["g1","g2"]))
    }

    @Test
    void testCustomProfileProperty() {
        def b = new DefaultUserBuilder()
            .setEmail("u@example.com")
            .setCustomProfileProperty("customK", 123)
        def req = buildAndCapture(b)
        assertEquals(req.profile.additionalProperties.get("customK"), 123)
    }

    @Test
    void testNextLogin() {
        def nl = UserNextLogin.values().length > 0 ? UserNextLogin.values()[0] : null
        def b = new DefaultUserBuilder()
            .setEmail("u@example.com")
            .setNextLogin(nl)
        ArgumentCaptor<UserNextLogin> nlCap = ArgumentCaptor.forClass(UserNextLogin)
        b.buildAndCreate(userApi)
        verify(userApi).createUser(any(CreateUserRequest), any(), any(), nlCap.capture())
        assertEquals(nlCap.value, nl)
    }

    @Test
    void testUserType() {
        def b = new DefaultUserBuilder()
            .setEmail("u@example.com")
            .setType("typeId123")
        def req = buildAndCapture(b)
        assertEquals(req.type.id, "typeId123")
    }

    @Test
    void testActiveFlagPassed() {
        def b = new DefaultUserBuilder()
            .setEmail("u@example.com")
            .setActive(true)
        ArgumentCaptor<Boolean> activeCap = ArgumentCaptor.forClass(Boolean)
        b.buildAndCreate(userApi)
        verify(userApi).createUser(any(CreateUserRequest), activeCap.capture(), any(), any())
        assertEquals(activeCap.value, Boolean.TRUE)
    }

    @Test
    void testMultipleProfileFields() {
        def b = new DefaultUserBuilder()
            .setEmail("u@example.com")
            .setFirstName("Jane")
            .setLastName("Doe")
            .setCity("Springfield")
            .setZipCode("12345")
        def req = buildAndCapture(b)
        assertEquals(req.profile.firstName, "Jane")
        assertEquals(req.profile.lastName, "Doe")
        assertEquals(req.profile.city, "Springfield")
        assertEquals(req.profile.zipCode, "12345")
    }

    @Test
    void testApiExceptionPropagates() {
        when(userApi.createUser(any(CreateUserRequest), any(), any(), any()))
            .thenThrow(new ApiException("failure"))
        def b = new DefaultUserBuilder()
            .setEmail("u@example.com")
        try {
            b.buildAndCreate(userApi)
            fail("Expected ApiException")
        } catch (ApiException ex) {
            assertTrue(ex.message.contains("failure"))
        }
    }

    @Test
    void testSetBcryptPasswordHash() {
        userBuilder.setBcryptPasswordHash("\$2a\$10\$Ro0CUfOqk6cXEKf3dyaM7OhSCvnwM9s4wIX9JeLapehKK5YdLxKcm")

        assertEquals(userBuilder.passwordHashProperties.size(), 4)
        assertEquals(userBuilder.passwordHashProperties.salt, "Ro0CUfOqk6cXEKf3dyaM7O")
        assertEquals(userBuilder.passwordHashProperties.workFactor, 10)
        assertEquals(userBuilder.passwordHashProperties.value, "hSCvnwM9s4wIX9JeLapehKK5YdLxKcm")
        assertEquals(userBuilder.passwordHashProperties.algorithm, "BCRYPT")
    }
}
