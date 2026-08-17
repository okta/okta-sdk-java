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
package com.okta.sdk.resource.application;

import com.okta.sdk.cache.Cache;
import com.okta.sdk.cache.CacheManager;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.model.Application;
import com.okta.sdk.resource.model.ApplicationCredentialsOAuthClient;
import com.okta.sdk.resource.model.ApplicationSignOnMode;
import com.okta.sdk.resource.model.ApplicationVisibility;
import com.okta.sdk.resource.model.OAuthApplicationCredentials;
import com.okta.sdk.resource.model.OpenIdConnectApplication;
import com.okta.sdk.resource.model.OpenIdConnectApplicationSettings;
import com.okta.sdk.resource.model.OpenIdConnectApplicationSettingsClient;
import com.okta.sdk.resource.model.SamlApplication;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for {@link PartialApplicationUpdater}, exercising the exact partial-update scenarios
 * reported live against the Admin Management API in OKTA-1228269: omitting {@code name} and/or
 * {@code credentials} from an {@code OpenIdConnectApplication} PUT gets rejected by the server with
 * misleading {@code 400}s (missing app settings / invalid signOnMode), because those are required,
 * ALWAYS-serialized sibling fields on a polymorphic subtype. These tests verify the merge step - not a
 * live API call - so they only need to prove the merged body is always fully populated with the
 * existing application's required fields, regardless of what the caller's partial object omits.
 */
public class PartialApplicationUpdaterTest {

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

    private final ApiClient apiClient = new ApiClient(HttpClients.createDefault(), NOOP_CACHE_MANAGER);

    private static OpenIdConnectApplication fullyPopulatedExistingApp() {
        OpenIdConnectApplication app = new OpenIdConnectApplication();
        app.setLabel("partial-update-test");
        app.setSignOnMode(ApplicationSignOnMode.OPENID_CONNECT);
        app.setName(OpenIdConnectApplication.NameEnum.OIDC_CLIENT);
        app.setVisibility(new ApplicationVisibility().autoSubmitToolbar(true));

        OAuthApplicationCredentials credentials = new OAuthApplicationCredentials()
            .oauthClient(new ApplicationCredentialsOAuthClient()
                .clientId("existing-client-id")
                .autoKeyRotation(true));
        app.setCredentials(credentials);

        OpenIdConnectApplicationSettingsClient oauthClient = new OpenIdConnectApplicationSettingsClient()
            .redirectUris(List.of("https://example.com/callback"))
            .clientUri("https://example.com");
        app.setSettings(new OpenIdConnectApplicationSettings().oauthClient(oauthClient));

        return app;
    }

    /**
     * Agrja Rastogi's first repro on OKTA-1228269: a partial update that sets only settings fields,
     * omitting both {@code name} and {@code credentials} entirely, produced a {@code 400} ("Invalid
     * signOnMode; settings.signOn doesn't match type; Missing visibility") because the server couldn't
     * resolve the OIDC subtype without them. The merge must restore both from the existing application.
     */
    @Test
    public void mergeApplication_partialOmittingNameAndCredentials_restoresBothFromExisting() throws Exception {
        OpenIdConnectApplication existing = fullyPopulatedExistingApp();

        OpenIdConnectApplication partial = new OpenIdConnectApplication();
        partial.setSettings(new OpenIdConnectApplicationSettings()
            .oauthClient(new OpenIdConnectApplicationSettingsClient()
                .redirectUris(List.of("https://example.com/new-callback"))));

        Application merged = PartialApplicationUpdater.mergeApplication(
            apiClient.getObjectMapper(), existing, partial);

        assertTrue(merged instanceof OpenIdConnectApplication, "expected OpenIdConnectApplication, got " + merged.getClass());
        OpenIdConnectApplication mergedOidc = (OpenIdConnectApplication) merged;

        assertEquals(mergedOidc.getName(), OpenIdConnectApplication.NameEnum.OIDC_CLIENT);
        assertEquals(mergedOidc.getCredentials().getOauthClient().getClientId(), "existing-client-id");
    }

    /**
     * Agrja's second repro: omitting {@code credentials} alone (with {@code name} correct) produced a
     * {@code 400} ("Missing app settings") referencing a field irrelevant to OIDC apps. The merge must
     * restore credentials from the existing application while still applying the caller's settings change.
     */
    @Test
    public void mergeApplication_partialOmittingCredentialsOnly_restoresCredentialsAndAppliesSettingsChange() throws Exception {
        OpenIdConnectApplication existing = fullyPopulatedExistingApp();

        OpenIdConnectApplication partial = new OpenIdConnectApplication();
        partial.setName(OpenIdConnectApplication.NameEnum.OIDC_CLIENT);
        partial.setSettings(new OpenIdConnectApplicationSettings()
            .oauthClient(new OpenIdConnectApplicationSettingsClient()
                .redirectUris(List.of("https://example.com/new-callback"))));

        Application merged = PartialApplicationUpdater.mergeApplication(
            apiClient.getObjectMapper(), existing, partial);

        OpenIdConnectApplication mergedOidc = (OpenIdConnectApplication) merged;
        assertEquals(mergedOidc.getCredentials().getOauthClient().getClientId(), "existing-client-id");
        assertEquals(mergedOidc.getSettings().getOauthClient().getRedirectUris(), List.of("https://example.com/new-callback"));
    }

    /**
     * The merge must be a deep merge of nested objects, not a full replace of {@code settings.oauthClient}:
     * changing only {@code redirectUris} must not drop sibling settings fields (e.g. {@code clientUri})
     * that the caller's partial object never touched.
     */
    @Test
    public void mergeApplication_partialSettingsChange_preservesUntouchedSiblingSettingsFields() throws Exception {
        OpenIdConnectApplication existing = fullyPopulatedExistingApp();

        OpenIdConnectApplication partial = new OpenIdConnectApplication();
        partial.setSettings(new OpenIdConnectApplicationSettings()
            .oauthClient(new OpenIdConnectApplicationSettingsClient()
                .redirectUris(List.of("https://example.com/new-callback"))));

        Application merged = PartialApplicationUpdater.mergeApplication(
            apiClient.getObjectMapper(), existing, partial);

        OpenIdConnectApplicationSettingsClient mergedOauthClient =
            ((OpenIdConnectApplication) merged).getSettings().getOauthClient();
        assertEquals(mergedOauthClient.getRedirectUris(), List.of("https://example.com/new-callback"));
        assertEquals(mergedOauthClient.getClientUri(), "https://example.com", "untouched sibling field should survive the merge");
    }

    /**
     * A true no-op-on-everything-else partial update (only {@code label} changed) must preserve every
     * other required field untouched - the simplest, most common partial-update case.
     */
    @Test
    public void mergeApplication_labelOnlyChange_preservesEverythingElse() throws Exception {
        OpenIdConnectApplication existing = fullyPopulatedExistingApp();

        OpenIdConnectApplication partial = new OpenIdConnectApplication();
        partial.setLabel("renamed-app");

        Application merged = PartialApplicationUpdater.mergeApplication(
            apiClient.getObjectMapper(), existing, partial);

        assertEquals(merged.getLabel(), "renamed-app");
        OpenIdConnectApplication mergedOidc = (OpenIdConnectApplication) merged;
        assertEquals(mergedOidc.getName(), OpenIdConnectApplication.NameEnum.OIDC_CLIENT);
        assertEquals(mergedOidc.getCredentials().getOauthClient().getClientId(), "existing-client-id");
        assertEquals(mergedOidc.getSettings().getOauthClient().getRedirectUris(), List.of("https://example.com/callback"));
    }

    /**
     * A partial object of a different concrete subtype than the application being updated (e.g. a
     * {@code SamlApplication} partial against an OIDC app) would blend fields from incompatible
     * application types if merged - reject it immediately instead of producing a confusing result.
     */
    @Test
    public void mergeApplication_partialOfDifferentSubtype_throwsImmediately() {
        OpenIdConnectApplication existing = fullyPopulatedExistingApp();
        SamlApplication mismatchedPartial = new SamlApplication();

        assertThrows(IllegalArgumentException.class, () ->
            PartialApplicationUpdater.mergeApplication(apiClient.getObjectMapper(), existing, mismatchedPartial));
    }
}
