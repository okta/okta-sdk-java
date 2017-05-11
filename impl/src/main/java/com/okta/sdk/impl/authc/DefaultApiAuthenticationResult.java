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
package com.okta.sdk.impl.authc;

import com.okta.sdk.api.ApiAuthenticationResult;
import com.okta.sdk.api.ApiKey;
import com.okta.sdk.authc.AuthenticationResultVisitor;
import com.okta.sdk.impl.ds.InternalDataStore;
import com.okta.sdk.impl.resource.AbstractResource;
import com.okta.sdk.impl.resource.Property;
import com.okta.sdk.impl.resource.ResourceReference;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 1.0.beta
 */
public class DefaultApiAuthenticationResult extends AbstractResource implements ApiAuthenticationResult {

    public static final ResourceReference<ApiKey> API_KEY = new ResourceReference<ApiKey>("apiKey", ApiKey.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(API_KEY);

    protected static Map<String, Object> buildPropertiesWithApiKeyApiKey(ApiKey apiKey) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(API_KEY.getName(), apiKey);
        return properties;
    }

    public DefaultApiAuthenticationResult(InternalDataStore dataStore, ApiKey apiKey) {
        super(dataStore, buildPropertiesWithApiKeyApiKey(apiKey));
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public ApiKey getApiKey() {
        return getResourceProperty(API_KEY);
    }

    @Override
    public void accept(AuthenticationResultVisitor visitor) {
        visitor.visit(this);
    }
}
