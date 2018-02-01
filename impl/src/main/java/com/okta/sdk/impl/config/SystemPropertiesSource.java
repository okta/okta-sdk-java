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

import com.okta.sdk.lang.Strings;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class SystemPropertiesSource implements PropertiesSource {

    @Override
    public Map<String,String> getProperties() {

        Map<String,String> properties = new LinkedHashMap<String, String>();

        Properties systemProps = System.getProperties();

        if (systemProps != null && !systemProps.isEmpty()) {

            Enumeration e = systemProps.propertyNames();

            while(e.hasMoreElements()) {

                Object name = e.nextElement();
                String key = String.valueOf(name);
                String value = systemProps.getProperty(key);

                if (Strings.hasText(value)) {
                    properties.put(key, value);
                }
            }
        }

        return properties;
    }

    public static PropertiesSource oktaFilteredPropertiesSource() {
        return new OktaFilteredSystemPropertiesSource();
    }

    private static class OktaFilteredSystemPropertiesSource extends FilteredPropertiesSource {

        private OktaFilteredSystemPropertiesSource() {
            super(new EnvironmentVariablesPropertiesSource(),
                    (key, value) -> {
                            if (key.startsWith("okta.")) {
                                return new String[]{key, value};
                            }
                            return null;
                    }
            );
        }
    }
}
