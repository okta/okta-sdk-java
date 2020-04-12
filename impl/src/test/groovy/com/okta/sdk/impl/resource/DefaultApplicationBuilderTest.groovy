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
