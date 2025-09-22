package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.OAuth2Grant;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class UserGrantApiTest {

    private ApiClient apiClient;
    private UserGrantApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new UserGrantApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(Collections.emptyList());
    }

    private <T> void stubInvoke(T value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubVoidInvoke() throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenReturn(null);
    }

    /* listGrantsForUser */
    @Test
    public void testListGrantsForUser_Success_AllParams() throws Exception {
        List<OAuth2Grant> expected = Arrays.asList(new OAuth2Grant());
        stubInvoke(expected);
        List<OAuth2Grant> actual = api.listGrantsForUser("user1", "scope1", "all", "cursor", 50);
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/users/user1/grants"), eq("GET"),
            anyList(), anyList(), anyString(), isNull(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));

        verify(apiClient).escapeString("user1");
        verify(apiClient).parameterToPair("scopeId", "scope1");
        verify(apiClient).parameterToPair("expand", "all");
        verify(apiClient).parameterToPair("after", "cursor");
        verify(apiClient).parameterToPair("limit", 50);
    }

    @Test
    public void testListGrantsForUser_MissingId() {
        try {
            api.listGrantsForUser(null, null, null, null, null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("userId"));
        }
    }

    /* revokeGrantForUser */
    @Test
    public void testRevokeGrantForUser_Success() throws Exception {
        stubVoidInvoke();
        api.revokeGrantForUser("user2", "grant1");

        verify(apiClient).invokeAPI(
            eq("/api/v1/users/user2/grants/grant1"), eq("DELETE"),
            anyList(), anyList(), anyString(), isNull(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), isNull());

        verify(apiClient).escapeString("user2");
        verify(apiClient).escapeString("grant1");
    }

    @Test
    public void testRevokeGrantForUser_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.revokeGrantForUser("user2.1", "grant1.1", Collections.singletonMap("X-Test", "true"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(), anyString(), isNull(), cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), isNull());
        assertEquals("true", cap.getValue().get("X-Test"));
    }

    /* revokeGrantsForUser */
    @Test
    public void testRevokeGrantsForUser_Success() throws Exception {
        stubVoidInvoke();
        api.revokeGrantsForUser("user3", "scope2");

        verify(apiClient).invokeAPI(
            eq("/api/v1/users/user3/grants"), eq("DELETE"),
            anyList(), anyList(), anyString(), isNull(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), isNull());

        verify(apiClient).escapeString("user3");
        verify(apiClient).parameterToPair("scopeId", "scope2");
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(anyString(), anyString(), anyList(), anyList(), anyString(), any(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class)))
            .thenThrow(new ApiException(404, "Not Found"));
        try {
            api.listGrantsForUser("non-existent-user", null, null, null, null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(404, ex.getCode());
            assertEquals("Not Found", ex.getMessage());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = UserGrantApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig().isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}