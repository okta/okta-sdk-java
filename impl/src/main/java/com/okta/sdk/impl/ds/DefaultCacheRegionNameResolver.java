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

import com.okta.sdk.lang.Assert;
import com.okta.sdk.resource.Resource;

/**
 * @since 0.8
 */
public class DefaultCacheRegionNameResolver implements CacheRegionNameResolver {

    @Override
    public <T extends Resource> String getCacheRegionName(Class<T> clazz) {
        Assert.notNull(clazz, "Class argument cannot be null.");
        Class iface = DefaultResourceFactory.getInterfaceClass(clazz);
        return iface.getName();
    }
}
