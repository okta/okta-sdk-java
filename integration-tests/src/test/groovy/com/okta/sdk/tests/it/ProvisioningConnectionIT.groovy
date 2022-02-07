/*
 * Copyright 2022-Present Okta, Inc.
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
package com.okta.sdk.tests.it

import com.okta.sdk.resource.application.Application
import com.okta.sdk.resource.application.Org2OrgApplication
import com.okta.sdk.resource.application.Org2OrgApplicationSettings
import com.okta.sdk.resource.application.Org2OrgApplicationSettingsApp
import com.okta.sdk.resource.application.ProvisioningConnection
import com.okta.sdk.resource.application.ProvisioningConnectionAuthScheme
import com.okta.sdk.resource.application.ProvisioningConnectionProfile
import com.okta.sdk.resource.application.ProvisioningConnectionRequest
import com.okta.sdk.resource.application.ProvisioningConnectionStatus
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue

/**
 * Integration tests of
 * @see ProvisioningConnection
 */
class ProvisioningConnectionIT extends ITSupport {

    private Application createApp() {
        Application application = client.createApplication(
            client.instantiate(Org2OrgApplication)
                .setLabel("java-sdk-it-" + UUID.randomUUID().toString())
                .setSettings(client.instantiate(Org2OrgApplicationSettings)
                    .setApp(client.instantiate(Org2OrgApplicationSettingsApp)
                        .setAcsUrl("https://example.com/acs.html")
                        .setAudRestriction("https://example.com/login.html")
                        .setBaseUrl("https://example.com/home.html")))
        )
        registerForCleanup(application)
        application
    }

    @Test
    void testGetDefaultProvisioningConnectionForApplication() {
        Application application = createApp()

        ProvisioningConnection provisioningConnection = client.instantiate(ProvisioningConnection)
            .getDefaultProvisioningConnectionForApplication(application.getId())

        assertThat provisioningConnection, notNullValue()
        assertThat provisioningConnection.getAuthScheme(), is(ProvisioningConnectionAuthScheme.TOKEN)
        assertThat provisioningConnection.getStatus(), is(ProvisioningConnectionStatus.DISABLED)
    }

    @Test
    void testSetDefaultProvisioningConnectionForApplication() {
        Application application = createApp()

        def profile = client.instantiate(ProvisioningConnectionProfile)
            .setAuthScheme(ProvisioningConnectionAuthScheme.TOKEN)
            .setToken("foo")
        def provisioningRequest = client.instantiate(ProvisioningConnectionRequest).setProfile(profile)
        def provisioningConnection = profile.setDefaultProvisioningConnectionForApplication(application.getId(), provisioningRequest)

        assertThat provisioningConnection, notNullValue()
        assertThat provisioningConnection.getAuthScheme(), is(ProvisioningConnectionAuthScheme.TOKEN)
        assertThat provisioningConnection.getStatus(), is(ProvisioningConnectionStatus.DISABLED)
    }
}
