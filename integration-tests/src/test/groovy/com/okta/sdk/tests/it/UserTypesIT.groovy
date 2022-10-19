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

import com.okta.sdk.resource.user.schema.UserSchema
import com.okta.sdk.resource.user.type.UserType
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test
import wiremock.org.apache.commons.lang3.RandomStringUtils

import static com.okta.sdk.tests.it.util.Util.assertNotPresent
import static com.okta.sdk.tests.it.util.Util.assertPresent
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue

/**
 * Tests for {@code /api/v1/meta/types/user}.
 * @since 2.0.0
 */
class UserTypesIT extends ITSupport {

    @Test
    void createUserTypeTest() {
        String name = "java_sdk_it_" + RandomStringUtils.randomAlphanumeric(15)

        UserType createdUserType = client.createUserType(client.instantiate(UserType)
            .setName(name)
            .setDisplayName(name)
            .setDescription(name + "_test_description"))
        registerForCleanup(createdUserType)

        assertThat(createdUserType.getId(), notNullValue())
        assertThat(createdUserType.getName(), equalTo(name))

        def schemaId = getSchemaIdForUserType(createdUserType)
        assertThat(schemaId, notNullValue())

        def userSchema = client.getUserSchema(schemaId)
        assertThat(userSchema, notNullValue())
        assertThat(userSchema.getLinks(), notNullValue())

        def userTypeId = getTypeIdFromUserSchema(userSchema)
        assertThat(userTypeId, equalTo(createdUserType.getId()))
        assertThat(userSchema.getDefinitions(), notNullValue())

        def userSchemaBase = userSchema.getDefinitions().getBase()
        assertThat(userSchemaBase, notNullValue())
        userSchemaBase.getRequired().forEach({ requiredItem ->
            assertThat(userSchemaBase.getProperties().containsKey(requiredItem), equalTo(true))
        })
    }

    @Test
    void getUserTypeTest() {
        String name = "java_sdk_it_" + RandomStringUtils.randomAlphanumeric(15)

        UserType createdUserType = client.createUserType(client.instantiate(UserType)
            .setName(name)
            .setDisplayName(name)
            .setDescription(name + "_test_description"))
        registerForCleanup(createdUserType)

        assertThat(createdUserType.getId(), notNullValue())

        UserType retrievedUserType = client.getUserType(createdUserType.getId())
        assertThat(retrievedUserType.getId(), equalTo(createdUserType.getId()))
        assertThat(retrievedUserType.getName(), equalTo(createdUserType.getName()))
    }

    @Test
    void updateUserTypeTest() {
        String name = "java_sdk_it_" + RandomStringUtils.randomAlphanumeric(15)

        UserType createdUserType = client.createUserType(client.instantiate(UserType)
            .setName(name)
            .setDisplayName(name)
            .setDescription(name + "_test_description"))
        registerForCleanup(createdUserType)

        assertThat(createdUserType.getId(), notNullValue())

        createdUserType.setDisplayName(name + "_updated").setDescription(name + "_test_description_updated")
            .update()

        assertThat(createdUserType.getId(), notNullValue())
        assertThat(createdUserType.getDisplayName(), equalTo(name + "_updated"))
        assertThat(createdUserType.getDescription(), equalTo(name + "_test_description_updated"))

        def schemaId = getSchemaIdForUserType(createdUserType)
        assertThat(schemaId, notNullValue())

        def userSchema = client.getUserSchema(schemaId)
        assertThat(userSchema, notNullValue())

        userSchema.getDefinitions().getCustom().getProperties().put("customPropertyName",
            new LinkedHashMap() {
                {
                    put("title", "Title of custom property")
                    put("description", "Description of custom property")
                    put("type", "string")
                    put("permissions", new ArrayList() {
                        {
                            add(
                                new LinkedHashMap() {
                                    {
                                        put("principal", "SELF")
                                        put("action", "READ_ONLY")
                                    }
                                }
                            )
                        }
                    })
                }
            })

        def updatedUserSchema = client.updateUserProfile(schemaId, userSchema)
        assertThat(updatedUserSchema, notNullValue())
        assertThat(updatedUserSchema.getDefinitions().getCustom(), notNullValue())

        def userSchemaPublic = updatedUserSchema.getDefinitions().getCustom()
        assertThat(userSchemaPublic.getProperties().containsKey("customPropertyName"), equalTo(true))

        def customPropertyMap = userSchemaPublic.getProperties().get("customPropertyName")
        assertThat(customPropertyMap["title"], equalTo("Title of custom property"))
        assertThat(customPropertyMap["description"], equalTo("Description of custom property"))
        assertThat(customPropertyMap["type"], equalTo("string"))
        assertThat(customPropertyMap["permissions"][0]["principal"], equalTo("SELF"))
        assertThat(customPropertyMap["permissions"][0]["action"], equalTo("READ_ONLY"))
    }

    @Test (groups = "group2")
    void deleteUserTypeTest() {
        String name = "java_sdk_it_" + RandomStringUtils.randomAlphanumeric(15)

        UserType createdUserType = client.createUserType(client.instantiate(UserType)
            .setName(name)
            .setDisplayName(name)
            .setDescription(name + "_test_description"))
        registerForCleanup(createdUserType)

        assertThat(createdUserType.getId(), notNullValue())

        createdUserType.delete()

        assertNotPresent(client.listUserTypes(), createdUserType)
    }

    @Test (groups = "group2")
    void listAllUserTypesTest() {
        String name1 = "java_sdk_it_" + RandomStringUtils.randomAlphanumeric(15)

        UserType createdUserType1 = client.createUserType(client.instantiate(UserType)
            .setName(name1)
            .setDisplayName(name1)
            .setDescription(name1 + "_test_description"))
        registerForCleanup(createdUserType1)

        assertThat(createdUserType1.getId(), notNullValue())

        String name2 = "java_sdk_it_" + RandomStringUtils.randomAlphanumeric(15)

        UserType createdUserType2 = client.createUserType(client.instantiate(UserType)
            .setName(name2)
            .setDisplayName(name2)
            .setDescription(name2 + "_test_description"))
        registerForCleanup(createdUserType2)

        assertThat(client.listUserTypes(), notNullValue())
        assertPresent(client.listUserTypes(), createdUserType1)
        assertPresent(client.listUserTypes(), createdUserType2)
    }

    String getSchemaIdForUserType(UserType userType) {
        def schema = userType.getLinks().get("schema")
        assertThat(schema, notNullValue())
        assertThat(schema instanceof LinkedHashMap, equalTo(true))

        def schemaHref = (schema as LinkedHashMap).get("href")
        assertThat(schemaHref, notNullValue())

        def schemaId = schemaHref.toString().split("/").last()
        assertThat(schemaId, notNullValue())

        return schemaId
    }

    String getTypeIdFromUserSchema(UserSchema userSchema) {

        def type = userSchema.getLinks().get("type")
        assertThat(type, notNullValue())
        assertThat(type instanceof LinkedHashMap, equalTo(true))

        def typeHref = (type as LinkedHashMap).get("href")
        assertThat(typeHref, notNullValue())

        def typeId = typeHref.toString().split("/").last()
        assertThat(typeId, notNullValue())

        return typeId
    }


}
