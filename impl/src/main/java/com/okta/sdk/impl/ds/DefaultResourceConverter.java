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

import com.okta.sdk.impl.resource.AbstractResource;
import com.okta.sdk.impl.resource.ReferenceFactory;
import com.okta.sdk.lang.Assert;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @since 0.5.0
 */
public class DefaultResourceConverter implements ResourceConverter {

    private final ReferenceFactory referenceFactory;

    public DefaultResourceConverter(ReferenceFactory referenceFactory) {
        Assert.notNull(referenceFactory, "referenceFactory cannot be null.");
        this.referenceFactory = referenceFactory;
    }

    @Override
    public Map<String, Object> convert(AbstractResource resource, boolean dirtOnly) {
        Assert.notNull(resource, "resource cannot be null.");

        return toMap(resource, dirtOnly);
    }

    private LinkedHashMap<String, Object> toMap(final AbstractResource resource, boolean partialUpdate) {

        Set<String> propNames = new HashSet<>(resource.getUpdatedPropertyNames());

        if (!partialUpdate) {
            propNames.addAll(resource.getPropertyNames());
        }

        LinkedHashMap<String, Object> props = new LinkedHashMap<>(propNames.size());

        for (String propName : propNames) {
            Object value = resource.getProperty(propName);
            value = toMapValue(propName, value, partialUpdate);
            props.put(propName, value);
        }

        return props;
    }

    private Object toMapValue(final String propName, Object value, boolean dirtyOnly) {

        if (value instanceof AbstractResource) {
            return toMap((AbstractResource)value, dirtyOnly);
        }

        if (value instanceof Map) {
            //Since defaultModel is a map, the DataStore thinks it is a Resource. This causes the code to crash later one as Resources
            //do need to have an href property
            return this.referenceFactory.createUnmaterializedReference(propName, (Map) value);
        }

        return value;
    }
}
