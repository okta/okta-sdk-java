package com.okta.sdk.impl.error;

import com.okta.sdk.error.ErrorCause;
import com.okta.sdk.impl.ds.InternalDataStore;
import com.okta.sdk.impl.resource.AbstractResource;
import com.okta.sdk.impl.resource.Property;
import com.okta.sdk.impl.resource.StringProperty;

import java.util.Map;

public class DefaultErrorCause extends AbstractResource implements ErrorCause {

    static final StringProperty SUMMARY = new StringProperty("errorSummary");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            SUMMARY
    );

    protected DefaultErrorCause(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getSummary() {
        return getString(SUMMARY);
    }

    @Override
    public String getResourceHref() {
        return null;
    }

    @Override
    public void setResourceHref(String href) {

    }
}
