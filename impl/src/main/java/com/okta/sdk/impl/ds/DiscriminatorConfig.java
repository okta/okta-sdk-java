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
package com.okta.sdk.impl.ds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * Simple configuration bean which allows the {@link DiscriminatorRegistry} configuration to be loaded from a file.
 *
 * @since 0.8.0
 */
public class DiscriminatorConfig {

    private static final Logger logger = LoggerFactory.getLogger(DiscriminatorConfig.class);

    private Map<String, ClassConfig> config;

    public Map<String, ClassConfig> getConfig() {
        return config;
    }

    public void setConfig(Map<String, ClassConfig> config) {
        this.config = config;
    }

    public static class ClassConfig {

        private String fieldName;

        private Map<String, String> values;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public Map<String, String> getValues() {
            return values;
        }

        public void setValues(Map<String, String> values) {
            this.values = values;
        }
    }

    /**
     * Loads DiscriminatorConfig from the classpath file: {code}/com/okta/sdk/resource/discrimination.yaml{code}.
     * If this file cannot be found an empty DiscriminatorConfig is returned.
     *
     * @return a DiscriminatorConfig based on the discrimination.yaml found on the classpath.
     */
    static DiscriminatorConfig loadConfig() {
        Yaml yaml = new Yaml();
        String configFile = "/com/okta/sdk/resource/discrimination.yaml";
        URL configYaml = DefaultDiscriminatorRegistry.class.getResource(configFile);
        if (configYaml != null) {
            try (InputStream is = configYaml.openStream()){
                return yaml.loadAs(is, DiscriminatorConfig.class);

            } catch (IOException e) {
                logger.warn("Failed to load config file: {}", configFile, e);
            }
        }
        else {
            logger.warn("Could not find config file on the classpath: {}", configFile);
        }
        return new DiscriminatorConfig();
    }
}