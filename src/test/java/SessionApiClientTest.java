import com.okta.sdk.clients.SessionApiClient;
import com.okta.sdk.clients.UserApiClient;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.models.sessions.Session;
import com.okta.sdk.models.users.User;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SessionApiClientTest {

    static ApiClientConfiguration oktaSettings;
    static SessionApiClient sessionClient;

    @BeforeClass
    public void setUp() throws Exception {
        oktaSettings = new ApiClientConfiguration(TestConstants.getUrlPath(), TestConstants.getApiKey());

        sessionClient = new SessionApiClient(oktaSettings);
    }

    @Test
    public void CreateSessions() throws Exception {
        String userId = TestConstants.getAdminUserName();
        String password = TestConstants.getAdminPassword();

        // Build user client
        UserApiClient userClient = new UserApiClient(oktaSettings);
        User user = userClient.getUser(userId);

        // Session tests
        Session session = sessionClient.createSessionForMe();
        Assert.assertNotNull(session.getId());

        session = sessionClient.createSessionWithCredentials(userId, password);
        Assert.assertEquals(session.getUserId(), user.getId());

        session = sessionClient.createSessionWithCredentialsAndCookieToken(userId, password);
        Assert.assertNotNull(session.getCookieToken());

        session = sessionClient.createSessionWithCredentialsAndCookieTokenUrl(userId, password);
        Assert.assertNotNull(session.getCookieTokenUrl());
    }
}