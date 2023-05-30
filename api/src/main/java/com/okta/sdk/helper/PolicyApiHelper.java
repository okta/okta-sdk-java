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
import org.openapitools.client.api.PolicyApi;
import org.openapitools.client.model.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.*;

/**
 * Helper class that provide typed Policy references.
 */
public class PolicyApiHelper extends PolicyApi {

    public static <T extends Policy> T getPolicy(ApiClient apiClient, String policyId, String expand) throws RestClientException {

        // verify the required parameter 'policyId' is set ''
        if (policyId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'policyId' when calling getPolicy");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("policyId", policyId);

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "expand", expand));

        final String[] localVarAccepts = {
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "apiToken", "oauth2" };

        ParameterizedTypeReference<T> localReturnType = new ParameterizedTypeReference<T>() {};
        ResponseEntity<T> respEntity = apiClient.invokeAPI("/api/v1/policies/{policyId}", HttpMethod.GET, uriVariables, localVarQueryParams, null, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
        ObjectMapper objectMapper = getObjectMapper();
        Policy policy = respEntity.getBody();
        PolicyType policyType = policy.getType();

        switch (Objects.requireNonNull(policyType)) {
            case ACCESS_POLICY:
                return (T) objectMapper.convertValue(policy, AccessPolicy.class);
            case IDP_DISCOVERY:
                return (T) objectMapper.convertValue(policy, IdentityProviderPolicy.class);
            case MFA_ENROLL:
                return (T) objectMapper.convertValue(policy, MultifactorEnrollmentPolicy.class);
            case OAUTH_AUTHORIZATION_POLICY:
                return (T) objectMapper.convertValue(policy, AuthorizationServerPolicy.class);
            case OKTA_SIGN_ON:
                return (T) objectMapper.convertValue(policy, OktaSignOnPolicy.class);
            case PASSWORD:
                return (T) objectMapper.convertValue(policy, PasswordPolicy.class);
            case PROFILE_ENROLLMENT:
                return (T) objectMapper.convertValue(policy, ProfileEnrollmentPolicy.class);
        }

        return (T) policy;
    }

    public static List<Policy> listPolicies(ApiClient apiClient, String type, String status, String expand) throws RestClientException {

        // verify the required parameter 'type' is set ''
        if (type == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'type' when calling listPolicies");
        }


        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "type", type));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "status", status));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "expand", expand));

        final String[] localVarAccepts = {
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "apiToken", "oauth2" };

        ParameterizedTypeReference<List<Policy>> localReturnType = new ParameterizedTypeReference<List<Policy>>() {};
        List<Policy> policies = apiClient.invokeAPI("/api/v1/policies", HttpMethod.GET, Collections.<String, Object>emptyMap(),
            localVarQueryParams, null, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept,
            localVarContentType, localVarAuthNames, localReturnType).getBody();
        ObjectMapper objectMapper = getObjectMapper();

        List<Policy> typedPolicies = new ArrayList<>(policies.size());

        for (Policy policy : policies) {
            switch (Objects.requireNonNull(policy.getType())) {
                case ACCESS_POLICY:
                    typedPolicies.add(objectMapper.convertValue(policy, AccessPolicy.class));
                    break;
                case IDP_DISCOVERY:
                    typedPolicies.add(objectMapper.convertValue(policy, IdentityProviderPolicy.class));
                    break;
                case MFA_ENROLL:
                    typedPolicies.add(objectMapper.convertValue(policy, MultifactorEnrollmentPolicy.class));
                    break;
                case OAUTH_AUTHORIZATION_POLICY:
                    typedPolicies.add(objectMapper.convertValue(policy, AuthorizationServerPolicy.class));
                    break;
                case OKTA_SIGN_ON:
                    typedPolicies.add(objectMapper.convertValue(policy, OktaSignOnPolicy.class));
                    break;
                case PASSWORD:
                    typedPolicies.add(objectMapper.convertValue(policy, PasswordPolicy.class));
                    break;
                case PROFILE_ENROLLMENT:
                    typedPolicies.add(objectMapper.convertValue(policy, ProfileEnrollmentPolicy.class));
                    break;
            }
        }

        return typedPolicies;
    }
}