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
import com.okta.sdk.client.Clients
import com.okta.sdk.resource.Deletable
import com.okta.sdk.resource.ResourceException
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserStatus
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.TestResources
import org.testng.Assert
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

    private ThreadLocal<Client> threadLocal = new ThreadLocal<>()
    private Collection<Deletable> toBeDeleted = []

    Client getClient() {
        Client client = threadLocal.get()
        if (client == null) {
            client = Clients.builder().build()
        }
        return client
    }

    @Override
    void run(IHookCallBack callBack, ITestResult testResult) {

        // Gets the current scenario (if one is defined)
        Scenario scenario = testResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Scenario)
        // Gets test resource if defined
        TestResources testResources = testResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestResources)

        try {
            // setup the scenario
            if (scenario != null) {
                threadLocal.set(null)
            }

            Client client = getClient()

            // delete any users that may collide with the test that is about to run
            testResources.users().each {email ->
                deleteUser(email, client)
            }

            // run the tests
            callBack.runTestMethod(testResult)
        }
        finally {
            // cleanup the thread local
            threadLocal.remove()
        }
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

    @AfterMethod
    void clean() {
        boolean exceptionThrown = false
        toBeDeleted.each {deletable ->
            try {
                if (deletable instanceof User) {
                    deletable.deactivate()
                }
                deletable.delete()
            }
            catch (Exception e) {
                System.err.println("Exception thrown during cleanup, it is ignored so the rest of the cleanup can be run")
                e.printStackTrace()
            }
        }
        if (exceptionThrown) {
            Assert.fail("Exception thrown during cleanup, see above exceptions")
        }
    }

}
