package com.okta.sdk.models.events;

import com.okta.sdk.framework.ApiObject;

public class Action extends ApiObject {

    /**
     * Description of an action
     */
    private String message;

    /**
     * Categories for an action
     */
    private String[] categories;

    /**
     * Identifies the unique type of an action
     */
    private String objectType;

    /**
     * Relative uri of the request that generated the event.
     */
    private String requestUri;

    /**
     * Gets message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Sets message
     */
    public void setMessage(String val) {
        this.message = val;
    }

    /**
     * Gets categories
     */
    public String[] getCategories() {
        return this.categories;
    }

    /**
     * Sets categories
     */
    public void setCategories(String[] val) {
        this.categories = val;
    }

    /**
     * Gets objectType
     */
    public String getObjectType() {
        return this.objectType;
    }

    /**
     * Sets objectType
     */
    public void setObjectType(String val) {
        this.objectType = val;
    }

    /**
     * Gets requestUri
     */
    public String getRequestUri() {
        return this.requestUri;
    }

    /**
     * Sets requestUri
     */
    public void setRequestUri(String val) {
        this.requestUri = val;
    }
}