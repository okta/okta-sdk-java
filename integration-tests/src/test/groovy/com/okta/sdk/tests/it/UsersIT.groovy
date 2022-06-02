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

import com.okta.sdk.tests.it.util.ITSupport
import org.openapitools.client.model.User
import org.testng.annotations.Test

/**
 * Tests for {@code /api/v1/users}.
 * @since 0.5.0
 */
class UsersIT extends ITSupport {

    @Test
    void doCrudTest() {

        User user = randomUser()
        println(user)
    }
}
