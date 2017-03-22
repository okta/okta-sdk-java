/*!
 * Copyright (c) 2015-2017, Okta, Inc. and/or its affiliates. All rights reserved.
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

    /**
     * Create a session within Okta.
     *
     * @return {@link Session}                       New session object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public Session createSessionForMe() throws IOException {
        return post(getEncodedPath("/me"), null, new TypeReference<Session>() { });
    }

    /**
     * Create a session within Okta with additional fields.
     *
     * @param  additionalFields {@link String}       Optional session properties.
     * @return {@link Session}                       Current session object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public Session createSessionForMeWithAdditionalFields(String additionalFields) throws IOException {
        return post(getEncodedPath("/me?additionalFields=%s", additionalFields), null, new TypeReference<Session>() { });
    }

    /**
     * Create a session within Okta using User credentials.
     *
     * @param  username {@link String}               Username of the user.
     * @param  password {@link String}               Password of the user.
     * @return {@link Session}                       Current session object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public Session createSessionWithCredentials(String username, String password) throws IOException {
        Credentials credentials = new Credentials();
        credentials.setUsername(username);
        credentials.setPassword(password);
        return post(getEncodedPath("/"), credentials, new TypeReference<Session>() { });
    }

    /**
     * Create a session within Okta with additional fields using User credentials.
     *
     * @param  username {@link String}               Username of the user.
     * @param  password {@link String}               Password of the user.
     * @param  additionalFields {@link String}       Optional session properties.
     * @return {@link Session}                       Current session object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public Session createSessionWithCredentialsAndAdditionalFields(String username, String password, String additionalFields) throws IOException {
        Credentials credentials = new Credentials();
        credentials.setUsername(username);
        credentials.setPassword(password);
        return post(getEncodedPath("?additionalFields=%s", additionalFields), credentials, new TypeReference<Session>() { });
    }

    /**
     * Create a session within Okta with User credentials and cookieToken.
     *
     * @param  username {@link String}               Username of the user.
     * @param  password {@link String}               Password of the user.
     * @return {@link Session}                       Current session object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public Session createSessionWithCredentialsAndCookieToken(String username, String password) throws IOException {
        return createSessionWithCredentialsAndAdditionalFields(username, password, "cookieToken");
    }

    /**
     * Create a session within Okta with User credentials and cookieToken via URL.
     *
     * @param  username {@link String}               Username of the user.
     * @param  password {@link String}               Password of the user.
     * @return {@link Session}                       Current session object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public Session createSessionWithCredentialsAndCookieTokenUrl(String username, String password) throws IOException {
        return createSessionWithCredentialsAndAdditionalFields(username, password, "cookieTokenUrl");
    }

    /**
     * Create a session within Okta with session token.
     *
     * @param  sessionToken {@link String}           Session obtained by the Authentication API.
     * @return {@link Session}                       Current session object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public Session createSessionWithSessionToken(String sessionToken) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sessionToken", sessionToken);
        return post(getEncodedPath("/"), params, new TypeReference<Session>() {
        });
    }

    /**
     * Create a session within Okta with session token and additional fields.
     *
     * @param  sessionToken {@link String}           Session obtained by the Authentication API.
     * @param  additionalFields {@link String}       Optional session properties.
     * @return {@link Session}                       Current session object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public Session createSessionWithSessionTokenAndAdditionalFields(String sessionToken, String additionalFields) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sessionToken", sessionToken);
        return post(getEncodedPath("?additionalFields=%s", additionalFields), params, new TypeReference<Session>() { });
    }

    /**
     * Returns a validated session.
     *
     * @param  sessionId {@link String}              Unique ID of the session.
     * @return {@link Session}                       Updated session object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public Session validateSession(String sessionId) throws IOException {
        return get(getEncodedPath("/%s", sessionId), new TypeReference<Session>() {
        });
    }

    /**
     * Returns an extended session.
     *
     * @param  sessionId {@link String}              Unique ID of the session.
     * @return {@link Session}                       Current session object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public Session extendSession(String sessionId) throws IOException  {
        return put(getEncodedPath("/%s", sessionId), new TypeReference<Session>() {
        });
    }

    /**
     * Clears the current session.
     *
     * @param  sessionId {@link String}              Unique ID of the session.
     * @throws IOException                           If an input or output exception occurred.
     */
    public void clearSession(String sessionId) throws IOException {
        delete(getEncodedPath("/%s", sessionId));
    }

    /**
     * Overriding method to get full path from relative path.
     *
     * @param  relativePath {@link String}
     * @return {@link String}
     */
    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/sessions%s", this.apiVersion, relativePath);
    }

}
