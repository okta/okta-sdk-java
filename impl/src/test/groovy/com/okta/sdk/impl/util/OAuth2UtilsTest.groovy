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
package com.okta.sdk.impl.util

import com.okta.sdk.impl.config.ClientConfiguration
import org.testng.annotations.Test

import java.nio.file.Paths

import static org.testng.Assert.assertEquals

import io.jsonwebtoken.SignatureAlgorithm;

import java.security.NoSuchAlgorithmException

import static org.testng.Assert.assertNotNull

class OAuth2UtilsTest {

    @Test
    void testGetValidSigningAlgorithms() {
        assertEquals SignatureAlgorithm.RS256, OAuth2Utils.getSignatureAlgorithm("RS256")
        assertEquals SignatureAlgorithm.RS384, OAuth2Utils.getSignatureAlgorithm("RS384")
        assertEquals SignatureAlgorithm.RS512, OAuth2Utils.getSignatureAlgorithm("RS512")

        assertEquals SignatureAlgorithm.ES256, OAuth2Utils.getSignatureAlgorithm("ES256")
        assertEquals SignatureAlgorithm.ES384, OAuth2Utils.getSignatureAlgorithm("ES384")
        assertEquals SignatureAlgorithm.ES512, OAuth2Utils.getSignatureAlgorithm("ES512")
    }

    @Test(expectedExceptions = NoSuchAlgorithmException.class, expectedExceptionsMessageRegExp = "Unsupported algorithm .*")
    void testGetInvalidSigningAlgorithm() {
        OAuth2Utils.getSignatureAlgorithm("spooky")
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Unsupported algorithm .*")
    void testValidateOAuth2ClientConfig_UnsupportedAlgorithm() {
        ClientConfiguration clientConfiguration = new ClientConfiguration()
        List<String> scopes = Arrays.asList({"scope1, scope2"})
        clientConfiguration.setScopes(scopes)
        clientConfiguration.setKeyFilePath "~/.okta/okta.yaml"
        clientConfiguration.setAlgorithm("somealgo")
        OAuth2Utils.validateOAuth2ClientConfig(clientConfiguration)
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Missing scopes")
    void testValidateOAuth2ClientConfig_EmptyScopes() {
        ClientConfiguration clientConfiguration = new ClientConfiguration()
        clientConfiguration.setScopes(null)
        OAuth2Utils.validateOAuth2ClientConfig(clientConfiguration)
    }

    @Test
    void testCreateSignedJWT() {
        ClientConfiguration clientConfiguration = new ClientConfiguration()
        clientConfiguration.setClientId("client12345")
        clientConfiguration.setKeyFilePath(Paths.get("src", "test", "resources", "privateKey.pem").toString())
        clientConfiguration.setAlgorithm("RS256")
        clientConfiguration.setBaseUrl("https://oktapreview-123.okta.com")
        String signedJwt = OAuth2Utils.createSignedJWT(clientConfiguration)
        assertNotNull(signedJwt)
    }

}
