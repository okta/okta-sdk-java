package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.BehaviorRule;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BehaviorApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.BehaviorApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.BehaviorApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    // activateBehaviorDetectionRule
    @Test
    public void testActivateBehaviorDetectionRule_Success() throws Exception {
        BehaviorRule expected = new BehaviorRule();
        stubInvoke(expected);

        BehaviorRule actual = api.activateBehaviorDetectionRule("b1");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/behaviors/b1/lifecycle/activate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("b1");
    }

    @Test
    public void testActivateBehaviorDetectionRule_WithHeaders() throws Exception {
        stubInvoke(new BehaviorRule());
        api.activateBehaviorDetectionRule("b2", Collections.singletonMap("X-A","v"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", hdrCap.getValue().get("X-A"));
    }

    @Test(expected = ApiException.class)
    public void testActivateBehaviorDetectionRule_MissingId() throws Exception {
        api.activateBehaviorDetectionRule(null);
    }

    // createBehaviorDetectionRule
    @Test
    public void testCreateBehaviorDetectionRule_Success() throws Exception {
        BehaviorRule body = new BehaviorRule();
        BehaviorRule expected = new BehaviorRule();
        stubInvoke(expected);

        BehaviorRule actual = api.createBehaviorDetectionRule(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/behaviors"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateBehaviorDetectionRule_WithHeaders() throws Exception {
        stubInvoke(new BehaviorRule());
        api.createBehaviorDetectionRule(new BehaviorRule(), Collections.singletonMap("X-C","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdrCap.getValue().get("X-C"));
    }

    @Test(expected = ApiException.class)
    public void testCreateBehaviorDetectionRule_MissingRule() throws Exception {
        api.createBehaviorDetectionRule(null);
    }

    // deactivateBehaviorDetectionRule
    @Test
    public void testDeactivateBehaviorDetectionRule_Success() throws Exception {
        BehaviorRule expected = new BehaviorRule();
        stubInvoke(expected);

        BehaviorRule actual = api.deactivateBehaviorDetectionRule("b3");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/behaviors/b3/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("b3");
    }

    @Test
    public void testDeactivateBehaviorDetectionRule_WithHeaders() throws Exception {
        stubInvoke(new BehaviorRule());
        api.deactivateBehaviorDetectionRule("b4", Collections.singletonMap("X-D","d"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("d", hdrCap.getValue().get("X-D"));
    }

    @Test(expected = ApiException.class)
    public void testDeactivateBehaviorDetectionRule_MissingId() throws Exception {
        api.deactivateBehaviorDetectionRule(null);
    }

    // deleteBehaviorDetectionRule
    @Test
    public void testDeleteBehaviorDetectionRule_Success() throws Exception {
        stubVoidInvoke();
        api.deleteBehaviorDetectionRule("b5");

        verify(apiClient).invokeAPI(
            eq("/api/v1/behaviors/b5"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("b5");
    }

    @Test
    public void testDeleteBehaviorDetectionRule_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteBehaviorDetectionRule("b6", Collections.singletonMap("X-Del","x"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("x", hdrCap.getValue().get("X-Del"));
    }

    @Test(expected = ApiException.class)
    public void testDeleteBehaviorDetectionRule_MissingId() throws Exception {
        api.deleteBehaviorDetectionRule(null);
    }

    // getBehaviorDetectionRule
    @Test
    public void testGetBehaviorDetectionRule_Success() throws Exception {
        BehaviorRule expected = new BehaviorRule();
        stubInvoke(expected);

        BehaviorRule actual = api.getBehaviorDetectionRule("b7");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/behaviors/b7"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("b7");
    }

    @Test
    public void testGetBehaviorDetectionRule_WithHeaders() throws Exception {
        stubInvoke(new BehaviorRule());
        api.getBehaviorDetectionRule("b8", Collections.singletonMap("X-G","g"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("g", hdrCap.getValue().get("X-G"));
    }

    @Test(expected = ApiException.class)
    public void testGetBehaviorDetectionRule_MissingId() throws Exception {
        api.getBehaviorDetectionRule(null);
    }

    // listBehaviorDetectionRules
    @Test
    public void testListBehaviorDetectionRules_Success() throws Exception {
        List<BehaviorRule> expected = Arrays.asList(new BehaviorRule(), new BehaviorRule());
        stubInvoke(expected);

        List<BehaviorRule> actual = api.listBehaviorDetectionRules();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/behaviors"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListBehaviorDetectionRules_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listBehaviorDetectionRules(Collections.singletonMap("X-L","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdrCap.getValue().get("X-L"));
    }

    // replaceBehaviorDetectionRule
    @Test
    public void testReplaceBehaviorDetectionRule_Success() throws Exception {
        BehaviorRule body = new BehaviorRule();
        BehaviorRule expected = new BehaviorRule();
        stubInvoke(expected);

        BehaviorRule actual = api.replaceBehaviorDetectionRule("b9", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/behaviors/b9"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("b9");
    }

    @Test
    public void testReplaceBehaviorDetectionRule_WithHeaders() throws Exception {
        stubInvoke(new BehaviorRule());
        api.replaceBehaviorDetectionRule("b10", new BehaviorRule(), Collections.singletonMap("X-R","r"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("r", hdrCap.getValue().get("X-R"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceBehaviorDetectionRule_MissingId() throws Exception {
        api.replaceBehaviorDetectionRule(null, new BehaviorRule());
    }

    @Test(expected = ApiException.class)
    public void testReplaceBehaviorDetectionRule_MissingRule() throws Exception {
        api.replaceBehaviorDetectionRule("b11", null);
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
