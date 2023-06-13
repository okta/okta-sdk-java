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
package com.okta.sdk.resource.application;

import com.okta.commons.lang.Classes;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.ApplicationApi;
import org.openapitools.client.model.Application;
import org.openapitools.client.model.ApplicationSignOnMode;

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

    Application buildAndCreate(ApplicationApi client) throws ApiException;
}
