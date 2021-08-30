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

import com.okta.sdk.resource.group.schema.GroupSchema
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue

/**
 * Tests for {@code /api/v1/meta/schemas/group/default}.
 * @since 5.0.0
 */
class GroupSchemaIT extends ITSupport {

    @Test (groups = "group1")
    @Scenario("get-group-schema")
    void getGroupSchemaTest() {

        GroupSchema schema = client.getGroupSchema()
        assertThat(schema.getId(), notNullValue())
        assertThat(schema.getName(), notNullValue())
        assertThat(schema.getSchema(), notNullValue())
        assertThat(schema.getTitle(), notNullValue())
        assertThat(schema.getType(), notNullValue())
    }

    @Test (groups = "group2")
    @Scenario("update-group-schema")
    void updateGroupSchemaTest() {

        String title = "java-sdk-it-" + UUID.randomUUID().toString()
        GroupSchema schema = client.getGroupSchema()
        schema.setTitle(title)

        GroupSchema updatedSchema = client.updateGroupSchema(schema)
        assertThat(updatedSchema.getId(), equalTo(schema.getId()))
        assertThat(updatedSchema.getTitle(), equalTo(title))
    }
}
