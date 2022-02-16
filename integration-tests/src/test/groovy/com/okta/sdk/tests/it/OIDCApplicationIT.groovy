/*
 * Copyright 2020-Present Okta, Inc.
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

import com.okta.sdk.client.Client
import com.okta.sdk.resource.Application
import com.okta.sdk.resource.ApplicationSignOnMode
import com.okta.sdk.resource.JsonWebKey
import com.okta.sdk.resource.OAuthEndpointAuthenticationMethod
import com.okta.sdk.resource.OAuthGrantType
import com.okta.sdk.resource.OAuthResponseType
import com.okta.sdk.resource.OpenIdConnectApplication
import com.okta.sdk.resource.OpenIdConnectApplicationSettings
import com.okta.sdk.resource.OpenIdConnectApplicationSettingsClient
import com.okta.sdk.resource.OpenIdConnectApplicationSettingsClientKeys
import com.okta.sdk.resource.OpenIdConnectApplicationType
import com.okta.sdk.resource.builder.OIDCApplicationBuilder
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.instanceOf
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.matchesPattern

class OIDCApplicationIT extends ITSupport implements CrudTestSupport {

    @Override
    def create(Client client) {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        Application app = OIDCApplicationBuilder.instance()
            .setName(name)
            .setLabel(name)
            .addRedirectUris("https://www.example.com")
            .setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
            .setGrantTypes(Arrays.asList(OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE))
            .setApplicationType(OpenIdConnectApplicationType.NATIVE)
            .setClientId(UUID.randomUUID().toString())
            .setClientSecret(UUID.randomUUID().toString())
            .setSignOnMode(ApplicationSignOnMode.OPENID_CONNECT)
            .setAutoKeyRotation(true)
            .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.NONE)
            .setIOS(false)
            .setWeb(true)
            .setLoginRedirectUrl("https://www.myapp.com")
            .setErrorRedirectUrl("https://www.myapp.com/error")
            .buildAndCreate(client)
        registerForCleanup(app)

        return (OpenIdConnectApplication) app
    }

    @Test (groups = "group2")
    void createOIDCApplicationWithPrivateKeyJwtTest() {

        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        def createdKey = client.instantiate(JsonWebKey)
            .setKty("RSA")
            .setKid("SIGNING_KEY")
            .setE("AQAB")
            .setN("MIIBIzANBgkqhkiG9w0BAQEFAAOCARAAMIIBCwKCAQIAnFo/4e91na8x/BsPkNS5QkwankewxJ1uZU6p827W/gkRcNHtNi/cE644W5OVdB4UaXV6koT+TsC1prhUEhRR3g5ggE0B/lwYqBaLq/Ejy19Crc4XYU3Aah67Y6HiHWcHGZ+BbpebtTixJv/UYW/Gw+k8M+zj4O001mOeBPpwlEiZZLIo33m/Xkfn28jaCFqTQBJHr67IQh4zEUFs4e5D5D6UE8ee93yeSUJyhbifeIgYh3tS/+ZW4Uo1KLIc0rcLRrnEMsS3aOQbrv/SEKij+Syx4KXI0Gi2xMdXctnFOVT6NM6/EkLxFp2POEdv9SNBtTvXcxIGRwK51W4Jdgh/xZcCAwEAAQ==")

        Application app = OIDCApplicationBuilder.instance()
            .setName(name)
            .setLabel(name)
            .setSignOnMode(ApplicationSignOnMode.OPENID_CONNECT)
            .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.PRIVATE_KEY_JWT)
            .addRedirectUris("https://www.example.com")
            .setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
            .setGrantTypes(Arrays.asList(OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE))
            .setApplicationType(OpenIdConnectApplicationType.NATIVE)
            .setJwks(Arrays.asList(createdKey))
            .buildAndCreate(client)
        registerForCleanup(app)

        assertThat(app, instanceOf(OpenIdConnectApplication))

        OpenIdConnectApplicationSettings openIdConnectApplicationSettings = app.getSettings() as OpenIdConnectApplicationSettings
        OpenIdConnectApplicationSettingsClient openIdConnectApplicationSettingsClient = openIdConnectApplicationSettings.getOAuthClient()

        assertThat(openIdConnectApplicationSettingsClient.getJwks() , instanceOf(OpenIdConnectApplicationSettingsClientKeys))

        OpenIdConnectApplicationSettingsClientKeys keys = openIdConnectApplicationSettingsClient.getJwks()
        assertThat(keys.getKeys(), hasSize(1))
        assertThat(keys.getKeys().get(0), instanceOf(JsonWebKey))

        JsonWebKey receivedKey = keys.getKeys().get(0)
        assertThat(receivedKey.getKty(), equalTo(createdKey.getKty()))
        assertThat(receivedKey.getKid(), equalTo(createdKey.getKid()))
        assertThat(receivedKey.getE(), equalTo(createdKey.getE()))
        assertThat(receivedKey.getN(), equalTo(createdKey.getN()))
    }

    @Override
    def read(Client client, String id) {
        return client.getApplication(id)
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listApplications().iterator()
    }

    @Override
    void update(Client client, def application) {
        application.setLabel(application.label +"-2")
        application.visibility.hide.iOS = true
        client.updateApplication(application, application.id)
    }

    @Override
    void assertUpdate(Client client, def app) {
        assertThat app.label, matchesPattern('^java-sdk-it-.*-2$')
        assertThat app.visibility.hide.iOS, is(true)
    }
}
