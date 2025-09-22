package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.AddGroupRequest;
import com.okta.sdk.resource.model.Application;
import com.okta.sdk.resource.model.Group;
import com.okta.sdk.resource.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class GroupApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.GroupApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.GroupApi(apiClient);

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

    /* addGroup */
    @Test
    public void testAddGroup_Success() throws Exception {
        Group expected = new Group();
        stubInvoke(expected);
        AddGroupRequest body = new AddGroupRequest();
        Group actual = api.addGroup(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testAddGroup_WithHeaders() throws Exception {
        stubInvoke(new Group());
        Map<String,String> hdrs = Collections.singletonMap("X-C","v");
        api.addGroup(new AddGroupRequest(), hdrs);

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-C"));
    }

    @Test
    public void testAddGroup_MissingBody() {
        try {
            api.addGroup(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("group"));
        }
    }

    /* replaceGroup */
    @Test
    public void testReplaceGroup_Success() throws Exception {
        Group expected = new Group();
        stubInvoke(expected);
        AddGroupRequest body = new AddGroupRequest();
        Group actual = api.replaceGroup("g1", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/g1"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("g1");
    }

    @Test
    public void testReplaceGroup_WithHeaders() throws Exception {
        stubInvoke(new Group());
        api.replaceGroup("g2", new AddGroupRequest(), Collections.singletonMap("X-R","1"));

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-R"));
    }

    @Test
    public void testReplaceGroup_MissingGroupId() {
        try {
            api.replaceGroup(null, new AddGroupRequest());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("groupId"));
        }
    }

    @Test
    public void testReplaceGroup_MissingBody() {
        try {
            api.replaceGroup("g3", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("group"));
        }
    }

    /* getGroup */
    @Test
    public void testGetGroup_Success() throws Exception {
        Group expected = new Group();
        stubInvoke(expected);
        Group actual = api.getGroup("g4");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/g4"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("g4");
    }

    @Test
    public void testGetGroup_MissingId() {
        try {
            api.getGroup(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("groupId"));
        }
    }

    /* deleteGroup */
    @Test
    public void testDeleteGroup_Success() throws Exception {
        stubVoidInvoke();
        api.deleteGroup("g5");
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/g5"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("g5");
    }

    @Test
    public void testDeleteGroup_MissingId() {
        try {
            api.deleteGroup(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("groupId"));
        }
    }

    @Test
    public void testDeleteGroup_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteGroup("g6", Collections.singletonMap("X-D","v"));

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

    /* assignUserToGroup */
    @Test
    public void testAssignUserToGroup_Success() throws Exception {
        stubVoidInvoke();
        api.assignUserToGroup("g7","u1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/g7/users/u1"), eq("PUT"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("g7");
        verify(apiClient).escapeString("u1");
    }

    @Test
    public void testAssignUserToGroup_MissingGroupId() {
        try {
            api.assignUserToGroup(null,"uX");
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("groupId"));
        }
    }

    @Test
    public void testAssignUserToGroup_MissingUserId() {
        try {
            api.assignUserToGroup("g8",null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("userId"));
        }
    }

    @Test
    public void testAssignUserToGroup_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.assignUserToGroup("g9","u2", Collections.singletonMap("X-A","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("1", cap.getValue().get("X-A"));
    }

    /* unassignUserFromGroup */
    @Test
    public void testUnassignUserFromGroup_Success() throws Exception {
        stubVoidInvoke();
        api.unassignUserFromGroup("g10","u3");
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/g10/users/u3"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("g10");
        verify(apiClient).escapeString("u3");
    }

    @Test
    public void testUnassignUserFromGroup_MissingGroupId() {
        try {
            api.unassignUserFromGroup(null,"u4");
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("groupId"));
        }
    }

    @Test
    public void testUnassignUserFromGroup_MissingUserId() {
        try {
            api.unassignUserFromGroup("g11",null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("userId"));
        }
    }

    @Test
    public void testUnassignUserFromGroup_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.unassignUserFromGroup("g12","u5", Collections.singletonMap("X-U","z"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("z", cap.getValue().get("X-U"));
    }

    /* listGroups */
    @Test
    public void testListGroups_Success_AllParams() throws Exception {
        List<Group> expected = Arrays.asList(new Group());
        stubInvoke(expected);
        List<Group> actual = api.listGroups("s","f","qv","a1",25,"exp","profile.name","desc");
        assertSame(expected, actual);

        verify(apiClient).parameterToPair("search","s");
        verify(apiClient).parameterToPair("filter","f");
        verify(apiClient).parameterToPair("q","qv");
        verify(apiClient).parameterToPair("after","a1");
        verify(apiClient).parameterToPair("limit",25);
        verify(apiClient).parameterToPair("expand","exp");
        verify(apiClient).parameterToPair("sortBy","profile.name");
        verify(apiClient).parameterToPair("sortOrder","desc");

        verify(apiClient).invokeAPI(
            eq("/api/v1/groups"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListGroups_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listGroups(null,null,null,null,null,null,null,null, Collections.singletonMap("X-L","1"));
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

    /* listGroupUsers */
    @Test
    public void testListGroupUsers_Success() throws Exception {
        List<User> expected = Arrays.asList(new User());
        stubInvoke(expected);
        List<User> actual = api.listGroupUsers("g13","a2",100);
        assertSame(expected, actual);

        verify(apiClient).parameterToPair("after","a2");
        verify(apiClient).parameterToPair("limit",100);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/g13/users"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("g13");
    }

    @Test
    public void testListGroupUsers_MissingGroupId() {
        try {
            api.listGroupUsers(null,null,null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("groupId"));
        }
    }

    /* listAssignedApplicationsForGroup */
    @Test
    public void testListAssignedApplicationsForGroup_Success() throws Exception {
        List<Application> expected = Arrays.asList(new Application());
        stubInvoke(expected);
        List<Application> actual = api.listAssignedApplicationsForGroup("g14","a3",10);
        assertSame(expected, actual);

        verify(apiClient).parameterToPair("after","a3");
        verify(apiClient).parameterToPair("limit",10);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/g14/apps"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("g14");
    }

    @Test
    public void testListAssignedApplicationsForGroup_MissingGroupId() {
        try {
            api.listAssignedApplicationsForGroup(null,null,null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("groupId"));
        }
    }

    /* ApiException propagation */
    @Test
    public void testAddGroup_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"boom"));
        try {
            api.addGroup(new AddGroupRequest());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertTrue(ex.getMessage().contains("boom"));
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.GroupApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
