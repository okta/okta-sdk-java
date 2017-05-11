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
package com.okta.sdk.impl.api;

import com.okta.sdk.api.ApiKey;
import com.okta.sdk.api.ApiKeyList;
import com.okta.sdk.impl.ds.InternalDataStore;
import com.okta.sdk.impl.resource.AbstractCollectionResource;
import com.okta.sdk.impl.resource.ArrayProperty;
import com.okta.sdk.impl.resource.Property;

import java.util.Map;

/**
 * @since 1.0.RC
 */
public class DefaultApiKeyList extends AbstractCollectionResource<ApiKey> implements ApiKeyList {

    public static final ArrayProperty<ApiKey> ITEMS = new ArrayProperty<ApiKey>(ITEMS_PROPERTY_NAME, ApiKey.class);

    private static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(ITEMS);

    public DefaultApiKeyList(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultApiKeyList(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    public DefaultApiKeyList(InternalDataStore dataStore, Map<String, Object> properties, Map<String, Object> queryParams) {
        super(dataStore, properties, queryParams);
    }

    @Override
    protected Class<ApiKey> getItemType() {
        return ApiKey.class;
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }
}
