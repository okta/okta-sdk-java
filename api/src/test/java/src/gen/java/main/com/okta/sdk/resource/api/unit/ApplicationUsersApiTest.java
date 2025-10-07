package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.AppUser;
import com.okta.sdk.resource.model.AppUserAssignRequest;
import com.okta.sdk.resource.model.AppUserUpdateRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApplicationUsersApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ApplicationUsersApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.ApplicationUsersApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(inv -> {
            String name = inv.getArgument(0);
            Object value = inv.getArgument(1);
            if (value == null) return Collections.emptyList();
            return Collections.singletonList(new Pair(name, String.valueOf(value)));
        });
    }

    // assignUserToApplication
    @Test
    public void testAssignUserToApplication_Success() throws Exception {
        AppUser expected = new AppUser();
        whenInvokeReturn(expected);

        AppUserAssignRequest body = new AppUserAssignRequest();
        AppUser actual = api.assignUserToApplication("app1", body);
        assertSame(expected, actual);

        verify(apiClient).escapeString("app1");
        verifyInvokeMethod("POST");
    }

    @Test(expected = ApiException.class)
    public void testAssignUserToApplication_MissingAppId() throws Exception {
        api.assignUserToApplication(null, new AppUserAssignRequest());
    }

    @Test(expected = ApiException.class)
    public void testAssignUserToApplication_MissingBody() throws Exception {
        api.assignUserToApplication("app1", null);
    }

    // getApplicationUser
    @Test
    public void testGetApplicationUser_SuccessWithExpand() throws Exception {
        AppUser expected = new AppUser();
        whenInvokeReturn(expected);

        AppUser actual = api.getApplicationUser("appA", "userB", "user");
        assertSame(expected, actual);

        ArgumentCaptor<List> qpCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"),
            qpCap.capture(), anyList(), anyString(), any(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));

        Map<String,String> qp = flattenPairs(qpCap.getValue());
        assertEquals("user", qp.get("expand"));
        verify(apiClient).escapeString("appA");
        verify(apiClient).escapeString("userB");
    }

    @Test(expected = ApiException.class)
    public void testGetApplicationUser_MissingAppId() throws Exception {
        api.getApplicationUser(null, "user1", null);
    }

    @Test(expected = ApiException.class)
    public void testGetApplicationUser_MissingUserId() throws Exception {
        api.getApplicationUser("app1", null, null);
    }

    // listApplicationUsers
    @Test
    public void testListApplicationUsers_AllQueryParams() throws Exception {
        List<AppUser> expected = Arrays.asList(new AppUser(), new AppUser());
        whenInvokeReturn(expected);

        List<AppUser> actual = api.listApplicationUsers("appZ", "cursor123", 25, "alice", "user");
        assertEquals(expected, actual);

        ArgumentCaptor<List> qpCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"),
            qpCap.capture(), anyList(), anyString(), any(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));

        Map<String,String> qp = flattenPairs(qpCap.getValue());
        assertEquals("cursor123", qp.get("after"));
        assertEquals("25", qp.get("limit"));
        assertEquals("alice", qp.get("q"));
        assertEquals("user", qp.get("expand"));
    }

    @Test
    public void testListApplicationUsers_NullOptionalParams() throws Exception {
        List<AppUser> expected = Collections.emptyList();
        whenInvokeReturn(expected);

        List<AppUser> actual = api.listApplicationUsers("appOnly", null, null, null, null);
        assertSame(expected, actual);

        ArgumentCaptor<List> qpCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"),
            qpCap.capture(), anyList(), anyString(), any(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));

        Map<String,String> qp = flattenPairs(qpCap.getValue());
        assertTrue(qp.isEmpty());
    }

    @Test(expected = ApiException.class)
    public void testListApplicationUsers_MissingAppId() throws Exception {
        api.listApplicationUsers(null, null, null, null, null);
    }

    // unassignUserFromApplication
    @Test
    public void testUnassignUserFromApplication_SuccessWithSendEmail() throws Exception {
        whenVoidInvoke();

        api.unassignUserFromApplication("app5", "user9", true);

        ArgumentCaptor<List> qpCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(anyString(), eq("DELETE"),
            qpCap.capture(), anyList(), anyString(), any(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), isNull());

        Map<String,String> qp = flattenPairs(qpCap.getValue());
        assertEquals("true", qp.get("sendEmail"));
        verifyInvokeMethod("DELETE");
        verify(apiClient).escapeString("app5");
        verify(apiClient).escapeString("user9");
    }

    @Test(expected = ApiException.class)
    public void testUnassignUserFromApplication_MissingAppId() throws Exception {
        api.unassignUserFromApplication(null, "userX", null);
    }

    @Test(expected = ApiException.class)
    public void testUnassignUserFromApplication_MissingUserId() throws Exception {
        api.unassignUserFromApplication("appX", null, null);
    }

    // updateApplicationUser
    @Test
    public void testUpdateApplicationUser_Success() throws Exception {
        AppUser expected = new AppUser();
        whenInvokeReturn(expected);

        AppUserUpdateRequest body = new AppUserUpdateRequest();
        AppUser actual = api.updateApplicationUser("app7", "user8", body);
        assertSame(expected, actual);

        verify(apiClient).escapeString("app7");
        verify(apiClient).escapeString("user8");
        verifyInvokeMethod("POST");
    }

    @Test(expected = ApiException.class)
    public void testUpdateApplicationUser_MissingAppId() throws Exception {
        api.updateApplicationUser(null, "u1", new AppUserUpdateRequest());
    }

    @Test(expected = ApiException.class)
    public void testUpdateApplicationUser_MissingUserId() throws Exception {
        api.updateApplicationUser("app1", null, new AppUserUpdateRequest());
    }

    @Test(expected = ApiException.class)
    public void testUpdateApplicationUser_MissingBody() throws Exception {
        api.updateApplicationUser("app1", "user1", null);
    }

    // Helpers
    private <T> void whenInvokeReturn(T value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void whenVoidInvoke() throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenReturn(null);
    }

    private void verifyInvokeMethod(String method) throws ApiException {
        ArgumentCaptor<String> methodCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient, atLeastOnce()).invokeAPI(
            anyString(), methodCap.capture(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertTrue(methodCap.getAllValues().contains(method));
    }

    @SuppressWarnings("unchecked")
    private Map<String,String> flattenPairs(List<?> pairs) {
        if (pairs == null) return Collections.emptyMap();
        return (Map<String,String>) pairs.stream()
            .filter(Objects::nonNull)
            .map(p -> (Pair) p)
            .collect(Collectors.toMap(Pair::getName, Pair::getValue, (a,b) -> b));
    }
}
