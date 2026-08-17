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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.okta.commons.lang.Assert;
import com.okta.sdk.resource.api.ApplicationApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.Application;

import java.util.Iterator;
import java.util.Map;

/**
 * Helper for updating an {@link Application} when the caller only has a subset of its properties to
 * change (OKTA-1228269).
 *
 * <p>The Admin Management API's {@code PUT /api/v1/apps/{appId}} (backing
 * {@link ApplicationApi#replaceApplication}) is a full-replace endpoint - the spec requires every required
 * property on every call, and there is no {@code PATCH} operation for applications. Sending a partial
 * {@link Application} (or subtype, e.g. {@code OpenIdConnectApplication}) directly to
 * {@code replaceApplication} is rejected with a {@code 400}, because the server can't validate or resolve
 * the application's concrete type from an incomplete body.
 *
 * <p>This helper works around that by fetching the current application, overlaying only the properties
 * set on the caller's partial object, and replacing with the merged, fully-populated result - giving
 * callers effective partial-update semantics without violating the API's full-replace contract.
 *
 * <p>Because the overlay only applies properties that are set (non-null) on {@code partialApplication},
 * this is a merge, not a JSON Merge Patch: explicitly setting a property to {@code null} does not clear
 * it on the existing application. Call {@link ApplicationApi#replaceApplication} directly with a
 * fully-populated object to clear a property.
 *
 * <p>{@code partialApplication} must be the same concrete subtype as the application being updated (e.g.
 * both {@code OpenIdConnectApplication}) - merging across subtypes would blend fields from incompatible
 * application types, so it's rejected up front rather than producing a confusing server-side error.
 *
 * @since 25.0.5
 */
public final class PartialApplicationUpdater {

    private PartialApplicationUpdater() {
    }

    /**
     * Fetches the application identified by {@code appId}, overlays the properties set on
     * {@code partialApplication} onto it, and replaces the application with the merged result.
     *
     * @param apiClient the {@link ApiClient} to use for the underlying get/replace calls
     * @param appId the {@code id} of the application to update
     * @param partialApplication an {@link Application} (or subtype) with only the changed properties set
     * @return the updated {@link Application} as returned by the API
     * @throws ApiException if the underlying get or replace call fails
     */
    public static Application partialUpdateApplication(ApiClient apiClient, String appId,
            Application partialApplication) throws ApiException {

        Assert.notNull(apiClient, "apiClient cannot be null");
        Assert.hasText(appId, "appId cannot be null or empty");
        Assert.notNull(partialApplication, "partialApplication cannot be null");

        ApplicationApi applicationApi = new ApplicationApi(apiClient);
        Application existingApplication = applicationApi.getApplication(appId, null);
        Application mergedApplication = mergeApplication(apiClient.getObjectMapper(), existingApplication, partialApplication);

        return applicationApi.replaceApplication(appId, mergedApplication);
    }

    /**
     * Overlays the properties set on {@code partialApplication} onto {@code existingApplication}, returning
     * a new, fully-populated {@link Application} suitable for {@link ApplicationApi#replaceApplication}.
     *
     * <p>Exposed separately from {@link #partialUpdateApplication} so the merge itself can be exercised
     * without a live {@link ApiClient}.
     *
     * @param objectMapper the {@link ObjectMapper} to serialize/merge/deserialize with - use
     *                      {@link ApiClient#getObjectMapper()} so the same Application-subtype
     *                      null-omission and polymorphism behavior applies
     * @param existingApplication the current, fully-populated application
     * @param partialApplication an {@link Application} (or subtype) with only the changed properties set
     * @return the merged, fully-populated {@link Application}
     */
    public static Application mergeApplication(ObjectMapper objectMapper, Application existingApplication,
            Application partialApplication) {

        Assert.notNull(objectMapper, "objectMapper cannot be null");
        Assert.notNull(existingApplication, "existingApplication cannot be null");
        Assert.notNull(partialApplication, "partialApplication cannot be null");
        Assert.isTrue(partialApplication.getClass().equals(existingApplication.getClass()),
            "partialApplication must be the same concrete type as existingApplication (expected "
                + existingApplication.getClass().getSimpleName() + ", got "
                + partialApplication.getClass().getSimpleName() + ")");

        // existingNode retains read-only fields (id, created, lastUpdated, _links) from the fetched
        // application; replaceApplication tolerates receiving them back unchanged.
        JsonNode existingNode = objectMapper.valueToTree(existingApplication);
        JsonNode partialNode = objectMapper.valueToTree(partialApplication);
        JsonNode mergedNode = deepMerge(existingNode, partialNode);

        try {
            return objectMapper.treeToValue(mergedNode, Application.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to merge partial application update", e);
        }
    }

    private static JsonNode deepMerge(JsonNode base, JsonNode overlay) {
        if (!(base instanceof ObjectNode) || !(overlay instanceof ObjectNode)) {
            return overlay;
        }

        ObjectNode merged = ((ObjectNode) base).deepCopy();
        Iterator<Map.Entry<String, JsonNode>> overlayFields = overlay.fields();
        while (overlayFields.hasNext()) {
            Map.Entry<String, JsonNode> field = overlayFields.next();
            JsonNode baseValue = merged.get(field.getKey());
            JsonNode overlayValue = field.getValue();
            if (baseValue != null && baseValue.isObject() && overlayValue.isObject()) {
                merged.set(field.getKey(), deepMerge(baseValue, overlayValue));
            } else {
                merged.set(field.getKey(), overlayValue);
            }
        }
        return merged;
    }
}
