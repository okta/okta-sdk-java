/*
 * Copyright 2017 Okta
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
package com.okta.sdk.impl.resource

import org.testng.annotations.Test
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.assertThat

class AbstractResourceTest {

    @Test
    void matchingTemplateUrl() {
        runScenario("/api/v1/users/{userId}/factors/{factorId}",
                    "/api/v1/users/my-userId/factors/my-factorId",
                    [ userId: "my-userId",
                      factorId: "my-factorId" ])
    }

    @Test
    void fullHref() {
        runScenario("/api/v1/users/{userId}/factors/{factorId}",
                    "https://example.com/api/v1/users/my-userId/factors/my-factorId",
                     [ userId: "my-userId",
                     factorId: "my-factorId"])
    }

    @Test
    void longerTemplateThenHref() {
        runScenario("/api/v1/users/{userId}/factors/{factorId}",
                    "https://example.com/api/v1/users/my-userId",
                     [ userId: "my-userId"])
    }

    @Test
    void longerHrefThenTemplate() {
        runScenario("/api/v1/users/{userId}/factors/{factorId}",
                    "https://example.com/api/v1/users/my-userId/factors/my-factorId/anotherAction",
                     [ userId: "my-userId",
                     factorId: "my-factorId"])
    }

    @Test
    void oddHref() {
        runScenario("/api/v1/users/{userId}/factors/{factorId}",
                    "foobar",
                     Collections.emptyMap())
    }

    @Test(expectedExceptions = URIParseException)
    void invalidHref() {
        runScenario("/api/v1/users/{userId}/factors/{factorId}",
                    " 😡  ",
                     null)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void missingHref() {
        runScenario("/api/v1/users/{userId}/factors/{factorId}",
                null,
                null)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void nullTemplate() {
        runScenario(null,
                "https://example.com/api/v1/users/my-userId/factors/my-factorId/anotherAction",
                null)
    }

    private static void runScenario(String templateUrl, String href, Map<String, String> expectedValues) {

        def resource = new AbstractResource(null){
            @Override
            Map<String, Property> getPropertyDescriptors() {
                return Collections.emptyMap()
            }
        }

        resource.setResourceHref(href)
        def actualMap = resource.getParamsFromHref(templateUrl)
        assertThat actualMap, equalTo(expectedValues)
    }
}