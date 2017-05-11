package com.okta.sdk.impl.resource;

import com.okta.sdk.impl.ds.InternalDataStore;
import com.okta.sdk.resource.User;

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
