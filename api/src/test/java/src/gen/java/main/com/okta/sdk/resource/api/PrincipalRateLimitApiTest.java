package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.PrincipalRateLimitEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class PrincipalRateLimitApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.PrincipalRateLimitApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.PrincipalRateLimitApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        // Prevent NPE for query param building
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(new ArrayList<>());
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

    /* createPrincipalRateLimitEntity */
    @Test
    public void testCreatePrincipalRateLimitEntity_Success() throws Exception {
        PrincipalRateLimitEntity expected = new PrincipalRateLimitEntity();
        stubInvoke(expected);
        PrincipalRateLimitEntity body = new PrincipalRateLimitEntity();
        PrincipalRateLimitEntity actual = api.createPrincipalRateLimitEntity(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/principal-rate-limits"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreatePrincipalRateLimitEntity_MissingEntity() {
        try {
            api.createPrincipalRateLimitEntity(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().toLowerCase().contains("entity"));
        }
    }

    @Test
    public void testCreatePrincipalRateLimitEntity_WithHeaders() throws Exception {
        PrincipalRateLimitEntity expected = new PrincipalRateLimitEntity();
        stubInvoke(expected);
        Map<String,String> headers = Collections.singletonMap("X-Custom","v");
        api.createPrincipalRateLimitEntity(new PrincipalRateLimitEntity(), headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", headerCap.getValue().get("X-Custom"));
    }

    /* getPrincipalRateLimitEntity */
    @Test
    public void testGetPrincipalRateLimitEntity_Success() throws Exception {
        PrincipalRateLimitEntity expected = new PrincipalRateLimitEntity();
        stubInvoke(expected);
        PrincipalRateLimitEntity actual = api.getPrincipalRateLimitEntity("PRL123");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/principal-rate-limits/PRL123"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetPrincipalRateLimitEntity_MissingId() {
        try {
            api.getPrincipalRateLimitEntity(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().toLowerCase().contains("principalratelimitid"));
        }
    }

    /* listPrincipalRateLimitEntities */
    @Test
    public void testListPrincipalRateLimitEntities_Success() throws Exception {
        List<PrincipalRateLimitEntity> expected = new ArrayList<>();
        stubInvoke(expected);
        List<PrincipalRateLimitEntity> actual = api.listPrincipalRateLimitEntities("principalType eq \"SSWS_TOKEN\"", null, null);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/principal-rate-limits"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testListPrincipalRateLimitEntities_MissingFilter() {
        try {
            api.listPrincipalRateLimitEntities(null, null, null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().toLowerCase().contains("filter"));
        }
    }

    /* replacePrincipalRateLimitEntity */
    @Test
    public void testReplacePrincipalRateLimitEntity_Success() throws Exception {
        PrincipalRateLimitEntity expected = new PrincipalRateLimitEntity();
        stubInvoke(expected);
        PrincipalRateLimitEntity body = new PrincipalRateLimitEntity();
        PrincipalRateLimitEntity actual = api.replacePrincipalRateLimitEntity("RID1", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/principal-rate-limits/RID1"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplacePrincipalRateLimitEntity_MissingArgs() {
        try { api.replacePrincipalRateLimitEntity(null, new PrincipalRateLimitEntity()); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.replacePrincipalRateLimitEntity("RID", null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_FromCreate() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502, "downstream"));
        try {
            api.createPrincipalRateLimitEntity(new PrincipalRateLimitEntity());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("downstream"));
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalledWithPath_Get() throws Exception {
        stubInvoke(new PrincipalRateLimitEntity());
        api.getPrincipalRateLimitEntity("XYZ");
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/principal-rate-limits/XYZ"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.PrincipalRateLimitApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
