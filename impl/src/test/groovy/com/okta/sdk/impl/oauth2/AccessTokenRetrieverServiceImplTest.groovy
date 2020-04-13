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

import com.okta.sdk.client.Client
import com.okta.sdk.impl.Util
import com.okta.sdk.impl.config.ClientConfiguration
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.testng.annotations.Test

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull

import static org.mockito.Mockito.*

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

        assertNotNull(resultPrivateKey, "privateKey cannot be null")
        assertEquals(resultPrivateKey.getAlgorithm(), "RSA", "privateKey algorithm is incorrect")
        assertEquals(resultPrivateKey.getFormat(), "PKCS#8", "privateKey format is incorrect")
    }

    @Test
    void testParsePrivateKey() {
        PrivateKey generatedPrivateKey = generatePrivateKey("RSA", 2048)
        File privateKeyPemFile = writePrivateKeyToPemFile(generatedPrivateKey, "privateKey")

        PrivateKey parsedPrivateKey = getAccessTokenRetrieverServiceInstance().parsePrivateKey(privateKeyPemFile.path)

        privateKeyPemFile.deleteOnExit()

        assertNotNull(parsedPrivateKey, "privateKey cannot be null")
        assertEquals(parsedPrivateKey.getAlgorithm(), "RSA", "privateKey algorithm is incorrect")
        assertEquals(parsedPrivateKey.getFormat(), "PKCS#8", "privateKey format is incorrect")
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

        assertNotNull(signedJwt, "signedJwt cannot be null")

        // decode the signed jwt and verify
        Claims claims = Jwts.parser()
            .setSigningKey(generatedPrivateKey)
            .parseClaimsJws(signedJwt).getBody()

        assertNotNull(claims, "claims cannot be null")
        assertEquals(claims.get("aud"), clientConfig.getBaseUrl() + "/oauth2/v1/token")
        assertNotNull(claims.get("iat"), "iat cannot be null")
        assertNotNull(claims.get("exp"), "exp cannot be null")
        assertNotNull(claims.get("iss"), "iss cannot be null")
        assertEquals(claims.get("iss"), clientConfig.getClientId(), "iss must be equal to client id")
        assertNotNull(claims.get("sub"), "sub cannot be null")
        assertEquals(claims.get("sub"), clientConfig.getClientId(), "sub must be equal to client id")
        assertNotNull(claims.get("jti"), "jti cannot be null")

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