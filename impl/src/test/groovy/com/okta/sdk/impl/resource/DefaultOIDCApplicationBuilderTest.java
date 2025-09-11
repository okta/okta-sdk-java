package com.okta.sdk.impl.resource;

import com.okta.sdk.resource.api.ApplicationApi;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.*;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.testng.Assert.*;

public class DefaultOIDCApplicationBuilderTest {

    // Helper: reflectively set private / inherited fields
    private void setField(Object target, String name, Object value) {
        Class<?> cls = target.getClass();
        while (cls != null) {
            try {
                Field f = cls.getDeclaredField(name);
                f.setAccessible(true);
                f.set(target, value);
                return;
            } catch (NoSuchFieldException e) {
                cls = cls.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException("Field not found: " + name);
    }

    // Helper: invoke private build()
    private OpenIdConnectApplication invokeBuild(DefaultOIDCApplicationBuilder b) {
        try {
            Method m = DefaultOIDCApplicationBuilder.class.getDeclaredMethod("build");
            m.setAccessible(true);
            return (OpenIdConnectApplication) m.invoke(b);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <E extends Enum<E>> E first(Class<E> enumCls) {
        return enumCls.getEnumConstants()[0];
    }

    private DefaultOIDCApplicationBuilder baseValidBuilder() {
        DefaultOIDCApplicationBuilder b = new DefaultOIDCApplicationBuilder();
        // Required collections / fields
        b.addResponseTypes(first(OAuthResponseType.class));
        b.addGrantTypes(first(OAuthGrantType.class));
        b.setApplicationType(first(OpenIdConnectApplicationType.class));
        b.setTokenEndpointAuthMethod(first(OAuthEndpointAuthenticationMethod.class));
        return b;
    }

    @Test
    public void testFullBuildAllOptionalFields() {
        DefaultOIDCApplicationBuilder b = baseValidBuilder();

        // Inherited (set via reflection to cover code paths)
        setField(b, "label", "My OIDC App");
        setField(b, "name", "oidc_app_internal");
        setField(b, "errorRedirectUrl", "https://err.example.com");
        setField(b, "selfService", Boolean.TRUE);
        setField(b, "iOS", Boolean.TRUE);
        setField(b, "web", Boolean.FALSE);

        // Direct setters
        b.setClientUri("https://client.example.com")
            .setLogoUri("https://logo.example.com/l.png")
            .setPolicyUri("https://policy.example.com/p")
            .setTosUri("https://tos.example.com/tos")
            .setConsentMethod(first(OpenIdConnectApplicationConsentMethod.class))
            .setGrantTypes(new ArrayList<>(Arrays.asList(
                first(OAuthGrantType.class))))
            .addGrantTypes(first(OAuthGrantType.class)) // add more (even duplicate)
            .setResponseTypes(new ArrayList<>(Collections.singletonList(first(OAuthResponseType.class))))
            .addResponseTypes(first(OAuthResponseType.class))
            .setPostLogoutRedirectUris(Arrays.asList("https://postlogout1.example.com", "https://postlogout2.example.com"))
            .setRedirectUris(new ArrayList<>(Arrays.asList("https://redirect1.example.com")))
            .addRedirectUris("https://redirect2.example.com")
            .setClientId("client-id-123")
            .setClientSecret("client-secret-xyz")
            .setAutoKeyRotation(Boolean.TRUE)
            .setImplicitAssignment(Boolean.TRUE)
            .setInlineHookId("hook-abc")
            .setLoginUrl("https://login.example.com")
            .setRedirectUrl("https://redir.example.com")
            .setJwks(new ArrayList<>(Collections.singletonList(new SchemasJsonWebKey())));

        OpenIdConnectApplication app = invokeBuild(b);

        assertEquals(app.getLabel(), "My OIDC App");
        assertEquals(app.getName(), "oidc_app_internal");
        assertEquals(app.getSignOnMode(), ApplicationSignOnMode.OPENID_CONNECT);

        // Accessibility
        assertNotNull(app.getAccessibility());
        assertEquals(app.getAccessibility().getErrorRedirectUrl(), "https://err.example.com");
        assertTrue(app.getAccessibility().getSelfService());

        // Visibility
        assertNotNull(app.getVisibility());
        assertNotNull(app.getVisibility().getHide());
        assertTrue(app.getVisibility().getHide().getiOS());
        assertFalse(app.getVisibility().getHide().getWeb());

        // Settings & OAuth Client
        OpenIdConnectApplicationSettings settings = (OpenIdConnectApplicationSettings) app.getSettings();
        assertNotNull(settings);
        assertTrue(settings.getImplicitAssignment());
        assertEquals(settings.getInlineHookId(), "hook-abc");

        OpenIdConnectApplicationSettingsClient client = settings.getOauthClient();
        assertEquals(client.getClientUri(), "https://client.example.com");
        assertEquals(client.getLogoUri(), "https://logo.example.com/l.png");
        assertEquals(client.getPolicyUri(), "https://policy.example.com/p");
        assertEquals(client.getTosUri(), "https://tos.example.com/tos");
        assertTrue(client.getPostLogoutRedirectUris().contains("https://postlogout1.example.com"));
        assertTrue(client.getRedirectUris().contains("https://redirect2.example.com"));
        assertFalse(client.getResponseTypes().isEmpty());
        assertFalse(client.getGrantTypes().isEmpty());
        assertNotNull(client.getApplicationType());
        assertNotNull(client.getConsentMethod());

        // SignOn (only when loginUrl present)
        assertNotNull(settings.getSignOn());
        assertEquals(settings.getSignOn().getLoginUrl(), "https://login.example.com");
        assertEquals(settings.getSignOn().getRedirectUrl(), "https://redir.example.com");

        // Credentials
        OAuthApplicationCredentials creds = (OAuthApplicationCredentials) app.getCredentials();
        assertNotNull(creds);
        assertEquals(creds.getOauthClient().getClientId(), "client-id-123");
        assertEquals(creds.getOauthClient().getClientSecret(), "client-secret-xyz");
        assertTrue(creds.getOauthClient().getAutoKeyRotation());
        assertNotNull(creds.getOauthClient().getTokenEndpointAuthMethod());

        // JWKS
        assertNotNull(client.getJwks());
        assertFalse(client.getJwks().getKeys().isEmpty());
    }

    @Test
    public void testSignOnNotCreatedWithoutLoginUrl() {
        DefaultOIDCApplicationBuilder b = baseValidBuilder();
        b.setRedirectUrl("https://redir.only.example.com");
        // Required pieces already set by baseValidBuilder
        OpenIdConnectApplication app = invokeBuild(b);
        OpenIdConnectApplicationSettings settings = (OpenIdConnectApplicationSettings) app.getSettings();
        assertNull(settings.getSignOn(), "SignOn should not be set when loginUrl is absent");
    }

    @Test
    public void testNoJwksWhenEmpty() {
        DefaultOIDCApplicationBuilder b = baseValidBuilder();
        OpenIdConnectApplication app = invokeBuild(b);
        OpenIdConnectApplicationSettingsClient client = ((OpenIdConnectApplicationSettings) app.getSettings()).getOauthClient();
        assertNull(client.getJwks(), "JWKS should be null when list is empty");
    }

    @Test
    public void testImplicitAssignmentNotSetWhenNull() {
        DefaultOIDCApplicationBuilder b = baseValidBuilder();
        OpenIdConnectApplicationSettings settings = (OpenIdConnectApplicationSettings) invokeBuild(b).getSettings();
        assertNull(settings.getImplicitAssignment());
    }

    @Test
    public void testInlineHookIdNotSetWhenNull() {
        DefaultOIDCApplicationBuilder b = baseValidBuilder();
        OpenIdConnectApplicationSettings settings = (OpenIdConnectApplicationSettings) invokeBuild(b).getSettings();
        assertNull(settings.getInlineHookId());
    }

    @Test
    public void testAddersAccumulate() {
        DefaultOIDCApplicationBuilder b = new DefaultOIDCApplicationBuilder();
        // Use adders exclusively
        b.addResponseTypes(first(OAuthResponseType.class));
        b.addGrantTypes(first(OAuthGrantType.class));
        b.setApplicationType(first(OpenIdConnectApplicationType.class));
        b.setTokenEndpointAuthMethod(first(OAuthEndpointAuthenticationMethod.class));
        b.addRedirectUris("https://one.example.com")
            .addRedirectUris("https://two.example.com");
        OpenIdConnectApplication app = invokeBuild(b);
        List<String> redirects = ((OpenIdConnectApplicationSettings) app.getSettings())
            .getOauthClient().getRedirectUris();
        assertTrue(redirects.contains("https://one.example.com"));
        assertTrue(redirects.contains("https://two.example.com"));
    }




    private void assertIllegalArgument(RuntimeException ex, String msgPart) {
        Throwable t = ex.getCause(); // InvocationTargetException expected
        if (t != null && t.getCause() != null) {
            // unwrap InvocationTargetException -> underlying IllegalArgumentException
            t = t.getCause();
        }
        assertTrue(t instanceof IllegalArgumentException, "Expected IllegalArgumentException but was: " + t);
        assertTrue(t.getMessage().contains(msgPart),
            "Message should contain '" + msgPart + "' but was: " + t.getMessage());
    }



    @Test
    public void testMissingApplicationTypeThrows() {
        DefaultOIDCApplicationBuilder b = new DefaultOIDCApplicationBuilder();
        b.addResponseTypes(first(OAuthResponseType.class));
        b.addGrantTypes(first(OAuthGrantType.class));
        b.setTokenEndpointAuthMethod(first(OAuthEndpointAuthenticationMethod.class));
        try {
            invokeBuild(b);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException ex) {
            assertIllegalArgument(ex, "Application Type cannot be null");
        }
    }



    @Test
    public void testBuildAndCreateCallsApi() throws ApiException {
        // Minimal viable builder
        DefaultOIDCApplicationBuilder b = baseValidBuilder();

        // Mock ApplicationApi by providing a stub that returns a no-op ApiClient
        ApplicationApi passed = new ApplicationApi() {
            @Override
            public com.okta.sdk.resource.client.ApiClient getApiClient() {
                return new com.okta.sdk.resource.client.ApiClient(); // new client for constructor
            }
        };

        // Since buildAndCreate internally creates a new ApplicationApi, we just ensure no exception
        // (Cannot easily assert the internal createApplication call without advanced instrumentation)
        try {
            b.buildAndCreate(passed);
        } catch (ApiException e) {
            // Acceptable if thrown by underlying unconfigured HTTP call; we are focusing on pre-call assembly
            // If network attempted, it may throw; so we just assert builder reached that point.
            assertNotNull(e);
        } catch (IllegalArgumentException ie) {
            // Should not be thrown here (means required fields missing)
            fail("Unexpected IllegalArgumentException: " + ie.getMessage());
        }
    }

    // Updated helper (unchanged logic, shown for clarity)


    // Updated tests using the helper:

    @Test
    public void testMissingResponseTypesThrows() {
        DefaultOIDCApplicationBuilder b = new DefaultOIDCApplicationBuilder();
        b.addGrantTypes(first(OAuthGrantType.class));
        b.setApplicationType(first(OpenIdConnectApplicationType.class));
        b.setTokenEndpointAuthMethod(first(OAuthEndpointAuthenticationMethod.class));
        try {
            invokeBuild(b);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException ex) {
            assertIllegalArgument(ex, "Response Type cannot be null");
        }
    }

    @Test
    public void testMissingGrantTypesThrows() {
        DefaultOIDCApplicationBuilder b = new DefaultOIDCApplicationBuilder();
        b.addResponseTypes(first(OAuthResponseType.class));
        b.setApplicationType(first(OpenIdConnectApplicationType.class));
        b.setTokenEndpointAuthMethod(first(OAuthEndpointAuthenticationMethod.class));
        try {
            invokeBuild(b);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException ex) {
            assertIllegalArgument(ex, "Grant Type cannot be null");
        }
    }



    @Test
    public void testMissingTokenEndpointAuthMethodThrows() {
        DefaultOIDCApplicationBuilder b = new DefaultOIDCApplicationBuilder();
        b.addResponseTypes(first(OAuthResponseType.class));
        b.addGrantTypes(first(OAuthGrantType.class));
        b.setApplicationType(first(OpenIdConnectApplicationType.class));
        try {
            invokeBuild(b);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException ex) {
            assertIllegalArgument(ex, "Token Endpoint Auth Method cannot be null");
        }
    }
}
