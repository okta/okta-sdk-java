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
package com.okta.sdk.tests.it.util

import com.okta.sdk.client.Client
import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.policy.GroupCondition
import com.okta.sdk.resource.policy.OktaSignOnPolicy
import com.okta.sdk.resource.policy.OktaSignOnPolicyConditions
import com.okta.sdk.resource.policy.PasswordPolicy
import com.okta.sdk.resource.policy.PasswordPolicyConditions
import com.okta.sdk.resource.policy.Policy
import com.okta.sdk.resource.policy.PolicyPeopleCondition
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.Scenario
import org.testng.ITestContext
import org.testng.annotations.AfterSuite
import org.testng.annotations.BeforeSuite

abstract class ITSupport implements ClientProvider {

    public static final USE_TEST_SERVER = "okta.use.testServer"
    public static final TEST_SERVER_ALL_SCENARIOS = "okta.testServer.allScenarios"

    private TestServer testServer

    @BeforeSuite
    void start(ITestContext context) {

        boolean useTestServer = Boolean.getBoolean(USE_TEST_SERVER) ||
                Boolean.valueOf(System.getenv("OKTA_USE_TEST_SERVER"))

        if (useTestServer) {
            List<String> scenarios = []

            // if TEST_SERVER_ALL_SCENARIOS is set, we need to run all of them
            if (!Boolean.getBoolean(TEST_SERVER_ALL_SCENARIOS)) {
                context.getAllTestMethods().each { testMethod ->
                    Scenario scenario = testMethod.getConstructorOrMethod().getMethod().getAnnotation(Scenario)
                    if (scenario != null) {
                        scenarios.add(scenario.value())
                    }
                }
            }

            testServer = new TestServer().start(scenarios)
            System.setProperty(TestServer.TEST_SERVER_BASE_URL, "http://localhost:${testServer.getMockPort()}/")
        }
    }

    @AfterSuite()
    void stop() {
        if (testServer != null) {
            testServer.verify()
            testServer.stop()
        }
    }

    User randomUser() {
        Client client = getClient()

        def email = "joe.coder+" + UUID.randomUUID().toString() + "@example.com"
        User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName("Joe")
                .setLastName("Code")
                .setPassword("Password1".toCharArray())
                .setActive(true)
                .buildAndCreate(client)
        registerForCleanup(user)

        return user
    }

    Group randomGroup(String name = "group-java-sdk-${UUID.randomUUID().toString()}") {

        Group group = GroupBuilder.instance()
            .setName(name)
            .setDescription(name)
            .buildAndCreate(getClient())
        registerForCleanup(group)

        return group
    }

    PasswordPolicy randomPasswordPolicy(String groupId) {

        PasswordPolicy policy = client.createPolicy(client.instantiate(PasswordPolicy)
            .setConditions(client.instantiate(PasswordPolicyConditions)
                .setPeople(client.instantiate(PolicyPeopleCondition)
                    .setGroups(client.instantiate(GroupCondition)
                        .setInclude([groupId]))))
            .setName("policy-java-" + UUID.randomUUID().toString())
            .setStatus(Policy.StatusEnum.ACTIVE)
            .setDescription("IT created Policy")
            .setStatus(Policy.StatusEnum.ACTIVE)
            .setPriority(1))

        registerForCleanup(policy)

        return policy
    }

    OktaSignOnPolicy randomSignOnPolicy(String groupId) {

        OktaSignOnPolicy policy = client.createPolicy(client.instantiate(OktaSignOnPolicy)
            .setConditions(client.instantiate(OktaSignOnPolicyConditions)
                .setPeople(client.instantiate(PolicyPeopleCondition)
                    .setGroups(client.instantiate(GroupCondition)
                        .setInclude([groupId]))))
            .setName("policy-java-" + UUID.randomUUID().toString())
            .setStatus(Policy.StatusEnum.ACTIVE)
            .setDescription("IT created Policy")
            .setStatus(Policy.StatusEnum.ACTIVE))

        registerForCleanup(policy)

        return policy
    }
}