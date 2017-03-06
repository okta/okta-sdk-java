/*!
 * Copyright (c) 2015-2016, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

public class TestConfig {
    public class MockOkta {
        private Integer port;
        private String proxy;
        private String proxied;
        private String apiKey;

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getProxy() {
            return proxy;
        }

        public void setProxy(String proxy) {
            this.proxy = proxy;
        }

        public String getProxied() {
            return proxied;
        }

        public void setProxied(String proxied) {
            this.proxied = proxied;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }

    private MockOkta mockOkta;

    public MockOkta getMockOkta() {
        return this.mockOkta;
    }

    public void setMockOkta(MockOkta mockOkta) {
        this.mockOkta = mockOkta;
    }
}
