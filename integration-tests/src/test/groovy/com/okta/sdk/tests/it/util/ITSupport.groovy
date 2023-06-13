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

import com.fasterxml.jackson.core.type.TypeReference
import com.okta.commons.http.MediaType
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.policy.OktaSignOnPolicyBuilder
import com.okta.sdk.resource.policy.PasswordPolicyBuilder
import com.okta.sdk.resource.policy.PolicyBuilder
import com.okta.sdk.tests.ConditionalSkipTestAnalyzer
import com.okta.sdk.tests.Scenario
import org.openapitools.client.Pair
import org.openapitools.client.api.GroupApi
import org.openapitools.client.api.PolicyApi
import org.openapitools.client.api.UserApi
import org.openapitools.client.model.CreateUserRequest
import org.openapitools.client.model.Group
import org.openapitools.client.model.HttpMethod
import org.openapitools.client.model.LifecycleStatus
import org.openapitools.client.model.OktaSignOnPolicy
import org.openapitools.client.model.PasswordPolicy
import org.openapitools.client.model.Policy
import org.openapitools.client.model.PolicyType
import org.openapitools.client.model.User
import org.openapitools.client.model.UserProfile

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.testng.ITestContext
import org.testng.ITestResult
import org.testng.annotations.AfterMethod
import org.testng.annotations.AfterSuite
import org.testng.annotations.BeforeMethod
import org.testng.annotations.BeforeSuite
import org.testng.annotations.Listeners

import java.lang.reflect.Method

@Listeners(value = ConditionalSkipTestAnalyzer.class)
abstract class ITSupport implements ClientProvider {

    private final static Logger log = LoggerFactory.getLogger(ITSupport)

    public static final USE_TEST_SERVER = "okta.use.testServer"
    public static final TEST_SERVER_ALL_SCENARIOS = "okta.testServer.allScenarios"
    public static final IT_OPERATION_DELAY = "okta.it.operationDelay"

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

    @BeforeMethod
    void log(Method method) {
        log.info("Running" + " - " + method.getName() + " on " + getClient().getBasePath())
    }

    @AfterMethod
    void afterMethod(ITestResult result) {
        log.info("Finished " + result.getInstanceName() + " - " + result.getName())
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

        return testDelay == null ? 0 : testDelay
    }

    User randomUser() {

        UserApi userApi = new UserApi(getClient())

        def email = "joe.coder+" + UUID.randomUUID().toString() + "@example.com"

        UserProfile userProfile = new UserProfile()
            .firstName("Joe")
            .lastName("Coder")
            .email(email)
            .mobilePhone("1234567890")
            .login(email)

        CreateUserRequest createUserRequest = new CreateUserRequest()
            .profile(userProfile)

        User createdUser = userApi.createUser(createUserRequest, true, null, null)

        return createdUser
    }

    Group randomGroup(String name = "java-sdk-it-${UUID.randomUUID().toString()}") {

        GroupApi groupApi = new GroupApi(getClient())

        Group group = GroupBuilder.instance()
            .setName(name)
            .setDescription(name)
            .buildAndCreate(groupApi)

        return group
    }

    PasswordPolicy randomPasswordPolicy(String groupId) {

        PolicyApi policyApi = new PolicyApi(getClient())

        PasswordPolicy policy = PasswordPolicyBuilder.instance()
            .setName("java-sdk-it-" + UUID.randomUUID().toString())
            .setStatus(LifecycleStatus.ACTIVE)
            .setDescription("IT created Policy")
            .setPriority(1)
            .addGroup(groupId)
            .buildAndCreate(policyApi) as PasswordPolicy

        return policy
    }

    OktaSignOnPolicy randomSignOnPolicy() {

        PolicyApi policyApi = new PolicyApi(getClient())

        OktaSignOnPolicy policy = OktaSignOnPolicyBuilder.instance()
            .setName("java-sdk-it-" + UUID.randomUUID().toString())
            .setDescription("IT created Policy")
            .setStatus(LifecycleStatus.ACTIVE)
            .setType(PolicyType.OKTA_SIGN_ON)
            .buildAndCreate(policyApi) as OktaSignOnPolicy

        return policy
    }

    boolean isOIEEnvironment() {

        Map<String, Object> response = getClient().invokeAPI(
            "/.well-known/okta-organization",
            HttpMethod.GET.name(),
            new ArrayList<Pair>(),
            new ArrayList<Pair>(),
            null,
            null,
            new HashMap<String, String>(),
            new HashMap<String, String>(),
            new HashMap<String, Object>(),
            MediaType.APPLICATION_JSON_VALUE,
            null,
            new String[]{ "apiToken" },
            new TypeReference<Map<String, Object>>() {})

        if (response != null && response.containsKey("pipeline")) {
            String pipeline = response.get("pipeline")
            if (Objects.equals(pipeline, "idx")) {
                return true
            }
        }

        return false
    }
}