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
import org.apache.hc.core5.http.HttpStatus;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Pair;
import org.openapitools.client.api.UserFactorApi;
import org.openapitools.client.model.*;

import java.lang.reflect.Type;
import java.util.*;

import static com.okta.sdk.helper.HelperConstants.*;

/**
 * Helper class that enables working with sub-typed {@link UserFactor} references.
 */
public class UserFactorApiHelper extends UserFactorApi {

    private static final ObjectMapper objectMapper = getObjectMapper();

    public static <T extends UserFactor> T activateFactor(Class<T> classType, UserFactorApi userFactorApi,
                                                          String userId, String factorId,
                                                          ActivateFactorRequest activateFactorRequest) throws ApiException {

        ApiClient apiClient = userFactorApi.getApiClient();

        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'userId' when calling activateFactor");
        }

        // verify the required parameter 'factorId' is set
        if (factorId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'factorId' when calling activateFactor");
        }

        // create path and map variables
        String localVarPath = "/api/v1/users/{userId}/factors/{factorId}/lifecycle/activate"
            .replaceAll("\\{" + "userId" + "\\}", apiClient.escapeString(userId))
            .replaceAll("\\{" + "factorId" + "\\}", apiClient.escapeString(factorId));

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
            activateFactorRequest,
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            apiClient.selectHeaderAccept(MEDIA_TYPE),
            apiClient.selectHeaderContentType(MEDIA_TYPE),
            AUTH_NAMES,
            localVarReturnType
        );
    }

    public static <T extends UserFactor> T enrollFactor(Class<T> classType, UserFactorApi userFactorApi, String userId,
                                                        UserFactor userFactor, Boolean updatePhone, String templateId,
                                                        Integer tokenLifetimeSeconds, Boolean activate) throws ApiException {

        ApiClient apiClient = userFactorApi.getApiClient();

        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'userId' when calling enrollFactor");
        }

        // verify the required parameter 'body' is set
        if (userFactor == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'body' when calling enrollFactor");
        }

        // create path and map variables
        String localVarPath = "/api/v1/users/{userId}/factors"
            .replaceAll("\\{" + "userId" + "\\}", apiClient.escapeString(userId));

        List<Pair> localVarQueryParams = new ArrayList<>();
        localVarQueryParams.addAll(apiClient.parameterToPair("updatePhone", updatePhone));
        localVarQueryParams.addAll(apiClient.parameterToPair("templateId", templateId));
        localVarQueryParams.addAll(apiClient.parameterToPair("tokenLifetimeSeconds", tokenLifetimeSeconds));
        localVarQueryParams.addAll(apiClient.parameterToPair("activate", activate));

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
            userFactor,
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            apiClient.selectHeaderAccept(MEDIA_TYPE),
            apiClient.selectHeaderContentType(MEDIA_TYPE),
            AUTH_NAMES,
            localVarReturnType
        );
    }

    public static <T extends UserFactor> T getFactor(UserFactorApi userFactorApi,
                                                     String userId, String factorId) throws ApiException {

        ApiClient apiClient = userFactorApi.getApiClient();

        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'userId' when calling getFactor");
        }

        // verify the required parameter 'factorId' is set
        if (factorId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'factorId' when calling getFactor");
        }

        // create path and map variables
        String localVarPath = "/api/v1/users/{userId}/factors/{factorId}"
            .replaceAll("\\{" + "userId" + "\\}", apiClient.escapeString(userId))
            .replaceAll("\\{" + "factorId" + "\\}", apiClient.escapeString(factorId));

        final String localVarAccept = apiClient.selectHeaderAccept(MEDIA_TYPE);

        final String[] localVarContentTypes = { };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        TypeReference<T> localVarReturnType = new TypeReference<T>() { };
        T userFactor = apiClient.invokeAPI(
            localVarPath,
            HttpMethod.GET.name(),
            new ArrayList<>(),
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

        ObjectMapper objectMapper = getObjectMapper();

        FactorType factorType = userFactor.getFactorType();

        switch (Objects.requireNonNull(factorType)) {
            case CALL:
                return (T) objectMapper.convertValue(userFactor, CallUserFactor.class);

            case EMAIL:
                return (T) objectMapper.convertValue(userFactor, EmailUserFactor.class);

            case HOTP:
                return (T) objectMapper.convertValue(userFactor, CustomHotpUserFactor.class);

            case PUSH:
                return (T) objectMapper.convertValue(userFactor, PushUserFactor.class);

            case SMS:
                return (T) objectMapper.convertValue(userFactor, SmsUserFactor.class);

            case QUESTION:
                return (T) objectMapper.convertValue(userFactor, SecurityQuestionUserFactor.class);

            case TOKEN:
            case TOKEN_HARDWARE:
            case TOKEN_HOTP:
            case TOKEN_SOFTWARE_TOTP:
                return (T) objectMapper.convertValue(userFactor, TokenUserFactor.class);

            case U2F:
                return (T) objectMapper.convertValue(userFactor, U2fUserFactor.class);

            case WEB:
                return (T) objectMapper.convertValue(userFactor, WebUserFactor.class);

            case WEBAUTHN:
                return (T) objectMapper.convertValue(userFactor, WebAuthnUserFactor.class);
        }

        return userFactor;
    }

    public static List<UserFactor> listFactors(UserFactorApi userFactorApi, String userId) throws ApiException {

        ApiClient apiClient = userFactorApi.getApiClient();

        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'userId' when calling listFactors");
        }

        // create path and map variables
        String localVarPath = "/api/v1/users/{userId}/factors"
            .replaceAll("\\{" + "userId" + "\\}", apiClient.escapeString(userId));

        TypeReference<List<UserFactor>> localVarReturnType = new TypeReference<List<UserFactor>>() {};
        List<UserFactor> userFactors = apiClient.invokeAPI(
            localVarPath,
            HttpMethod.GET.name(),
            new ArrayList<>(),
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

        List<UserFactor> typedUserFactors = new ArrayList<>(userFactors.size());

        for (UserFactor userFactor : userFactors) {
            switch (Objects.requireNonNull(userFactor.getFactorType())) {
                case CALL:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, CallUserFactor.class));
                    break;
                case EMAIL:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, EmailUserFactor.class));
                    break;
                case HOTP:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, CustomHotpUserFactor.class));
                    break;
                case PUSH:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, PushUserFactor.class));
                    break;
                case QUESTION:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, SecurityQuestionUserFactor.class));
                    break;
                case SMS:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, SmsUserFactor.class));
                    break;
                case TOKEN:
                case TOKEN_HARDWARE:
                case TOKEN_HOTP:
                case TOKEN_SOFTWARE_TOTP:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, TokenUserFactor.class));
                    break;
                case U2F:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, U2fUserFactor.class));
                    break;
                case WEB:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, WebUserFactor.class));
                    break;
                case WEBAUTHN:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, WebAuthnUserFactor.class));
                    break;
            }
        }
        return typedUserFactors;
    }

    public static List<UserFactor> listSupportedFactors(UserFactorApi userFactorApi, String userId) throws ApiException {

        ApiClient apiClient = userFactorApi.getApiClient();

        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'userId' when calling listSupportedFactors");
        }

        // create path and map variables
        String localVarPath = "/api/v1/users/{userId}/factors/catalog"
            .replaceAll("\\{" + "userId" + "\\}", apiClient.escapeString(userId));

        TypeReference<List<UserFactor>> localVarReturnType = new TypeReference<List<UserFactor>>() {};
        List<UserFactor> userFactors = apiClient.invokeAPI(
            localVarPath,
            HttpMethod.GET.name(),
            new ArrayList<>(),
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

        List<UserFactor> typedUserFactors = new ArrayList<>(userFactors.size());

        for (UserFactor userFactor : userFactors) {
            switch (Objects.requireNonNull(userFactor.getFactorType())) {
                case CALL:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, CallUserFactor.class));
                    break;
                case EMAIL:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, EmailUserFactor.class));
                    break;
                case HOTP:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, CustomHotpUserFactor.class));
                    break;
                case PUSH:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, PushUserFactor.class));
                    break;
                case QUESTION:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, SecurityQuestionUserFactor.class));
                    break;
                case SMS:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, SmsUserFactor.class));
                    break;
                case TOKEN:
                case TOKEN_HARDWARE:
                case TOKEN_HOTP:
                case TOKEN_SOFTWARE_TOTP:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, TokenUserFactor.class));
                    break;
                case U2F:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, U2fUserFactor.class));
                    break;
                case WEB:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, WebUserFactor.class));
                    break;
                case WEBAUTHN:
                    typedUserFactors.add(objectMapper.convertValue(userFactor, WebAuthnUserFactor.class));
                    break;
            }
        }
        return typedUserFactors;
    }

    public static <T extends UserFactor> T resendEnrollFactor(UserFactorApi userFactorApi, String userId, String factorId,
                                                              UserFactor userFactor, String templateId) throws ApiException {

        ApiClient apiClient = userFactorApi.getApiClient();

        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'userId' when calling resendEnrollFactor");
        }

        // verify the required parameter 'factorId' is set
        if (factorId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'factorId' when calling resendEnrollFactor");
        }

        // verify the required parameter 'userFactor' is set
        if (userFactor == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'userFactor' when calling resendEnrollFactor");
        }

        // create path and map variables
        String localVarPath = "/api/v1/users/{userId}/factors/{factorId}/resend"
            .replaceAll("\\{" + "userId" + "\\}", apiClient.escapeString(userId))
            .replaceAll("\\{" + "factorId" + "\\}", apiClient.escapeString(factorId));

        List<Pair> localVarQueryParams = new ArrayList<>(apiClient.parameterToPair("templateId", templateId));

        TypeReference<T> localVarReturnType = new TypeReference<T>() {};
        return apiClient.invokeAPI(
            localVarPath,
            HttpMethod.POST.name(),
            localVarQueryParams,
            new ArrayList<>(),
            QUERY_STRING_JOINER.toString(),
            userFactor,
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            apiClient.selectHeaderAccept(MEDIA_TYPE),
            apiClient.selectHeaderContentType(MEDIA_TYPE),
            AUTH_NAMES,
            localVarReturnType
        );
    }
}