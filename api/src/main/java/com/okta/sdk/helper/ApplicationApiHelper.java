/*
 * Copyright 2023-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.hc.core5.http.HttpStatus;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Pair;
import org.openapitools.client.api.ApplicationApi;
import org.openapitools.client.model.*;

import java.lang.reflect.Type;
import java.util.*;

import static com.okta.sdk.helper.HelperConstants.*;
import static com.okta.sdk.helper.HelperUtil.getApplicationType;

/**
 * Helper class that enables working with subclassed {@link Application} references.
 */
public final class ApplicationApiHelper<T extends Application> extends ApplicationApi {

    public ApplicationApiHelper(ApplicationApi applicationApi) {
        super(applicationApi.getApiClient());
    }

    public ApplicationApiHelper(ApiClient apiClient) {
        super(apiClient);
    }

    public <T extends Application> T createApplicationOfType(Class<T> classType,
                                                             Application application,
                                                             Boolean activate,
                                                             String oktaAccessGatewayAgent) throws ApiException {

        ApiClient apiClient = getApiClient();

        // verify the required parameter 'application' is set
        if (application == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST,
                "Missing the required parameter 'application' when calling createApplication");
        }

        // create path and map variables
        String localVarPath = "/api/v1/apps";

        List<Pair> localVarQueryParams = new ArrayList<>(apiClient.parameterToPair("activate", activate));
        Map<String, String> localVarHeaderParams = new HashMap<>();

        if (oktaAccessGatewayAgent != null) {
            localVarHeaderParams.put("OktaAccessGateway-Agent", apiClient.parameterToString(oktaAccessGatewayAgent));
        }

        TypeReference<T> localVarReturnType = new TypeReference<T>() {
            @Override
            public Type getType() {
                return classType;
            }
        };

        return apiClient.invokeAPI(
            localVarPath,
            HttpMethod.POST.name(),
            localVarQueryParams,
            new ArrayList<>(),
            QUERY_STRING_JOINER.toString(),
            application,
            localVarHeaderParams,
            new HashMap<>(),
            new HashMap<>(),
            apiClient.selectHeaderAccept(MEDIA_TYPE),
            apiClient.selectHeaderContentType(MEDIA_TYPE),
            AUTH_NAMES,
            localVarReturnType
        );
    }

    @Override
    public T getApplication(String appId, String expand) throws ApiException {

        ApiClient apiClient = getApiClient();

        // verify the required parameter 'appId' is set
        if (appId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST,
                "Missing the required parameter 'appId' when calling getApplication");
        }

        // create path and map variables
        String localVarPath = "/api/v1/apps/{appId}"
            .replaceAll("\\{" + "appId" + "\\}", apiClient.escapeString(appId));

        List<Pair> localVarQueryParams = new ArrayList<>(apiClient.parameterToPair("expand", expand));

        TypeReference<T> localVarReturnType = new TypeReference<T>() { };

        T application = apiClient.invokeAPI(
            localVarPath,
            HttpMethod.GET.name(),
            localVarQueryParams,
            new ArrayList<>(),
            QUERY_STRING_JOINER.toString(),
            null,
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            apiClient.selectHeaderAccept(MEDIA_TYPE),
            apiClient.selectHeaderContentType(new String[] { }),
            AUTH_NAMES,
            localVarReturnType
        );

        return (T) getObjectMapper().convertValue(application, getApplicationType(application));
    }

    @Override
    public List<Application> listApplications(String q, String after, Integer limit, String filter,
                                              String expand, Boolean includeNonDeleted) throws ApiException {

        ApiClient apiClient = getApiClient();

        // create path and map variables
        String localVarPath = "/api/v1/apps";

        List<Pair> localVarQueryParams = new ArrayList<>();
        localVarQueryParams.addAll(apiClient.parameterToPair("q", q));
        localVarQueryParams.addAll(apiClient.parameterToPair("after", after));
        localVarQueryParams.addAll(apiClient.parameterToPair("limit", limit));
        localVarQueryParams.addAll(apiClient.parameterToPair("filter", filter));
        localVarQueryParams.addAll(apiClient.parameterToPair("expand", expand));
        localVarQueryParams.addAll(apiClient.parameterToPair("includeNonDeleted", includeNonDeleted));

        TypeReference<List<Application>> localVarReturnType = new TypeReference<List<Application>>() { };

        List<Application> applications = apiClient.invokeAPI(
            localVarPath,
            HttpMethod.GET.name(),
            localVarQueryParams,
            new ArrayList<>(),
            QUERY_STRING_JOINER.toString(),
            null,
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            apiClient.selectHeaderAccept(MEDIA_TYPE),
            apiClient.selectHeaderContentType(new String[] { }),
            AUTH_NAMES,
            localVarReturnType
        );

        List<Application> typedApplications = new ArrayList<>(applications.size());

        applications.forEach(application ->
            typedApplications.add(getObjectMapper().convertValue(application, getApplicationType(application))));

        return typedApplications;
    }

    public <T extends Application> T replaceApplicationOfType(Class<T> classType,
                                                              String appId,
                                                              Application application) throws ApiException {

        ApiClient apiClient = getApiClient();

        // verify the required parameter 'appId' is set
        if (appId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'appId' when calling replaceApplication");
        }

        // verify the required parameter 'application' is set
        if (application == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'application' when calling replaceApplication");
        }

        // create path and map variables
        String localVarPath = "/api/v1/apps/{appId}"
            .replaceAll("\\{" + "appId" + "\\}", apiClient.escapeString(appId));

        TypeReference<T> localVarReturnType = new TypeReference<T>() {
            @Override
            public Type getType() {
                return classType;
            }
        };

        T app = apiClient.invokeAPI(
            localVarPath,
            HttpMethod.PUT.name(),
            new ArrayList<>(),
            new ArrayList<>(),
            QUERY_STRING_JOINER.toString(),
            application,
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            apiClient.selectHeaderAccept(MEDIA_TYPE),
            apiClient.selectHeaderContentType(MEDIA_TYPE),
            AUTH_NAMES,
            localVarReturnType
        );

        return (T) getObjectMapper().convertValue(app, getApplicationType(app));
    }
}
