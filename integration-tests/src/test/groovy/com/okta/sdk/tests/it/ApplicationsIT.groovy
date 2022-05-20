/*
 * Copyright 2017-Present Okta, Inc.
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

import com.okta.commons.http.MediaType
import com.okta.sdk.client.Client
import com.okta.sdk.impl.ds.DefaultDataStore
import com.okta.sdk.resource.Resource
import com.okta.sdk.resource.ResourceException
import com.okta.sdk.resource.application.*
import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.inline.hook.InlineHook
import com.okta.sdk.resource.inline.hook.InlineHookBuilder
import com.okta.sdk.resource.inline.hook.InlineHookChannel
import com.okta.sdk.resource.inline.hook.InlineHookType
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.schema.UserSchema
import com.okta.sdk.resource.user.schema.UserSchemaDefinitions
import com.okta.sdk.resource.user.schema.UserSchemaPublic
import com.okta.sdk.tests.it.util.ITSupport
import org.openapitools.client.ApiClient
import org.openapitools.client.api.ApplicationApi
import org.openapitools.client.model.Application
import org.openapitools.client.model.OpenIdConnectApplication
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import org.testng.Assert
import org.testng.annotations.Test

import javax.xml.parsers.DocumentBuilderFactory

import static com.okta.sdk.tests.it.util.Util.assertNotPresent
import static com.okta.sdk.tests.it.util.Util.assertPresent
import static com.okta.sdk.tests.it.util.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/apps}.
 * @since 0.9.0
 */
class ApplicationsIT extends ITSupport {

    @Test
    void doCrudTest(Application app) {

        RestTemplate restTemplate = new RestTemplate()
        ApiClient apiClient = new ApiClient(restTemplate)

        apiClient.invokeAPI("https://java-sdk.oktapreview.com/api/v1/apps",
            HttpMethod.GET,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )

    }
}
