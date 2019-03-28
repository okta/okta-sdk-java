/*
 * Copyright 2019-Present Okta, Inc.
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
package com.okta.sdk.impl.http.httpclient

import org.testng.IHookCallBack
import org.testng.IHookable
import org.testng.ITestResult
import org.testng.SkipException
import org.testng.annotations.Test

import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.stream.Collectors

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class HttpClientRequestExecutorStaticConfigTest implements IHookable {

    private static final String REQUEST_EXECUTOR_CLASSNAME = "com.okta.sdk.impl.http.httpclient.HttpClientRequestExecutor"
    private static final String VALIDATE_AFTER_INACTIVITY_PROP = "com.okta.sdk.impl.http.httpclient.HttpClientRequestExecutor.connPoolControl.validateAfterInactivity"

    @Test
    void validateAfterInactivityTo4000() {

        System.properties.setProperty(VALIDATE_AFTER_INACTIVITY_PROP, "4000")
        def requestExecutor = isolatedClassLoader().loadClass(REQUEST_EXECUTOR_CLASSNAME)
        assertThat requestExecutor.CONNECTION_VALIDATION_INACTIVITY, is(4000)
    }

    @Test
    void validateAfterInactivityDefaultValue() {

        def requestExecutor = isolatedClassLoader().loadClass(REQUEST_EXECUTOR_CLASSNAME)
        assertThat requestExecutor.CONNECTION_VALIDATION_INACTIVITY, is(5000)
    }

    @Test
    void validateAfterInactivityInvalidValue() {

        System.properties.setProperty(VALIDATE_AFTER_INACTIVITY_PROP, "O'Doyle Rules!")
        def requestExecutor = isolatedClassLoader().loadClass(REQUEST_EXECUTOR_CLASSNAME)
        assertThat requestExecutor.CONNECTION_VALIDATION_INACTIVITY, is(5000) // default value
    }

    @Test
    void validateAfterInactivityIsZero() {
        System.properties.setProperty(VALIDATE_AFTER_INACTIVITY_PROP, "0")
        def requestExecutor = isolatedClassLoader().loadClass(REQUEST_EXECUTOR_CLASSNAME)
        assertThat requestExecutor.CONNECTION_VALIDATION_INACTIVITY, is(0)
    }

    @Test
    void validateAfterInactivityIsEmpty() {
        System.properties.setProperty(VALIDATE_AFTER_INACTIVITY_PROP, "")
        def requestExecutor = isolatedClassLoader().loadClass(REQUEST_EXECUTOR_CLASSNAME)
        assertThat requestExecutor.CONNECTION_VALIDATION_INACTIVITY, is(5000) // default value
    }

    static ClassLoader isolatedClassLoader() {
        def originalClassLoader = Thread.currentThread().getContextClassLoader()
        URLClassLoader oldClassLoader = (URLClassLoader) originalClassLoader
        return new URLClassLoader(oldClassLoader.getURLs(), (ClassLoader) null)
    }

    @Override
    void run(IHookCallBack callBack, ITestResult testResult) {
        def originalProperties = System.getProperties()
        Properties copy = new Properties()
        copy.putAll(originalProperties)
        System.setProperties(copy)
        try {
            // run the tests
            if (!System.getProperty("java.version").startsWith("1.8")) {
                throw new SkipException("Test test only supported on JDK 8, see https://github.com/okta/okta-sdk-java/issues/276")
            }
            callBack.runTestMethod(testResult)

        } finally {
            System.setProperties(originalProperties);
        }
    }
}
