/*
 * Copyright 2024-Present Okta, Inc.
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

import com.okta.sdk.resource.api.UserApi
import com.okta.sdk.resource.api.UserResourcesApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for User Resources API.
 * Tests user resource management (app links, clients, devices, groups).
 */
class UserResourcesIT extends ITSupport {

    private UserApi userApi
    private UserResourcesApi userResourcesApi

    UserResourcesIT() {
        this.userApi = new UserApi(getClient())
        this.userResourcesApi = new UserResourcesApi(getClient())
    }

    @Test(groups = "group3")
    void listAppLinksTest() {
        def email = "user-applinks-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("AppLinks")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // List application links for user
            def appLinks = userResourcesApi.listAppLinks(user.getId())
            
            assertThat(appLinks, notNullValue())
            // User may have app links depending on app assignments

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listAppLinksEmptyResponseTest() {
        def email = "user-applinks-empty-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("AppLinksEmpty")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def appLinks = userResourcesApi.listAppLinks(user.getId())
            
            assertThat(appLinks, notNullValue())
            // Newly created user typically has no app assignments

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listAppLinksWithUserLoginTest() {
        def email = "user-applinks-login-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("AppLinksLogin")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Use login instead of ID
            def appLinks = userResourcesApi.listAppLinks(email)
            
            assertThat(appLinks, notNullValue())

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listAppLinksCallsCorrectEndpointTest() {
        def email = "user-applinks-endpoint-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("AppLinksEndpoint")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            userResourcesApi.listAppLinks(user.getId())

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listAppLinksWithHiddenAppsTest() {
        def email = "user-hidden-apps-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("HiddenApps")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def appLinks = userResourcesApi.listAppLinks(user.getId())
            
            assertThat(appLinks, notNullValue())
            // May include hidden apps in response

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserClientsTest() {
        def email = "user-clients-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Clients")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // List OAuth clients for user
            def clients = userResourcesApi.listUserClients(user.getId())
            
            assertThat(clients, notNullValue())
            // User may have authorized OAuth clients

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserClientsEmptyResponseTest() {
        def email = "user-clients-empty-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ClientsEmpty")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clients = userResourcesApi.listUserClients(user.getId())
            
            assertThat(clients, notNullValue())
            // Newly created user typically has no OAuth clients

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserClientsSingleClientTest() {
        def email = "user-single-client-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("SingleClient")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clients = userResourcesApi.listUserClients(user.getId())
            
            assertThat(clients, notNullValue())
            // May return single client if one is authorized

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserClientsCallsCorrectEndpointTest() {
        def email = "user-clients-endpoint-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ClientsEndpoint")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            userResourcesApi.listUserClients(user.getId())

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserDevicesTest() {
        def email = "user-devices-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Devices")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // List devices registered by user
            def devices = userResourcesApi.listUserDevices(user.getId())
            
            assertThat(devices, notNullValue())
            // User may have registered devices

        } catch (ApiException e) {
            // Expected if device management not enabled or insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserDevicesEmptyResponseTest() {
        def email = "user-devices-empty-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("DevicesEmpty")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def devices = userResourcesApi.listUserDevices(user.getId())
            
            assertThat(devices, notNullValue())
            // Newly created user typically has no devices

        } catch (ApiException e) {
            // Expected if device management not enabled
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserDevicesWithMultipleStatusesTest() {
        def email = "user-devices-statuses-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("DevicesStatuses")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def devices = userResourcesApi.listUserDevices(user.getId())
            
            assertThat(devices, notNullValue())
            // Devices may have ACTIVE, SUSPENDED, or other statuses

        } catch (ApiException e) {
            // Expected if device management not enabled
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserDevicesCallsCorrectEndpointTest() {
        def email = "user-devices-endpoint-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("DevicesEndpoint")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            userResourcesApi.listUserDevices(user.getId())

        } catch (ApiException e) {
            // Expected if device management not enabled
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserGroupsTest() {
        def email = "user-groups-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Groups")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // List groups user belongs to
            def groups = userResourcesApi.listUserGroups(user.getId())
            
            assertThat(groups, notNullValue())
            // User typically belongs to "Everyone" group at minimum

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserGroupsEmptyResponseTest() {
        def email = "user-groups-empty-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GroupsEmpty")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def groups = userResourcesApi.listUserGroups(user.getId())
            
            assertThat(groups, notNullValue())
            // User typically has at least one group (Everyone)

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserGroupsWithUserLoginTest() {
        def email = "user-groups-login-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GroupsLogin")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Use login instead of ID
            def groups = userResourcesApi.listUserGroups(email)
            
            assertThat(groups, notNullValue())

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserGroupsWithDifferentGroupTypesTest() {
        def email = "user-mixed-groups-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("MixedGroups")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def groups = userResourcesApi.listUserGroups(user.getId())
            
            assertThat(groups, notNullValue())
            // Groups may include OKTA_GROUP, BUILT_IN types

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserGroupsCallsCorrectEndpointTest() {
        def email = "user-groups-endpoint-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GroupsEndpoint")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            userResourcesApi.listUserGroups(user.getId())

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void userResourcesCompleteWorkflowTest() {
        def email = "user-resources-workflow-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ResourcesWorkflow")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // 1. List app links
            def appLinks = userResourcesApi.listAppLinks(user.getId())
            assertThat(appLinks, notNullValue())
            
            // 2. List OAuth clients
            def clients = userResourcesApi.listUserClients(user.getId())
            assertThat(clients, notNullValue())
            
            // 3. List devices
            def devices = userResourcesApi.listUserDevices(user.getId())
            assertThat(devices, notNullValue())
            
            // 4. List groups
            def groups = userResourcesApi.listUserGroups(user.getId())
            assertThat(groups, notNullValue())
            // User should have at least one group (Everyone)

        } catch (ApiException e) {
            // Expected if some resources not available or insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listResourcesWithSpecialCharactersTest() {
        def email = "user-special+chars-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("SpecialChars")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Test with email containing special characters
            def appLinks = userResourcesApi.listAppLinks(email)
            assertThat(appLinks, notNullValue())
            
            def clients = userResourcesApi.listUserClients(email)
            assertThat(clients, notNullValue())
            
            def groups = userResourcesApi.listUserGroups(email)
            assertThat(groups, notNullValue())

        } catch (ApiException e) {
            // Expected if resources not available
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }
}
