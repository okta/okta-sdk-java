/*
 * Copyright 2025 Okta
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
package quickstart;

import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.client.ApiClient;

/**
 * Standalone example demonstrating multi-threading warning system.
 * Run this class directly to see the automatic warnings in action.
 */
public class MultiThreadingWarningExample {

    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("DEMO: Multi-Threading Warning System");
        System.out.println("=".repeat(80));
        System.out.println("Creating multiple ApiClient instances will trigger automatic warnings.");
        System.out.println("Watch for INFO and WARN logs from MultiThreadingWarningUtil below:");
        System.out.println("=".repeat(80) + "\n");

        System.out.println("Creating 1st ApiClient instance...");
        System.out.println("  --> Expect: INFO log about using single instance");
        ApiClient client1 = Clients.builder().build();
        
        System.out.println("\nCreating 2nd ApiClient instance...");
        System.out.println("  --> Expect: WARNING banner about multiple instances detected");
        ApiClient client2 = Clients.builder().build();
        
        System.out.println("\nCreating 3rd ApiClient instance...");
        System.out.println("  --> Expect: Simplified WARN message");
        ApiClient client3 = Clients.builder().build();

        System.out.println("\n" + "=".repeat(80));
        System.out.println("Multi-Threading Warning Demo Complete!");
        System.out.println("=".repeat(80));
        System.out.println("\nNOTE: In production applications:");
        System.out.println("  - Create only ONE ApiClient instance");
        System.out.println("  - Reuse it throughout your application lifecycle");
        System.out.println("  - The SDK's cache manager is shared across all instances in the JVM");
        System.out.println("=".repeat(80) + "\n");
    }
}
