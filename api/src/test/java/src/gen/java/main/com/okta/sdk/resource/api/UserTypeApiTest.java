package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.api.UserTypeApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.UserType;
import com.okta.sdk.resource.model.UserTypePostRequest;
import com.okta.sdk.resource.model.UserTypePutRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserTypeApiTest {

    private ApiClient apiClient;
    private UserTypeApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new UserTypeApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    // createUserType
    @Test
    public void testCreateUserType_Success() throws Exception {
        UserType body = new UserType();
        UserType expected = new UserType();
        stubInvoke(expected);

        UserType actual = api.createUserType(body);
        assertSame(expected, actual);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> method = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            path.capture(), method.capture(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("POST", method.getValue());
        assertEquals("/api/v1/meta/types/user", path.getValue());
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateUserType_WithHeaders() throws Exception {
        stubInvoke(new UserType());
        api.createUserType(new UserType(), Collections.singletonMap("X-C","v"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", headers.getValue().get("X-C"));
    }

    @Test(expected = ApiException.class)
    public void testCreateUserType_MissingBody() throws Exception {
        api.createUserType(null);
    }

    // deleteUserType
    @Test
    public void testDeleteUserType_Success() throws Exception {
        stubVoidInvoke();
        api.deleteUserType("tid1");

        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/types/user/tid1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("tid1");
    }

    @Test
    public void testDeleteUserType_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteUserType("tid2", Collections.singletonMap("X-D","d"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("d", headers.getValue().get("X-D"));
    }

    @Test(expected = ApiException.class)
    public void testDeleteUserType_MissingTypeId() throws Exception {
        api.deleteUserType(null);
    }

    // getUserType
    @Test
    public void testGetUserType_Success() throws Exception {
        UserType expected = new UserType();
        stubInvoke(expected);

        UserType actual = api.getUserType("tid3");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/types/user/tid3"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("tid3");
    }

    @Test
    public void testGetUserType_WithHeaders() throws Exception {
        stubInvoke(new UserType());
        api.getUserType("tid4", Collections.singletonMap("X-G","g"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("g", headers.getValue().get("X-G"));
    }

    @Test(expected = ApiException.class)
    public void testGetUserType_MissingTypeId() throws Exception {
        api.getUserType(null);
    }

    // listUserTypes
    @Test
    public void testListUserTypes_Success() throws Exception {
        List<UserType> expected = Arrays.asList(new UserType(), new UserType());
        stubInvoke(expected);

        List<UserType> actual = api.listUserTypes();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/types/user"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListUserTypes_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listUserTypes(Collections.singletonMap("X-L","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", headers.getValue().get("X-L"));
    }

    // replaceUserType
    @Test
    public void testReplaceUserType_Success() throws Exception {
        UserType expected = new UserType();
        stubInvoke(expected);
        UserTypePutRequest body = new UserTypePutRequest();

        UserType actual = api.replaceUserType("tid5", body);
        assertSame(expected, actual);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> method = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            path.capture(), method.capture(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("PUT", method.getValue());
        assertEquals("/api/v1/meta/types/user/tid5", path.getValue());
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("tid5");
    }

    @Test
    public void testReplaceUserType_NullBodyAllowed() throws Exception {
        UserType expected = new UserType();
        stubInvoke(expected);

        UserType actual = api.replaceUserType("tid6", null);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertNull(bodyCap.getValue());
    }

    @Test
    public void testReplaceUserType_WithHeaders() throws Exception {
        stubInvoke(new UserType());
        api.replaceUserType("tid7", new UserTypePutRequest(), Collections.singletonMap("X-R","r"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("r", headers.getValue().get("X-R"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceUserType_MissingTypeId() throws Exception {
        api.replaceUserType(null, new UserTypePutRequest());
    }

    // updateUserType
    @Test
    public void testUpdateUserType_Success() throws Exception {
        UserTypePostRequest body = new UserTypePostRequest();
        UserType expected = new UserType();
        stubInvoke(expected);

        UserType actual = api.updateUserType("tid8", body);
        assertSame(expected, actual);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> method = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            path.capture(), method.capture(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("POST", method.getValue());
        assertEquals("/api/v1/meta/types/user/tid8", path.getValue());
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("tid8");
    }

    @Test
    public void testUpdateUserType_WithHeaders() throws Exception {
        stubInvoke(new UserType());
        api.updateUserType("tid9", new UserTypePostRequest(), Collections.singletonMap("X-U","u"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("u", headers.getValue().get("X-U"));
    }

    @Test(expected = ApiException.class)
    public void testUpdateUserType_MissingTypeId() throws Exception {
        api.updateUserType(null, new UserTypePostRequest());
    }

    @Test(expected = ApiException.class)
    public void testUpdateUserType_MissingBody() throws Exception {
        api.updateUserType("tid10", null);
    }

    // Helpers
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

    private void stubVoidInvoke() throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull())
        ).thenReturn(null);
    }
}
