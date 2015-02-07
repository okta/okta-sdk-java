package com.okta.sdk.models.apps;

import com.okta.sdk.framework.ApiObject;
import org.joda.time.DateTime;

public class AppGroup extends ApiObject {

    /**
     * unique key of group
     */
    private String id;

    /**
     * timestamp when app group was last updated
     */
    private DateTime lastUpdated;

    /**
     * priority of group assignment
     */
    private Integer priority;

    /**
     * Gets id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets id
     */
    public void setId(String val) {
        this.id = val;
    }

    /**
     * Gets lastUpdated
     */
    public DateTime getLastUpdated() {
        return this.lastUpdated;
    }

    /**
     * Sets lastUpdated
     */
    public void setLastUpdated(DateTime val) {
        this.lastUpdated = val;
    }

    /**
     * Gets priority
     */
    public Integer getPriority() {
        return this.priority;
    }

    /**
     * Sets priority
     */
    public void setPriority(Integer val) {
        this.priority = val;
    }
}