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
package com.okta.sdk.impl.resource.event.hook;

import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;

import com.okta.sdk.client.Client;
import com.okta.sdk.resource.event.hook.EventHook;
import com.okta.sdk.resource.event.hook.EventHookBuilder;
import com.okta.sdk.resource.event.hook.EventHookChannel;
import com.okta.sdk.resource.event.hook.EventHookChannelConfig;
import com.okta.sdk.resource.event.hook.EventHookChannelConfigHeader;
import com.okta.sdk.resource.event.hook.EventHookChannelConfigAuthScheme;
import com.okta.sdk.resource.event.hook.EventHookChannelConfigAuthSchemeType;
import com.okta.sdk.resource.event.hook.EventSubscriptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Builder for {@link EventHook}.
 * @since 2.0.0
 */
public class DefaultEventHookBuilder implements EventHookBuilder {

    private static final String LIFECYCLE_EVENT_CREATE = "user.lifecycle.create";
    private static final String LIFECYCLE_EVENT_ACTIVATE = "user.lifecycle.activate";

    private String name;
    private String url;
    private String authorizationHeaderValue;
    private Map<String, String> headerMap = Maps.newHashMap();
    private String version;

    @Override
    public EventHookBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public EventHookBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public EventHookBuilder setAuthorizationHeaderValue(String authorizationHeaderValue) {
        this.authorizationHeaderValue = authorizationHeaderValue;
        return this;
    }

    @Override
    public EventHookBuilder addHeader(String name, String value) {
        headerMap.put(name, value);
        return this;
    }

    @Override
    public EventHookBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public EventHook buildAndCreate(Client client) {

        EventSubscriptions eventSubscriptions = client.instantiate(EventSubscriptions.class)
            .setType(EventSubscriptions.TypeEnum.EVENT_TYPE)
            .setItems(Arrays.asList(LIFECYCLE_EVENT_CREATE, LIFECYCLE_EVENT_ACTIVATE));

        EventHookChannelConfigAuthScheme eventHookChannelConfigAuthScheme =
            client.instantiate(EventHookChannelConfigAuthScheme.class)
                .setType(EventHookChannelConfigAuthSchemeType.HEADER)
                .setKey(HttpHeaders.AUTHORIZATION)
                .setValue(authorizationHeaderValue);

        List<EventHookChannelConfigHeader> headers = new ArrayList<>();

        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            headers.add(client.instantiate(EventHookChannelConfigHeader.class)
                .setKey(entry.getKey())
                .setValue(entry.getValue()));
        }

        EventHookChannelConfig eventHookChannelConfig = client.instantiate(EventHookChannelConfig.class)
            .setUri(url)
            .setHeaders(headers)
            .setAuthScheme(eventHookChannelConfigAuthScheme);

        EventHookChannel eventHookChannel = client.instantiate(EventHookChannel.class)
            .setType(EventHookChannel.TypeEnum.HTTP)
            .setVersion(version)
            .setConfig(eventHookChannelConfig);

        EventHook createdEventHook = client.createEventHook(client.instantiate(EventHook.class)
            .setName(name)
            .setEvents(eventSubscriptions)
            .setChannel(eventHookChannel));

        return createdEventHook;
    }

}
