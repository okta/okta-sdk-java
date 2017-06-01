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
package com.okta.sdk.impl.resource;

import com.okta.sdk.resource.Resource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility code to serve classes within resource package
 * @since 0.5.0
 */
public class ResourceUtil {

    /**
     * Filters non- string values out of a the {@link Resource} properties
     * @param resource the resource to be filtered
     * @return filtered map
     */
    public static final Map<String, String> filterNonStringValuesWithResource(Resource resource){
        Map<String, Object> properties = new LinkedHashMap<>(((AbstractResource) resource).properties);
        properties.putAll(((AbstractResource) resource).dirtyProperties);
        return filterNonStringValues(properties);
    }

    /**
     * Filters non- string values out of a the {@link Resource} dirty properties
     * @param resource the resource to be filtered
     * @return filtered map
     */
    public static final Map<String, String> filterNonStringValuesWithResourceDirtyProperties(Resource resource){
        Map<String, Object> dirtyProperties = ((AbstractResource) resource).dirtyProperties;
        return filterNonStringValues(dirtyProperties);
    }

    /**
     * Filters non- string values out of a Map
     * @param map input map
     * @return filtered map
     */
    public static final Map<String, String> filterNonStringValues(Map<String, Object> map){
        Map<String, String> filteredMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!(entry.getValue() instanceof String)) {
                continue;
            }
            filteredMap.put(entry.getKey(), (String) entry.getValue());
        }
        return filteredMap;
    }
}
