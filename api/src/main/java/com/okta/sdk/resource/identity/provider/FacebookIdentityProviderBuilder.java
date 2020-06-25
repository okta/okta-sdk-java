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
package com.okta.sdk.resource.identity.provider;

import com.okta.commons.lang.Classes;

public interface FacebookIdentityProviderBuilder extends IdentityProviderBuilder<FacebookIdentityProviderBuilder> {

    static FacebookIdentityProviderBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.identity.provider.DefaultFacebookIdentityProviderBuilder");
    }

    FacebookIdentityProviderBuilder setIsProfileMaster(Boolean isProfileMaster);

    FacebookIdentityProviderBuilder setUserNameTemplate(String userNameTemplate);
}
