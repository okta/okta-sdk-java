package com.okta.sdk.impl.resource;

import com.okta.sdk.impl.ds.InternalDataStore;
import com.okta.sdk.resource.VoidResource;

import java.util.Collections;
import java.util.Map;

/**
 * Default implementation of {@link VoidResource}.
 */
public class DefaultVoidResource extends AbstractResource implements VoidResource {

    public DefaultVoidResource(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultVoidResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return Collections.emptyMap();
    }
}