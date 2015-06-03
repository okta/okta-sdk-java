import com.okta.sdk.clients.FactorsApiClient;
import com.okta.sdk.clients.UserApiClient;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.models.factors.Factor;
import com.okta.sdk.models.users.User;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Random;

public class FactorApiClientTest {

    static ApiClientConfiguration oktaSettings;
    static FactorsApiClient factorsClient;
    static UserApiClient usersClient;
    static Random random;

    @BeforeClass
    public void setUp() throws Exception {
        oktaSettings = new ApiClientConfiguration(TestConstants.getUrlPath(), TestConstants.getApiKey());

        factorsClient = new FactorsApiClient(oktaSettings);
        usersClient = new UserApiClient(oktaSettings);

        random = new Random();
    }

    @Test
    public void testEnrollQuestion() throws Exception {
        // Create a user
        User newUser = usersClient.createUser(
                "First",
                "Last",
                "login" + Integer.toString(random.nextInt()) + "@example.com",
                "email" + Integer.toString(random.nextInt()) + "@example.com",
                true);

        // Enroll the user
        Factor factor = factorsClient.enrollSecurityQuestion(newUser.getId(), "disliked_food", "mayonnaise");
        Assert.assertNotNull(factor.getId());
    }
}