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

import com.okta.sdk.resource.feature.Feature
import com.okta.sdk.resource.feature.FeatureList
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/features}.
 * @since 2.0.0
 */
class FeaturesIT extends ITSupport {

    @Test (groups = "group2")
    void featureOperationsTest() {
        // list features
        FeatureList featureList = client.listFeatures()
        assertThat(featureList, iterableWithSize(greaterThan(0)))

        // pick first one from the list
        Feature feature = featureList[0]
        assertThat(feature, notNullValue())

        // get feature by id
        Feature retrievedFeature = client.getFeature(feature.getId())
        assertThat(retrievedFeature, notNullValue())
        assertThat(retrievedFeature.getId(), equalTo(feature.getId()))
        assertThat(retrievedFeature.getName(), equalTo(feature.getName()))
        assertThat(retrievedFeature.getDescription(), equalTo(feature.getDescription()))
    }
}
