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

import com.okta.sdk.impl.config.ClientConfiguration;
import com.okta.sdk.lang.Assert;
import com.okta.sdk.lang.Strings;

import java.util.Properties;

/**
 * @since 1.0.0
 */
public class ConfigurationCredentialsProvider extends AbstractApiKeyCredentialsProvider {

    private ClientConfiguration clientConfiguration;

    public ConfigurationCredentialsProvider(ClientConfiguration clientConfiguration) {
        Assert.notNull(clientConfiguration, "clientConfiguration must not be null.");
        this.clientConfiguration = clientConfiguration;
    }

    @Override
    protected Properties loadProperties() {
        Properties props = new Properties();

        String keyId = clientConfiguration.getApiKeyId();
        if (Strings.hasText(keyId)) {
            props.put(DEFAULT_ID_PROPERTY_NAME, keyId);
        }

        String secret = clientConfiguration.getApiKeySecret();
        if (Strings.hasText(secret)) {
            props.put(DEFAULT_SECRET_PROPERTY_NAME, secret);
        }

        return props;
    }

}
