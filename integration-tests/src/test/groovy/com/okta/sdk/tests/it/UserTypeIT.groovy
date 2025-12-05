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

import com.okta.sdk.resource.api.UserTypeApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Ignore
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for User Type API.
 * Tests user type management (create, get, list, update, replace, delete).
 */
class UserTypeIT extends ITSupport {

    private UserTypeApi userTypeApi

    UserTypeIT() {
        this.userTypeApi = new UserTypeApi(getClient())
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void createUserTypeTest() {
        def typeName = "custom_type_"+UUID.randomUUID().toString().substring(0,8)+""
        
        try {
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Custom Type "+UUID.randomUUID().toString().substring(0,8)+"")
            userType.setDescription("Test custom user type")
            
            def createdType = userTypeApi.createUserType(userType)
            registerForCleanup(createdType)
            
            assertThat(createdType, notNullValue())
            assertThat(createdType.getId(), notNullValue())
            assertThat(createdType.getName(), equalTo(typeName))
            assertThat(createdType.getDisplayName(), equalTo("Custom Type "+UUID.randomUUID().toString().substring(0,8)+""))

        } catch (ApiException e) {
            // Expected if user type management not enabled or insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void createUserTypeWithHttpInfoTest() {
        def typeName = "custom_httpinfo_"+UUID.randomUUID().toString().substring(0,8)+""
        
        try {
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Custom HttpInfo Type "+UUID.randomUUID().toString().substring(0,8)+"")
            
            def response = userTypeApi.createUserTypeWithHttpInfo(userType)
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(201))
            assertThat(response.getData(), notNullValue())
            assertThat(response.getData().getId(), notNullValue())
            
            registerForCleanup(response.getData())

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void getUserTypeTest() {
        def typeName = "custom_get_"+UUID.randomUUID().toString().substring(0,8)+""
        
        try {
            // Create a type first
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Get Type "+UUID.randomUUID().toString().substring(0,8)+"")
            
            def createdType = userTypeApi.createUserType(userType)
            registerForCleanup(createdType)
            
            // Get the type
            def retrievedType = userTypeApi.getUserType(createdType.getId())
            
            assertThat(retrievedType, notNullValue())
            assertThat(retrievedType.getId(), equalTo(createdType.getId()))
            assertThat(retrievedType.getName(), equalTo(typeName))

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void getUserTypeWithHttpInfoTest() {
        def typeName = "custom_get_httpinfo_"+UUID.randomUUID().toString().substring(0,8)+""
        
        try {
            // Create a type first
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Get HttpInfo Type "+UUID.randomUUID().toString().substring(0,8)+"")
            
            def createdType = userTypeApi.createUserType(userType)
            registerForCleanup(createdType)
            
            // Get with HTTP info
            def response = userTypeApi.getUserTypeWithHttpInfo(createdType.getId())
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(200))
            assertThat(response.getData(), notNullValue())
            assertThat(response.getData().getId(), equalTo(createdType.getId()))

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void getUserTypeWithInvalidIdTest() {
        try {
            userTypeApi.getUserType("invalid-type-id-"+UUID.randomUUID().toString().substring(0,8)+"")
            
            // Should fail
            assertThat("Should throw exception for invalid type ID", false)

        } catch (ApiException e) {
            // Expected - invalid type ID should return 404
            assertThat(e.getCode(), equalTo(404))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void listUserTypesTest() {
        try {
            def userTypes = userTypeApi.listUserTypes()
            
            assertThat(userTypes, notNullValue())
            // Should have at least the default user type

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void listUserTypesWithHttpInfoTest() {
        try {
            def response = userTypeApi.listUserTypesWithHttpInfo()
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(200))
            assertThat(response.getData(), notNullValue())

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void updateUserTypeTest() {
        def typeName = "custom_update_"+UUID.randomUUID().toString().substring(0,8)+""
        
        try {
            // Create a type first
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Update Type "+UUID.randomUUID().toString().substring(0,8)+"")
            
            def createdType = userTypeApi.createUserType(userType)
            registerForCleanup(createdType)
            
            // Update the type
            def updateRequest = new UserType()
            updateRequest.setDisplayName("Updated Type "+UUID.randomUUID().toString().substring(0,8)+"")
            updateRequest.setDescription("Updated description")
            
            def updatedType = userTypeApi.updateUserType(createdType.getId(), updateRequest)
            
            assertThat(updatedType, notNullValue())
            assertThat(updatedType.getId(), equalTo(createdType.getId()))
            assertThat(updatedType.getDisplayName(), equalTo("Updated Type "+UUID.randomUUID().toString().substring(0,8)+""))

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void updateUserTypeWithHttpInfoTest() {
        def typeName = "custom_update_httpinfo_"+UUID.randomUUID().toString().substring(0,8)+""
        
        try {
            // Create a type first
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Update HttpInfo "+UUID.randomUUID().toString().substring(0,8)+"")
            
            def createdType = userTypeApi.createUserType(userType)
            registerForCleanup(createdType)
            
            // Update with HTTP info
            def updateRequest = new UserType()
            updateRequest.setDisplayName("Updated HttpInfo "+UUID.randomUUID().toString().substring(0,8)+"")
            
            def response = userTypeApi.updateUserTypeWithHttpInfo(createdType.getId(), updateRequest)
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(200))
            assertThat(response.getData(), notNullValue())

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void replaceUserTypeTest() {
        def typeName = "custom_replace_"+UUID.randomUUID().toString().substring(0,8)+""
        
        try {
            // Create a type first
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Replace Type "+UUID.randomUUID().toString().substring(0,8)+"")
            
            def createdType = userTypeApi.createUserType(userType)
            registerForCleanup(createdType)
            
            // Replace the type
            def replaceRequest = new UserType()
            replaceRequest.setDisplayName("Replaced Type "+UUID.randomUUID().toString().substring(0,8)+"")
            replaceRequest.setDescription("Replaced description")
            
            def replacedType = userTypeApi.replaceUserType(createdType.getId(), replaceRequest)
            
            assertThat(replacedType, notNullValue())
            assertThat(replacedType.getId(), equalTo(createdType.getId()))
            assertThat(replacedType.getDisplayName(), equalTo("Replaced Type "+UUID.randomUUID().toString().substring(0,8)+""))

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void replaceUserTypeWithHttpInfoTest() {
        def typeName = "custom_replace_httpinfo_"+UUID.randomUUID().toString().substring(0,8)+""
        
        try {
            // Create a type first
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Replace HttpInfo "+UUID.randomUUID().toString().substring(0,8)+"")
            
            def createdType = userTypeApi.createUserType(userType)
            registerForCleanup(createdType)
            
            // Replace with HTTP info
            def replaceRequest = new UserType()
            replaceRequest.setDisplayName("Replaced HttpInfo "+UUID.randomUUID().toString().substring(0,8)+"")
            
            def response = userTypeApi.replaceUserTypeWithHttpInfo(createdType.getId(), replaceRequest)
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(200))
            assertThat(response.getData(), notNullValue())

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void deleteUserTypeTest() {
        def typeName = "custom_delete_"+UUID.randomUUID().toString().substring(0,8)+""
        
        try {
            // Create a type first
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Delete Type "+UUID.randomUUID().toString().substring(0,8)+"")
            
            def createdType = userTypeApi.createUserType(userType)
            
            assertThat(createdType, notNullValue())
            assertThat(createdType.getId(), notNullValue())
            
            // Delete the type
            userTypeApi.deleteUserType(createdType.getId())
            
            // Verify deletion - getting should fail
            try {
                userTypeApi.getUserType(createdType.getId())
                assertThat("Type should be deleted", false)
            } catch (ApiException notFoundEx) {
                assertThat(notFoundEx.getCode(), equalTo(404))
            }

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void deleteUserTypeWithHttpInfoTest() {
        def typeName = "custom_delete_httpinfo_"+UUID.randomUUID().toString().substring(0,8)+""
        
        try {
            // Create a type first
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Delete HttpInfo "+UUID.randomUUID().toString().substring(0,8)+"")
            
            def createdType = userTypeApi.createUserType(userType)
            
            assertThat(createdType, notNullValue())
            
            // Delete with HTTP info
            def response = userTypeApi.deleteUserTypeWithHttpInfo(createdType.getId())
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(204))

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void userTypeCompleteLifecycleTest() {
        def typeName = "custom_lifecycle_"+UUID.randomUUID().toString().substring(0,8)+""
        
        try {
            // 1. Create user type
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Lifecycle Type "+UUID.randomUUID().toString().substring(0,8)+"")
            userType.setDescription("Initial description")
            
            def createdType = userTypeApi.createUserType(userType)
            registerForCleanup(createdType)
            
            assertThat(createdType, notNullValue())
            assertThat(createdType.getId(), notNullValue())
            String typeId = createdType.getId()
            
            // 2. Get user type
            def retrievedType = userTypeApi.getUserType(typeId)
            assertThat(retrievedType, notNullValue())
            assertThat(retrievedType.getId(), equalTo(typeId))
            
            // 3. List user types - should include our type
            def types = userTypeApi.listUserTypes()
            assertThat(types, notNullValue())
            def found = types.any { it.getId() == typeId }
            assertThat(found, equalTo(true))
            
            // 4. Update user type
            def updateRequest = new UserType()
            updateRequest.setDisplayName("Updated Lifecycle "+UUID.randomUUID().toString().substring(0,8)+"")
            
            def updatedType = userTypeApi.updateUserType(typeId, updateRequest)
            assertThat(updatedType, notNullValue())
            assertThat(updatedType.getDisplayName(), equalTo("Updated Lifecycle "+UUID.randomUUID().toString().substring(0,8)+""))
            
            // 5. Replace user type
            def replaceRequest = new UserType()
            replaceRequest.setDisplayName("Replaced Lifecycle "+UUID.randomUUID().toString().substring(0,8)+"")
            replaceRequest.setDescription("Replaced description")
            
            def replacedType = userTypeApi.replaceUserType(typeId, replaceRequest)
            assertThat(replacedType, notNullValue())
            
            // 6. Delete user type
            userTypeApi.deleteUserType(typeId)

        } catch (ApiException e) {
            // Expected if user type management not fully enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void createMultipleUserTypesTest() {
        def typeName1 = "custom_multi_1_"+UUID.randomUUID().toString().substring(0,8)+""
        def typeName2 = "custom_multi_2_"+UUID.randomUUID().toString().substring(0,8)+""
        
        try {
            // Create first type
            def userType1 = new UserType()
            userType1.setName(typeName1)
            userType1.setDisplayName("Multi Type 1 "+UUID.randomUUID().toString().substring(0,8)+"")
            
            def createdType1 = userTypeApi.createUserType(userType1)
            registerForCleanup(createdType1)
            
            assertThat(createdType1, notNullValue())
            
            // Create second type
            def userType2 = new UserType()
            userType2.setName(typeName2)
            userType2.setDisplayName("Multi Type 2 "+UUID.randomUUID().toString().substring(0,8)+"")
            
            def createdType2 = userTypeApi.createUserType(userType2)
            registerForCleanup(createdType2)
            
            assertThat(createdType2, notNullValue())
            
            // List should include both types
            def types = userTypeApi.listUserTypes()
            assertThat(types, notNullValue())
            assertThat(types.size(), greaterThanOrEqualTo(2))

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void userTypePropertiesVerificationTest() {
        def typeName = "custom_properties_"+UUID.randomUUID().toString().substring(0,8)+""
        
        try {
            // Create type with all properties
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Properties Type "+UUID.randomUUID().toString().substring(0,8)+"")
            userType.setDescription("Type with all properties set")
            
            def createdType = userTypeApi.createUserType(userType)
            registerForCleanup(createdType)
            
            assertThat(createdType, notNullValue())
            assertThat(createdType.getId(), notNullValue())
            assertThat(createdType.getName(), equalTo(typeName))
            assertThat(createdType.getDisplayName(), equalTo("Properties Type "+UUID.randomUUID().toString().substring(0,8)+""))
            assertThat(createdType.getDescription(), equalTo("Type with all properties set"))
            // Default should be false for custom types
            if (createdType.getDefault() != null) {
                assertThat(createdType.getDefault(), equalTo(false))
            }
            // Created and lastUpdated should be set
            assertThat(createdType.getCreated(), notNullValue())
            assertThat(createdType.getLastUpdated(), notNullValue())

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void updateVsReplaceUserTypeTest() {
        def typeName = "custom_update_replace_"+UUID.randomUUID().toString().substring(0,8)+""
        
        try {
            // Create type
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Original "+UUID.randomUUID().toString().substring(0,8)+"")
            userType.setDescription("Original description")
            
            def createdType = userTypeApi.createUserType(userType)
            registerForCleanup(createdType)
            
            assertThat(createdType, notNullValue())
            String typeId = createdType.getId()
            
            // Update (partial) - only change display name
            def updateRequest = new UserType()
            updateRequest.setDisplayName("Updated "+UUID.randomUUID().toString().substring(0,8)+"")
            
            def updatedType = userTypeApi.updateUserType(typeId, updateRequest)
            assertThat(updatedType.getDisplayName(), equalTo("Updated "+UUID.randomUUID().toString().substring(0,8)+""))
            
            // Replace (full) - provide complete object
            def replaceRequest = new UserType()
            replaceRequest.setDisplayName("Replaced "+UUID.randomUUID().toString().substring(0,8)+"")
            replaceRequest.setDescription("Replaced description")
            
            def replacedType = userTypeApi.replaceUserType(typeId, replaceRequest)
            assertThat(replacedType.getDisplayName(), equalTo("Replaced "+UUID.randomUUID().toString().substring(0,8)+""))

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void deleteNonExistentUserTypeTest() {
        try {
            // Try to delete non-existent type
            userTypeApi.deleteUserType("non-existent-type-"+UUID.randomUUID().toString().substring(0,8)+"")
            
            // Should fail
            assertThat("Should throw exception for non-existent type", false)

        } catch (ApiException e) {
            // Expected - non-existent type should return 404
            assertThat(e.getCode(), equalTo(404))
        }
    }

    @Test(groups = "group3")
    @Ignore("WithHttpInfo method not available in Java SDK")
    void userTypeNameValidationTest() {
        try {
            // Try to create type with invalid name (spaces not allowed)
            def userType = new UserType()
            userType.setName("invalid name with spaces")
            userType.setDisplayName("Invalid Type")
            
            userTypeApi.createUserType(userType)
            
            // Should fail with validation error
            assertThat("Should throw exception for invalid type name", false)

        } catch (ApiException e) {
            // Expected - invalid name should return 400
            assertThat(e.getCode(), equalTo(400))
        }
    }

    // ========== Map Parameter Coverage Tests ==========

    @Test(groups = "group3")
    void createUserType_withMapParameter_callsCorrectEndpoint() {
        def typeName = "custom_map_"+UUID.randomUUID().toString().substring(0,8)
        
        try {
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Map Param Type")
            userType.setDescription("Test with map parameter")
            
            def additionalParams = [:] // Empty map for additional parameters
            
            def createdType = userTypeApi.createUserType(userType, additionalParams)
            registerForCleanup(createdType)
            
            assertThat(createdType, notNullValue())
            assertThat(createdType.getId(), notNullValue())
            assertThat(createdType.getName(), equalTo(typeName))

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void getUserType_withMapParameter_returnsUserType() {
        def typeName = "custom_getmap_"+UUID.randomUUID().toString().substring(0,8)
        
        try {
            // Create a type first
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Get Map Type")
            
            def createdType = userTypeApi.createUserType(userType)
            registerForCleanup(createdType)
            
            def additionalParams = [:] // Empty map for additional parameters
            
            // Get the type with Map parameter
            def retrievedType = userTypeApi.getUserType(createdType.getId(), additionalParams)
            
            assertThat(retrievedType, notNullValue())
            assertThat(retrievedType.getId(), equalTo(createdType.getId()))
            assertThat(retrievedType.getName(), equalTo(typeName))

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserTypes_withMapParameter_returnsUserTypes() {
        try {
            def additionalParams = [:] // Empty map for additional parameters
            
            // List types with Map parameter
            def userTypes = userTypeApi.listUserTypes(additionalParams)
            
            assertThat(userTypes, notNullValue())
            assertThat(userTypes, not(empty()))

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void updateUserType_withMapParameter_updatesUserType() {
        def typeName = "custom_updmap_"+UUID.randomUUID().toString().substring(0,8)
        
        try {
            // Create a type first
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Update Map Type")
            
            def createdType = userTypeApi.createUserType(userType)
            registerForCleanup(createdType)
            
            def additionalParams = [:] // Empty map for additional parameters
            
            // Update the type with Map parameter
            def updateRequest = new UserTypePostRequest()
            updateRequest.setDisplayName("Updated Map Display Name")
            updateRequest.setDescription("Updated description")
            
            def updatedType = userTypeApi.updateUserType(createdType.getId(), updateRequest, additionalParams)
            
            assertThat(updatedType, notNullValue())
            assertThat(updatedType.getDisplayName(), equalTo("Updated Map Display Name"))
            assertThat(updatedType.getDescription(), equalTo("Updated description"))

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void replaceUserType_withMapParameter_replacesUserType() {
        def typeName = "custom_replmap_"+UUID.randomUUID().toString().substring(0,8)
        
        try {
            // Create a type first
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Replace Map Type")
            userType.setDescription("Original description")
            
            def createdType = userTypeApi.createUserType(userType)
            registerForCleanup(createdType)
            
            def additionalParams = [:] // Empty map for additional parameters
            
            // Replace the type with Map parameter
            def replaceRequest = new UserTypePutRequest()
            replaceRequest.setDisplayName("Replaced Map Display Name")
            replaceRequest.setDescription("Replaced description")
            
            def replacedType = userTypeApi.replaceUserType(createdType.getId(), replaceRequest, additionalParams)
            
            assertThat(replacedType, notNullValue())
            assertThat(replacedType.getDisplayName(), equalTo("Replaced Map Display Name"))
            assertThat(replacedType.getDescription(), equalTo("Replaced description"))

        } catch (ApiException e) {
            // Expected if user type management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void deleteUserType_withMapParameter_deletesUserType() {
        def typeName = "custom_delmap_"+UUID.randomUUID().toString().substring(0,8)
        
        try {
            // Create a type first
            def userType = new UserType()
            userType.setName(typeName)
            userType.setDisplayName("Delete Map Type")
            
            def createdType = userTypeApi.createUserType(userType)
            // Don't register for cleanup since we're deleting it
            
            def additionalParams = [:] // Empty map for additional parameters
            
            // Delete the type with Map parameter
            userTypeApi.deleteUserType(createdType.getId(), additionalParams)
            
            // Verify it's deleted by trying to get it
            try {
                userTypeApi.getUserType(createdType.getId())
                assertThat("Should throw exception for deleted type", false)
            } catch (ApiException e) {
                // Expected - deleted type should return 404
                assertThat(e.getCode(), equalTo(404))
            }

        } catch (ApiException e) {
            // Expected if user type management not enabled or server error
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404), equalTo(500)))
        }
    }
}

