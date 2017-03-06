/*!
 * Copyright (c) 2015-2016, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

import com.okta.sdk.clients.UserApiClient;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.models.users.User;
import com.okta.sdk.models.users.UserProfile;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserApiClientTest {

    public UserApiClient getClient(String description) throws Exception {
        Map customHeaders = new HashMap();
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

    @Test
    public void testRequestsAUser() throws Exception {
        UserApiClient userApiClient = getClient("/api/v1/users/:id - requests a user");
        List<User> users = userApiClient.getUsersWithLimit(-1);
        userApiClient.getUser(users.get(0).getId());
    }

    @Test
    public void testCreateUserWithoutCredentials() throws Exception {
        UserApiClient userApiClient = getClient("/api/v1/users/:id - creates a user without credentials");
        userApiClient.createUser(
            "First",
            "McJanky",
            "mocktestexample-brutis@mocktestexample.com",
            "mocktestexample-brutis@mocktestexample.com"
        );
    }

    @Test
    public void testUpdatesAUser() throws Exception {
        UserApiClient userApiClient = getClient("/api/v1/users/:id - updates a user");
        User user = userApiClient.getUser("mocktestexample-frutis@mocktestexample.com");
        UserProfile userProfile = user.getProfile();
        userProfile.setFirstName("NewFirst");
        userProfile.setLastName("McJanky");
        userProfile.setEmail("mocktestexample-frutis@mocktestexample.com");
        userProfile.setLogin("mocktestexample-frutis@mocktestexample.com");
        User updatedUser = new User();
        updatedUser.setProfile(userProfile);
        userApiClient.updateUser(user.getId(), updatedUser);
    }

    @Test
    public void testDeletesAUser() throws Exception {
        UserApiClient userApiClient = getClient("/api/v1/users/:id - deletes a user");
        User user = userApiClient.getUser("mocktestexample-deleteme@mocktestexample.com");
        userApiClient.deleteUser(user.getId());
    }

    @Test
    public void testChangesUserPassword() throws Exception {
        UserApiClient userApiClient = getClient("/api/v1/users/:id/credentials - change a user password");
        User user = userApiClient.getUser("mocktestexample-frutis@mocktestexample.com");
        userApiClient.changePassword(user.getId(), "Asdf1234", "Asdf1234!");

    }

    @Test
    public void testExpiresUserPassword() throws Exception {
        UserApiClient userApiClient = getClient("/api/v1/users/:id/credentials - expires a user password");
        User user = userApiClient.getUser("mocktestexample-frutis@mocktestexample.com");
        userApiClient.expirePassword(user.getId());
    }

    @Test
    public void testResetsUserPassword() throws Exception {
        UserApiClient userApiClient = getClient("/api/v1/users/:id/lifecycle - reset a user password");
        User user = userApiClient.getUser("mocktestexample-frutis@mocktestexample.com");
        userApiClient.resetPassword(user.getId(), false);
    }

    @Test
    public void testActivatesUser() throws Exception {
        UserApiClient userApiClient = getClient("/api/v1/users/:id/lifecycle - activates a user");
        User user = userApiClient.getUser("mocktestexample-deactive@mocktestexample.com");
        userApiClient.activateUser(user.getId(), true);
    }

    @Test
    public void testDeactivatesUser() throws Exception {
        UserApiClient userApiClient = getClient("/api/v1/users/:id/lifecycle - deactivates a user");
        User user = userApiClient.getUser("mocktestexample-deactive@mocktestexample.com");
        userApiClient.deactivateUser(user.getId());
    }

}
