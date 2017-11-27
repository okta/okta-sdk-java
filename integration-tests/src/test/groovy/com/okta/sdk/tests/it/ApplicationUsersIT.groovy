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
import com.okta.sdk.resource.application.AppUser
import com.okta.sdk.resource.application.AppUserCredentials
import com.okta.sdk.resource.application.AppUserList
import com.okta.sdk.resource.application.Application
import com.okta.sdk.resource.application.ApplicationVisibility
import com.okta.sdk.resource.application.ApplicationVisibilityHide
import com.okta.sdk.resource.application.AutoLoginApplication
import com.okta.sdk.resource.application.AutoLoginApplicationSettings
import com.okta.sdk.resource.application.AutoLoginApplicationSettingsSignOn
import com.okta.sdk.resource.user.PasswordCredential
import com.okta.sdk.resource.user.User
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

class ApplicationUsersIT extends ITSupport {


    @Test
    void associateUserWithApplication() {

        Client client = getClient()
        User user1 = randomUser()
        User user2 = randomUser()

        String label = "app-${UUID.randomUUID().toString()}"
        Application app = client.instantiate(AutoLoginApplication)
                .setLabel(label)
                .setVisibility(client.instantiate(ApplicationVisibility)
                    .setAutoSubmitToolbar(false)
                    .setHide(client.instantiate(ApplicationVisibilityHide)
                        .setIOS(false)
                        .setWeb(false)))
                .setSettings(client.instantiate(AutoLoginApplicationSettings)
                    .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
                        .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
                        .setLoginUrl("http://swaprimaryloginurl.okta.com")))
        client.createApplication(app)

        AppUserList appUserList = app.listApplicationUsers()
        assertThat appUserList.iterator().size(), equalTo(0)

        AppUser appUser1 = client.instantiate(AppUser)
            .setScope("USER")
            .setId(user1.id)
            .setCredentials(client.instantiate(AppUserCredentials)
                .setUserName(user1.getProfile().getEmail())
                .setPassword(client.instantiate(PasswordCredential)
                    .setValue("super-secret1")))
        app.assignUserToApplication(appUser1)

        AppUser appUser2 = client.instantiate(AppUser)
            .setScope("USER")
            .setId(user2.id)
            .setCredentials(client.instantiate(AppUserCredentials)
                .setUserName(user2.getProfile().getEmail())
                .setPassword(client.instantiate(PasswordCredential)
                    .setValue("super-secret2")))

        // FIXME: returned object should be the same
        appUser1 = app.assignUserToApplication(appUser1)
        appUser2 = app.assignUserToApplication(appUser2)

        // now we should have 2
        assertThat appUserList.iterator().size(), equalTo(2)

        // delete just one
        appUser1.delete()

        // now we should have 1
        assertThat appUserList.iterator().size(), equalTo(1)

        appUser2.getCredentials().setUserName("updated-"+user2.getProfile().getEmail())
        appUser2.update()

        AppUser readAppUser = app.getApplicationUser(appUser2.id)
        assertThat readAppUser.getCredentials().getUserName(), equalTo("updated-"+user2.getProfile().getEmail())

    }
}
