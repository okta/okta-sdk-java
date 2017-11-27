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
package com.okta.sdk.tests.it

import com.okta.sdk.client.Client
import com.okta.sdk.resource.application.*
import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue

class ApplicationMiscIT extends ITSupport {

    @Test
    void applicationKeysTest() {

        Client client = getClient()

        Application app1 = client.instantiate(AutoLoginApplication)
                .setLabel("app-${UUID.randomUUID().toString()}")
                .setVisibility(client.instantiate(ApplicationVisibility)
                    .setAutoSubmitToolbar(false)
                    .setHide(client.instantiate(ApplicationVisibilityHide)
                        .setIOS(false)
                        .setWeb(false)))
                .setSettings(client.instantiate(AutoLoginApplicationSettings)
                    .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
                        .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
                        .setLoginUrl("http://swaprimaryloginurl.okta.com")))

        Application app2 = client.instantiate(AutoLoginApplication)
                .setLabel("app-${UUID.randomUUID().toString()}")
                .setVisibility(client.instantiate(ApplicationVisibility)
                    .setAutoSubmitToolbar(false)
                    .setHide(client.instantiate(ApplicationVisibilityHide)
                        .setIOS(false)
                        .setWeb(false)))
                .setSettings(client.instantiate(AutoLoginApplicationSettings)
                    .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
                        .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
                        .setLoginUrl("http://swaprimaryloginurl.okta.com")))
        client.createApplication(app1)
        client.createApplication(app2)

        JsonWebKeyList app1Keys = app1.listApplicationKeys()
        assertThat(app1Keys.size(), equalTo(1))

        JsonWebKey webKey = app1.generateApplicationKey(5)
        assertThat(webKey, notNullValue())
        assertThat(app1.listApplicationKeys().size(), equalTo(2))

        JsonWebKey readWebKey = app1.getApplicationKey(webKey.getKid())
        assertThat(webKey, equalTo(readWebKey))

        JsonWebKey clonedWebKey = app1.cloneApplicationKey(webKey.getKid(), app2.getId())
        assertThat(clonedWebKey, notNullValue())

        JsonWebKeyList app2Keys = app2.listApplicationKeys()
        assertThat(app2Keys.size(), equalTo(2))
    }

    @Test
    void deactivateActivateTest() {
        Application app = client.createApplication(client.instantiate(AutoLoginApplication)
                .setLabel("app-${UUID.randomUUID().toString()}")
                .setVisibility(client.instantiate(ApplicationVisibility)
                    .setAutoSubmitToolbar(false)
                    .setHide(client.instantiate(ApplicationVisibilityHide)
                        .setIOS(false)
                        .setWeb(false)))
                .setSettings(client.instantiate(AutoLoginApplicationSettings)
                    .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
                        .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
                        .setLoginUrl("http://swaprimaryloginurl.okta.com"))))

        assertThat(app.status, equalTo(Application.StatusEnum.ACTIVE))

        app.deactivate()
        assertThat(client.getApplication(app.getId()).status, equalTo(Application.StatusEnum.INACTIVE))

        app.activate()
        assertThat(client.getApplication(app.getId()).status, equalTo(Application.StatusEnum.ACTIVE))
    }

//    @Test
//    void groupAssignmentWithNullBodyTest() {
//
//        Application app = client.createApplication(client.instantiate(AutoLoginApplication)
//                .setLabel("app-${UUID.randomUUID().toString()}")
//                .setVisibility(client.instantiate(ApplicationVisibility)
//                    .setAutoSubmitToolbar(false)
//                    .setHide(client.instantiate(ApplicationVisibilityHide)
//                        .setIOS(false)
//                        .setWeb(false)))
//                .setSettings(client.instantiate(AutoLoginApplicationSettings)
//                    .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
//                        .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
//                        .setLoginUrl("http://swaprimaryloginurl.okta.com"))))
//
//        Group group = GroupBuilder.instance()
//                .setName("app-test-group-" + UUID.randomUUID().toString())
//                .setDescription("IT created Group")
//                .buildAndCreate(client)
//
//        registerForCleanup(app)
//        registerForCleanup(group)
//
//        ApplicationGroupAssignment groupAssignment = app.createApplicationGroupAssignment(group.id, null)
//        assertThat(groupAssignment, notNullValue())
//    }

    @Test
    void groupAssignmentTest() {

        Application app = client.createApplication(client.instantiate(AutoLoginApplication)
                .setLabel("app-${UUID.randomUUID().toString()}")
                .setVisibility(client.instantiate(ApplicationVisibility)
                    .setAutoSubmitToolbar(false)
                    .setHide(client.instantiate(ApplicationVisibilityHide)
                        .setIOS(false)
                        .setWeb(false)))
                .setSettings(client.instantiate(AutoLoginApplicationSettings)
                    .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
                        .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
                        .setLoginUrl("http://swaprimaryloginurl.okta.com"))))

        Group group = GroupBuilder.instance()
                .setName("app-test-group-" + UUID.randomUUID().toString())
                .setDescription("IT created Group")
                .buildAndCreate(client)

        registerForCleanup(app)
        registerForCleanup(group)

        ApplicationGroupAssignmentList assignmentList = app.listApplicationGroupAssignments()
        println("FOOBAR")
        println(assignmentList)
        assertThat(app.listApplicationGroupAssignments().iterator().size(), equalTo(0))

        ApplicationGroupAssignment aga = client.instantiate(ApplicationGroupAssignment)
            .setPriority(2)

        ApplicationGroupAssignment groupAssignment = app.createApplicationGroupAssignment(group.id, aga)
        assertThat(groupAssignment, notNullValue())
        assertThat(groupAssignment.priority, equalTo(2))
        assertThat(app.listApplicationGroupAssignments().iterator().size(), equalTo(1))

        // delete the assignment
        groupAssignment.delete()
        assertThat(app.listApplicationGroupAssignments().iterator().size(), equalTo(0))
    }
}
