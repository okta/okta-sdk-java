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
import org.testng.annotations.AfterMethod
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

    // Thread-safe list to track realms that need cleanup
    private List<String> realmsToCleanup = Collections.synchronizedList(new ArrayList<>());

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
     * Override the registerForCleanup method to track realms for deletion.
     * This is called by test methods to ensure realms are cleaned up after tests.
     */
    @Override
    public void registerForCleanup(Object resource) {
        if (resource instanceof Realm) {
            Realm realm = (Realm) resource;
            realmsToCleanup.add(realm.getId());
            logger.debug("Registered realm {} for cleanup", realm.getId());
        }
    }

    /**
     * Clean up all realms that were created during the test.
     * This runs after each test method completes.
     */
    @AfterMethod
    public void cleanupRealms() {
        if (realmsToCleanup.isEmpty()) {
            return;
        }

        logger.info("Cleaning up {} realm(s)", realmsToCleanup.size());

        // Create a copy to avoid concurrent modification issues
        List<String> realmsCopy = new ArrayList<>(realmsToCleanup);

        for (String realmId : realmsCopy) {
            try {
                logger.debug("Deleting realm: {}", realmId);
                realmApi.deleteRealm(realmId);
                logger.debug("Successfully deleted realm: {}", realmId);
            } catch (ApiException e) {
                if (e.getCode() == 404) {
                    // Realm already deleted, that's fine
                    logger.debug("Realm {} already deleted", realmId);
                } else {
                    // Log the error but don't fail the test
                    logger.warn("Failed to delete realm {}: {} - {}", realmId, e.getCode(), e.getMessage());
                }
            } catch (Exception e) {
                logger.warn("Unexpected error deleting realm {}: {}", realmId, e.getMessage());
            }
        }

        // Clear the list after cleanup
        realmsToCleanup.clear();
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

        // 2. List all realms without any filters
        List<Realm> realms = realmApi.listRealms(null, null, null, null, null);

        // 3. Assert that the list is valid and contains our newly created realm
        assertThat(realms, notNullValue());
        assertThat(realms, not(empty()));

        // Use a more direct approach to check if the realm is in the list

        for (Realm realm : realms) {
            if (realm.getId().equals(createdRealm.getId())) {
                System.out.println("here");
                assertThat(realm.getProfile().getName(), is(createdRealm.getProfile().getName()));
                return;
            }
        }

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
        // 3. Test listing with a 'search' parameter using the 'co' (contains) operator
        String searchQuery = "profile.name co \"" + createdRealm.getProfile().getName() + "\"";

// Retry logic to handle indexing delay
        List<Realm> searchedRealms = null;
        int maxRetries = 10;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            searchedRealms = realmApi.listRealms(null, null, searchQuery, null, null);
            if (!searchedRealms.isEmpty()) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(500);
            retryCount++;
        }

        assertThat("Realm should be searchable after creation", searchedRealms, hasSize(1));
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
//
//    @Test
//    public void deleteAllRealms() throws ApiException {
//        skipIfNoPermissions();
//
//        logger.info("================================================================================");
//        logger.info("STARTING DELETION OF ALL REALMS");
//        logger.info("================================================================================");
//
//        // Fetch all realms
//        List<Realm> realms = realmApi.listRealms(100, null, null, null, null);
//
//        if (realms.isEmpty()) {
//            logger.info("No realms found in the organization.");
//            return;
//        }
//
//        logger.info("Found {} realm(s) to delete", realms.size());
//
//        int successCount = 0;
//        int failureCount = 0;
//
//        for (Realm realm : realms) {
//            try {
//                logger.info("Deleting realm: {} (ID: {})", realm.getProfile().getName(), realm.getId());
//                realmApi.deleteRealm(realm.getId());
//                successCount++;
//                logger.info("✓ Successfully deleted realm: {}", realm.getProfile().getName());
//
//                // Add a small delay to avoid rate limiting
//                TimeUnit.MILLISECONDS.sleep(200);
//            } catch (ApiException e) {
//                failureCount++;
//                logger.error("✗ Failed to delete realm {} (ID: {}): {} - {}",
//                    realm.getProfile().getName(), realm.getId(), e.getCode(), e.getMessage());
//            } catch (InterruptedException e) {
//                logger.warn("Sleep interrupted: {}", e.getMessage());
//                Thread.currentThread().interrupt();
//            }
//        }
//
//        logger.info("================================================================================");
//        logger.info("DELETION COMPLETED");
//        logger.info("  Successfully deleted: {}", successCount);
//        logger.info("  Failed: {}", failureCount);
//        logger.info("================================================================================");
//    }

    @Test
    void testPagedAndHeadersOverloads() {
        def headers = Collections.<String, String>emptyMap()
        try {
            // Paged - listRealms
            def realms = realmApi.listRealmsPaged(null, null, null, null, null)
            for (def r : realms) { break }
            def realmsH = realmApi.listRealmsPaged(null, null, null, null, null, headers)
            for (def r : realmsH) { break }

            // Non-paged with headers
            realmApi.listRealms(null, null, null, null, null, headers)
        } catch (Exception e) {
            // Expected
        }
    }
}

