/*
 * Copyright 2018-Present Okta, Inc.
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

import com.okta.sdk.resource.LinkedObjectDetails
import com.okta.sdk.resource.LinkedObjectDetailsType
import com.okta.sdk.resource.linked.object.LinkedObject
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test
import wiremock.org.apache.commons.lang3.RandomStringUtils

import static com.okta.sdk.tests.it.util.Util.assertPresent
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue

/**
 * Tests for {@code /api/v1/meta/schemas/user/linkedObjects}.
 * @since 2.0.0
 */
class LinkedObjectsIT extends ITSupport {

    @Test
    void addLinkedObjectDefinitionTest() {
        String primaryName = "java-sdk-linked-object-primary-" + RandomStringUtils.randomAlphanumeric(15)
        String associatedName = "java-sdk-linked-object-associated-" + RandomStringUtils.randomAlphanumeric(15)

        LinkedObjectDetails primary = client.instantiate(LinkedObjectDetails)
            .setName(primaryName)
            .setTitle("Primary")
            .setDescription("Primary link property")
            .setType(LinkedObjectDetailsType.USER)

        LinkedObjectDetails associated = client.instantiate(LinkedObjectDetails)
            .setName(associatedName)
            .setTitle("Associated")
            .setDescription("Associated link property")
            .setType(LinkedObjectDetailsType.USER)

        LinkedObject linkedObject = client.instantiate(LinkedObject)
            .setName("Linked Object")
            .setPrimary(primary)
            .setAssociated(associated)
        registerForCleanup(linkedObject)

        LinkedObject createdLinkedObjectDefinition = client.addLinkedObjectDefinition(linkedObject)

        assertThat(createdLinkedObjectDefinition.getPrimary(), notNullValue())
        assertThat(createdLinkedObjectDefinition.getPrimary().getName(), equalTo(primaryName))
        assertThat(createdLinkedObjectDefinition.getPrimary().getTitle(), equalTo("Primary"))
        assertThat(createdLinkedObjectDefinition.getPrimary().getDescription(), equalTo("Primary link property"))
        assertThat(createdLinkedObjectDefinition.getPrimary().getType(), equalTo(LinkedObjectDetailsType.USER))
        assertThat(createdLinkedObjectDefinition.getAssociated(), notNullValue())
        assertThat(createdLinkedObjectDefinition.getAssociated().getName(), equalTo(associatedName))
        assertThat(createdLinkedObjectDefinition.getAssociated().getTitle(), equalTo("Associated"))
        assertThat(createdLinkedObjectDefinition.getAssociated().getDescription(), equalTo("Associated link property"))
        assertThat(createdLinkedObjectDefinition.getAssociated().getType(), equalTo(LinkedObjectDetailsType.USER))
    }

    @Test
    void getLinkedObjectDefinitionByPrimaryNameTest() {
        String primaryName = "java-sdk-linked-object-primary-" + RandomStringUtils.randomAlphanumeric(15)
        String associatedName = "java-sdk-linked-object-associated-" + RandomStringUtils.randomAlphanumeric(15)

        LinkedObjectDetails primary = client.instantiate(LinkedObjectDetails)
            .setName(primaryName)
            .setTitle("Primary")
            .setDescription("Primary link property")
            .setType(LinkedObjectDetailsType.USER)

        LinkedObjectDetails associated = client.instantiate(LinkedObjectDetails)
            .setName(associatedName)
            .setTitle("Associated")
            .setDescription("Associated link property")
            .setType(LinkedObjectDetailsType.USER)

        LinkedObject linkedObject = client.instantiate(LinkedObject)
            .setName("Linked Object")
            .setPrimary(primary)
            .setAssociated(associated)
        registerForCleanup(linkedObject)

        LinkedObject createdLinkedObjectDefinition = client.addLinkedObjectDefinition(linkedObject)

        LinkedObject retrievedLinkedObject = client.getLinkedObjectDefinition(primaryName)

        assertThat(retrievedLinkedObject.getPrimary(), notNullValue())
        assertThat(retrievedLinkedObject.getPrimary().getName(), equalTo(primaryName))
        assertThat(retrievedLinkedObject.getPrimary().getTitle(), equalTo("Primary"))
        assertThat(retrievedLinkedObject.getPrimary().getDescription(), equalTo("Primary link property"))
        assertThat(retrievedLinkedObject.getPrimary().getType(), equalTo(LinkedObjectDetailsType.USER))
        assertThat(retrievedLinkedObject.getAssociated(), notNullValue())
        assertThat(retrievedLinkedObject.getAssociated().getName(), equalTo(associatedName))
        assertThat(retrievedLinkedObject.getAssociated().getTitle(), equalTo("Associated"))
        assertThat(retrievedLinkedObject.getAssociated().getDescription(), equalTo("Associated link property"))
        assertThat(retrievedLinkedObject.getAssociated().getType(), equalTo(LinkedObjectDetailsType.USER))
    }

    @Test
    void getLinkedObjectDefinitionByAssociatedNameTest() {
        String primaryName = "java-sdk-linked-object-primary-" + RandomStringUtils.randomAlphanumeric(15)
        String associatedName = "java-sdk-linked-object-associated-" + RandomStringUtils.randomAlphanumeric(15)

        LinkedObjectDetails primary = client.instantiate(LinkedObjectDetails)
            .setName(primaryName)
            .setTitle("Primary")
            .setDescription("Primary link property")
            .setType(LinkedObjectDetailsType.USER)

        LinkedObjectDetails associated = client.instantiate(LinkedObjectDetails)
            .setName(associatedName)
            .setTitle("Associated")
            .setDescription("Associated link property")
            .setType(LinkedObjectDetailsType.USER)

        LinkedObject linkedObject = client.instantiate(LinkedObject)
            .setName("Linked Object")
            .setPrimary(primary)
            .setAssociated(associated)
        registerForCleanup(linkedObject)

        LinkedObject retrievedLinkedObject = client.getLinkedObjectDefinition(associatedName)

        assertThat(retrievedLinkedObject.getPrimary(), notNullValue())
        assertThat(retrievedLinkedObject.getPrimary().getName(), equalTo(primaryName))
        assertThat(retrievedLinkedObject.getPrimary().getTitle(), equalTo("Primary"))
        assertThat(retrievedLinkedObject.getPrimary().getDescription(), equalTo("Primary link property"))
        assertThat(retrievedLinkedObject.getPrimary().getType(), equalTo(LinkedObjectDetailsType.USER))
        assertThat(retrievedLinkedObject.getAssociated(), notNullValue())
        assertThat(retrievedLinkedObject.getAssociated().getName(), equalTo(associatedName))
        assertThat(retrievedLinkedObject.getAssociated().getTitle(), equalTo("Associated"))
        assertThat(retrievedLinkedObject.getAssociated().getDescription(), equalTo("Associated link property"))
        assertThat(retrievedLinkedObject.getAssociated().getType(), equalTo(LinkedObjectDetailsType.USER))
    }

    @Test
    void getAllLinkedObjectDefinitionsTest() {
        // create first linked object definition

        String primaryName1 = "java-sdk-linked-object-primary-1-" + RandomStringUtils.randomAlphanumeric(15)
        String associatedName1 = "java-sdk-linked-object-associated-1-" + RandomStringUtils.randomAlphanumeric(15)

        LinkedObjectDetails primary1 = client.instantiate(LinkedObjectDetails)
            .setName(primaryName1)
            .setTitle("Primary")
            .setDescription("Primary link property")
            .setType(LinkedObjectDetailsType.USER)

        LinkedObjectDetails associated1 = client.instantiate(LinkedObjectDetails)
            .setName(associatedName1)
            .setTitle("Associated")
            .setDescription("Associated link property")
            .setType(LinkedObjectDetailsType.USER)

        LinkedObject linkedObject1 = client.instantiate(LinkedObject)
            .setName("Linked Object")
            .setPrimary(primary1)
            .setAssociated(associated1)
        registerForCleanup(linkedObject1)

        LinkedObject createdLinkedObjectDefinition1 = client.addLinkedObjectDefinition(linkedObject1)

        // create second linked object definition

        String primaryName2 = "java-sdk-linked-object-primary-2-" + RandomStringUtils.randomAlphanumeric(15)
        String associatedName2 = "java-sdk-linked-object-associated-2-" + RandomStringUtils.randomAlphanumeric(15)

        LinkedObjectDetails primary2 = client.instantiate(LinkedObjectDetails)
            .setName(primaryName2)
            .setTitle("Primary")
            .setDescription("Primary link property")
            .setType(LinkedObjectDetailsType.USER)

        LinkedObjectDetails associated2 = client.instantiate(LinkedObjectDetails)
            .setName(associatedName2)
            .setTitle("Associated")
            .setDescription("Associated link property")
            .setType(LinkedObjectDetailsType.USER)

        LinkedObject linkedObject2 = client.instantiate(LinkedObject)
            .setName("Linked Object")
            .setPrimary(primary2)
            .setAssociated(associated2)
        registerForCleanup(linkedObject2)

        LinkedObject createdLinkedObjectDefinition2 = client.addLinkedObjectDefinition(linkedObject2)

        sleep(2000)

        assertPresent(client.listLinkedObjectDefinitions(), createdLinkedObjectDefinition1)
        assertPresent(client.listLinkedObjectDefinitions(), createdLinkedObjectDefinition2)
    }
}
