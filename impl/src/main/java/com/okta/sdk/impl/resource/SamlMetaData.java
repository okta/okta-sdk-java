package com.okta.sdk.impl.resource;

import com.okta.commons.lang.Classes;
import com.okta.sdk.resource.Resource;

import java.util.Map;

public interface SamlMetaData extends Resource {
    static SamlMetaData instance() {
        return Classes.newInstance("com.okta.sdk.impl.ds.DefaultSamlMetaData");
    }
    void setMetadata(Map metadata);
    Map getMetadata();
}
