/*
 * Copyright 2017 Okta
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class StringResource implements Resource {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final String  string;
    private final Charset charset;

    public StringResource(String s) {
        this(s, UTF_8);
    }

    public StringResource(String string, Charset charset) {
        Assert.hasText(string, "String argument cannot be null or empty.");
        Assert.notNull(charset, "Charset argument cannot be null or empty.");
        this.string = string;
        this.charset = charset;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(string.getBytes(charset));
    }
}
