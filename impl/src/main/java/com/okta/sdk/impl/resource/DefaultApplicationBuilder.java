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
package com.okta.sdk.impl.resource;

import com.okta.commons.lang.Strings;
import com.okta.sdk.resource.application.ApplicationBuilder;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.ApplicationApi;
import org.openapitools.client.model.Application;
import org.openapitools.client.model.ApplicationAccessibility;
import org.openapitools.client.model.ApplicationSignOnMode;
import org.openapitools.client.model.ApplicationVisibility;
import org.openapitools.client.model.ApplicationVisibilityHide;

import java.util.Objects;

public class DefaultApplicationBuilder<T extends ApplicationBuilder> implements ApplicationBuilder<T> {

    protected String name;
    protected String label;
    protected String errorRedirectUrl;
    protected String loginRedirectUrl;
    protected Boolean selfService;
    protected ApplicationSignOnMode signOnMode;
    protected Boolean iOS;
    protected Boolean web;

    @Override
    public T setName(String name) {
        this.name = name;
        return self();
    }

    @Override
    public T setLabel(String label) {
        this.label = label;
        return self();
    }

    @Override
    public T setErrorRedirectUrl(String errorRedirectUrl) {
        this.errorRedirectUrl = errorRedirectUrl;
        return self();
    }

    @Override
    public T setLoginRedirectUrl(String loginRedirectUrl) {
        this.loginRedirectUrl = loginRedirectUrl;
        return self();
    }

    @Override
    public T setSelfService(Boolean selfService) {
        this.selfService = selfService;
        return self();
    }

    @Override
    public T setSignOnMode(ApplicationSignOnMode signOnMode) {
        this.signOnMode = signOnMode;
        return self();
    }

    @Override
    public T setIOS(Boolean iOS) {
        this.iOS = iOS;
        return self();
    }

    @Override
    public T setWeb(Boolean web) {
        this.web = web;
        return self();
    }

    @SuppressWarnings("unchecked")
    protected T self() { return (T) this;}

    @Override
    public Application buildAndCreate(ApplicationApi client) throws ApiException {
        return client.createApplication(build(), false, null);
    }

    private Application build(){

        Application application = new Application();

        if (Strings.hasText(label)) application.setLabel(label);

        if (Objects.nonNull(signOnMode)) application.setSignOnMode(signOnMode);

        // Accessibility
        ApplicationAccessibility applicationAccessibility = new ApplicationAccessibility();

        if (Strings.hasText(loginRedirectUrl))
            applicationAccessibility.setLoginRedirectUrl(loginRedirectUrl);

        if (Strings.hasText(errorRedirectUrl))
            applicationAccessibility.setErrorRedirectUrl(errorRedirectUrl);

        if (Objects.nonNull(selfService))
            applicationAccessibility.setSelfService(selfService);

        application.setAccessibility(applicationAccessibility);

        // Visibility
        ApplicationVisibility applicationVisibility = new ApplicationVisibility();

        ApplicationVisibilityHide applicationVisibilityHide = new ApplicationVisibilityHide();

        if (Objects.nonNull(iOS))
            applicationVisibilityHide.setiOS(iOS);

        if (Objects.nonNull(web))
            applicationVisibilityHide.setWeb(web);

        applicationVisibility.setHide(applicationVisibilityHide);

        application.setVisibility(applicationVisibility);

        return application;
    }

}
