package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.ListProfileMappings;
import com.okta.sdk.resource.model.ProfileMapping;
import com.okta.sdk.resource.model.ProfileMappingRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class ProfileMappingApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ProfileMappingApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.ProfileMappingApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(new ArrayList<Pair>());
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

    /* getProfileMapping */
    @Test
    public void testGetProfileMapping_Success() throws Exception {
        ProfileMapping expected = new ProfileMapping();
        stubInvoke(expected);
        ProfileMapping actual = api.getProfileMapping("MID123");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/mappings/MID123"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetProfileMapping_WithHeaders() throws Exception {
        stubInvoke(new ProfileMapping());
        api.getProfileMapping("MID1", Collections.singletonMap("X-H","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("v", headers.getValue().get("X-H"));
    }

    @Test
    public void testGetProfileMapping_MissingId() {
        try {
            api.getProfileMapping(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().toLowerCase().contains("mappingid"));
        }
    }

    /* listProfileMappings */
    @Test
    public void testListProfileMappings_Success() throws Exception {
        List<ListProfileMappings> expected = new ArrayList<>();
        stubInvoke(expected);
        List<ListProfileMappings> actual = api.listProfileMappings("A1", 50, "SRC", "TGT");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/mappings"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        verify(apiClient).parameterToPair(eq("after"), eq("A1"));
        verify(apiClient).parameterToPair(eq("limit"), eq(50));
        verify(apiClient).parameterToPair(eq("sourceId"), eq("SRC"));
        verify(apiClient).parameterToPair(eq("targetId"), eq("TGT"));
    }

    @Test
    public void testListProfileMappings_WithHeaders() throws Exception {
        stubInvoke(new ArrayList<>());
        api.listProfileMappings(null, null, null, null, Collections.singletonMap("X-Q","1"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("1", headers.getValue().get("X-Q"));
    }

    /* updateProfileMapping */
    @Test
    public void testUpdateProfileMapping_Success() throws Exception {
        ProfileMapping expected = new ProfileMapping();
        stubInvoke(expected);
        ProfileMappingRequest body = new ProfileMappingRequest();
        ProfileMapping actual = api.updateProfileMapping("MID9", body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/mappings/MID9"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testUpdateProfileMapping_WithHeaders() throws Exception {
        stubInvoke(new ProfileMapping());
        api.updateProfileMapping("MIDX", new ProfileMappingRequest(), Collections.singletonMap("X-U","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("v", headers.getValue().get("X-U"));
    }

    @Test
    public void testUpdateProfileMapping_MissingMappingId() {
        try {
            api.updateProfileMapping(null, new ProfileMappingRequest());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    @Test
    public void testUpdateProfileMapping_MissingBody() {
        try {
            api.updateProfileMapping("MID10", null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_Update() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500, "fail"));
        try {
            api.updateProfileMapping("MID_ERR", new ProfileMappingRequest());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
            assertTrue(e.getMessage().contains("fail"));
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled() throws Exception {
        stubInvoke(new ProfileMapping());
        api.getProfileMapping("MIDHEADER");
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/mappings/MIDHEADER"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.ProfileMappingApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
