/*
 * Copyright 2020-Present Okta, Inc.
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
import com.okta.sdk.resource.application.*
import org.testng.annotations.Test

import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class DefaultApplicationBuilderTest {
    @Test
    void basicUsage() {
        def client = mock(Client)
        def application = mock(Application)
        def applicationAccessibility = mock(ApplicationAccessibility)
        def applicationVisibilityHide = mock(ApplicationVisibilityHide)
        def applicationVisibility = mock(ApplicationVisibility)

        when(client.instantiate(Application.class)).thenReturn(application);
        when(client.instantiate(ApplicationAccessibility.class)).thenReturn(applicationAccessibility)
        when(client.instantiate(ApplicationVisibilityHide.class)).thenReturn(applicationVisibilityHide)
        when(application.getAccessibility()).thenReturn(applicationAccessibility)
        when(client.instantiate(ApplicationVisibility.class)).thenReturn(applicationVisibility)
        when(application.getVisibility())thenReturn(applicationVisibility)

        new DefaultApplicationBuilder()
            .setLabel("test_app")
            .setLoginRedirectUrl("http://www.myApp.com")
            .setErrorRedirectUrl("http://www.myApp.com/error")
            .setSelfService(false)
            .setSignOnMode(ApplicationSignOnMode.AUTO_LOGIN)
            .setIOS(false)
            .setWeb(true)
            .buildAndCreate(client);

        verify(client).createApplication(eq(application))
        verify(application).setLabel("test_app")
        verify(application).setSignOnMode(ApplicationSignOnMode.AUTO_LOGIN)
        verify(applicationAccessibility).setSelfService(false)
        verify(applicationAccessibility).setErrorRedirectUrl("http://www.myApp.com/error")
        verify(applicationAccessibility).setLoginRedirectUrl("http://www.myApp.com")
        verify(applicationVisibilityHide).setWeb(true)
        verify(applicationVisibilityHide).setIOS(false)
    }
}
