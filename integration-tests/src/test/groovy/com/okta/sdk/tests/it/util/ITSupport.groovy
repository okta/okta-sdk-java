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

import com.okta.sdk.tests.Scenario
import org.testng.ITestContext
import org.testng.annotations.AfterSuite
import org.testng.annotations.BeforeSuite

abstract class ITSupport implements ClientProvider {

    public static final USE_TEST_SERVER = "okta.use.testServer"

    private TestServer testServer

    @BeforeSuite
    void start(ITestContext context) {

        boolean useTestServer = Boolean.getBoolean(USE_TEST_SERVER) ||
                Boolean.valueOf(System.getenv("OKTA_USE_TEST_SERVER"))

        if (useTestServer) {
            List<String> scenarios = []
            context.getAllTestMethods().each {testMethod ->
                Scenario scenario = testMethod.getConstructorOrMethod().getMethod().getAnnotation(Scenario)
                if (scenario != null) {
                    scenarios.add(scenario.value())
                }
            }

            testServer = new TestServer().start(scenarios, System.getProperty("okta.testServer.bin", "node_modules/.bin/okta-sdk-test-server"))
        }
    }

    @AfterSuite()
    void stop() {
        if (testServer != null) {
            testServer.stop()
        }
    }

}
