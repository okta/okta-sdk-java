package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.CreateGroupRuleRequest;
import com.okta.sdk.resource.model.GroupRule;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class GroupRuleApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.GroupRuleApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.GroupRuleApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(Collections.emptyList());
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

    /* activateGroupRule */
    @Test
    public void testActivateGroupRule_Success() throws Exception {
        stubVoidInvoke();
        api.activateGroupRule("r1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/rules/r1/lifecycle/activate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("r1");
    }

    @Test
    public void testActivateGroupRule_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.activateGroupRule("r2", Collections.singletonMap("X-A","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("1", cap.getValue().get("X-A"));
    }

    @Test
    public void testActivateGroupRule_MissingId() {
        try {
            api.activateGroupRule(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("groupruleid"));
        }
    }

    /* createGroupRule */
    @Test
    public void testCreateGroupRule_Success() throws Exception {
        GroupRule expected = new GroupRule();
        stubInvoke(expected);
        CreateGroupRuleRequest body = new CreateGroupRuleRequest();
        GroupRule actual = api.createGroupRule(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/rules"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateGroupRule_WithHeaders() throws Exception {
        stubInvoke(new GroupRule());
        api.createGroupRule(new CreateGroupRuleRequest(), Collections.singletonMap("X-C","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-C"));
    }

    @Test
    public void testCreateGroupRule_MissingBody() {
        try {
            api.createGroupRule(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("grouprule"));
        }
    }

    /* deactivateGroupRule */
    @Test
    public void testDeactivateGroupRule_Success() throws Exception {
        stubVoidInvoke();
        api.deactivateGroupRule("r3");
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/rules/r3/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("r3");
    }

    @Test
    public void testDeactivateGroupRule_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deactivateGroupRule("r4", Collections.singletonMap("X-D","x"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("x", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeactivateGroupRule_MissingId() {
        try {
            api.deactivateGroupRule(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("groupruleid"));
        }
    }

    /* deleteGroupRule */
    @Test
    public void testDeleteGroupRule_Success() throws Exception {
        stubVoidInvoke();
        api.deleteGroupRule("r5", true);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/rules/r5"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).parameterToPair("removeUsers", true);
        verify(apiClient).escapeString("r5");
    }

    @Test
    public void testDeleteGroupRule_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteGroupRule("r6", false, Collections.singletonMap("X-Del","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("1", cap.getValue().get("X-Del"));
    }

    @Test
    public void testDeleteGroupRule_MissingId() {
        try {
            api.deleteGroupRule(null, true);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* getGroupRule */
    @Test
    public void testGetGroupRule_Success() throws Exception {
        GroupRule expected = new GroupRule();
        stubInvoke(expected);
        GroupRule actual = api.getGroupRule("r8", "groupIdToGroupNameMap");
        assertSame(expected, actual);
        verify(apiClient).parameterToPair("expand","groupIdToGroupNameMap");
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/rules/r8"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("r8");
    }

    @Test
    public void testGetGroupRule_WithHeaders() throws Exception {
        stubInvoke(new GroupRule());
        api.getGroupRule("r9", null, Collections.singletonMap("X-G","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-G"));
    }

    @Test
    public void testGetGroupRule_MissingId() {
        try {
            api.getGroupRule(null, null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* listGroupRules */
    @Test
    public void testListGroupRules_Success_AllParams() throws Exception {
        List<GroupRule> expected = Arrays.asList(new GroupRule());
        stubInvoke(expected);
        List<GroupRule> actual = api.listGroupRules(25,"after1","name eq \"X\"","groupIdToGroupNameMap");
        assertSame(expected, actual);

        verify(apiClient).parameterToPair("limit",25);
        verify(apiClient).parameterToPair("after","after1");
        verify(apiClient).parameterToPair("search","name eq \"X\"");
        verify(apiClient).parameterToPair("expand","groupIdToGroupNameMap");
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/rules"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListGroupRules_Success_Minimal() throws Exception {
        stubInvoke(Collections.emptyList());
        List<GroupRule> list = api.listGroupRules(null,null,null,null);
        assertNotNull(list);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/rules"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListGroupRules_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listGroupRules(null,null,null,null, Collections.singletonMap("X-L","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-L"));
    }

    /* replaceGroupRule */
    @Test
    public void testReplaceGroupRule_Success() throws Exception {
        GroupRule expected = new GroupRule();
        stubInvoke(expected);
        GroupRule body = new GroupRule();
        GroupRule actual = api.replaceGroupRule("r10", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/rules/r10"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("r10");
    }

    @Test
    public void testReplaceGroupRule_WithHeaders() throws Exception {
        stubInvoke(new GroupRule());
        api.replaceGroupRule("r11", new GroupRule(), Collections.singletonMap("X-R","y"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("y", cap.getValue().get("X-R"));
    }

    @Test
    public void testReplaceGroupRule_MissingId() {
        try {
            api.replaceGroupRule(null, new GroupRule());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("groupruleid"));
        }
    }

    @Test
    public void testReplaceGroupRule_MissingBody() {
        try {
            api.replaceGroupRule("r12", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("grouprule"));
        }
    }

    /* ApiException propagation */
    @Test
    public void testCreateGroupRule_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"boom"));
        try {
            api.createGroupRule(new CreateGroupRuleRequest());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertTrue(ex.getMessage().contains("boom"));
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.GroupRuleApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
