/*
 * Copyright 2017 Okta, Inc.
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

public class ClientConfiguration {

    private String orgUrl;

    private String token;

    private int connectionTimeout = 30;

    private ClientProxyInfo proxy = new ClientProxyInfo();

    public String getOrgUrl() {
        return orgUrl;
    }

    public ClientConfiguration setOrgUrl(String orgUrl) {
        this.orgUrl = orgUrl;
        return this;
    }

    public String getToken() {
        return token;
    }

    public ClientConfiguration setToken(String token) {
        this.token = token;
        return this;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public ClientConfiguration setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public ClientProxyInfo getProxy() {
        return proxy;
    }

    public ClientConfiguration setProxy(ClientProxyInfo proxy) {
        this.proxy = proxy;
        return this;
    }

    public static class ClientProxyInfo {

        private String hostname;

        private int port;

        private String username;

        private String password;

        public String getHostname() {
            return hostname;
        }

        public ClientProxyInfo setHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public int getPort() {
            return port;
        }

        public ClientProxyInfo setPort(int port) {
            this.port = port;
            return this;
        }

        public String getUsername() {
            return username;
        }

        public ClientProxyInfo setUsername(String username) {
            this.username = username;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public ClientProxyInfo setPassword(String password) {
            this.password = password;
            return this;
        }
    }
}