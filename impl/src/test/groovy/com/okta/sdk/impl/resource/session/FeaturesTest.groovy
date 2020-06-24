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
package com.okta.sdk.impl.resource.session

import com.okta.sdk.impl.ds.InternalDataStore
import com.okta.sdk.impl.resource.feature.DefaultFeature
import com.okta.sdk.resource.feature.Feature
import org.testng.annotations.Test

import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify

/**
 * Tests for the Features API.
 *
 * @since 2.0.0
 */
class FeaturesTest {

    @Test
    void toggleFeatureTest() {

        InternalDataStore dataStore = mock(InternalDataStore)

        Feature featureToEnable = new DefaultFeature(dataStore, [id: "test_feature_id", name: "test feature", status: "DISABLED"])

        // enable
        featureToEnable.updateLifecycle("enable", "force")

        verify(dataStore).create(
                (String) eq("/api/v1/features/test_feature_id/enable".toString()),
                any(),
                any(),
                (Class) eq(Feature.class),
                eq(Collections.singletonMap("mode", "force")),
                eq(Collections.emptyMap()))

        Feature featureToDisable = new DefaultFeature(dataStore, [id: "test_feature_id", name: "test feature", status: "ENABLED"])

        // disable
        featureToDisable.updateLifecycle("disable", "force")

        verify(dataStore).create(
            (String) eq("/api/v1/features/test_feature_id/disable".toString()),
            any(),
            any(),
            (Class) eq(Feature.class),
            eq(Collections.singletonMap("mode", "force")),
            eq(Collections.emptyMap()))
    }
}