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
import com.okta.sdk.resource.User;
import com.okta.sdk.resource.UserGroup;
import com.okta.sdk.resource.UserGroupProfile;

import com.okta.sdk.resource.UserList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * This class demonstrates the code found in the Stormpath Java SDK QuickStart Guide
 *
 * @since 1.0.0
 */
public class Quickstart {

    private static final Logger log = LoggerFactory.getLogger(Quickstart.class);

    public static void main(String[] args) {

        // Instantiate a builder for your Client. If needed, settings like Proxy and Caching can be defined here.
        ClientBuilder builder = Clients.builder();

        // No need to define anything else; build the Client instance. The ApiKey information will be automatically sought
        // in pre-defined locations.
        Client client = builder.build();

        UserGroup group = client.instantiate(UserGroup.class);
        group.getProfile().setName("my-user-group-"+ UUID.randomUUID().toString());

        group = client.createGroup(group);

        System.out.println("Group: '"+ group.getId()+ "' was last updated on: "+ group.getLastUpdated());

//        UserList users = client.getUsers();
//
//        int ii = 0;
//        for (User user : users) {
//            System.out.println("["+ ii++ +"] User: " + user.getProfile().getEmail());
//        }


//
//        // Retrieve your application
//        ApplicationList applications = tenant.getApplications(
//                Applications.where(Applications.name().eqIgnoreCase(APPLICATION_NAME))
//        );
//
//        Application application = applications.iterator().next();
//        log.info("Application: " + application.getHref() + ", " + application.getName());
//
//        // Create a User Account
//
//        //Create the account object
//        Account account = client.instantiate(Account.class);
//
//        //Set the account properties
//        account.setGivenName("Joe")
//                .setSurname("Quickstart_Stormtrooper")
//                .setUsername("tk421")  // optional, defaults to email if unset
//                .setEmail("tk421@stormpath.com")
//                .setPassword("Changeme1");
//        CustomData customData = account.getCustomData();
//        customData.put("favoriteColor", "white");
//
//        // Create the account using the existing Application object
//        account = application.createAccount(account);
//
//        // Print account details
//
//        log.info("Given Name: " + account.getGivenName());
//        log.info("Favorite Color: " + account.getCustomData().get("favoriteColor"));
//
//        // Search for a User Account
//
//        Map<String, Object> queryParams = new HashMap<String, Object>();
//        queryParams.put("email", "tk421@stormpath.com");
//        AccountList accounts = application.getAccounts(queryParams);
//        account = accounts.iterator().next();
//
//        log.info("Found Account: " + account.getHref() + ", " + account.getEmail());
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
