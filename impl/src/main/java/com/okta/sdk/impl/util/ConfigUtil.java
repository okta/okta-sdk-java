package com.okta.sdk.impl.util;

public class ConfigUtil {

    /**
     * Check if the PEM key has BEGIN content wrapper.
     *
     * @param key the supplied key has BEGIN wrapper/header
     * @return
     */
    public static boolean hasPrivateKeyContentWrapper(String key) {
        return key.startsWith("-----BEGIN");
    }
}
