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
package com.okta.sdk.impl.test;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

/**
 * TestNG Listener that will restore the system SecurityManager after test methods.
 *
 * Based on: https://github.com/stefanbirkner/system-rules/blob/master/src/main/java/org/junit/contrib/java/lang/system/ProvideSecurityManager.java
 */
public class RestoreSecurityManager implements IInvokedMethodListener {

    private SecurityManager manager;

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        manager = System.getSecurityManager();
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        System.setSecurityManager(manager);
    }
}
