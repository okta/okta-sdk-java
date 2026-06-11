/*
 * Copyright 2024-Present Okta, Inc.
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
package com.okta.sdk.impl.deserializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.okta.sdk.resource.model.IdentityProviderProtocol;
import com.okta.sdk.resource.model.ProtocolEndpointBinding;
import com.okta.sdk.resource.model.ProtocolMtls;
import com.okta.sdk.resource.model.ProtocolOAuth;
import com.okta.sdk.resource.model.ProtocolOidc;
import com.okta.sdk.resource.model.ProtocolSaml;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Regression tests for OKTA-1175444: SAML/OIDC IdP protocol fields silently
 * dropped during deserialization due to missing discriminator on
 * {@code IdentityProvider.protocol}.
 *
 * Each test deserializes a wire-format JSON snapshot (matching what the Okta API
 * actually returns) and asserts that previously-dropped fields are now populated
 * in the correctly-typed subclass.
 */
public class IdentityProviderProtocolDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeMethod
    public void setUp() {
        // Mirror the ObjectMapper configuration used by ApiClient so tests
        // exercise the same deserialization path as production code.
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.registerModule(new JavaTimeModule());
    }

    // -------------------------------------------------------------------------
    // SAML2 protocol
    // -------------------------------------------------------------------------

    @Test
    public void saml2Protocol_deserializesToProtocolSamlSubtype() throws JsonProcessingException {
        String json = saml2ProtocolJson();

        IdentityProviderProtocol protocol = objectMapper.readValue(json, IdentityProviderProtocol.class);

        assertNotNull(protocol);
        assertTrue(protocol instanceof ProtocolSaml,
                "Expected ProtocolSaml but got " + protocol.getClass().getSimpleName());
    }

    @Test
    public void saml2Protocol_ssoEndpointFieldsPopulated() throws JsonProcessingException {
        ProtocolSaml saml = (ProtocolSaml) objectMapper.readValue(saml2ProtocolJson(), IdentityProviderProtocol.class);

        assertNotNull(saml.getEndpoints(), "endpoints must not be null");
        assertNotNull(saml.getEndpoints().getSso(), "endpoints.sso must not be null");
        assertEquals(saml.getEndpoints().getSso().getUrl(), "https://idp.example.com/sso/saml");
        assertEquals(saml.getEndpoints().getSso().getBinding(), ProtocolEndpointBinding.HTTP_REDIRECT);
        assertEquals(saml.getEndpoints().getSso().getDestination(), "https://idp.example.com/sso/saml");
    }

    @Test
    public void saml2Protocol_acsEndpointFieldsPopulated() throws JsonProcessingException {
        ProtocolSaml saml = (ProtocolSaml) objectMapper.readValue(saml2ProtocolJson(), IdentityProviderProtocol.class);

        assertNotNull(saml.getEndpoints().getAcs(), "endpoints.acs must not be null");
        assertEquals(saml.getEndpoints().getAcs().getBinding(), ProtocolEndpointBinding.HTTP_POST);
    }

    @Test
    public void saml2Protocol_trustCredentialsPopulated() throws JsonProcessingException {
        ProtocolSaml saml = (ProtocolSaml) objectMapper.readValue(saml2ProtocolJson(), IdentityProviderProtocol.class);

        assertNotNull(saml.getCredentials(), "credentials must not be null");
        assertNotNull(saml.getCredentials().getTrust(), "credentials.trust must not be null");
        assertEquals(saml.getCredentials().getTrust().getIssuer(), "https://idp.example.com");
        assertEquals(saml.getCredentials().getTrust().getAudience(), "https://www.okta.com/saml2/service-provider/xyz");
        assertEquals(saml.getCredentials().getTrust().getKid(), "your-key-id");
    }

    @Test
    public void saml2Protocol_settingsNameFormatPopulated() throws JsonProcessingException {
        ProtocolSaml saml = (ProtocolSaml) objectMapper.readValue(saml2ProtocolJson(), IdentityProviderProtocol.class);

        assertNotNull(saml.getSettings(), "settings must not be null");
        assertNotNull(saml.getSettings().getNameFormat(), "settings.nameFormat must not be null");
    }

    @Test
    public void saml2Protocol_typeFieldCorrect() throws JsonProcessingException {
        ProtocolSaml saml = (ProtocolSaml) objectMapper.readValue(saml2ProtocolJson(), IdentityProviderProtocol.class);

        assertEquals(saml.getType().getValue(), "SAML2");
    }

    @Test
    public void saml2Protocol_roundTripPreservesKeyFields() throws JsonProcessingException {
        ProtocolSaml original = (ProtocolSaml) objectMapper.readValue(saml2ProtocolJson(), IdentityProviderProtocol.class);
        String serialized = objectMapper.writeValueAsString(original);
        ProtocolSaml roundTripped = (ProtocolSaml) objectMapper.readValue(serialized, IdentityProviderProtocol.class);

        assertEquals(roundTripped.getEndpoints().getSso().getUrl(),
                original.getEndpoints().getSso().getUrl(),
                "sso.url must survive a serialize/deserialize round-trip");
        assertEquals(roundTripped.getCredentials().getTrust().getIssuer(),
                original.getCredentials().getTrust().getIssuer(),
                "credentials.trust.issuer must survive a serialize/deserialize round-trip");
    }

    @Test
    public void saml2Protocol_noOidcEndpointsInvented() throws JsonProcessingException {
        // Regression: the flat IdentityProviderProtocol class used to set OIDC-shaped
        // endpoints (authorization, token, jwks, par) on a SAML2 IdP.  After the fix,
        // the ProtocolSaml class has no such fields; this test confirms they are absent
        // from the serialized output.
        ProtocolSaml saml = (ProtocolSaml) objectMapper.readValue(saml2ProtocolJson(), IdentityProviderProtocol.class);
        String serialized = objectMapper.writeValueAsString(saml);

        assertFalse(serialized.contains("\"authorization\""),
                "SAML2 serialized output must not contain OIDC authorization endpoint");
        assertFalse(serialized.contains("\"token\""),
                "SAML2 serialized output must not contain OIDC token endpoint");
        assertFalse(serialized.contains("\"jwks\""),
                "SAML2 serialized output must not contain OIDC jwks endpoint");
    }

    // -------------------------------------------------------------------------
    // OIDC protocol
    // -------------------------------------------------------------------------

    @Test
    public void oidcProtocol_deserializesToProtocolOidcSubtype() throws JsonProcessingException {
        IdentityProviderProtocol protocol = objectMapper.readValue(oidcProtocolJson(), IdentityProviderProtocol.class);

        assertNotNull(protocol);
        assertTrue(protocol instanceof ProtocolOidc,
                "Expected ProtocolOidc but got " + protocol.getClass().getSimpleName());
    }

    @Test
    public void oidcProtocol_pkceRequiredPreserved() throws JsonProcessingException {
        // Regression: pkce_required=true was silently dropped by the flat class,
        // which would have disabled PKCE server-side on replaceIdentityProvider.
        ProtocolOidc oidc = (ProtocolOidc) objectMapper.readValue(oidcProtocolJson(), IdentityProviderProtocol.class);

        assertNotNull(oidc.getCredentials(), "credentials must not be null");
        assertNotNull(oidc.getCredentials().getClient(), "credentials.client must not be null");
        assertTrue(oidc.getCredentials().getClient().getPkceRequired(),
                "pkce_required=true must be preserved after deserialization");
    }

    @Test
    public void oidcProtocol_userInfoEndpointPopulated() throws JsonProcessingException {
        ProtocolOidc oidc = (ProtocolOidc) objectMapper.readValue(oidcProtocolJson(), IdentityProviderProtocol.class);

        assertNotNull(oidc.getEndpoints(), "endpoints must not be null");
        assertNotNull(oidc.getEndpoints().getUserInfo(), "endpoints.userInfo must not be null");
        assertEquals(oidc.getEndpoints().getUserInfo().getUrl(),
                "https://idp.example.com/oauth2/v1/userinfo");
        assertEquals(oidc.getEndpoints().getUserInfo().getBinding(), ProtocolEndpointBinding.HTTP_REDIRECT);
    }

    @Test
    public void oidcProtocol_roundTripPreservesPkce() throws JsonProcessingException {
        ProtocolOidc original = (ProtocolOidc) objectMapper.readValue(oidcProtocolJson(), IdentityProviderProtocol.class);
        String serialized = objectMapper.writeValueAsString(original);
        ProtocolOidc roundTripped = (ProtocolOidc) objectMapper.readValue(serialized, IdentityProviderProtocol.class);

        assertTrue(roundTripped.getCredentials().getClient().getPkceRequired(),
                "pkce_required must survive a serialize/deserialize round-trip");
    }

    // -------------------------------------------------------------------------
    // OAUTH2 protocol
    // -------------------------------------------------------------------------

    @Test
    public void oauth2Protocol_deserializesToProtocolOAuthSubtype() throws JsonProcessingException {
        String json = "{\"type\":\"OAUTH2\",\"credentials\":{\"client\":{\"client_id\":\"abc\",\"client_secret\":\"secret\"}}}";

        IdentityProviderProtocol protocol = objectMapper.readValue(json, IdentityProviderProtocol.class);

        assertNotNull(protocol);
        assertTrue(protocol instanceof ProtocolOAuth,
                "Expected ProtocolOAuth but got " + protocol.getClass().getSimpleName());
    }

    // -------------------------------------------------------------------------
    // MTLS protocol
    // -------------------------------------------------------------------------

    @Test
    public void mtlsProtocol_deserializesToProtocolMtlsSubtype() throws JsonProcessingException {
        String json = "{\"type\":\"MTLS\"}";

        IdentityProviderProtocol protocol = objectMapper.readValue(json, IdentityProviderProtocol.class);

        assertNotNull(protocol);
        assertTrue(protocol instanceof ProtocolMtls,
                "Expected ProtocolMtls but got " + protocol.getClass().getSimpleName());
    }

    // -------------------------------------------------------------------------
    // JSON fixtures (wire-format snapshots matching real Okta API responses)
    // -------------------------------------------------------------------------

    private static String saml2ProtocolJson() {
        return "{"
                + "\"type\": \"SAML2\","
                + "\"algorithms\": {"
                + "  \"request\": {\"signature\": {\"algorithm\": \"SHA-256\", \"scope\": \"REQUEST\"}},"
                + "  \"response\": {\"signature\": {\"algorithm\": \"SHA-256\", \"scope\": \"ANY\"}}"
                + "},"
                + "\"credentials\": {"
                + "  \"signing\": {\"kid\": \"signing-key-id\"},"
                + "  \"trust\": {"
                + "    \"issuer\": \"https://idp.example.com\","
                + "    \"audience\": \"https://www.okta.com/saml2/service-provider/xyz\","
                + "    \"kid\": \"your-key-id\""
                + "  }"
                + "},"
                + "\"endpoints\": {"
                + "  \"sso\": {"
                + "    \"url\": \"https://idp.example.com/sso/saml\","
                + "    \"binding\": \"HTTP-REDIRECT\","
                + "    \"destination\": \"https://idp.example.com/sso/saml\""
                + "  },"
                + "  \"acs\": {"
                + "    \"binding\": \"HTTP-POST\","
                + "    \"type\": \"INSTANCE\""
                + "  }"
                + "},"
                + "\"settings\": {"
                + "  \"nameFormat\": \"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\","
                + "  \"honorPersistentNameId\": true"
                + "}"
                + "}";
    }

    private static String oidcProtocolJson() {
        return "{"
                + "\"type\": \"OIDC\","
                + "\"credentials\": {"
                + "  \"client\": {"
                + "    \"client_id\": \"my-client-id\","
                + "    \"pkce_required\": true"
                + "  }"
                + "},"
                + "\"endpoints\": {"
                + "  \"userInfo\": {"
                + "    \"url\": \"https://idp.example.com/oauth2/v1/userinfo\","
                + "    \"binding\": \"HTTP-REDIRECT\""
                + "  }"
                + "},"
                + "\"settings\": {"
                + "  \"nameFormat\": \"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\""
                + "}"
                + "}";
    }
}
