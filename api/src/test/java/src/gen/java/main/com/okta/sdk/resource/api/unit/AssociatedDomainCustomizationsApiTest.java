package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.api.AssociatedDomainCustomizationsApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.WellKnownURIsRoot;
import com.okta.sdk.resource.model.WellKnownURIObjectResponse;
import com.okta.sdk.resource.model.WellKnownURIRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AssociatedDomainCustomizationsApiTest {

    private ApiClient apiClient;
    private AssociatedDomainCustomizationsApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new AssociatedDomainCustomizationsApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPairs(anyString(), eq("expand"), any())).thenAnswer(inv -> {
            Object val = inv.getArgument(2);
            if (val == null) return Collections.emptyList();
            if (val instanceof Collection) {
                String joined = String.join(",", ((Collection<?>) val).stream().map(String::valueOf).toArray(String[]::new));
                return Collections.singletonList(new Pair("expand", joined));
            }
            return Collections.singletonList(new Pair("expand", String.valueOf(val)));
        });
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

    // getAllWellKnownURIs
    @Test
    public void testGetAllWellKnownURIs_SuccessWithExpand() throws Exception {
        WellKnownURIsRoot expected = new WellKnownURIsRoot();
        stubInvoke(expected);

        List<String> expand = Arrays.asList("a", "b");
        WellKnownURIsRoot actual = api.getAllWellKnownURIs("brand123", expand);
        assertSame(expected, actual);

        ArgumentCaptor<List> collectionQpCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            contains("/api/v1/brands/brand123/well-known-uris"), eq("GET"),
            anyList(), collectionQpCap.capture(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));

        @SuppressWarnings("unchecked")
        List<Pair> cqp = collectionQpCap.getValue();
        assertFalse(cqp.isEmpty());
        assertEquals("expand", cqp.get(0).getName());
        assertEquals("a,b", cqp.get(0).getValue());
        verify(apiClient).escapeString("brand123");
    }

    @Test(expected = ApiException.class)
    public void testGetAllWellKnownURIs_MissingBrandId() throws Exception {
        api.getAllWellKnownURIs(null, Collections.singletonList("x"));
    }

    // getAppleAppSiteAssociationWellKnownURI
    @Test
    public void testGetAppleAppSiteAssociationWellKnownURI_Success() throws Exception {
        Object expected = new Object();
        stubInvoke(expected);
        Object actual = api.getAppleAppSiteAssociationWellKnownURI();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/.well-known/apple-app-site-association"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    // getAssetLinksWellKnownURI
    @Test
    public void testGetAssetLinksWellKnownURI_Success() throws Exception {
        List<Object> expected = Arrays.asList(new Object(), new Object());
        stubInvoke(expected);
        List<Object> actual = api.getAssetLinksWellKnownURI();
        assertEquals(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/.well-known/assetlinks.json"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    // getBrandWellKnownURI
    @Test
    public void testGetBrandWellKnownURI_Success() throws Exception {
        WellKnownURIObjectResponse expected = new WellKnownURIObjectResponse();
        stubInvoke(expected);
        WellKnownURIObjectResponse actual = api.getBrandWellKnownURI("brandX", "apple-app-site-association");
        assertSame(expected, actual);

        verify(apiClient).escapeString("brandX");
        verify(apiClient).escapeString("apple-app-site-association");
        verify(apiClient).invokeAPI(
            contains("/api/v1/brands/brandX/well-known-uris/apple-app-site-association/customized"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test(expected = ApiException.class)
    public void testGetBrandWellKnownURI_MissingBrandId() throws Exception {
        api.getBrandWellKnownURI(null, "p");
    }

    @Test(expected = ApiException.class)
    public void testGetBrandWellKnownURI_MissingPath() throws Exception {
        api.getBrandWellKnownURI("brand", null);
    }

    // getRootBrandWellKnownURI
    @Test
    public void testGetRootBrandWellKnownURI_SuccessWithExpand() throws Exception {
        WellKnownURIObjectResponse expected = new WellKnownURIObjectResponse();
        stubInvoke(expected);
        List<String> expand = Collections.singletonList("meta");
        WellKnownURIObjectResponse actual = api.getRootBrandWellKnownURI("brandY", "webauthn", expand);
        assertSame(expected, actual);

        ArgumentCaptor<List> collectionQpCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            contains("/api/v1/brands/brandY/well-known-uris/webauthn"), eq("GET"),
            anyList(), collectionQpCap.capture(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));

        @SuppressWarnings("unchecked")
        List<Pair> cqp = collectionQpCap.getValue();
        assertEquals(1, cqp.size());
        assertEquals("expand", cqp.get(0).getName());
        assertEquals("meta", cqp.get(0).getValue());
    }

    @Test(expected = ApiException.class)
    public void testGetRootBrandWellKnownURI_MissingBrandId() throws Exception {
        api.getRootBrandWellKnownURI(null, "p", null);
    }

    @Test(expected = ApiException.class)
    public void testGetRootBrandWellKnownURI_MissingPath() throws Exception {
        api.getRootBrandWellKnownURI("brand", null, null);
    }

    // getWebAuthnWellKnownURI
    @Test
    public void testGetWebAuthnWellKnownURI_Success() throws Exception {
        Object expected = new Object();
        stubInvoke(expected);
        Object actual = api.getWebAuthnWellKnownURI();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/.well-known/webauthn"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    // replaceBrandWellKnownURI
    @Test
    public void testReplaceBrandWellKnownURI_Success() throws Exception {
        WellKnownURIObjectResponse expected = new WellKnownURIObjectResponse();
        stubInvoke(expected);

        WellKnownURIRequest body = new WellKnownURIRequest();
        WellKnownURIObjectResponse actual = api.replaceBrandWellKnownURI("brandZ", "webauthn", body);
        assertSame(expected, actual);

        ArgumentCaptor<String> methodCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(
            contains("/api/v1/brands/brandZ/well-known-uris/webauthn/customized"), methodCap.capture(),
            anyList(), anyList(),
            anyString(), eq(body),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("PUT", methodCap.getValue());
    }

    @Test(expected = ApiException.class)
    public void testReplaceBrandWellKnownURI_MissingBrandId() throws Exception {
        api.replaceBrandWellKnownURI(null, "p", new WellKnownURIRequest());
    }

    @Test(expected = ApiException.class)
    public void testReplaceBrandWellKnownURI_MissingPath() throws Exception {
        api.replaceBrandWellKnownURI("brand", null, new WellKnownURIRequest());
    }
}
