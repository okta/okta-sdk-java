package com.okta.sdk.impl.resource;

import com.okta.sdk.resource.api.ApplicationApi;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.Application;
import com.okta.sdk.resource.model.ApplicationAccessibility;
import com.okta.sdk.resource.model.ApplicationSignOnMode;
import com.okta.sdk.resource.model.ApplicationVisibility;
import com.okta.sdk.resource.model.ApplicationVisibilityHide;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

public class DefaultApplicationBuilderTest {

    private DefaultApplicationBuilder<?> builder;
    private ApplicationApi applicationApi;

    @BeforeMethod
    public void setUp() {
        builder = new DefaultApplicationBuilder<>();
        applicationApi = mock(ApplicationApi.class);
    }

    @Test
    public void testSetName() {
        DefaultApplicationBuilder<?> result = (DefaultApplicationBuilder<?>) builder.setName("test-name");
        assertEquals("test-name", builder.name);
        assertSame(builder, result);
    }

    @Test
    public void testSetLabel() {
        DefaultApplicationBuilder<?> result = (DefaultApplicationBuilder<?>) builder.setLabel("test-label");
        assertEquals("test-label", builder.label);
        assertSame(builder, result);
    }

    @Test
    public void testSetErrorRedirectUrl() {
        DefaultApplicationBuilder<?> result = (DefaultApplicationBuilder<?>) builder.setErrorRedirectUrl("https://error.example.com");
        assertEquals("https://error.example.com", builder.errorRedirectUrl);
        assertSame(builder, result);
    }

    @Test
    public void testSetLoginRedirectUrl() {
        DefaultApplicationBuilder<?> result = (DefaultApplicationBuilder<?>) builder.setLoginRedirectUrl("https://login.example.com");
        assertEquals("https://login.example.com", builder.loginRedirectUrl);
        assertSame(builder, result);
    }

    @Test
    public void testSetSelfService() {
        DefaultApplicationBuilder<?> result = (DefaultApplicationBuilder<?>) builder.setSelfService(true);
        assertTrue(builder.selfService);
        assertSame(builder, result);
    }

    @Test
    public void testSetSignOnMode() {
        ApplicationSignOnMode signOnMode = ApplicationSignOnMode.AUTO_LOGIN;
        DefaultApplicationBuilder<?> result = (DefaultApplicationBuilder<?>) builder.setSignOnMode(signOnMode);
        assertEquals(signOnMode, builder.signOnMode);
        assertSame(builder, result);
    }

    @Test
    public void testSetIOS() {
        DefaultApplicationBuilder<?> result = (DefaultApplicationBuilder<?>) builder.setIOS(true);
        assertTrue(builder.iOS);
        assertSame(builder, result);
    }

    @Test
    public void testSetWeb() {
        DefaultApplicationBuilder<?> result = (DefaultApplicationBuilder<?>) builder.setWeb(true);
        assertTrue(builder.web);
        assertSame(builder, result);
    }

    @Test
    public void testBuildAndCreate() throws ApiException {
        // Setup mock response
        Application mockApplication = new Application();
        when(applicationApi.createApplication(any(Application.class), eq(false), isNull())).thenReturn(mockApplication);

        // Configure the builder with all properties
        builder.setName("test-name")
            .setLabel("test-label")
            .setErrorRedirectUrl("https://error.example.com")
            .setLoginRedirectUrl("https://login.example.com")
            .setSelfService(true)
            .setSignOnMode(ApplicationSignOnMode.AUTO_LOGIN)
            .setIOS(true)
            .setWeb(false);

        // Call method under test
        Application result = builder.buildAndCreate(applicationApi);

        // Verify result
        assertSame(mockApplication, result);

        // Capture and verify the Application object passed to the API
        ArgumentCaptor<Application> appCaptor = ArgumentCaptor.forClass(Application.class);
        verify(applicationApi).createApplication(appCaptor.capture(), eq(false), isNull());

        Application capturedApp = appCaptor.getValue();
        assertEquals("test-label", capturedApp.getLabel());
        assertEquals(ApplicationSignOnMode.AUTO_LOGIN, capturedApp.getSignOnMode());

        // Verify accessibility settings
        ApplicationAccessibility accessibility = capturedApp.getAccessibility();
        assertNotNull(accessibility);
        assertEquals("https://login.example.com", accessibility.getLoginRedirectUrl());
        assertEquals("https://error.example.com", accessibility.getErrorRedirectUrl());
        assertTrue(accessibility.getSelfService());

        // Verify visibility settings
        ApplicationVisibility visibility = capturedApp.getVisibility();
        assertNotNull(visibility);
        ApplicationVisibilityHide hide = visibility.getHide();
        assertNotNull(hide);
        assertTrue(hide.getiOS());
        assertEquals(Boolean.FALSE, hide.getWeb());
    }

    @Test
    public void testBuildWithNullValues() throws ApiException {
        // Setup mock response
        Application mockApplication = new Application();
        when(applicationApi.createApplication(any(Application.class), eq(false), isNull())).thenReturn(mockApplication);

        // Call method with default values
        Application result = builder.buildAndCreate(applicationApi);

        // Verify basic structure
        ArgumentCaptor<Application> appCaptor = ArgumentCaptor.forClass(Application.class);
        verify(applicationApi).createApplication(appCaptor.capture(), eq(false), isNull());

        Application capturedApp = appCaptor.getValue();
        assertNotNull(capturedApp);
        assertNotNull(capturedApp.getAccessibility());
        assertNotNull(capturedApp.getVisibility());
        assertNotNull(capturedApp.getVisibility().getHide());
    }

    @Test(expectedExceptions = ApiException.class)
    public void testBuildAndCreateWithApiException() throws ApiException {
        when(applicationApi.createApplication(any(), eq(false), isNull())).thenThrow(new ApiException());
        builder.buildAndCreate(applicationApi);
    }
}
