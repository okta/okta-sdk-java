package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.ApplicationFeature;
import com.okta.sdk.resource.model.ApplicationFeatureType;
import com.okta.sdk.resource.model.UpdateFeatureForApplicationRequest;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ApplicationFeaturesApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ApplicationFeaturesApi api;

    @BeforeMethod
    public void setUp() {
        apiClient = mock(ApiClient.class);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any())).thenReturn("application/json");

        api = new com.okta.sdk.resource.api.ApplicationFeaturesApi(apiClient);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetFeatureForApplication_success_withAdditionalHeaders() throws ApiException {
        ApplicationFeature feature = new ApplicationFeature();
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(feature);

        ApplicationFeatureType featureName = ApplicationFeatureType.values()[0];
        Map<String,String> headers = new HashMap<>();
        headers.put("X-Test", "abc");

        ApplicationFeature result = api.getFeatureForApplication("app1", featureName, headers);
        assertNotNull(result);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);

        verify(apiClient).invokeAPI(pathCap.capture(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertEquals(pathCap.getValue(), "/api/v1/apps/app1/features/" + featureName.toString());
        assertEquals(headerCap.getValue().get("X-Test"), "abc");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetFeatureForApplication_missingAppId() throws ApiException {
        api.getFeatureForApplication(null, ApplicationFeatureType.values()[0]);
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetFeatureForApplication_missingFeatureName() throws ApiException {
        api.getFeatureForApplication("app1", null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListFeaturesForApplication_success() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(Collections.singletonList(new ApplicationFeature()));

        List<ApplicationFeature> list = api.listFeaturesForApplication("app2");
        assertEquals(list.size(), 1);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertEquals(pathCap.getValue(), "/api/v1/apps/app2/features");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testListFeaturesForApplication_missingAppId() throws ApiException {
        api.listFeaturesForApplication(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateFeatureForApplication_success() throws ApiException {
        ApplicationFeature updated = new ApplicationFeature();
        UpdateFeatureForApplicationRequest body = new UpdateFeatureForApplicationRequest();
        ApplicationFeatureType featureName = ApplicationFeatureType.values()[0];

        when(apiClient.invokeAPI(anyString(), eq("PUT"), anyList(), anyList(), anyString(), eq(body),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(updated);

        ApplicationFeature result = api.updateFeatureForApplication("app3", featureName, body);
        assertNotNull(result);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);

        verify(apiClient).invokeAPI(pathCap.capture(), eq("PUT"), anyList(), anyList(), anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertEquals(pathCap.getValue(), "/api/v1/apps/app3/features/" + featureName.toString());
        assertSame(bodyCap.getValue(), body);
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUpdateFeatureForApplication_missingAppId() throws ApiException {
        api.updateFeatureForApplication(null, ApplicationFeatureType.values()[0], new UpdateFeatureForApplicationRequest());
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUpdateFeatureForApplication_missingFeatureName() throws ApiException {
        api.updateFeatureForApplication("app3", null, new UpdateFeatureForApplicationRequest());
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUpdateFeatureForApplication_missingBody() throws ApiException {
        api.updateFeatureForApplication("app3", ApplicationFeatureType.values()[0], null);
    }
}
