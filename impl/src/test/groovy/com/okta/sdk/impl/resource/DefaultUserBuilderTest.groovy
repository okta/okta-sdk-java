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
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserNextLogin
import com.okta.sdk.resource.user.UserProfile
import org.testng.annotations.Test

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
}
