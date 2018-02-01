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
package com.okta.sdk.impl.config;

import com.okta.sdk.impl.io.Resource;
import com.okta.sdk.lang.Assert;

import java.io.IOException;
import java.util.Map;

public class ResourcePropertiesSource implements PropertiesSource {

    private final Resource resource;

    public ResourcePropertiesSource(Resource resource) {
        Assert.notNull(resource, "resource argument cannot be null.");
        this.resource = resource;
    }

    @Override
    public Map<String, String> getProperties() {
        try {
            return new DefaultPropertiesParser().parse(resource);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read resource [" + resource + "]: " + e.getMessage(), e);
        }
    }
}
