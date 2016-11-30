/*!
 * Copyright (c) 2015-2016, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.okta.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.JsonApiClient;
import com.okta.sdk.models.sessions.Credentials;
import com.okta.sdk.models.sessions.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionApiClient extends JsonApiClient {

    public SessionApiClient(ApiClientConfiguration config) {
        super(config);
    }

    ////////////////////////////////////////////
    // COMMON METHODS
    ////////////////////////////////////////////

    public Session createSessionForMe() throws IOException {
        return post(getEncodedPath("/me"), null, new TypeReference<Session>() { });
    }

    public Session createSessionForMeWithAdditionalFields(String additionalFields) throws IOException {
        return post(getEncodedPath("/me?additionalFields=%s", additionalFields), null, new TypeReference<Session>() { });
    }

    public Session createSessionWithCredentials(String username, String password) throws IOException {
        Credentials credentials = new Credentials();
        credentials.setUsername(username);
        credentials.setPassword(password);
        return post(getEncodedPath("/"), credentials, new TypeReference<Session>() { });
    }

    public Session createSessionWithCredentialsAndAdditionalFields(String username, String password, String additionalFields) throws IOException {
        Credentials credentials = new Credentials();
        credentials.setUsername(username);
        credentials.setPassword(password);
        return post(getEncodedPath("?additionalFields=%s", additionalFields), credentials, new TypeReference<Session>() { });
    }

    public Session createSessionWithCredentialsAndCookieToken(String username, String password) throws IOException {
        return createSessionWithCredentialsAndAdditionalFields(username, password, "cookieToken");
    }

    public Session createSessionWithCredentialsAndCookieTokenUrl(String username, String password) throws IOException {
        return createSessionWithCredentialsAndAdditionalFields(username, password, "cookieTokenUrl");
    }

    public Session createSessionWithSessionToken(String sessionToken) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sessionToken", sessionToken);
        return post(getEncodedPath("/"), params, new TypeReference<Session>() {
        });
    }

    public Session createSessionWithSessionTokenAndAdditionalFields(String sessionToken, String additionalFields) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sessionToken", sessionToken);
        return post(getEncodedPath("?additionalFields=%s", additionalFields), params, new TypeReference<Session>() { });
    }

    public Session validateSession(String sessionId) throws IOException {
        return get(getEncodedPath("/%s", sessionId), new TypeReference<Session>() {
        });
    }

    public Session extendSession(String sessionId) throws IOException  {
        return put(getEncodedPath("/%s", sessionId), new TypeReference<Session>() {
        });
    }

    public void clearSession(String sessionId) throws IOException {
        delete(getEncodedPath("/%s", sessionId));
    }

    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/sessions%s", this.apiVersion, relativePath);
    }

}
