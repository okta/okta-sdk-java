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
import com.okta.sdk.resource.ActivationToken;
import com.okta.sdk.resource.InputUserWithGroupIds;
import com.okta.sdk.resource.PasswordCredential;
import com.okta.sdk.resource.ResourceException;
import com.okta.sdk.resource.User;
import com.okta.sdk.resource.UserCredentials;
import com.okta.sdk.resource.UserGroup;

import com.okta.sdk.resource.UserList;
import com.okta.sdk.resource.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * This class demonstrates the code found in the Okta Java SDK QuickStart Guide
 *
 * @since 0.5.0
 */
public class Quickstart {

    private static final Logger log = LoggerFactory.getLogger(Quickstart.class);

    public static void main(String[] args) {

        try {
            // Instantiate a builder for your Client. If needed, settings like Proxy and Caching can be defined here.
            ClientBuilder builder = Clients.builder();

            // No need to define anything else; build the Client instance. The ClientCredential information will be automatically found
            // in pre-defined locations.
            Client client = builder.build();

            UserGroup group = client.instantiate(UserGroup.class);
            group.getProfile().setName("my-user-group-" + UUID.randomUUID().toString());

            group = client.createGroup(group);

            println("Group: '" + group.getId() + "' was last updated on: " + group.getLastUpdated());

            UserList users = client.listUsers();

            // get the first user in the collection
            println("First user: " + users.iterator().next().getProfile().getEmail());

            // or loop through all of them
//        int ii = 0;
//        for (User user : users) {
//            println("["+ ii++ +"] User: " + user.getProfile().getEmail());
//        }


            // Create a User Account
            String email = "joe.coder+" + UUID.randomUUID().toString() + "@example.com";
            InputUserWithGroupIds user = client.instantiate(InputUserWithGroupIds.class); // FIXME: this needs to be a 'User' not a 'InputUserWithGroupIds'
            UserProfile userProfile = user.getProfile();
            userProfile.setFirstName("Joe");
            userProfile.setLastName("Coder");
            userProfile.setLogin(email);
            userProfile.setEmail(email);
//          userProfile.put("userProperty", "userValue");
            user.setCredentials(client.instantiate(UserCredentials.class));
            user.getCredentials().setPassword(client.instantiate(PasswordCredential.class));
            List<String> groupIds = Collections.singletonList(group.getId());
            user.setGroupIds(groupIds);

            User createdUser = client.createUser(user, false, false);

            println("User created with ID: " + createdUser.getId());

            // activate the new user
            ActivationToken activationToken = createdUser.activate(false);
            // if you don't want Okta to email the user, you can grab the activation url/token and add it to your custom flow
            String activationUrl = activationToken.getActivationUrl();

            // you can iterate through the users too
//            client.listUsers();
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
