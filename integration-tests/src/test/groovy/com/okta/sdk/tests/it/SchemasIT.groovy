/*
 * Copyright 2017-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
import org.openapitools.client.api.SchemaApi
import org.openapitools.client.model.UserSchema
import org.openapitools.client.model.UserSchemaAttribute
import org.openapitools.client.model.UserSchemaAttributePermission
import org.openapitools.client.model.UserSchemaAttributeScope
import org.openapitools.client.model.UserSchemaAttributeType
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/meta/schemas}.
 */
class SchemasIT extends ITSupport {

    @Test (groups = "group2")
    void getUserSchemaTest() {

        SchemaApi schemaApi = new SchemaApi(getClient())

        UserSchema userSchema = schemaApi.getUserSchema("default")

        assertThat(userSchema, notNullValue())
        assertThat(userSchema.getId(), notNullValue())
        assertThat(userSchema.getName(), equalTo("user"))
        assertThat(userSchema.getTitle(), equalTo("User"))
        assertThat(userSchema.getType(), equalTo("object"))
        assertThat(userSchema.getDefinitions().getBase().getProperties().getLogin().getTitle(), equalTo("Username"))
        assertThat(userSchema.getDefinitions().getBase().getProperties().getLogin().getType(), equalTo(UserSchemaAttributeType.STRING))
        assertThat(userSchema.getDefinitions().getBase().getProperties().getLogin().getRequired(), is(true))
        assertThat(userSchema.getDefinitions().getBase().getProperties().getLogin().getMutability(), equalTo("READ_WRITE"))
        assertThat(userSchema.getDefinitions().getBase().getProperties().getLogin().getScope(), equalTo(UserSchemaAttributeScope.NONE))
        assertThat(userSchema.getDefinitions().getBase().getProperties().getLogin().getMinLength(), equalTo(5))
        assertThat(userSchema.getDefinitions().getBase().getProperties().getLogin().getMaxLength(), equalTo(100))
        assertThat(userSchema.getDefinitions().getBase().getProperties().getLogin().getPermissions().first().getPrincipal(), equalTo("SELF"))
        assertThat(userSchema.getDefinitions().getBase().getProperties().getLogin().getPermissions().first().getAction(), equalTo("READ_ONLY"))
        assertThat(userSchema.getDefinitions().getBase().getRequired(), hasItem("login"))
    }

    @Test (groups = "group2")
    void updateUserProfileSchemaPropertyTest() {

        String name = "test_" + UUID.randomUUID().toString().replaceAll("-", "_")
        String desc = UUID.randomUUID().toString().replaceAll("-", "_")

        SchemaApi schemaApi = new SchemaApi(getClient())

        UserSchema userSchema = schemaApi.getUserSchema("default")

        // add custom attribute
        UserSchemaAttribute customAttributeDetails = new UserSchemaAttribute()
        UserSchemaAttributePermission userSchemaAttributePermission = new UserSchemaAttributePermission()
        userSchemaAttributePermission.setAction("READ_WRITE")
        userSchemaAttributePermission.setPrincipal("SELF")
        List<UserSchemaAttributePermission> userSchemaAttributePermissionList = new ArrayList<>()
        userSchemaAttributePermissionList.add(userSchemaAttributePermission)

        customAttributeDetails.setTitle(name)
        customAttributeDetails.setType(UserSchemaAttributeType.STRING)
        customAttributeDetails.setDescription(desc)
        customAttributeDetails.setMinLength(1)
        customAttributeDetails.setMaxLength(20)
        customAttributeDetails.setPermissions(userSchemaAttributePermissionList)

        Map<String, UserSchemaAttribute> customAttribute = new HashMap<>()
        customAttribute.put(name, customAttributeDetails)

        userSchema.getDefinitions().getCustom().setProperties(customAttribute)

        UserSchema updatedUserSchema = schemaApi.updateUserProfile("default", userSchema)

        UserSchemaAttribute retrievedCustomAttribute = updatedUserSchema.getDefinitions().getCustom().getProperties().get(name)

        assertThat(retrievedCustomAttribute.getType(), equalTo(UserSchemaAttributeType.STRING))
        assertThat(retrievedCustomAttribute.getDescription(), equalTo(desc))
        assertThat(retrievedCustomAttribute.getMinLength(), equalTo(1))
        assertThat(retrievedCustomAttribute.getMaxLength(), equalTo(20))
        assertThat(retrievedCustomAttribute.getPermissions().first().getPrincipal(), equalTo("SELF"))
        assertThat(retrievedCustomAttribute.getPermissions().first().getAction(), equalTo("READ_WRITE"))

        // wait for job to be finished
        sleep(1000)

        // Remove custom attribute
        customAttribute.put(name, null)
        updatedUserSchema.getDefinitions().getCustom().setProperties(customAttribute)

        updatedUserSchema = schemaApi.updateUserProfile("default", updatedUserSchema)

        assertThat(updatedUserSchema.getDefinitions().getCustom().getProperties().containsKey(name), equalTo(false))
    }
}
