//package com.okta.sdk.resource.api;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.okta.sdk.resource.client.ApiClient;
//import com.okta.sdk.resource.client.ApiException;
//import com.okta.sdk.resource.client.Pair;
//import com.okta.sdk.resource.model.CreateUserRequest;
//import com.okta.sdk.resource.model.UpdateUserRequest;
//import com.okta.sdk.resource.model.User;
//import com.okta.sdk.resource.model.UserGetSingleton;
//import com.okta.sdk.resource.model.UserNextLogin;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.ArgumentCaptor;
//
//import java.util.*;
//
//import static org.junit.Assert.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@SuppressWarnings({"unchecked","rawtypes"})
//public class UserApiTest {
//
//    private ApiClient apiClient;
//    private com.okta.sdk.resource.api.UserApi api;
//
//    @Before
//    public void setup() {
//        apiClient = mock(ApiClient.class);
//        api = new com.okta.sdk.resource.api.UserApi(apiClient);
//
//        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
//        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
//        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
//        when(apiClient.parameterToPair(anyString(), any())).thenReturn(new ArrayList<Pair>());
//        when(apiClient.parameterToString(any())).thenAnswer(i -> String.valueOf(i.getArgument(0)));
//    }
//
//    private <T> void stubInvokeReturn(T value) throws ApiException {
//        when(apiClient.invokeAPI(
//            anyString(), anyString(),
//            anyList(), anyList(),
//            anyString(), any(),
//            anyMap(), anyMap(), anyMap(),
//            anyString(), anyString(),
//            any(String[].class), any(TypeReference.class)
//        )).thenReturn(value);
//    }
//
//    private void stubInvokeVoid() throws ApiException {
//        when(apiClient.invokeAPI(
//            anyString(), anyString(),
//            anyList(), anyList(),
//            anyString(), any(),
//            anyMap(), anyMap(), anyMap(),
//            anyString(), anyString(),
//            any(String[].class), isNull()
//        )).thenReturn(null);
//    }
//
//    /* createUser (overload with additional headers) */
//    @Test
//    public void testCreateUser_WithAdditionalHeaders() throws Exception {
//        stubInvokeReturn(new User());
//        CreateUserRequest body = new CreateUserRequest();
//        Map<String,String> extra = Collections.singletonMap("X-Trace","abc");
//        // call overload via reflection if not directly visible (keeps compile safety if signature present)
//        api.createUser(body, true, false, UserNextLogin.CHANGE_PASSWORD, extra);
//        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
//        verify(apiClient).invokeAPI(eq("/api/v1/users"), eq("POST"),
//            anyList(), anyList(), anyString(), eq(body),
//            cap.capture(), anyMap(), anyMap(), anyString(), anyString(),
//            any(String[].class), any(TypeReference.class));
//        assertEquals("abc", cap.getValue().get("X-Trace"));
//    }
//
//    /* listUsers (additional headers) */
//    @Test
//    public void testListUsers_WithAdditionalHeaders() throws Exception {
//        stubInvokeReturn(new ArrayList<User>());
//        Map<String,String> hdr = Collections.singletonMap("X-Req-Id","r1");
//        api.listUsers("application/json", null, null, null, null, null, null, null, null, hdr);
//        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
//        verify(apiClient).invokeAPI(eq("/api/v1/users"), eq("GET"),
//            anyList(), anyList(), anyString(), isNull(),
//            cap.capture(), anyMap(), anyMap(), anyString(), anyString(),
//            any(String[].class), any(TypeReference.class));
//        assertEquals("r1", cap.getValue().get("X-Req-Id"));
//    }
//
//    /* getUser missing id */
//    @Test
//    public void testGetUser_MissingId() {
//        try {
//            api.getUser(null, null, null);
//            fail("Expected ApiException");
//        } catch (ApiException e) {
//            assertEquals(400, e.getCode());
//        }
//    }
//
//    /* replaceUser missing id */
//    @Test
//    public void testReplaceUser_MissingId() {
//        try {
//            api.replaceUser(null, new UpdateUserRequest(), false, null);
//            fail("Expected ApiException");
//        } catch (ApiException e) {
//            assertEquals(400, e.getCode());
//        }
//    }
//
//    /* updateUser missing id */
//    @Test
//    public void testUpdateUser_MissingId() {
//        try {
//            api.updateUser(null, new UpdateUserRequest(), false, null);
//            fail("Expected ApiException");
//        } catch (ApiException e) {
//            assertEquals(400, e.getCode());
//        }
//    }
//
//    /* deleteUser additional headers overload */
//    @Test
//    public void testDeleteUser_WithAdditionalHeaders() throws Exception {
//        stubInvokeVoid();
//        Map<String,String> hdr = Collections.singletonMap("Prefer","respond-async");
//        api.deleteUser("userA", true, "respond-async", hdr);
//        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
//        verify(apiClient).invokeAPI(eq("/api/v1/users/userA"), eq("DELETE"),
//            anyList(), anyList(), anyString(), isNull(),
//            cap.capture(), anyMap(), anyMap(), anyString(), anyString(),
//            any(String[].class), isNull());
//        assertEquals("respond-async", cap.getValue().get("Prefer"));
//    }
//
//    /* getUser additional headers overload */
//    @Test
//    public void testGetUser_WithAdditionalHeaders() throws Exception {
//        stubInvokeReturn(new UserGetSingleton());
//        Map<String,String> hdr = Collections.singletonMap("Content-Type","application/json; okta-response=omitCredentials");
//        api.getUser("userB", "application/json; okta-response=omitCredentials", null, hdr);
//        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
//        verify(apiClient).invokeAPI(eq("/api/v1/users/userB"), eq("GET"),
//            anyList(), anyList(), anyString(), isNull(),
//            cap.capture(), anyMap(), anyMap(), anyString(), anyString(),
//            any(String[].class), any(TypeReference.class));
//        assertEquals("application/json; okta-response=omitCredentials", cap.getValue().get("Content-Type"));
//    }
//
//    /* replaceUser additional headers */
//    @Test
//    public void testReplaceUser_WithAdditionalHeaders() throws Exception {
//        User expected = new User();
//        stubInvokeReturn(expected);
//        UpdateUserRequest body = new UpdateUserRequest();
//        Map<String,String> hdr = Collections.singletonMap("If-Match","etag123");
//        User actual = api.replaceUser("userC", body, true, "etag123", hdr);
//        assertSame(expected, actual);
//        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
//        verify(apiClient).invokeAPI(eq("/api/v1/users/userC"), eq("PUT"),
//            anyList(), anyList(), anyString(), eq(body),
//            cap.capture(), anyMap(), anyMap(), anyString(), anyString(),
//            any(String[].class), any(TypeReference.class));
//        assertEquals("etag123", cap.getValue().get("If-Match"));
//    }
//
//    /* updateUser additional headers */
//    @Test
//    public void testUpdateUser_WithAdditionalHeaders() throws Exception {
//        User expected = new User();
//        stubInvokeReturn(expected);
//        UpdateUserRequest body = new UpdateUserRequest();
//        Map<String,String> hdr = Collections.singletonMap("If-Match","etag999");
//        User actual = api.updateUser("userD", body, false, "etag999", hdr);
//        assertSame(expected, actual);
//        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
//        verify(apiClient).invokeAPI(eq("/api/v1/users/userD"), eq("POST"),
//            anyList(), anyList(), anyString(), eq(body),
//            cap.capture(), anyMap(), anyMap(), anyString(), anyString(),
//            any(String[].class), any(TypeReference.class));
//        assertEquals("etag999", cap.getValue().get("If-Match"));
//    }
//
//    /* ApiException propagation on createUser */
//    @Test
//    public void testCreateUser_ApiExceptionPropagates() throws Exception {
//        when(apiClient.invokeAPI(
//            anyString(), eq("POST"),
//            anyList(), anyList(), anyString(), any(),
//            anyMap(), anyMap(), anyMap(), anyString(), anyString(),
//            any(String[].class), any(TypeReference.class)
//        )).thenThrow(new ApiException(500,"err"));
//        try {
//            api.createUser(new CreateUserRequest(), true, false, null);
//            fail("Expected");
//        } catch (ApiException e) {
//            assertEquals(500, e.getCode());
//        }
//    }
//}
