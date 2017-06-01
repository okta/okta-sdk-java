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

import com.okta.sdk.lang.Assert;
import com.okta.sdk.resource.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 0.5.0
 */
public class ReferenceFactory {

    public ReferenceFactory(){}

    public Map<String, String> createReference(String resourceName, Map map) {
//        Assert.isTrue(!map.isEmpty() && map.containsKey(AbstractResource.HREF_PROP_NAME),
//                "'" + resourceName + "' resource must have an 'href' property.");
//        String href = String.valueOf(map.get(AbstractResource.HREF_PROP_NAME));

        Map<String, String> reference = new HashMap<>(1);
//        reference.put(AbstractResource.HREF_PROP_NAME, href);

        return reference;
    }

    public Map<String, String> createUnmaterializedReference(String resourceName, Map map) {
        Map<String, String> reference = new HashMap<>();
            reference.putAll(map);
        return reference;
    }

    public Map<String, String> createReference(String resourceName, Resource resource) {
        Assert.notNull(resource, "Resource argument cannot be null.");
        String href = resource.getResourceHref();
        Assert.hasText(href,  "'" + resourceName + "' resource must have an 'href' property.");

        Map<String, String> reference = new HashMap<>(1);
//        reference.put(AbstractResource.HREF_PROP_NAME, href); // TODO

        return reference;
    }

    public Map<String, String> createUnmaterializedReference (String resourceName, Resource resource, boolean dirtyOnly){
        Assert.notNull(resource, "Resource argument cannot be null.");
        Assert.isNull(resource.getResourceHref(), "Resource "+ resourceName +" must be unmaterialized and not have an 'href' property.");

        Map<String, String> reference = dirtyOnly ? ResourceUtil.filterNonStringValuesWithResourceDirtyProperties(resource)
                                                  : ResourceUtil.filterNonStringValuesWithResource(resource);
        return reference;
    }
}
