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

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.okta.commons.lang.Strings
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.TestResources
import org.openapitools.client.ApiClient
import org.openapitools.client.api.ApplicationApi
import org.openapitools.client.api.IdentityProviderApi
import org.openapitools.client.api.UserApi
import org.openapitools.client.model.Application
import org.openapitools.client.model.ApplicationLifecycleStatus
import org.openapitools.client.model.IdentityProvider
import org.openapitools.client.model.LifecycleStatus
import org.openapitools.client.model.User
import org.openapitools.client.model.UserStatus
import org.openapitools.jackson.nullable.JsonNullableModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
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

//        String testServerBaseUrl = System.getProperty(TestServer.TEST_SERVER_BASE_URL)
//        if (isRunningWithTestServer() && scenarioId != null) {
//            return Clients.builder()
//                    .setOrgUrl(testServerBaseUrl + scenarioId)
//                    .setClientCredentials(new TokenClientCredentials("00ICU812"))
//                    .setCacheManager(new DisabledCacheManager()) // disable cache when using mock server
//                    .build()
//        }
//
//        ApiClient client = Clients.builder().build()
//        client.dataStore.requestExecutor.numRetries = 10
//        return client

        String orgUrl = System.getenv("OKTA_CLIENT_ORGURL")
        String apiKey = System.getenv("OKTA_CLIENT_TOKEN")

        ApiClient apiClient = new ApiClient(buildRestTemplate())
        apiClient.setBasePath(orgUrl)
        apiClient.setApiKey(apiKey)
        apiClient.setApiKeyPrefix("SSWS")

        return apiClient
    }

    private RestTemplate buildRestTemplate() {

        ObjectMapper objectMapper = new ObjectMapper()
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter(objectMapper)
        ObjectMapper mapper = messageConverter.getObjectMapper()
        messageConverter.setSupportedMediaTypes(Arrays.asList(
            MediaType.APPLICATION_JSON,
            MediaType.parseMediaType("application/x-pem-file"),
            MediaType.parseMediaType("application/x-x509-ca-cert"),
            MediaType.parseMediaType("application/pkix-cert")))
        mapper.registerModule(new JavaTimeModule())
        mapper.registerModule(new JsonNullableModule())

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>()
        messageConverters.add(messageConverter)

        RestTemplate restTemplate = new RestTemplate(messageConverters)
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()))

        return restTemplate
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
                testResources.users().each { email ->
                    deleteUser(email, client)
                }

//                testResources.groups().each { groupName ->
//                    deleteGroup(groupName, client)
//                }
//
//                testResources.rules().each { ruleName ->
//                    deleteRule(ruleName, client)
//                }
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

    void deleteApp(Application app, ApiClient client) {
        log.info("Deleting App: {} {}", app.getId(), app.getName())

        ApplicationApi applicationApi = new ApplicationApi(client)

        if (app.getStatus() == ApplicationLifecycleStatus.ACTIVE) {
            // deactivate
            applicationApi.deactivateApplication(app.getId())
        }
        // delete
        applicationApi.deleteApplication(app.getId())
    }

    void deleteUser(User user, ApiClient client) {
        log.info("Deleting User: {}", user.getProfile().getEmail())

        UserApi userApi = new UserApi(client)

        if (user.getStatus() != UserStatus.DEPROVISIONED) {
            // deactivate
            userApi.deactivateUser(user.id, false)
        }
        // delete
        userApi.deactivateOrDeleteUser(user.id, false)
    }

    void deleteIdp(IdentityProvider idp, ApiClient client) {
        log.info("Deleting IdP: {} {}", idp.getId(), idp.getName())

        IdentityProviderApi idpApi = new IdentityProviderApi(client)

        if (idp.getStatus() == LifecycleStatus.ACTIVE) {
            // deactivate
            idpApi.deactivateIdentityProvider(idp.getId())
        }

        // delete
        idpApi.deleteIdentityProvider(idp.getId())
    }

//    void deleteGroup(String groupName, Client client) {
//        Util.ignoring(ResourceException) {
//            GroupList groups = client.listGroups(groupName, null, null)
//            groups.each {group ->
//                if (groupName.equals(group.profile.name)) {
//                    group.delete()
//                }
//            }
//        }
//    }
//
//    void deleteRule(String ruleName, Client client) {
//        Util.ignoring(ResourceException) {
//            GroupRuleList rules = client.listGroupRules()
//            rules.each {rule ->
//                if (ruleName.equals(rule.name)) {
//                    if (rule.status == GroupRuleStatus.ACTIVE) {
//                        rule.deactivate()
//                    }
//                    rule.delete()
//                }
//            }
//        }
//    }
//
    @AfterMethod (groups = ["group1", "group2", "group3"])
    void clean() {
        if (!isRunningWithTestServer()) {
            // delete them in reverse order so dependencies are resolved
            toBeDeleted.reverse().each { deletable ->
                try {
                    if (deletable instanceof User) {
                        User tobeDeletedUser = (User) deletable
                        deleteUser(tobeDeletedUser, getClient())
                    }
                    else if (deletable instanceof Application) {
                        Application tobeDeletedApp = (Application) deletable
                        deleteApp(tobeDeletedApp, getClient())
                    }
                    else if (deletable instanceof IdentityProvider) {
                        IdentityProvider tobeDeletedIdp = (IdentityProvider) deletable
                        deleteIdp(tobeDeletedIdp, getClient())
                    }
                }
                catch (Exception e) {
                    log.trace("Exception thrown during cleanup, it is ignored so the rest of the cleanup can be run:", e)
                }
            }
        }
    }

}
