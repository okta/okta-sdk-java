package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.AgentPoolUpdate;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class AgentPoolsApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.AgentPoolsApi agentPoolsApi;

    @BeforeMethod
    public void setUp() {
        // Manually create the mock (no @Mock / initMocks needed)
        apiClient = mock(ApiClient.class);

        // Common stubs used by the generated API code
        when(apiClient.escapeString(any())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any())).thenReturn("application/json");

        agentPoolsApi = new com.okta.sdk.resource.api.AgentPoolsApi(apiClient);
    }

    @Test
    public void testActivateAgentPoolsUpdate() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), any(), anyString(),
            any(), any(), any(), any(), anyString(), anyString(), any(), any()))
            .thenReturn(new AgentPoolUpdate());

        AgentPoolUpdate r = agentPoolsApi.activateAgentPoolsUpdate("p1", "u1");
        assertNotNull(r);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("POST"), anyList(), any(), anyString(),
            any(), any(), any(), any(), anyString(), anyString(), any(), any());
        assertTrue(path.getValue().contains("/api/v1/agentPools/p1/updates/u1/activate"));
    }

    @Test(expectedExceptions = ApiException.class)
    public void testActivateAgentPoolsUpdate_nullPoolId() throws ApiException {
        agentPoolsApi.activateAgentPoolsUpdate(null, "u1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testActivateAgentPoolsUpdate_nullUpdateId() throws ApiException {
        agentPoolsApi.activateAgentPoolsUpdate("p1", null);
    }
}
