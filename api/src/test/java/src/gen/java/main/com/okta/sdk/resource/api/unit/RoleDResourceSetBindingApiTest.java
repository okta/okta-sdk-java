package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.RoleDResourceSetBindingApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.ResourceSetBindingCreateRequest;
import com.okta.sdk.resource.model.ResourceSetBindingEditResponse;
import com.okta.sdk.resource.model.ResourceSetBindingResponse;
import com.okta.sdk.resource.model.ResourceSetBindings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class RoleDResourceSetBindingApiTest {

    private ApiClient apiClient;
    private RoleDResourceSetBindingApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new RoleDResourceSetBindingApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(new ArrayList<Pair>());
    }

    private void stubInvokeCreate(ResourceSetBindingEditResponse value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubInvokeGet(ResourceSetBindingResponse value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubInvokeList(ResourceSetBindings value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    /* createResourceSetBinding */
    @Test
    public void testCreateBinding_Success() throws Exception {
        ResourceSetBindingCreateRequest req = new ResourceSetBindingCreateRequest();
        ResourceSetBindingEditResponse expected = new ResourceSetBindingEditResponse();
        stubInvokeCreate(expected);

        ResourceSetBindingEditResponse actual = api.createResourceSetBinding("RS1", req);
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/resource-sets/RS1/bindings"),
            eq("POST"),
            anyList(), anyList(),
            anyString(), eq(req),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testCreateBinding_WithHeaders() throws Exception {
        stubInvokeCreate(new ResourceSetBindingEditResponse());
        Map<String,String> hdr = Collections.singletonMap("X-H","v");
        api.createResourceSetBinding("RS2", new ResourceSetBindingCreateRequest(), hdr);

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", cap.getValue().get("X-H"));
    }

    @Test
    public void testCreateBinding_MissingParams() {
        expect400(() -> api.createResourceSetBinding(null, new ResourceSetBindingCreateRequest()));
        expect400(() -> api.createResourceSetBinding("RSX", null));
    }

    /* getBinding */
    @Test
    public void testGetBinding_Success() throws Exception {
        ResourceSetBindingResponse expected = new ResourceSetBindingResponse();
        stubInvokeGet(expected);
        ResourceSetBindingResponse actual = api.getBinding("RS3","ROLE1");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/resource-sets/RS3/bindings/ROLE1"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetBinding_WithHeaders() throws Exception {
        stubInvokeGet(new ResourceSetBindingResponse());
        api.getBinding("RS4","ROLE2", Collections.singletonMap("X-G","g"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);

        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("g", cap.getValue().get("X-G"));
    }

    @Test
    public void testGetBinding_MissingParams() {
        expect400(() -> api.getBinding(null, "R"));
        expect400(() -> api.getBinding("RS", null));
    }

    /* listBindings */
    @Test
    public void testListBindings_Success() throws Exception {
        ResourceSetBindings expected = new ResourceSetBindings();
        stubInvokeList(expected);
        ResourceSetBindings actual = api.listBindings("RS5","CUR123");
        assertSame(expected, actual);

        verify(apiClient).parameterToPair("after","CUR123");
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/resource-sets/RS5/bindings"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testListBindings_WithHeaders() throws Exception {
        stubInvokeList(new ResourceSetBindings());
        api.listBindings("RS6", null, Collections.singletonMap("X-L","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("1", cap.getValue().get("X-L"));
    }

    @Test
    public void testListBindings_MissingParam() {
        expect400(() -> api.listBindings(null, null));
    }

    /* deleteBinding */
    @Test
    public void testDeleteBinding_Success() throws Exception {
        api.deleteBinding("RS7","ROLE7");
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/resource-sets/RS7/bindings/ROLE7"),
            eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testDeleteBinding_WithHeaders() throws Exception {
        api.deleteBinding("RS8","ROLE8", Collections.singletonMap("X-D","d"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("d", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeleteBinding_MissingParams() {
        expect400(() -> api.deleteBinding(null,"R"));
        expect400(() -> api.deleteBinding("RS", null));
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_Create() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"err"));
        try {
            api.createResourceSetBinding("RS9", new ResourceSetBindingCreateRequest());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
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
        )).thenThrow(new ApiException(503,"down"));
        try {
            api.deleteBinding("RSX","ROLEX");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_List() throws Exception {
        stubInvokeList(new ResourceSetBindings());
        api.listBindings("RS10", null);
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/iam/resource-sets/RS10/bindings"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_Get() throws Exception {
        stubInvokeGet(new ResourceSetBindingResponse());
        api.getBinding("RS11","ROLE11");
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/iam/resource-sets/RS11/bindings/ROLE11"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = RoleDResourceSetBindingApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }

    /* helper */
    private void expect400(Runnable r) {
        try {
            r.run();
            fail("Expected 400");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }
}
