package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.ErrorPage;
import com.okta.sdk.resource.model.SignInPage;
import com.okta.sdk.resource.model.HostedPage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CustomPagesApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.CustomPagesApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.CustomPagesApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    // replaceCustomizedSignInPage
    @Test
    public void testReplaceCustomizedSignInPage_Success() throws Exception {
        SignInPage body = new SignInPage();
        SignInPage expected = new SignInPage();
        stubInvoke(expected);

        SignInPage actual = api.replaceCustomizedSignInPage("b1", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b1/pages/sign-in/customized"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("b1");
    }

    @Test
    public void testReplaceCustomizedSignInPage_WithHeaders() throws Exception {
        stubInvoke(new SignInPage());
        api.replaceCustomizedSignInPage("b2", new SignInPage(), Collections.singletonMap("X-C","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdr.getValue().get("X-C"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceCustomizedSignInPage_MissingBrandId() throws Exception {
        api.replaceCustomizedSignInPage(null, new SignInPage());
    }

    @Test(expected = ApiException.class)
    public void testReplaceCustomizedSignInPage_MissingBody() throws Exception {
        api.replaceCustomizedSignInPage("b3", null);
    }

    // replacePreviewErrorPage
    @Test
    public void testReplacePreviewErrorPage_Success() throws Exception {
        ErrorPage body = new ErrorPage();
        ErrorPage expected = new ErrorPage();
        stubInvoke(expected);

        ErrorPage actual = api.replacePreviewErrorPage("b4", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b4/pages/error/preview"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("b4");
    }

    @Test
    public void testReplacePreviewErrorPage_WithHeaders() throws Exception {
        stubInvoke(new ErrorPage());
        api.replacePreviewErrorPage("b5", new ErrorPage(), Collections.singletonMap("X-P","p"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("p", hdr.getValue().get("X-P"));
    }

    @Test(expected = ApiException.class)
    public void testReplacePreviewErrorPage_MissingBrandId() throws Exception {
        api.replacePreviewErrorPage(null, new ErrorPage());
    }

    @Test(expected = ApiException.class)
    public void testReplacePreviewErrorPage_MissingBody() throws Exception {
        api.replacePreviewErrorPage("b6", null);
    }

    // replacePreviewSignInPage
    @Test
    public void testReplacePreviewSignInPage_Success() throws Exception {
        SignInPage body = new SignInPage();
        SignInPage expected = new SignInPage();
        stubInvoke(expected);

        SignInPage actual = api.replacePreviewSignInPage("b7", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b7/pages/sign-in/preview"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("b7");
    }

    @Test
    public void testReplacePreviewSignInPage_WithHeaders() throws Exception {
        stubInvoke(new SignInPage());
        api.replacePreviewSignInPage("b8", new SignInPage(), Collections.singletonMap("X-RP","v"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", hdr.getValue().get("X-RP"));
    }

    @Test(expected = ApiException.class)
    public void testReplacePreviewSignInPage_MissingBrandId() throws Exception {
        api.replacePreviewSignInPage(null, new SignInPage());
    }

    @Test(expected = ApiException.class)
    public void testReplacePreviewSignInPage_MissingBody() throws Exception {
        api.replacePreviewSignInPage("b9", null);
    }

    // replaceSignOutPageSettings
    @Test
    public void testReplaceSignOutPageSettings_Success() throws Exception {
        HostedPage body = new HostedPage();
        HostedPage expected = new HostedPage();
        stubInvoke(expected);

        HostedPage actual = api.replaceSignOutPageSettings("b10", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b10/pages/sign-out/customized"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("b10");
    }

    @Test
    public void testReplaceSignOutPageSettings_WithHeaders() throws Exception {
        stubInvoke(new HostedPage());
        api.replaceSignOutPageSettings("b11", new HostedPage(), Collections.singletonMap("X-SO","x"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("x", hdr.getValue().get("X-SO"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceSignOutPageSettings_MissingBrandId() throws Exception {
        api.replaceSignOutPageSettings(null, new HostedPage());
    }

    @Test(expected = ApiException.class)
    public void testReplaceSignOutPageSettings_MissingBody() throws Exception {
        api.replaceSignOutPageSettings("b12", null);
    }

    // Helpers
    private <T> void stubInvoke(T value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            any(List.class), any(List.class),
            anyString(), any(),
            any(Map.class), any(Map.class), any(Map.class),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class))
        ).thenReturn(value);
    }
}
