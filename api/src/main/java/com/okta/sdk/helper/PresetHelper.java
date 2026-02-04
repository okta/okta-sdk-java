/*
 * Copyright 2025-Present, Okta, Inc.
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

package com.okta.sdk.helper;

import com.okta.sdk.resource.client.ApiClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import java.io.InputStream;
import java.util.Properties;

/**
 * Helper class to create and configure Okta API clients for testing.
 * 
 * Supports configuration from multiple sources:
 * <ul>
 * <li>okta.yaml in classpath</li>
 * <li>~/.okta/okta.yaml</li>
 * <li>Environment variables (OKTA_CLIENT_ORGURL, OKTA_CLIENT_TOKEN)</li>
 * <li>System properties (okta.client.orgurl, okta.client.token)</li>
 * </ul>
 * 
 * Usage:
 *   PresetHelper helper = new PresetHelper();
 *   ApiClient client = helper.getApiClient();
 */
public class PresetHelper {

    private ApiClient apiClient;

    public PresetHelper() {
        initializeApiClient();
    }

    private void initializeApiClient() {
        try {
            // Load configuration from environment
            // Note: test-setup.sh passes -Dokta.client.orgurl (lowercase) via Maven
            String orgUrl = getConfigValue("okta.client.orgurl", "OKTA_CLIENT_ORGURL");
            String apiToken = getConfigValue("okta.client.token", "OKTA_CLIENT_TOKEN");
            
            if (orgUrl == null || orgUrl.isEmpty()) {
                throw new IllegalArgumentException("Okta org URL not found");
            }
            if (apiToken == null || apiToken.isEmpty()) {
                throw new IllegalArgumentException("Okta API token not found");
            }
            
            // Create ApiClient with HttpClient and a simple no-op cache manager
            // We pass a test cache manager to avoid depending on okta-sdk-impl
            org.apache.hc.client5.http.impl.classic.CloseableHttpClient httpClient = 
                org.apache.hc.client5.http.impl.classic.HttpClients.createDefault();
            
            this.apiClient = new ApiClient(
                httpClient,
                new NoOpCacheManager()  // Simple test cache manager
            );
            this.apiClient.setBasePath(orgUrl);
            // Set API key with SSWS prefix for Okta API token authentication
            this.apiClient.setApiKeyPrefix("SSWS");
            this.apiClient.setApiKey(apiToken);
            
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to initialize Okta API client. Ensure one of the following:\n" +
                "  1. Create ~/.okta/okta.yaml with okta.client.orgUrl and okta.client.token\n" +
                "  2. Create okta.yaml in classpath with okta configuration\n" +
                "  3. Set environment variables: OKTA_CLIENT_ORGURL and OKTA_CLIENT_TOKEN\n" +
                "  4. Set system properties: okta.client.orgurl and okta.client.token", e
            );
        }
    }

    /**
     * Get configuration value from system property or environment variable.
     * 
     * @param propertyName System property name
     * @param envVarName Environment variable name
     * @return Configuration value or null if not found
     */
    private String getConfigValue(String propertyName, String envVarName) {
        // First check system properties
        String value = System.getProperty(propertyName);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        
        // Then check environment variables
        value = System.getenv(envVarName);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        
        return null;
    }

    /**
     * Get the configured API client instance.
     * 
     * @return ApiClient configured with Okta credentials
     */
    public ApiClient getApiClient() {
        return apiClient;
    }

    /**
     * Simple no-op cache manager for testing.
     * Implements CacheManager interface but doesn't actually cache anything.
     */
    private static class NoOpCacheManager implements com.okta.sdk.cache.CacheManager {
        @Override
        public <K, V> com.okta.sdk.cache.Cache<K, V> getCache(String name) {
            return new NoOpCache<>();
        }
    }

    /**
     * Simple no-op cache for testing.
     * Implements Cache interface but doesn't actually cache anything.
     */
    private static class NoOpCache<K, V> implements com.okta.sdk.cache.Cache<K, V> {
        @Override
        public V get(K key) {
            return null;
        }

        @Override
        public V put(K key, V value) {
            return null;
        }

        @Override
        public V remove(K key) {
            return null;
        }
    }
}
