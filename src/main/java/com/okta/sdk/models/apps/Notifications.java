/*!
 * Copyright (c) 2015-2017, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.okta.sdk.models.apps;

import com.okta.sdk.framework.ApiObject;

public class Notifications extends ApiObject {
    public static class Vpn extends ApiObject {
        public static class Network extends ApiObject {
            /**
             * Connection of the network.
             */
            private String connection;

            /**
             * Return the connection.
             * @return {@link String}
             */
            public String getConnection() {
                return connection;
            }

            /**
             * Sets the connection.
             * @param val {@link String}
             */
            public void setConnection(String val) {
                this.connection = val;
            }
        }

        /**
         * Network object for VPN.
         */
        private Network network;

        /**
         * Message from VPN.
         */
        private String message;

        /**
         * URL for help.
         */
        private String helpUrl;

        /**
         * Return the network.
         * @return {@link Network}
         */
        public Network getNetwork() {
            return network;
        }

        /**
         * Set the network.
         * @param val {@link Network}
         */
        public void setNetwork(Network val) {
            this.network = val;
        }

        /**
         * Return the message.
         * @return {@link String}
         */
        public String getMessage() {
            return message;
        }

        /**
         * Set the message.
         * @param val {@link String}
         */
        public void setMessage(String val) {
            this.message = val;
        }

        /**
         * Return the helpUrl.
         * @return {@link String}
         */
        public String getHelpUrl() {
            return helpUrl;
        }

        /**
         * Set the helpUrl.
         * @param val {@link String}
         */
        public void setHelpUrl(String val) {
            this.helpUrl = val;
        }
    }

    /**
     * VPN connected.
     */
    private Vpn vpn;

    /**
     * Return the current VPN.
     * @return {@link Vpn}
     */
    public Vpn getVpn() {
        return vpn;
    }

    /**
     * Set the VPN.
     * @param val {@link Vpn}
     */
    public void setVpn(Vpn val) {
        this.vpn = val;
    }
}
