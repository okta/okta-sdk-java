/*
 * Copyright 2025-Present Okta, Inc.
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
package com.okta.sdk.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class to detect and warn about multi-threaded usage of the Okta SDK.
 * 
 * <p>The Okta SDK stores per-thread pagination state keyed by thread ID. When multiple threads
 * use the same ApiClient instance, this can lead to:</p>
 * <ul>
 *   <li>Unexpected pagination behavior (threads interfering with each other's pagination state)</li>
 *   <li>Thread safety issues in thread pool environments</li>
 *   <li>Difficult-to-debug race conditions</li>
 * </ul>
 * 
 * <p>This utility tracks which threads access the SDK and emits warnings when multi-threaded
 * usage is detected. It is designed to have minimal performance impact and will only log
 * warnings, not prevent multi-threaded usage.</p>
 * 
 * @since 14.0.0
 */
public class MultiThreadingWarningUtil {
    
    private static final Logger log = LoggerFactory.getLogger(MultiThreadingWarningUtil.class);
    
    /**
     * Threshold for emitting warnings about the number of threads using the SDK.
     * If more than this many unique threads access the same ApiClient instance,
     * a warning will be logged.
     */
    private static final int THREAD_COUNT_WARNING_THRESHOLD = 3;
    
    /**
     * Maximum number of thread IDs to track before we stop tracking new ones.
     * This prevents unbounded memory growth in the tracking set itself.
     */
    private static final int MAX_TRACKED_THREADS = 1000;
    
    /**
     * Set of thread IDs that have accessed this ApiClient instance.
     * We use a ConcurrentHashMap as a thread-safe set.
     */
    private final Set<Long> accessedThreadIds = ConcurrentHashMap.newKeySet();
    
    /**
     * Counter for the number of unique threads that have accessed this ApiClient.
     */
    private final AtomicInteger uniqueThreadCount = new AtomicInteger(0);
    
    /**
     * Flag to track if we've already emitted the initial multi-threading warning.
     * We only want to warn once, not on every access.
     */
    private final AtomicBoolean multiThreadWarningEmitted = new AtomicBoolean(false);
    
    /**
     * Flag to track if we've already emitted the thread pool warning.
     */
    private final AtomicBoolean threadPoolWarningEmitted = new AtomicBoolean(false);
    
    /**
     * Flag to track if we've reached the max tracked threads limit.
     */
    private final AtomicBoolean maxThreadsReachedWarningEmitted = new AtomicBoolean(false);
    
    /**
     * Records that the current thread has accessed the SDK and checks if warnings should be emitted.
     * 
     * <p>This method should be called at key points in the SDK lifecycle where thread state is
     * being used (e.g., when storing or retrieving pagination state).</p>
     */
    public void recordThreadAccess() {
        long currentThreadId = Thread.currentThread().getId();
        String currentThreadName = Thread.currentThread().getName();
        
        // Bail out early if we've already hit the cap to avoid churn
        if (uniqueThreadCount.get() >= MAX_TRACKED_THREADS) {
            if (maxThreadsReachedWarningEmitted.compareAndSet(false, true)) {
                log.warn(
                    "OKTA SDK WARNING: Maximum tracked threads ({}) exceeded. " +
                    "Stopping thread tracking to prevent memory issues. " +
                    "This indicates excessive thread churn. " +
                    "The Okta SDK is not designed for high-concurrency, multi-threaded usage with a single ApiClient instance.",
                    MAX_TRACKED_THREADS
                );
            }
            return;
        }

        // Deduplicate fast path
        if (!accessedThreadIds.add(currentThreadId)) {
            return;
        }

        // Atomically "increment if below MAX"
        int previousCount = uniqueThreadCount.getAndUpdate(count ->
            count >= MAX_TRACKED_THREADS ? count : count + 1
        );

        if (previousCount >= MAX_TRACKED_THREADS) {
            // We didn't bump the counter, so drop the ID and warn once
            accessedThreadIds.remove(currentThreadId);
            if (maxThreadsReachedWarningEmitted.compareAndSet(false, true)) {
                log.warn(
                    "OKTA SDK WARNING: Maximum tracked threads ({}) exceeded. " +
                    "Stopping thread tracking to prevent memory issues. " +
                    "This indicates excessive thread churn. " +
                    "The Okta SDK is not designed for high-concurrency, multi-threaded usage with a single ApiClient instance.",
                    MAX_TRACKED_THREADS
                );
            }
            return;
        }

        int threadCount = previousCount + 1; // safe: we know we actually incremented

        log.debug("New thread detected accessing Okta SDK: {} (ID: {}). Total unique threads: {}",
            currentThreadName, currentThreadId, threadCount);

        // Emit warning if we've crossed the threshold
        if (threadCount > THREAD_COUNT_WARNING_THRESHOLD) {
            if (multiThreadWarningEmitted.compareAndSet(false, true)) {
                emitMultiThreadingWarning(threadCount);
            }
        }

        // Check if this looks like a thread pool pattern
        if (threadCount > 1 && isLikelyThreadPoolThread(currentThreadName)) {
            if (threadPoolWarningEmitted.compareAndSet(false, true)) {
                emitThreadPoolWarning();
            }
        }
    }
    
    /**
     * Returns the number of unique threads that have accessed this ApiClient instance.
     * 
     * @return the count of unique threads
     */
    public int getUniqueThreadCount() {
        return uniqueThreadCount.get();
    }
    
    /**
     * Checks if a thread name suggests it's from a thread pool.
     * Common patterns include names like "pool-1-thread-5", "http-nio-8080-exec-3",
     * "ForkJoinPool.commonPool-worker-1", etc.
     * 
     * @param threadName the name of the thread
     * @return true if the thread name suggests it's from a thread pool
     */
    private boolean isLikelyThreadPoolThread(String threadName) {
        if (threadName == null) {
            return false;
        }
        
        String lowerName = threadName.toLowerCase();
        return lowerName.contains("pool") ||
               lowerName.contains("worker") ||
               lowerName.contains("executor") ||
               lowerName.contains("thread-") ||
               lowerName.contains("exec-");
    }
    
    /**
     * Emits a warning about multi-threaded usage of the SDK.
     */
    private void emitMultiThreadingWarning(int threadCount) {
        log.warn(
            "\n" +
            "================================================================================\n" +
            "OKTA SDK MULTI-THREADING WARNING\n" +
            "================================================================================\n" +
            "The Okta SDK has detected that {} unique threads are accessing the same\n" +
            "ApiClient instance. The SDK stores pagination metadata per thread and is\n" +
            "NOT designed for concurrent, multi-threaded usage patterns.\n" +
            "\n" +
            "POTENTIAL ISSUES:\n" +
            "  - Pagination may behave unexpectedly when multiple threads make requests\n" +
            "  - Thread state may interfere between concurrent operations\n" +
            "  - In thread pool environments, per-thread state persists across requests\n" +
            "\n" +
            "RECOMMENDATIONS:\n" +
            "  1. Use a separate ApiClient instance per thread (thread-local pattern)\n" +
            "  2. Synchronize access to a shared ApiClient instance (not recommended for\n" +
            "     high-concurrency scenarios due to performance impact)\n" +
            "  3. Avoid using the SDK's collection/pagination methods in multi-threaded\n" +
            "     contexts where threads share an ApiClient instance\n" +
            "\n" +
            "For more information, see: https://github.com/okta/okta-sdk-java/issues/1637\n" +
            "================================================================================\n",
            threadCount
        );
    }
    
    /**
     * Emits a warning specific to thread pool usage.
     */
    private void emitThreadPoolWarning() {
        log.warn(
            "\n" +
            "================================================================================\n" +
            "OKTA SDK THREAD POOL WARNING\n" +
            "================================================================================\n" +
            "The Okta SDK has detected thread pool usage (e.g., in a web server or\n" +
            "async framework). This usage pattern has specific concerns:\n" +
            "\n" +
            "THREAD POOL ISSUE:\n" +
            "  - Thread pool threads are reused and live for the lifetime of the application\n" +
            "  - Per-thread state from one request can leak into subsequent requests\n" +
            "    handled by the same thread\n" +
            "  - Pagination state from Request A might unexpectedly affect Request B\n" +
            "\n" +
            "RECOMMENDED PATTERNS FOR WEB SERVERS:\n" +
            "  1. Create a new ApiClient per request (safest, but has overhead)\n" +
            "  2. Use a thread-local ApiClient pattern:\n" +
            "     \n" +
            "     private static final ThreadLocal<ApiClient> CLIENT_HOLDER =\n" +
            "         ThreadLocal.withInitial(() -> createNewApiClient());\n" +
            "     \n" +
            "  3. If using Spring, consider request-scoped beans\n" +
            "  4. Avoid storing pagination state across HTTP requests\n" +
            "\n" +
            "For more information, see: https://github.com/okta/okta-sdk-java/issues/1637\n" +
            "================================================================================\n"
        );
    }
    
    /**
     * Resets all tracking state. This is primarily useful for testing.
     * 
     * <p><strong>WARNING:</strong> This method should not be called in production code.
     * It exists solely for testing purposes.</p>
     */
    public void reset() {
        accessedThreadIds.clear();
        uniqueThreadCount.set(0);
        multiThreadWarningEmitted.set(false);
        threadPoolWarningEmitted.set(false);
        maxThreadsReachedWarningEmitted.set(false);
    }
}
