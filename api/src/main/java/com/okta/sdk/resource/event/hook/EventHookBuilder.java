/*
 * Copyright 2020-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.resource.event.hook;

import com.okta.commons.lang.Classes;
import com.okta.sdk.client.Client;

public interface EventHookBuilder {
    EventHookBuilder setName(String name);

    EventHookBuilder setUrl(String url);

    EventHookBuilder setAuthorizationHeaderValue(String authorizationHeaderValue);

    EventHookBuilder addHeader(String name, String value);

    EventHook buildAndCreate(Client client);

    static EventHookBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.event.hook.DefaultEventHookBuilder");
    }
}
