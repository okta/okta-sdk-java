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

import com.okta.sdk.impl.ds.InternalDataStore;

import java.util.Map;

public class TestResource extends AbstractResource {

    public TestResource(InternalDataStore dataStore) {
        super(dataStore);
    }

    public TestResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public String getName() {
        return super.getStringProperty("name");
    }

    public void setName(String name) {
        super.setProperty("name", name);
    }

    public String getDescription() {
        return super.getStringProperty("description");
    }

    public void setDescription(String description) {
        super.setProperty("description", description);
    }
}