/*
 * Copyright 2024-Present Okta, Inc.
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
package com.okta.sdk.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.SkipException;

import java.net.UnknownHostException;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import javax.net.ssl.SSLHandshakeException;

/**
 * TestNG listener that handles network-related test failures gracefully.
 * When a test fails due to network issues (UnknownHostException, SocketTimeoutException, etc.),
 * this listener marks the test as skipped instead of failed.
 * 
 * This is useful for integration tests that depend on external services which may be
 * temporarily unavailable due to network issues, DNS problems, or VPN disconnection.
 * 
 * @since 25.0.0
 */
public class NetworkAwareTestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(NetworkAwareTestListener.class);

    /**
     * System property to disable network error handling (tests will fail normally on network errors)
     */
    public static final String FAIL_ON_NETWORK_ERROR = "okta.it.failOnNetworkError";

    @Override
    public void onTestFailure(ITestResult result) {
        // Check if we should fail normally on network errors
        if (Boolean.getBoolean(FAIL_ON_NETWORK_ERROR) || 
            Boolean.parseBoolean(System.getenv("OKTA_IT_FAIL_ON_NETWORK_ERROR"))) {
            return; // Let the test fail normally
        }

        Throwable throwable = result.getThrowable();
        if (isNetworkError(throwable)) {
            String testName = result.getMethod().getMethodName();
            String errorType = getRootCause(throwable).getClass().getSimpleName();
            String errorMessage = getRootCause(throwable).getMessage();
            
            log.warn("Test '{}' failed due to network error ({}): {}. Marking as SKIPPED.", 
                     testName, errorType, errorMessage);
            
            // Change status to SKIP
            result.setStatus(ITestResult.SKIP);
            result.setThrowable(new SkipException(
                "Test skipped due to network connectivity issue: " + errorType + " - " + errorMessage));
        }
    }

    /**
     * Check if the exception (or its cause chain) indicates a network error.
     */
    private boolean isNetworkError(Throwable throwable) {
        if (throwable == null) {
            return false;
        }

        Throwable current = throwable;
        while (current != null) {
            if (current instanceof UnknownHostException ||
                current instanceof SocketTimeoutException ||
                current instanceof ConnectException ||
                current instanceof SSLHandshakeException ||
                isHttpHostConnectException(current)) {
                return true;
            }
            
            // Check message for common network error patterns
            String message = current.getMessage();
            if (message != null) {
                String lowerMessage = message.toLowerCase();
                if (lowerMessage.contains("unknownhostexception") ||
                    lowerMessage.contains("unknown host") ||
                    lowerMessage.contains("nodename nor servname provided") ||
                    lowerMessage.contains("connection timed out") ||
                    lowerMessage.contains("connect timed out") ||
                    lowerMessage.contains("read timed out") ||
                    lowerMessage.contains("connection refused") ||
                    lowerMessage.contains("no route to host") ||
                    lowerMessage.contains("network is unreachable")) {
                    return true;
                }
            }
            
            current = current.getCause();
        }
        
        return false;
    }

    /**
     * Check for Apache HttpClient's HttpHostConnectException without direct dependency.
     */
    private boolean isHttpHostConnectException(Throwable throwable) {
        String className = throwable.getClass().getName();
        return className.contains("HttpHostConnectException") ||
               className.contains("ConnectTimeoutException") ||
               className.contains("NoRouteToHostException");
    }

    /**
     * Get the root cause of an exception.
     */
    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }
}
