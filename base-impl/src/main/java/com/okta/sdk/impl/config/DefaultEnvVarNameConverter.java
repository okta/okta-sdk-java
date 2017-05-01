/*
 * Copyright 2017 Okta
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
package com.okta.sdk.impl.config;

import com.okta.sdk.lang.Assert;
import com.okta.sdk.lang.Strings;

public class DefaultEnvVarNameConverter implements EnvVarNameConverter {

    @Override
    public String toEnvVarName(String dottedPropertyName) {
        Assert.hasText(dottedPropertyName, "dottedPropertyName argument cannot be null or empty.");
        dottedPropertyName = Strings.trimWhitespace(dottedPropertyName);

        //special cases (camel case):
        if ("okta.client.baseUrl".equals(dottedPropertyName)) {
            return "OKTA_CLIENT_BASEURL";
        }
        if ("okta.client.apiKey.id".equals(dottedPropertyName)) {
            return "OKTA_API_KEY_ID";
        }
        if ("okta.client.apiKey.secret".equals(dottedPropertyName)) {
            return "OKTA_API_KEY_SECRET";
        }
        if ("okta.client.apiKey.file".equals(dottedPropertyName)) {
            return "OKTA_API_KEY_FILE";
        }
        if ("okta.client.authenticationScheme".equals(dottedPropertyName)) {
            return "OKTA_AUTHENTICATION_SCHEME";
        }

        StringBuilder sb = new StringBuilder();

        for(char c : dottedPropertyName.toCharArray()) {
            if (c == '.') {
                sb.append('_');
                continue;
            }
            if (Character.isUpperCase(c)) {
                sb.append('_');
            }
            sb.append(Character.toUpperCase(c));
        }

        return sb.toString();
    }

    @Override
    public String toDottedPropertyName(String envVarName) {
        Assert.hasText(envVarName, "envVarName argument cannot be null or empty.");
        envVarName = Strings.trimWhitespace(envVarName);

        //special cases (camel case):
        if ("OKTA_API_KEY_ID".equals(envVarName)) {
            return "okta.client.apiKey.id";
        }
        if ("OKTA_API_KEY_SECRET".equals(envVarName)) {
            return "okta.client.apiKey.secret";
        }
        if ("OKTA_API_KEY_FILE".equals(envVarName)) {
            return "okta.client.apiKey.file";
        }
        if ("OKTA_AUTHENTICATION_SCHEME".equals(envVarName)) {
            return "okta.client.authenticationScheme";
        }
        if ("OKTA_CLIENT_BASEURL".equals(envVarName)) {
            return "okta.client.baseUrl";
        }
        if ("OKTA_WEB_VERIFYEMAIL_ENABLED".equals((envVarName))) {
            return "okta.web.verifyEmail.enabled";
        }
        if ("OKTA_WEB_FORGOTPASSWORD_ENABLED".equals((envVarName))) {
            return "okta.web.forgotPassword.enabled";
        }
        if ("OKTA_WEB_CHANGEPASSWORD_ENABLED".equals((envVarName))) {
            return "okta.web.changePassword.enabled";
        }
        if ("OKTA_WEB_IDSITE_ENABLED".equals((envVarName))) {
            return "okta.web.idSite.enabled";
        }

        //default cases:
        StringBuilder sb = new StringBuilder();

        for(char c : envVarName.toCharArray()) {
            if (c == '_') {
                sb.append('.');
                continue;
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }
}
