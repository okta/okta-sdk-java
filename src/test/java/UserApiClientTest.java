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
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserApiClientTest {

    public UserApiClient getClient(String description) {
        Map customHeaders = new HashMap();
        customHeaders.put("x-test-description", description);
        return new UserApiClient(
                new ApiClientConfiguration(
                        "http://localhost:3000",
                        "00ZecD9pl8qikDoFhKQNIuiFrU8r8UQCQZzCag_rlb",
                        customHeaders
                )
        );
    }

    @Test
    public void testRequestsUsers() throws Exception {
        UserApiClient userApiClient = getClient("/api/v1/users - requests users");
        userApiClient.getUsersWithLimit(-1);
    }

    @Test
    public void testCreateUserWithoutCredentials() throws Exception {
        UserApiClient userApiClient = getClient("/api/v1/users/:id - creates a user without credentials");
        userApiClient.createUser("First", "McJanky", "brutis.mcjanky@example.com", "brutis.mcjanky@example.com");
    }

    @Test
    public void testRequestsAUser() throws Exception {
        UserApiClient userApiClient = getClient("/api/v1/users/:id - requests a user");
        List<User> users = userApiClient.getUsersWithLimit(-1);
        userApiClient.getUser(users.get(0).getId());
    }

}
