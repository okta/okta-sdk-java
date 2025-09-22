package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.Feature;
import com.okta.sdk.resource.model.FeatureLifecycle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class FeatureApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.FeatureApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.FeatureApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(Collections.emptyList());
    }

    private <T> void stubInvoke(T value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    /* getFeature */
    @Test
    public void testGetFeature_Success() throws Exception {
        Feature expected = new Feature();
        stubInvoke(expected);

        Feature actual = api.getFeature("feat1");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/features/feat1"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("feat1");
    }

    @Test
    public void testGetFeature_WithHeaders() throws Exception {
        stubInvoke(new Feature());
        Map<String,String> hdrs = Collections.singletonMap("X-H","1");
        api.getFeature("feat2", hdrs);

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-H"));
    }

    @Test
    public void testGetFeature_MissingId() {
        try {
            api.getFeature(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("featureId"));
        }
    }

    /* listFeatureDependencies */
    @Test
    public void testListFeatureDependencies_Success() throws Exception {
        List<Feature> expected = Arrays.asList(new Feature());
        stubInvoke(expected);

        List<Feature> actual = api.listFeatureDependencies("feat3");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/features/feat3/dependencies"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("feat3");
    }

    @Test
    public void testListFeatureDependencies_MissingId() {
        try {
            api.listFeatureDependencies(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("featureId"));
        }
    }

    /* listFeatureDependents */
    @Test
    public void testListFeatureDependents_Success() throws Exception {
        List<Feature> expected = Arrays.asList(new Feature());
        stubInvoke(expected);

        List<Feature> actual = api.listFeatureDependents("feat4");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/features/feat4/dependents"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("feat4");
    }

    @Test
    public void testListFeatureDependents_MissingId() {
        try {
            api.listFeatureDependents(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("featureId"));
        }
    }

    /* listFeatures */
    @Test
    public void testListFeatures_Success() throws Exception {
        List<Feature> expected = Arrays.asList(new Feature());
        stubInvoke(expected);

        List<Feature> actual = api.listFeatures();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/features"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListFeatures_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listFeatures(Collections.singletonMap("X-L","v"));

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-L"));
    }

    /* updateFeatureLifecycle */
    @Test
    public void testUpdateFeatureLifecycle_Success_WithMode() throws Exception {
        Feature expected = new Feature();
        stubInvoke(expected);

        Feature actual = api.updateFeatureLifecycle("feat5", FeatureLifecycle.ENABLE, "force");
        assertSame(expected, actual);

        verify(apiClient).parameterToPair("mode", "force");
        verify(apiClient).invokeAPI(
            eq("/api/v1/features/feat5/ENABLE"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("feat5");
        verify(apiClient).escapeString("ENABLE");
    }

    @Test
    public void testUpdateFeatureLifecycle_WithHeaders() throws Exception {
        stubInvoke(new Feature());
        api.updateFeatureLifecycle("feat6", FeatureLifecycle.DISABLE, null,
            Collections.singletonMap("X-U","1"));

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-U"));
    }

    @Test
    public void testUpdateFeatureLifecycle_MissingFeatureId() {
        try {
            api.updateFeatureLifecycle(null, FeatureLifecycle.ENABLE, null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("featureId"));
        }
    }

    @Test
    public void testUpdateFeatureLifecycle_MissingLifecycle() {
        try {
            api.updateFeatureLifecycle("feat7", null, null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("lifecycle"));
        }
    }

    /* ApiException propagation */
    @Test
    public void testGetFeature_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"err"));
        try {
            api.getFeature("featX");
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertTrue(ex.getMessage().contains("err"));
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.FeatureApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
