/*
 * Copyright (c) 2011 Google Inc.
 * Modifications Copyright 2018 Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package com.okta.sdk.impl.http.httpclient

import org.testng.annotations.Test

import java.time.Clock

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Tests {@link ExponentialBackoffStrategy}.
 *
 * @author Ravi Mistry
 */
class ExponentialBackoffStrategyTest {

    @Test
    void testConstructor() {

        def backoffStrategy = new ExponentialBackoffStrategy()
        assertThat backoffStrategy.getInitialIntervalMillis(), is(ExponentialBackoffStrategy.DEFAULT_INITIAL_INTERVAL_MILLIS)
        assertThat backoffStrategy.getCurrentIntervalMillis(), is(ExponentialBackoffStrategy.DEFAULT_INITIAL_INTERVAL_MILLIS)
        assertThat backoffStrategy.getRandomizationFactor(), is(ExponentialBackoffStrategy.DEFAULT_RANDOMIZATION_FACTOR)
        assertThat backoffStrategy.getMultiplier(), is(ExponentialBackoffStrategy.DEFAULT_MULTIPLIER)
        assertThat backoffStrategy.getMaxIntervalMillis(), is(ExponentialBackoffStrategy.DEFAULT_MAX_INTERVAL_MILLIS)
        assertThat backoffStrategy.getMaxElapsedTimeMillis(), is(ExponentialBackoffStrategy.DEFAULT_MAX_ELAPSED_TIME_MILLIS)

        int initialInterval = 1
        double randomizationFactor = 0.1
        double multiplier = 5.0
        int maxInterval = 10
        int maxElapsedTime = 900000

        backoffStrategy = new ExponentialBackoffStrategy(initialInterval, randomizationFactor, multiplier, maxInterval, maxElapsedTime)
        assertThat backoffStrategy.getInitialIntervalMillis(), is(initialInterval)
        assertThat backoffStrategy.getCurrentIntervalMillis(), is(initialInterval)
        assertThat backoffStrategy.getRandomizationFactor(), is(randomizationFactor)
        assertThat backoffStrategy.getMultiplier(), is(multiplier)
        assertThat backoffStrategy.getMaxIntervalMillis(), is(maxInterval)
        assertThat backoffStrategy.getMaxElapsedTimeMillis(), is(maxElapsedTime)
    }

    @Test
    void testBackoff() throws Exception {

        int testInitialInterval = 500
        double testRandomizationFactor = 0.1
        double testMultiplier = 2.0
        int testMaxInterval = 5000
        int testMaxElapsedTime = 900000

        ExponentialBackoffStrategy backoffStrategy = new ExponentialBackoffStrategy(
                testInitialInterval,
                testRandomizationFactor,
                testMultiplier,
                testMaxInterval,
                testMaxElapsedTime)
        int[] expectedResults = [500, 1000, 2000, 4000, 5000, 5000, 5000, 5000, 5000, 5000]
        for (int expected : expectedResults) {
            assertThat backoffStrategy.getCurrentIntervalMillis(), is(expected)
            // Assert that the next back off falls in the expected range.
            int minInterval = (int) (expected - (testRandomizationFactor * expected))
            int maxInterval = (int) (expected + (testRandomizationFactor * expected))
            long actualInterval = backoffStrategy.getDelayMillis()
            assertThat minInterval.longValue(), lessThanOrEqualTo(actualInterval)
            assertThat actualInterval, lessThanOrEqualTo(maxInterval.longValue())
        }
    }

    @Test
    void testGetRandomizedInterval() {
        // 33% chance of being 1.
        assertThat ExponentialBackoffStrategy.getRandomValueFromInterval(0.5, 0, 2), is(1)
        assertThat ExponentialBackoffStrategy.getRandomValueFromInterval(0.5, 0.33, 2), is(1)
        // 33% chance of being 2.
        assertThat ExponentialBackoffStrategy.getRandomValueFromInterval(0.5, 0.34, 2), is(2)
        assertThat ExponentialBackoffStrategy.getRandomValueFromInterval(0.5, 0.66, 2), is(2)
        // 33% chance of being 3.
        assertThat ExponentialBackoffStrategy.getRandomValueFromInterval(0.5, 0.67, 2), is(3)
        assertThat ExponentialBackoffStrategy.getRandomValueFromInterval(0.5, 0.99, 2), is(3)
    }

    @Test
    void testGetElapsedTimeMillis() {

        Clock clock = mock(Clock)
        when(clock.millis()).thenReturn(0L).thenReturn(1000L)

        def backoffStrategy = new ExponentialBackoffStrategy()
        backoffStrategy.clock = clock
        backoffStrategy.reset()

        long elapsedTimeMillis = backoffStrategy.getElapsedTimeMillis()
        assertThat elapsedTimeMillis, is(1000L)
    }

    @Test
    void testMaxElapsedTime() throws Exception {

        Clock clock = mock(Clock)
        when(clock.millis()).thenReturn(1000L)
                            .thenReturn(10000L)
                            .thenReturn(1000000L)

        def backoffStrategy = new ExponentialBackoffStrategy()
        backoffStrategy.clock = clock
        backoffStrategy.reset()

        assertThat backoffStrategy.getDelayMillis(), is(not(-1L))

        // Change the currentElapsedTimeMillis to be 0 ensuring that the elapsed time will be greater
        // than the max elapsed time.
        backoffStrategy.startTimeMillis = 0
        assertThat backoffStrategy.getDelayMillis(), is(-1L)
    }

    @Test
    void testBackoffOverflow() throws Exception {
        int initialInterval = Integer.MAX_VALUE / 2
        double multiplier = 2.1
        int maxInterval = Integer.MAX_VALUE

        def backoffStrategy = new ExponentialBackoffStrategy(initialInterval,
                                                             ExponentialBackoffStrategy.DEFAULT_RANDOMIZATION_FACTOR,
                                                             multiplier,
                                                             maxInterval,
                                                             ExponentialBackoffStrategy.DEFAULT_MAX_ELAPSED_TIME_MILLIS)
        backoffStrategy.getDelayMillis()
        // Assert that when an overflow is possible the current interval is set to the max interval.
        assertThat backoffStrategy.getCurrentIntervalMillis(), is(maxInterval)
    }
}