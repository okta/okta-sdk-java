package com.okta.sdk.impl.resource;

import com.okta.commons.lang.Classes;
import com.okta.sdk.resource.Resource;

import java.util.Map;

public interface SamlMetaData extends Resource {
    void setMetadata(Map metadata);
    Map getMetadata();
}
