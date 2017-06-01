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
package com.okta.sdk.impl.authc.credentials;

import com.okta.sdk.impl.test.RestoreEnvironmentVariables;
import com.okta.sdk.impl.test.RestoreSystemProperties;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static com.okta.sdk.impl.test.RestoreEnvironmentVariables.setEnvironmentVariable;

@Listeners({RestoreSystemProperties.class, RestoreEnvironmentVariables.class})
public class EnvironmentVariableFileCredentialsProviderTest {

    @Test
    public void credentialsReadFromEnvirionmentVariableFileLocation() {

        setEnvironmentVariable("OKTA_API_KEY_FILE", "classpath:credentials.txt");

        ClientCredentials clientCredentials = new EnvironmentVariableFileCredentialsProvider().getClientCredentials();

        assertNotNull(clientCredentials);
        assertEquals(clientCredentials.getBaseUrl(), "1234");
        assertEquals(clientCredentials.getSecret(), "5678");
    }
}
