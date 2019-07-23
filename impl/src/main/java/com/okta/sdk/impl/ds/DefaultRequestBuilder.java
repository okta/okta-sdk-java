/*
 * Copyright 2018-Present Okta, Inc.
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
package com.okta.sdk.impl.ds;

import com.okta.commons.http.HttpHeaders;
import com.okta.sdk.ds.RequestBuilder;
import com.okta.sdk.resource.Resource;
import com.okta.sdk.resource.VoidResource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The default implementation of {@link RequestBuilder}.
 *
 * @since 1.2.0
 */
class DefaultRequestBuilder implements RequestBuilder {

    private final InternalDataStore dataStore;

    private Resource body;
    private Map<String, Object> queryParams = new HashMap<>();
    private HttpHeaders headers = new HttpHeaders();

    DefaultRequestBuilder(InternalDataStore dataStore) {
        this.dataStore = dataStore;
        this.body = dataStore.instantiate(VoidResource.class);
    }

    @Override
    public RequestBuilder setBody(Resource resource) {
        this.body = resource;
        return this;
    }

    @Override
    public RequestBuilder addQueryParameter(String key, String value) {
        queryParams.put(key, value);
        return this;
    }

    @Override
    public RequestBuilder setQueryParameters(Map<String, String> queryParams) {
        this.queryParams.clear();
        if (!com.okta.commons.lang.Collections.isEmpty(queryParams)) {
            this.queryParams.putAll(queryParams);
        }
        return this;
    }

    @Override
    public RequestBuilder addHeaderParameter(String key, String value) {
        this.headers.add(key, value);
        return this;
    }

    @Override
    public RequestBuilder addHeaderParameter(String key, List<String> values) {
        this.headers.put(key, values);
        return this;
    }

    @Override
    public RequestBuilder setHeaderParameters(Map<String, List<String>> headerParams) {
        this.headers.clear();
        if (!com.okta.commons.lang.Collections.isEmpty(headerParams)) {
            this.headers.putAll(headerParams);
        }
        return this;
    }

    @Override
    public <T extends Resource> T get(String href, Class<T> type) {
        return dataStore.getResource(href, type, queryParams, headers);
    }

    @Override
    public void put(String href) {
        dataStore.save(href, body, null, queryParams, headers);
    }

    @Override
    public <T extends Resource> T post(String href, Class<T> type) {
        return dataStore.create(href, body, null, type, queryParams, headers);
    }

    @Override
    public void delete(String href) {
        dataStore.delete(href, queryParams, headers);
    }

    // exposed for testing
    Resource getBody() {
        return body;
    }

    Map<String, Object> getQueryParameters() {
        return Collections.unmodifiableMap(queryParams);
    }

    Map<String, List<String>> getHeaderParameters() {
        return Collections.unmodifiableMap(headers);
    }
}