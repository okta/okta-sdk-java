/*
 * Copyright 2014 Stormpath, Inc.
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
package com.okta.sdk.impl.authc.credentials

import com.okta.sdk.authc.credentials.ClientCredentials
import com.okta.sdk.impl.config.ClientConfiguration
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals


class ConfigurationCredentialsProviderTest {

    @Test
    public void configuredCredentialsReturned() {

        String secret = UUID.randomUUID().toString()

        ClientConfiguration clientConfiguration = new ClientConfiguration()
        clientConfiguration.setApiKeySecret(secret)

        ClientCredentials clientCredentials = new ConfigurationCredentialsProvider(clientConfiguration).getClientCredentials()

        assertEquals(clientCredentials.getCredentials(), secret)

    }

}
