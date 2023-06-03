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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.commons.http.MediaType;
import org.apache.hc.core5.http.HttpStatus;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Pair;
import org.openapitools.client.api.PolicyApi;
import org.openapitools.client.model.*;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Helper class that enables working with sub-typed {@link Policy} references.
 */
public class PolicyApiHelper extends PolicyApi {

    public static <T extends Policy> T createPolicy(Class<T> classType, PolicyApi policyApi, Policy policy, Boolean activate) throws ApiException {

        ApiClient apiClient = policyApi.getApiClient();

        // verify the required parameter 'policy' is set
        if (policy == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'policy' when calling createPolicy");
        }

        // create path and map variables
        String localVarPath = "/api/v1/policies";

        StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        List<Pair> localVarQueryParams = new ArrayList<>(apiClient.parameterToPair("activate", activate));
        List<Pair> localVarCollectionQueryParams = new ArrayList<>();
        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, String> localVarCookieParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();

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
        Policy createdPolicy = apiClient.invokeAPI(
            localVarPath,
            HttpMethod.POST.name(),
            localVarQueryParams,
            localVarCollectionQueryParams,
            localVarQueryStringJoiner.toString(),
            policy,
            localVarHeaderParams,
            localVarCookieParams,
            localVarFormParams,
            localVarAccept,
            localVarContentType,
            localVarAuthNames,
            localVarReturnType
        );

        return (T) createdPolicy;
    }

    public static <T extends Policy> T getPolicy(PolicyApi policyApi, String policyId, String expand) throws ApiException {

        ApiClient apiClient = policyApi.getApiClient();

        // verify the required parameter 'policyId' is set
        if (policyId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'policyId' when calling getPolicy");
        }

        // create path and map variables
        String localVarPath = "/api/v1/policies/{policyId}"
            .replaceAll("\\{" + "policyId" + "\\}", apiClient.escapeString(policyId));

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

        TypeReference<Policy> localVarReturnType = new TypeReference<Policy>() {
        };
        Policy policy = apiClient.invokeAPI(
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

    public static List<Policy> listPolicies(PolicyApi policyApi, String type, String status, String expand) throws ApiException {

        ApiClient apiClient = policyApi.getApiClient();

        // verify the required parameter 'type' is set
        if (type == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'type' when calling listPolicies");
        }

        // create path and map variables
        String localVarPath = "/api/v1/policies";

        StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        List<Pair> localVarQueryParams = new ArrayList<>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<>();
        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, String> localVarCookieParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();

        localVarQueryParams.addAll(apiClient.parameterToPair("type", type));
        localVarQueryParams.addAll(apiClient.parameterToPair("status", status));
        localVarQueryParams.addAll(apiClient.parameterToPair("expand", expand));

        final String[] localVarAccepts = { MediaType.APPLICATION_JSON_VALUE };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

        final String[] localVarContentTypes = { };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "apiToken", "oauth2" };

        TypeReference<List<Policy>> localVarReturnType = new TypeReference<List<Policy>>() {
        };
        List<Policy> policies = apiClient.invokeAPI(
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

    public static <T extends PolicyRule> T createPolicyRule(Class<T> classType, PolicyApi policyApi, String policyId, PolicyRule policyRule) throws ApiException {

        ApiClient apiClient = policyApi.getApiClient();

        // verify the required parameter 'policyId' is set
        if (policyId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'policyId' when calling createPolicyRule");
        }

        // verify the required parameter 'policyRule' is set
        if (policyRule == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'policyRule' when calling createPolicyRule");
        }

        // create path and map variables
        String localVarPath = "/api/v1/policies/{policyId}/rules"
            .replaceAll("\\{" + "policyId" + "\\}", apiClient.escapeString(policyId));

        StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        List<Pair> localVarQueryParams = new ArrayList<>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<>();
        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, String> localVarCookieParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();

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

        PolicyRule createdPolicyRule = apiClient.invokeAPI(
            localVarPath,
            HttpMethod.POST.name(),
            localVarQueryParams,
            localVarCollectionQueryParams,
            localVarQueryStringJoiner.toString(),
            policyRule,
            localVarHeaderParams,
            localVarCookieParams,
            localVarFormParams,
            localVarAccept,
            localVarContentType,
            localVarAuthNames,
            localVarReturnType
        );

        return (T) createdPolicyRule;
    }
}