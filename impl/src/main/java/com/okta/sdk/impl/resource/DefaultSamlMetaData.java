package com.okta.sdk.impl.resource;

import com.okta.sdk.impl.ds.InternalDataStore;

import java.util.Map;

public class DefaultSamlMetaData implements SamlMetaData {
    Map metadata;
    public DefaultSamlMetaData(InternalDataStore dataStore, Map<String, Object> properties) {

        this.metadata=properties;
    }


    @Override
    public void setMetadata(Map metadata) {
        this.metadata = metadata;
    }

    @Override
    public Map getMetadata() {
        return metadata;
    }

    @Override
    public String getResourceHref() {
        return null;
    }

    @Override
    public void setResourceHref(String href) {

    }
}
