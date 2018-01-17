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
package com.okta.sdk.resource;

/**
 * Base representation of a REST resource payload.  All objects transferred over the wire by this SDK are Resources.
 * @since 0.5.0
 */
public interface Resource {

    String getResourceHref();
    void setResourceHref(String href);

    /**
     * Returns the class this resource represents. This is used when you need to figure out what the main type of
     * Resource this object is.  For example, DefaultFoo, ImplFoo, and SuperFoo may all represent the Resource of
     * <code>Foo</code>, in this case this method may return <code>Foo.class</code>.
     * (Defaults to <code>getClass()</code>.)
     *
     * @return The type of resource this class represents.
     * @since 0.11.0
     */
    default Class<? extends Resource> getResourceClass() {
        return getClass();
    }
}
