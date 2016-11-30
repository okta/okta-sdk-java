/*!
 * Copyright (c) 2015-2016, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

import java.util.List;
import java.util.Objects;

public final class Log extends ApiObject {

    /**
     * Describes entity that performed the action
     */
    private final LogResource actor;

    /**
     * Identifies the client that requested the action
     */
    private final LogClient client;

    /**
     * Identifies the authentication data of the action
     */
    private final LogAuthenticationContext authenticationContext;

    /**
     * Display message regarding the event
     */
    private final String displayMessage;

    /**
     * Type of event that was published
     */
    private final String eventType;

    /**
     * Identifies the outcome of the action
     */
    private final LogOutcome outcome;

    /**
     * Timestamp when event was published
     */
    private final String published;

    /**
     * Identifies the security data of the action
     */
    private final LogSecurityContext securityContext;

    /**
     * Indicates how severe the event is
     */
    private final Severity severity;

    /**
     * Identifies the debug request data of the action
     */
    private final LogDebugContext debugContext;

    /**
     * Legacy event type
     */
    private final String legacyEventType;

    /**
     * Identifies the transaction details of the action
     */
    private final LogTransaction transaction;

    /**
     * unique key for event
     */
    private final String uuid;
    private final String version;
    private final LogRequest request;
    private final List<LogResource> target;

    @JsonCreator
    public Log(
            @JsonProperty("actor") LogResource actor,
            @JsonProperty("client") LogClient client,
            @JsonProperty("authenticationContext") LogAuthenticationContext authenticationContext,
            @JsonProperty("displayMessage") String displayMessage,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("outcome") LogOutcome outcome,
            @JsonProperty("published") String published,
            @JsonProperty("securityContext") LogSecurityContext securityContext,
            @JsonProperty("severity") Severity severity,
            @JsonProperty("debugContext") LogDebugContext debugContext,
            @JsonProperty("legacyEventType") String legacyEventType,
            @JsonProperty("transaction") LogTransaction transaction,
            @JsonProperty("uuid") String uuid,
            @JsonProperty("version") String version,
            @JsonProperty("request") LogRequest request,
            @JsonProperty("target") List<LogResource> target
    ) {
        this.actor = actor;
        this.client = client;
        this.authenticationContext = authenticationContext;
        this.displayMessage = displayMessage;
        this.eventType = eventType;
        this.outcome = outcome;
        this.published = published;
        this.securityContext = securityContext;
        this.severity = severity;
        this.debugContext = debugContext;
        this.legacyEventType = legacyEventType;
        this.transaction = transaction;
        this.uuid = uuid;
        this.version = version;
        this.request = request;
        this.target = target;
    }

    public String getPublished() {
        return published;
    }

    public String getEventType() {
        return eventType;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public Severity getSeverity() {
        return severity;
    }

    public LogClient getClient() {
        return client;
    }

    public LogResource getActor() {
        return actor;
    }

    public LogOutcome getOutcome() {
        return outcome;
    }

    public List<LogResource> getTarget() {
        return target;
    }

    public LogAuthenticationContext getAuthenticationContext() {
        return authenticationContext;
    }

    public LogSecurityContext getSecurityContext() {
        return securityContext;
    }

    public LogDebugContext getDebugContext() {
        return debugContext;
    }

    public String getLegacyEventType() {
        return legacyEventType;
    }

    public LogTransaction getTransaction() {
        return transaction;
    }

    public String getUuid() {
        return uuid;
    }

    public String getVersion() {
        return version;
    }

    public LogRequest getRequest() {
        return request;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                actor,
                client,
                authenticationContext,
                displayMessage,
                eventType,
                outcome,
                published,
                securityContext,
                severity,
                debugContext,
                legacyEventType,
                transaction,
                uuid,
                version,
                request,
                target
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Log)) {
            return false;
        }
        final Log other = (Log) obj;
        return Objects.equals(this.actor, other.actor)
                && Objects.equals(this.client, other.client)
                && Objects.equals(this.authenticationContext, other.authenticationContext)
                && Objects.equals(this.displayMessage, other.displayMessage)
                && Objects.equals(this.eventType, other.eventType)
                && Objects.equals(this.outcome, other.outcome)
                && Objects.equals(this.published, other.published)
                && Objects.equals(this.securityContext, other.securityContext)
                && Objects.equals(this.severity, other.severity)
                && Objects.equals(this.debugContext, other.debugContext)
                && Objects.equals(this.legacyEventType, other.legacyEventType)
                && Objects.equals(this.transaction, other.transaction)
                && Objects.equals(this.uuid, other.uuid)
                && Objects.equals(this.version, other.version)
                && Objects.equals(this.request, other.request)
                && Objects.equals(this.target, other.target);
    }
}
