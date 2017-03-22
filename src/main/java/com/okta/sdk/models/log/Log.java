/*!
 * Copyright (c) 2015-2017, Okta, Inc. and/or its affiliates. All rights reserved.
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
     * Describes entity that performed the action.
     */
    private final LogResource actor;

    /**
     * Identifies the client that requested the action.
     */
    private final LogClient client;

    /**
     * Identifies the authentication data of the action.
     */
    private final LogAuthenticationContext authenticationContext;

    /**
     * Display message regarding the event.
     */
    private final String displayMessage;

    /**
     * Type of event that was published.
     */
    private final String eventType;

    /**
     * Identifies the outcome of the action.
     */
    private final LogOutcome outcome;

    /**
     * Timestamp when event was published.
     */
    private final String published;

    /**
     * Identifies the security data of the action.
     */
    private final LogSecurityContext securityContext;

    /**
     * Indicates how severe the event is.
     */
    private final Severity severity;

    /**
     * Identifies the debug request data of the action.
     */
    private final LogDebugContext debugContext;

    /**
     * Legacy event type.
     */
    private final String legacyEventType;

    /**
     * Identifies the transaction details of the action.
     */
    private final LogTransaction transaction;

    /**
     * Unique key for event.
     */
    private final String uuid;

    /**
     * Version for event.
     */
    private final String version;

    /**
     * Request of the log.
     */
    private final LogRequest request;

    /**
     * List of targets
     */
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
    /**
     * Returns the published timestamp.
     * @return {@link String}
     */
    public String getPublished() {
        return published;
    }

    /**
     * Returns the eventType.
     * @return {@link String}
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Returns the displayMessage.
     * @return {@link String}
     */
    public String getDisplayMessage() {
        return displayMessage;
    }

    /**
     * Returns the severity.
     * @return {@link Severity}
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * Returns the client.
     * @return {@link LogClient}
     */
    public LogClient getClient() {
        return client;
    }

    /**
     * Returns the actor.
     * @return {@link LogResource}
     */
    public LogResource getActor() {
        return actor;
    }

    /**
     * Returns the outcome.
     * @return {@link LogOutcome}
     */
    public LogOutcome getOutcome() {
        return outcome;
    }

    /**
     * Returns the target.
     * @return {@link List}
     */
    public List<LogResource> getTarget() {
        return target;
    }

    /**
     * Returns the authenticationContext.
     * @return {@link LogAuthenticationContext}
     */
    public LogAuthenticationContext getAuthenticationContext() {
        return authenticationContext;
    }

    /**
     * Returns the securityContext.
     * @return {@link LogSecurityContext}
     */
    public LogSecurityContext getSecurityContext() {
        return securityContext;
    }

    /**
     * Returns the debugContext.
     * @return {@link LogDebugContext}
     */
    public LogDebugContext getDebugContext() {
        return debugContext;
    }

    /**
     * Returns the legacEventType.
     * @return {@link String}
     */
    public String getLegacyEventType() {
        return legacyEventType;
    }

    /**
     * Returns the transaction.
     * @return {@link LogTransaction}
     */
    public LogTransaction getTransaction() {
        return transaction;
    }

    /**
     * Returns the uuid.
     * @return {@link String}
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Returns the version.
     * @return {@link String}
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the request.
     * @return {@link LogRequest}
     */
    public LogRequest getRequest() {
        return request;
    }

    /**
     * Hashes the object.
     * @return {@link Integer}
     */
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

    /**
     * Overriding method for equals.
     * @param obj {@link Object}
     * @return {@link Boolean}
     */
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
