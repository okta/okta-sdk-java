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
package com.okta.sdk.impl.resource.inline.hook;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.inline.hook.InlineHook;
import com.okta.sdk.resource.inline.hook.InlineHookBuilder;
import com.okta.sdk.resource.inline.hook.InlineHookChannel;
import com.okta.sdk.resource.inline.hook.InlineHookChannelConfig;
import com.okta.sdk.resource.inline.hook.InlineHookChannelConfigAuthScheme;
import com.okta.sdk.resource.inline.hook.InlineHookChannelConfigHeaders;
import com.okta.sdk.resource.inline.hook.InlineHookType;

import java.util.List;
import java.util.Map;

/**
 * Builder for {@link InlineHook}.
 * @since 2.0.0
 */
public class DefaultInlineHookBuilder implements InlineHookBuilder {

    private static final String VERSION = "1.0.0";

    private String name;
    private InlineHookType hookType;
    private InlineHookChannel.TypeEnum channelType;
    private String url;
    private String authorizationHeaderValue;
    private Map<String, String> headerMap = Maps.newHashMap();

    @Override
    public InlineHookBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public InlineHookBuilder setHookType(InlineHookType hookType) {
        this.hookType = hookType;
        return this;
    }

    @Override
    public InlineHookBuilder setChannelType(InlineHookChannel.TypeEnum channelType) {
        this.channelType = channelType;
        return this;
    }

    @Override
    public InlineHookBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public InlineHookBuilder setAuthorizationHeaderValue(String authorizationHeaderValue) {
        this.authorizationHeaderValue = authorizationHeaderValue;
        return this;
    }

    @Override
    public InlineHookBuilder addHeader(String name, String value) {
        headerMap.put(name, value);
        return this;
    }

    @Override
    public InlineHook buildAndCreate(Client client) {
        List<InlineHookChannelConfigHeaders> headers = Lists.newArrayList();

        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            headers.add(client.instantiate(InlineHookChannelConfigHeaders.class)
                .setKey(entry.getKey())
                .setValue(entry.getValue()));
        }

        InlineHookChannelConfigAuthScheme inlineHookChannelConfigAuthScheme =
            client.instantiate(InlineHookChannelConfigAuthScheme.class)
                .setType("HEADER")
                .setKey(HttpHeaders.AUTHORIZATION)
                .setValue(authorizationHeaderValue);

        InlineHookChannelConfig inlineHookChannelConfig = client.instantiate(InlineHookChannelConfig.class)
            .setUri(url)
            .setHeaders(headers)
            .setAuthScheme(inlineHookChannelConfigAuthScheme);

        InlineHookChannel inlineHookChannel = client.instantiate(InlineHookChannel.class)
            .setType(channelType)
            .setVersion(VERSION)
            .setConfig(inlineHookChannelConfig);

        InlineHook createdInlineHook = client.instantiate(InlineHook.class)
            .setName(name)
            .setType(hookType)
            .setVersion(VERSION)
            .setChannel(inlineHookChannel);

        return createdInlineHook;
    }
}
