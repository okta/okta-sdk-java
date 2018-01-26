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
package com.okta.sdk.impl.ds;

import com.okta.sdk.ds.DataStore;
import com.okta.sdk.impl.ds.cache.CacheResolver;
import com.okta.sdk.resource.Resource;

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

    /**
     * Instantiates and returns a new instance of the specified Resource type. The instance is merely instantiated and
     * is not saved/synchronized with the server in any way. This operation allows
     * the <code>href</code> to be a fragment, where the <code>baseUrl</code> can be missing and will be added automatically.
     *
     * @param clazz the Resource class to instantiate.
     * @param <T>   the Resource sub-type
     * @param properties the properties the instantiated resource will have
     * @param hrefFragment when <code>true</code>, the baseUrl will be appended to the value found in the href key of the properties map.
     *                     If <code>false</code> the href will not be altered and will be kept as-is.
     * @return a resource instance corresponding to the specified clazz.

     */
    <T extends Resource> T instantiate(Class<T> clazz, Map<String, Object> properties, boolean hrefFragment);

    <T extends Resource> T create(String parentHref, T resource, T parentResource);

    <T extends Resource, R extends Resource> R create(String parentHref, T resource, T parentResource, Class<? extends R> returnType);

    <T extends Resource> void save(T resource);

    <T extends Resource> void save(String href, T resource, T parentResource);

    <T extends Resource> void delete(T resource);

    <T extends Resource> void delete(String href, T resource);

    void delete(String href);

    <T extends Resource> T getResource(String href, Class<T> clazz, Map<String,Object> queryParameters);

    CacheResolver getCacheResolver();

    String getBaseUrl();

}
