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

import com.okta.sdk.client.Client;
import com.okta.sdk.client.ClientBuilder;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.group.GroupBuilder;
import com.okta.sdk.resource.ResourceException;
import com.okta.sdk.resource.user.UserBuilder;

import com.okta.sdk.resource.group.Group;
import com.okta.sdk.resource.group.User;
import com.okta.sdk.resource.group.UserList;
import com.okta.sdk.resource.group.UserStatus;

import java.util.UUID;

/**
 * This class demonstrates the code found in the Okta Java SDK QuickStart Guide
 *
 * @since 0.5.0
 */
@SuppressWarnings("PMD.UnusedLocalVariable")
public class Quickstart {

    public static void main(String[] args) {

        final String email = "joe.coder+" + UUID.randomUUID().toString() + "@example.com";
        final String groupName = "java-sdk-quickstart-" + UUID.randomUUID().toString();
        final char[] password = {'P','a','s','s','w','o','r','d','1'};

        ClientBuilder builder;
        Client client = null;
        Group group = null;
        User user = null;

        try {
            // Instantiate a builder for your Client. If needed, settings like Proxy and Caching can be defined here.
            builder = Clients.builder();

            // No need to define anything else; build the Client instance. The ClientCredential information will be automatically found
            // in pre-defined locations: i.e. ~/.okta/okta.yaml
            client = builder.build();

            // Create a group
            group = GroupBuilder.instance()
                    .setName(groupName)
                    .setDescription("Quickstart created Group")
                    .buildAndCreate(client);

            println("Group: '" + group.getId() + "' was last updated on: " + group.getLastUpdated());

            // Create a User Account
            user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName("Joe")
                .setLastName("Coder")
                .setPassword(password)
                .setSecurityQuestion("Favorite security question?")
                .setSecurityQuestionAnswer("None of them!")
                .putProfileProperty("division", "Seven") // key/value pairs predefined in the user profile schema
                .setActive(true)
                .buildAndCreate(client);

            // add user to the newly created group
            client.addUserToGroup(group.getId(), user.getId());

            String userId = user.getId();
            println("User created with ID: " + userId);

            // You can look up user by ID
            println("User lookup by ID: "+ client.getUser(userId).getProfile().getLogin());

            // or by Email
            println("User lookup by Email: "+ client.getUser(email).getProfile().getLogin());


            // get the list of users
            UserList users = client.listUsers();

            // get the first user in the collection
            println("First user in collection: " + users.iterator().next().getProfile().getEmail());

            // or loop through all of them (paging is automatic)
//            int ii = 0;
//            for (User tmpUser : users) {
//                println("["+ ii++ +"] User: " + tmpUser.getProfile().getEmail());
//            }

        }
        catch (ResourceException e) {

            // we can get the user friendly message from the Exception
            println(e.getMessage());

            // and you can get the details too
            e.getCauses().forEach( cause -> println("\t" + cause.getSummary()));
            throw e;
        }
        finally {
            // cleanup

            // deactivate (if de-provisioned) and delete user
            if (user != null) {
                if (user.getStatus() != UserStatus.DEPROVISIONED) {
                    client.deactivateOrDeleteUser(user.getId());
                }
            }

            // delete group
            if (group != null) {
                client.deleteGroup(group.getId());
            }
        }
    }

    private static void println(String message) {
        System.out.println(message);
        System.out.flush();
    }
}
