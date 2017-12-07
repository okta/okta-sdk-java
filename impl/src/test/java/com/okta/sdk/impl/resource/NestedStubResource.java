/*
 * Copyright 2017 Okta
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

import com.okta.sdk.impl.ds.InternalDataStore;

import java.util.Map;

public class NestedStubResource extends AbstractResource {

    public final StringProperty stringProp = new StringProperty("nestedStringPropKey");
    private final Map<String, Property> propertyDescriptors = AbstractResource.createPropertyDescriptorMap(stringProp);

    protected NestedStubResource(InternalDataStore dataStore) {
        super(dataStore);
    }

    protected NestedStubResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    public String getStringProp() {
        return getString(stringProp);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return propertyDescriptors;
    }
}