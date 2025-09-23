package src.gen.java.main.com.okta.sdk.resource.api;

import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.FederatedClaim;
import com.okta.sdk.resource.model.FederatedClaimRequestBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ApplicationSsoFederatedClaimsApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ApplicationSsoFederatedClaimsApi api;

    @BeforeEach
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.ApplicationSsoFederatedClaimsApi(apiClient);
    }

    @Test
    public void testCreateFederatedClaim_NullAppId_ThrowsException() {
        FederatedClaimRequestBody body = new FederatedClaimRequestBody();
        ApiException ex = assertThrows(ApiException.class, () -> api.createFederatedClaim(null, body));
        assertEquals(400, ex.getCode());
    }

    @Test
    public void testCreateFederatedClaim_NullBody_ThrowsException() {
        ApiException ex = assertThrows(ApiException.class, () -> api.createFederatedClaim("appId", null));
        assertEquals(400, ex.getCode());
    }

    @Test
    public void testDeleteFederatedClaim_NullAppId_ThrowsException() {
        ApiException ex = assertThrows(ApiException.class, () -> api.deleteFederatedClaim(null, "claimId"));
        assertEquals(400, ex.getCode());
    }

    @Test
    public void testDeleteFederatedClaim_NullClaimId_ThrowsException() {
        ApiException ex = assertThrows(ApiException.class, () -> api.deleteFederatedClaim("appId", null));
        assertEquals(400, ex.getCode());
    }

    @Test
    public void testGetFederatedClaim_NullAppId_ThrowsException() {
        ApiException ex = assertThrows(ApiException.class, () -> api.getFederatedClaim(null, "claimId"));
        assertEquals(400, ex.getCode());
    }

    @Test
    public void testGetFederatedClaim_NullClaimId_ThrowsException() {
        ApiException ex = assertThrows(ApiException.class, () -> api.getFederatedClaim("appId", null));
        assertEquals(400, ex.getCode());
    }

    @Test
    public void testListFederatedClaims_NullAppId_ThrowsException() {
        ApiException ex = assertThrows(ApiException.class, () -> api.listFederatedClaims(null));
        assertEquals(400, ex.getCode());
    }

    @Test
    public void testReplaceFederatedClaim_NullAppId_ThrowsException() {
        ApiException ex = assertThrows(ApiException.class, () -> api.replaceFederatedClaim(null, "claimId", new FederatedClaim()));
        assertEquals(400, ex.getCode());
    }

    @Test
    public void testReplaceFederatedClaim_NullClaimId_ThrowsException() {
        ApiException ex = assertThrows(ApiException.class, () -> api.replaceFederatedClaim("appId", null, new FederatedClaim()));
        assertEquals(400, ex.getCode());
    }

//    @Test
//    public void testListFederatedClaims_CallsApiClient() throws ApiException {
//        // Ensure parameterToString never returns null
//        when(apiClient.parameterToString(any())).thenAnswer(invocation -> {
//            Object arg = invocation.getArgument(0);
//            return arg == null ? "" : arg.toString();
//        });
//        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
//        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
//        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), any(), anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any()))
//            .thenReturn(Collections.emptyList());
//
//        List<FederatedClaim> result = api.listFederatedClaims("appId");
//        assertNotNull(result);
//        verify(apiClient, times(1)).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), any(), anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
//    }
}
