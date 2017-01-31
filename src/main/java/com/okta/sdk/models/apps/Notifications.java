package com.okta.sdk.models.apps;

public class Notifications {
    public static class Vpn {
        public static class Network {
            private String connection;

            public String getConnection() {
                return connection;
            }

            public void setConnection(String connection) {
                this.connection = connection;
            }
        }

        private Network network;
        private String message;
        private String helpUrl;

        public Network getNetwork() {
            return network;
        }

        public void setNetwork(Network network) {
            this.network = network;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getHelpUrl() {
            return helpUrl;
        }

        public void setHelpUrl(String helpUrl) {
            this.helpUrl = helpUrl;
        }
    }

    private Vpn vpn;

    public Vpn getVpn() {
        return vpn;
    }

    public void setVpn(Vpn vpn) {
        this.vpn = vpn;
    }
}
