package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.api.UserRiskApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.UserRiskGetResponse;
import com.okta.sdk.resource.model.UserRiskPutResponse;
import com.okta.sdk.resource.model.UserRiskRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserRiskApiTest {

    private ApiClient apiClient;
    private UserRiskApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new UserRiskApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    // getUserRisk
    @Test
    public void testGetUserRisk_Success() throws Exception {
        UserRiskGetResponse expected = new UserRiskGetResponse();
        stubInvoke(expected);

        UserRiskGetResponse actual = api.getUserRisk("u1");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/users/u1/risk"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("u1");
    }

    @Test
    public void testGetUserRisk_WithHeaders() throws Exception {
        stubInvoke(new UserRiskGetResponse());
        api.getUserRisk("u2", Collections.singletonMap("X-G","g"));

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
    public void testGetUserRisk_MissingUserId() throws Exception {
        api.getUserRisk(null);
    }

    // upsertUserRisk
    @Test
    public void testUpsertUserRisk_Success() throws Exception {
        UserRiskRequest body = new UserRiskRequest();
        UserRiskPutResponse expected = new UserRiskPutResponse();
        stubInvoke(expected);

        UserRiskPutResponse actual = api.upsertUserRisk("u3", body);
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
        assertEquals("/api/v1/users/u3/risk", path.getValue());
        assertEquals("PUT", method.getValue());
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("u3");
    }

    @Test
    public void testUpsertUserRisk_WithHeaders() throws Exception {
        stubInvoke(new UserRiskPutResponse());
        api.upsertUserRisk("u4", new UserRiskRequest(), Collections.singletonMap("X-U","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", headerCap.getValue().get("X-U"));
    }

    @Test(expected = ApiException.class)
    public void testUpsertUserRisk_MissingUserId() throws Exception {
        api.upsertUserRisk(null, new UserRiskRequest());
    }

    @Test(expected = ApiException.class)
    public void testUpsertUserRisk_MissingBody() throws Exception {
        api.upsertUserRisk("u5", null);
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
}
