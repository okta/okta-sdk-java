/*
 * Copyright 2026-Present Okta, Inc.
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
package com.okta.sdk.resource.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.cache.Cache;
import com.okta.sdk.cache.CacheManager;
import com.okta.sdk.resource.model.Application;
import com.okta.sdk.resource.model.ApplicationVisibility;
import com.okta.sdk.resource.model.ApplicationVisibilityHide;
import com.okta.sdk.resource.model.ListJwk200ResponseInner;
import com.okta.sdk.resource.model.OpenIdConnectApplication;
import com.okta.sdk.resource.model.OrgContactType;
import com.okta.sdk.resource.model.OrgContactTypeObj;
import com.okta.sdk.resource.model.SamlApplication;
import com.okta.sdk.resource.model.SamlAttributeStatement;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for the Jackson mixins registered in {@link ApiClient}'s default {@code ObjectMapper}.
 */
public class ApiClientJacksonMixinTest {

    // A minimal no-op CacheManager, so this test doesn't need the okta-sdk-impl module (which would
    // introduce a circular dependency back onto this api module) just to construct an ApiClient.
    private static final CacheManager NOOP_CACHE_MANAGER = new CacheManager() {
        @Override
        public <K, V> Cache<K, V> getCache(String name) {
            return new Cache<K, V>() {
                @Override
                public V get(K key) {
                    return null;
                }

                @Override
                public V put(K key, V value) {
                    return null;
                }

                @Override
                public V remove(K key) {
                    return null;
                }
            };
        }
    };

    private final ObjectMapper objectMapper =
        new ApiClient(HttpClients.createDefault(), NOOP_CACHE_MANAGER).getObjectMapper();

    /**
     * OKTA-1227472: ListJwk200ResponseInner declares @JsonSubTypes entries for classes that don't actually
     * extend it, which used to throw InvalidTypeIdException. The mixin disables polymorphic resolution so
     * the flat class (which already has every branch's properties) is used directly.
     */
    @Test
    public void deserializeListJwkResponse_withMixedSigAndEncEntries_doesNotThrow() throws Exception {
        String json = "["
            + "{\"kid\":\"kid1\",\"status\":\"ACTIVE\",\"kty\":\"RSA\",\"use\":\"sig\",\"id\":\"pks1\"},"
            + "{\"e\":\"AQAB\",\"kty\":\"RSA\",\"n\":\"mkC6\",\"use\":\"enc\"}"
            + "]";

        List<ListJwk200ResponseInner> keys = objectMapper.readValue(json,
            new TypeReference<List<ListJwk200ResponseInner>>() { });

        assertEquals(keys.size(), 2);
        assertEquals(keys.get(0).getKid(), "kid1");
        assertEquals(keys.get(1).getE(), "AQAB");
    }

    /**
     * OKTA-1227472: same defect on SamlAttributeStatement (EXPRESSION/GROUP anyOf without allOf inheritance).
     */
    @Test
    public void deserializeSamlAttributeStatement_withExpressionAndGroupEntries_doesNotThrow() throws Exception {
        String json = "["
            + "{\"type\":\"EXPRESSION\",\"name\":\"email\",\"values\":[\"user.email\"]},"
            + "{\"type\":\"GROUP\",\"filterType\":\"STARTS_WITH\",\"filterValue\":\"Team\"}"
            + "]";

        List<SamlAttributeStatement> statements = objectMapper.readValue(json,
            new TypeReference<List<SamlAttributeStatement>>() { });

        assertEquals(statements.size(), 2);
        assertEquals(statements.get(0).getType(), SamlAttributeStatement.TypeEnum.EXPRESSION);
        assertEquals(statements.get(0).getName(), "email");
        assertEquals(statements.get(1).getType(), SamlAttributeStatement.TypeEnum.GROUP);
        assertEquals(statements.get(1).getFilterValue(), "Team");
    }

    /**
     * OKTA-1218351: OpenIdConnectApplication marks credentials/name/settings as required, which generates
     * @JsonInclude(ALWAYS) on those properties. A partial update (only visibility set) must not serialize
     * them as literal null, or the API overwrites/rejects the update.
     */
    @Test
    public void serializePartialOpenIdConnectApplication_omitsNullRequiredFields() throws Exception {
        OpenIdConnectApplication app = new OpenIdConnectApplication();
        ApplicationVisibilityHide hide = new ApplicationVisibilityHide().web(true).iOS(true);
        app.setVisibility(new ApplicationVisibility().hide(hide).autoSubmitToolbar(true));

        String json = objectMapper.writeValueAsString(app);

        assertFalse(json.contains("\"credentials\""), "credentials should be omitted, got: " + json);
        assertFalse(json.contains("\"name\""), "name should be omitted, got: " + json);
        assertFalse(json.contains("\"settings\""), "settings should be omitted, got: " + json);
        assertTrue(json.contains("\"visibility\""), "visibility should be present, got: " + json);
    }

    @Test
    public void serializeFullOpenIdConnectApplication_stillIncludesRequiredFields() throws Exception {
        OpenIdConnectApplication app = new OpenIdConnectApplication();
        app.setName(OpenIdConnectApplication.NameEnum.OIDC_CLIENT);

        String json = objectMapper.writeValueAsString(app);

        assertNotNull(json);
        assertTrue(json.contains("\"name\""), "name should be present when set, got: " + json);
    }

    /**
     * GH-1654: reported that listApplications only returned OIDC apps, no SAML apps. A SAML app whose
     * settings.signOn.attributeStatements uses the (now-fixed) broken SamlAttributeStatement type would
     * throw InvalidTypeIdException while parsing the response array - depending on how a caller's
     * pagination/error handling reacted to that, it could plausibly look like SAML apps were being
     * silently dropped. Confirms a mixed OIDC/SAML list - with attribute statements populated - now
     * deserializes cleanly end-to-end via Application's own (structurally correct) polymorphism.
     */
    @Test
    public void deserializeApplicationList_withMixedOidcAndSamlAttributeStatements_doesNotThrow() throws Exception {
        String json = "["
            + "{\"signOnMode\":\"OPENID_CONNECT\",\"label\":\"oidc-app\",\"id\":\"0oa1\"},"
            + "{\"signOnMode\":\"SAML_2_0\",\"label\":\"saml-app\",\"id\":\"0oa2\",\"settings\":{\"signOn\":{"
            + "\"attributeStatements\":["
            + "{\"type\":\"EXPRESSION\",\"name\":\"email\",\"values\":[\"user.email\"]},"
            + "{\"type\":\"GROUP\",\"filterType\":\"STARTS_WITH\",\"filterValue\":\"Team\"}"
            + "]}}}"
            + "]";

        List<Application> apps = objectMapper.readValue(json, new TypeReference<List<Application>>() { });

        assertEquals(apps.size(), 2);
        assertTrue(apps.get(0) instanceof OpenIdConnectApplication, "expected OpenIdConnectApplication, got " + apps.get(0).getClass());
        assertTrue(apps.get(1) instanceof SamlApplication, "expected SamlApplication, got " + apps.get(1).getClass());

        SamlApplication samlApp = (SamlApplication) apps.get(1);
        List<SamlAttributeStatement> statements = samlApp.getSettings().getSignOn().getAttributeStatements();
        assertEquals(statements.size(), 2);
        assertEquals(statements.get(0).getType(), SamlAttributeStatement.TypeEnum.EXPRESSION);
        assertEquals(statements.get(1).getType(), SamlAttributeStatement.TypeEnum.GROUP);
    }

    /**
     * Found via an audit of every oneOf/anyOf + discriminator pair in the spec for the same defect class
     * as OKTA-1227472: OrgContactTypeObj declares BILLING/TECHNICAL subtypes that don't extend it, breaking
     * the real listOrgContactTypes endpoint.
     */
    @Test
    public void deserializeOrgContactTypeObj_withBillingAndTechnicalEntries_doesNotThrow() throws Exception {
        String json = "["
            + "{\"contactType\":\"BILLING\"},"
            + "{\"contactType\":\"TECHNICAL\"}"
            + "]";

        List<OrgContactTypeObj> contacts = objectMapper.readValue(json, new TypeReference<List<OrgContactTypeObj>>() { });

        assertEquals(contacts.size(), 2);
        assertEquals(contacts.get(0).getContactType(), OrgContactType.BILLING);
        assertEquals(contacts.get(1).getContactType(), OrgContactType.TECHNICAL);
    }
}
