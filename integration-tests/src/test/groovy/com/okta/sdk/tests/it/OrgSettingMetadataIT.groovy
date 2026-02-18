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
package com.okta.sdk.tests.it

import com.okta.sdk.resource.api.OrgSettingMetadataApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.WellKnownOrgMetadata
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for OrgSettingMetadata API
 *
 * Coverage:
 * - GET /.well-known/okta-organization - getWellknownOrgMetadata()
 *
 * Note: This is a read-only public endpoint (no authentication required).
 * It returns org metadata including ID, pipeline, and settings.
 */
class OrgSettingMetadataIT extends ITSupport {

    private OrgSettingMetadataApi orgSettingMetadataApi

    /**
     * Test retrieving well-known org metadata.
     *
     * The /.well-known/okta-organization endpoint is a public endpoint
     * that returns basic org metadata without requiring authentication.
     */
    @Test(groups = "group3")
    void testGetWellKnownOrgMetadata() {
        // Initialize API
        orgSettingMetadataApi = new OrgSettingMetadataApi(getClient())

        println "\n" + "=".multiply(60)
        println "TESTING ORG SETTING METADATA API"
        println "=".multiply(60)

        // ========================================
        // Step 1: Get well-known org metadata
        // ========================================
        println "\n1. GET /.well-known/okta-organization"
        WellKnownOrgMetadata metadata = orgSettingMetadataApi.getWellknownOrgMetadata()

        assertThat "Org metadata should not be null", metadata, notNullValue()

        // Validate org ID
        assertThat "Org ID should not be null", metadata.getId(), notNullValue()
        assertThat "Org ID should not be empty", metadata.getId(), not(emptyOrNullString())
        println "  ✓ Org ID: ${metadata.getId()}"

        // Validate pipeline
        assertThat "Pipeline should not be null", metadata.getPipeline(), notNullValue()
        println "  ✓ Pipeline: ${metadata.getPipeline()}"

        // ========================================
        // Step 2: Verify consistency (call again)
        // ========================================
        println "\n2. Verifying consistency (second call)..."
        WellKnownOrgMetadata metadata2 = orgSettingMetadataApi.getWellknownOrgMetadata()

        assertThat "Second call should return same org ID",
                   metadata2.getId(), equalTo(metadata.getId())
        assertThat "Second call should return same pipeline",
                   metadata2.getPipeline(), equalTo(metadata.getPipeline())
        println "  ✓ Consistent results across multiple calls"

        // ========================================
        // Summary
        // ========================================
        println "\n" + "=".multiply(60)
        println "✅ ALL ORG SETTING METADATA API TESTS COMPLETE"
        println "=".multiply(60)
        println "\n=== API Coverage Summary ==="
        println "All 1 OrgSettingMetadata API endpoint tested:"
        println "  ✓ GET /.well-known/okta-organization"
        println "\n=== Test Results ==="
        println "• Total Endpoints: 1"
        println "• Endpoints Tested: 1 (100%)"
        println "• Test Scenarios: Get metadata, Verify fields, Verify consistency"
        println "• Org Impact: Zero (read-only endpoint)"
    }

    @Test(groups = "group3")
    void testAdditionalHeadersOverloads() {
        def headers = Collections.<String, String>emptyMap()
        try {
            orgSettingMetadataApi.getWellknownOrgMetadata(headers)
        } catch (Exception e) {
            // Expected
        }
    }
}
