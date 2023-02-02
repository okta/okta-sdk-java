/*
 * Copyright 2022-Present Okta, Inc.
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

import com.okta.sdk.resource.role.RoleType
import com.okta.sdk.resource.role.Subscription
import com.okta.sdk.resource.role.SubscriptionStatus
import com.okta.sdk.resource.subscription.NotificationType
import com.okta.sdk.resource.user.User
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue

/**
 * Integration tests of
 * @see Subscription
 */
class SubscriptionIT extends ITSupport {

    @Test
    void testListRoleSubscriptions() {
        def subscriptionList = client.instantiate(Subscription).listRoleSubscriptions(RoleType.SUPER_ADMIN.toString())
        assertThat subscriptionList, notNullValue()
        assertThat subscriptionList.collect(), hasSize(greaterThan(0))
        assertThat subscriptionList[0].getNotificationType(), notNullValue()
        assertThat subscriptionList[0].getChannels(), notNullValue()
        assertThat subscriptionList[0].getStatus(), notNullValue()
    }

    @Test
    void testGetRoleSubscriptionByNotificationType() {
        def subscription = client.instantiate(Subscription)
            .getRoleSubscriptionByNotificationType(RoleType.SUPER_ADMIN.toString(), NotificationType.IWA_AGENT.toString())
        assertThat subscription, notNullValue()
        assertThat subscription.getNotificationType(), notNullValue()
        assertThat subscription.getChannels(), notNullValue()
        assertThat subscription.getStatus(), notNullValue()
    }

    @Test
    void testSubscribeRoleSubscriptionByNotificationType() {
        client.instantiate(Subscription)
            .unsubscribeRoleSubscriptionByNotificationType(RoleType.SUPER_ADMIN.toString(), NotificationType.IWA_AGENT.toString())

        def unsubscribed = client.instantiate(Subscription).listRoleSubscriptions(RoleType.SUPER_ADMIN.toString())
            .stream()
            .filter(subscription -> subscription.getNotificationType() == NotificationType.IWA_AGENT)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Subscription not found"))
            .getStatus()

        assertThat(unsubscribed, is(SubscriptionStatus.UNSUBSCRIBED))

        client.instantiate(Subscription)
            .subscribeRoleSubscriptionByNotificationType(RoleType.SUPER_ADMIN.toString(), NotificationType.IWA_AGENT.toString())

        def subscribed = client.instantiate(Subscription).listRoleSubscriptions(RoleType.SUPER_ADMIN.toString())
            .stream()
            .filter(subscription -> subscription.getNotificationType() == NotificationType.IWA_AGENT)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Subscription not found"))
            .getStatus()

        assertThat(subscribed, is(SubscriptionStatus.SUBSCRIBED))
    }

    @Test
    void testListUserSubscriptions() {
        def currentUser = client.http().get("/api/v1/users/me", User.class)
        def subscriptionList = client.instantiate(Subscription).listUserSubscriptions(currentUser.getId())
        assertThat subscriptionList, notNullValue()
        assertThat subscriptionList.collect(), hasSize(greaterThan(0))
        assertThat subscriptionList[0].getNotificationType(), notNullValue()
        assertThat subscriptionList[0].getChannels(), notNullValue()
        assertThat subscriptionList[0].getStatus(), notNullValue()
    }

    @Test
    void testGetUserSubscriptionByNotificationType() {
        def currentUser = client.http().get("/api/v1/users/me", User.class)
        def subscription = client.instantiate(Subscription)
            .getUserSubscriptionByNotificationType(currentUser.getId(), NotificationType.IWA_AGENT.toString())
        assertThat subscription, notNullValue()
        assertThat subscription.getNotificationType(), notNullValue()
        assertThat subscription.getChannels(), notNullValue()
        assertThat subscription.getStatus(), notNullValue()
    }

    // flaky test, hence disabled
    @Test (groups = "bacon", enabled = false)
    void testSubscribeUserSubscriptionByNotificationType() {
        def currentUser = client.http().get("/api/v1/users/me", User.class)
        client.instantiate(Subscription)
            .unsubscribeUserSubscriptionByNotificationType(currentUser.getId(), NotificationType.IWA_AGENT.toString())

        def unsubscribed = client.instantiate(Subscription).listUserSubscriptions(currentUser.getId())
            .stream()
            .filter(subscription -> subscription.getNotificationType() == NotificationType.IWA_AGENT)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Subscription not found"))
            .getStatus()

        assertThat(unsubscribed, is(SubscriptionStatus.UNSUBSCRIBED))

        client.instantiate(Subscription)
            .subscribeUserSubscriptionByNotificationType(currentUser.getId(), NotificationType.IWA_AGENT.toString())

        def subscribed = client.instantiate(Subscription).listUserSubscriptions(currentUser.getId())
            .stream()
            .filter(subscription -> subscription.getNotificationType() == NotificationType.IWA_AGENT)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Subscription not found"))
            .getStatus()

        assertThat(subscribed, is(SubscriptionStatus.SUBSCRIBED))
    }
}
