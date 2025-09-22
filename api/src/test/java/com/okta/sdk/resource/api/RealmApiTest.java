package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.CreateRealmRequest;
import com.okta.sdk.resource.model.Realm;
import com.okta.sdk.resource.model.UpdateRealmRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class RealmApiTest {

    private ApiClient apiClient;
    private RealmApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new RealmApi(apiClient);

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

    /* createRealm */
    @Test
    public void testCreateRealm_Success() throws Exception {
        Realm expected = new Realm();
        stubInvoke(expected);
        CreateRealmRequest body = new CreateRealmRequest();
        Realm actual = api.createRealm(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/realms"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateRealm_WithHeaders() throws Exception {
        stubInvoke(new Realm());
        Map<String,String> headers = Collections.singletonMap("X-Custom","v");
        api.createRealm(new CreateRealmRequest(), headers);
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

    @Test
    public void testCreateRealm_MissingBody() {
        try {
            api.createRealm(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().contains("body"));
        }
    }

    /* getRealm */
    @Test
    public void testGetRealm_Success() throws Exception {
        Realm expected = new Realm();
        stubInvoke(expected);
        Realm actual = api.getRealm("RID1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/realms/RID1"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetRealm_WithHeaders() throws Exception {
        stubInvoke(new Realm());
        api.getRealm("RID2", Collections.singletonMap("X-H","1"));
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("1", headerCap.getValue().get("X-H"));
    }

    @Test
    public void testGetRealm_MissingId() {
        try {
            api.getRealm(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().contains("realmId"));
        }
    }

    /* deleteRealm */
    @Test
    public void testDeleteRealm_Success() throws Exception {
        api.deleteRealm("RID3");
        verify(apiClient).invokeAPI(
            eq("/api/v1/realms/RID3"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testDeleteRealm_WithHeaders() throws Exception {
        api.deleteRealm("RID4", Collections.singletonMap("X-D","z"));
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("z", headerCap.getValue().get("X-D"));
    }

    @Test
    public void testDeleteRealm_MissingId() {
        try {
            api.deleteRealm(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().contains("realmId"));
        }
    }

    /* listRealms */
    @Test
    public void testListRealms_Success() throws Exception {
        List<Realm> expected = new ArrayList<>();
        stubInvoke(expected);
        List<Realm> actual = api.listRealms(50, "AFTER1", "profile.name co \"abc\"", "profile.name", "desc");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/realms"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        verify(apiClient).parameterToPair(eq("limit"), eq(50));
        verify(apiClient).parameterToPair(eq("after"), eq("AFTER1"));
        verify(apiClient).parameterToPair(eq("search"), eq("profile.name co \"abc\""));
        verify(apiClient).parameterToPair(eq("sortBy"), eq("profile.name"));
        verify(apiClient).parameterToPair(eq("sortOrder"), eq("desc"));
    }

    @Test
    public void testListRealms_WithHeaders() throws Exception {
        stubInvoke(new ArrayList<>());
        api.listRealms(null, null, null, null, null, Collections.singletonMap("X-L","v"));
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", headerCap.getValue().get("X-L"));
    }

    /* replaceRealm */
    @Test
    public void testReplaceRealm_Success() throws Exception {
        Realm expected = new Realm();
        stubInvoke(expected);
        UpdateRealmRequest body = new UpdateRealmRequest();
        Realm actual = api.replaceRealm("RID5", body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/realms/RID5"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceRealm_WithHeaders() throws Exception {
        stubInvoke(new Realm());
        api.replaceRealm("RID6", new UpdateRealmRequest(), Collections.singletonMap("X-R","1"));
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("1", headerCap.getValue().get("X-R"));
    }

    @Test
    public void testReplaceRealm_MissingArgs() {
        try { api.replaceRealm(null, new UpdateRealmRequest()); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); assertTrue(e.getMessage().contains("realmId")); }
        try { api.replaceRealm("RID7", null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); assertTrue(e.getMessage().contains("body")); }
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_Create() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"downstream"));
        try {
            api.createRealm(new CreateRealmRequest());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("downstream"));
        }
    }

    @Test
    public void testApiExceptionPropagates_Get() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"err"));
        try {
            api.getRealm("RID_ERR");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
            assertTrue(e.getMessage().contains("err"));
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_GetRealm() throws Exception {
        stubInvoke(new Realm());
        api.getRealm("RID8");
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/realms/RID8"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_Create() throws Exception {
        stubInvoke(new Realm());
        api.createRealm(new CreateRealmRequest());
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/realms"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = RealmApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
