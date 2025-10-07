package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.SsfSecurityEventTokenApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SsfSecurityEventTokenApiTest {

    private ApiClient apiClient;
    private SsfSecurityEventTokenApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new SsfSecurityEventTokenApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/secevent+jwt");
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

    /* publishSecurityEventTokens */
    @Test
    public void testPublishSecurityEventTokens_Success() throws Exception {
        stubVoidInvoke();
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        api.publishSecurityEventTokens(token);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/security/api/v1/security-events"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertSame(token, bodyCap.getValue());
    }

    @Test
    public void testPublishSecurityEventTokens_WithHeaders() throws Exception {
        stubVoidInvoke();
        String token = "some.jwt.token";
        api.publishSecurityEventTokens(token, Collections.singletonMap("X-Custom-Header", "value"));

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("value", cap.getValue().get("X-Custom-Header"));
    }

    @Test
    public void testPublishSecurityEventTokens_MissingToken() {
        try {
            api.publishSecurityEventTokens(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("securityEventToken"));
        }
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenThrow(new ApiException(401, "Unauthorized"));
        try {
            api.publishSecurityEventTokens("some.invalid.token");
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(401, ex.getCode());
            assertEquals("Unauthorized", ex.getMessage());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = SsfSecurityEventTokenApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}