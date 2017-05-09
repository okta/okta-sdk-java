package com.okta.sdk.impl.resource;

import com.okta.sdk.impl.ds.InternalDataStore;

import java.util.Collections;
import java.util.Map;

/**
 * A generic Resource implementation that can be used when the response type is expected to be empty;
 */
public class VoidResource extends AbstractResource {

    public VoidResource(InternalDataStore dataStore) {
        super(dataStore);
    }

    public VoidResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return Collections.emptyMap();
    }
}
