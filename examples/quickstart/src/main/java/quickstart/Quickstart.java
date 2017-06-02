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
import com.okta.sdk.resource.GroupBuilder;
import com.okta.sdk.resource.ResourceException;
import com.okta.sdk.resource.UserBuilder;

import com.okta.sdk.resource.UserList;
import com.okta.sdk.resource.group.UserGroup;
import com.okta.sdk.resource.user.ActivationToken;
import com.okta.sdk.resource.user.User;

import java.util.UUID;

/**
 * This class demonstrates the code found in the Okta Java SDK QuickStart Guide
 *
 * @since 0.5.0
 */
@SuppressWarnings("PMD.UnusedLocalVariable")
public class Quickstart {

    public static void main(String[] args) {

        try {
            // Instantiate a builder for your Client. If needed, settings like Proxy and Caching can be defined here.
            ClientBuilder builder = Clients.builder();

            // No need to define anything else; build the Client instance. The ClientCredential information will be automatically found
            // in pre-defined locations.
            Client client = builder.build();


            // Create a group
            UserGroup group = GroupBuilder.INSTANCE
                    .setName("my-user-group-" + UUID.randomUUID().toString())
                    .setDescription("Quickstart created Group")
                    .buildAndCreate(client);

            println("Group: '" + group.getId() + "' was last updated on: " + group.getLastUpdated());


            // Create a User Account
            String email = "joe.coder+" + UUID.randomUUID().toString() + "@example.com";

            User user = UserBuilder.INSTANCE
                .setEmail(email)
                .setFirstName("Joe")
                .setLastName("Coder")
                .setPassword("Password1")
                .setSecurityQuestion("Favorite security question?")
                .setSecurityQuestionAnswer("None of them!")
//                .putProfileProperty("customProp", "Custom Value!")
                .addGroupId(group.getId())
                .buildAndCreate(client);

            String userId = user.getId();
            println("User created with ID: " + userId);

            // activate the new user
            ActivationToken activationToken = user.activate(false);
            // if you don't want Okta to email the user, you can grab the activation url/token and add it to your custom flow
            String activationUrl = activationToken.getActivationUrl();

            // You can look up user by ID
            println("User lookup by ID: "+ client.getUser(userId).getProfile().getLogin());

            // or by Email
            println("User lookup by Email: "+ client.getUser(email).getProfile().getLogin());


            // get the list of users
            UserList users = client.listUsers();

            // get the first user in the collection
            println("First user: " + users.iterator().next().getProfile().getEmail());

            // or loop through all of them (paging is automatic)
//        int ii = 0;
//        for (User user : users) {
//            println("["+ ii++ +"] User: " + user.getProfile().getEmail());
//        }

        }
        catch (ResourceException e) {

            // we can get the user friendly message from the Exception
            println(e.getMessage());

            // and you can get the details too
            e.getCauses().forEach( cause -> println("\t" + cause.getSummary()));
            throw e;
        }
    }

    private static void println(String message) {
        System.out.println(message);
        System.out.flush();
    }
}
