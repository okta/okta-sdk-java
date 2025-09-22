package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.AssignGroupOwnerRequestBody;
import com.okta.sdk.resource.model.GroupOwner;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class GroupOwnerApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.GroupOwnerApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.GroupOwnerApi(apiClient);

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

    /* assignGroupOwner */
    @Test
    public void testAssignGroupOwner_Success() throws Exception {
        GroupOwner expected = new GroupOwner();
        stubInvoke(expected);
        AssignGroupOwnerRequestBody body = new AssignGroupOwnerRequestBody();
        GroupOwner actual = api.assignGroupOwner("g1", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/g1/owners"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("g1");
    }

    @Test
    public void testAssignGroupOwner_WithHeaders() throws Exception {
        stubInvoke(new GroupOwner());
        Map<String,String> hdrs = Collections.singletonMap("X-A","1");
        api.assignGroupOwner("g2", new AssignGroupOwnerRequestBody(), hdrs);

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-A"));
    }

    @Test
    public void testAssignGroupOwner_MissingGroupId() {
        try {
            api.assignGroupOwner(null, new AssignGroupOwnerRequestBody());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("groupid"));
        }
    }

    @Test
    public void testAssignGroupOwner_MissingBody() {
        try {
            api.assignGroupOwner("g3", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("assigngroupownerrequestbody"));
        }
    }

    /* deleteGroupOwner */
    @Test
    public void testDeleteGroupOwner_Success() throws Exception {
        stubVoidInvoke();
        api.deleteGroupOwner("g4","o1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/g4/owners/o1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("g4");
        verify(apiClient).escapeString("o1");
    }

    @Test
    public void testDeleteGroupOwner_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteGroupOwner("g5","o2", Collections.singletonMap("X-D","v"));
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
    public void testDeleteGroupOwner_MissingGroupId() {
        try {
            api.deleteGroupOwner(null,"o3");
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("groupid"));
        }
    }

    @Test
    public void testDeleteGroupOwner_MissingOwnerId() {
        try {
            api.deleteGroupOwner("g6", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("ownerid"));
        }
    }

    /* listGroupOwners */
    @Test
    public void testListGroupOwners_Success_AllParams() throws Exception {
        List<GroupOwner> expected = Arrays.asList(new GroupOwner());
        stubInvoke(expected);
        List<GroupOwner> actual = api.listGroupOwners("g7","type eq \"USER\"","c1",500);
        assertSame(expected, actual);

        verify(apiClient).parameterToPair("search","type eq \"USER\"");
        verify(apiClient).parameterToPair("after","c1");
        verify(apiClient).parameterToPair("limit",500);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/g7/owners"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("g7");
    }

    @Test
    public void testListGroupOwners_Success_Minimal() throws Exception {
        List<GroupOwner> expected = Collections.emptyList();
        stubInvoke(expected);
        List<GroupOwner> actual = api.listGroupOwners("g8", null, null, null);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/g8/owners"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("g8");
    }

    @Test
    public void testListGroupOwners_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listGroupOwners("g9", null, null, null, Collections.singletonMap("X-L","1"));
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
    public void testListGroupOwners_MissingGroupId() {
        try {
            api.listGroupOwners(null, null, null, null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("groupid"));
        }
    }

    /* ApiException propagation */
    @Test
    public void testAssignGroupOwner_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"boom"));
        try {
            api.assignGroupOwner("gX", new AssignGroupOwnerRequestBody());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertTrue(ex.getMessage().contains("boom"));
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.GroupOwnerApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
