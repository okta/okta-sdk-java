package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.GroupPushMappingApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.CreateGroupPushMappingRequest;
import com.okta.sdk.resource.model.GroupPushMapping;
import com.okta.sdk.resource.model.GroupPushMappingStatus;
import com.okta.sdk.resource.model.UpdateGroupPushMappingRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class GroupPushMappingApiTest {

    private ApiClient apiClient;
    private GroupPushMappingApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new GroupPushMappingApi(apiClient);

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

    /* createGroupPushMapping */
    @Test
    public void testCreateGroupPushMapping_Success() throws Exception {
        GroupPushMapping expected = new GroupPushMapping();
        stubInvoke(expected);
        CreateGroupPushMappingRequest body = new CreateGroupPushMappingRequest();
        GroupPushMapping actual = api.createGroupPushMapping("app1", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/apps/app1/group-push/mappings"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("app1");
    }

    @Test
    public void testCreateGroupPushMapping_WithHeaders() throws Exception {
        stubInvoke(new GroupPushMapping());
        Map<String,String> hdrs = Collections.singletonMap("X-C","1");
        api.createGroupPushMapping("app2", new CreateGroupPushMappingRequest(), hdrs);

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-C"));
    }

    @Test
    public void testCreateGroupPushMapping_MissingAppId() {
        try {
            api.createGroupPushMapping(null, new CreateGroupPushMappingRequest());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    @Test
    public void testCreateGroupPushMapping_MissingBody() {
        try {
            api.createGroupPushMapping("appX", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* deleteGroupPushMapping */
    @Test
    public void testDeleteGroupPushMapping_Success() throws Exception {
        stubVoidInvoke();
        api.deleteGroupPushMapping("app3","m1", true);
        verify(apiClient).invokeAPI(
            eq("/api/v1/apps/app3/group-push/mappings/m1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).parameterToPair("deleteTargetGroup", true);
        verify(apiClient).escapeString("app3");
        verify(apiClient).escapeString("m1");
    }

    @Test
    public void testDeleteGroupPushMapping_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteGroupPushMapping("app4","m2", false, Collections.singletonMap("X-D","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeleteGroupPushMapping_MissingAppId() {
        try {
            api.deleteGroupPushMapping(null,"m", true);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    @Test
    public void testDeleteGroupPushMapping_MissingDeleteTargetGroup() {
        try {
            api.deleteGroupPushMapping("app","m", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* getGroupPushMapping */
    @Test
    public void testGetGroupPushMapping_Success() throws Exception {
        GroupPushMapping expected = new GroupPushMapping();
        stubInvoke(expected);
        GroupPushMapping actual = api.getGroupPushMapping("app5","m3");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/apps/app5/group-push/mappings/m3"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("app5");
        verify(apiClient).escapeString("m3");
    }

    @Test
    public void testGetGroupPushMapping_WithHeaders() throws Exception {
        stubInvoke(new GroupPushMapping());
        api.getGroupPushMapping("app6","m4", Collections.singletonMap("X-G","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-G"));
    }

    @Test
    public void testGetGroupPushMapping_MissingAppId() {
        try {
            api.getGroupPushMapping(null,"m");
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    @Test
    public void testGetGroupPushMapping_MissingMappingId() {
        try {
            api.getGroupPushMapping("app", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* listGroupPushMappings */
    @Test
    public void testListGroupPushMappings_Success_AllParams() throws Exception {
        List<GroupPushMapping> expected = Arrays.asList(new GroupPushMapping());
        stubInvoke(expected);
        List<GroupPushMapping> actual = api.listGroupPushMappings("app7","after1",50,"2025-09-01T00:00:00Z","src123", GroupPushMappingStatus.ACTIVE);
        assertSame(expected, actual);

        verify(apiClient).parameterToPair("after","after1");
        verify(apiClient).parameterToPair("limit",50);
        verify(apiClient).parameterToPair("lastUpdated","2025-09-01T00:00:00Z");
        verify(apiClient).parameterToPair("sourceGroupId","src123");
        verify(apiClient).parameterToPair("status", GroupPushMappingStatus.ACTIVE);
        verify(apiClient).invokeAPI(
            eq("/api/v1/apps/app7/group-push/mappings"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("app7");
    }

    @Test
    public void testListGroupPushMappings_Success_Minimal() throws Exception {
        stubInvoke(Collections.emptyList());
        List<GroupPushMapping> actual = api.listGroupPushMappings("app8", null, null, null, null, null);
        assertNotNull(actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/apps/app8/group-push/mappings"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("app8");
    }

    @Test
    public void testListGroupPushMappings_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listGroupPushMappings("app9", null, null, null, null, null, Collections.singletonMap("X-L","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-L"));
    }

    @Test
    public void testListGroupPushMappings_MissingAppId() {
        try {
            api.listGroupPushMappings(null, null, null, null, null, null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* updateGroupPushMapping */
    @Test
    public void testUpdateGroupPushMapping_Success() throws Exception {
        GroupPushMapping expected = new GroupPushMapping();
        stubInvoke(expected);
        UpdateGroupPushMappingRequest body = new UpdateGroupPushMappingRequest();
        GroupPushMapping actual = api.updateGroupPushMapping("app10","m10", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/apps/app10/group-push/mappings/m10"), eq("PATCH"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("app10");
        verify(apiClient).escapeString("m10");
    }

    @Test
    public void testUpdateGroupPushMapping_WithHeaders() throws Exception {
        stubInvoke(new GroupPushMapping());
        api.updateGroupPushMapping("app11","m11", new UpdateGroupPushMappingRequest(),
            Collections.singletonMap("X-U","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PATCH"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-U"));
    }

    @Test
    public void testUpdateGroupPushMapping_MissingAppId() {
        try {
            api.updateGroupPushMapping(null,"m", new UpdateGroupPushMappingRequest());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    @Test
    public void testUpdateGroupPushMapping_MissingMappingId() {
        try {
            api.updateGroupPushMapping("app", null, new UpdateGroupPushMappingRequest());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    @Test
    public void testUpdateGroupPushMapping_MissingBody() {
        try {
            api.updateGroupPushMapping("app","m", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* ApiException propagation */
    @Test
    public void testCreateGroupPushMapping_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"boom"));
        try {
            api.createGroupPushMapping("appX", new CreateGroupPushMappingRequest());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertTrue(ex.getMessage().contains("boom"));
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = GroupPushMappingApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
