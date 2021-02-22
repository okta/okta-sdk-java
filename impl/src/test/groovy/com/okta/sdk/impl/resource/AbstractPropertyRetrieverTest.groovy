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
    void testTest(String configValue, ApplicationSignOnMode expectedValue) {
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
            ["RADIUS_FOR_GENERIC_APP", ApplicationSignOnMode.CUSTOM],
            ["AWS_FEDERATED_LOGIN", ApplicationSignOnMode.CUSTOM],
            ["UNKNOWN_VALUE", ApplicationSignOnMode.CUSTOM],
            ["", ApplicationSignOnMode.CUSTOM]
        ]
    }
}
