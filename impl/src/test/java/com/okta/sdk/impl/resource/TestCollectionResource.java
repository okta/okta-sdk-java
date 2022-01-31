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
import com.okta.sdk.resource.group.User;

import java.util.Map;

public class TestCollectionResource extends AbstractCollectionResource<TestResource> {

    private static final ArrayProperty<User> ITEMS = new ArrayProperty<>("items", User.class);
    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(ITEMS);

    protected TestCollectionResource(InternalDataStore dataStore) {
        super(dataStore);
    }

    protected TestCollectionResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    protected TestCollectionResource(InternalDataStore dataStore, Map<String, Object> properties, Map<String, Object> queryParams) {
        super(dataStore, properties, queryParams);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    protected Class<TestResource> getItemType() {
        return TestResource.class;
    }
}
