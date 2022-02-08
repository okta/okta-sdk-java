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
import com.okta.sdk.resource.Deletable
import com.okta.sdk.resource.ExtensibleResource
import com.okta.sdk.resource.OktaSignOnPolicy
import com.okta.sdk.resource.PasswordPolicy
import com.okta.sdk.resource.authorization.server.LifecycleStatus
import com.okta.sdk.resource.authorization.server.PolicyType
import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.group.User
import com.okta.sdk.resource.policy.OktaSignOnPolicyBuilder
import com.okta.sdk.resource.policy.PasswordPolicyBuilder
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.ConditionalSkipTestAnalyzer
import com.okta.sdk.tests.Scenario
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.ITestContext
import org.testng.annotations.AfterSuite
import org.testng.annotations.BeforeSuite
import org.testng.annotations.Listeners

@Listeners(value = ConditionalSkipTestAnalyzer.class)
abstract class ITSupport implements ClientProvider {

    private final static Logger log = LoggerFactory.getLogger(ITSupport)

    public static final USE_TEST_SERVER = "okta.use.testServer"
    public static final TEST_SERVER_ALL_SCENARIOS = "okta.testServer.allScenarios"
    public static final IT_OPERATION_DELAY = "okta.it.operationDelay"

    public static final SPINE_NAME_PREFIX = "java-sdk-it-"
    public static final UNDERSCORE_NAME_PREFIX = "java_sdk_it_"

    public static boolean isOIEEnvironment
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

        isOIEEnvironment = isOIEEnvironment()
    }

    @AfterSuite()
    void stop() {
        if (testServer != null) {
            testServer.verify()
            testServer.stop()
        }
    }

    /**
     * Some Integration test operations exhibit flakiness at times due to replication lag at the core backend.
     * We therefore add delays between some test operations to ensure we give sufficient
     * interval between invoking operations on the backend resource.
     *
     * This delay could be specified by the following in order of precedence:
     * - System property 'okta.it.operationDelay'
     * - Env variable 'OKTA_IT_OPERATION_DELAY'
     */
    static long getTestOperationDelay() {
        Long testDelay = Long.getLong(IT_OPERATION_DELAY)

        if (testDelay == null) {
            try {
                testDelay = Long.valueOf(System.getenv().getOrDefault("OKTA_IT_OPERATION_DELAY", "0"))
            } catch (NumberFormatException e) {
                log.error("Could not parse env variable OKTA_IT_OPERATION_DELAY. Will default to 0!")
                return 0
            }
        }

        return testDelay == null ? 0 : testDelay;
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

    Group randomGroup(String name = "java-sdk-it-${UUID.randomUUID().toString()}") {

        Group group = GroupBuilder.instance()
            .setName(name)
            .setDescription(name)
            .buildAndCreate(getClient())
        registerForCleanup(group)

        return group
    }

    PasswordPolicy randomPasswordPolicy(String groupId) {

        PasswordPolicy policy = PasswordPolicyBuilder.instance()
            .setName("java-sdk-it-" + UUID.randomUUID().toString())
            .setStatus(LifecycleStatus.ACTIVE)
            .setDescription("IT created Policy")
            .setStatus(LifecycleStatus.ACTIVE)
            .setPriority(1)
            .addGroup(groupId)
        .buildAndCreate(client) as PasswordPolicy

        registerForCleanup(policy)

        return policy
    }

    OktaSignOnPolicy randomSignOnPolicy(String groupId) {

        OktaSignOnPolicy policy = OktaSignOnPolicyBuilder.instance()
            .setName("java-sdk-it-" + UUID.randomUUID().toString())
            .setDescription("IT created Policy")
            .setStatus(LifecycleStatus.ACTIVE)
        .setType(PolicyType.OKTA_SIGN_ON)
        .buildAndCreate(client) as OktaSignOnPolicy

        registerForCleanup(policy)

        return policy
    }

    boolean isOIEEnvironment() {

        Object pipeline = client.http()
            .get("/.well-known/okta-organization", ExtensibleResource.class)
            .get("pipeline")
        if (pipeline != null && pipeline.toString() == "idx") {
            return true
        }
        return false
    }
}