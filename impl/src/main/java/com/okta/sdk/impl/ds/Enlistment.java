/*
 * Copyright 2014 Stormpath, Inc.
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
package com.okta.sdk.impl.ds;

import com.okta.sdk.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This {@link Map} is used to store a single instance of Resource's data which is shared among all Resource instances
 * describing the same server-side item (i.e, having the very same href). Locks are used to provide thread-safe Map
 * operations.
 *
 * @see {@link DefaultDataStore}
 * @since 1.0.RC3
 */
public class Enlistment implements Map<String, Object> {

    private static final Logger log = LoggerFactory.getLogger(Enlistment.class);

    protected final Map<String, Object> backingMap;  //Protected by read/write lock
    protected final Lock readLock;
    protected final Lock writeLock;

    public Enlistment(Map<String, Object> map) {
        ReadWriteLock rwl = new ReentrantReadWriteLock();
        this.readLock = rwl.readLock();
        this.writeLock = rwl.writeLock();
        this.backingMap = new LinkedHashMap<String, Object>();
        setProperties(map);
    }

    public final void setProperties(Map<String, Object> properties) {
        if(properties != null) {
            writeLock.lock();
            try {
                this.backingMap.clear();
                this.backingMap.putAll(properties);
            } finally {
                writeLock.unlock();
            }
        }
    }

    @Override
    public int hashCode() {
        readLock.lock();
        try {
            return this.backingMap.isEmpty() ? 0 : this.backingMap.hashCode();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int size() {
        readLock.lock();
        try {
            return this.backingMap.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        readLock.lock();
        try {
            return this.backingMap.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        Assert.isInstanceOf(String.class, key);
        readLock.lock();
        try {
            return this.backingMap.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        readLock.lock();
        try {
            return this.backingMap.containsValue(value);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Object get(Object key) {
        Assert.isInstanceOf(String.class, key);
        readLock.lock();
        try {
            return this.backingMap.get(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Object put(String key, Object value) {
        writeLock.lock();
        Object previous;
        try {
            previous = this.backingMap.put(key, value);
        } finally {
            writeLock.unlock();
        }
        return previous;
    }

    @Override
    public Object remove(Object key) {
        Assert.isInstanceOf(String.class, key);
        writeLock.lock();
        try {
            Object object = this.backingMap.remove(key);
            return object;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        if (com.okta.sdk.lang.Collections.isEmpty(m)) {
            return;
        }
        writeLock.lock();
        try {
            this.backingMap.putAll(m);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            this.backingMap.clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Set<String> keySet() {
        readLock.lock();
        try {
            Set<String> keys = this.backingMap.keySet();
            return new LinkedHashSet<String>(keys);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Collection<Object> values() {
        readLock.lock();
        try {
            return this.backingMap.values();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        readLock.lock();
        try {
            return this.backingMap.entrySet();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        //ApiKeys obtained via Application#getApiKey(String) are retrieved from an internal CollectionResource (ApiKeyList).
        //Resources inside a collection are not shared among resources with same href. Therefore, when comparing ApiKeys,
        //we can be receiving an api key whose properties do not reside inside and Enlistment object, therefore we need to
        //compare Map vs Map instead of Enlistment vs Enlistment
        //This fixes the failing tests: ApplicationIT#testGetApiKeyById and ApplicationIT#testGetApiKeyByIdCacheDisabled
        if (o instanceof Map && !(o instanceof Enlistment) && (((String)((Map)o).get("href")).contains("/apiKeys/"))) {
            return o.equals(this);
        }
        if( ! (o instanceof Enlistment)) {
            return false;
        }
        Enlistment other = (Enlistment) o;
        readLock.lock();
        try {
            other.readLock.lock();
            try {
                return this.backingMap.equals(other);
            } finally {
                other.readLock.unlock();
            }
        } finally {
            readLock.unlock();
        }
    }

}
