package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.RoleCResourceSetApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.CreateResourceSetRequest;
import com.okta.sdk.resource.model.ResourceSet;
import com.okta.sdk.resource.model.ResourceSets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class RoleCResourceSetApiTest {

    private ApiClient apiClient;
    private RoleCResourceSetApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new RoleCResourceSetApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(new ArrayList<Pair>());
    }

    private void stubInvokeResourceSet(ResourceSet value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubInvokeResourceSets(ResourceSets value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    /* createResourceSet */
    @Test
    public void testCreateResourceSet_Success() throws Exception {
        ResourceSet expected = new ResourceSet();
        stubInvokeResourceSet(expected);
        CreateResourceSetRequest req = new CreateResourceSetRequest();
        ResourceSet actual = api.createResourceSet(req);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/resource-sets"),
            eq("POST"),
            anyList(), anyList(),
            anyString(), eq(req),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testCreateResourceSet_WithHeaders() throws Exception {
        ResourceSet rs = new ResourceSet();
        stubInvokeResourceSet(rs);
        Map<String,String> hdr = Collections.singletonMap("X-H","v");
        api.createResourceSet(new CreateResourceSetRequest(), hdr);
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", headers.getValue().get("X-H"));
    }

    @Test
    public void testCreateResourceSet_MissingParam() {
        try {
            api.createResourceSet(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* getResourceSet */
    @Test
    public void testGetResourceSet_Success() throws Exception {
        ResourceSet expected = new ResourceSet();
        stubInvokeResourceSet(expected);
        ResourceSet actual = api.getResourceSet("RS1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/resource-sets/RS1"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetResourceSet_WithHeaders() throws Exception {
        stubInvokeResourceSet(new ResourceSet());
        api.getResourceSet("RS2", Collections.singletonMap("X-G","g"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("g", headers.getValue().get("X-G"));
    }

    @Test
    public void testGetResourceSet_MissingParam() {
        try {
            api.getResourceSet(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* listResourceSets */
    @Test
    public void testListResourceSets_Success() throws Exception {
        ResourceSets expected = new ResourceSets();
        stubInvokeResourceSets(expected);
        ResourceSets actual = api.listResourceSets("AFTER1");
        assertSame(expected, actual);
        verify(apiClient).parameterToPair("after", "AFTER1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/resource-sets"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testListResourceSets_WithHeaders() throws Exception {
        stubInvokeResourceSets(new ResourceSets());
        api.listResourceSets(null, Collections.singletonMap("X-L","1"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("1", headers.getValue().get("X-L"));
    }

    /* replaceResourceSet */
    @Test
    public void testReplaceResourceSet_Success() throws Exception {
        ResourceSet body = new ResourceSet();
        ResourceSet expected = new ResourceSet();
        stubInvokeResourceSet(expected);
        ResourceSet actual = api.replaceResourceSet("RSX", body);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/resource-sets/RSX"),
            eq("PUT"),
            anyList(), anyList(),
            anyString(), eq(body),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testReplaceResourceSet_WithHeaders() throws Exception {
        stubInvokeResourceSet(new ResourceSet());
        Map<String,String> hdr = Collections.singletonMap("X-R","v");
        api.replaceResourceSet("RSY", new ResourceSet(), hdr);
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", headers.getValue().get("X-R"));
    }

    @Test
    public void testReplaceResourceSet_MissingParams() {
        try {
            api.replaceResourceSet(null, new ResourceSet());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
        try {
            api.replaceResourceSet("RSZ", null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* deleteResourceSet */
    @Test
    public void testDeleteResourceSet_Success() throws Exception {
        stubInvokeResourceSet(null); // safe default for void
        api.deleteResourceSet("RSD");
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/resource-sets/RSD"),
            eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testDeleteResourceSet_WithHeaders() throws Exception {
        stubInvokeResourceSet(null);
        api.deleteResourceSet("RSE", Collections.singletonMap("X-D","d"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("d", headers.getValue().get("X-D"));
    }

    @Test
    public void testDeleteResourceSet_MissingParam() {
        try {
            api.deleteResourceSet(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_Get() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"down"));
        try {
            api.getResourceSet("X");
            fail("Expected");
        } catch (ApiException e){
            assertEquals(502, e.getCode());
        }
    }

    @Test
    public void testApiExceptionPropagates_Delete() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenThrow(new ApiException(503,"svc"));
        try {
            api.deleteResourceSet("Y");
            fail("Expected");
        } catch (ApiException e){
            assertEquals(503, e.getCode());
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_List() throws Exception {
        stubInvokeResourceSets(new ResourceSets());
        api.listResourceSets(null);
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/iam/resource-sets"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_Get() throws Exception {
        stubInvokeResourceSet(new ResourceSet());
        api.getResourceSet("AAA");
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/iam/resource-sets/AAA"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = RoleCResourceSetApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
