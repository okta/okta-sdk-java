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
import com.okta.sdk.clients.UserGroupApiClient;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.models.usergroups.UserGroup;
import com.okta.sdk.models.usergroups.UserGroupProfile;
import com.okta.sdk.models.users.User;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserGroupApiClientTest {

    public UserGroupApiClient getClient(String description) throws Exception {
        Map customHeaders = new HashMap();
        customHeaders.put("x-test-description", description);
        TestConfig.MockOkta testConfig = Util.parseTestConfig().getMockOkta();
        return new UserGroupApiClient(
                new ApiClientConfiguration(
                        String.format("%s:%d", testConfig.getProxy(), testConfig.getPort()),
                        testConfig.getApiKey(),
                        customHeaders
                )
        );
    }

    public UserApiClient getUserClient(String description) throws Exception {
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

    private UserGroup findUserGroup(List<UserGroup> userGroups, String name) throws Exception {
        for (UserGroup userGroup : userGroups) {
            if (userGroup.getProfile().getName().equals(name)) {
                return userGroup;
            }
        }
        throw new Exception("No group found with name of " + name);
    }

    @Test
    public void testRequestsAGroup() throws Exception {
        UserGroupApiClient userGroupApiClient = getClient("/api/v1/groups/:id - requests a group");
        List<UserGroup> userGroups = userGroupApiClient.getUserGroupsWithLimit(-1);
        userGroupApiClient.getUserGroup(userGroups.get(0).getId());
    }

    @Test
    public void testCreatesAGroup() throws Exception {
        UserGroupApiClient userGroupApiClient = getClient("/api/v1/groups/:id - creates a group");
        UserGroup userGroup = new UserGroup();
        UserGroupProfile userGroupProfile = new UserGroupProfile();
        userGroupProfile.setName("West Coast Users");
        userGroupProfile.setDescription("Straight Outta Compton");
        userGroup.setProfile(userGroupProfile);
        userGroupApiClient.createUserGroup(userGroup);
    }

    @Test
    public void testUpdatesAGroup() throws Exception {
        UserGroupApiClient userGroupApiClient = getClient("/api/v1/groups/:id - updates a group");
        List<UserGroup> userGroups = userGroupApiClient.getUserGroupsWithLimit(-1);
        UserGroup userGroup = findUserGroup(userGroups, "TestGroup");
        UserGroup updatedGroup = new UserGroup();
        UserGroupProfile userGroupProfile = new UserGroupProfile();
        userGroupProfile.setName("TestGroup");
        userGroupProfile.setDescription("Amended description");
        updatedGroup.setProfile(userGroupProfile);
        userGroupApiClient.updateUserGroup(userGroup.getId(), updatedGroup);
    }

    @Test
    public void testDeletesAGroup() throws Exception {
        UserGroupApiClient userGroupApiClient = getClient("/api/v1/groups/:id - deletes a group");
        List<UserGroup> userGroups = userGroupApiClient.getUserGroupsWithLimit(-1);
        UserGroup userGroup = findUserGroup(userGroups, "DeleteGroup");
        userGroupApiClient.deleteUserGroup(userGroup.getId());
    }

    @Test
    public void testRequestGroupMembers() throws Exception {
        UserGroupApiClient userGroupApiClient = getClient("/api/v1/groups/:id - request group members");
        List<UserGroup> userGroups = userGroupApiClient.getUserGroupsWithLimit(-1);
        userGroupApiClient.getUsers(userGroups.get(0).getId());
    }

    @Test
    public void testAddUserToGroup() throws Exception {
        String testDescription = "/api/v1/groups/:id - add user to group";
        UserGroupApiClient userGroupApiClient = getClient(testDescription);
        UserApiClient userApiClient = getUserClient(testDescription);
        List<UserGroup> userGroups = userGroupApiClient.getUserGroupsWithLimit(-1);
        UserGroup userGroup = findUserGroup(userGroups, "TestGroup");
        User user = userApiClient.getUser("mocktestexample-frutis@mocktestexample.com");
        userGroupApiClient.addUserToGroup(userGroup.getId(), user.getId());
    }

}
