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
package com.okta.sdk.impl.util

import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@link MultiThreadingWarningUtil}.
 */
class MultiThreadingWarningUtilTest {

    @BeforeMethod
    void setUp() {
        // Reset the counter before each test
        MultiThreadingWarningUtil.resetInstanceCounter()
    }

    @Test
    void testSingleInstanceCreation() {
        assertThat MultiThreadingWarningUtil.getInstanceCount(), is(0)
        
        MultiThreadingWarningUtil.recordInstanceCreation()
        
        assertThat MultiThreadingWarningUtil.getInstanceCount(), is(1)
    }

    @Test
    void testMultipleInstanceCreation() {
        assertThat MultiThreadingWarningUtil.getInstanceCount(), is(0)
        
        MultiThreadingWarningUtil.recordInstanceCreation()
        assertThat MultiThreadingWarningUtil.getInstanceCount(), is(1)
        
        MultiThreadingWarningUtil.recordInstanceCreation()
        assertThat MultiThreadingWarningUtil.getInstanceCount(), is(2)
        
        MultiThreadingWarningUtil.recordInstanceCreation()
        assertThat MultiThreadingWarningUtil.getInstanceCount(), is(3)
    }

    @Test
    void testResetCounter() {
        MultiThreadingWarningUtil.recordInstanceCreation()
        MultiThreadingWarningUtil.recordInstanceCreation()
        assertThat MultiThreadingWarningUtil.getInstanceCount(), is(2)
        
        MultiThreadingWarningUtil.resetInstanceCounter()
        assertThat MultiThreadingWarningUtil.getInstanceCount(), is(0)
    }

    @Test
    void testConcurrentInstanceCreation() {
        int threadCount = 10
        Thread[] threads = new Thread[threadCount]
        
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread({
                MultiThreadingWarningUtil.recordInstanceCreation()
            })
        }
        
        for (Thread thread : threads) {
            thread.start()
        }
        
        for (Thread thread : threads) {
            thread.join()
        }
        
        assertThat MultiThreadingWarningUtil.getInstanceCount(), is(threadCount)
    }
}
