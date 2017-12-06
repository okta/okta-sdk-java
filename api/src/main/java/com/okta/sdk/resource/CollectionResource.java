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

import java.util.stream.Stream;

/**
 * A {@code CollectionResource} is a first-class {@link Resource} that contains a collection of
 * other {@link Resource} instances.
 *
 * @since 0.5.0
 */
public interface CollectionResource<T extends Resource> extends Resource, Iterable<T> {

    /**
     * This is just a convenience method to retrieve the single element expected to exist in this collection. This method is
     * intended to be used in cases where the returned list is explicitly expected to contain a single element. This operation will throw
     * an exception if this list contains zero or more than one element.
     *
     * <p>This method is not backed by any specific property obtained from the backend, as already mentioned, this is a convenience method.</p>
     *
     * @return the single unique resource that is expect to be contained within this list.
     * @throws java.lang.IllegalStateException if this list contains either zero or more than one element.
     */
    T single();

    /**
     * Returns a sequential {@code Stream} with this collection as its source.
     *
     * @return a sequential {@code Stream} over the elements in this resource
     * @since 0.11.0
     */
    Stream<T> stream();

}
