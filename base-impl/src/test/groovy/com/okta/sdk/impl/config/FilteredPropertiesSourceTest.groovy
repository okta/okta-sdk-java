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
package com.okta.sdk.impl.config

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNull

/**
 * @since 1.0.RC9
 */
class FilteredPropertiesSourceTest {

    @Test
    void test() {

        def filteredPropertiesSource = new FilteredPropertiesSource(
            new OptionalPropertiesSource(
                new PropertiesSource() {
                    @Override
                    Map<String, String> getProperties() {
                        return [
                            "okta_key_one":"okta_value_one",
                            "my_special_key":"my_special_value"
                        ]
                    }
                }
            ),
            new FilteredPropertiesSource.Filter() {
                @Override
                public String[] map(String key, String value) {
                    // only return properties that start with okta_
                    if (key.startsWith("okta_")) {
                        return [key, value].toArray()
                    }
                    return null
                }
            }
        )
        def result = filteredPropertiesSource.getProperties()

        assertNull result.get("my_special_key")
        assertEquals result.get("okta_key_one"), "okta_value_one"
    }
}
