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
package com.okta.sdk.resource.inline.hook;

import com.okta.commons.lang.Classes;
import com.okta.sdk.client.Client;

public interface InlineHookBuilder {

    InlineHookBuilder setName(String name);

    InlineHookBuilder setHookType(InlineHook.TypeEnum hookType);

    InlineHookBuilder setChannelType(InlineHookChannel.TypeEnum channelType);

    InlineHookBuilder setUrl(String url);

    InlineHookBuilder setAuthorizationHeaderValue(String authorizationHeaderValue);

    InlineHookBuilder addHeader(String name, String value);

    InlineHook buildAndCreate(Client client);

    static InlineHookBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.inline.hook.DefaultInlineHookBuilder");
    }
}