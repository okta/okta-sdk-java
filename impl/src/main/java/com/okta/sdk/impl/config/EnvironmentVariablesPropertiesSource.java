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

import com.okta.sdk.lang.Collections;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnvironmentVariablesPropertiesSource implements PropertiesSource {

    @Override
    public Map<String,String> getProperties() {

        Map<String,String> envVars = System.getenv();

        if (!Collections.isEmpty(envVars)) {
            return new LinkedHashMap<String, String>(envVars);
        }

        return java.util.Collections.emptyMap();
    }

    public static PropertiesSource oktaFilteredPropertiesSource() {
        return new OktaFilteredEnvironmentPropertiesSource();
    }

    private static class OktaFilteredEnvironmentPropertiesSource extends FilteredPropertiesSource {

        private static final EnvVarNameConverter envVarNameConverter = new DefaultEnvVarNameConverter();

        private OktaFilteredEnvironmentPropertiesSource() {
            super(new EnvironmentVariablesPropertiesSource(),
                    (key, value) -> {
                        if (key.startsWith("OKTA_")) {
                            //we want to convert env var naming convention to dotted property convention
                            //to allow overrides.  Overrides work based on overriding identically-named keys:
                            key = envVarNameConverter.toDottedPropertyName(key);
                            return new String[]{key, value};
                        } else {
                            return null;
                        }
                    }
            );
        }
    }
}
