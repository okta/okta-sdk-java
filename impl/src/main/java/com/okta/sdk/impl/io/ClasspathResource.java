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

import com.okta.sdk.lang.Classes;

import java.io.InputStream;

public class ClasspathResource extends AbstractResource {

    private static final String SCHEME = "classpath";
    public static final String SCHEME_PREFIX = SCHEME + ":";

    public ClasspathResource(String location) {
        super(location);
    }

    @Override
    protected String getScheme() {
        return SCHEME;
    }

    @Override
    public InputStream getInputStream() {
        return Classes.getResourceAsStream(getLocation());
    }

    @Override
    public String toString() {
        return SCHEME_PREFIX + getLocation();
    }
}
