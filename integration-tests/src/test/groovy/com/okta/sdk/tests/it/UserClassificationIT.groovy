///*
// * Copyright 2025-Present Okta, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.okta.sdk.tests.it
//
//import com.okta.sdk.resource.api.UserApi
//import com.okta.sdk.resource.api.UserClassificationApi
//import com.okta.sdk.resource.client.ApiException
//import com.okta.sdk.resource.model.*
//import com.okta.sdk.resource.user.UserBuilder
//import com.okta.sdk.tests.it.util.ITSupport
//import org.testng.annotations.Test
//
//import static org.hamcrest.MatcherAssert.assertThat
//import static org.hamcrest.Matchers.*
//
///**
// * Integration tests for UserClassificationApi
// * Tests user classification management (STANDARD/LITE)
// */
//class UserClassificationIT extends ITSupport {
//
//    private UserClassificationApi userClassificationApi
//    private UserApi userApi
//
//    UserClassificationIT() {
//        this.userClassificationApi = new UserClassificationApi(getClient())
//        this.userApi = new UserApi(getClient())
//    }
//
//    @Test(groups = "group3")
//    void getUserClassificationStandardTest() {
//        // Create a standard user
//        def email = "user-classification-standard-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
//        User user = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Classification")
//            .setLastName("Standard")
//            .buildAndCreate(userApi)
//        registerForCleanup(user)
//
//        // Get user classification
//        def classification = userClassificationApi.getUserClassification(user.getId())
//
//        // Assert
//        assertThat(classification, notNullValue())
//        assertThat(classification.getType(), equalTo(ClassificationType.STANDARD))
//        assertThat(classification.getLastUpdated(), notNullValue())
//    }
//
//    @Test(groups = "group3")
//    void replaceUserClassificationToStandardTest() {
//        // Create a user
//        def email = "user-replace-standard-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
//        User user = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Replace")
//            .setLastName("ToStandard")
//            .buildAndCreate(userApi)
//        registerForCleanup(user)
//
//        // Replace classification to STANDARD
//        ReplaceUserClassification replaceRequest = new ReplaceUserClassification()
//        replaceRequest.setType(ClassificationType.STANDARD)
//
//        def classification = userClassificationApi.replaceUserClassification(user.getId(), replaceRequest)
//
//        // Assert
//        assertThat(classification, notNullValue())
//        assertThat(classification.getType(), equalTo(ClassificationType.STANDARD))
//        assertThat(classification.getLastUpdated(), notNullValue())
//    }
//
//    @Test(groups = "group3")
//    void replaceUserClassificationToLiteTest() {
//        // Create a user
//        def email = "user-replace-lite-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
//        User user = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Replace")
//            .setLastName("ToLite")
//            .buildAndCreate(userApi)
//        registerForCleanup(user)
//
//        // Replace classification to LITE
//        ReplaceUserClassification replaceRequest = new ReplaceUserClassification()
//        replaceRequest.setType(ClassificationType.LITE)
//
//        def classification = userClassificationApi.replaceUserClassification(user.getId(), replaceRequest)
//
//        // Assert
//        assertThat(classification, notNullValue())
//        assertThat(classification.getType(), equalTo(ClassificationType.LITE))
//        assertThat(classification.getLastUpdated(), notNullValue())
//    }
//
//    @Test(groups = "group3")
//    void classificationManagementGetThenReplaceTest() {
//        // Create a user
//        def email = "user-classification-flow-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
//        User user = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Classification")
//            .setLastName("Flow")
//            .buildAndCreate(userApi)
//        registerForCleanup(user)
//
//        // Get initial classification
//        def initialClassification = userClassificationApi.getUserClassification(user.getId())
//        assertThat(initialClassification.getType(), equalTo(ClassificationType.STANDARD))
//
//        // Update classification
//        ReplaceUserClassification replaceRequest = new ReplaceUserClassification()
//        replaceRequest.setType(ClassificationType.LITE)
//
//        def updatedClassification = userClassificationApi.replaceUserClassification(user.getId(), replaceRequest)
//
//        // Assert updated
//        assertThat(updatedClassification.getType(), equalTo(ClassificationType.LITE))
//        assertThat(updatedClassification.getLastUpdated(), greaterThan(initialClassification.getLastUpdated()))
//    }
//
//    @Test(groups = "group3")
//    void upgradeFromLiteToStandardTest() {
//        // Create a user
//        def email = "user-upgrade-standard-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
//        User user = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Upgrade")
//            .setLastName("Standard")
//            .buildAndCreate(userApi)
//        registerForCleanup(user)
//
//        // Set to LITE first
//        ReplaceUserClassification liteRequest = new ReplaceUserClassification()
//        liteRequest.setType(ClassificationType.LITE)
//        userClassificationApi.replaceUserClassification(user.getId(), liteRequest)
//
//        // Upgrade to STANDARD
//        ReplaceUserClassification upgradeRequest = new ReplaceUserClassification()
//        upgradeRequest.setType(ClassificationType.STANDARD)
//
//        def upgradedClassification = userClassificationApi.replaceUserClassification(user.getId(), upgradeRequest)
//
//        // Assert
//        assertThat(upgradedClassification.getType(), equalTo(ClassificationType.STANDARD))
//    }
//
//    @Test(groups = "group3")
//    void downgradeFromStandardToLiteTest() {
//        // Create a user
//        def email = "user-downgrade-lite-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
//        User user = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Downgrade")
//            .setLastName("Lite")
//            .buildAndCreate(userApi)
//        registerForCleanup(user)
//
//        // Verify initially STANDARD
//        def initialClassification = userClassificationApi.getUserClassification(user.getId())
//        assertThat(initialClassification.getType(), equalTo(ClassificationType.STANDARD))
//
//        // Downgrade to LITE
//        ReplaceUserClassification downgradeRequest = new ReplaceUserClassification()
//        downgradeRequest.setType(ClassificationType.LITE)
//
//        def downgradedClassification = userClassificationApi.replaceUserClassification(user.getId(), downgradeRequest)
//
//        // Assert
//        assertThat(downgradedClassification.getType(), equalTo(ClassificationType.LITE))
//    }
//
//    @Test(groups = "group3")
//    void replaceClassificationSameTypeUpdatesTimestampTest() {
//        // Create a user
//        def email = "user-same-type-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
//        User user = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("SameType")
//            .setLastName("Update")
//            .buildAndCreate(userApi)
//        registerForCleanup(user)
//
//        // Get initial classification
//        def initialClassification = userClassificationApi.getUserClassification(user.getId())
//        def initialTimestamp = initialClassification.getLastUpdated()
//
//        Thread.sleep(1000) // Wait to ensure timestamp difference
//
//        // Replace with same type
//        ReplaceUserClassification replaceRequest = new ReplaceUserClassification()
//        replaceRequest.setType(ClassificationType.STANDARD)
//
//        def updatedClassification = userClassificationApi.replaceUserClassification(user.getId(), replaceRequest)
//
//        // Assert timestamp updated
//        assertThat(updatedClassification.getType(), equalTo(ClassificationType.STANDARD))
//        assertThat(updatedClassification.getLastUpdated(), greaterThanOrEqualTo(initialTimestamp))
//    }
//
//    @Test(groups = "group3")
//    void getUserClassificationWithSpecialCharactersInUserIdTest() {
//        // Create a user with email as ID
//        def email = "user+special.classification-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
//        User user = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Special")
//            .setLastName("Classification")
//            .buildAndCreate(userApi)
//        registerForCleanup(user)
//
//        // Get classification using user ID
//        def classification = userClassificationApi.getUserClassification(user.getId())
//
//        // Assert
//        assertThat(classification, notNullValue())
//        assertThat(classification.getType(), notNullValue())
//    }
//}
