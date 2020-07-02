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
package com.okta.sdk.impl.cache;

import com.okta.commons.lang.Assert;
import java.time.Duration;

import java.util.concurrent.TimeUnit;

/**
 * @since 0.5.0
 */
public class DefaultCacheConfiguration implements CacheConfiguration {

    private final String name;
    private final Duration timeToLive;
    private final Duration timeToIdle;

    public DefaultCacheConfiguration(String name, Duration timeToLive, Duration timeToIdle) {
        Assert.hasText(name, "Cache Region name cannot be null or empty.");
        this.name = name;
        this.timeToLive = timeToLive;
        this.timeToIdle = timeToIdle;
    }

    static Duration toDuration(long value, TimeUnit tu) {
        long timeInMillis = TimeUnit.MILLISECONDS.convert(value, tu);
        if (timeInMillis > 0) {
            return Duration.ofMillis(timeInMillis);
        }
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Duration getTimeToLive() {
        return this.timeToLive;
    }

    @Override
    public Duration getTimeToIdle() {
        return this.timeToIdle;
    }

    @Override
    public String toString() {
        return "DefaultCacheConfiguration{" +
                "name='" + name + '\'' +
                ", timeToLive=" + timeToLive +
                ", timeToIdle=" + timeToIdle +
                '}';
    }
}
