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

package com.okta.sdk.models.events;

import com.okta.sdk.framework.ApiObject;
import org.joda.time.DateTime;

public class Event extends ApiObject {

    /**
     * Unique key for event.
     */
    private String id;

    /**
     * Timestamp when event was published.
     */
    private DateTime published;

    /**
     * Identifies the request.
     */
    private String requestId;

    /**
     * Session in which the event occurred.
     */
    private String sessionId;

    /**
     * Action performed by the event.
     */
    private Action action;

    /**
     * Actors causing the event.
     */
    private Actor[] actors;

    /**
     * Event targets
     */
    private Target[] targets;

    /**
     * Returns the ID.
     * @return {@link String}
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the ID.
     * @param val {@link String}
     */
    public void setId(String val) {
        this.id = val;
    }

    /**
     * Returns the published time.
     * @return {@link DateTime}
     */
    public DateTime getPublished() {
        return this.published;
    }

    /**
     * Sets the published time.
     * @param val {@link String}
     */
    public void setPublished(DateTime val) {
        this.published = val;
    }

    /**
     * Returns the requestId.
     * @return {@link String}
     */
    public String getRequestId() {
        return this.requestId;
    }

    /**
     * Sets the requestId.
     * @param val {@link String}
     */
    public void setRequestId(String val) {
        this.requestId = val;
    }

    /**
     * Returns the sessionId.
     * @return {@link String}
     */
    public String getSessionId() {
        return this.sessionId;
    }

    /**
     * Sets the sessionId.
     * @param val {@link String}
     */
    public void setSessionId(String val) {
        this.sessionId = val;
    }

    /**
     * Returns the action.
     * @return {@link Action}
     */
    public Action getAction() {
        return this.action;
    }

    /**
     * Sets the action.
     * @param val {@link Action}
     */
    public void setAction(Action val) {
        this.action = val;
    }

    /**
     * Returns the actors.
     * @return {@link Actor}
     */
    public Actor[] getActors() {
        return this.actors;
    }

    /**
     * Sets the actors.
     * @param val {@link Actor}
     */
    public void setActors(Actor[] val) {
        this.actors = val;
    }

    /**
     * Returns the targets.
     * @return {@link Target}
     */
    public Target[] getTargets() {
        return this.targets;
    }

    /**
     * Sets the targets.
     * @param val {@link Target}
     */
    public void setTargets(Target[] val) {
        this.targets = val;
    }
}
