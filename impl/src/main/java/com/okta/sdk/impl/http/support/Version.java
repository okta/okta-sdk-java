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
package com.okta.sdk.impl.http.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @since 0.5.0
 */
public class Version {

    private static final String CLIENT_VERSION = lookupClientVersion();
    private static final String VERSION_FILE = "/com/okta/sdk/version.properties";

    public static String getClientVersion() {
        return CLIENT_VERSION;
    }

    private static String lookupClientVersion() {
        Class clazz = Version.class;
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = clazz.getResourceAsStream(VERSION_FILE);
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            do {
                line = reader.readLine();
            } while (line != null && (line.startsWith("#") || line.isEmpty()));
            return line;
        } catch (IOException e) {
            throw new RuntimeException("Unable to obtain version from [" + VERSION_FILE + "].", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("Exception while trying to close file [" + VERSION_FILE + "].", e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("Exception while trying to close file [" + VERSION_FILE + "].", e);
                }
            }
        }
    }
}
