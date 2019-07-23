/*
 * Copyright 2018-Present Okta, Inc.
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

import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.cache.Caches;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.ExtensibleResource;
import com.okta.sdk.resource.application.Application;
import com.okta.sdk.resource.application.ApplicationList;
import com.okta.sdk.resource.application.SwaApplication;
import com.okta.sdk.resource.application.SwaApplicationSettings;
import com.okta.sdk.resource.application.SwaApplicationSettingsApplication;
import com.okta.sdk.resource.group.Group;
import com.okta.sdk.resource.group.GroupBuilder;
import com.okta.sdk.resource.group.GroupList;
import com.okta.sdk.resource.log.LogEventList;
import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.UserBuilder;
import com.okta.sdk.resource.user.UserList;
import com.okta.sdk.resource.user.factor.Factor;
import com.okta.sdk.resource.user.factor.FactorList;
import com.okta.sdk.resource.user.factor.SmsFactor;
import com.okta.sdk.resource.user.factor.VerifyFactorRequest;
import com.okta.sdk.resource.user.factor.VerifyFactorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static com.okta.sdk.cache.Caches.forResource;
/**
 * Example snippets used for this projects README.md.
 * <p>
 * Manually run {@code mvn okta-code-snippet:snip} after chaging this file to update the README.md.
 */
@SuppressWarnings({"unused"})
public class ReadmeSnippets {

    private static final Logger log = LoggerFactory.getLogger(ReadmeSnippets.class);

    private final Client client = Clients.builder().build();
    private final User user = null;

    private void createClient() {
        Client client = Clients.builder()
            .setOrgUrl("{yourOktaDomain}")
            .setClientCredentials(new TokenClientCredentials("{apiToken}"))
            .build();
    }

    private void getUser() {
        User user = client.getUser("a-user-id");
    }

    private void listAllUsers() {
        UserList users = client.listUsers();

        // stream
        client.listUsers().stream()
            .forEach(user -> {
              // do something
            });
    }

    private void userSearch() {
        // search by email
        UserList users = client.listUsers("jcoder@example.com", null, null, null, null);

        // filter parameter
        users = client.listUsers(null, "status eq \"ACTIVE\"", null, null, null);
    }

    private void createUser() {
        User user = UserBuilder.instance()
            .setEmail("joe.coder@example.com")
            .setFirstName("Joe")
            .setLastName("Code")
            .buildAndCreate(client);
    }

    private void updateUser() {
        user.getProfile().setFirstName("new-first-name");
        user.update();
    }

    private void customAttributes() {
        user.getProfile().put("customPropertyKey", "a value");
        user.getProfile().get("customPropertyKey");
    }

    private void deleteUser() {
        user.deactivate();
        user.delete();
    }

    private void listUsersGroup() {
        GroupList groups = user.listGroups();
    }

    private void createGroup() {
        Group group = GroupBuilder.instance()
            .setName("a-group-name")
            .setDescription("Example Group")
            .buildAndCreate(client);
    }

    private void addUserToGroup() {
        user.addToGroup("groupId");
    }

    private void listUserFactors() {
        FactorList factors = user.listFactors();
    }

    private void enrollUserInFactor() {
        SmsFactor smsFactor = client.instantiate(SmsFactor.class);
        smsFactor.getProfile().setPhoneNumber("555 867 5309");
        user.addFactor(smsFactor);
    }

    private void activateFactor() {
        Factor factor = user.getFactor("factorId");
        VerifyFactorRequest verifyFactorRequest = client.instantiate(VerifyFactorRequest.class);
        verifyFactorRequest.setPassCode("123456");
        factor.activate(verifyFactorRequest);
    }

    private void verifyFactor() {
        Factor factor = user.getFactor("factorId");
        VerifyFactorRequest verifyFactorRequest = client.instantiate(VerifyFactorRequest.class);
        verifyFactorRequest.setPassCode("123456");
        VerifyFactorResponse response = factor.verify(verifyFactorRequest);
    }

    private void listApplication() {
        ApplicationList applications = client.listApplications();
    }

    private void getApplication() {
        Application app = client.getApplication("appId");
    }

    private void createSwaApplication() {
        SwaApplication swaApp = client.instantiate(SwaApplication.class)
            .setSettings(client.instantiate(SwaApplicationSettings.class)
            .setApp(client.instantiate(SwaApplicationSettingsApplication.class)
              .setButtonField("btn-login")
              .setPasswordField("txtbox-password")
              .setUsernameField("txtbox-username")
              .setUrl("https://example.com/login.html")));
    }

    private void listSysLogs() {
        // page through all log events
        LogEventList logEvents = client.getLogs();

        // or use a filter (start date, end date, filter, or query, sort order) all options are nullable
        logEvents = client.getLogs(null, null, null, "interestingURI.com", "ASCENDING");
    }

    private void callAnotherEndpoint() {
        // Create an IdP, see: https://developer.okta.com/docs/api/resources/idps#add-identity-provider
        ExtensibleResource resource = client.instantiate(ExtensibleResource.class);
        ExtensibleResource protocolNode = client.instantiate(ExtensibleResource.class);
        protocolNode.put("type", "OAUTH");
        resource.put("protocol", protocolNode);

        ExtensibleResource result = client.http()
            .setBody(resource)
            .post("/api/v1/idps", ExtensibleResource.class);
    }

    private void paging() {
        // get the list of users
        UserList users = client.listUsers();

        // get the first user in the collection
        log.info("First user in collection: {}", users.iterator().next().getProfile().getEmail());

        // or loop through all of them (paging is automatic)
        for (User tmpUser : users) {
            log.info("User: {}", tmpUser.getProfile().getEmail());
        }

        // or via a stream
        users.stream().forEach(tmpUser -> log.info("User: {}", tmpUser.getProfile().getEmail()));
    }

    private void complexCaching() {
         Caches.newCacheManager()
             .withDefaultTimeToLive(300, TimeUnit.SECONDS) // default
             .withDefaultTimeToIdle(300, TimeUnit.SECONDS) //general default
             .withCache(forResource(User.class) //User-specific cache settings
                 .withTimeToLive(1, TimeUnit.HOURS)
                 .withTimeToIdle(30, TimeUnit.MINUTES))
             .withCache(forResource(Group.class) //Group-specific cache settings
                 .withTimeToLive(2, TimeUnit.HOURS))
             //... etc ...
             .build(); //build the CacheManager
    }

    private void disableCaching() {
        Client client = Clients.builder()
            .setCacheManager(Caches.newDisabledCacheManager())
            .build();
    }
}