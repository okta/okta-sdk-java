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
package com.okta.sdk.impl.config

import com.okta.sdk.client.ClientBuilder
import org.testng.annotations.Test

import java.lang.reflect.Modifier

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class DefaultEnvVarNameConverterTest {

    @Test
    void testToEnvVarName() {
        def converter = new DefaultEnvVarNameConverter()
        def envVarName = converter.toEnvVarName('okta.client.apiKey.id')
        assertThat envVarName, is('OKTA_CLIENT_APIKEY_ID')
    }

    @Test
    void testPropNameForApiKeyIdEnvVar() {
        def converter = new DefaultEnvVarNameConverter()
        def propName = converter.toDottedPropertyName('OKTA_API_KEY_ID')
        assertThat propName, is('okta.api.key.id')
    }

    @Test
    void testPropNameForApiKeySecretEnvVar() {
        def converter = new DefaultEnvVarNameConverter()
        def propName = converter.toDottedPropertyName('OKTA_CLIENT_TOKEN')
        assertThat propName, is('okta.client.token')
    }

    @Test
    void testPropNameForApiKeyFileEnvVar() {
        def converter = new DefaultEnvVarNameConverter()
        def propName = converter.toDottedPropertyName('OKTA_CONFIG_FILE')
        assertThat propName, is('okta.config.file')
    }

    @Test
    void testPropNameForNormalEnvVar() {
        def converter = new DefaultEnvVarNameConverter()
        def propName = converter.toDottedPropertyName('OKTA_CLIENT_AUTHENTICATIONSCHEME')
        assertThat propName, is('okta.client.authenticationScheme')
    }

    @Test
    void testAllClientBuilderStaticVars() {
        def converter = new DefaultEnvVarNameConverter()
        // loop over all the config property static fields ClientBuilder, and make sure they convert correctly
        Arrays.stream(ClientBuilder.class.getDeclaredFields())
            .filter { Modifier.isStatic(it.getModifiers()) }
            .filter { String.class == it.getType() }
            .filter { it.name.startsWith("DEFAULT_CLIENT_") }
            .map {(String) it.get(null)}
            .forEach {
             println it
                // we want to round trip these properties
                def env = converter.toEnvVarName(it)
                def backToDotted = converter.toDottedPropertyName(env)
                assertThat backToDotted, is(it)
            }
    }
}
