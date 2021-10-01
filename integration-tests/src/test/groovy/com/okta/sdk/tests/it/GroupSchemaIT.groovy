/*
 * Copyright 2021-Present Okta, Inc.
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
import com.okta.sdk.resource.group.schema.GroupSchemaAttribute
import com.okta.sdk.resource.user.schema.UserSchemaAttributeMaster
import com.okta.sdk.resource.user.schema.UserSchemaAttributeMasterType
import com.okta.sdk.resource.user.schema.UserSchemaAttributePermission
import com.okta.sdk.resource.user.schema.UserSchemaAttributeScope
import com.okta.sdk.resource.user.schema.UserSchemaAttributeType
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test
import org.testng.collections.Lists
import wiremock.org.apache.commons.lang3.RandomStringUtils

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/meta/schemas/group/default}.
 * @since 6.x.x
 */
class GroupSchemaIT extends ITSupport {

    @Test (groups = "group1")
    @Scenario("get-group-schema")
    void getGroupSchemaTest() {

        GroupSchema schema = client.getGroupSchema()
        assertThat(schema, notNullValue())
        assertThat(schema.getId(), notNullValue())
        assertThat(schema.getDescription(), notNullValue())
        assertThat(schema.getName(), notNullValue())
        assertThat(schema.getSchema(), notNullValue())
        assertThat(schema.getTitle(), notNullValue())
        assertThat(schema.getType(), notNullValue())
        assertThat(schema.getType(), equalTo("object"))
        assertThat(schema.getDefinitions(), notNullValue())
        assertThat(schema.getDefinitions().getBase(), notNullValue())
        assertThat(schema.getDefinitions().getBase().getId(), notNullValue())
        assertThat(schema.getDefinitions().getBase().getType(), notNullValue())
        assertThat(schema.getDefinitions().getBase().getType(), equalTo("object"))
    }

    @Test (groups = "group2")
    @Scenario("update-group-schema")
    void updateGroupSchemaTest() {

        String customPropertyKey = "java_sdk_it_custom_property_" + RandomStringUtils.randomAlphanumeric(10)

        GroupSchema schema = client.getGroupSchema()
        schema.getDefinitions().getCustom().getProperties().put(customPropertyKey,
            client.instantiate(GroupSchemaAttribute)
                .setDescription("exampleCustomPropertyDescription")
                .setMaster(
                    client.instantiate(UserSchemaAttributeMaster)
                        .setType(UserSchemaAttributeMasterType.PROFILE_MASTER)
                )
                .setMaxLength(20)
                .setMinLength(1)
                .setMutability("READ_WRITE")
                .setPermissions(Lists.newArrayList(
                    client.instantiate(UserSchemaAttributePermission)
                            .setAction("READ_WRITE")
                            .setPrincipal("SELF"))
                )
                .setRequired(false)
                .setScope(UserSchemaAttributeScope.NONE)
                .setTitle("exampleCustomPropertyTitle")
                .setType(UserSchemaAttributeType.STRING)
                .setUnique("UNIQUE_VALIDATED")
        )

        GroupSchema updatedSchema = client.updateGroupSchema(schema)
        assertThat(updatedSchema.getId(), equalTo(schema.getId()))
        def groupSchemaAttribute = updatedSchema.getDefinitions().getCustom().getProperties().get(customPropertyKey)
        assertThat(groupSchemaAttribute, notNullValue())
        assertThat(groupSchemaAttribute["description"], equalTo("exampleCustomPropertyDescription"))
        assertThat(groupSchemaAttribute["master"]["type"], equalTo(UserSchemaAttributeMasterType.PROFILE_MASTER.toString()))
        assertThat(groupSchemaAttribute["maxLength"], equalTo(20))
        assertThat(groupSchemaAttribute["minLength"], equalTo(1))
        assertThat(groupSchemaAttribute["mutability"], equalTo("READ_WRITE"))
        assertThat(groupSchemaAttribute["permissions"], containsInAnyOrder(allOf(
            hasEntry("action", "READ_WRITE"),
            hasEntry("principal", "SELF"),
        )))
        assertThat(groupSchemaAttribute["scope"], equalTo(UserSchemaAttributeScope.NONE.toString()))
        assertThat(groupSchemaAttribute["title"], equalTo("exampleCustomPropertyTitle"))
        assertThat(groupSchemaAttribute["type"], equalTo(UserSchemaAttributeType.STRING.toString()))
        assertThat(groupSchemaAttribute["unique"], equalTo("UNIQUE_VALIDATED"))


        updatedSchema.getDefinitions().getCustom().getProperties().put(customPropertyKey, null)
        GroupSchema restoredSchema = client.updateGroupSchema(updatedSchema)
        assertThat(restoredSchema.getId(), equalTo(updatedSchema.getId()))
        assertThat(restoredSchema.getDefinitions().getCustom().getProperties().get(customPropertyKey), nullValue())
    }
}
