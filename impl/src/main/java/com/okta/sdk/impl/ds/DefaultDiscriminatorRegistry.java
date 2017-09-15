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
package com.okta.sdk.impl.ds;

import com.okta.sdk.lang.Classes;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultDiscriminatorRegistry implements DiscriminatorRegistry {

    private Map<Class, AttributeDiscriminationStrategy> supportedClassMap = new HashMap<>();

    public DefaultDiscriminatorRegistry() {

        DiscriminatorConfig.loadConfig().getConfig().forEach((key, value) -> {
            Class clazz = Classes.forName(key);

            Map<String, Class> values = value.getValues().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> Classes.forName(e.getValue())));
            supportedClassMap.put(clazz, new AttributeDiscriminationStrategy(value.getFieldName(), values));
        });
    }

    public boolean supportedClass(Class clazz) {
        return supportedClassMap.containsKey(clazz);
    }

    public <P> Class<P> resolve(Class<P> clazz, Map data) {

        if (supportedClass(clazz)) {
            return supportedClassMap.get(clazz).resolve(clazz, data);
        }
        return clazz;
    }

    private static class AttributeDiscriminationStrategy {

        private final String key;
        private final Map<String, Class> values;

        private AttributeDiscriminationStrategy(String key, Map<String, Class> values) {
            this.key = key;
            this.values = values;
        }

        private <P> Class<P> resolve(Class<P> clazz, Map data) {

            if (data.containsKey(key)) {
                Object value = data.get(key);
                Class<P> result = values.get(value);
                if (result != null) {
                    return result;
                }
            }
            return clazz;
        }
    }
}