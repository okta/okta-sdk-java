import com.okta.sdk.clients.UserGroupApiClient;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.PagedResults;
import com.okta.sdk.models.usergroups.UserGroup;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class UserGroupApiClientTest {

    static ApiClientConfiguration oktaSettings;
    static UserGroupApiClient userGroupClient;
    static Random random;

    @BeforeClass
    public void setUp() throws Exception {
        oktaSettings = new ApiClientConfiguration(TestConstants.getUrlPath(), TestConstants.getApiKey());

        userGroupClient = new UserGroupApiClient(oktaSettings);

        random = new Random();
    }

    @Test
    public void testGetList() throws Exception {
        List<UserGroup> userGroups = userGroupClient.getUserGroupsWithLimit(1);
        Assert.assertTrue(userGroups.size() > 0, "There should be at least one UserGroup returned");
    }
}