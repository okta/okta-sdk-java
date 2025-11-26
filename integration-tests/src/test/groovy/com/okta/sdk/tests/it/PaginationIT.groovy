/*
 * Copyright 2025-Present Okta, Inc.
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
import com.okta.sdk.resource.api.GroupApi
import com.okta.sdk.resource.api.ApplicationApi
import com.okta.sdk.resource.model.*
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for the new PagedIterable pagination API
 * Tests pagination across multiple API endpoints: Users, Groups, Applications, etc.
 */
class PaginationIT extends ITSupport {

    @Test(groups = "group3")
    void testPagedIterableWithUsers() {
        println "\n=== Testing Users Pagination ==="
        UserApi userApi = new UserApi(getClient())
        
        // Create multiple test users to ensure pagination
        def usersToCreate = 5
        def createdUsers = []
        
        try {
            println "Creating ${usersToCreate} test users..."
            for (int i = 0; i < usersToCreate; i++) {
                def email = "pag${i}-${uniqueTestName.take(30)}@ex.com"
                User user = createUser(userApi, email, "PagTest${i}", "User${i}")
                createdUsers.add(user)
                registerForCleanup(user)
            }
            
            // Allow time for users to be indexed
            Thread.sleep(getTestOperationDelay())
            
            // Test: Iterate using the new PagedIterable API with small page size
            println "Fetching users with PagedIterable (limit=2 per page)..."
            def collectedUsers = []
            def pageCount = 0
            
            // Use listUsersPaged with limit=2 to force pagination
            // Signature: listUsersPaged(String contentType, String q, String after, Integer limit, String filter, String search, String sortBy, String sortOrder)
            for (User user : userApi.listUsersPaged(null, null, null, 2, null, null, null, null)) {
                collectedUsers.add(user)
                if (collectedUsers.size() % 2 == 0) {
                    pageCount++
                    println "  Fetched page ${pageCount} (${collectedUsers.size()} total users so far)"
                }
                // Stop after collecting enough users to find all ours (collect more to be safe)
                if (collectedUsers.size() >= usersToCreate * 2) {
                    break
                }
            }
            
            println "✓ Collected ${collectedUsers.size()} users across ${pageCount} pages"
            
            assertThat("Should have collected enough users", 
                       collectedUsers.size(), greaterThanOrEqualTo(usersToCreate))
            assertThat("Should have fetched multiple pages", pageCount, greaterThan(1))
            
            // Verify we can find at least some of our created users (not all may be in the first pages)
            def foundEmails = collectedUsers.collect { it.profile.email }
            def ourEmails = createdUsers.collect { it.profile.email }
            def foundOurUsers = ourEmails.findAll { email -> foundEmails.contains(email) }
            
            println "  Found ${foundOurUsers.size()} of our ${usersToCreate} created users"
            assertThat("Should find most of our created users", 
                      foundOurUsers.size(), greaterThanOrEqualTo(3)) // At least 3 out of 5
            
        } finally {
            // Cleanup is handled by registerForCleanup
        }
    }

    @Test(groups = "group3")
    void testPagedIterableWithGroups() {
        println "\n=== Testing Groups Pagination ==="
        GroupApi groupApi = new GroupApi(getClient())
        
        def groupsToCreate = 5
        def createdGroups = []
        
        try {
            println "Creating ${groupsToCreate} test groups..."
            for (int i = 0; i < groupsToCreate; i++) {
                AddGroupRequest request = new AddGroupRequest()
                OktaUserGroupProfile profile = new OktaUserGroupProfile()
                profile.name = "PagTest${i}-${uniqueTestName}".take(100) // Ensure name isn't too long
                profile.description = "Test group ${i}"
                request.profile = profile
                
                Group created = groupApi.addGroup(request)
                createdGroups.add(created)
                registerForCleanup(created)
            }
            
            Thread.sleep(getTestOperationDelay())
            
            println "Fetching groups with PagedIterable (limit=2 per page)..."
            def collectedGroups = []
            def pageCount = 0
            
            // Use listGroupsPaged with limit=2 to force pagination
            // Signature: listGroupsPaged(String q, String filter, String after, Integer limit, String expand, String search, String sortBy, String sortOrder)
            for (Group group : groupApi.listGroupsPaged(null, null, null, 2, null, null, null, null)) {
                collectedGroups.add(group)
                if (collectedGroups.size() % 2 == 0) {
                    pageCount++
                    println "  Fetched page ${pageCount} (${collectedGroups.size()} total groups so far)"
                }
                // Collect more groups to ensure we find all ours
                if (collectedGroups.size() >= groupsToCreate * 2) {
                    break
                }
            }
            
            println "✓ Collected ${collectedGroups.size()} groups across ${pageCount} pages"
            
            assertThat("Should have collected enough groups", 
                       collectedGroups.size(), greaterThanOrEqualTo(groupsToCreate))
            assertThat("Should have fetched multiple pages", pageCount, greaterThan(1))
            
            // Verify we can find at least some of our created groups
            def foundNames = collectedGroups.collect { it.profile.name }
            def ourNames = createdGroups.collect { it.profile.name }
            def foundOurGroups = ourNames.findAll { name -> foundNames.contains(name) }
            
            println "  Found ${foundOurGroups.size()} of our ${groupsToCreate} created groups"
            assertThat("Should find most of our created groups", 
                      foundOurGroups.size(), greaterThanOrEqualTo(3)) // At least 3 out of 5
            
        } finally {
            // Cleanup is handled by registerForCleanup
        }
    }

    @Test(groups = "group3")
    void testPagedIterableWithApplications() {
        println "\n=== Testing Applications Pagination ==="
        ApplicationApi appApi = new ApplicationApi(getClient())
        
        println "Fetching applications with PagedIterable (limit=5 per page)..."
        def collectedApps = []
        def pageCount = 0
        
        // Use listApplicationsPaged
        // Signature: listApplicationsPaged(String q, String after, Boolean useOptimization, Integer limit, String filter, String expand, Boolean includeNonDeleted)
        for (Application app : appApi.listApplicationsPaged(null, null, null, 5, null, null, null)) {
            collectedApps.add(app)
            if (collectedApps.size() % 5 == 0) {
                pageCount++
                println "  Fetched page ${pageCount} (${collectedApps.size()} total apps so far)"
            }
            // Limit to avoid processing too many apps
            if (collectedApps.size() >= 10) {
                break
            }
        }
        
        println "✓ Collected ${collectedApps.size()} applications across multiple pages"
        
        assertThat("Should have collected some applications", 
                   collectedApps.size(), greaterThan(0))
    }

    @Test(groups = "group3")
    void testPagedIterableWithGroupMembers() {
        println "\n=== Testing Group Members Pagination ==="
        UserApi userApi = new UserApi(getClient())
        GroupApi groupApi = new GroupApi(getClient())
        
        // Create a group
        AddGroupRequest request = new AddGroupRequest()
        OktaUserGroupProfile profile = new OktaUserGroupProfile()
        profile.name = "PagMemb-${uniqueTestName}".take(100)
        profile.description = "Test group for member pagination"
        request.profile = profile
        
        Group createdGroup = groupApi.addGroup(request)
        registerForCleanup(createdGroup)
        
        // Create and add users to the group
        def usersToAdd = 5
        def createdUsers = []
        
        try {
            println "Creating ${usersToAdd} users and adding to group..."
            for (int i = 0; i < usersToAdd; i++) {
                def email = "pagmem${i}-${uniqueTestName.take(30)}@ex.com"
                User user = createUser(userApi, email, "MemberTest${i}", "User${i}")
                createdUsers.add(user)
                registerForCleanup(user)
                
                // Add user to group
                groupApi.assignUserToGroup(createdGroup.id, user.id)
            }
            
            Thread.sleep(getTestOperationDelay())
            
            println "Fetching group members with PagedIterable (limit=2 per page)..."
            def collectedMembers = []
            def pageCount = 1  // Start at 1 since we'll fetch at least one page
            def previousSize = 0
            
            // Use listGroupUsersPaged with limit=2
            for (User member : groupApi.listGroupUsersPaged(createdGroup.id, null, 2)) {
                collectedMembers.add(member)
                // Increment page count when we've fetched a new batch (size increases by more than 0 after hitting limit boundary)
                if (previousSize > 0 && previousSize % 2 == 0 && collectedMembers.size() > previousSize) {
                    pageCount++
                    println "  Fetched page ${pageCount} (${collectedMembers.size()} total members so far)"
                } else if (collectedMembers.size() % 2 == 0) {
                    println "  Fetched page ${pageCount} (${collectedMembers.size()} total members so far)"
                }
                previousSize = collectedMembers.size()
            }
            
            println "✓ Collected ${collectedMembers.size()} members across ${pageCount} pages"
            
            assertThat("Should have collected at least 3 members", 
                       collectedMembers.size(), greaterThanOrEqualTo(3))
            assertThat("Should have fetched multiple pages", pageCount, greaterThan(1))
            
        } finally {
            // Cleanup is handled by registerForCleanup
        }
    }

    @Test(groups = "group3")
    void testPagedIterableThreadSafety() {
        println "\n=== Testing Thread-Safety ==="
        UserApi userApi = new UserApi(getClient())
        
        println "Starting 3 concurrent threads to iterate users..."
        // Test that multiple threads can safely iterate
        def threads = []
        def errors = Collections.synchronizedList([])
        def results = Collections.synchronizedList([])
        
        for (int i = 0; i < 3; i++) {
            def threadNum = i + 1
            def thread = Thread.start {
                try {
                    def count = 0
                    for (User user : userApi.listUsersPaged(null, null, null, 10, null, null, null, null)) {
                        count++
                        if (count >= 10) break // Limit to avoid long test
                    }
                    results.add(count)
                    println "  Thread ${threadNum} collected ${count} users"
                } catch (Exception e) {
                    errors.add(e)
                    println "  Thread ${threadNum} encountered error: ${e.message}"
                }
            }
            threads.add(thread)
        }
        
        // Wait for all threads
        threads.each { it.join(30000) } // 30 second timeout
        
        println "✓ All threads completed successfully"
        
        assertThat("No errors should occur in multi-threaded access", 
                   errors, empty())
        assertThat("All threads should collect users", 
                   results.size(), equalTo(3))
        results.each {
            assertThat("Each thread should collect users", it, greaterThan(0))
        }
    }

    @Test(groups = "group3")
    void testPagedIterableEarlyBreak() {
        println "\n=== Testing Early Break ==="
        UserApi userApi = new UserApi(getClient())
        
        println "Fetching users but breaking early after 5 items..."
        // Test that we can break early without fetching all pages
        def collectedCount = 0
        def limit = 5
        
        for (User user : userApi.listUsersPaged(null, null, null, 10, null, null, null, null)) {
            collectedCount++
            if (collectedCount >= limit) {
                println "  Breaking at ${collectedCount} users"
                break
            }
        }
        
        println "✓ Successfully stopped at ${collectedCount} users (early break works)"
        
        assertThat("Should collect some users and be able to break", 
                   collectedCount, greaterThan(0))
        assertThat("Should not exceed the limit", 
                   collectedCount, lessThanOrEqualTo(limit))
    }

    @Test(groups = "group3")
    void testPagedIterableWithFilter() {
        println "\n=== Testing Filtered Pagination ==="
        UserApi userApi = new UserApi(getClient())
        
        def email = "pagfilt-${uniqueTestName.take(30)}@ex.com"
        println "Creating user: ${email}"
        User createdUser = createUser(userApi, email, "FilterTest", "User")
        registerForCleanup(createdUser)
        
        // Allow time for indexing
        Thread.sleep(getTestOperationDelay())
        
        println "Fetching users with filter: profile.email eq \"${email}\""
        // Use filter with PagedIterable
        def found = false
        def count = 0
        for (User user : userApi.listUsersPaged(null, null, null, 10, 
                "profile.email eq \"${email}\"", null, null, null)) {
            count++
            println "  Found user: ${user.profile.email}"
            if (user.profile.email == email) {
                found = true
                break
            }
        }
        
        println "✓ Filter worked - found ${count} user(s) matching criteria"
        
        assertThat("Should find filtered user", found, equalTo(true))
    }

    @Test(groups = "group3")
    void testPagedIterableMultipleIterations() {
        println "\n=== Testing Multiple Iterations ==="
        UserApi userApi = new UserApi(getClient())
        
        println "Creating PagedIterable for users..."
        // Test that we can iterate multiple times on the same iterable
        def iterable = userApi.listUsersPaged(null, null, null, 5, null, null, null, null)
        
        println "First iteration (collecting 5 users)..."
        def firstCount = 0
        for (User user : iterable) {
            firstCount++
            if (firstCount >= 5) break
        }
        
        println "Second iteration (collecting 5 users)..."
        def secondCount = 0
        for (User user : iterable) {
            secondCount++
            if (secondCount >= 5) break
        }
        
        println "✓ First iteration: ${firstCount} users, Second iteration: ${secondCount} users"
        
        assertThat("First iteration should collect users", firstCount, greaterThan(0))
        assertThat("Second iteration should also collect users", secondCount, greaterThan(0))
        assertThat("Both iterations should collect same number", firstCount, equalTo(secondCount))
    }

    private User createUser(UserApi userApi, String email, String firstName, String lastName) {
        CreateUserRequest createUserRequest = new CreateUserRequest()
        
        UserProfile userProfile = new UserProfile()
        userProfile.firstName = firstName
        userProfile.lastName = lastName
        userProfile.email = email
        userProfile.login = email
        
        User user = new User()
        user.profile = userProfile
        
        UserCredentials credentials = new UserCredentials()
        PasswordCredential password = new PasswordCredential()
        password.value = 'Abcd1234!@#$'
        credentials.password = password
        user.credentials = credentials
        
        createUserRequest.profile = userProfile
        createUserRequest.credentials = credentials
        
        return userApi.createUser(createUserRequest, true, false, null)
    }
}
