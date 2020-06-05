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

import com.okta.sdk.resource.ResourceException
import com.okta.sdk.resource.common.EnabledStatus
import com.okta.sdk.resource.feature.Feature
import com.okta.sdk.resource.feature.FeatureList
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.Assert
import org.testng.annotations.Test
import wiremock.org.apache.commons.lang3.RandomStringUtils

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for /api/v1/features
 * @since 2.0.0
 */
class FeaturesIT extends ITSupport {

    @Test
    void featureOperationsTest() {
        // list features
        FeatureList featureList = client.listFeatures()
        assertThat(featureList, iterableWithSize(greaterThanOrEqualTo(1)))

        // get first feature from the list (size >= 1)
        Feature feature = featureList.getAt(0)
        assertThat(feature.id, notNullValue())

        EnabledStatus enabledStatus = feature.getStatus()

        // update a feature (lifecycle)
        if (enabledStatus == EnabledStatus.ENABLED) {
            // disable it
            // "force" ensures all dependencies are also updated, if any
            feature.updateLifecycle("false", "force")
            assertThat(feature.getStatus(), is(EnabledStatus.DISABLED))
        } else {
            // enabling it programmatically may not work for all features
            // ref: https://developer.okta.com/docs/reference/api/features/#error-response-method-not-allowed
            try {
                feature.updateLifecycle("true", "force")
                assertThat(feature.getStatus(), is(EnabledStatus.ENABLED))
            } catch (ResourceException e) {
                assertThat e.status, equalTo(405)
            }
        }
    }
}
