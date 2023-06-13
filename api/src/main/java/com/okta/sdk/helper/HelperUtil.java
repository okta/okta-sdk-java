/*
 * Copyright 2023-Present Okta, Inc.
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
package com.okta.sdk.helper;

import com.okta.commons.lang.Assert;
import org.openapitools.client.model.*;

public class HelperUtil {

    static Class<? extends Application> getApplicationType(Application application) {

        Assert.notNull(application);
        Assert.notNull(application.getSignOnMode());

        switch (application.getSignOnMode()) {
            case AUTO_LOGIN:
                return AutoLoginApplication.class;
            case BASIC_AUTH:
                return BasicAuthApplication.class;
            case BOOKMARK:
                return BookmarkApplication.class;
            case BROWSER_PLUGIN:
                return BrowserPluginApplication.class;
            case OPENID_CONNECT:
                return OpenIdConnectApplication.class;
            case SAML_1_1:
            case SAML_2_0:
                return SamlApplication.class;
            case SECURE_PASSWORD_STORE:
                return SecurePasswordStoreApplication.class;
            case WS_FEDERATION:
                return WsFederationApplication.class;
            default:
                return Application.class;
        }
    }

    static Class<? extends Policy> getPolicyType(Policy policy) {

        Assert.notNull(policy);
        Assert.notNull(policy.getType());

        switch (policy.getType()) {
            case ACCESS_POLICY:
                return AccessPolicy.class;
            case IDP_DISCOVERY:
                return IdentityProviderPolicy.class;
            case MFA_ENROLL:
                return MultifactorEnrollmentPolicy.class;
            case OAUTH_AUTHORIZATION_POLICY:
                return AuthorizationServerPolicy.class;
            case OKTA_SIGN_ON:
                return OktaSignOnPolicy.class;
            case PASSWORD:
                return PasswordPolicy.class;
            case PROFILE_ENROLLMENT:
                return ProfileEnrollmentPolicy.class;
        }

        return Policy.class;
    }

    static Class<? extends UserFactor> getUserFactorType(UserFactor userFactor) {

        Assert.notNull(userFactor);
        Assert.notNull(userFactor.getFactorType());

        switch (userFactor.getFactorType()) {
            case CALL:
                return CallUserFactor.class;

            case EMAIL:
                return EmailUserFactor.class;

            case HOTP:
                return CustomHotpUserFactor.class;

            case PUSH:
                return PushUserFactor.class;

            case SMS:
                return SmsUserFactor.class;

            case QUESTION:
                return SecurityQuestionUserFactor.class;

            case TOKEN:
            case TOKEN_HARDWARE:
            case TOKEN_HOTP:
            case TOKEN_SOFTWARE_TOTP:
                return TokenUserFactor.class;

            case U2F:
                return U2fUserFactor.class;

            case WEB:
                return WebUserFactor.class;

            case WEBAUTHN:
                return WebAuthnUserFactor.class;

            default:
                return UserFactor.class;
        }
    }
}
