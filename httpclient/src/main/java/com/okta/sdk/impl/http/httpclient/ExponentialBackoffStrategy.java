/*
 * Copyright (c) 2011 Google Inc.
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
package com.okta.sdk.impl.http.httpclient;

import com.okta.sdk.impl.http.support.BackoffStrategy;
import com.okta.sdk.lang.Assert;

import java.time.Clock;

/**
 * Implementation of {@link BackoffStrategy} that increases the back off period for each retry attempt using
 * a randomization function that grows exponentially.
 * <p>
 * <p>
 * {@link #getDelayMillis()} is calculated using the following formula:
 * </p>
 * <p>
 * <pre>
 * randomized_interval =
 * retry_interval * (random value in range [1 - randomization_factor, 1 + randomization_factor])
 * </pre>
 * <p>
 * <p>
 * In other words {@link #getDelayMillis()} will range between the randomization factor
 * percentage below and above the retry interval. For example, using 2 seconds as the base retry
 * interval and 0.5 as the randomization factor, the actual back off period used in the next retry
 * attempt will be between 1 and 3 seconds.
 * </p>
 * <p>
 * <p>
 * <b>Note:</b> max_interval caps the retry_interval and not the randomized_interval.
 * </p>
 * <p>
 * <p>
 * If the time elapsed since an {@link ExponentialBackoffStrategy} instance is created goes past the
 * max_elapsed_time then the method {@link #getDelayMillis()} starts returning
 * -1. The elapsed time can be reset by calling {@link #reset()}.
 * </p>
 * <p>
 * <p>
 * Example: The default retry_interval is .5 seconds, default randomization_factor is 0.5, default
 * multiplier is 1.5 and the default max_interval is 1 minute. For 10 tries the sequence will be
 * (values in seconds) and assuming we go over the max_elapsed_time on the 10th try:
 * </p>
 * <p>
 * <pre>
 * request#     retry_interval     randomized_interval
 *
 * 1             0.5                [0.25,   0.75]
 * 2             0.75               [0.375,  1.125]
 * 3             1.125              [0.562,  1.687]
 * 4             1.687              [0.8435, 2.53]
 * 5             2.53               [1.265,  3.795]
 * 6             3.795              [1.897,  5.692]
 * 7             5.692              [2.846,  8.538]
 * 8             8.538              [4.269, 12.807]
 * 9            12.807              [6.403, 19.210]
 * 10           19.210              -1
 * </pre>
 * <p>
 * <p>
 * Implementation is not thread-safe.
 * </p>
 *
 * @author Ravi Mistry
 * @since 1.15
 */
public class ExponentialBackoffStrategy {

    /**
     * The default initial interval value in milliseconds (0.5 seconds).
     */
    public static final int DEFAULT_INITIAL_INTERVAL_MILLIS = 500;

    /**
     * The default randomization factor (0.5 which results in a random period ranging between 50%
     * below and 50% above the retry interval).
     */
    public static final double DEFAULT_RANDOMIZATION_FACTOR = 0.5;

    /**
     * The default multiplier value (1.5 which is 50% increase per back off).
     */
    public static final double DEFAULT_MULTIPLIER = 1.5;

    /**
     * The default maximum back off time in milliseconds (1 minute).
     */
    public static final int DEFAULT_MAX_INTERVAL_MILLIS = 60000;

    /**
     * The default maximum elapsed time in milliseconds (15 minutes).
     */
    public static final int DEFAULT_MAX_ELAPSED_TIME_MILLIS = 900000;

    /**
     * Single for when maximum elapsed time has passed.
     */
    private static final long STOP = -1L;

    /**
     * The initial retry interval in milliseconds.
     */
    private final int initialIntervalMillis;

    /**
     * The randomization factor to use for creating a range around the retry interval.
     * <p>
     * <p>
     * A randomization factor of 0.5 results in a random period ranging between 50% below and 50%
     * above the retry interval.
     * </p>
     */
    private final double randomizationFactor;

    /**
     * The value to multiply the current interval with for each retry attempt.
     */
    private final double multiplier;

    /**
     * The maximum value of the back off period in milliseconds. Once the retry interval reaches this
     * value it stops increasing.
     */
    private final int maxIntervalMillis;

    /**
     * The maximum elapsed time after instantiating {@link ExponentialBackoffStrategy} or calling
     * {@link #reset()} after which {@link #getDelayMillis()} returns -1.
     */
    private final int maxElapsedTimeMillis;

    /**
     * The current retry interval in milliseconds.
     */
    private int currentIntervalMillis;

    /**
     * The system time in miliseconds. It is calculated when an ExponentialBackOffStrategy instance is
     * created and is reset when {@link #reset()} is called.
     */
    long startTimeMillis;

    /**
     * Clock instance used for timing. Non-final to support testing.
     */
    private Clock clock = Clock.systemUTC();

    /**
     * Creates an instance of ExponentialBackOffPolicy using default values.
     * <p>
     * <ul>
     * <li>{@code initialIntervalMillis} defaults to {@link #DEFAULT_INITIAL_INTERVAL_MILLIS}</li>
     * <li>{@code randomizationFactor} defaults to {@link #DEFAULT_RANDOMIZATION_FACTOR}</li>
     * <li>{@code multiplier} defaults to {@link #DEFAULT_MULTIPLIER}</li>
     * <li>{@code maxIntervalMillis} defaults to {@link #DEFAULT_MAX_INTERVAL_MILLIS}</li>
     * <li>{@code maxElapsedTimeMillis} defaults in {@link #DEFAULT_MAX_ELAPSED_TIME_MILLIS}</li>
     * </ul>
     */
    public ExponentialBackoffStrategy() {
        this(DEFAULT_INITIAL_INTERVAL_MILLIS, DEFAULT_RANDOMIZATION_FACTOR, DEFAULT_MULTIPLIER, DEFAULT_MAX_INTERVAL_MILLIS, DEFAULT_MAX_ELAPSED_TIME_MILLIS);
    }

    protected ExponentialBackoffStrategy(int initialIntervalMillis, double randomizationFactor, double multiplier, int maxIntervalMillis, int maxElapsedTimeMillis) {
        this.initialIntervalMillis = initialIntervalMillis;
        this.randomizationFactor = randomizationFactor;
        this.multiplier = multiplier;
        this.maxIntervalMillis = maxIntervalMillis;
        this.maxElapsedTimeMillis = maxElapsedTimeMillis;

        Assert.isTrue(this.initialIntervalMillis > 0, "initialIntervalMillis must be greater than 0");
        Assert.isTrue(0 <= this.randomizationFactor && this.randomizationFactor < 1, "randomizationFactor must be between 0 and 1");
        Assert.isTrue(this.multiplier >= 1, "multiplier must be >= 1");
        Assert.isTrue(this.maxIntervalMillis >= this.initialIntervalMillis, "maxIntervalMillis must be greater than initialIntervalMillis");
        Assert.isTrue(this.maxElapsedTimeMillis > 0, "maxElapsedTimeMillis must be greater than 0"); // TODO allow for zero to sort circuit?
        reset();
    }

    /**
     * Sets the interval back to the initial retry interval and restarts the timer.
     */
    final void reset() {
        currentIntervalMillis = initialIntervalMillis;
        startTimeMillis = clock.millis();
    }

    long getDelayMillis() {
        // Make sure we have not gone over the maximum elapsed time.
        if (getElapsedTimeMillis() > maxElapsedTimeMillis) {
            return STOP;
        }
        int randomizedInterval = getRandomValueFromInterval(randomizationFactor, Math.random(), currentIntervalMillis);
        incrementCurrentInterval();
        return randomizedInterval;
    }

//    @Override
//    public long getDelayMillis(int retryCount) {
//        return getDelayMillis();
//    }

    /**
     * Returns a random value from the interval [randomizationFactor * currentInterval,
     * randomizationFactor * currentInterval].
     */
    static int getRandomValueFromInterval(double randomizationFactor, double random, int currentIntervalMillis) {
        double delta = randomizationFactor * currentIntervalMillis;
        double minInterval = currentIntervalMillis - delta;
        double maxInterval = currentIntervalMillis + delta;
        // Get a random value from the range [minInterval, maxInterval].
        // The formula used below has a +1 because if the minInterval is 1 and the maxInterval is 3 then
        // we want a 33% chance for selecting either 1, 2 or 3.
        int randomValue = (int) (minInterval + (random * (maxInterval - minInterval + 1)));
        return randomValue;
    }

    /**
     * Returns the initial retry interval in milliseconds.
     */
    final int getInitialIntervalMillis() {
        return initialIntervalMillis;
    }

    /**
     * Returns the randomization factor to use for creating a range around the retry interval.
     * <p>
     * <p>
     * A randomization factor of 0.5 results in a random period ranging between 50% below and 50%
     * above the retry interval.
     * </p>
     */
    final double getRandomizationFactor() {
        return randomizationFactor;
    }

    /**
     * Returns the current retry interval in milliseconds.
     */
    final int getCurrentIntervalMillis() {
        return currentIntervalMillis;
    }

    /**
     * Returns the value to multiply the current interval with for each retry attempt.
     */
    final double getMultiplier() {
        return multiplier;
    }

    /**
     * Returns the maximum value of the back off period in milliseconds. Once the current interval
     * reaches this value it stops increasing.
     */
    final int getMaxIntervalMillis() {
        return maxIntervalMillis;
    }

    /**
     * Returns the maximum elapsed time in milliseconds.
     * <p>
     * <p>
     * If the time elapsed since an {@link ExponentialBackoffStrategy} instance is created goes past the
     * max_elapsed_time then the method {@link #getDelayMillis()} starts returning
     * -1. The elapsed time can be reset by calling {@link #reset()}.
     * </p>
     */
    final int getMaxElapsedTimeMillis() {
        return maxElapsedTimeMillis;
    }

    /**
     * Returns the elapsed time in milliseconds since an {@link ExponentialBackoffStrategy} instance is
     * created and is reset when {@link #reset()} is called.
     * <p>
     * <p>
     * The elapsed time is computed using {@link System#currentTimeMillis()}.
     * </p>
     */
    final long getElapsedTimeMillis() {
        return (clock.millis() - startTimeMillis);
    }

    /**
     * Sets clock used for timing. Exposed for testing
     * @param clock clock used for timing
     */
    final void setClock(Clock clock) {
        this.clock = clock;
    }

    /**
     * Increments the current interval by multiplying it with the multiplier.
     */
    private void incrementCurrentInterval() {
        // Check for overflow, if overflow is detected set the current interval to the max interval.
        if (currentIntervalMillis >= maxIntervalMillis / multiplier) {
            currentIntervalMillis = maxIntervalMillis;
        } else {
            currentIntervalMillis *= multiplier;
        }
    }
}