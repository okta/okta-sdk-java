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

import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.ClientBuilder;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.PasswordCredential;
import com.okta.sdk.resource.User;
import com.okta.sdk.resource.UserCredentials;
import com.okta.sdk.resource.UserGroup;
import com.okta.sdk.resource.UserGroupProfile;

import com.okta.sdk.resource.UserList;
import com.okta.sdk.resource.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
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

        // Instantiate a builder for your Client. If needed, settings like Proxy and Caching can be defined here.
        ClientBuilder builder = Clients.builder();

        // No need to define anything else; build the Client instance. The ClientCredential information will be automatically found
        // in pre-defined locations.
        Client client = builder.build();

        UserGroup group = client.instantiate(UserGroup.class);
        group.getProfile().setName("my-user-group-"+ UUID.randomUUID().toString());

        group = client.createGroup(group);

        System.out.println("Group: '"+ group.getId()+ "' was last updated on: "+ group.getLastUpdated());

        UserList users = client.listUsers();

        // get the first user in the collection
        System.out.println("First user: "+ users.iterator().next().getProfile().getEmail());

        // or loop through all of them
//        int ii = 0;
//        for (User user : users) {
//            System.out.println("["+ ii++ +"] User: " + user.getProfile().getEmail());
//        }


//        // Create a User Account
//        User user = client.instantiate(User.class);
//        UserProfile userProfile = user.getProfile();
//        userProfile.setFirstName("Joe");
//        userProfile.setLastName("Code");
//        userProfile.setEmail("joe.coder+" + UUID.randomUUID().toString() + "example.com");
////        userProfile.put("userProperty", "userValue");
//        user.setCredentials(client.instantiate(UserCredentials.class));
//        user.getCredentials().setPassword(client.instantiate(PasswordCredential.class));
//        client.createUser(user);

//        client.listUsers();

//        // Search for a User Account
//
//        Map<String, Object> queryParams = new HashMap<String, Object>();
//        queryParams.put("email", "tk421@stormpath.com");
//        AccountList accounts = application.getAccounts(queryParams);
//        account = accounts.iterator().next();
//
//        log.info("Found Account: " + account.getResourceHref() + ", " + account.getEmail());
//
//        // Authenticate a User Account
//
//        String usernameOrEmail = "tk421@stormpath.com";
//        String rawPassword = "Changeme1";
//
//        // Create an authentication request using the credentials
//        AuthenticationRequest request = UsernamePasswordRequests.builder()
//                .setUsernameOrEmail(usernameOrEmail)
//                .setPassword(rawPassword)
//                .build();
//
//        //Now let's authenticate the account with the application:
//        try {
//            AuthenticationResult result = application.authenticateAccount(request);
//            account = result.getAccount();
//            log.info("Authenticated Account: " + account.getUsername() + ", Email: " + account.getEmail());
//        } catch (ResourceException ex) {
//            log.error(ex.getMessage());
//        }
    }
}
