/*
 * Copyright 2021-Present Okta, Inc.
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
package com.okta.sdk.impl.resource

import com.okta.sdk.impl.ds.InternalDataStore
import com.okta.sdk.resource.application.ApplicationSignOnMode
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.mockito.Mockito.*

class AbstractPropertyRetrieverTest {

    @Test(dataProvider = "signOnModes")
    void ApplicationSignOnModeTest(String configValue, ApplicationSignOnMode expectedValue) {
        def internalDataStore = mock(InternalDataStore.class)

        Map<String,Object> data = new HashMap<>()
        data.put("signOnMode", configValue)
        def absResource = mock(AbstractResource.class,
            withSettings().useConstructor(internalDataStore, data).defaultAnswer(CALLS_REAL_METHODS)
        )

        def res = absResource.getEnumProperty("signOnMode", ApplicationSignOnMode)
        assertThat(res, is(expectedValue))
    }

    @DataProvider
    Object[][] signOnModes() {
        return [
            ["BOOKMARK", ApplicationSignOnMode.BOOKMARK],
            ["BASIC_AUTH", ApplicationSignOnMode.BASIC_AUTH],
            ["BROWSER_PLUGIN", ApplicationSignOnMode.BROWSER_PLUGIN],
            ["SECURE_PASSWORD_STORE", ApplicationSignOnMode.SECURE_PASSWORD_STORE],
            ["AUTO_LOGIN", ApplicationSignOnMode.AUTO_LOGIN],
            ["WS_FEDERATION", ApplicationSignOnMode.WS_FEDERATION],
            ["SAML_2_0", ApplicationSignOnMode.SAML_2_0],
            ["OPENID_CONNECT", ApplicationSignOnMode.OPENID_CONNECT],
            ["SAML_1_1", ApplicationSignOnMode.SAML_1_1],
            ["RADIUS_FOR_GENERIC_APP", ApplicationSignOnMode.SDK_UNKNOWN],
            ["RADIUS_FOR_CISCO_ASA", ApplicationSignOnMode.SDK_UNKNOWN],
            ["AWS_FEDERATED_LOGIN", ApplicationSignOnMode.SDK_UNKNOWN],
            ["UNKNOWN_VALUE", ApplicationSignOnMode.SDK_UNKNOWN],
            ["", ApplicationSignOnMode.SDK_UNKNOWN]
        ]
    }
}
