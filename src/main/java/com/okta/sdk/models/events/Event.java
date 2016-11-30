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