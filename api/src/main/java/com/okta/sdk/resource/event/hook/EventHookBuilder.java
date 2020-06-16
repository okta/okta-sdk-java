package com.okta.sdk.resource.event.hook;

import com.okta.commons.lang.Classes;
import com.okta.sdk.client.Client;

import java.util.List;

public interface EventHookBuilder {
    EventHookBuilder setName(String name);

    EventHookBuilder setUrl(String url);

    EventHookBuilder setAuthorizationHeaderValue(String authorizationHeaderValue);

    EventHookBuilder setHeaders(List<EventHookChannelConfigHeader> headers);

    EventHookBuilder setVersion(String version);

    EventHook buildAndCreate(Client client);

    static EventHookBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.EventHookBuilder");
    }
}
