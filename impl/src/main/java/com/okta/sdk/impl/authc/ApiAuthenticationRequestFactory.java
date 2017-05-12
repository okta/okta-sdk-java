/*
 * Copyright 2014 Stormpath, Inc.
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
package com.okta.sdk.impl.authc;

import com.okta.sdk.authc.AuthenticationRequest;
import com.okta.sdk.error.authc.InvalidAuthenticationException;
import com.okta.sdk.error.authc.MissingApiKeyException;
import com.okta.sdk.error.authc.UnsupportedAuthenticationSchemeException;
import com.okta.sdk.http.HttpMethod;
import com.okta.sdk.http.HttpRequest;
import com.okta.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.okta.sdk.impl.http.MediaType;
import com.okta.sdk.lang.Strings;
import com.okta.sdk.oauth.RequestLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @since 1.0.0
 */
public class ApiAuthenticationRequestFactory {

    private final static Set<String> SUPPORTED_AUTHENTICATION_SCHEMES;

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    public static final String BASIC_AUTHENTICATION_SCHEME = "Basic";

    public static final String BEARER_AUTHENTICATION_SCHEME = "Bearer";

    public static final String GRANT_TYPE_PARAMETER = "grant_type";

    static {
        Set<String> tempSet = new HashSet<String>();
        tempSet.add(BASIC_AUTHENTICATION_SCHEME);
        tempSet.add(BEARER_AUTHENTICATION_SCHEME);
        SUPPORTED_AUTHENTICATION_SCHEMES = Collections.unmodifiableSet(tempSet);
    }

    public AuthenticationRequest createFrom(HttpRequest httpRequest) {

        String authzHeaderValue = httpRequest.getHeader(AUTHORIZATION_HEADER);

        String[] schemeAndValue = getSchemeAndValue(authzHeaderValue);

        if (schemeAndValue == null) {
            RequestLocation[] requestLocations = getRequestLocations(httpRequest, false);
            if (requestLocations.length > 0) {
                return OAuthAuthenticationRequestFactory.INSTANCE.createRequest(httpRequest, requestLocations);
            }
        } else {
            if (schemeAndValue[0].equalsIgnoreCase(BEARER_AUTHENTICATION_SCHEME)) {
                RequestLocation[] requestLocations = getRequestLocations(httpRequest, true);
                return OAuthAuthenticationRequestFactory.INSTANCE.createRequest(httpRequest, requestLocations);
            }

            if (schemeAndValue[0].equalsIgnoreCase(BASIC_AUTHENTICATION_SCHEME)) {

                if (hasContentType(httpRequest.getHeader(CONTENT_TYPE_HEADER),
                                   MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {

                    Map<String, String[]> parameterMap = httpRequest.getParameters();

                    if (parameterMap != null && parameterMap.containsKey(GRANT_TYPE_PARAMETER)) {
                        return OAuthAuthenticationRequestFactory.INSTANCE.createTokenRequest(httpRequest);
                    }
                }

                return new DefaultBasicApiAuthenticationRequest(httpRequest);
            }
        }

        throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(InvalidAuthenticationException.class);
    }

    protected String[] getSchemeAndValue(String authzHeaderValue) {
        if (authzHeaderValue == null) {
            return null;
        }
        String[] tokens = authzHeaderValue.split(" ", 2);

        if (tokens.length != 2) {
            throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(MissingApiKeyException.class);
        }
        validateSupportedScheme(tokens[0]);
        return tokens;
    }

    private void validateSupportedScheme(String scheme) {
        for (String supportedScheme : SUPPORTED_AUTHENTICATION_SCHEMES) {
            if (supportedScheme.equalsIgnoreCase(scheme)) {
                return;
            }
        }
        throw ApiAuthenticationExceptionFactory
            .newApiAuthenticationException(UnsupportedAuthenticationSchemeException.class);
    }

    protected boolean hasContentType(String requestContentType, String requiredContentType) {
        if (!Strings.hasText(requiredContentType) || !Strings.hasText(requestContentType)) {
            return false;
        }
        StringTokenizer tokenizer = new StringTokenizer(requestContentType, ";");
        while (tokenizer.hasMoreTokens()) {
            if (requiredContentType.equalsIgnoreCase(tokenizer.nextToken())) {
                return true;
            }
        }

        return false;
    }

    protected RequestLocation[] getRequestLocations(HttpRequest request, boolean addHeaderLocation) {
        HttpMethod method = request.getMethod();
        String contentType = request.getHeader(CONTENT_TYPE_HEADER);

        List<RequestLocation> requestLocationList = new ArrayList<RequestLocation>();
        if (addHeaderLocation) {
            requestLocationList.add(RequestLocation.HEADER);
        }

        EnumSet<HttpMethod> bodyLocationMethods = EnumSet.of(HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PUT);

        if (bodyLocationMethods.contains(method) &&
            hasContentType(contentType, MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            requestLocationList.add(RequestLocation.BODY);
        }
        RequestLocation[] requestLocations = new RequestLocation[requestLocationList.size()];
        return requestLocationList.toArray(requestLocations);

    }
}
