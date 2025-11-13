/*
 * Copyright 2025 Okta, Inc.
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
package com.okta.sdk.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class to track and warn about multiple ApiClient instance creation in multi-threaded applications.
 * This helps detect potential misuse of the SDK where multiple instances are created, which is not supported
 * due to shared cache manager state across all instances within the same JVM.
 *
 * @since 11.1.0
 */
public final class MultiThreadingWarningUtil {

    private static final Logger log = LoggerFactory.getLogger(MultiThreadingWarningUtil.class);
    private static final AtomicInteger instanceCounter = new AtomicInteger(0);
    private static final String WARNING_MESSAGE =
        "\n" +
        "================================================================================\n" +
        "WARNING: Multiple ApiClient instances detected (%d instances created)\n" +
        "================================================================================\n" +
        "Creating multiple ApiClient instances in a multi-threaded application is NOT\n" +
        "SUPPORTED and may lead to:\n" +
        "  - Cache inconsistencies\n" +
        "  - Memory leaks\n" +
        "  - Sub-optimal performance\n" +
        "\n" +
        "The SDK uses an internal cache manager that is shared across all ApiClient\n" +
        "instances within the same JVM. You MUST use a single ApiClient instance\n" +
        "throughout the entire lifecycle of your application.\n" +
        "\n" +
        "For more information, see:\n" +
        "https://github.com/okta/okta-sdk-java/blob/master/README.md#thread-safety\n" +
        "================================================================================\n";

    private MultiThreadingWarningUtil() {
        // Utility class, prevent instantiation
    }

    /**
     * Records the creation of an ApiClient instance and logs a warning if multiple instances are detected.
     * This method is thread-safe and uses an atomic counter to track instances.
     */
    public static void recordInstanceCreation() {
        int count = instanceCounter.incrementAndGet();
        
        if (count == 1) {
            log.info("ApiClient instance created. Remember: Use a single ApiClient instance throughout your application.");
        } else if (count == 2) {
            // First time we detect multiple instances, log a strong warning
            log.warn(String.format(WARNING_MESSAGE, count));
        } else {
            // Subsequent instances, log a simpler warning to avoid log spam
            log.warn("Multiple ApiClient instances detected ({} total). This is NOT SUPPORTED in multi-threaded applications. " +
                     "See: https://github.com/okta/okta-sdk-java/blob/master/README.md#thread-safety", count);
        }
    }

    /**
     * Gets the current count of ApiClient instances created in this JVM.
     * This is primarily useful for testing purposes.
     *
     * @return the number of ApiClient instances created
     */
    public static int getInstanceCount() {
        return instanceCounter.get();
    }

    /**
     * Resets the instance counter. This is primarily useful for testing purposes.
     * <b>WARNING:</b> This method should NOT be used in production code.
     */
    public static void resetInstanceCounter() {
        instanceCounter.set(0);
    }
}
