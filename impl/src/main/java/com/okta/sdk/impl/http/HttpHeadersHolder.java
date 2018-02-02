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
package com.okta.sdk.impl.http;

import java.util.List;
import java.util.Map;

/**
 * This class is used to hold key/value pairs in a ThreadLocal.
 */
public abstract class HttpHeadersHolder {
    private static final ThreadLocal<Map<String, List<String>>> current = new ThreadLocal<>();

    public static void set(Map<String, List<String>> headers) {
        current.set(headers);
    }

    public static Map<String, List<String>> get() {
        return current.get();
    }

    public static void clear() {
        current.remove();
    }
}
