/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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

import com.okta.commons.lang.Classes;

/**
 * Static utility/helper class for working with {@link com.okta.sdk.resource.client.ApiClient} resources. For example:
 * <pre>
 * <b>Clients.builder()</b>
 *     // ... etc ...
 *     .setProxy(new Proxy("192.168.2.120", 9001))
 *     .build();
 * </pre>
 *
 * <p>See the {@link ClientBuilder ClientBuilder} JavaDoc for extensive documentation on client configuration.</p>
 *
 * <h3>Multi-Threading Considerations</h3>
 * <p>The Okta SDK uses thread-local state for pagination and response tracking. This design has important
 * implications for multi-threaded applications:</p>
 *
 * <h4>Memory Leak Fix (v14.0.0+)</h4>
 * <p>As of version 14.0.0, the SDK uses {@link ThreadLocal} instead of {@link java.util.concurrent.ConcurrentHashMap}
 * to store thread state. This prevents memory leaks in applications with high thread churn. However, in
 * thread pool environments (e.g., web servers), {@code ThreadLocal} state persists for the lifetime of
 * the thread, which may be the lifetime of the application.</p>
 *
 * <h4>Thread Safety Warning</h4>
 * <p><strong>The Okta SDK is NOT designed for concurrent, multi-threaded usage with a shared ApiClient instance.</strong>
 * Using the same ApiClient instance from multiple threads can lead to:</p>
 * <ul>
 *   <li>Unexpected pagination behavior (threads may see each other's pagination state)</li>
 *   <li>Race conditions in thread pool environments where state leaks between requests</li>
 *   <li>Difficult-to-debug issues in high-concurrency scenarios</li>
 * </ul>
 *
 * <h4>Recommended Patterns</h4>
 * <p>For multi-threaded applications, use one of these patterns:</p>
 *
 * <h5>1. Thread-Local ApiClient (Recommended for Thread Pools)</h5>
 * <pre>
 * private static final ThreadLocal&lt;ApiClient&gt; CLIENT_HOLDER =
 *     ThreadLocal.withInitial(() -&gt; Clients.builder()...build());
 *
 * public void handleRequest() {
 *     ApiClient client = CLIENT_HOLDER.get();
 *     // Use client...
 * }
 * </pre>
 *
 * <h5>2. Per-Request ApiClient (Safest)</h5>
 * <pre>
 * public void handleRequest() {
 *     ApiClient client = Clients.builder()...build();
 *     // Use client...
 * }
 * </pre>
 *
 * <h5>3. Spring Request-Scoped Bean</h5>
 * <pre>
 * {@literal @}Bean
 * {@literal @}Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
 * public ApiClient oktaClient() {
 *     return Clients.builder()...build();
 * }
 * </pre>
 *
 * <h5>4. Synchronized Access (Not Recommended for High Concurrency)</h5>
 * <pre>
 * private final ApiClient client = Clients.builder()...build();
 *
 * public synchronized void makeRequest() {
 *     // Use client...
 * }
 * </pre>
 *
 * <h4>Thread Count Monitoring</h4>
 * <p>The SDK automatically detects and logs warnings when multiple threads access the same ApiClient instance.
 * You can also programmatically check the thread count:</p>
 * <pre>
 * ApiClient client = Clients.builder()...build();
 * int threadCount = client.getUniqueThreadCount();
 * if (threadCount &gt; 1) {
 *     // Multiple threads are using this client
 * }
 * </pre>
 *
 * <p>For more information, see: <a href="https://github.com/okta/okta-sdk-java/issues/1637">GitHub Issue #1637</a></p>
 *
 * @see ClientBuilder
 * @since 0.5.0
 */
public final class Clients {

    /**
     * Returns new {@link ClientBuilder} instance, used to construct {@link com.okta.sdk.resource.client.ApiClient} instances.
     *
     * @return new {@link ClientBuilder} instance, used to construct {@link com.okta.sdk.resource.client.ApiClient} instances.
     */
    public static ClientBuilder builder() {
        return (ClientBuilder) Classes.newInstance("com.okta.sdk.impl.client.DefaultClientBuilder");
    }

}

