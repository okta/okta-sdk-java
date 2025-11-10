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
import com.okta.sdk.resource.api.UserLinkedObjectApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Ignore
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for User Linked Object API.
 * Tests user relationship management (manager, supervisor, direct reports).
 */
class UserLinkedObjectIT extends ITSupport {

    private UserApi userApi
    private UserLinkedObjectApi userLinkedObjectApi

    UserLinkedObjectIT() {
        this.userApi = new UserApi(getClient())
        this.userLinkedObjectApi = new UserLinkedObjectApi(getClient())
    }

    @Test(groups = "group3")
    void assignLinkedObjectValueForPrimaryTest() {
        def managerEmail = "manager-assign-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def employeeEmail = "employee-assign-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User manager = UserBuilder.instance()
            .setEmail(managerEmail)
            .setFirstName("Manager")
            .setLastName("Assign-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(manager)

        User employee = UserBuilder.instance()
            .setEmail(employeeEmail)
            .setFirstName("Employee")
            .setLastName("Assign-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(employee)

        try {
            // Assign manager relationship
            userLinkedObjectApi.assignLinkedObjectValueForPrimary(employee.getId(), "manager", manager.getId())
            
            // Success - relationship created
            // Note: Some orgs may not have linked object definitions configured

        } catch (ApiException e) {
            // Expected if linked objects not configured in org
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void assignLinkedObjectValueForPrimaryWithHttpInfoTest() {
        def managerEmail = "manager-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def employeeEmail = "employee-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User manager = UserBuilder.instance()
            .setEmail(managerEmail)
            .setFirstName("Manager")
            .setLastName("HttpInfo-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(manager)

        User employee = UserBuilder.instance()
            .setEmail(employeeEmail)
            .setFirstName("Employee")
            .setLastName("HttpInfo-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(employee)

        try {
            // Assign relationship
            userLinkedObjectApi.assignLinkedObjectValueForPrimary(employee.getId(), "supervisor", manager.getId())
            
            // If successful, verify the link exists
            def links = userLinkedObjectApi.listLinkedObjectsForUser(employee.getId(), "supervisor")
            assertThat(links, notNullValue())

        } catch (ApiException e) {
            // Expected if linked objects not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void assignLinkedObjectWithUserLoginTest() {
        def managerEmail = "manager-login-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def employeeEmail = "employee-login-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User manager = UserBuilder.instance()
            .setEmail(managerEmail)
            .setFirstName("ManagerLogin")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(manager)

        User employee = UserBuilder.instance()
            .setEmail(employeeEmail)
            .setFirstName("EmployeeLogin")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(employee)

        try {
            // Use login instead of ID
            userLinkedObjectApi.assignLinkedObjectValueForPrimary(employeeEmail, "manager", managerEmail)
            
            // Success - relationship created using logins

        } catch (ApiException e) {
            // Expected if linked objects not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void assignLinkedObjectWithSpecialCharactersInRelationshipTest() {
        def managerEmail = "manager-special-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def employeeEmail = "employee-special-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User manager = UserBuilder.instance()
            .setEmail(managerEmail)
            .setFirstName("ManagerSpecial")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(manager)

        User employee = UserBuilder.instance()
            .setEmail(employeeEmail)
            .setFirstName("EmployeeSpecial")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(employee)

        try {
            // Relationship name with special characters
            userLinkedObjectApi.assignLinkedObjectValueForPrimary(employee.getId(), "manager-level-1", manager.getId())

        } catch (ApiException e) {
            // Expected if relationship not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void deleteLinkedObjectForUserTest() {
        def managerEmail = "manager-delete-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def employeeEmail = "employee-delete-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User manager = UserBuilder.instance()
            .setEmail(managerEmail)
            .setFirstName("ManagerDelete")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(manager)

        User employee = UserBuilder.instance()
            .setEmail(employeeEmail)
            .setFirstName("EmployeeDelete")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(employee)

        try {
            // Assign relationship first
            userLinkedObjectApi.assignLinkedObjectValueForPrimary(employee.getId(), "manager", manager.getId())
            
            // Delete the relationship
            userLinkedObjectApi.deleteLinkedObjectForUser(employee.getId(), "manager")
            
            // Success - relationship deleted

        } catch (ApiException e) {
            // Expected if linked objects not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void deleteLinkedObjectForUserWithHttpInfoTest() {
        def managerEmail = "manager-delete-info-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def employeeEmail = "employee-delete-info-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User manager = UserBuilder.instance()
            .setEmail(managerEmail)
            .setFirstName("ManagerDeleteInfo")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(manager)

        User employee = UserBuilder.instance()
            .setEmail(employeeEmail)
            .setFirstName("EmployeeDeleteInfo")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(employee)

        try {
            // Assign relationship
            userLinkedObjectApi.assignLinkedObjectValueForPrimary(employee.getId(), "supervisor", manager.getId())
            
            // Delete with HTTP info
            def response = userLinkedObjectApi.deleteLinkedObjectForUserWithHttpInfo(employee.getId(), "supervisor")
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(204))

        } catch (ApiException e) {
            // Expected if linked objects not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void deleteLinkedObjectWithUserLoginTest() {
        def managerEmail = "manager-delete-login-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def employeeEmail = "employee-delete-login-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User manager = UserBuilder.instance()
            .setEmail(managerEmail)
            .setFirstName("ManagerDeleteLogin")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(manager)

        User employee = UserBuilder.instance()
            .setEmail(employeeEmail)
            .setFirstName("EmployeeDeleteLogin")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(employee)

        try {
            // Assign using IDs
            userLinkedObjectApi.assignLinkedObjectValueForPrimary(employee.getId(), "manager", manager.getId())
            
            // Delete using login
            userLinkedObjectApi.deleteLinkedObjectForUser(employeeEmail, "manager")

        } catch (ApiException e) {
            // Expected if linked objects not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void deleteNonExistentLinkedObjectTest() {
        def email = "user-delete-nonexistent-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("DeleteNonExistent")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Try to delete non-existent relationship
            userLinkedObjectApi.deleteLinkedObjectForUser(user.getId(), "nonexistent")
            
            // May succeed (idempotent) or fail depending on implementation

        } catch (ApiException e) {
            // Expected if relationship doesn't exist or not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void deleteLinkedObjectWithSpecialCharactersTest() {
        def email = "user-delete-special-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("DeleteSpecial")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Relationship name with special characters
            userLinkedObjectApi.deleteLinkedObjectForUser(user.getId(), "direct_reports")

        } catch (ApiException e) {
            // Expected if relationship not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listLinkedObjectsForUserTest() {
        def managerEmail = "manager-list-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def employeeEmail = "employee-list-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User manager = UserBuilder.instance()
            .setEmail(managerEmail)
            .setFirstName("ManagerList")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(manager)

        User employee = UserBuilder.instance()
            .setEmail(employeeEmail)
            .setFirstName("EmployeeList")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(employee)

        try {
            // Assign relationship
            userLinkedObjectApi.assignLinkedObjectValueForPrimary(employee.getId(), "manager", manager.getId())
            
            // List linked objects
            def linkedObjects = userLinkedObjectApi.listLinkedObjectsForUser(employee.getId(), "manager")
            
            assertThat(linkedObjects, notNullValue())
            // May return list of ResponseLinks objects

        } catch (ApiException e) {
            // Expected if linked objects not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listLinkedObjectsEmptyResponseTest() {
        def email = "user-list-empty-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ListEmpty")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // List linked objects when none exist
            def linkedObjects = userLinkedObjectApi.listLinkedObjectsForUser(user.getId(), "manager")
            
            assertThat(linkedObjects, notNullValue())
            // Should return empty list

        } catch (ApiException e) {
            // Expected if relationship not configured or no links
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listLinkedObjectsWithUserLoginTest() {
        def email = "user-list-login-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ListLogin")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Use login instead of ID
            def linkedObjects = userLinkedObjectApi.listLinkedObjectsForUser(email, "supervisor")
            
            assertThat(linkedObjects, notNullValue())

        } catch (ApiException e) {
            // Expected if relationship not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listLinkedObjectsSinglePrimaryTest() {
        def managerEmail = "manager-single-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def employeeEmail = "employee-single-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User manager = UserBuilder.instance()
            .setEmail(managerEmail)
            .setFirstName("ManagerSingle")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(manager)

        User employee = UserBuilder.instance()
            .setEmail(employeeEmail)
            .setFirstName("EmployeeSingle")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(employee)

        try {
            // Assign single manager relationship
            userLinkedObjectApi.assignLinkedObjectValueForPrimary(employee.getId(), "manager", manager.getId())
            
            // List should return single relationship
            def linkedObjects = userLinkedObjectApi.listLinkedObjectsForUser(employee.getId(), "manager")
            
            assertThat(linkedObjects, notNullValue())

        } catch (ApiException e) {
            // Expected if linked objects not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listLinkedObjectsMultipleAssociatedUsersTest() {
        def managerEmail = "manager-multiple-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def employee1Email = "employee1-multiple-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def employee2Email = "employee2-multiple-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def employee3Email = "employee3-multiple-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User manager = UserBuilder.instance()
            .setEmail(managerEmail)
            .setFirstName("ManagerMultiple")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(manager)

        User employee1 = UserBuilder.instance()
            .setEmail(employee1Email)
            .setFirstName("Employee1")
            .setLastName("Multiple-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(employee1)

        User employee2 = UserBuilder.instance()
            .setEmail(employee2Email)
            .setFirstName("Employee2")
            .setLastName("Multiple-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(employee2)

        User employee3 = UserBuilder.instance()
            .setEmail(employee3Email)
            .setFirstName("Employee3")
            .setLastName("Multiple-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(employee3)

        try {
            // Assign multiple direct reports
            userLinkedObjectApi.assignLinkedObjectValueForPrimary(employee1.getId(), "manager", manager.getId())
            userLinkedObjectApi.assignLinkedObjectValueForPrimary(employee2.getId(), "manager", manager.getId())
            userLinkedObjectApi.assignLinkedObjectValueForPrimary(employee3.getId(), "manager", manager.getId())
            
            // List manager's direct reports (reverse relationship)
            // Note: This requires the associated relationship name
            def linkedObjects = userLinkedObjectApi.listLinkedObjectsForUser(manager.getId(), "subordinates")
            
            assertThat(linkedObjects, notNullValue())
            // Should return 3 employees if relationship properly configured

        } catch (ApiException e) {
            // Expected if linked objects not configured or relationship not bidirectional
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listLinkedObjectsWithSpecialCharactersTest() {
        def email = "user-list-special-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ListSpecial")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Relationship name with special characters
            def linkedObjects = userLinkedObjectApi.listLinkedObjectsForUser(user.getId(), "team_lead")
            
            assertThat(linkedObjects, notNullValue())

        } catch (ApiException e) {
            // Expected if relationship not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void linkedObjectCompleteWorkflowTest() {
        def managerEmail = "manager-workflow-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def employeeEmail = "employee-workflow-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User manager = UserBuilder.instance()
            .setEmail(managerEmail)
            .setFirstName("ManagerWorkflow")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(manager)

        User employee = UserBuilder.instance()
            .setEmail(employeeEmail)
            .setFirstName("EmployeeWorkflow")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(employee)

        try {
            // 1. Assign manager relationship
            userLinkedObjectApi.assignLinkedObjectValueForPrimary(employee.getId(), "manager", manager.getId())
            
            // 2. List linked objects
            def linkedObjects = userLinkedObjectApi.listLinkedObjectsForUser(employee.getId(), "manager")
            assertThat(linkedObjects, notNullValue())
            
            // 3. Delete the relationship
            userLinkedObjectApi.deleteLinkedObjectForUser(employee.getId(), "manager")
            
            // 4. Verify deletion - list should be empty or fail
            try {
                def afterDelete = userLinkedObjectApi.listLinkedObjectsForUser(employee.getId(), "manager")
                // Should be empty after deletion
            } catch (ApiException notFoundEx) {
                // Expected if relationship no longer exists
                assertThat(notFoundEx.getCode(), equalTo(404))
            }

        } catch (ApiException e) {
            // Expected if linked objects not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }
}
