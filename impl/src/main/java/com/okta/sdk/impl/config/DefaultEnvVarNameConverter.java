/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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

        StringBuilder sb = new StringBuilder();

        for(char c : dottedPropertyName.toCharArray()) {
            if (c == '.') {
                sb.append('_');
                continue;
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
        if ("OKTA_CLIENT_TOKEN".equals(envVarName)) {
            return "okta.client.token";
        }
        if ("OKTA_CLIENT_AUTHENTICATIONSCHEME".equals(envVarName)) {
            return "okta.client.authenticationScheme";
        }
        if ("OKTA_CLIENT_ORGURL".equals(envVarName)) {
            return "okta.client.orgUrl";
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
