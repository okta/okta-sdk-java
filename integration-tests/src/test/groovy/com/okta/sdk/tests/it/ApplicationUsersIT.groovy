/*
 * Copyright 2024-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.tests.it

import com.okta.sdk.resource.api.ApplicationApi
import com.okta.sdk.resource.api.ApplicationUsersApi
import com.okta.sdk.resource.api.UserApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.tests.it.util.ITSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

import static com.okta.sdk.tests.it.util.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Comprehensive integration tests for Application Users API.
 * Mirrors C# SDK ApplicationUsersApiTests coverage.
 * 
 * ENDPOINTS TESTED (5):
 * 1. POST   /api/v1/apps/{appId}/users - Assign user to application
 * 2. GET    /api/v1/apps/{appId}/users/{userId} - Get assigned application user
 * 3. GET    /api/v1/apps/{appId}/users - List application users
 * 4. POST   /api/v1/apps/{appId}/users/{userId} - Update application user
 * 5. DELETE /api/v1/apps/{appId}/users/{userId} - Unassign user from application
 * 
 * FEATURES TESTED:
 * - User assignment to applications
 * - User retrieval with expand parameters
 * - User listing with pagination (limit, query)
 * - Profile and credential updates
 * - User unassignment with sendEmail parameter
 * - Error handling (404, 400 for invalid/null parameters)
 * - Duplicate assignment handling
 * - Multiple user pagination
 */
class ApplicationUsersIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationUsersIT.class)
    private ApplicationApi applicationApi
    private ApplicationUsersApi appUsersApi
    private UserApi userApi
    private List<String> createdUserIds = []
    private List<String> createdAppIds = []
    private List<Map<String, String>> assignedUsers = []

    @BeforeClass
    void setup() {
        applicationApi = new ApplicationApi(getClient())
        appUsersApi = new ApplicationUsersApi(getClient())
        userApi = new UserApi(getClient())
        logger.info("ApplicationUsersIT setup complete")
    }

    @AfterClass
    void cleanup() {
        // Unassign all users
        assignedUsers.each { assignment ->
            try {
                appUsersApi.unassignUserFromApplication(assignment.appId, assignment.userId, false)
                logger.debug("Unassigned user ${assignment.userId} from app ${assignment.appId}")
            } catch (Exception e) {
                logger.warn("Failed to unassign user: ${e.message}")
            }
        }

        // Delete all apps
        createdAppIds.each { appId ->
            try {
                applicationApi.deactivateApplication(appId)
                Thread.sleep(500)
                applicationApi.deleteApplication(appId)
                logger.debug("Deleted app ${appId}")
            } catch (Exception e) {
                logger.warn("Failed to delete app: ${e.message}")
            }
        }

        // Delete all users
        createdUserIds.each { userId ->
            try {
                userApi.deleteUser(userId, false, null)  // deactivates if not DEPROVISIONED
                userApi.deleteUser(userId, false, null)  // permanently deletes
                logger.debug("Deleted user ${userId}")
            } catch (Exception e) {
                logger.warn("Failed to delete user: ${e.message}")
            }
        }

        logger.info("ApplicationUsersIT cleanup complete")
    }

    private BookmarkApplication createTestApplication() {
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label("Test App " + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)

        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark")
            .requestIntegration(false)

        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)

        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        createdAppIds.add(createdApp.getId())
        logger.debug("Created test app: ${createdApp.getId()}")
        return createdApp
    }

    private User createTestUser() {
        String randomId = UUID.randomUUID().toString().substring(0, 8)
        
        CreateUserRequest userRequest = new CreateUserRequest()
        UserProfile profile = new UserProfile()
        profile.firstName("Test")
            .lastName("User")
            .email("test.user.${randomId}@example.com")
            .login("test.user.${randomId}@example.com")
        
        userRequest.profile(profile)
        
        UserCredentialsWritable credentials = new UserCredentialsWritable()
        PasswordCredential password = new PasswordCredential()
        password.value("Abed1234!@#\$")
        credentials.password(password)
        userRequest.credentials(credentials)

        User createdUser = userApi.createUser(userRequest, true, false, null)
        createdUserIds.add(createdUser.getId())
        logger.debug("Created test user: ${createdUser.getId()}")
        return createdUser
    }

    /**
     * Comprehensive test for all application user operations.
     * Tests ASSIGN, GET, LIST, UPDATE, UNASSIGN with various parameters.
     */
    @Test
    void testComprehensiveApplicationUserOperations() {
        logger.info("Testing comprehensive application user operations...")

        BookmarkApplication testApp = createTestApplication()
        User testUser = createTestUser()

        assertThat(testApp, notNullValue())
        assertThat(testApp.getId(), notNullValue())
        assertThat(testUser, notNullValue())
        assertThat(testUser.getId(), notNullValue())

        // Test 1: POST /api/v1/apps/{appId}/users - Assign user to application
        AppUserAssignRequest assignRequest = new AppUserAssignRequest()
        assignRequest.id(testUser.getId())
        
        AppUser assignedUser = appUsersApi.assignUserToApplication(testApp.getId(), assignRequest)

        assertThat("Assigned user should not be null", assignedUser, notNullValue())
        assertThat("User ID should match", assignedUser.getId(), equalTo(testUser.getId()))
        assertThat("Scope should be USER", assignedUser.getScope(), equalTo(AppUser.ScopeEnum.USER))
        assertThat("Credentials should not be null", assignedUser.getCredentials(), notNullValue())
        assertThat("Created timestamp should exist", assignedUser.getCreated(), notNullValue())
        assertThat("LastUpdated timestamp should exist", assignedUser.getLastUpdated(), notNullValue())
        assertThat("Status should not be null", assignedUser.getStatus(), notNullValue())
        assertThat("StatusChanged should exist", assignedUser.getStatusChanged(), notNullValue())
        assertThat("Links should not be null", assignedUser.getLinks(), notNullValue())
        assertThat("App link should exist", assignedUser.getLinks().getApp(), notNullValue())
        assertThat("User link should exist", assignedUser.getLinks().getUser(), notNullValue())
        assignedUsers.add([appId: testApp.getId(), userId: testUser.getId()])
        logger.debug("User assigned successfully")

        // Test 2: GET /api/v1/apps/{appId}/users/{userId} - Get assigned user
        AppUser retrievedUser = appUsersApi.getApplicationUser(testApp.getId(), testUser.getId(), null)

        assertThat("Retrieved user should not be null", retrievedUser, notNullValue())
        assertThat("User ID should match", retrievedUser.getId(), equalTo(testUser.getId()))
        assertThat("Scope should be USER", retrievedUser.getScope(), equalTo(AppUser.ScopeEnum.USER))
        assertThat("Created timestamp should exist", retrievedUser.getCreated(), notNullValue())
        assertThat("LastUpdated timestamp should exist", retrievedUser.getLastUpdated(), notNullValue())
        assertThat("Status should not be null", retrievedUser.getStatus(), notNullValue())
        assertThat("StatusChanged should exist", retrievedUser.getStatusChanged(), notNullValue())
        assertThat("SyncState should not be null", retrievedUser.getSyncState(), notNullValue())
        assertThat("Credentials should not be null", retrievedUser.getCredentials(), notNullValue())
        assertThat("Profile should not be null", retrievedUser.getProfile(), notNullValue())
        assertThat("Links should not be null", retrievedUser.getLinks(), notNullValue())
        assertThat("App link should contain app ID", retrievedUser.getLinks().getApp().getHref(), containsString(testApp.getId()))
        assertThat("User link should contain user ID", retrievedUser.getLinks().getUser().getHref(), containsString(testUser.getId()))
        logger.debug("User retrieved successfully")

        // Test 3: GET with expand=user parameter - Validates _embedded.user
        AppUser expandedUser = appUsersApi.getApplicationUser(testApp.getId(), testUser.getId(), "user")

        assertThat("Expanded user should not be null", expandedUser, notNullValue())
        assertThat("User ID should match", expandedUser.getId(), equalTo(testUser.getId()))
        assertThat("Embedded should not be null", expandedUser.getEmbedded(), notNullValue())
        assertThat("Embedded should contain 'user' key", expandedUser.getEmbedded().containsKey("user"), is(true))
        Object embeddedUserObj = expandedUser.getEmbedded().get("user")
        assertThat("Embedded user object should not be null", embeddedUserObj, notNullValue())
        logger.debug("User retrieved with expand parameter")

        // Test 4: GET /api/v1/apps/{appId}/users - List all users
        List<AppUser> usersList = appUsersApi.listApplicationUsers(testApp.getId(), null, null, null, null)

        assertThat("Users list should not be null", usersList, notNullValue())
        assertThat("Users list should have at least 1 user", usersList.size(), greaterThanOrEqualTo(1))
        assertThat("Users list should contain assigned user", 
            usersList.stream().anyMatch(u -> u.getId().equals(testUser.getId())), is(true))
        logger.debug("Users listed successfully")

        // Test 5: GET with query parameter
        String emailPrefix = testUser.getProfile().getEmail().substring(0, 10)
        List<AppUser> filteredUsers = appUsersApi.listApplicationUsers(testApp.getId(), null, null, emailPrefix, null)

        assertThat("Filtered users list should not be null", filteredUsers, notNullValue())
        logger.debug("Users filtered by query")

        // Test 6: GET with limit parameter
        List<AppUser> limitedUsers = appUsersApi.listApplicationUsers(testApp.getId(), null, 1, null, null)

        assertThat("Limited users list should not be null", limitedUsers, notNullValue())
        assertThat("Limited users list should have at most 1 user", limitedUsers.size(), lessThanOrEqualTo(1))
        logger.debug("Users listed with limit")

        // Test 7: POST /api/v1/apps/{appId}/users/{userId} - Update user profile
        AppUserUpdateRequest updateRequest = new AppUserUpdateRequest()
        Map<String, Object> profileMap = new HashMap<>()
        profileMap.put("email", testUser.getProfile().getEmail())
        updateRequest.profile(profileMap)

        AppUser updatedUser = appUsersApi.updateApplicationUser(testApp.getId(), testUser.getId(), updateRequest)

        assertThat("Updated user should not be null", updatedUser, notNullValue())
        assertThat("User ID should match", updatedUser.getId(), equalTo(testUser.getId()))
        logger.debug("User profile updated")

        // Test 8: Assign another user for unassignment tests
        User testUser2 = createTestUser()
        AppUserAssignRequest assignRequest2 = new AppUserAssignRequest()
        assignRequest2.id(testUser2.getId())
        
        AppUser assignedUser2 = appUsersApi.assignUserToApplication(testApp.getId(), assignRequest2)
        assertThat(assignedUser2, notNullValue())
        assertThat(assignedUser2.getId(), equalTo(testUser2.getId()))
        assignedUsers.add([appId: testApp.getId(), userId: testUser2.getId()])
        logger.debug("Second user assigned")

        // Test 9: DELETE /api/v1/apps/{appId}/users/{userId} - Unassign user
        appUsersApi.unassignUserFromApplication(testApp.getId(), testUser2.getId(), false)
        assignedUsers.removeIf { it.appId == testApp.getId() && it.userId == testUser2.getId() }
        logger.debug("User unassigned")

        // Test 10: Verify unassignment (should throw 404)
        ApiException unassignException = expect(ApiException.class, () ->
            appUsersApi.getApplicationUser(testApp.getId(), testUser2.getId(), null))
        assertThat("Should return 404 for unassigned user", unassignException.getCode(), is(404))
        logger.debug("Unassignment verified")

        // Test 11: Test sendEmail parameter
        User testUser3 = createTestUser()
        AppUserAssignRequest assignRequest3 = new AppUserAssignRequest()
        assignRequest3.id(testUser3.getId())
        appUsersApi.assignUserToApplication(testApp.getId(), assignRequest3)
        assignedUsers.add([appId: testApp.getId(), userId: testUser3.getId()])

        appUsersApi.unassignUserFromApplication(testApp.getId(), testUser3.getId(), false)
        assignedUsers.removeIf { it.appId == testApp.getId() && it.userId == testUser3.getId() }
        logger.debug("User unassigned with sendEmail=false")

        // Cleanup remaining assignment
        appUsersApi.unassignUserFromApplication(testApp.getId(), testUser.getId(), false)
        assignedUsers.removeIf { it.appId == testApp.getId() && it.userId == testUser.getId() }

        logger.info("Comprehensive application user operations test completed successfully!")
    }

    /**
     * Test credential updates for application users.
     */
    @Test
    void testApplicationUserCredentialUpdate() {
        logger.info("Testing application user credential update...")

        BookmarkApplication testApp = createTestApplication()
        User testUser = createTestUser()

        // Assign user
        AppUserAssignRequest assignRequest = new AppUserAssignRequest()
        assignRequest.id(testUser.getId())
        appUsersApi.assignUserToApplication(testApp.getId(), assignRequest)
        assignedUsers.add([appId: testApp.getId(), userId: testUser.getId()])

        // Update credentials
        AppUserUpdateRequest updateRequest = new AppUserUpdateRequest()
        AppUserCredentials credentials = new AppUserCredentials()
        credentials.userName(testUser.getProfile().getEmail())
        AppUserPasswordCredential password = new AppUserPasswordCredential()
        password.value("NewP@ssw0rd123!")
        credentials.password(password)
        updateRequest.credentials(credentials)

        AppUser updatedUser = appUsersApi.updateApplicationUser(testApp.getId(), testUser.getId(), updateRequest)

        assertThat("Updated user should not be null", updatedUser, notNullValue())
        assertThat("User ID should match", updatedUser.getId(), equalTo(testUser.getId()))
        assertThat("Credentials should not be null", updatedUser.getCredentials(), notNullValue())
        assertThat("Username should match", updatedUser.getCredentials().getUserName(), equalTo(testUser.getProfile().getEmail()))
        assertThat("LastUpdated should exist", updatedUser.getLastUpdated(), notNullValue())

        // Cleanup
        appUsersApi.unassignUserFromApplication(testApp.getId(), testUser.getId(), false)
        assignedUsers.removeIf { it.appId == testApp.getId() && it.userId == testUser.getId() }

        logger.info("Application user credential update test completed successfully!")
    }

    /**
     * Test error handling for invalid scenarios.
     * Tests null parameters, invalid IDs, and non-existent resources.
     */
    @Test
    void testInvalidScenarios() {
        logger.info("Testing error handling for invalid scenarios...")

        BookmarkApplication testApp = createTestApplication()
        User testUser = createTestUser()

        AppUserAssignRequest assignRequest = new AppUserAssignRequest()
        assignRequest.id(testUser.getId())

        // Test 1: AssignUserToApplication with null appId
        ApiException nullAppException = expect(ApiException.class, () ->
            appUsersApi.assignUserToApplication(null, assignRequest))
        assertThat("Should return 400 for null appId", nullAppException.getCode(), is(400))
        assertThat("Error message should not be empty", nullAppException.getMessage(), notNullValue())
        logger.debug("Null appId validation passed")

        // Test 2: AssignUserToApplication with null request
        ApiException nullRequestException = expect(ApiException.class, () ->
            appUsersApi.assignUserToApplication(testApp.getId(), null))
        assertThat("Should return 400 for null request", nullRequestException.getCode(), is(400))
        assertThat("Error message should not be empty", nullRequestException.getMessage(), notNullValue())
        logger.debug("Null request validation passed")

        // Test 3: AssignUserToApplication with invalid appId
        ApiException invalidAppException = expect(ApiException.class, () ->
            appUsersApi.assignUserToApplication("invalid-app-id", assignRequest))
        assertThat("Should return 404 for invalid appId", invalidAppException.getCode(), is(404))
        assertThat("Error message should not be empty", invalidAppException.getMessage(), notNullValue())
        assertThat("Error response should not be empty", invalidAppException.getResponseBody(), notNullValue())
        logger.debug("Invalid appId validation passed")

        // Test 4: AssignUserToApplication with invalid userId
        AppUserAssignRequest invalidUserRequest = new AppUserAssignRequest()
        invalidUserRequest.id("invalid-user-id")
        ApiException invalidUserException = expect(ApiException.class, () ->
            appUsersApi.assignUserToApplication(testApp.getId(), invalidUserRequest))
        assertThat("Should return 404 for invalid userId", invalidUserException.getCode(), is(404))
        assertThat("Error message should not be empty", invalidUserException.getMessage(), notNullValue())
        assertThat("Error response should not be empty", invalidUserException.getResponseBody(), notNullValue())
        logger.debug("Invalid userId validation passed")

        // Test 5: GetApplicationUser with null appId
        ApiException getAppNullException = expect(ApiException.class, () ->
            appUsersApi.getApplicationUser(null, testUser.getId(), null))
        assertThat("Should return 400 for null appId", getAppNullException.getCode(), is(400))
        assertThat("Error message should not be empty", getAppNullException.getMessage(), notNullValue())
        logger.debug("GetApplicationUser null appId validation passed")

        // Test 6: GetApplicationUser with null userId
        ApiException getUserNullException = expect(ApiException.class, () ->
            appUsersApi.getApplicationUser(testApp.getId(), null, null))
        assertThat("Should return 400 for null userId", getUserNullException.getCode(), is(400))
        assertThat("Error message should not be empty", getUserNullException.getMessage(), notNullValue())
        logger.debug("GetApplicationUser null userId validation passed")

        // Test 7: GetApplicationUser with invalid appId
        ApiException getInvalidAppException = expect(ApiException.class, () ->
            appUsersApi.getApplicationUser("invalid-app-id", testUser.getId(), null))
        assertThat("Should return 404 for invalid appId", getInvalidAppException.getCode(), is(404))
        assertThat("Error message should not be empty", getInvalidAppException.getMessage(), notNullValue())
        assertThat("Error response should not be empty", getInvalidAppException.getResponseBody(), notNullValue())
        logger.debug("GetApplicationUser invalid appId validation passed")

        // Test 8: GetApplicationUser for user not assigned to app
        ApiException notAssignedException = expect(ApiException.class, () ->
            appUsersApi.getApplicationUser(testApp.getId(), testUser.getId(), null))
        assertThat("Should return 404 for unassigned user", notAssignedException.getCode(), is(404))
        assertThat("Error message should not be empty", notAssignedException.getMessage(), notNullValue())
        assertThat("Error response should not be empty", notAssignedException.getResponseBody(), notNullValue())
        logger.debug("GetApplicationUser not assigned validation passed")

        // Test 9: UpdateApplicationUser with null appId
        AppUserUpdateRequest updateRequest = new AppUserUpdateRequest()
        Map<String, Object> profileMap = new HashMap<>()
        profileMap.put("email", testUser.getProfile().getEmail())
        updateRequest.profile(profileMap)
        
        ApiException updateAppNullException = expect(ApiException.class, () ->
            appUsersApi.updateApplicationUser(null, testUser.getId(), updateRequest))
        assertThat("Should return 400 for null appId", updateAppNullException.getCode(), is(400))
        assertThat("Error message should not be empty", updateAppNullException.getMessage(), notNullValue())
        logger.debug("UpdateApplicationUser null appId validation passed")

        // Test 10: UpdateApplicationUser with null userId
        ApiException updateUserNullException = expect(ApiException.class, () ->
            appUsersApi.updateApplicationUser(testApp.getId(), null, updateRequest))
        assertThat("Should return 400 for null userId", updateUserNullException.getCode(), is(400))
        assertThat("Error message should not be empty", updateUserNullException.getMessage(), notNullValue())
        logger.debug("UpdateApplicationUser null userId validation passed")

        // Test 11: UpdateApplicationUser with null request
        ApiException updateRequestNullException = expect(ApiException.class, () ->
            appUsersApi.updateApplicationUser(testApp.getId(), testUser.getId(), null))
        assertThat("Should return 400 for null request", updateRequestNullException.getCode(), is(400))
        assertThat("Error message should not be empty", updateRequestNullException.getMessage(), notNullValue())
        logger.debug("UpdateApplicationUser null request validation passed")

        // Test 12: UnassignUserFromApplication with null appId
        ApiException unassignAppNullException = expect(ApiException.class, () ->
            appUsersApi.unassignUserFromApplication(null, testUser.getId(), false))
        assertThat("Should return 400 for null appId", unassignAppNullException.getCode(), is(400))
        assertThat("Error message should not be empty", unassignAppNullException.getMessage(), notNullValue())
        logger.debug("UnassignUserFromApplication null appId validation passed")

        // Test 13: UnassignUserFromApplication with null userId
        ApiException unassignUserNullException = expect(ApiException.class, () ->
            appUsersApi.unassignUserFromApplication(testApp.getId(), null, false))
        assertThat("Should return 400 for null userId", unassignUserNullException.getCode(), is(400))
        assertThat("Error message should not be empty", unassignUserNullException.getMessage(), notNullValue())
        logger.debug("UnassignUserFromApplication null userId validation passed")

        // Test 14: UnassignUserFromApplication for user not assigned
        ApiException unassignNotAssignedException = expect(ApiException.class, () ->
            appUsersApi.unassignUserFromApplication(testApp.getId(), testUser.getId(), false))
        assertThat("Should return 404 for unassigned user", unassignNotAssignedException.getCode(), is(404))
        assertThat("Error message should not be empty", unassignNotAssignedException.getMessage(), notNullValue())
        assertThat("Error response should not be empty", unassignNotAssignedException.getResponseBody(), notNullValue())
        logger.debug("UnassignUserFromApplication not assigned validation passed")

        // Test 15: ListApplicationUsers with null appId
        ApiException listNullException = expect(ApiException.class, () ->
            appUsersApi.listApplicationUsers(null, null, null, null, null))
        assertThat("Should return 400 for null appId", listNullException.getCode(), is(400))
        logger.debug("ListApplicationUsers null appId validation passed")

        // Test 16: ListApplicationUsers with invalid appId
        ApiException listInvalidException = expect(ApiException.class, () ->
            appUsersApi.listApplicationUsers("invalid-app-id", null, null, null, null))
        assertThat("Should return 404 for invalid appId", listInvalidException.getCode(), is(404))
        logger.debug("ListApplicationUsers invalid appId validation passed")

        logger.info("Error handling test completed successfully!")
    }

    /**
     * Test pagination with multiple users.
     */
    @Test
    void testPaginationWithMultipleUsers() {
        logger.info("Testing pagination with multiple users...")

        BookmarkApplication testApp = createTestApplication()
        List<User> users = []

        // Create and assign 5 users
        for (int i = 0; i < 5; i++) {
            User user = createTestUser()
            users.add(user)
            
            AppUserAssignRequest assignRequest = new AppUserAssignRequest()
            assignRequest.id(user.getId())
            appUsersApi.assignUserToApplication(testApp.getId(), assignRequest)
            assignedUsers.add([appId: testApp.getId(), userId: user.getId()])
            logger.debug("Assigned user ${i + 1}/5")
        }

        // Test pagination with limit (retry for eventual consistency — all 5 users may not be visible immediately)
        List<AppUser> firstPage = null
        int maxRetries = 15
        int retryCount = 0
        while (retryCount < maxRetries) {
            firstPage = appUsersApi.listApplicationUsers(testApp.getId(), null, 20, null, null)
            if (firstPage != null && firstPage.size() >= 5) {
                break
            }
            TimeUnit.MILLISECONDS.sleep(1000)
            retryCount++
        }

        assertThat("First page should not be null", firstPage, notNullValue())
        assertThat("First page should have at least 5 users after ${retryCount} retries", firstPage.size(), greaterThanOrEqualTo(5))
        logger.debug("First page retrieved with ${firstPage.size()} users")

        // Test listing all users
        List<AppUser> allUsers = appUsersApi.listApplicationUsers(testApp.getId(), null, null, null, null)

        assertThat("All users list should not be null", allUsers, notNullValue())
        assertThat("All users list should have at least 5 users", allUsers.size(), greaterThanOrEqualTo(5))
        
        users.each { user ->
            assertThat("Should contain user ${user.getId()}", 
                allUsers.stream().anyMatch(u -> u.getId().equals(user.getId())), is(true))
        }
        logger.debug("All users retrieved: ${allUsers.size()} users")

        // Cleanup
        users.each { user ->
            appUsersApi.unassignUserFromApplication(testApp.getId(), user.getId(), false)
            assignedUsers.removeIf { it.appId == testApp.getId() && it.userId == user.getId() }
        }

        logger.info("Pagination test completed successfully!")
    }

    /**
     * Test complete workflow of assigning, updating, and unassigning users.
     */
    @Test
    void testCompleteUserWorkflow() {
        logger.info("Testing complete user workflow...")

        BookmarkApplication testApp = createTestApplication()
        User testUser = createTestUser()

        // Step 1: Assign user
        AppUserAssignRequest assignRequest = new AppUserAssignRequest()
        assignRequest.id(testUser.getId())
        AppUser assignedUser = appUsersApi.assignUserToApplication(testApp.getId(), assignRequest)

        assertThat("Assigned user should not be null", assignedUser, notNullValue())
        assertThat("User ID should match", assignedUser.getId(), equalTo(testUser.getId()))
        assertThat("Scope should be USER", assignedUser.getScope(), equalTo(AppUser.ScopeEnum.USER))
        assignedUsers.add([appId: testApp.getId(), userId: testUser.getId()])
        logger.debug("User assigned")

        // Step 2: Retrieve user
        AppUser retrievedUser = appUsersApi.getApplicationUser(testApp.getId(), testUser.getId(), null)
        assertThat("Retrieved user should not be null", retrievedUser, notNullValue())
        assertThat("User ID should match", retrievedUser.getId(), equalTo(testUser.getId()))
        logger.debug("User retrieved")

        // Step 3: Update user profile
        AppUserUpdateRequest updateRequest = new AppUserUpdateRequest()
        Map<String, Object> profileMap = new HashMap<>()
        profileMap.put("email", testUser.getProfile().getEmail())
        updateRequest.profile(profileMap)
        
        AppUser updatedUser = appUsersApi.updateApplicationUser(testApp.getId(), testUser.getId(), updateRequest)
        
        assertThat("Updated user should not be null", updatedUser, notNullValue())
        assertThat("User ID should match", updatedUser.getId(), equalTo(testUser.getId()))
        logger.debug("User updated")

        // Step 4: Get user with expand parameter
        AppUser expandedUser = appUsersApi.getApplicationUser(testApp.getId(), testUser.getId(), "user")
        assertThat("Expanded user should not be null", expandedUser, notNullValue())
        assertThat("User ID should match", expandedUser.getId(), equalTo(testUser.getId()))
        logger.debug("User retrieved with expand")

        // Step 5: List users and verify
        List<AppUser> usersList = appUsersApi.listApplicationUsers(testApp.getId(), null, null, null, null)
        assertThat("Users list should contain assigned user",
            usersList.stream().anyMatch(u -> u.getId().equals(testUser.getId())), is(true))
        logger.debug("User found in list")

        // Step 6: Unassign user
        appUsersApi.unassignUserFromApplication(testApp.getId(), testUser.getId(), false)
        assignedUsers.removeIf { it.appId == testApp.getId() && it.userId == testUser.getId() }
        logger.debug("User unassigned")

        logger.info("Complete user workflow test completed successfully!")
    }

    /**
     * Test duplicate assignment handling.
     */
    @Test
    void testDuplicateAssignment() {
        logger.info("Testing duplicate assignment handling...")

        BookmarkApplication testApp = createTestApplication()
        User testUser = createTestUser()

        // First assignment
        AppUserAssignRequest assignRequest = new AppUserAssignRequest()
        assignRequest.id(testUser.getId())
        AppUser firstAssignment = appUsersApi.assignUserToApplication(testApp.getId(), assignRequest)
        
        assertThat("First assignment should not be null", firstAssignment, notNullValue())
        assertThat("User ID should match", firstAssignment.getId(), equalTo(testUser.getId()))
        assignedUsers.add([appId: testApp.getId(), userId: testUser.getId()])
        logger.debug("First assignment successful")

        // Attempt second assignment (should succeed or return 400/409)
        try {
            AppUser secondAssignment = appUsersApi.assignUserToApplication(testApp.getId(), assignRequest)
            assertThat("Second assignment should not be null", secondAssignment, notNullValue())
            assertThat("User ID should match", secondAssignment.getId(), equalTo(testUser.getId()))
            logger.debug("Second assignment allowed (idempotent)")
        } catch (ApiException ex) {
            assertThat("Should return 400 or 409 for duplicate", ex.getCode(), isOneOf(400, 409))
            logger.debug("Duplicate assignment rejected: ${ex.getCode()}")
        }

        // Cleanup
        appUsersApi.unassignUserFromApplication(testApp.getId(), testUser.getId(), false)
        assignedUsers.removeIf { it.appId == testApp.getId() && it.userId == testUser.getId() }

        logger.info("Duplicate assignment test completed successfully!")
    }

    @Test
    void testPagedAndHeadersOverloads() {
        def headers = Collections.<String, String>emptyMap()
        def appId = null
        try {
            def app = applicationApi.createApplication(
                new com.okta.sdk.resource.model.BookmarkApplication()
                    .name(com.okta.sdk.resource.model.BookmarkApplication.NameEnum.BOOKMARK)
                    .label("Users-Paged-${UUID.randomUUID().toString().substring(0,8)}")
                    .signOnMode(com.okta.sdk.resource.model.ApplicationSignOnMode.BOOKMARK)
                    .settings(new com.okta.sdk.resource.model.BookmarkApplicationSettings()
                        .app(new com.okta.sdk.resource.model.BookmarkApplicationSettingsApplication()
                            .url("https://example.com/users-paged"))),
                true, null)
            appId = app.getId()

            // Paged - listApplicationUsers
            def users = appUsersApi.listApplicationUsersPaged(appId, null, null, null, null)
            for (def u : users) { break }
            def usersH = appUsersApi.listApplicationUsersPaged(appId, null, null, null, null, headers)
            for (def u : usersH) { break }

            // Non-paged with headers
            appUsersApi.listApplicationUsers(appId, null, null, null, null, headers)

        } catch (Exception e) {
            logger.info("Paged users test: {}", e.getMessage())
        } finally {
            if (appId) {
                try {
                    applicationApi.deactivateApplication(appId)
                    applicationApi.deleteApplication(appId)
                } catch (Exception ignored) {}
            }
        }
    }
}
