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
package com.okta.sdk.impl.oauth2

import com.okta.sdk.impl.Util
import com.okta.sdk.impl.config.ClientConfiguration
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.testng.annotations.Test

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when
import static org.testng.Assert.assertEquals

/**
 *
 * Test for {@link AccessTokenRetrieverServiceImpl} class
 */
class AccessTokenRetrieverServiceImplTest {

    @Test
    void testInstantiationWithNullClientConfig() {
        Util.expect(IllegalArgumentException) {
            new AccessTokenRetrieverServiceImpl(null)
        }
    }

    @Test
    void testGetPrivateKeyFromPem() {

        PrivateKey generatedPrivateKey = generatePrivateKey("RSA", 2048)
        File privateKeyPemFile = writePrivateKeyToPemFile(generatedPrivateKey, "privateKey")

        // Now test the pem -> private key conversion function of getPrivateKeyFromPem method
        Reader pemFileReader = new FileReader(privateKeyPemFile)

        PrivateKey resultPrivateKey = getAccessTokenRetrieverServiceInstance().getPrivateKeyFromPEM(pemFileReader)

        privateKeyPemFile.deleteOnExit()

        assertThat(resultPrivateKey, notNullValue())
        assertThat(resultPrivateKey.getAlgorithm(), is("RSA"))
        assertThat(resultPrivateKey.getFormat(), is("PKCS#8"))
    }

    @Test
    void testParsePrivateKey() {
        PrivateKey generatedPrivateKey = generatePrivateKey("RSA", 2048)
        File privateKeyPemFile = writePrivateKeyToPemFile(generatedPrivateKey, "privateKey")

        PrivateKey parsedPrivateKey = getAccessTokenRetrieverServiceInstance().parsePrivateKey(privateKeyPemFile.path)

        privateKeyPemFile.deleteOnExit()

        assertThat(parsedPrivateKey, notNullValue())
        assertThat(parsedPrivateKey.getAlgorithm(), is("RSA"))
        assertThat(parsedPrivateKey.getFormat(), is("PKCS#8"))
    }

    @Test
    void testCreateSignedJWT() {
        def clientConfig = mock(ClientConfiguration)

        PrivateKey generatedPrivateKey = generatePrivateKey("RSA", 2048)
        File privateKeyPemFile = writePrivateKeyToPemFile(generatedPrivateKey, "privateKey")

        when(clientConfig.getBaseUrl()).thenReturn("https://okta.example.com")
        when(clientConfig.getClientId()).thenReturn("client12345")
        when(clientConfig.getPrivateKey()).thenReturn(privateKeyPemFile.path)

        String signedJwt = getAccessTokenRetrieverServiceInstance(clientConfig).createSignedJWT()

        privateKeyPemFile.deleteOnExit()

        assertThat(signedJwt, notNullValue())

        // decode the signed jwt and verify
        Claims claims = Jwts.parser()
            .setSigningKey(generatedPrivateKey)
            .parseClaimsJws(signedJwt).getBody()

        assertThat(claims, notNullValue())

        assertEquals(claims.get("aud"), clientConfig.getBaseUrl() + "/oauth2/v1/token")
        assertThat(claims.get("iat"), notNullValue())
        assertThat(claims.get("exp"), notNullValue())
        assertEquals(Integer.valueOf(claims.get("exp")) - Integer.valueOf(claims.get("iat")), 3600)
        assertThat(claims.get("iss"), notNullValue())
        assertEquals(claims.get("iss"), clientConfig.getClientId(), "iss must be equal to client id")
        assertThat(claims.get("sub"), notNullValue())
        assertEquals(claims.get("sub"), clientConfig.getClientId(), "sub must be equal to client id")
        assertThat(claims.get("jti"), notNullValue())
    }

    // helper methods

    PrivateKey generatePrivateKey(String algorithm, int keySize) {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm)
        keyPairGenerator.initialize(keySize)
        KeyPair keyPair = keyPairGenerator.generateKeyPair()
        PrivateKey privateKey = keyPair.getPrivate()
        return privateKey
    }

    File writePrivateKeyToPemFile(PrivateKey privateKey, String fileNamePrefix) {
        String encodedString = "-----BEGIN PRIVATE KEY-----\n"
        encodedString = encodedString + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "\n"
        encodedString = encodedString + "-----END PRIVATE KEY-----\n"
        File privateKeyPemFile = File.createTempFile(fileNamePrefix,".pem")
        privateKeyPemFile.write(encodedString)
        return privateKeyPemFile
    }

    AccessTokenRetrieverServiceImpl getAccessTokenRetrieverServiceInstance(ClientConfiguration clientConfiguration) {
        if (clientConfiguration == null) {
            return new AccessTokenRetrieverServiceImpl(new ClientConfiguration())
        }
        return new AccessTokenRetrieverServiceImpl(clientConfiguration)
    }

}