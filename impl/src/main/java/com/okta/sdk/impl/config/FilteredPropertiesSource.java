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
import com.okta.sdk.lang.Collections;

import java.util.LinkedHashMap;
import java.util.Map;

public class FilteredPropertiesSource implements PropertiesSource {

    private final PropertiesSource propertiesSource;
    private final Filter filter;

    public FilteredPropertiesSource(PropertiesSource propertiesSource, Filter filter) {
        Assert.notNull(propertiesSource, "source cannot be null.");
        Assert.notNull(filter, "filter cannot be null.");
        this.propertiesSource = propertiesSource;
        this.filter = filter;
    }

    @Override
    public Map<String,String> getProperties() {

        Map<String,String> props = this.propertiesSource.getProperties();

        Map<String,String> retained = new LinkedHashMap<String, String>();

        if (!Collections.isEmpty(props)) {

            props.forEach((key, value) -> {
                String[] evaluated = filter.map(key, value);
                if (evaluated != null) {
                    Assert.isTrue(2 == evaluated.length,
                            "Filter returned string array must have a length of 2 (key/value pair)");
                    retained.put(evaluated[0], evaluated[1]);
                }
            });
        }

        return retained;
    }

    public interface Filter {
        String[] map(String key, String value);
    }
}
