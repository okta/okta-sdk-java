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
package quickstart;

import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.client.ApiClient;

/**
 * Demo program to test multi-threading warnings.
 * This demonstrates what happens when multiple ApiClient instances are created.
 * 
 * Expected behavior:
 * - 1st instance: INFO log
 * - 2nd instance: Large WARNING banner
 * - 3rd+ instances: Simplified WARNING messages
 */
public class MultiThreadingWarningDemo {

    public static void main(String[] args) {
        System.out.println("\n========================================");
        System.out.println("Multi-Threading Warning Demo");
        System.out.println("========================================\n");

        // Scenario 1: Single instance (CORRECT usage)
        System.out.println("--- Scenario 1: Creating FIRST ApiClient instance (CORRECT) ---");
        ApiClient client1 = Clients.builder()
            .setOrgUrl("https://dev-12345.okta.com")
            .build();
        System.out.println("✅ First instance created successfully\n");

        // Wait a moment to see the log clearly
        sleep(1000);

        // Scenario 2: Second instance (INCORRECT - triggers WARNING)
        System.out.println("--- Scenario 2: Creating SECOND ApiClient instance (INCORRECT) ---");
        ApiClient client2 = Clients.builder()
            .setOrgUrl("https://dev-12345.okta.com")
            .build();
        System.out.println("⚠️  Second instance created - WARNING should be shown above\n");

        // Wait a moment
        sleep(1000);

        // Scenario 3: Third instance (INCORRECT - additional warning)
        System.out.println("--- Scenario 3: Creating THIRD ApiClient instance (INCORRECT) ---");
        ApiClient client3 = Clients.builder()
            .setOrgUrl("https://dev-12345.okta.com")
            .build();
        System.out.println("⚠️  Third instance created - WARNING should be shown above\n");

        // Wait a moment
        sleep(1000);

        // Scenario 4: Multiple instances in parallel threads (INCORRECT)
        System.out.println("--- Scenario 4: Creating instances in PARALLEL threads (INCORRECT) ---");
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int threadNum = i + 1;
            threads[i] = new Thread(() -> {
                System.out.println("Thread-" + threadNum + " creating ApiClient...");
                ApiClient clientInThread = Clients.builder()
                    .setOrgUrl("https://dev-12345.okta.com")
                    .build();
                System.out.println("Thread-" + threadNum + " instance created (hashCode: " + 
                    clientInThread.hashCode() + ")");
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted: " + e.getMessage());
            }
        }

        System.out.println("\n========================================");
        System.out.println("Demo Complete!");
        System.out.println("========================================");
        System.out.println("\nSummary:");
        System.out.println("- Created 6 ApiClient instances total (1 + 1 + 1 + 3 in threads)");
        System.out.println("- First instance: Should show INFO log");
        System.out.println("- Second instance: Should show large WARNING banner");
        System.out.println("- Remaining instances: Should show simplified WARNING messages");
        System.out.println("\n✅ CORRECT approach: Create ONLY ONE ApiClient instance");
        System.out.println("❌ INCORRECT approach: Creating multiple instances as shown above");
        System.out.println("\nNote: These instances reference different objects (see hashCodes)");
        System.out.println("      but share the same internal cache manager, causing issues!\n");
        
        // Show the instance details
        System.out.println("Instance details:");
        System.out.println("- client1 hashCode: " + client1.hashCode());
        System.out.println("- client2 hashCode: " + client2.hashCode());
        System.out.println("- client3 hashCode: " + client3.hashCode());
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
