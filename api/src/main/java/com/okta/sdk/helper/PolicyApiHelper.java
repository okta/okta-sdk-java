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
import org.openapitools.client.api.PolicyApi;
import org.openapitools.client.model.*;

import java.lang.reflect.Type;
import java.util.*;

import static com.okta.sdk.helper.HelperConstants.*;
import static com.okta.sdk.helper.HelperUtil.getPolicyType;

/**
 * Helper class that enables working with sub-typed {@link Policy} references.
 */
public class PolicyApiHelper<T extends Policy> extends PolicyApi {

    public PolicyApiHelper(PolicyApi policyApi) {
        super(policyApi.getApiClient());
    }

    public PolicyApiHelper(ApiClient apiClient) {
        super(apiClient);
    }

    public <T extends Policy> T createPolicyOfType(Class<T> classType,
                                                   Policy policy,
                                                   Boolean activate) throws ApiException {

        ApiClient apiClient = getApiClient();

        // verify the required parameter 'policy' is set
        if (policy == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'policy' when calling createPolicy");
        }

        // create path and map variables
        String localVarPath = "/api/v1/policies";

        List<Pair> localVarQueryParams = new ArrayList<>(apiClient.parameterToPair("activate", activate));

        final String localVarAccept = apiClient.selectHeaderAccept(MEDIA_TYPE);
        final String localVarContentType = apiClient.selectHeaderContentType(MEDIA_TYPE);

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
            policy,
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            localVarAccept,
            localVarContentType,
            AUTH_NAMES,
            localVarReturnType
        );
    }

    @Override
    public T getPolicy(String policyId, String expand) throws ApiException {

        ApiClient apiClient = getApiClient();

        // verify the required parameter 'policyId' is set
        if (policyId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'policyId' when calling getPolicy");
        }

        // create path and map variables
        String localVarPath = "/api/v1/policies/{policyId}"
            .replaceAll("\\{" + "policyId" + "\\}", apiClient.escapeString(policyId));

        List<Pair> localVarQueryParams = new ArrayList<>(apiClient.parameterToPair("expand", expand));
        final String localVarAccept = apiClient.selectHeaderAccept(MEDIA_TYPE);

        final String[] localVarContentTypes = { };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        TypeReference<T> localVarReturnType = new TypeReference<T>() { };

        T policy = apiClient.invokeAPI(
            localVarPath,
            HttpMethod.GET.name(),
            localVarQueryParams,
            new ArrayList<>(),
            QUERY_STRING_JOINER.toString(),
            null,
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            localVarAccept,
            localVarContentType,
            AUTH_NAMES,
            localVarReturnType
        );

        return (T) getObjectMapper().convertValue(policy, getPolicyType(policy));
    }

    @Override
    public List<Policy> listPolicies(String type,
                                     String status,
                                     String expand) throws ApiException {

        ApiClient apiClient = getApiClient();

        // verify the required parameter 'type' is set
        if (type == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'type' when calling listPolicies");
        }

        // create path and map variables
        String localVarPath = "/api/v1/policies";

        List<Pair> localVarQueryParams = new ArrayList<>();
        localVarQueryParams.addAll(apiClient.parameterToPair("type", type));
        localVarQueryParams.addAll(apiClient.parameterToPair("status", status));
        localVarQueryParams.addAll(apiClient.parameterToPair("expand", expand));

        TypeReference<List<Policy>> localVarReturnType = new TypeReference<List<Policy>>() { };

        List<Policy> policies = apiClient.invokeAPI(
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

        List<Policy> typedPolicies = new ArrayList<>(policies.size());

        policies.forEach(policy ->
            typedPolicies.add(getObjectMapper().convertValue(policy, getPolicyType(policy))));

        return typedPolicies;
    }

    public <T extends PolicyRule> T createPolicyRuleOfType(Class<T> classType,
                                                           String policyId,
                                                           PolicyRule policyRule) throws ApiException {

        ApiClient apiClient = getApiClient();

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

        final String localVarAccept = apiClient.selectHeaderAccept(MEDIA_TYPE);
        final String localVarContentType = apiClient.selectHeaderContentType(MEDIA_TYPE);

        TypeReference<T> localVarReturnType = new TypeReference<T>() {
            @Override
            public Type getType() {
                return classType;
            }
        };

        return apiClient.invokeAPI(
            localVarPath,
            HttpMethod.POST.name(),
            new ArrayList<>(),
            new ArrayList<>(),
            QUERY_STRING_JOINER.toString(),
            policyRule,
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            localVarAccept,
            localVarContentType,
            AUTH_NAMES,
            localVarReturnType
        );
    }
}