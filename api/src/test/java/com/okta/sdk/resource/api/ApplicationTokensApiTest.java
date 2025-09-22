package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.OAuth2RefreshToken;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApplicationTokensApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ApplicationTokensApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.ApplicationTokensApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(inv -> {
            String name = inv.getArgument(0);
            Object value = inv.getArgument(1);
            if (value == null) return Collections.emptyList();
            return Collections.singletonList(new Pair(name, String.valueOf(value)));
        });
    }

    @Test
    public void testGetOAuth2TokenForApplication_Success() throws Exception {
        OAuth2RefreshToken expected = new OAuth2RefreshToken();
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class))
        ).thenReturn(expected);

        OAuth2RefreshToken actual = api.getOAuth2TokenForApplication("app123", "tok789", "scope");
        assertSame(expected, actual);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(
            pathCap.capture(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));

        String path = pathCap.getValue();
        assertTrue(path.contains("app123"));
        assertTrue(path.contains("tok789"));
        verify(apiClient).escapeString("app123");
        verify(apiClient).escapeString("tok789");
    }

    @Test(expected = ApiException.class)
    public void testGetOAuth2TokenForApplication_MissingAppId() throws Exception {
        api.getOAuth2TokenForApplication(null, "tok", null);
    }

    @Test(expected = ApiException.class)
    public void testGetOAuth2TokenForApplication_MissingTokenId() throws Exception {
        api.getOAuth2TokenForApplication("app", null, null);
    }

    @Test
    public void testListOAuth2TokensForApplication_WithAllQueryParams() throws Exception {
        List<OAuth2RefreshToken> expected = Arrays.asList(new OAuth2RefreshToken(), new OAuth2RefreshToken());
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class))
        ).thenReturn(expected);

        List<OAuth2RefreshToken> actual = api.listOAuth2TokensForApplication("appA", "scope", "cursor123", 50);
        assertEquals(expected, actual);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> queryListCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            queryListCap.capture(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));

        Map<String, String> qp = flattenPairs(queryListCap.getValue());
        assertEquals("scope", qp.get("expand"));
        assertEquals("cursor123", qp.get("after"));
        assertEquals("50", qp.get("limit"));
    }

    @Test
    public void testListOAuth2TokensForApplication_NullOptionalParams() throws Exception {
        List<OAuth2RefreshToken> expected = Collections.emptyList();
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class))
        ).thenReturn(expected);

        List<OAuth2RefreshToken> actual = api.listOAuth2TokensForApplication("appOnly", null, null, null);
        assertSame(expected, actual);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> queryListCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            queryListCap.capture(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));

        Map<String, String> qp = flattenPairs(queryListCap.getValue());
        assertFalse(qp.containsKey("expand"));
        assertFalse(qp.containsKey("after"));
        assertFalse(qp.containsKey("limit"));
    }

    @Test(expected = ApiException.class)
    public void testListOAuth2TokensForApplication_MissingAppId() throws Exception {
        api.listOAuth2TokensForApplication(null, null, null, null);
    }

    @Test
    public void testRevokeOAuth2TokenForApplication_Success() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull())
        ).thenReturn(null);

        api.revokeOAuth2TokenForApplication("app1", "tok1");

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> methodCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(
            pathCap.capture(), methodCap.capture(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());

        assertEquals("DELETE", methodCap.getValue());
        assertTrue(pathCap.getValue().contains("tok1"));
    }

    @Test(expected = ApiException.class)
    public void testRevokeOAuth2TokenForApplication_MissingAppId() throws Exception {
        api.revokeOAuth2TokenForApplication(null, "tok");
    }

    @Test(expected = ApiException.class)
    public void testRevokeOAuth2TokenForApplication_MissingTokenId() throws Exception {
        api.revokeOAuth2TokenForApplication("app", null);
    }

    @Test
    public void testRevokeOAuth2TokensForApplication_All() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull())
        ).thenReturn(null);

        api.revokeOAuth2TokensForApplication("appZ");

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(
            pathCap.capture(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());

        String path = pathCap.getValue();
        assertTrue(path.endsWith("/tokens"));
        assertFalse(path.matches(".*tokens/.+")); // no tokenId segment
    }

    @Test(expected = ApiException.class)
    public void testRevokeOAuth2TokensForApplication_MissingAppId() throws Exception {
        api.revokeOAuth2TokensForApplication(null);
    }

    private Map<String, String> flattenPairs(List<?> pairs) {
        if (pairs == null) return Collections.emptyMap();
        return (Map<String, String>) pairs.stream()
            .filter(Objects::nonNull)
            .map(p -> (Pair) p)
            .collect(Collectors.toMap(Pair::getName, Pair::getValue, (a, b) -> b));
    }
}
