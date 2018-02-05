/*
 * Copyright 2018 Okta, Inc.
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
package com.okta.sdk.impl.resource;

import com.okta.sdk.lang.Collections;
import com.okta.sdk.resource.Resource;

import java.util.Map;

/**
 * The default HREF resolver return a HAL self link if available ({@code _links.self.href}).
 *
 * @since 1.0
 */
public class HalResourceHrefResolver implements ResourceHrefResolver {

    @Override
    public <R extends Resource> String resolveHref(Map<String, ?> properties, Class<R> clazz) {
        Map<String, ?> links = getMapValue(properties, "_links");
        Map<String, ?> self = getMapValue(links, "self");
        if (!Collections.isEmpty(self)) {
            return (String) self.get("href");
        }
        return null;
    }

    private Map<String, ?> getMapValue(Map<String, ?> properties, String key) {
        if (!Collections.isEmpty(properties)) {
            return (Map<String, ?>) properties.get(key);
        }
        return null;
    }
}