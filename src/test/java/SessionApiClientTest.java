import com.okta.sdk.clients.AuthApiClient;
import com.okta.sdk.clients.SessionApiClient;
import com.okta.sdk.clients.UserApiClient;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.models.auth.AuthResult;
import com.okta.sdk.models.sessions.Session;
import com.okta.sdk.models.users.LoginCredentials;
import com.okta.sdk.models.users.Password;
import com.okta.sdk.models.users.User;
import com.okta.sdk.models.users.UserProfile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class SessionApiClientTest {

    private static final String PASSWORD = "tlpWENT2m";

    private UserApiClient userApiClient;
    private AuthApiClient authApiClient;
    private SessionApiClient sessionApiClient;

    private UserApiClient getUserClient(String description) throws Exception {
        Map<String, String> customHeaders = new HashMap<String, String>();
        customHeaders.put("x-test-description", description);
        TestConfig.MockOkta testConfig = Util.parseTestConfig().getMockOkta();
        return new UserApiClient(
                new ApiClientConfiguration(
                        String.format("%s:%d", testConfig.getProxy(), testConfig.getPort()),
                        testConfig.getApiKey(),
                        customHeaders
                )
        );
    }

    private AuthApiClient getAuthClient(String description) throws Exception {
        Map<String, String> customHeaders = new HashMap<String, String>();
        customHeaders.put("x-test-description", description);
        TestConfig.MockOkta testConfig = Util.parseTestConfig().getMockOkta();
        return new AuthApiClient(
                new ApiClientConfiguration(
                        String.format("%s:%d", testConfig.getProxy(), testConfig.getPort()),
                        testConfig.getApiKey(),
                        customHeaders
                )
        );
    }

    private SessionApiClient getSessionClient(String description) throws Exception {
        Map<String, String> customHeaders = new HashMap<String, String>();
        customHeaders.put("x-test-description", description);
        TestConfig.MockOkta testConfig = Util.parseTestConfig().getMockOkta();
        return new SessionApiClient(
                new ApiClientConfiguration(
                        String.format("%s:%d", testConfig.getProxy(), testConfig.getPort()),
                        testConfig.getApiKey(),
                        customHeaders
                )
        );
    }

    @BeforeMethod
    public void setUp() throws Exception {
        userApiClient = getUserClient("/api/v1/sessions/:id/lifecycle/refresh - refreshes a session");
        authApiClient = getAuthClient("/api/v1/sessions/:id/lifecycle/refresh - refreshes a session");
        sessionApiClient = getSessionClient("/api/v1/sessions/:id/lifecycle/refresh - refreshes a session");
    }

    @Test
    public void testRefreshSession() throws Exception {
        User user = userApiClient.createUser(getUser(), true);
        AuthResult authResult = authApiClient.authenticate(user.getProfile().getLogin(), PASSWORD, "");
        Session session = sessionApiClient.createSessionWithSessionToken(authResult.getSessionToken());
        Session refreshedSession = sessionApiClient.refreshSession(session.getId());
    }

    private User getUser() {
        User user = new User();

        UserProfile profile = new UserProfile();
        profile.setFirstName("Sessions");
        profile.setFirstName("McJanky");
        profile.setEmail("mocktestexample-sessions@mocktestexample.com");
        profile.setLogin("mocktestexample-sessions@mocktestexample.com");
        profile.setMobilePhone("555-415-1337");

        LoginCredentials credentials = new LoginCredentials();
        Password password = new Password();
        password.setValue(PASSWORD);
        credentials.setPassword(password);

        user.setProfile(profile);
        user.setCredentials(credentials);

        return user;
    }
}
