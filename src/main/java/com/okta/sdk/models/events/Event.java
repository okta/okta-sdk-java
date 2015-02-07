package com.okta.sdk.models.events;

import com.okta.sdk.framework.ApiObject;
import org.joda.time.DateTime;

public class Event extends ApiObject {

    /**
     * unique key for event
     */
    private String id;

    /**
     * timestamp when event was published  
     */
    private DateTime published;

    /**
     * identifies the request
     */
    private String requestId;

    /**
     * session in which the event occurred
     */
    private String sessionId;

    private Action action;

    private Actor[] actors;

    private Target[] targets;

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
     * Gets published
     */
    public DateTime getPublished() {
        return this.published;
    }

    /**
     * Sets published
     */
    public void setPublished(DateTime val) {
        this.published = val;
    }

    /**
     * Gets requestId
     */
    public String getRequestId() {
        return this.requestId;
    }

    /**
     * Sets requestId
     */
    public void setRequestId(String val) {
        this.requestId = val;
    }

    /**
     * Gets sessionId
     */
    public String getSessionId() {
        return this.sessionId;
    }

    /**
     * Sets sessionId
     */
    public void setSessionId(String val) {
        this.sessionId = val;
    }

    /**
     * Gets action
     */
    public Action getAction() {
        return this.action;
    }

    /**
     * Sets action
     */
    public void setAction(Action val) {
        this.action = val;
    }

    /**
     * Gets actors
     */
    public Actor[] getActors() {
        return this.actors;
    }

    /**
     * Sets actors
     */
    public void setActors(Actor[] val) {
        this.actors = val;
    }

    /**
     * Gets targets
     */
    public Target[] getTargets() {
        return this.targets;
    }

    /**
     * Sets targets
     */
    public void setTargets(Target[] val) {
        this.targets = val;
    }
}