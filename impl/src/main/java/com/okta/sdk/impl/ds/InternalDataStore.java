/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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

import com.okta.sdk.ds.DataStore;
import com.okta.sdk.resource.Resource;

import java.util.List;
import java.util.Map;

/**
 * Internal DataStore used for implementation purposes only.  Not intended to be called by SDK end users!
 * <p>
 * <b>WARNING: This API CAN CHANGE AT ANY TIME, WITHOUT NOTICE.  DO NOT DEPEND ON IT.</b>
 *
 * @since 0.5.0
 */
public interface InternalDataStore extends DataStore {

    <T extends Resource> T instantiate(Class<T> clazz, Map<String,Object> properties);

    <T extends Resource> T create(String parentHref, T resource, T parentResource);

    <T extends Resource, R extends Resource> R create(String parentHref, T resource, T parentResource, Class<? extends R> returnType);

    /**
     * @since 1.2.0
     */
    <T extends Resource, R extends Resource> R create(String parentHref, T resource, T parentResource, Class<? extends R> returnType, Map<String,Object> queryParameters);

    /**
     * @since 1.5.0
     */
    <T extends Resource, R extends Resource> R create(String parentHref, T resource, T parentResource, Class<? extends R> returnType, Map<String,Object> queryParameters, Map<String, List<String>> headerParameters);

    <T extends Resource> void save(T resource);

    <T extends Resource> void save(String href, T resource, T parentResource);

    /**
     * @since 1.2.0
     */
    <T extends Resource> void save(String href, T resource, T parentResource, Map<String,Object> queryParameters);

    /**
     * @since 1.5.0
     */
    <T extends Resource> void save(String href, T resource, T parentResource, Map<String,Object> queryParameters, Map<String, List<String>> headerParameters);

    <T extends Resource> void delete(T resource);

    <T extends Resource> void delete(String href, T resource);

    void delete(String href);

    /**
     * @since 1.2.0
     */
    void delete(String href, Map<String,Object> queryParameters);

    /**
     * @since 1.5.0
     */
    void delete(String href, Map<String,Object> queryParameters, Map<String, List<String>> headerParameters);

    /**
     * @since 1.6.0
     */
    <T extends Resource> void delete(String href, T resource, Map<String,Object> queryParameters, Map<String, List<String>> headerParameters);

    <T extends Resource> T getResource(String href, Class<T> clazz, Map<String,Object> queryParameters);

    /**
     * @since 1.5.0
     */
    <T extends Resource> T getResource(String href, Class<T> clazz, Map<String,Object> queryParameters, Map<String, List<String>> headerParameters);

    String getBaseUrl();

    default <T extends Resource> T create(String parentHref, T resource) {
        return create(parentHref, resource, (T) null);
    }

    default <T extends Resource, R extends Resource> R create(String parentHref, T resource, Class<? extends R> returnType) {
        return create(parentHref, resource, null, returnType);
    }

    default <T extends Resource> void save(String href, T resource) {
        save(href, resource, null);
    }
}
