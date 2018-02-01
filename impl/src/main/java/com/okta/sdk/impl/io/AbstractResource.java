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
package com.okta.sdk.impl.io;

import com.okta.sdk.lang.Assert;

public abstract class AbstractResource implements Resource {

    private final String location;

    public AbstractResource(String location) {
        Assert.hasText(location, "Location argument cannot be null or empty.");
        this.location = canonicalize(location);
    }

    protected String canonicalize(String input) {
        if (hasResourcePrefix(input)) {
            input = stripPrefix(input);
        }
        return input;
    }

    /**
     * Returns {@code true} if the resource path is not null and starts with one of the recognized
     * resource prefixes ({@code classpath:}, {@code url:}, or {@code file:}, {@code false} otherwise.
     *
     * @param resourcePath the resource path to check
     * @return {@code true} if the resource path is not null and starts with one of the recognized
     *         resource prefixes, {@code false} otherwise.
     */
    protected boolean hasResourcePrefix(String resourcePath) {
        return resourcePath != null && resourcePath.startsWith(getScheme() + ":");
    }

    private static String stripPrefix(String resourcePath) {
        return resourcePath.substring(resourcePath.indexOf(":") + 1);
    }

    public String getLocation() {
        return location;
    }

    protected abstract String getScheme();

    @Override
    public String toString() {
        return getScheme() + location;
    }
}
