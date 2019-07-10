/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018-present Okta, Inc.
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
package com.okta.sdk.ds;

import com.okta.sdk.resource.Resource;
import com.okta.sdk.resource.VoidResource;

import java.util.List;
import java.util.Map;

/**
 * A RequestBuilder allows for making {@link Resource} based request to any Okta endpoint. Not all Okta endpoints are
 * implemented by this SDK currently, this interface allow to make requests with:
 * <ul>
 *     <li>Any Resource as the request {@code body}</li>
 *     <li>Setting query parameters</li>
 *     <li>GET, POST, PUT, and DELETE requests</li>
 * </ul>
 *
 * Example usage for the <a href="https://developer.okta.com/docs/api/resources/users#set-password">Set Password</a>: <p>
 *
 * <code>
 * // create a resource
 * Resource userPasswordRequest = client.instantiate(ExtensibleResource)
 * userPasswordRequest.put("credentials", client.instantiate(ExtensibleResource)
 *                        .put("password", client.instantiate(ExtensibleResource)
 *                            .put("value", "aPassword1!".toCharArray())))
 *
 * // make a POST request to `/api/v1/users/{userId}` and return a User resource
 * User result = client.getDataStore().http()
 *                          .setBody(userPasswordRequest)
 *                          .post("/api/v1/users/"+ userId, User.class)
 * </code>
 *
 * @since 1.2.0
 */
public interface RequestBuilder {

    /**
     * Sets the Resource body for the request.
     *
     * @param resource the request body
     * @return the RequestBuilder to allow for chaining methods.
     */
    RequestBuilder setBody(Resource resource);

    /**
     * Adds a query parameter to the request builder.
     *
     * @param key the query parameter field name
     * @param value the query parameter field value
     * @return the RequestBuilder to allow for chaining methods.
     */
    RequestBuilder addQueryParameter(String key, String value);

    /**
     * Sets the query parameters to be used for the request.
     *
     * @param queryParams the query parameters to be used for the request
     * @return the RequestBuilder to allow for chaining methods.
     */
    RequestBuilder setQueryParameters(Map<String, String> queryParams);

    /**
     * Adds a header parameter to the request builder.
     *
     * @param key the header parameter field name
     * @param value the header parameter field value
     * @return the RequestBuilder to allow for chaining methods.
     */
    RequestBuilder addHeaderParameter(String key, String value);

    /**
     * Adds a header parameter to the request builder.
     *
     * @param key the header parameter field name
     * @param values the header parameter field values
     * @return the RequestBuilder to allow for chaining methods.
     */
    RequestBuilder addHeaderParameter(String key, List<String> values);

    /**
     * Sets the header parameters to be used for the request.
     *
     * @param headerParams the header parameters to be used for the request
     * @return the RequestBuilder to allow for chaining methods.
     */
    RequestBuilder setHeaderParameters(Map<String, List<String>> headerParams);

    /**
     * Executes a {@code GET} request and returns a Resource.
     *
     * @param href an absolute or partial HREF
     * @param type the Resource type to return
     * @param <T> the Resource type to return
     * @return The response payload unmarshalled into a Resource
     */
    <T extends Resource> T get(String href, Class<T> type);

    /**
     * Executes a {@code PUT} request and updates the resource used as the {@code body}.
     *
     * @param href an absolute or partial HREF
     */
    void put(String href);

    /**
     * Executes a {@code POST} request and returns a Resource.
     *
     * @param href an absolute or partial HREF
     * @param type the Resource type to return
     * @param <T> the Resource type to return
     * @return The response payload unmarshalled into a Resource
     */
    <T extends Resource> T post(String href, Class<T> type);

    /**
     * Executes a {@code POST} request.
     *
     * @param href an absolute or partial HREF
     */
    default void post(String href) {
        post(href, VoidResource.class);
    }

    /**
     *
     * Executes a {@code DELETE} request.
     *
     * @param href an absolute or partial HREF
     */
    void delete(String href);
}