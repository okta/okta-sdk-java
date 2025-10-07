package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.AgentAction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class DirectoriesIntegrationApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.DirectoriesIntegrationApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.DirectoriesIntegrationApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
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

    @Test
    public void testUpdateADGroupMembership_Success() throws Exception {
        stubVoidInvoke();
        AgentAction body = new AgentAction();
        api.updateADGroupMembership("app123", body);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/directories/app123/groups/modify"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("app123");
    }

    @Test
    public void testUpdateADGroupMembership_WithHeaders() throws Exception {
        stubVoidInvoke();
        AgentAction body = new AgentAction();
        Map<String,String> headers = Collections.singletonMap("X-Custom","v");
        api.updateADGroupMembership("app456", body, headers);

        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", hdrCap.getValue().get("X-Custom"));
    }

    @Test
    public void testUpdateADGroupMembership_MissingAppInstanceId() {
        try {
            api.updateADGroupMembership(null, new AgentAction());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("appInstanceId"));
        }
    }

    @Test
    public void testUpdateADGroupMembership_MissingAgentAction() {
        try {
            api.updateADGroupMembership("app789", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("agentAction"));
        }
    }

    @Test
    public void testUpdateADGroupMembership_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenThrow(new ApiException(500,"boom"));

        try {
            api.updateADGroupMembership("appErr", new AgentAction());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertTrue(ex.getMessage().contains("boom"));
        }
    }

//    @Test
//    public void testGetObjectMapperConfiguration() {
//        ObjectMapper mapper = com.okta.sdk.resource.api.DirectoriesIntegrationApi.getObjectMapper();
//        assertNotNull(mapper);
//        assertFalse(mapper.getDeserializationConfig()
//            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
//        assertTrue(mapper.getDeserializationConfig()
//            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
//    }
}
