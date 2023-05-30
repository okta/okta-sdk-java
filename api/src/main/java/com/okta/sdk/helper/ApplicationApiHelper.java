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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.client.ApiClient;
import org.openapitools.client.api.ApplicationApi;
import org.openapitools.client.model.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * Helper class that provide typed Application references.
 */
public class ApplicationApiHelper extends ApplicationApi {

    public static <T extends Application> T getApplication(ApiClient apiClient, String appId, String expand) throws RestClientException {

        // verify the required parameter 'appId' is set ''
        if (appId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'appId' when calling getApplication");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("appId", appId);

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "expand", expand));

        final String[] localVarAccepts = {
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "apiToken", "oauth2" };

        ParameterizedTypeReference<T> localReturnType = new ParameterizedTypeReference<T>() {};
        ResponseEntity<T> respEntity = apiClient.invokeAPI("/api/v1/apps/{appId}", HttpMethod.GET, uriVariables,
            localVarQueryParams, null, localVarHeaderParams, localVarCookieParams, localVarFormParams,
            localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
        ObjectMapper objectMapper = getObjectMapper();

        Application application = respEntity.getBody();
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

    public static List<Application> listApplications(ApiClient apiClient, String q, String after, Integer limit, String filter, String expand, Boolean includeNonDeleted) throws RestClientException {

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "q", q));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "after", after));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "limit", limit));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "filter", filter));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "expand", expand));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "includeNonDeleted", includeNonDeleted));

        final String[] localVarAccepts = {
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "apiToken", "oauth2" };

        ParameterizedTypeReference<List<Application>> localReturnType = new ParameterizedTypeReference<List<Application>>() {};
        List<Application> applications = apiClient.invokeAPI("/api/v1/apps", HttpMethod.GET, Collections.emptyMap(),
            localVarQueryParams, null, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept,
            localVarContentType, localVarAuthNames, localReturnType).getBody();
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