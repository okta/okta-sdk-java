package com.okta.sdk.impl.test;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.okta.sdk.client.AuthorizationMode;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.api.RoleApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import org.apache.commons.text.StringEscapeUtils;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Stream;

public class SpecialCharsTest {

    public static final String PRIVATE_KEY =
        "-----BEGIN PRIVATE KEY-----\n" +
        "PEM_KEY\n" +
        "Vq8nxYOpGVRtId7gbwRrywqC\n" +
        "-----END PRIVATE KEY-----\n";
    public static final String ORG_URL = "https://OKTA_DOMAIN";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String KID = "KID";

    @Test
    public void test() {
        ApiClient apiClient = Clients.builder()
            .setOrgUrl(ORG_URL)
            .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
            .setClientId(CLIENT_ID)
            .setPrivateKey(PRIVATE_KEY)
            .setKid(KID)
            .setScopes(ImmutableSet.of("okta.roles.read"))
            .build();
        RoleApi roleApi = new RoleApi(apiClient);
        " %-._~!$'()*,;&=@:+\\\"/#".chars()
            .mapToObj(c -> "prefix" + (char) c + "suffix")
            .forEach(label -> {
                try {
                    roleApi.getRole(label);
                } catch (ApiException e) {
                    String error;
                    List<String> wwwAuthenticate = e.getResponseHeaders().get("www-authenticate");
                    if (e.getCode() == 404){
                        if (e.getMessage().contains(StringEscapeUtils.escapeHtml4(label))) return;
                        error = "Unexpected 404 message: " + e.getMessage();
                    } else if (wwwAuthenticate != null) {
                        error = "www-authenticate error: " + wwwAuthenticate.get(0);
                    } else {
                        error = e.getCode() + " error: " + e.getMessage();
                    }
                    System.out.println("Failed '" + label + "': " + error);
                }
            });
    }

}
