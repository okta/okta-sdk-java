//package com.okta.sdk.resource.api;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.okta.sdk.resource.client.ApiClient;
//import com.okta.sdk.resource.client.ApiException;
//import com.okta.sdk.resource.client.Pair;
//import com.okta.sdk.resource.model.ListSubscriptionsRoleRoleRefParameter;
//import com.okta.sdk.resource.model.NotificationType;
//import com.okta.sdk.resource.model.Subscription;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.ArgumentCaptor;
//
//import java.lang.reflect.Method;
//import java.util.*;
//
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//
//@SuppressWarnings({"unchecked","rawtypes"})
//public class SubscriptionApiTest {
//
//    private ApiClient apiClient;
//    private SubscriptionApi api;
//
//    @Before
//    public void setUp() {
//        apiClient = mock(ApiClient.class);
//        api = new SubscriptionApi(apiClient);
//
//        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
//        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
//        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
//        when(apiClient.parameterToPair(anyString(), any())).thenReturn(new ArrayList<Pair>());
//    }
//
//    /* Stubs */
//    private void stubInvokeSubscription(Subscription value) throws ApiException {
//        when(apiClient.invokeAPI(
//            anyString(), eq("GET"),
//            anyList(), anyList(),
//            anyString(), any(),
//            anyMap(), anyMap(), anyMap(),
//            anyString(), anyString(),
//            any(String[].class), any(TypeReference.class)
//        )).thenReturn(value);
//    }
//
//    private void stubInvokeSubscriptionList(List<Subscription> value) throws ApiException {
//        when(apiClient.invokeAPI(
//            anyString(), eq("GET"),
//            anyList(), anyList(),
//            anyString(), any(),
//            anyMap(), anyMap(), anyMap(),
//            anyString(), anyString(),
//            any(String[].class), any(TypeReference.class)
//        )).thenReturn(value);
//    }
//
//    private void stubInvokeVoidPost() throws ApiException {
//        when(apiClient.invokeAPI(
//            anyString(), eq("POST"),
//            anyList(), anyList(),
//            anyString(), any(),
//            anyMap(), anyMap(), anyMap(),
//            anyString(), anyString(),
//            any(String[].class), isNull()
//        )).thenReturn(null);
//    }
//
//    private ListSubscriptionsRoleRoleRefParameter roleRefConst(final String v) {
//        return new ListSubscriptionsRoleRoleRefParameter() {
//            @Override public String toString() { return v; }
//        };
//    }
//
//    /* getSubscriptionsNotificationTypeRole */
//    @Test
//    public void testGetSubscriptionsNotificationTypeRole_Success() throws Exception {
//        Subscription expected = new Subscription();
//        stubInvokeSubscription(expected);
//        ListSubscriptionsRoleRoleRefParameter roleRef = roleRefConst("RREF1");
//        Subscription actual = api.getSubscriptionsNotificationTypeRole(roleRef, NotificationType.USER_IMPORT);
//        assertSame(expected, actual);
//        verify(apiClient).invokeAPI(
//            eq("/api/v1/roles/RREF1/subscriptions/USER_IMPORT"),
//            eq("GET"),
//            anyList(), anyList(),
//            anyString(), isNull(),
//            anyMap(), anyMap(), anyMap(),
//            eq("application/json"), anyString(),
//            any(String[].class), any(TypeReference.class)
//        );
//    }
//
//    @Test
//    public void testGetSubscriptionsNotificationTypeRole_WithHeaders() throws Exception {
//        stubInvokeSubscription(new Subscription());
//        ListSubscriptionsRoleRoleRefParameter roleRef = roleRefConst("RREF2");
//        api.getSubscriptionsNotificationTypeRole(roleRef, NotificationType.USER_IMPORT,
//            Collections.singletonMap("X-H","v"));
//        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
//        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), any(),
//            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
//        assertEquals("v", cap.getValue().get("X-H"));
//    }
//
//    @Test
//    public void testGetSubscriptionsNotificationTypeRole_MissingParams() {
//        expect400(() -> api.getSubscriptionsNotificationTypeRole(null, NotificationType.USER_IMPORT));
//        expect400(() -> api.getSubscriptionsNotificationTypeRole(roleRefConst("RREF3"), null));
//    }
//
//    /* getSubscriptionsNotificationTypeUser */
//    @Test
//    public void testGetSubscriptionsNotificationTypeUser_Success() throws Exception {
//        Subscription expected = new Subscription();
//        stubInvokeSubscription(expected);
//        Subscription actual = api.getSubscriptionsNotificationTypeUser(NotificationType.USER_IMPORT, "U1");
//        assertSame(expected, actual);
//        verify(apiClient).invokeAPI(
//            eq("/api/v1/users/U1/subscriptions/USER_IMPORT"),
//            eq("GET"),
//            anyList(), anyList(),
//            anyString(), isNull(),
//            anyMap(), anyMap(), anyMap(),
//            eq("application/json"), anyString(),
//            any(String[].class), any(TypeReference.class)
//        );
//    }
//
//    @Test
//    public void testGetSubscriptionsNotificationTypeUser_WithHeaders() throws Exception {
//        stubInvokeSubscription(new Subscription());
//        api.getSubscriptionsNotificationTypeUser(NotificationType.USER_IMPORT, "U2",
//            Collections.singletonMap("X-G","g"));
//        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
//        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), any(),
//            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
//        assertEquals("g", cap.getValue().get("X-G"));
//    }
//
//    @Test
//    public void testGetSubscriptionsNotificationTypeUser_MissingParams() {
//        expect400(() -> api.getSubscriptionsNotificationTypeUser(null, "U3"));
//        expect400(() -> api.getSubscriptionsNotificationTypeUser(NotificationType.USER_IMPORT, null));
//    }
//
//    /* listSubscriptionsRole */
//    @Test
//    public void testListSubscriptionsRole_Success() throws Exception {
//        List<Subscription> expected = Arrays.asList(new Subscription(), new Subscription());
//        stubInvokeSubscriptionList(expected);
//        List<Subscription> actual = api.listSubscriptionsRole(roleRefConst("RREF4"));
//        assertSame(expected, actual);
//        verify(apiClient).invokeAPI(
//            eq("/api/v1/roles/RREF4/subscriptions"),
//            eq("GET"),
//            anyList(), anyList(),
//            anyString(), isNull(),
//            anyMap(), anyMap(), anyMap(),
//            eq("application/json"), anyString(),
//            any(String[].class), any(TypeReference.class)
//        );
//    }
//
//    @Test
//    public void testListSubscriptionsRole_WithHeaders() throws Exception {
//        stubInvokeSubscriptionList(new ArrayList<>());
//        api.listSubscriptionsRole(roleRefConst("RREF5"), Collections.singletonMap("X-L","1"));
//        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
//        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), any(),
//            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
//        assertEquals("1", cap.getValue().get("X-L"));
//    }
//
//    @Test
//    public void testListSubscriptionsRole_MissingParam() {
//        expect400(() -> api.listSubscriptionsRole(null));
//    }
//
//    /* listSubscriptionsUser */
//    @Test
//    public void testListSubscriptionsUser_Success() throws Exception {
//        List<Subscription> expected = Collections.singletonList(new Subscription());
//        stubInvokeSubscriptionList(expected);
//        List<Subscription> actual = api.listSubscriptionsUser("U4");
//        assertSame(expected, actual);
//        verify(apiClient).invokeAPI(
//            eq("/api/v1/users/U4/subscriptions"),
//            eq("GET"),
//            anyList(), anyList(),
//            anyString(), isNull(),
//            anyMap(), anyMap(), anyMap(),
//            eq("application/json"), anyString(),
//            any(String[].class), any(TypeReference.class)
//        );
//    }
//
//    @Test
//    public void testListSubscriptionsUser_WithHeaders() throws Exception {
//        stubInvokeSubscriptionList(new ArrayList<>());
//        api.listSubscriptionsUser("U5", Collections.singletonMap("X-LU","z"));
//        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
//        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), any(),
//            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
//        assertEquals("z", cap.getValue().get("X-LU"));
//    }
//
//    @Test
//    public void testListSubscriptionsUser_MissingParam() {
//        expect400(() -> api.listSubscriptionsUser(null));
//    }
//
//    /* subscribeByNotificationTypeRole */
//    @Test
//    public void testSubscribeRole_Success() throws Exception {
//        stubInvokeVoidPost();
//        api.subscribeByNotificationTypeRole(roleRefConst("RREF6"), NotificationType.USER_IMPORT);
//        verify(apiClient).invokeAPI(
//            eq("/api/v1/roles/RREF6/subscriptions/USER_IMPORT/subscribe"),
//            eq("POST"),
//            anyList(), anyList(),
//            anyString(), isNull(),
//            anyMap(), anyMap(), anyMap(),
//            eq("application/json"), anyString(),
//            any(String[].class), isNull()
//        );
//    }
//
//    @Test
//    public void testSubscribeRole_WithHeaders() throws Exception {
//        stubInvokeVoidPost();
//        api.subscribeByNotificationTypeRole(roleRefConst("RREF7"), NotificationType.USER_IMPORT,
//            Collections.singletonMap("X-S","r"));
//        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
//        verify(apiClient).invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), any(),
//            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), isNull());
//        assertEquals("r", cap.getValue().get("X-S"));
//    }
//
//    @Test
//    public void testSubscribeRole_MissingParams() {
//        expect400(() -> api.subscribeByNotificationTypeRole(null, NotificationType.USER_IMPORT));
//        expect400(() -> api.subscribeByNotificationTypeRole(roleRefConst("RREF8"), null));
//    }
//
//    /* subscribeByNotificationTypeUser */
//    @Test
//    public void testSubscribeUser_Success() throws Exception {
//        stubInvokeVoidPost();
//        api.subscribeByNotificationTypeUser(NotificationType.USER_IMPORT, "U6");
//        verify(apiClient).invokeAPI(
//            eq("/api/v1/users/U6/subscriptions/USER_IMPORT/subscribe"),
//            eq("POST"),
//            anyList(), anyList(),
//            anyString(), isNull(),
//            anyMap(), anyMap(), anyMap(),
//            eq("application/json"), anyString(),
//            any(String[].class), isNull()
//        );
//    }
//
//    @Test
//    public void testSubscribeUser_WithHeaders() throws Exception {
//        stubInvokeVoidPost();
//        api.subscribeByNotificationTypeUser(NotificationType.USER_IMPORT, "U7",
//            Collections.singletonMap("X-SU","v"));
//        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
//        verify(apiClient).invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), any(),
//            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), isNull());
//        assertEquals("v", cap.getValue().get("X-SU"));
//    }
//
//    @Test
//    public void testSubscribeUser_MissingParams() {
//        expect400(() -> api.subscribeByNotificationTypeUser(null, "U8"));
//        expect400(() -> api.subscribeByNotificationTypeUser(NotificationType.USER_IMPORT, null));
//    }
//
//    /* unsubscribeByNotificationTypeRole */
//    @Test
//    public void testUnsubscribeRole_Success() throws Exception {
//        stubInvokeVoidPost();
//        api.unsubscribeByNotificationTypeRole(roleRefConst("RREF9"), NotificationType.USER_IMPORT);
//        verify(apiClient).invokeAPI(
//            eq("/api/v1/roles/RREF9/subscriptions/USER_IMPORT/unsubscribe"),
//            eq("POST"),
//            anyList(), anyList(),
//            anyString(), isNull(),
//            anyMap(), anyMap(), anyMap(),
//            eq("application/json"), anyString(),
//            any(String[].class), isNull()
//        );
//    }
//
//    @Test
//    public void testUnsubscribeRole_WithHeaders() throws Exception {
//        stubInvokeVoidPost();
//        api.unsubscribeByNotificationTypeRole(roleRefConst("RREF10"), NotificationType.USER_IMPORT,
//            Collections.singletonMap("X-UR","1"));
//        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
//        verify(apiClient).invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), any(),
//            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), isNull());
//        assertEquals("1", cap.getValue().get("X-UR"));
//    }
//
//    @Test
//    public void testUnsubscribeRole_MissingParams() {
//        expect400(() -> api.unsubscribeByNotificationTypeRole(null, NotificationType.USER_IMPORT));
//        expect400(() -> api.unsubscribeByNotificationTypeRole(roleRefConst("RREF11"), null));
//    }
//
//    /* unsubscribeByNotificationTypeUser */
//    @Test
//    public void testUnsubscribeUser_Success() throws Exception {
//        stubInvokeVoidPost();
//        api.unsubscribeByNotificationTypeUser(NotificationType.USER_IMPORT, "U9");
//        verify(apiClient).invokeAPI(
//            eq("/api/v1/users/U9/subscriptions/USER_IMPORT/unsubscribe"),
//            eq("POST"),
//            anyList(), anyList(),
//            anyString(), isNull(),
//            anyMap(), anyMap(), anyMap(),
//            eq("application/json"), anyString(),
//            any(String[].class), isNull()
//        );
//    }
//
//    @Test
//    public void testUnsubscribeUser_WithHeaders() throws Exception {
//        stubInvokeVoidPost();
//        api.unsubscribeByNotificationTypeUser(NotificationType.USER_IMPORT, "U10",
//            Collections.singletonMap("X-UU","h"));
//        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
//        verify(apiClient).invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), any(),
//            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), isNull());
//        assertEquals("h", cap.getValue().get("X-UU"));
//    }
//
//    @Test
//    public void testUnsubscribeUser_MissingParams() {
//        expect400(() -> api.unsubscribeByNotificationTypeUser(null, "U11"));
//        expect400(() -> api.unsubscribeByNotificationTypeUser(NotificationType.USER_IMPORT, null));
//    }
//
//    /* ApiException propagation */
//    @Test
//    public void testApiExceptionPropagates_GetRoleSubscription() throws Exception {
//        when(apiClient.invokeAPI(
//            anyString(), eq("GET"),
//            anyList(), anyList(),
//            anyString(), any(),
//            anyMap(), anyMap(), anyMap(),
//            anyString(), anyString(),
//            any(String[].class), any(TypeReference.class)
//        )).thenThrow(new ApiException(502,"bad"));
//        try {
//            api.getSubscriptionsNotificationTypeRole(roleRefConst("RREF12"), NotificationType.USER_IMPORT);
//            fail("Expected");
//        } catch (ApiException e) {
//            assertEquals(502, e.getCode());
//        }
//    }
//
//    @Test
//    public void testApiExceptionPropagates_SubscribeUser() throws Exception {
//        when(apiClient.invokeAPI(
//            anyString(), eq("POST"),
//            anyList(), anyList(),
//            anyString(), any(),
//            anyMap(), anyMap(), anyMap(),
//            anyString(), anyString(),
//            any(String[].class), isNull()
//        )).thenThrow(new ApiException(503,"down"));
//        try {
//            api.subscribeByNotificationTypeUser(NotificationType.USER_IMPORT, "UERR");
//            fail("Expected");
//        } catch (ApiException e) {
//            assertEquals(503, e.getCode());
//        }
//    }
//
//    /* Header negotiation */
//    @Test
//    public void testSelectHeaderAcceptCalled_GetRoleSubscription() throws Exception {
//        stubInvokeSubscription(new Subscription());
//        api.getSubscriptionsNotificationTypeRole(roleRefConst("RREF13"), NotificationType.USER_IMPORT);
//        verify(apiClient).selectHeaderAccept(any(String[].class),
//            eq("/api/v1/roles/RREF13/subscriptions/USER_IMPORT"));
//        verify(apiClient).selectHeaderContentType(any(String[].class));
//    }
//
//    @Test
//    public void testSelectHeaderAcceptCalled_SubscribeUser() throws Exception {
//        stubInvokeVoidPost();
//        api.subscribeByNotificationTypeUser(NotificationType.USER_IMPORT, "U14");
//        verify(apiClient).selectHeaderAccept(any(String[].class),
//            eq("/api/v1/users/U14/subscriptions/USER_IMPORT/subscribe"));
//        verify(apiClient).selectHeaderContentType(any(String[].class));
//    }
//
//    /* ObjectMapper configuration */
//    @Test
//    public void testGetObjectMapperConfiguration() throws Exception {
//        Method m = SubscriptionApi.class.getDeclaredMethod("getObjectMapper");
//        m.setAccessible(true);
//        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
//        assertNotNull(mapper);
//        assertFalse(mapper.getDeserializationConfig()
//            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
//        assertTrue(mapper.getDeserializationConfig()
//            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
//    }
//
//    /* helper */
//    private void expect400(Runnable r) {
//        try {
//            r.run();
//            fail("Expected 400");
//        } catch (ApiException e) {
//            assertEquals(400, e.getCode());
//        }
//    }
//}
