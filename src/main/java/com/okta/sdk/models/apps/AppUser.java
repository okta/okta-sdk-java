package com.okta.sdk.models.apps;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.okta.sdk.framework.ApiObject;
import com.okta.sdk.models.links.LinksUnion;
import com.okta.sdk.models.users.LoginCredentials;
import com.okta.sdk.models.users.UserProfile;
import org.joda.time.DateTime;

import java.util.Map;

public class AppUser extends ApiObject {

    /**
     * unique key for user
     */
    private String id;

    /**
     * unique external key for user
     */
    private String externalId;

    /**
     * timestamp when user was created
     */
    private DateTime created;

    /**
     * timestamp when user was last updated
     */
    private DateTime lastUpdated;

    /**
     * toggles the assignment between user or group scope
     */
    private String scope;

    /**
     * status of app user
     */
    private String status;

    /**
     * timestamp when user was last updated
     */
    private DateTime statusChanged;

    /**
     * timestamp when app password last changed
     */
    private DateTime passwordChanged;

    /**
     * synchronization state for app user
     */
    private String syncState;

    /**
     * timestamp when last sync operation was executed
     */
    private DateTime lastSync;

    /**
     * credentials for assigned app
     */
    private LoginCredentials credentials;

    /**
     * app-specific profile for the user
     */
    private UserProfile profile;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_links")
    private Map<String, LinksUnion> links;

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
     * Sets externalId
     */
    public void setExternalId(String val) {
        this.externalId = val;
    }

    /**
     * Gets externalId
     */
    public String getExternalId() {
        return this.externalId;
    }


    /**
     * Gets created
     */
    public DateTime getCreated() {
        return this.created;
    }

    /**
     * Sets created
     */
    public void setCreated(DateTime val) {
        this.created = val;
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
     * Gets scope
     */
    public String getScope() {
        return this.scope;
    }

    /**
     * Sets scope
     */
    public void setScope(String val) {
        this.scope = val;
    }

    /**
     * Gets status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Sets status
     */
    public void setStatus(String val) {
        this.status = val;
    }

    /**
     * Gets statusChanged
     */
    public DateTime getStatusChanged() {
        return this.statusChanged;
    }

    /**
     * Sets statusChanged
     */
    public void setStatusChanged(DateTime val) {
        this.statusChanged = val;
    }


    /**
     * Gets passwordChanged
     */
    public DateTime getPasswordChanged() {
        return this.passwordChanged;
    }

    /**
     * Sets passwordChanged
     */
    public void setPasswordChanged(DateTime val) {
        this.passwordChanged = val;
    }

    /**
     * Gets syncState
     */
    public String getSyncState() {
        return this.syncState;
    }

    /**
     * Sets scope
     */
    public void setSyncState(String val) {
        this.syncState = val;
    }

    /**
     * Gets lastSync
     */
    public DateTime getLastSync() {
        return this.lastSync;
    }

    /**
     * Sets passwordChanged
     */
    public void setLastSync(DateTime val) {
        this.lastSync = val;
    }

    /**
     * Gets credentials
     */
    public LoginCredentials getCredentials() {
        return this.credentials;
    }

    /**
     * Sets credentials
     */
    public void setCredentials(LoginCredentials val) {
        this.credentials = val;
    }

    /**
     * Gets profile
     */
    public UserProfile getProfile() {
        return this.profile;
    }

    /**
     * Sets profile
     */
    public void setProfile(UserProfile val) {
        this.profile = val;
    }

    public Map<String, LinksUnion> getLinks() {
        return links;
    }

    public void setLinks(Map<String, LinksUnion> links) {
        this.links = links;
    }
}
