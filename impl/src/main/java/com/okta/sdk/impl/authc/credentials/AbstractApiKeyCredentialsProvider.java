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
package com.okta.sdk.impl.authc.credentials;

import com.okta.sdk.api.ApiKey;
import com.okta.sdk.api.ApiKeyBuilder;
import com.okta.sdk.impl.api.ClientApiKey;
import com.okta.sdk.impl.io.DefaultResourceFactory;
import com.okta.sdk.impl.io.ResourceFactory;
import com.okta.sdk.lang.Strings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

/**
 * @since 1.0.0
 */
public abstract class AbstractApiKeyCredentialsProvider implements ClientCredentialsProvider {

    public static final String DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION =
            System.getProperty("user.home") + File.separatorChar + ".okta" + File.separatorChar + "apiKey.properties";

    public static final String DEFAULT_ID_PROPERTY_NAME = "apiKey.id";
    public static final String DEFAULT_SECRET_PROPERTY_NAME = "apiKey.secret";

    private ResourceFactory resourceFactory = new DefaultResourceFactory();

    public ClientCredentials getClientCredentials() {

        Properties props = loadProperties();

        String id = getPropertyValue(props, DEFAULT_ID_PROPERTY_NAME); // TODO: remove this
        String secret = getPropertyValue(props, DEFAULT_SECRET_PROPERTY_NAME);

        ApiKey apiKey = createApiKey(id, secret);

        return new ApiKeyCredentials(apiKey);
    }

    protected abstract Properties loadProperties();

    protected ApiKey createApiKey(String id, String secret) {

        if (!Strings.hasText(id)) {
            String msg = "Unable to find an API Key 'id', either from explicit configuration (for example, " +
                    ApiKeyBuilder.class.getSimpleName() + ".setApiKeyId) or from fallback locations:\n\n" +
                    "1) system property okta.client.apiKey.id\n" +
                    "2) resource file path or URL specified by system property okta.client.apiKey.file\n" +
                    "3) resource file path or URL specified by environment variable OKTA_API_KEY_FILE\n" +
                    "4) environment variable OKTA_API_KEY_ID\n" +
                    "5) default apiKey.properties file location " + DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION +
                    ".\n\n" +
                    "Please ensure you manually configure an API Key ID or ensure that it exists in one of these " +
                    "fallback locations.";
            throw new IllegalStateException(msg);
        }

        if (!Strings.hasText(secret)) {
            String msg = "Unable to find an API Key 'secret', either from explicit configuration (for example, " +
                    ApiKeyBuilder.class.getSimpleName() + ".setApiKeySecret) or from fallback locations:\n\n" +
                    "1) system property okta.client.apiKey.secret\n" +
                    "2) resource file path or URL specified by system property okta.client.apiKey.file\n" +
                    "3) resource file path or URL specified by environment variable OKTA_API_KEY_FILE\n" +
                    "4) environment variable OKTA_API_KEY_SECRET\n" +
                    "5) default apiKey.properties file location " + DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION +
                    ".\n\n" +
                    "Please ensure you manually configure an API Key Secret or ensure that it exists in one of " +
                    "these fallback locations.";
            throw new IllegalStateException(msg);
        }

        return new ClientApiKey(id, secret);
    }


    protected Reader createFileReader(String apiKeyFileLocation) throws IOException {
        InputStream is = this.resourceFactory.createResource(apiKeyFileLocation).getInputStream();
        return toReader(is);
    }

    private static Reader toReader(InputStream is) throws IOException {
        return new InputStreamReader(is, "ISO-8859-1");
    }

    protected static Properties toProperties(Reader reader) throws IOException {
        Properties properties = new Properties();
        properties.load(reader);
        return properties;
    }

    protected static String getPropertyValue(Properties properties, String propName) {

        String value = properties.getProperty(propName);
        if (value != null) {
            value = value.trim();
            if ("".equals(value)) {
                value = null;
            }
        }
        return value;
    }

}
