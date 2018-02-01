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
package com.okta.sdk.client;

import com.okta.sdk.lang.Classes;

/**
 * Static utility/helper class for working with {@link Client} resources. For example:
 * <pre>
 * <b>Clients.builder()</b>
 *     // ... etc ...
 *     .setProxy(new Proxy("192.168.2.120", 9001))
 *     .build();
 * </pre>
 *
 * <p>See the {@link ClientBuilder ClientBuilder} JavaDoc for extensive documentation on client configuration.</p>
 *
 * @see ClientBuilder
 * @since 0.5.0
 */
public final class Clients {

    /**
     * Returns a new {@link ClientBuilder} instance, used to construct {@link Client} instances.
     *
     * @return a a new {@link ClientBuilder} instance, used to construct {@link Client} instances.
     */
    public static ClientBuilder builder() {
        return (ClientBuilder) Classes.newInstance("com.okta.sdk.impl.client.DefaultClientBuilder");
    }

}
