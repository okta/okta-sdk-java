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
import com.okta.sdk.authc.credentials.TokenClientCredentials
import com.okta.sdk.client.AuthorizationMode
import com.okta.sdk.client.Client
import com.okta.sdk.client.Clients
import com.okta.sdk.impl.cache.DisabledCacheManager
import com.okta.sdk.resource.Deletable
import com.okta.sdk.resource.ResourceException
import com.okta.sdk.resource.application.Application
import com.okta.sdk.resource.group.GroupList
import com.okta.sdk.resource.group.rule.GroupRule
import com.okta.sdk.resource.group.rule.GroupRuleList
import com.okta.sdk.resource.group.rule.GroupRuleStatus
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserStatus
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.TestResources
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.IHookCallBack
import org.testng.IHookable
import org.testng.ITestNGMethod
import org.testng.ITestResult
import org.testng.annotations.AfterMethod
import org.testng.annotations.Listeners


/**
 * Creates a thread local client for a test method to use. The client may be connected to an actual Okta instance or a Test Server.
 */
@Listeners(ClientProvider)
trait ClientProvider implements IHookable {

    private Logger log = LoggerFactory.getLogger(ClientProvider)

    private ThreadLocal<Client> threadLocal = new ThreadLocal<>()
    private ThreadLocal<String> testName = new ThreadLocal<>()
    private List<Deletable> toBeDeleted = []

    Client getClient(String scenarioId = null) {
        Client client = threadLocal.get()
        if (client == null) {
            threadLocal.set(buildClient(scenarioId))
        }
        return threadLocal.get()
    }

    private isRunningWithTestServer() {
        return Strings.hasText(System.getProperty(TestServer.TEST_SERVER_BASE_URL))
    }

    private Client buildClient(String scenarioId = null) {

        String testServerBaseUrl = System.getProperty(TestServer.TEST_SERVER_BASE_URL)
        if (isRunningWithTestServer() && scenarioId != null) {
            return Clients.builder()
                    .setOrgUrl(testServerBaseUrl + scenarioId)
                    .setClientCredentials(new TokenClientCredentials("00ICU812"))
                    .setCacheManager(new DisabledCacheManager()) // disable cache when using mock server
                    .build()
        }

        Client client = Clients.builder().build()
        client.dataStore.requestExecutor.numRetries = 10
        return client
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

            Client client = getClient(scenarioId)

            if (!isRunningWithTestServer() && testResources != null) {
                // delete any users that may collide with the test that is about to run
                testResources.users().each { email ->
                    deleteUser(email, client)
                }

                testResources.groups().each { groupName ->
                    deleteGroup(groupName, client)
                }

                testResources.rules().each { ruleName ->
                    deleteRule(ruleName, client)
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
     * @param deletable Resource to be deleted.
     */
    void registerForCleanup(Deletable deletable) {
        toBeDeleted.add(deletable)
    }

    void deleteUser(String email, Client client) {
        Util.ignoring(ResourceException) {
            User user = client.getUser(email)
            if (user.status != UserStatus.DEPROVISIONED) {
                user.deactivate()
            }
            user.delete()
        }
    }

    void deleteGroup(String groupName, Client client) {
        Util.ignoring(ResourceException) {
            GroupList groups = client.listGroups(groupName, null, null)
            groups.each {group ->
                if (groupName.equals(group.profile.name)) {
                    group.delete()
                }
            }
        }
    }

    void deleteRule(String ruleName, Client client) {
        Util.ignoring(ResourceException) {
            GroupRuleList rules = client.listRules()
            rules.each {rule ->
                if (ruleName.equals(rule.name)) {
                    if (rule.status == GroupRuleStatus.ACTIVE) {
                        rule.deactivate()
                    }
                    rule.delete()
                }
            }
        }
    }

    @AfterMethod
    void clean() {
        if (!isRunningWithTestServer()) {
            // delete them in reverse order so dependencies are resolved
            toBeDeleted.reverse().each { deletable ->
                try {
                    if (deletable instanceof User) {
                        deletable.deactivate()
                    } else if (deletable instanceof GroupRule) {
                        deletable.deactivate()
                    } else if (deletable instanceof Application) {
                        deletable.deactivate()
                    }
                    deletable.delete()
                }
                catch (Exception e) {
                    log.trace("Exception thrown during cleanup, it is ignored so the rest of the cleanup can be run:", e)
                }
            }
        }
    }

}
