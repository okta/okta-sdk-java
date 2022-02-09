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
import com.okta.sdk.client.Client
import com.okta.sdk.client.Clients
import com.okta.sdk.impl.cache.DisabledCacheManager
import com.okta.sdk.resource.Resource
import com.okta.sdk.resource.ResourceException
import com.okta.sdk.resource.application.Application
import com.okta.sdk.resource.application.ApplicationLifecycleStatus
import com.okta.sdk.resource.authorization.server.AuthorizationServer
import com.okta.sdk.resource.common.Policy
import com.okta.sdk.resource.common.UserType
import com.okta.sdk.resource.event.hook.EventHook
import com.okta.sdk.resource.group.*
import com.okta.sdk.resource.identity.provider.IdentityProvider
import com.okta.sdk.resource.inline.hook.InlineHook
import com.okta.sdk.resource.linked.object.LinkedObject
import com.okta.sdk.resource.network.zone.NetworkZone
import com.okta.sdk.resource.template.SmsTemplate
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.TestResources
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

    private ThreadLocal<Client> threadLocal = new ThreadLocal<>()
    private ThreadLocal<String> testName = new ThreadLocal<>()
    private List<Resource> toBeDeleted = []

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
     * Registers a Resource to be cleaned up after the test is run.
     * @param resource Resource to be deleted.
     */
    void registerForCleanup(Resource resource) {
        toBeDeleted.add(resource)
    }

    void deleteUser(String email, Client client) {
        Util.ignoring(ResourceException) {
            User user = client.getUser(email)
            if (user.status != UserStatus.DEPROVISIONED) {
                // deactivate
                client.deactivateOrDeleteUser(user.getId())
            }
            // delete
            client.deactivateOrDeleteUser(user.getId())
        }
    }

    void deleteGroup(String groupName, Client client) {
        Util.ignoring(ResourceException) {
            GroupList groups = client.listGroups(groupName, null, null)
            groups.each {group ->
                if (groupName == group.profile.name) {
                    client.deleteGroup(group.getId())
                }
            }
        }
    }

    void deleteRule(String ruleName, Client client) {
        Util.ignoring(ResourceException) {
            GroupRuleList rules = client.listGroupRules()
            rules.each {rule ->
                if (ruleName == rule.name) {
                    if (rule.status == GroupRuleStatus.ACTIVE) {
                        client.deactivateGroupRule(rule.getId())
                    }
                    client.deleteGroupRule(rule.getId())
                }
            }
        }
    }

    @AfterMethod (groups = ["group1", "group2", "group3"])
    void clean() {
        if (!isRunningWithTestServer()) {
            Client client = getClient()

            // delete them in reverse order so dependencies are resolved
            toBeDeleted.reverse().each { resource ->
                try {
                    if (resource instanceof Application) {
                        if (resource.getStatus() != ApplicationLifecycleStatus.INACTIVE) {
                            client.deactivateApplication(resource.getId())
                        }
                        client.deleteApplication(resource.getId())
                    }
                    if (resource instanceof AuthorizationServer) {
                        client.deleteAuthorizationServer(resource.getId())
                    }
                    if (resource instanceof EventHook) {
                        client.deleteEventHook(resource.getId())
                    }
                    if (resource instanceof Group) {
                        client.deleteGroup(resource.getId())
                    }
                    if (resource instanceof GroupRule) {
                        client.deleteGroupRule(resource.getId())
                    }
                    if (resource instanceof IdentityProvider) {
                        client.deleteIdentityProvider(resource.getId())
                    }
                    if (resource instanceof InlineHook) {
                        client.deleteInlineHook(resource.getId())
                    }
                    if (resource instanceof LinkedObject) {
                        client.deleteLinkedObjectDefinition(resource.getPrimary().getName())
                    }
                    if (resource instanceof NetworkZone) {
                        client.deleteNetworkZone(resource.getId())
                    }
                    if (resource instanceof Policy) {
                        client.deletePolicy(resource.getId())
                    }
                    if (resource instanceof SmsTemplate) {
                        client.deleteSmsTemplate(resource.getId())
                    }
                    if (resource instanceof User) {
                        // deactivate first
                        client.deactivateOrDeleteUser(resource.getId())
                        // then delete
                        client.deactivateOrDeleteUser(resource.getId())
                    }
                    if (resource instanceof UserType) {
                        client.deleteUserType(resource.getId())
                    }
                }
                catch (Exception e) {
                    log.trace("Exception thrown during cleanup, it is ignored so the rest of the cleanup can be run:", e)
                }
            }
        }
    }

}
