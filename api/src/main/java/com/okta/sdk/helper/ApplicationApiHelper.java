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

import com.okta.commons.http.MediaType;
import org.apache.hc.core5.http.HttpStatus;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Pair;
import org.openapitools.client.api.ApplicationApi;
import org.openapitools.client.model.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Helper class that enables working with sub-typed {@link Application} references.
 */
public class ApplicationApiHelper extends ApplicationApi {

    public static <T extends Application> T createApplication(Class<T> classType, ApplicationApi applicationApi, Application application, Boolean activate, String oktaAccessGatewayAgent) throws ApiException {

        ApiClient apiClient = applicationApi.getApiClient();

        // verify the required parameter 'application' is set
        if (application == null) {
            throw new ApiException(400, "Missing the required parameter 'application' when calling createApplication");
        }

        // create path and map variables
        String localVarPath = "/api/v1/apps";

        StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        List<Pair> localVarQueryParams = new ArrayList<>(apiClient.parameterToPair("activate", activate));
        List<Pair> localVarCollectionQueryParams = new ArrayList<>();
        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, String> localVarCookieParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();

        if (oktaAccessGatewayAgent != null) {
            localVarHeaderParams.put("OktaAccessGateway-Agent", apiClient.parameterToString(oktaAccessGatewayAgent));
        }

        final String[] localVarAccepts = { MediaType.APPLICATION_JSON_VALUE };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

        final String[] localVarContentTypes = { MediaType.APPLICATION_JSON_VALUE };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "apiToken", "oauth2" };

        TypeReference<T> localVarReturnType = new TypeReference<T>() {
            @Override
            public Type getType() {
                return classType;
            }
        };

        Application createdApplication = apiClient.invokeAPI(
            localVarPath,
            HttpMethod.POST.name(),
            localVarQueryParams,
            localVarCollectionQueryParams,
            localVarQueryStringJoiner.toString(),
            application,
            localVarHeaderParams,
            localVarCookieParams,
            localVarFormParams,
            localVarAccept,
            localVarContentType,
            localVarAuthNames,
            localVarReturnType
        );

        return (T) createdApplication;
    }

    public static <T extends Application> T getApplication(ApplicationApi applicationApi, String appId, String expand) throws ApiException {

        ApiClient apiClient = applicationApi.getApiClient();

        // verify the required parameter 'appId' is set
        if (appId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'appId' when calling getApplication");
        }

        // create path and map variables
        String localVarPath = "/api/v1/apps/{appId}"
            .replaceAll("\\{" + "appId" + "\\}", apiClient.escapeString(appId));

        StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        List<Pair> localVarQueryParams = new ArrayList<>(apiClient.parameterToPair("expand", expand));
        List<Pair> localVarCollectionQueryParams = new ArrayList<>();
        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, String> localVarCookieParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();

        final String[] localVarAccepts = { MediaType.APPLICATION_JSON_VALUE };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

        final String[] localVarContentTypes = { };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "apiToken", "oauth2" };

        TypeReference<T> localVarReturnType = new TypeReference<T>() { };
        Application application = apiClient.invokeAPI(
            localVarPath,
            HttpMethod.GET.name(),
            localVarQueryParams,
            localVarCollectionQueryParams,
            localVarQueryStringJoiner.toString(),
            null,
            localVarHeaderParams,
            localVarCookieParams,
            localVarFormParams,
            localVarAccept,
            localVarContentType,
            localVarAuthNames,
            localVarReturnType
        );

        ObjectMapper objectMapper = getObjectMapper();

        ApplicationSignOnMode applicationSignOnMode = application.getSignOnMode();

        switch (Objects.requireNonNull(applicationSignOnMode)) {
            case AUTO_LOGIN:
                return (T) objectMapper.convertValue(application, AutoLoginApplication.class);
            case BASIC_AUTH:
                return (T) objectMapper.convertValue(application, BasicAuthApplication.class);
            case BOOKMARK:
                return (T) objectMapper.convertValue(application, BookmarkApplication.class);
            case BROWSER_PLUGIN:
                return (T) objectMapper.convertValue(application, BrowserPluginApplication.class);
            case OPENID_CONNECT:
                return (T) objectMapper.convertValue(application, OpenIdConnectApplication.class);
            case SAML_1_1:
            case SAML_2_0:
                return (T) objectMapper.convertValue(application, SamlApplication.class);
            case SECURE_PASSWORD_STORE:
                return (T) objectMapper.convertValue(application, SecurePasswordStoreApplication.class);
            case WS_FEDERATION:
                return (T) objectMapper.convertValue(application, WsFederationApplication.class);
        }

        return (T) application;
    }

    public static <T extends Application> T replaceApplication(Class<?> clazz, ApiClient apiClient, String appId, Application application) throws ApiException {
        ApplicationApi applicationApi = new ApplicationApi(apiClient);
        return (T) getObjectMapper().convertValue(applicationApi.replaceApplication(appId, application), clazz);
    }

    public static List<Application> listApplications(ApiClient apiClient, String q, String after, Integer limit, String filter, String expand, Boolean includeNonDeleted) throws ApiException {

        // create path and map variables
        String localVarPath = "/api/v1/apps";

        StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        List<Pair> localVarQueryParams = new ArrayList<>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<>();
        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, String> localVarCookieParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();

        localVarQueryParams.addAll(apiClient.parameterToPair("q", q));
        localVarQueryParams.addAll(apiClient.parameterToPair("after", after));
        localVarQueryParams.addAll(apiClient.parameterToPair("limit", limit));
        localVarQueryParams.addAll(apiClient.parameterToPair("filter", filter));
        localVarQueryParams.addAll(apiClient.parameterToPair("expand", expand));
        localVarQueryParams.addAll(apiClient.parameterToPair("includeNonDeleted", includeNonDeleted));

        final String[] localVarAccepts = { MediaType.APPLICATION_JSON_VALUE };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

        final String[] localVarContentTypes = { };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "apiToken", "oauth2" };

        TypeReference<List<Application>> localVarReturnType = new TypeReference<List<Application>>() { };

        List<Application> applications = apiClient.invokeAPI(
            localVarPath,
            HttpMethod.GET.name(),
            localVarQueryParams,
            localVarCollectionQueryParams,
            localVarQueryStringJoiner.toString(),
            null,
            localVarHeaderParams,
            localVarCookieParams,
            localVarFormParams,
            localVarAccept,
            localVarContentType,
            localVarAuthNames,
            localVarReturnType
        );

        ObjectMapper objectMapper = getObjectMapper();

        List<Application> typedApplications = new ArrayList<>(applications.size());

        for (Application application : applications) {
            switch (Objects.requireNonNull(application.getSignOnMode())) {
                case AUTO_LOGIN:
                    typedApplications.add(objectMapper.convertValue(application, AutoLoginApplication.class));
                    break;
                case BASIC_AUTH:
                    typedApplications.add(objectMapper.convertValue(application, BasicAuthApplication.class));
                    break;
                case BOOKMARK:
                    typedApplications.add(objectMapper.convertValue(application, BookmarkApplication.class));
                    break;
                case BROWSER_PLUGIN:
                    typedApplications.add(objectMapper.convertValue(application, BrowserPluginApplication.class));
                    break;
                case OPENID_CONNECT:
                    typedApplications.add(objectMapper.convertValue(application, OpenIdConnectApplication.class));
                    break;
                case SAML_1_1:
                case SAML_2_0:
                    typedApplications.add(objectMapper.convertValue(application, SamlApplication.class));
                    break;
                case SECURE_PASSWORD_STORE:
                    typedApplications.add(objectMapper.convertValue(application, SecurePasswordStoreApplication.class));
                    break;
                case WS_FEDERATION:
                    typedApplications.add(objectMapper.convertValue(application, WsFederationApplication.class));
                    break;
            }
        }
        return typedApplications;
    }
}