import com.okta.sdk.clients.UserApiClient;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.Filter;
import com.okta.sdk.framework.Filters;
import com.okta.sdk.framework.PagedResults;
import com.okta.sdk.models.users.UserProfile;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;
import com.okta.sdk.models.users.User;

import java.util.List;
import java.util.Random;

public class UserApiClientTest {

    static ApiClientConfiguration oktaSettings;
    static UserApiClient usersClient;
    static Random random;

    @BeforeClass
    public void setUp() throws Exception {
        oktaSettings = new ApiClientConfiguration(TestConstants.getUrlPath(), TestConstants.getApiKey());

        usersClient = new UserApiClient(oktaSettings);

        random = new Random();
    }

    @Test
    public void testCRUD() throws Exception {
        // Create
        User newUser = usersClient.createUser(
                "First",
                "Last",
                "login" + Integer.toString(random.nextInt()) + "@example.com",
                "email" + Integer.toString(random.nextInt()) + "@example.com",
                true);

        // Read
        User user = usersClient.getUser(newUser.getId());
        Assert.assertTrue(newUser.getId().equals(user.getId()));

        // Update
        user.getProfile().setLastName("NewLast");
        user = usersClient.updateUser(user);

        // Delete
        usersClient.deleteUser(user.getId());
    }

    @Test
    public void testListUsers() throws Exception {
        List<User> users = usersClient.getUsers();
        Assert.assertTrue(users.size() > 1);
    }

    @Test
    public void testListUsersWithPagination() throws Exception {
        PagedResults<User> pagedResults = usersClient.getUsersPagedResultsWithLimit(1);

        int counter = 0;
        do {
            if(!pagedResults.isFirstPage()) {
                pagedResults = usersClient.getUsersPagedResultsByUrl(pagedResults.getNextUrl());
            }

            for(User user : pagedResults.getResult()) {
                counter++;
            }
        }
        while(!pagedResults.isLastPage());

        Assert.assertTrue(counter > 1);
    }

    @Test
    public void testSetRecoveryQuestion() throws Exception {

        // Create a user and activate a user
        String username = "fakeuser" + random.nextInt() + "@fake.com";
        String password = "A1a!" + random.nextInt();
        usersClient.createUser("First", "Last", username, username, true);

        // Set their recovery question, it shouldn't throw an exception
        usersClient.setRecoveryQuestion(username, "What is your favorite color?", "Blue, no green");
    }
}