/*
 * Copyright 2017 Okta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package quickstart;

import org.openapitools.client.ApiClient;
import com.okta.sdk.client.Clients;
import com.okta.sdk.client.ClientBuilder;
import com.okta.sdk.resource.group.GroupBuilder;
import com.okta.sdk.resource.user.UserBuilder;

import org.openapitools.client.ApiException;
import org.openapitools.client.api.UserApi;
import org.openapitools.client.api.GroupApi;

import org.openapitools.client.model.Group;
import org.openapitools.client.model.User;
import org.openapitools.client.model.UserStatus;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * This class demonstrates the code found in the Okta Java SDK QuickStart Guide
 *
 * @since 0.5.0
 */
@SuppressWarnings("PMD.UnusedLocalVariable")
public class Quickstart {

    public static void main(String[] args) throws ApiException {

        final String email = "joe.coder+" + UUID.randomUUID() + "@example.com";
        final String groupName = "java-sdk-quickstart-" + UUID.randomUUID();
        final char[] password = {'$','D','o','l','l','a','r','d','i','m','e','1','2','3','*'};

        ClientBuilder builder;
        ApiClient client;
        Group group = null;
        User user = null;

        UserApi userApi = null;
        GroupApi groupApi = null;

        try {
            // Instantiate a builder for your Client. If needed, settings like Proxy and Caching can be defined here.
            builder = Clients.builder();

            // No need to define anything else; build the Client instance. The ClientCredential information will be automatically found
            // in pre-defined locations: i.e. ~/.okta/okta.yaml
            client = builder.build();

            userApi = new UserApi(client);
            groupApi = new GroupApi(client);

            // Create a group
            group = GroupBuilder.instance()
                    .setName(groupName)
                    .setDescription("Quickstart created Group")
                    .buildAndCreate(groupApi);

            println("Group: '" + group.getId() + "' was last updated on: " + group.getLastUpdated());

            // Create a User Account
            user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName("Joe")
                .setLastName("Coder")
                .setPassword(password)
                .setSecurityQuestion("Favorite security question?")
                .setSecurityQuestionAnswer("None of them!")
                //.setCustomProfileProperty("foo", "bar") // pre-req: custom profile properties need to be set in your Org's Profile editor via Admin UI.
                .setActive(true)
                .addGroup(group.getId()) // add user to the newly created group
                .buildAndCreate(userApi);

            String userId = user.getId();
            println("User created with ID: " + userId);

            // You can look up user by ID
            println("User lookup by ID: "+ Objects.requireNonNull(userApi.getUser(userId).getProfile()).getLogin());

            // or by Email
            println("User lookup by Email: "+ Objects.requireNonNull(userApi.getUser(email).getProfile()).getLogin());

            // get the list of users
            List<User> users = userApi.listUsers(null, null, null, "status eq \"ACTIVE\"", null, null, null);

            // get the first user in the collection
            println("First user in collection: " + Objects.requireNonNull(Objects.requireNonNull(users.stream().findFirst().orElse(null)).getProfile()).getEmail());
        }
        catch (Exception e) {

            // we can get the user-friendly message from the Exception
            println(e.getMessage());
            throw e;
        }
        finally {
            // cleanup

            // deactivate (if de-provisioned) and delete user
            if (user != null) {
                if (!Objects.equals(user.getStatus(), UserStatus.DEPROVISIONED)) {
                    userApi.deactivateUser(user.getId(), false);
                }
                userApi.deleteUser(user.getId(), false);
            }

            // delete group
            if (group != null) {
                groupApi.deleteGroup(group.getId());
            }
        }
    }

    private static void println(String message) {
        System.out.println(message);
        System.out.flush();
    }
}
