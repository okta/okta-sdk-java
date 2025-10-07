package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.SchemaApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.GroupSchema;
import com.okta.sdk.resource.model.LogStreamSchema;
import com.okta.sdk.resource.model.LogStreamType;
import com.okta.sdk.resource.model.UserSchema;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class SchemaApiTest {

    private ApiClient apiClient;
    private SchemaApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new SchemaApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    /* Stubs */
    private void stubInvokeUserSchema(UserSchema value, String method) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq(method),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubInvokeGroupSchema(GroupSchema value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubInvokeLogStreamSchema(LogStreamSchema value, String method) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq(method),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubInvokeLogStreamSchemaList(List<LogStreamSchema> value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    /* getApplicationUserSchema */
    @Test
    public void testGetApplicationUserSchema_Success() throws Exception {
        UserSchema expected = new UserSchema();
        stubInvokeUserSchema(expected, "GET");

        UserSchema actual = api.getApplicationUserSchema("APP1");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/schemas/apps/APP1/default"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetApplicationUserSchema_WithHeaders() throws Exception {
        stubInvokeUserSchema(new UserSchema(), "GET");
        Map<String,String> hdr = Collections.singletonMap("X-A","v");
        api.getApplicationUserSchema("APP2", hdr);

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), any(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-A"));
    }

    @Test
    public void testGetApplicationUserSchema_MissingParam() {
        expect400(() -> api.getApplicationUserSchema(null));
    }

    @Test
    public void testGetLogStreamSchema_WithHeaders() throws Exception {
        stubInvokeLogStreamSchema(new LogStreamSchema(), "GET");
        api.getLogStreamSchema(LogStreamType.SPLUNK_CLOUD_LOGSTREAMING,
            Collections.singletonMap("X-LS","h"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), any(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("h", cap.getValue().get("X-LS"));
    }

    @Test
    public void testGetLogStreamSchema_MissingParam() {
        expect400(() -> api.getLogStreamSchema(null));
    }

    /* getUserSchema */
    @Test
    public void testGetUserSchema_Success() throws Exception {
        UserSchema expected = new UserSchema();
        stubInvokeUserSchema(expected, "GET");
        UserSchema actual = api.getUserSchema("default");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/schemas/user/default"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetUserSchema_MissingParam() {
        expect400(() -> api.getUserSchema(null));
    }

    /* listLogStreamSchemas */
    @Test
    public void testListLogStreamSchemas_Success() throws Exception {
        List<LogStreamSchema> expected = Arrays.asList(new LogStreamSchema(), new LogStreamSchema());
        stubInvokeLogStreamSchemaList(expected);
        List<LogStreamSchema> actual = api.listLogStreamSchemas();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/schemas/logStream"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testListLogStreamSchemas_WithHeaders() throws Exception {
        stubInvokeLogStreamSchemaList(new ArrayList<>());
        api.listLogStreamSchemas(Collections.singletonMap("X-LIST","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), any(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-LIST"));
    }

    /* updateApplicationUserProfile */
    @Test
    public void testUpdateApplicationUserProfile_Success() throws Exception {
        UserSchema body = new UserSchema();
        UserSchema expected = new UserSchema();
        stubInvokeUserSchema(expected, "POST");

        UserSchema actual = api.updateApplicationUserProfile("APP3", body);
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/schemas/apps/APP3/default"),
            eq("POST"),
            anyList(), anyList(),
            anyString(), eq(body),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testUpdateApplicationUserProfile_WithHeaders() throws Exception {
        stubInvokeUserSchema(new UserSchema(), "POST");
        api.updateApplicationUserProfile("APP4", new UserSchema(),
            Collections.singletonMap("X-U","y"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), any(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("y", cap.getValue().get("X-U"));
    }

    @Test
    public void testUpdateApplicationUserProfile_MissingParam() {
        expect400(() -> api.updateApplicationUserProfile(null, new UserSchema()));
    }

    /* updateGroupSchema */
    @Test
    public void testUpdateGroupSchema_Success() throws Exception {
        GroupSchema body = new GroupSchema();
        GroupSchema expected = new GroupSchema();
        stubInvokeGroupSchema(expected);

        GroupSchema actual = api.updateGroupSchema(body);
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/schemas/group/default"),
            eq("POST"),
            anyList(), anyList(),
            anyString(), eq(body),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testUpdateGroupSchema_WithHeaders() throws Exception {
        stubInvokeGroupSchema(new GroupSchema());
        api.updateGroupSchema(new GroupSchema(), Collections.singletonMap("X-G","g1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), any(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("g1", cap.getValue().get("X-G"));
    }

    /* updateUserProfile */
    @Test
    public void testUpdateUserProfile_Success() throws Exception {
        UserSchema body = new UserSchema();
        UserSchema expected = new UserSchema();
        stubInvokeUserSchema(expected, "POST");

        UserSchema actual = api.updateUserProfile("customType", body);
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/schemas/user/customType"),
            eq("POST"),
            anyList(), anyList(),
            anyString(), eq(body),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testUpdateUserProfile_MissingParams() {
        expect400(() -> api.updateUserProfile(null, new UserSchema()));
        expect400(() -> api.updateUserProfile("default", null));
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_GetUserSchema() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"bad"));
        try {
            api.getUserSchema("default");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
        }
    }

    @Test
    public void testApiExceptionPropagates_UpdateUserProfile() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"err"));
        try {
            api.updateUserProfile("default", new UserSchema());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_GetApplicationUserSchema() throws Exception {
        stubInvokeUserSchema(new UserSchema(), "GET");
        api.getApplicationUserSchema("APP5");
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/meta/schemas/apps/APP5/default"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_UpdateUserProfile() throws Exception {
        stubInvokeUserSchema(new UserSchema(), "POST");
        api.updateUserProfile("default", new UserSchema());
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/meta/schemas/user/default"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = SchemaApi.class.getDeclaredMethod("getObjectMapper");
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
