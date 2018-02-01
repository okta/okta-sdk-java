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

import com.okta.sdk.cache.CacheConfigurationBuilder;
import com.okta.sdk.cache.CacheManager;
import com.okta.sdk.cache.CacheManagerBuilder;
import com.okta.sdk.lang.Assert;
import com.okta.sdk.lang.Collections;
import com.okta.sdk.lang.Duration;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @since 0.5.0
 */
public class DefaultCacheManagerBuilder implements CacheManagerBuilder {

    private Duration defaultTimeToLive;
    private Duration defaultTimeToIdle;

    private final Set<CacheConfiguration> configs = new LinkedHashSet<CacheConfiguration>();

    @Override
    public CacheManagerBuilder withDefaultTimeToLive(long ttl, TimeUnit timeUnit) {
        this.defaultTimeToLive = DefaultCacheConfiguration.toDuration(ttl, timeUnit);
        return this;
    }

    @Override
    public CacheManagerBuilder withDefaultTimeToIdle(long tti, TimeUnit timeUnit) {
        this.defaultTimeToIdle = DefaultCacheConfiguration.toDuration(tti, timeUnit);
        return this;
    }

    @Override
    public CacheManagerBuilder withCache(CacheConfigurationBuilder builder) {
        Assert.isInstanceOf(DefaultCacheConfigurationBuilder.class, builder,
                "This implementation only accepts " + DefaultCacheConfigurationBuilder.class.getName() + " instances.");

        DefaultCacheConfigurationBuilder b = (DefaultCacheConfigurationBuilder) builder;

        this.configs.add(b.build());

        return this;
    }

    @Override
    public CacheManager build() {
        DefaultCacheManager manager = new DefaultCacheManager();

        if (this.defaultTimeToLive != null) {
            manager.setDefaultTimeToLive(this.defaultTimeToLive);
        }

        if (this.defaultTimeToIdle != null) {
            manager.setDefaultTimeToIdle(this.defaultTimeToIdle);
        }

        if (!Collections.isEmpty(configs)) {
            manager.setCacheConfigurations(configs);
        }

        return manager;
    }
}
