package com.okta.sdk.resource.application;

import com.okta.commons.lang.Classes;
import com.okta.sdk.client.Client;

public interface ApplicationBuilder<T extends ApplicationBuilder> {
    static ApplicationBuilder<ApplicationBuilder> instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.DefaultApplicationBuilder");
    }

    T setName(String name);

    T setLabel(String label);

    T setErrorRedirectUrl(String errorRedirectUrl);

    T setLoginRedirectUrl(String loginRedirectUrl);

    T setSelfService(Boolean selfService);

    T setSignOnMode(ApplicationSignOnMode signOnMode);

    T setIOS(Boolean iOS);

    T setWeb(Boolean web);

    Application buildAndCreate(Client client);
}
