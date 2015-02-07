package com.okta.sdk.framework;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public abstract class ApiObject {

    @JsonAnyGetter
    public Map<String, Object> getUnmapped() {
        return unmapped;
    }

    private Map<String, Object> unmapped = new HashMap<String, Object>();

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // TODO: log a warning
        unmapped.put(key, value);
    }
}
