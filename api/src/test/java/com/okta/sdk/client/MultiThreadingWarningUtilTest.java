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

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
 * Unit tests for {@link MultiThreadingWarningUtil}.
 */
public class MultiThreadingWarningUtilTest {
    
    private MultiThreadingWarningUtil util;
    
    @BeforeMethod
    public void setUp() {
        util = new MultiThreadingWarningUtil();
    }
    
    @AfterMethod
    public void tearDown() {
        // Clean up after each test
        if (util != null) {
            util.reset();
        }
    }
    
    @Test
    public void testSingleThreadAccess() {
        // When: A single thread accesses the utility
        util.recordThreadAccess();
        
        // Then: The count should be 1
        assertEquals(util.getUniqueThreadCount(), 1, "Should track single thread");
    }
    
    @Test
    public void testMultipleAccessFromSameThread() {
        // When: The same thread accesses the utility multiple times
        util.recordThreadAccess();
        util.recordThreadAccess();
        util.recordThreadAccess();
        
        // Then: The count should still be 1
        assertEquals(util.getUniqueThreadCount(), 1, "Should not increment count for repeated access from same thread");
    }
    
    @Test
    public void testMultipleThreadAccess() throws InterruptedException {
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        // When: Multiple threads access the utility
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    util.recordThreadAccess();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all threads to complete
        assertTrue(latch.await(5, TimeUnit.SECONDS), "All threads should complete");
        executor.shutdown();
        
        // Then: The count should match the number of unique threads
        assertEquals(util.getUniqueThreadCount(), threadCount, 
            "Should track all unique threads");
    }
    
    @Test
    public void testThreadPoolPattern() throws InterruptedException {
        int poolSize = 3;
        int tasksPerThread = 10;
        CountDownLatch latch = new CountDownLatch(poolSize * tasksPerThread);
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        
        // When: A thread pool executes multiple tasks
        for (int i = 0; i < poolSize * tasksPerThread; i++) {
            executor.submit(() -> {
                try {
                    util.recordThreadAccess();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all tasks to complete
        assertTrue(latch.await(5, TimeUnit.SECONDS), "All tasks should complete");
        executor.shutdown();
        
        // Then: The count should be at most the pool size (threads are reused)
        int uniqueThreads = util.getUniqueThreadCount();
        assertTrue(uniqueThreads <= poolSize, 
            "Thread count should not exceed pool size (threads are reused)");
        assertTrue(uniqueThreads > 0, "Should track at least one thread");
    }
    
    @Test
    public void testConcurrentAccess() throws InterruptedException {
        int threadCount = 20;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        List<Thread> threads = new ArrayList<>();
        
        // Create threads that will all start at the same time
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                try {
                    startLatch.await(); // Wait for the signal to start
                    util.recordThreadAccess();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
            thread.start();
            threads.add(thread);
        }
        
        // Release all threads at once to test concurrency
        startLatch.countDown();
        
        // Wait for all threads to complete
        assertTrue(endLatch.await(5, TimeUnit.SECONDS), "All threads should complete");
        
        // Then: All threads should be tracked
        assertEquals(util.getUniqueThreadCount(), threadCount, 
            "Should correctly track all threads even under concurrent access");
    }
    
    @Test
    public void testReset() throws InterruptedException {
        int threadCount = 3;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        // When: Multiple threads access the utility
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    util.recordThreadAccess();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "All threads should complete");
        executor.shutdown();
        
        int countBeforeReset = util.getUniqueThreadCount();
        assertTrue(countBeforeReset > 0, "Should have tracked threads before reset");
        
        // When: We reset the utility
        util.reset();
        
        // Then: The count should be back to 0
        assertEquals(util.getUniqueThreadCount(), 0, "Count should be 0 after reset");
    }
    
    @Test
    public void testMainThreadIdentification() {
        // When: The main test thread accesses the utility
        util.recordThreadAccess();
        
        // Then: It should be tracked
        assertEquals(util.getUniqueThreadCount(), 1, 
            "Main thread should be tracked");
    }
    
    @Test(timeOut = 10000)
    public void testPerformanceWithManyAccesses() {
        // When: The same thread accesses the utility many times
        for (int i = 0; i < 10000; i++) {
            util.recordThreadAccess();
        }
        
        // Then: The count should still be 1 and operations should be fast
        assertEquals(util.getUniqueThreadCount(), 1, 
            "Should handle many accesses from same thread efficiently");
    }
    
    @Test
    public void testMaxTrackedThreadsLimit() throws InterruptedException {
        // This test verifies that the utility stops tracking after MAX_TRACKED_THREADS
        // Note: This is a long-running test and may be skipped in normal runs
        
        int attemptedThreads = 1100; // More than MAX_TRACKED_THREADS (1000)
        CountDownLatch latch = new CountDownLatch(attemptedThreads);
        
        // Create and start many threads
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < attemptedThreads; i++) {
            Thread thread = new Thread(() -> {
                try {
                    util.recordThreadAccess();
                } finally {
                    latch.countDown();
                }
            });
            thread.start();
            threads.add(thread);
        }
        
        // Wait for all threads to complete (with a generous timeout)
        assertTrue(latch.await(30, TimeUnit.SECONDS), 
            "All threads should complete");
        
        // The utility should cap tracking at MAX_TRACKED_THREADS
        int uniqueThreads = util.getUniqueThreadCount();
        assertEquals(uniqueThreads, 1000,
            "Tracker should cap at MAX_TRACKED_THREADS when additional threads appear");

        // When: even more threads arrive after the cap is hit
        Thread extraThread = new Thread(util::recordThreadAccess);
        extraThread.start();
        extraThread.join();

        // Then: the count should remain capped
        assertEquals(util.getUniqueThreadCount(), 1000,
            "Tracker must remain capped after hitting MAX_TRACKED_THREADS");
    }
}
