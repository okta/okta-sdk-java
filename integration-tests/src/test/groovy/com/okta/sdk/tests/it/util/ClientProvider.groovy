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

import com.okta.commons.lang.Strings
import com.okta.sdk.client.Clients
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.TestResources
import org.openapitools.client.ApiClient
import org.openapitools.client.api.*
import org.openapitools.client.model.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.IHookCallBack
import org.testng.IHookable
import org.testng.ITestResult
import org.testng.annotations.AfterMethod
import org.testng.annotations.Listeners

/**
 * Creates a thread local client for a test method to use. The client may be connected to an actual Okta instance or a Test Server.
 */
@Listeners(ClientProvider)
trait ClientProvider implements IHookable {

    private Logger log = LoggerFactory.getLogger(ClientProvider)

    private ThreadLocal<ApiClient> threadLocal = new ThreadLocal<>()
    private ThreadLocal<String> testName = new ThreadLocal<>()
    private List<Object> toBeDeleted = []

    ApiClient getClient(String scenarioId = null) {
        ApiClient client = threadLocal.get()
        if (client == null) {
            threadLocal.set(buildClient())
        }
        return threadLocal.get()
    }

    private isRunningWithTestServer() {
        return Strings.hasText(System.getProperty(TestServer.TEST_SERVER_BASE_URL))
    }

    private ApiClient buildClient() {

        ApiClient apiClient = Clients.builder().build()
        //apiClient.setDebugging(true)
        return apiClient
    }

    @Override
    void run(IHookCallBack callBack, ITestResult testResult) {

        testName.set(testResult.name)

        // Gets the current scenario (if one is defined)
        Scenario scenario = testResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Scenario)
        // Gets test resource if defined
        TestResources testResources = testResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestResources)

        try {
            // setup the scenario
            String scenarioId = null
            if (scenario != null) {
                if (scenario != null) scenarioId = scenario.value()
            }

            ApiClient client = getClient(scenarioId)

            if (!isRunningWithTestServer() && testResources != null) {
                // delete any users that may collide with the test that is about to run
                testResources.users().each { id ->
                    deleteUser(id, client)
                }

                testResources.groups().each { id ->
                    deleteGroup(id, client)
                }

                testResources.rules().each { id ->
                    deleteGroupRule(id, client)
                }
            }
            // run the tests
            callBack.runTestMethod(testResult)
        }
        finally {
            // cleanup the thread local
            threadLocal.remove()
            testName.remove()
        }
    }

    def getTestName() {
        return "java-sdk-" + testName.get()
    }

    def getUniqueTestName() {
        return "${getTestName()}-${UUID.randomUUID()}"
    }

    /**
     * Registers a Deletable to be cleaned up after the test is run.
     */
    void registerForCleanup(Object deletable) {
        toBeDeleted.add(deletable)
    }

    void deleteApp(String id, ApiClient client) {
        log.info("Deleting App: {}", id)

        ApplicationApi applicationApi = new ApplicationApi(client)
        Application appToDelete = applicationApi.getApplication(id, null)

        if (appToDelete != null) {
            if (appToDelete.getStatus() == "ACTIVE") {
                // deactivate
                applicationApi.deactivateApplication(appToDelete.getId())
            }
            // delete
            applicationApi.deleteApplication(appToDelete.getId())
        }
    }

    void deleteUser(String id, ApiClient client) {
        log.info("Deleting User: {}", id)
        UserApi userApi = new UserApi(client)
        User userToDelete = userApi.getUser(id)

        if (userToDelete != null) {
            if (userToDelete.getStatus() != "DEPROVISIONED") {
                // deactivate
                userApi.deactivateUser(userToDelete.getId(), false)
            }
            // delete
            userApi.deactivateOrDeleteUser(userToDelete.getId(), false)
        }
    }

    void deleteUserType(String id, ApiClient client) {
        log.info("Deleting UserType: {}", id)
        UserTypeApi userTypeApi= new UserTypeApi(client)
        UserType userTypeToDelete = userTypeApi.getUserType(id)

        if (userTypeToDelete != null) {
            userTypeApi.deleteUserType(userTypeToDelete.getId())
        }
    }

    void deleteGroup(String id, ApiClient client) {
        log.info("Deleting Group: {}", id)
        GroupApi groupApi = new GroupApi(client)
        Group groupToDelete = groupApi.getGroup(id)

        if (groupToDelete != null) {
            groupApi.deleteGroup(groupToDelete.getId())
        }
    }

    void deleteGroupRule(String id, ApiClient client) {
        log.info("Deleting GroupRule: {}", id)
        GroupApi groupApi = new GroupApi(client)
        GroupRule groupRuleToDelete = groupApi.getGroupRule(id, null)

        if (groupRuleToDelete != null) {
            groupApi.deleteGroup(groupRuleToDelete.getId())
        }
    }

    void deleteIdp(String id, ApiClient client) {
        log.info("Deleting IdP: {}", id)
        IdentityProviderApi idpApi = new IdentityProviderApi(client)
        IdentityProvider idpToDelete = idpApi.getIdentityProvider(id)

        if (idpToDelete != null) {
            if (idpToDelete.getStatus() == "ACTIVE") {
                // deactivate
                idpApi.deactivateIdentityProvider(idpToDelete.getId())
            }
            // delete
            idpApi.deleteIdentityProvider(idpToDelete.getId())
        }
    }

    void deleteInlineHook(String id, ApiClient client) {
        log.info("Deleting InlineHook: {}", id)
        InlineHookApi inlineHookApi = new InlineHookApi(client)
        InlineHook inlineHookToDelete = inlineHookApi.getInlineHook(id)

        if (inlineHookToDelete != null) {
            if (inlineHookToDelete.getStatus() == "ACTIVE") {
                // deactivate
                inlineHookApi.deactivateInlineHook(inlineHookToDelete.getId())
            }
            // delete
            inlineHookApi.deleteInlineHook(inlineHookToDelete.getId())
        }
    }

    void deletePolicy(String id, ApiClient client) {
        log.info("Deleting Policy: {}", id)
        PolicyApi policyApi = new PolicyApi(client)
        Policy policyToDelete = policyApi.getPolicy(id, "false")

        if (policyToDelete != null) {
            if (policyToDelete.getStatus() == "ACTIVE") {
                // deactivate
                policyApi.deactivatePolicy(policyToDelete.getId())
            }
            // delete
            policyApi.deletePolicy(policyToDelete.getId())
        }
    }

    void deletePolicyRule(String policyId, String ruleId, ApiClient client) {
        log.info("Deleting PolicyRule: policyId {}, ruleId: {}", policyId, ruleId)
        PolicyApi policyApi = new PolicyApi(client)
        policyApi.deletePolicyRule(policyId, ruleId)
    }

    @AfterMethod (groups = ["group1", "group2", "group3"])
    void clean() {
        if (!isRunningWithTestServer()) {
            // delete them in reverse order so dependencies are resolved
            toBeDeleted.reverse().each { deletable ->
                try {
                    if (deletable instanceof User) {
                        User tobeDeletedUser = (User) deletable
                        deleteUser(tobeDeletedUser.getId(), getClient())
                    }
                    else if (deletable instanceof UserType) {
                        UserType tobeDeletedUserType = (UserType) deletable
                        deleteUserType(tobeDeletedUserType.getId(), getClient())
                    }
                    else if (deletable instanceof Group) {
                        Group tobeDeletedGroup = (Group) deletable
                        deleteGroup(tobeDeletedGroup.getId(), getClient())
                    }
                    else if (deletable instanceof GroupRule) {
                        GroupRule tobeDeletedGroupRule = (GroupRule) deletable
                        deleteGroupRule(tobeDeletedGroupRule.getId(), getClient())
                    }
                    else if (deletable instanceof Application) {
                        Application tobeDeletedApp = (Application) deletable
                        deleteApp(tobeDeletedApp.getId(), getClient())
                    }
                    else if (deletable instanceof IdentityProvider) {
                        IdentityProvider tobeDeletedIdp = (IdentityProvider) deletable
                        deleteIdp(tobeDeletedIdp.getId(), getClient())
                    }
                    else if (deletable instanceof InlineHook) {
                        InlineHook tobeDeletedInlineHook = (InlineHook) deletable
                        deleteInlineHook(tobeDeletedInlineHook.getId(), getClient())
                    }
                    else if (deletable instanceof Policy) {
                        Policy tobeDeletedPolicy = (Policy) deletable
                        deletePolicy(tobeDeletedPolicy.getId(), getClient())
                    }
                }
                catch (Exception e) {
                    log.trace("Exception thrown during cleanup, it is ignored so the rest of the cleanup can be run:", e)
                }
            }
        }
    }

}
