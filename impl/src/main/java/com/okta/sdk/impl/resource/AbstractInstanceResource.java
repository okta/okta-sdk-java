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
package com.okta.sdk.impl.resource;

import com.okta.sdk.impl.ds.InternalDataStore;
import com.okta.sdk.resource.Resource;
import com.okta.sdk.resource.Saveable;

import java.util.Map;

/**
 * @since 0.5.0
 */
public abstract class AbstractInstanceResource<R extends Resource> extends AbstractResource implements Saveable<R> {

    protected AbstractInstanceResource(InternalDataStore dataStore) {
        super(dataStore);
    }

    protected AbstractInstanceResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public R save() {
        getDataStore().save(this);
        return (R) this;
    }

    /**
     * Basic delete support method that simply calls, {@code dataStore.delete(this)}.  This can be exposed from child
     * classes when needed.
     */
    protected void delete() {
        writeLock.lock();
        try {
            getDataStore().delete(this);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Returns the class this resource represents. This is used when you need to figure out what the main type of
     * Resource this object is.  For example, DefaultFoo, ImplFoo, and SuperFoo may all represent the Resource of
     * {@code Foo}, in this case this method may return <code>Foo.class</code>.
     * (Defaults to {@code getClass()}.)
     *
     * @return The type of resource this class represents.
     * @since 1.0
     */
    public Class<? extends Resource> getResourceClass() {
        return getClass();
    }
}
