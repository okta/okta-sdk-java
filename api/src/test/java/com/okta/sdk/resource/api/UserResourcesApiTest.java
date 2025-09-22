package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.AssignedAppLink;
import com.okta.sdk.resource.model.Group;
import com.okta.sdk.resource.model.OAuth2Client;
import com.okta.sdk.resource.model.UserDevice;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserResourcesApiTest {

    private ApiClient apiClient;
    private UserResourcesApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new UserResourcesApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    // listAppLinks
    @Test
    public void testListAppLinks_Success() throws Exception {
        List<AssignedAppLink> expected = Arrays.asList(new AssignedAppLink(), new AssignedAppLink());
        stubInvoke(expected);

        List<AssignedAppLink> actual = api.listAppLinks("u1");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/users/u1/appLinks"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("u1");
    }

    @Test
    public void testListAppLinks_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listAppLinks("u2", Collections.singletonMap("X-A","v"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", headerCap.getValue().get("X-A"));
    }

    @Test(expected = ApiException.class)
    public void testListAppLinks_MissingId() throws Exception {
        api.listAppLinks(null);
    }

    // listUserClients
    @Test
    public void testListUserClients_Success() throws Exception {
        List<OAuth2Client> expected = Arrays.asList(new OAuth2Client(), new OAuth2Client());
        stubInvoke(expected);

        List<OAuth2Client> actual = api.listUserClients("u3");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/users/u3/clients"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("u3");
    }

    @Test
    public void testListUserClients_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listUserClients("u4", Collections.singletonMap("X-C","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", headerCap.getValue().get("X-C"));
    }

    @Test(expected = ApiException.class)
    public void testListUserClients_MissingUserId() throws Exception {
        api.listUserClients(null);
    }

    // listUserDevices
    @Test
    public void testListUserDevices_Success() throws Exception {
        List<UserDevice> expected = Arrays.asList(new UserDevice(), new UserDevice());
        stubInvoke(expected);

        List<UserDevice> actual = api.listUserDevices("u5");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/users/u5/devices"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("u5");
    }

    @Test
    public void testListUserDevices_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listUserDevices("u6", Collections.singletonMap("X-D","d"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("d", headerCap.getValue().get("X-D"));
    }

    @Test(expected = ApiException.class)
    public void testListUserDevices_MissingUserId() throws Exception {
        api.listUserDevices(null);
    }

    // listUserGroups
    @Test
    public void testListUserGroups_Success() throws Exception {
        List<Group> expected = Arrays.asList(new Group(), new Group());
        stubInvoke(expected);

        List<Group> actual = api.listUserGroups("u7");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/users/u7/groups"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("u7");
    }

    @Test
    public void testListUserGroups_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listUserGroups("u8", Collections.singletonMap("X-G","g"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("g", headerCap.getValue().get("X-G"));
    }

    @Test(expected = ApiException.class)
    public void testListUserGroups_MissingId() throws Exception {
        api.listUserGroups(null);
    }

    // Helper
    private <T> void stubInvoke(T value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class))
        ).thenReturn(value);
    }
}
