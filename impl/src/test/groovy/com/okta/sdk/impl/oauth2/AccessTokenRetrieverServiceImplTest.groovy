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

import com.nimbusds.jose.jwk.RSAKey
import com.okta.commons.http.config.BaseUrlResolver
import com.okta.sdk.client.AuthorizationMode
import com.okta.sdk.client.Clients

import com.okta.sdk.impl.Util
import com.okta.sdk.impl.api.DefaultClientCredentialsResolver
import com.okta.sdk.impl.client.DefaultClientBuilder
import com.okta.sdk.impl.config.ClientConfiguration
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import org.bouncycastle.openssl.PEMException
import org.hamcrest.MatcherAssert
import org.mockito.ArgumentMatchers
import org.openapitools.client.ApiClient
import org.openapitools.client.ApiException
import org.openapitools.client.Pair
import org.openapitools.client.model.HttpMethod
import org.testng.annotations.Test

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue
import static org.mockito.ArgumentMatchers.*
import static org.mockito.Mockito.*
import static org.testng.Assert.assertEquals

/**
 * Test for {@link AccessTokenRetrieverServiceImpl} class
 *
 * @since 1.6.0
 */
class AccessTokenRetrieverServiceImplTest {

    // dummy private key generated and used for unit test only

    private static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n" +
        "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDH0Y47a8w/Tgiv" +
        "V4mytjBSR/5HIu+P58/v3g6gbYvxC/NWPzPZ3hjTeRskJpB1AfNwm55rAhjSD99d" +
        "4ZiHWMEFjEeSKIaEtMxDPU1pg23R/e+sATVevEfx1G1+IoaSu6SKLnHN7iNtvWlK" +
        "reR5pNUVHKcRotg/auiNUd8P9Wok8FQhFGxbZEdYhjvICLfHLrZKQKOR1AwqPscX" +
        "+FnLF2F+9X0QXRAX3XW/D4S9sfS9JN+J6mhhOQpy78p5VDxGPZim2bk/WNhB3uQ+" +
        "4UE7xdMYIMBPxP3kd2/vhBLG+AhqGvh1XhROvCQ2mtuRq+vTFCfd+tObx1b5a7eU" +
        "UwVWM8AxAgMBAAECggEBAJ1vapU+1ep63TTpz8BS87egqaP6zq2fg6IGX5ffOAdv" +
        "1wX5Pi1GZGEaZlwRVngaVWg/9I1zVYMMpn0dpkPdlhd881chPvuIR/gicL/Voc12" +
        "OkRXn2lJB5ZuPObI5SbvWTDWbyxFmPx55F/GquF9EbZUoP2wRJmS7i+Kdinovvzi" +
        "Rwgevpdo/IA3uosX5NzIazIhCeb4v1v2tpvNH63pfAzKsEHVF0bVQ7yRtrDE5bbp" +
        "NbyNR5em30G2CXqNQIdKMQAL3b4LfCGkXrJABVszjTXQO117PhCnifNmLGzlVhBD" +
        "qC65Luh5GJ21qvj2InWIYdLft0DvzUVH29Bb9uB4H5ECgYEA/+FIE5PDRx+PP9Zq" +
        "kKtXaTtBstZAUbx0vwIs1IruB9Xio/lskZ8woAXDqWYR4VOkqR6JeOPImHFN8keK" +
        "Vvd04J/2nDEHa4SPy23Ww0YcmtvlpRwsWZ1JktO9PFK1YadyUEYWVjWToSGmbZD3" +
        "aPOSKf+uFDuOUClyGUjZiWBMWRMCgYEAx+mLOk76eraiLC3QIJ7g9XjBDBmBBBQA" +
        "3yk7F86zrrRxb/FA7G9zc19GkMjnGT5QYG7Qdw0LRbh4AT3zkMPVbd4Cy3YRhKFi" +
        "XwF5loOv4YHKlB+Ny9yKC7Jz9tzhccOAxjyjwDtY2tw/DQP4xdgtDuMccr7DLdrB" +
        "8mrZNn1vTisCgYAxdd50yk8o5FTQRiX7KOOQl7+vTfLI2eDHOyhnPSOdqB5TC9eM" +
        "nnTLudGEYRJ7t6tQdXKlR4Jy1RP4DRQUk2ioMsN8lY2Vnt4cuHKW9Gp7FJ5jN/rq" +
        "p5idJQijLGmbIr7Z/XI738dVkieVbjwksVBDhgSkLI7pt9kyQf6qq06WuQKBgQC6" +
        "E2b1ghfhauduacIk6t2HfrtpkL+m1RuunEkVst9KyUghIxUEPgTfKZqcH3QD6h2U" +
        "dPDzLyAD6F1DArAYWj/pwNEnIqHRqwnOVqge8joek9nEn84zJ/cSRitsZ1IsuwW8" +
        "/yqIPnVJWeISMlU3iiz+g2SyZV906f7Grq+56W1V+wKBgQDrgV2VHJyIHuHS6t+A" +
        "BV89ditsFt4n2h2SPzX9xI5uwUclwPy01bCaMccKvzPwhmJWMRDzIeKgLy3aJWkl" +
        "8zZeebOsKqNB3Nlm8wNrYbJJvpTEyt6kpZi+YJ/S4mQ/0CzIpmq6014aKm16NJxv" +
        "AHYKHuDUvhiDFwadf8Q7kSK5KA==\n" +
        "-----END PRIVATE KEY-----"

    // dummy private key generated and used for unit test only

    private static final String RSA_PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\n" +
        "MIIEpQIBAAKCAQEAx9GOO2vMP04Ir1eJsrYwUkf+RyLvj+fP794OoG2L8QvzVj8z" +
        "2d4Y03kbJCaQdQHzcJueawIY0g/fXeGYh1jBBYxHkiiGhLTMQz1NaYNt0f3vrAE1" +
        "XrxH8dRtfiKGkrukii5xze4jbb1pSq3keaTVFRynEaLYP2rojVHfD/VqJPBUIRRs" +
        "W2RHWIY7yAi3xy62SkCjkdQMKj7HF/hZyxdhfvV9EF0QF911vw+EvbH0vSTfiepo" +
        "YTkKcu/KeVQ8Rj2Yptm5P1jYQd7kPuFBO8XTGCDAT8T95Hdv74QSxvgIahr4dV4U" +
        "TrwkNprbkavr0xQn3frTm8dW+Wu3lFMFVjPAMQIDAQABAoIBAQCdb2qVPtXqet00" +
        "6c/AUvO3oKmj+s6tn4OiBl+X3zgHb9cF+T4tRmRhGmZcEVZ4GlVoP/SNc1WDDKZ9" +
        "HaZD3ZYXfPNXIT77iEf4InC/1aHNdjpEV59pSQeWbjzmyOUm71kw1m8sRZj8eeRf" +
        "xqrhfRG2VKD9sESZku4vinYp6L784kcIHr6XaPyAN7qLF+TcyGsyIQnm+L9b9rab" +
        "zR+t6XwMyrBB1RdG1UO8kbawxOW26TW8jUeXpt9Btgl6jUCHSjEAC92+C3whpF6y" +
        "QAVbM4010Dtdez4Qp4nzZixs5VYQQ6guuS7oeRidtar49iJ1iGHS37dA781FR9vQ" +
        "W/bgeB+RAoGBAP/hSBOTw0cfjz/WapCrV2k7QbLWQFG8dL8CLNSK7gfV4qP5bJGf" +
        "MKAFw6lmEeFTpKkeiXjjyJhxTfJHilb3dOCf9pwxB2uEj8tt1sNGHJrb5aUcLFmd" +
        "SZLTvTxStWGnclBGFlY1k6Ehpm2Q92jzkin/rhQ7jlApchlI2YlgTFkTAoGBAMfp" +
        "izpO+nq2oiwt0CCe4PV4wQwZgQQUAN8pOxfOs660cW/xQOxvc3NfRpDI5xk+UGBu" +
        "0HcNC0W4eAE985DD1W3eAst2EYShYl8BeZaDr+GBypQfjcvciguyc/bc4XHDgMY8" +
        "o8A7WNrcPw0D+MXYLQ7jHHK+wy3awfJq2TZ9b04rAoGAMXXedMpPKORU0EYl+yjj" +
        "kJe/r03yyNngxzsoZz0jnageUwvXjJ50y7nRhGESe7erUHVypUeCctUT+A0UFJNo" +
        "qDLDfJWNlZ7eHLhylvRqexSeYzf66qeYnSUIoyxpmyK+2f1yO9/HVZInlW48JLFQ" +
        "Q4YEpCyO6bfZMkH+qqtOlrkCgYEAuhNm9YIX4WrnbmnCJOrdh367aZC/ptUbrpxJ" +
        "FbLfSslIISMVBD4E3ymanB90A+odlHTw8y8gA+hdQwKwGFo/6cDRJyKh0asJzlao" +
        "HvI6HpPZxJ/OMyf3EkYrbGdSLLsFvP8qiD51SVniEjJVN4os/oNksmVfdOn+xq6v" +
        "ueltVfsCgYEA64FdlRyciB7h0urfgAVfPXYrbBbeJ9odkj81/cSObsFHJcD8tNWw" +
        "mjHHCr8z8IZiVjEQ8yHioC8t2iVpJfM2XnmzrCqjQdzZZvMDa2GySb6UxMrepKWY" +
        "vmCf0uJkP9AsyKZqutNeGiptejScbwB2Ch7g1L4YgxcGnX/EO5EiuSg=\n" +
        "-----END RSA PRIVATE KEY-----"

    @Test
    void testInstantiationWithNullClientConfig() {
        Util.expect(IllegalArgumentException) {
            new AccessTokenRetrieverServiceImpl(null, null)
        }
    }

    @Test
    void testGetPrivateKeyFromPem() {

        def clientConfiguration = mock(ClientConfiguration)
        def apiClient = mock(ApiClient)

        // dummy private key generated and used for unit test only

        PrivateKey generatedPrivateKey = generatePrivateKey("RSA", 2048)
        File privateKeyPemFile = writePrivateKeyToPemFile(generatedPrivateKey, "privateKey")

        // Now test the pem -> private key conversion function of getPrivateKeyFromPem method
        Reader pemFileReader = new FileReader(privateKeyPemFile)

        PrivateKey resultPrivateKey = getAccessTokenRetrieverServiceInstance(clientConfiguration, apiClient).getPrivateKeyFromPEM(pemFileReader)

        privateKeyPemFile.deleteOnExit()

        assertThat(resultPrivateKey, notNullValue())
        MatcherAssert.assertThat(resultPrivateKey.getAlgorithm(), is("RSA"))
        MatcherAssert.assertThat(resultPrivateKey.getFormat(), is("PKCS#8"))
    }

    @Test
    void testParsePrivateKey() {

        def apiClient = mock(ApiClient)
        def clientConfiguration = mock(ClientConfiguration)

        // dummy private key generated and used for unit test only

        PrivateKey generatedPrivateKey = generatePrivateKey("RSA", 2048)
        File privateKeyPemFile = writePrivateKeyToPemFile(generatedPrivateKey, "privateKey")
        Reader reader = new BufferedReader(new FileReader(privateKeyPemFile))

        PrivateKey parsedPrivateKey = getAccessTokenRetrieverServiceInstance(clientConfiguration, apiClient).parsePrivateKey(reader)

        privateKeyPemFile.deleteOnExit()

        assertThat(parsedPrivateKey, notNullValue())
        MatcherAssert.assertThat(parsedPrivateKey.getAlgorithm(), is("RSA"))
        MatcherAssert.assertThat(parsedPrivateKey.getFormat(), is("PKCS#8"))
    }

    @Test
    void testAccessTokenRetrieval() {

        def apiClient = mock(ApiClient)
        def clientConfiguration = mock(ClientConfiguration)

        when(clientConfiguration.getPrivateKey()).thenReturn(PRIVATE_KEY)

        def accessTokenRetrievalService = new AccessTokenRetrieverServiceImpl(clientConfiguration, apiClient)

        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken()
        oAuth2AccessToken.setAccessToken("accessToken")
        oAuth2AccessToken.setIdToken("idToken")
        oAuth2AccessToken.setTokenType("Bearer")
        oAuth2AccessToken.setExpiresIn(3600)
        oAuth2AccessToken.setScope("openid")

        when(apiClient.invokeAPI(
            anyString(),
            eq(HttpMethod.POST.name()) as String,
            anyList() as List<Pair>,
            anyList() as List<Pair>,
            isNull() as String,
            isNull() as String,
            ArgumentMatchers.any() as Map<String, String>,
            ArgumentMatchers.any() as Map<String, String>,
            ArgumentMatchers.any() as Map<String, Object>,
            anyString(),
            anyString(),
            ArgumentMatchers.any() as String[],
            any())).thenReturn(oAuth2AccessToken)

        OAuth2AccessToken mockResponseAccessToken = accessTokenRetrievalService.getOAuth2AccessToken()
        assertEquals(mockResponseAccessToken.getAccessToken(), "accessToken")
        assertEquals(mockResponseAccessToken.getIdToken(), "idToken")
        assertEquals(mockResponseAccessToken.getTokenType(), "Bearer")
        assertEquals(mockResponseAccessToken.getExpiresIn(), 3600)
        assertEquals(mockResponseAccessToken.getScope(), "openid")
    }

    @Test
    void testCreateSignedJWT() {

        def clientConfig = mock(ClientConfiguration)
        def apiClient = mock(ApiClient)

        // dummy private key generated and used for unit test only

        PrivateKey generatedPrivateKey = generatePrivateKey("RSA", 2048)
        File privateKeyPemFile = writePrivateKeyToPemFile(generatedPrivateKey, "privateKey")

        String baseUrl = "https://sample.okta.com"
        BaseUrlResolver baseUrlResolver = new BaseUrlResolver() {
            @Override
            String getBaseUrl() {
                return baseUrl
            }
        }

        when(clientConfig.getBaseUrl()).thenReturn(baseUrl)
        when(clientConfig.getClientId()).thenReturn("client12345")
        when(clientConfig.getPrivateKey()).thenReturn(privateKeyPemFile.path)
        when(clientConfig.getKid()).thenReturn("kid-value")
        when(clientConfig.getBaseUrlResolver()).thenReturn(baseUrlResolver)
        when(clientConfig.getClientCredentialsResolver()).thenReturn(
            new DefaultClientCredentialsResolver({ -> Optional.empty() }))

        String signedJwt = getAccessTokenRetrieverServiceInstance(clientConfig, apiClient).createSignedJWT()

        privateKeyPemFile.deleteOnExit()

        assertThat(signedJwt, notNullValue())

        // decode the signed jwt and verify
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(generatedPrivateKey)
            .build()
            .parseClaimsJws(signedJwt).getBody()

        assertThat(claims, notNullValue())

        assertEquals(claims.get("aud"), clientConfig.getBaseUrl() + "/oauth2/v1/token")
        assertThat(claims.get("iat"), notNullValue())
        assertThat(claims.get("exp"), notNullValue())
        assertEquals(Integer.valueOf(claims.get("exp") as String) - Integer.valueOf(claims.get("iat") as String), 3000,
            "token expiry time is not 50 minutes")
        assertThat(claims.get("iss"), notNullValue())
        assertEquals(claims.get("iss"), clientConfig.getClientId(), "iss must be equal to client id")
        assertThat(claims.get("sub"), notNullValue())
        assertEquals(claims.get("sub"), clientConfig.getClientId(), "sub must be equal to client id")
        assertThat(claims.get("jti"), notNullValue())

        Header header = Jwts.parser()
            .setSigningKey(generatedPrivateKey)
            .parseClaimsJws(signedJwt)
            .getHeader()

        assertThat(header.get("kid"), notNullValue())
        assertThat(header.get("kid"), is("kid-value"))
    }

    @Test
    void testCreateSignedJWTUsingPrivateKeyFromString() {

        def apiClient = mock(ApiClient)
        def clientConfig = mock(ClientConfiguration)

        // dummy private key generated and used for unit test only

        PrivateKey generatedPrivateKey = generatePrivateKey("RSA", 2048)

        String baseUrl = "https://sample.okta.com"
        BaseUrlResolver baseUrlResolver = new BaseUrlResolver() {
            @Override
            String getBaseUrl() {
                return baseUrl
            }
        }

        when(clientConfig.getBaseUrl()).thenReturn(baseUrl)
        when(clientConfig.getClientId()).thenReturn("client12345")
        when(clientConfig.getPrivateKey()).thenReturn(createPemFileContent(generatedPrivateKey))
        when(clientConfig.getBaseUrlResolver()).thenReturn(baseUrlResolver)
        when(clientConfig.getClientCredentialsResolver()).thenReturn(
            new DefaultClientCredentialsResolver({ -> Optional.empty() }))

        String signedJwt = getAccessTokenRetrieverServiceInstance(clientConfig, apiClient).createSignedJWT()

        assertThat(signedJwt, notNullValue())
    }

    @Test(expectedExceptions = OAuth2TokenRetrieverException.class)
    void testGetOAuth2TokenRetrieverRuntimeException() {

        def tokenClient = mock(ApiClient)
        def clientConfig = mock(ClientConfiguration)

        // dummy private key generated and used for unit test only

        PrivateKey generatedPrivateKey = generatePrivateKey("RSA", 2048)
        File privateKeyPemFile = writePrivateKeyToPemFile(generatedPrivateKey, "privateKey")

        String baseUrl = "https://sample.okta.com"
        BaseUrlResolver baseUrlResolver = new BaseUrlResolver() {
            @Override
            String getBaseUrl() {
                return baseUrl
            }
        }

        when(clientConfig.getBaseUrl()).thenReturn(baseUrl)
        when(clientConfig.getClientId()).thenReturn("client12345")
        when(clientConfig.getPrivateKey()).thenReturn(privateKeyPemFile.path)
        when(clientConfig.getBaseUrlResolver()).thenReturn(baseUrlResolver)
        when(clientConfig.getClientCredentialsResolver()).thenReturn(
            new DefaultClientCredentialsResolver({ -> Optional.empty() }))

        def accessTokenRetrieverService = new AccessTokenRetrieverServiceImpl(clientConfig, tokenClient)
        accessTokenRetrieverService.getOAuth2AccessToken()

        verify(tokenClient, times(1))
    }

    @Test(expectedExceptions = OAuth2HttpException.class)
    void testGetOAuth2ResourceException() {

        def apiClient = mock(ApiClient)
        def clientConfig = mock(ClientConfiguration)

        // dummy private key generated and used for unit test only

        PrivateKey generatedPrivateKey = generatePrivateKey("RSA", 2048)
        File privateKeyPemFile = writePrivateKeyToPemFile(generatedPrivateKey, "privateKey")

        String baseUrl = "https://sample.okta.com"
        BaseUrlResolver baseUrlResolver = new BaseUrlResolver() {
            @Override
            String getBaseUrl() {
                return baseUrl
            }
        }

        when(clientConfig.getBaseUrl()).thenReturn(baseUrl)
        when(clientConfig.getClientId()).thenReturn("client12345")
        when(clientConfig.getPrivateKey()).thenReturn(privateKeyPemFile.path)
        when(clientConfig.getBaseUrlResolver()).thenReturn(baseUrlResolver)
        when(clientConfig.getClientCredentialsResolver()).thenReturn(
            new DefaultClientCredentialsResolver({ -> Optional.empty() }))

        ApiException apiException = new ApiException(401, "Unauthorized", null, "{\n" +
            "    \"error\" : \"invalid_client\",\n" +
            "    \"error_description\" : \"No client credentials found.\"\n" +
            "}")

        when(apiClient.invokeAPI(
            anyString(),
            eq(HttpMethod.POST.name()),
            anyList() as List<Pair>,
            anyList() as List<Pair>,
            isNull() as String,
            isNull() as String,
            ArgumentMatchers.any() as Map<String, String>,
            ArgumentMatchers.any() as Map<String, String>,
            ArgumentMatchers.any() as Map<String, Object>,
            anyString(),
            anyString(),
            ArgumentMatchers.any() as String[],
            any())).thenThrow(apiException)

        def accessTokenRetrieverService = new AccessTokenRetrieverServiceImpl(clientConfig, apiClient)
        accessTokenRetrieverService.getOAuth2AccessToken()

        verify(apiClient, times(1)).invokeAPI()
    }

    // helper methods

    // dummy private key generated and used for unit test only

    static PrivateKey generatePrivateKey(String algorithm, int keySize) {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm)
        keyPairGenerator.initialize(keySize)
        KeyPair keyPair = keyPairGenerator.generateKeyPair()
        PrivateKey privateKey = keyPair.getPrivate()
        return privateKey
    }

    static String createPemFileContent(PrivateKey privateKey) {
        String encodedString = "-----BEGIN PRIVATE KEY-----\n"
        encodedString = encodedString + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "\n"
        encodedString = encodedString + "-----END PRIVATE KEY-----\n"
        return encodedString

    }

    static File writePrivateKeyToPemFile(PrivateKey privateKey, String fileNamePrefix) {
        File privateKeyPemFile = File.createTempFile(fileNamePrefix,".pem")
        privateKeyPemFile.write(createPemFileContent(privateKey))
        return privateKeyPemFile
    }

    static AccessTokenRetrieverServiceImpl getAccessTokenRetrieverServiceInstance(ClientConfiguration clientConfiguration, ApiClient apiClient) {
        if (clientConfiguration == null) {
            ClientConfiguration cc = new ClientConfiguration()
            cc.setBaseUrlResolver(new BaseUrlResolver() {
                @Override
                String getBaseUrl() {
                    return "https://sample.okta.com"
                }
            })
            cc.setClientCredentialsResolver(new DefaultClientCredentialsResolver({ -> Optional.empty() }))
            return new AccessTokenRetrieverServiceImpl(cc, apiClient)
        }

        return new AccessTokenRetrieverServiceImpl(clientConfiguration, apiClient)
    }

    @Test
    void testConvertPemKeyToRsaPrivateKey() {
        ApiClient apiClient = mock(ApiClient)

        DefaultClientBuilder oktaClient = (DefaultClientBuilder) Clients.builder()
            .setOrgUrl("https://sample.okta.com")
            .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
            .setPrivateKey(RSAKey.parseFromPEMEncodedObjects(PRIVATE_KEY).toRSAKey().toPrivateKey())

        ClientConfiguration clientConfiguration = oktaClient.getClientConfiguration()

        assertEquals(clientConfiguration.getPrivateKey(), RSA_PRIVATE_KEY)

        String signedJwt = getAccessTokenRetrieverServiceInstance(clientConfiguration, apiClient).createSignedJWT()
        assertThat(signedJwt, notNullValue())
    }

    @Test
    void testParseRsaPrivateKey() {
        ApiClient apiClient = mock(ApiClient)

        DefaultClientBuilder oktaClient = (DefaultClientBuilder) Clients.builder()
            .setOrgUrl("https://sample.okta.com")
            .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
            .setPrivateKey(RSAKey.parseFromPEMEncodedObjects(RSA_PRIVATE_KEY).toRSAKey().toPrivateKey())

        String signedJwt = getAccessTokenRetrieverServiceInstance(oktaClient.getClientConfiguration(), apiClient).createSignedJWT()
        assertThat(signedJwt, notNullValue())
    }

    @Test(expectedExceptions = PEMException.class)
    void testParsePemKeyAsRsaPrivateKey() {
        ClientConfiguration clientConfigMock = mock(ClientConfiguration)
        ApiClient apiClient = mock(ApiClient)

        BaseUrlResolver baseUrlResolver = { -> "https://sample.okta.com" }
        when(clientConfigMock.getBaseUrlResolver()).thenReturn(baseUrlResolver)
        when(clientConfigMock.getPrivateKey()).thenReturn(PRIVATE_KEY.replaceAll(" PRIVATE ", " RSA PRIVATE "))

        String signedJwt = getAccessTokenRetrieverServiceInstance(clientConfigMock, apiClient).createSignedJWT()
        assertThat(signedJwt, notNullValue())
    }
}