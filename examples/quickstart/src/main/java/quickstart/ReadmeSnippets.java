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

import com.fasterxml.jackson.core.type.TypeReference;

import com.okta.commons.http.MediaType;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.cache.Caches;
import com.okta.sdk.client.AuthenticationScheme;
import com.okta.sdk.client.AuthorizationMode;
import com.okta.sdk.client.Clients;
import com.okta.sdk.helper.ApplicationApiHelper;
import com.okta.sdk.helper.UserFactorApiHelper;
import com.okta.sdk.helper.PolicyApiHelper;
import com.okta.sdk.resource.group.GroupBuilder;
import com.okta.sdk.resource.user.UserBuilder;

import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Pair;
import org.openapitools.client.api.*;
import org.openapitools.client.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.okta.sdk.cache.Caches.forResource;

/**
 * Example snippets used for this projects README.md.
 * <p>
 * Manually run {@code mvn okta-code-snippet:snip} after changing this file to update the README.md.
 */
@SuppressWarnings({"unused"})
public class ReadmeSnippets {

    private static final Logger log = LoggerFactory.getLogger(ReadmeSnippets.class);

    private final ApiClient client = Clients.builder().build();
    private final User user = null;

    private void createClient() {
        ApiClient client = Clients.builder()
            .setOrgUrl("https://{yourOktaDomain}")  // e.g. https://dev-123456.okta.com
            .setClientCredentials(new TokenClientCredentials("{apiToken}"))
            .build();
    }

    private void createOAuth2Client() {
        ApiClient client = Clients.builder()
            .setOrgUrl("https://{yourOktaDomain}")  // e.g. https://dev-123456.okta.com
            .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
            .setClientId("{clientId}")
            .setKid("{kid}") // optional
            .setScopes(new HashSet<>(Arrays.asList("okta.users.manage", "okta.apps.manage", "okta.groups.manage")))
            .setPrivateKey("/path/to/yourPrivateKey.pem")
            // (or) .setPrivateKey("full PEM payload")
            // (or) .setPrivateKey(Paths.get("/path/to/yourPrivateKey.pem"))
            // (or) .setPrivateKey(inputStream)
            // (or) .setPrivateKey(privateKey)
            // (or) .setOAuth2AccessToken("access token string") // if set, private key (if supplied) will be ignored
            .build();
    }

    private void getUser() throws ApiException {
        UserApi userApi = new UserApi(client);

        User user = userApi.getUser("userId");
    }

    private void listAllUsers() throws ApiException {
        UserApi userApi = new UserApi(client);
        List<User> users = userApi.listUsers(null, null, 5, null, null, null, null);

        // stream
        users.stream()
            .forEach(user -> {
              // do something
            });
    }

    private void userSearch() throws ApiException {
        UserApi userApi = new UserApi(client);
        // search by email
        List<User> users = userApi.listUsers(null, null, 5, null, "profile.email eq \"jcoder@example.com\"", null, null);

        // filter parameter
        userApi.listUsers(null, null, null, "status eq \"ACTIVE\"",null, null, null);
    }

    private void createUser() throws ApiException {
        UserApi userApi = new UserApi(client);

        User user = UserBuilder.instance()
            .setEmail("joe.coder@example.com")
            .setFirstName("Joe")
            .setLastName("Code")
            .buildAndCreate(userApi);
    }

    private void createUserWithGroups() throws ApiException {
        UserApi userApi = new UserApi(client);

        User user = UserBuilder.instance()
            .setEmail("joe.coder@example.com")
            .setFirstName("Joe")
            .setLastName("Code")
            .setGroups(Arrays.asList("groupId-1", "groupId-2"))
            .buildAndCreate(userApi);
    }

    private void updateUser() throws ApiException {
        UserApi userApi = new UserApi(client);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        UserProfile userProfile = new UserProfile();
        userProfile.setNickName("Batman");
        updateUserRequest.setProfile(userProfile);

        userApi.updateUser(user.getId(), updateUserRequest, true);
    }

    private void deleteUser() throws ApiException {
        UserApi userApi = new UserApi(client);

        // deactivate first
        userApi.deactivateUser(user.getId(), false);
        // then delete
        userApi.deleteUser(user.getId(), false);
    }

    private void listUsersGroup() throws ApiException {
        GroupApi groupApi = new GroupApi(client);

        List<Group> groups = groupApi.listGroups(null, null, null, 10, null, null, null, null);
    }

    private void createGroup() throws ApiException {
        GroupApi groupApi = new GroupApi(client);

        Group group = GroupBuilder.instance()
            .setName("a-group-name")
            .setDescription("Example Group")
            .buildAndCreate(groupApi);
    }

    private void assignUserToGroup() throws ApiException {
        // create user
        UserApi userApi = new UserApi(client);

        User user = UserBuilder.instance()
            .setEmail("joe.coder@example.com")
            .setFirstName("Joe")
            .setLastName("Code")
            .buildAndCreate(userApi);

        // create group
        GroupApi groupApi = new GroupApi(client);

        Group group = GroupBuilder.instance()
            .setName("a-group-name")
            .setDescription("Example Group")
            .buildAndCreate(groupApi);

        // assign user to group
        groupApi.assignUserToGroup(group.getId(), user.getId());
    }

    private void listUserFactors() throws ApiException {
        UserFactorApiHelper<UserFactor> userFactorApiHelper = new UserFactorApiHelper<>(new UserFactorApi(client));

        List<UserFactor> userFactors = userFactorApiHelper.listFactors("userId");
    }

    private void enrollUserInFactor() throws ApiException {
        UserFactorApiHelper<UserFactor> userFactorApiHelper = new UserFactorApiHelper<>(new UserFactorApi(client));

        SmsUserFactorProfile smsUserFactorProfile = new SmsUserFactorProfile();
        smsUserFactorProfile.setPhoneNumber("555 867 5309");

        SmsUserFactor smsUserFactor = new SmsUserFactor();
        smsUserFactor.setProvider(FactorProvider.OKTA);
        smsUserFactor.setFactorType(FactorType.SMS);
        smsUserFactor.setProfile(smsUserFactorProfile);

        userFactorApiHelper.enrollFactorOfType(SmsUserFactor.class, "userId", smsUserFactor, true, "templateId", 30, true);
    }

    private void activateFactor() throws ApiException {
        UserFactorApiHelper<UserFactor> userFactorApiHelper = new UserFactorApiHelper<>(new UserFactorApi(client));

        CallUserFactor userFactor = (CallUserFactor) userFactorApiHelper.getFactor("userId", "factorId");
        ActivateFactorRequest activateFactorRequest = new ActivateFactorRequest();
        activateFactorRequest.setPassCode("123456");

        userFactorApiHelper.activateFactorOfType(CallUserFactor.class, "userId", "factorId", activateFactorRequest);
    }

    private void verifyFactor() throws ApiException {
        UserFactorApiHelper<UserFactor> userFactorApiHelper = new UserFactorApiHelper<>(new UserFactorApi(client));

        UserFactor userFactor = userFactorApiHelper.getFactor( "userId", "factorId");
        VerifyFactorRequest verifyFactorRequest = new VerifyFactorRequest();
        verifyFactorRequest.setPassCode("123456");

        VerifyUserFactorResponse verifyUserFactorResponse =
            userFactorApiHelper.verifyFactor("userId", "factorId", "templateId", 10, "xForwardedFor", "userAgent", "acceptLanguage", verifyFactorRequest);
    }

    private void listApplications() throws ApiException {
        ApplicationApiHelper<Application> applicationApiHelper = new ApplicationApiHelper<>(new ApplicationApi(client));

        List<Application> applications = applicationApiHelper.listApplications(null, null, null, null, null, true);
    }

    private void getApplication() throws ApiException {
        ApplicationApiHelper<Application> applicationApiHelper = new ApplicationApiHelper<>(new ApplicationApi(client));

        // get bookmarkApplication application type
        BookmarkApplication bookmarkApp = (BookmarkApplication) applicationApiHelper.getApplication("bookmark-app-id", null);
    }

    private void createSwaApplication() throws ApiException {
        ApplicationApiHelper<Application> applicationApiHelper = new ApplicationApiHelper<>(new ApplicationApi(client));

        SwaApplicationSettingsApplication swaApplicationSettingsApplication = new SwaApplicationSettingsApplication();
        swaApplicationSettingsApplication.buttonField("btn-login")
            .passwordField("txtbox-password")
            .usernameField("txtbox-username")
            .url("https://example.com/login.html");
        SwaApplicationSettings swaApplicationSettings = new SwaApplicationSettings();
        swaApplicationSettings.app(swaApplicationSettingsApplication);
        BrowserPluginApplication browserPluginApplication = new BrowserPluginApplication();
        browserPluginApplication.name("template_swa");
        browserPluginApplication.label("Sample Plugin App");
        browserPluginApplication.settings(swaApplicationSettings);

        // create BrowserPluginApplication app type
        BrowserPluginApplication createdApp =
            applicationApiHelper.createApplicationOfType(BrowserPluginApplication.class, browserPluginApplication, true, null);
    }

    private void listPolicies() throws ApiException {
        PolicyApiHelper<Policy> policyPolicyApiHelper = new PolicyApiHelper<>(new PolicyApi(client));

        List<Policy> policies = policyPolicyApiHelper.listPolicies(PolicyType.PASSWORD.name(), LifecycleStatus.ACTIVE.name(), null);
    }

    private void getPolicy() throws ApiException {
        PolicyApiHelper<Policy> policyPolicyApiHelper = new PolicyApiHelper<>(new PolicyApi(client));

        // get MultifactorEnrollmentPolicy policy type
        MultifactorEnrollmentPolicy mfaPolicy =
            (MultifactorEnrollmentPolicy) policyPolicyApiHelper.getPolicy("mfa-policy-id", null);
    }

    private void listSysLogs() throws ApiException {
        SystemLogApi systemLogApi = new SystemLogApi(client);

        // use a filter (start date, end date, filter, or query, sort order) all options are nullable
        List<LogEvent> logEvents =
            systemLogApi.listLogEvents(null, null, null, "interestingURI.com", 100, "ASCENDING", null);
    }

    private void callAnotherEndpoint() throws ApiException {

        ApiClient apiClient = buildApiClient("orgBaseUrl", "apiKey");

        // Create a BookmarkApplication
        BookmarkApplication bookmarkApplication = new BookmarkApplication();
        bookmarkApplication.setName("bookmark");
        bookmarkApplication.setLabel("Sample Bookmark App");
        bookmarkApplication.setSignOnMode(ApplicationSignOnMode.BOOKMARK);
        BookmarkApplicationSettings bookmarkApplicationSettings = new BookmarkApplicationSettings();
        BookmarkApplicationSettingsApplication bookmarkApplicationSettingsApplication =
            new BookmarkApplicationSettingsApplication();
        bookmarkApplicationSettingsApplication.setUrl("https://example.com/bookmark.htm");
        bookmarkApplicationSettingsApplication.setRequestIntegration(false);
        bookmarkApplicationSettings.setApp(bookmarkApplicationSettingsApplication);
        bookmarkApplication.setSettings(bookmarkApplicationSettings);

        StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        List<Pair> localVarQueryParams = new ArrayList<>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<>();
        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, String> localVarCookieParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();

        BookmarkApplication createdApp = apiClient.invokeAPI(
            "/api/v1/apps",   // path
            HttpMethod.POST.name(),   // http method
            localVarQueryParams,   // query params
            localVarCollectionQueryParams, // collection query params
            localVarQueryStringJoiner.toString(),
            bookmarkApplication,   // request body
            localVarHeaderParams,   // header params
            localVarCookieParams,   // cookie params
            localVarFormParams,   // form params
            MediaType.APPLICATION_JSON_VALUE,   // accept
            MediaType.APPLICATION_JSON_VALUE,   // content type
            new String[]{ "apiToken", "oauth2" },   // auth names
            new TypeReference<BookmarkApplication>() { }  // return type
        );
    }

    private void complexCaching() {
        Caches.newCacheManager()
            .withDefaultTimeToLive(300, TimeUnit.SECONDS) // default
            .withDefaultTimeToIdle(300, TimeUnit.SECONDS) //general default
            .withCache(forResource(User.class) //User-specific cache settings
                .withTimeToLive(1, TimeUnit.HOURS)
                .withTimeToIdle(30, TimeUnit.MINUTES))
            .withCache(forResource(Group.class) //Group-specific cache settings
                .withTimeToLive(1, TimeUnit.HOURS))
            //... etc ...
            .build(); //build the CacheManager
    }

    private void disableCaching() {
        ApiClient client = Clients.builder()
            .setCacheManager(Caches.newDisabledCacheManager())
            .build();
    }

    private static ApiClient buildApiClient(String orgBaseUrl, String apiKey) {

        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(orgBaseUrl);
        apiClient.setApiKey(apiKey);
        apiClient.setApiKeyPrefix(AuthenticationScheme.SSWS.name());
        return apiClient;
    }
}
