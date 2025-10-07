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

import com.okta.sdk.resource.api.RealmApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.CreateRealmRequest
import com.okta.sdk.resource.model.Realm
import com.okta.sdk.resource.model.RealmProfile
import com.okta.sdk.resource.model.UpdateRealmRequest
import com.okta.sdk.tests.it.util.ITSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.SkipException
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test


import static com.okta.sdk.tests.it.util.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import java.util.concurrent.TimeUnit

/**
 * Integration tests for {@code /api/v1/realms}.
 *
 * <p>NOTE: These tests require the API token to have REALMS:MANAGE permission
 * in your Okta organization. If your API token doesn't have the required
 * permissions, the tests will be skipped.</p>
 *
 * <p>To run these tests successfully:</p>
 * <ol>
 *   <li>Ensure your API token has the REALMS:MANAGE permission</li>
 *   <li>In the Okta Admin Console, go to Security > API > Tokens</li>
 *   <li>Create or edit your token and ensure it has the necessary permissions</li>
 * </ol>
 */
public class RealmsIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(RealmsIT.class);
    private final RealmApi realmApi = new RealmApi(getClient());
    private boolean hasRealmPermissions = false;

    /**
     * Before running tests, check if the API token has the required permissions
     * to manage Realms. If not, skip the tests.
     */
    @BeforeClass
    public void checkPermissions() {
        try {
            // Try to list realms to check for permissions
            realmApi.listRealms(1, null, null, null, null);
            hasRealmPermissions = true;
            logger.info("API token has permissions to manage Realms. Tests will run.");
        } catch (ApiException e) {
            if (e.getCode() == 401 || e.getCode() == 403) {
                logger.warn("API token does not have permissions to manage Realms. " +
                    "Tests will be skipped. Error: {}", e.getMessage());
                hasRealmPermissions = false;
            } else {
                // Some other error occurred, but we might still have permissions
                logger.warn("An error occurred while checking Realm permissions: {}", e.getMessage());
                hasRealmPermissions = true;
            }
        }
    }

    /**
     * Helper method to generate unique realm data for testing.
     * @param suffix A string to append to the realm name for easy identification.
     * @return A {@link CreateRealmRequest} object with a unique name.
     */
    private CreateRealmRequest createTestRealmData(String suffix) {
        // Generate a unique identifier to ensure realm name is unique
        String uniqueId = UUID.randomUUID().toString().substring(0, 12);
        String realmName = "IT-" + suffix + "-" + uniqueId;

        RealmProfile profile = new RealmProfile();
        profile.setName(realmName);

        CreateRealmRequest request = new CreateRealmRequest();
        request.setProfile(profile);
        return request;
    }

    /**
     * Checks if the current API token has Realm management permissions.
     * If not, skips the test with an informative message.
     */
    private void skipIfNoPermissions() {
        if (!hasRealmPermissions) {
            throw new SkipException(
                "Test skipped. API token does not have REALMS:MANAGE permission. " +
                    "Please update your API token permissions in the Okta Admin Console."
            );
        }
    }

    @Test
    public void testCreateAndGetRealm() throws ApiException {
        skipIfNoPermissions();

        // 1. Create a new realm
        CreateRealmRequest request = createTestRealmData("create-get");
        Realm createdRealm = realmApi.createRealm(request);
        registerForCleanup(createdRealm); // Ensures the realm is deleted after the test run

        // 2. Assert that the realm was created successfully
        assertThat(createdRealm, notNullValue());
        assertThat(createdRealm.getId(), notNullValue());
        assertThat(createdRealm.getProfile().getName(), is(request.getProfile().getName()));

        // 3. Retrieve the realm by its ID
        Realm retrievedRealm = realmApi.getRealm(createdRealm.getId());

        // 4. Assert that the retrieved realm matches the created one
        assertThat(retrievedRealm, notNullValue());
        assertThat(retrievedRealm.getId(), is(createdRealm.getId()));
        assertThat(retrievedRealm.getProfile().getName(), is(createdRealm.getProfile().getName()));
    }

    @Test
    public void testUpdateRealm() throws ApiException {
        skipIfNoPermissions();

        // 1. Create a realm to update
        CreateRealmRequest createRequest = createTestRealmData("update");
        Realm createdRealm = realmApi.createRealm(createRequest);
        registerForCleanup(createdRealm);

        // 2. Prepare an update request with a new name
        String updatedName = createdRealm.getProfile().getName() + "-Updated";
        RealmProfile updatedProfile = new RealmProfile();
        updatedProfile.setName(updatedName);
        UpdateRealmRequest updateRequest = new UpdateRealmRequest();
        updateRequest.setProfile(updatedProfile);

        // 3. Perform the update (replace) operation
        Realm updatedRealm = realmApi.replaceRealm(createdRealm.getId(), updateRequest);

        // 4. Assert that the returned realm reflects the update
        assertThat(updatedRealm, notNullValue());
        assertThat(updatedRealm.getId(), is(createdRealm.getId()));
        assertThat(updatedRealm.getProfile().getName(), is(updatedName));

        // 5. Verify the update by fetching the realm again
        Realm retrievedRealm = realmApi.getRealm(createdRealm.getId());
        assertThat(retrievedRealm.getProfile().getName(), is(updatedName));
    }

    @Test
    public void testDeleteRealm() throws ApiException {
        skipIfNoPermissions();

        // 1. Create a realm specifically for this deletion test
        CreateRealmRequest request = createTestRealmData("delete");
        Realm realmToDelete = realmApi.createRealm(request);
        // NOTE: We do NOT register for cleanup because we are deleting it manually in the test.

        // 2. Delete the realm
        realmApi.deleteRealm(realmToDelete.getId());

        // 3. Verify deletion by attempting to get the realm, which should fail with a 404 Not Found error
        expect(ApiException.class, () -> realmApi.getRealm(realmToDelete.getId()));
    }

    @Test
    public void testListRealms() throws ApiException {
        skipIfNoPermissions();

        // 1. Create a test realm to ensure the list is not empty
        CreateRealmRequest request = createTestRealmData("list");
        Realm createdRealm = realmApi.createRealm(request);
        registerForCleanup(createdRealm);

        // Wait a moment to allow the realm to be indexed and available in list operations
        logger.info("Waiting for realm to be available in list operations...");
        TimeUnit.SECONDS.sleep(3);

        // 2. List all realms without any filters
        List<Realm> realms = realmApi.listRealms(null, null, null, null, null);

        // 3. Assert that the list is valid and contains our newly created realm
        assertThat(realms, notNullValue());
        assertThat(realms, not(empty()));

        // Use a more direct approach to check if the realm is in the list
        boolean found = false;
        for (Realm realm : realms) {
            if (realm.getId().equals(createdRealm.getId())) {
                found = true;
                break;
            }
        }
        assertThat("Newly created realm should be present in the list", found, is(true));
    }

    @Test
    public void testListRealmsWithParameters() throws ApiException {
        skipIfNoPermissions();

        // 1. Create a realm with a unique name to test search functionality
        CreateRealmRequest request = createTestRealmData("list-params");
        Realm createdRealm = realmApi.createRealm(request);
        registerForCleanup(createdRealm);

        // 2. Test listing with a 'limit' parameter
        List<Realm> limitedRealms = realmApi.listRealms(1, null, null, null, null);
        assertThat(limitedRealms, hasSize(lessThanOrEqualTo(1)));

        // 3. Test listing with a 'search' parameter using the 'co' (contains) operator
        String searchQuery = "profile.name co \"" + createdRealm.getProfile().getName() + "\"";
        List<Realm> searchedRealms = realmApi.listRealms(null, null, searchQuery, null, null);
        assertThat(searchedRealms, hasSize(1));
        assertThat(searchedRealms.get(0).getId(), is(createdRealm.getId()));
    }

    @Test
    public void testGetRealmNotFound() {
        skipIfNoPermissions();

        // Attempt to get a realm with a randomly generated, non-existent ID
        String nonExistentRealmId = "00r-non-existent-" + UUID.randomUUID();
        expect(ApiException.class, () -> realmApi.getRealm(nonExistentRealmId));
    }

    @Test
    public void testCreateRealmWithInvalidData() {
        skipIfNoPermissions();

        // Attempt to create a realm with a null profile, which is a required field
        CreateRealmRequest invalidRequest = new CreateRealmRequest();
        invalidRequest.setProfile(null); // This makes the request invalid

        // Expect a 400 Bad Request error
        expect(ApiException.class, () -> realmApi.createRealm(invalidRequest));
    }

    @Test
    void testComprehensiveRealmWorkflow() throws ApiException {
        skipIfNoPermissions()

        // 1. Create multiple realms to simulate a more complex scenario
        Realm realm1 = realmApi.createRealm(createTestRealmData("workflow-1"))

        Realm realm2 = realmApi.createRealm(createTestRealmData("workflow-2"))

        // 2. Assert successful creation
        assertThat(realm1.getId(), notNullValue())
        assertThat(realm2.getId(), notNullValue())

        // Wait for realms to be available in list operations
        logger.info("Waiting for realms to be available in list operations...")
        TimeUnit.SECONDS.sleep(10)

        // 3. List all realms and verify that the new realms are present
        List<Realm> allRealms = realmApi.listRealms(null, null, null, null, null)

        // Find the realms in the list directly instead of using the hasItems matcher
        boolean foundRealm1 = allRealms.stream().anyMatch(realm -> realm.getId().equals(realm1.getId()))
        boolean foundRealm2 = allRealms.stream().anyMatch(realm -> realm.getId().equals(realm2.getId()))

        // Assert that both realms are found
        assertThat("First realm should be present in the list", foundRealm1, is(true))
        assertThat("Second realm should be present in the list", foundRealm2, is(true))

        // 4. Update one of the realms
        String updatedName = realm1.getProfile().getName() + "-Workflow-Updated"
        UpdateRealmRequest updateRequest = new UpdateRealmRequest()
        updateRequest.setProfile(new RealmProfile().name(updatedName))
        realmApi.replaceRealm(realm1.getId(), updateRequest)

        // 5. Verify the update was successful
        Realm retrievedRealm = realmApi.getRealm(realm1.getId())
        assertThat(retrievedRealm.getProfile().getName(), is(updatedName))
        registerForCleanup(realm1)
        registerForCleanup(realm2)
        // Deletion of both realms is handled automatically by the 'registerForCleanup' mechanism.
    }
}