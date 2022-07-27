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
import com.okta.sdk.client.AuthorizationMode;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.common.PagedList;
import com.okta.sdk.resource.group.GroupBuilder;
import com.okta.sdk.resource.user.UserBuilder;

import org.openapitools.client.ApiClient;
import org.openapitools.client.model.Application;
import org.openapitools.client.model.ApplicationSignOnMode;
import org.openapitools.client.model.BookmarkApplication;
import org.openapitools.client.model.BookmarkApplicationSettings;
import org.openapitools.client.model.BookmarkApplicationSettingsApplication;
import org.openapitools.client.model.BrowserPluginApplication;
import org.openapitools.client.model.SwaApplicationSettings;
import org.openapitools.client.model.SwaApplicationSettingsApplication;
import org.openapitools.client.model.Group;
import org.openapitools.client.model.LogEvent;
import org.openapitools.client.model.UserProfile;
import org.openapitools.client.model.UpdateUserRequest;
import org.openapitools.client.model.ActivateFactorRequest;
import org.openapitools.client.model.User;
import org.openapitools.client.model.UserFactor;
import org.openapitools.client.model.SmsUserFactor;
import org.openapitools.client.model.VerifyFactorRequest;
import org.openapitools.client.model.VerifyUserFactorResponse;
import org.openapitools.jackson.nullable.JsonNullableModule;

import org.openapitools.client.api.*;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            .setKid("{kid}") // key id (optional)
            .setScopes(new HashSet<>(Arrays.asList("okta.users.read", "okta.apps.read")))
            .setPrivateKey("/path/to/yourPrivateKey.pem")
            // (or) .setPrivateKey("full PEM payload")
            // (or) .setPrivateKey(Paths.get("/path/to/yourPrivateKey.pem"))
            // (or) .setPrivateKey(inputStream)
            // (or) .setPrivateKey(privateKey)
            .build();
    }

    private void getUser() {
        UserApi userApi = new UserApi(client);

        User user = userApi.getUser("userId");
    }

    private void listAllUsers() {
        UserApi userApi = new UserApi(client);
        List<User> users = userApi.listUsers(null, null, 5, null, null, null, null);

        // stream
        users.stream()
            .forEach(user -> {
              // do something
            });
    }

    private void userSearch() {
        UserApi userApi = new UserApi(client);
        // search by email
        List<User> users = userApi.listUsers(null, null, 5, null, "jcoder@example.com", null, null);

        // filter parameter
        users = userApi.listUsers(null, null, null, "status eq \"ACTIVE\"",null, null, null);
    }

    private void createUser() {
        UserApi userApi = new UserApi(client);

        User user = UserBuilder.instance()
            .setEmail("joe.coder@example.com")
            .setFirstName("Joe")
            .setLastName("Code")
            .buildAndCreate(userApi);
    }

    private void createUserWithGroups() {
        UserApi userApi = new UserApi(client);

        User user = UserBuilder.instance()
            .setEmail("joe.coder@example.com")
            .setFirstName("Joe")
            .setLastName("Code")
            .setGroups(Arrays.asList("groupId-1", "groupId-2"))
            .buildAndCreate(userApi);
    }

    private void updateUser() {
        UserApi userApi = new UserApi(client);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        UserProfile userProfile = new UserProfile();
        userProfile.setNickName("Batman");
        updateUserRequest.setProfile(userProfile);

        userApi.partialUpdateUser(user.getId(), updateUserRequest, true);
    }

    private void customAttributes() {
        UserProfile userProfile = new UserProfile();
        userProfile.getProperties().put("customPropertyKey", "a value");
        userProfile.getProperties().get("customPropertyKey");
    }

    private void deleteUser() {
        UserApi userApi = new UserApi(client);

        // deactivate first
        userApi.deactivateOrDeleteUser(user.getId(), false);
        // then delete
        userApi.deactivateOrDeleteUser(user.getId(), false);
    }

    private void listUsersGroup() {
        GroupApi groupApi = new GroupApi(client);
        List<Group> groups = groupApi.listGroups(null, null, null, 10, null);
    }

    private void createGroup() {
        GroupApi groupApi = new GroupApi(client);

        Group group = GroupBuilder.instance()
            .setName("a-group-name")
            .setDescription("Example Group")
            .buildAndCreate(groupApi);
    }

    private void listUserFactors() {
        UserFactorApi userFactorApi = new UserFactorApi(client);

        List<UserFactor> userFactors = userFactorApi.listFactors("userId");
    }

    private void enrollUserInFactor() {
        UserFactorApi userFactorApi = new UserFactorApi(client);

        SmsUserFactor smsFactor = new SmsUserFactor();
        smsFactor.getProfile().setPhoneNumber("555 867 5309");

        UserFactor userFactor = userFactorApi.enrollFactor("userId", smsFactor, true, "templateId", 30, true);
    }

    private void activateFactor() {
        UserFactorApi userFactorApi = new UserFactorApi(client);

        UserFactor userFactor = userFactorApi.getFactor("userId", "factorId");
        ActivateFactorRequest activateFactorRequest = new ActivateFactorRequest();
        activateFactorRequest.setPassCode("123456");

        UserFactor activatedUserFactor = userFactorApi.activateFactor("userId", "factorId", activateFactorRequest);
    }

    private void verifyFactor() {
        UserFactorApi userFactorApi = new UserFactorApi(client);

        UserFactor userFactor = userFactorApi.getFactor("userId", "factorId");
        VerifyFactorRequest verifyFactorRequest = new VerifyFactorRequest();
        verifyFactorRequest.setPassCode("123456");

        VerifyUserFactorResponse verifyUserFactorResponse =
            userFactorApi.verifyFactor("userId", "factorId", "templateId", 10, "xForwardedFor", "userAgent", "acceptLanguage", verifyFactorRequest);
    }

    private void listApplication() {
        ApplicationApi applicationApi = new ApplicationApi(client);

        List<Application> applications = applicationApi.listApplications(null, null, 10, null, null, true);
    }

    private void getApplication() {
        ApplicationApi applicationApi = new ApplicationApi(client);

        Application app = applicationApi.getApplication("appId", null);
    }

    private void createSwaApplication() {
        ApplicationApi applicationApi = new ApplicationApi(client);

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

        // create
        BrowserPluginApplication createdApp =
            applicationApi.createApplication(BrowserPluginApplication.class, browserPluginApplication, true, null);
    }

    private void listSysLogs() {
        SystemLogApi systemLogApi = new SystemLogApi(client);

        // use a filter (start date, end date, filter, or query, sort order) all options are nullable
        List<LogEvent> logEvents = systemLogApi.getLogs(null, null, null, "interestingURI.com", 100, "ASCENDING", null);
    }

    private void callAnotherEndpoint() {

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

        ResponseEntity<BookmarkApplication> responseEntity = apiClient.invokeAPI("/api/v1/apps",
            HttpMethod.POST,
            Collections.emptyMap(),
            null,
            bookmarkApplication,
            new HttpHeaders(),
            new LinkedMultiValueMap<>(),
            null,
            Collections.singletonList(MediaType.APPLICATION_JSON),
            MediaType.APPLICATION_JSON,
            new String[]{"API Token"},
            new ParameterizedTypeReference<BookmarkApplication>() {});

        BookmarkApplication createdApp = responseEntity.getBody();
    }

    private void paging() {

        UserApi userApi = new UserApi(client);

        // limit
        int pageSize = 2;

        PagedList<User> usersPagedListOne = userApi.listUsersWithPaginationInfo(null, null, pageSize, null, null, null, null);

        // e.g. https://example.okta.com/api/v1/users?after=000u3pfv9v4SQXvpBB0g7&limit=2
        String nextPageUrl = usersPagedListOne.getNextPage();

        // replace 'after' with actual cursor from the nextPageUrl
        PagedList<User> usersPagedListTwo = userApi.listUsersWithPaginationInfo("after", null, pageSize, null, null, null, null);

        // loop through all of them (paging is automatic)
        for (User tmpUser : usersPagedListOne.getItems()) {
            log.info("User: {}", tmpUser.getProfile().getEmail());
        }

        // or stream
        usersPagedListOne.getItems().stream().forEach(tmpUser -> log.info("User: {}", tmpUser.getProfile().getEmail()));
    }

    private static ApiClient buildApiClient(String orgBaseUrl, String apiKey) {

        ApiClient apiClient = new ApiClient(buildRestTemplate());
        apiClient.setBasePath(orgBaseUrl);
        apiClient.setApiKey(apiKey);
        apiClient.setApiKeyPrefix("SSWS");
        return apiClient;
    }

    private static RestTemplate buildRestTemplate() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        ObjectMapper mapper = messageConverter.getObjectMapper();
        messageConverter.setSupportedMediaTypes(Arrays.asList(
            MediaType.APPLICATION_JSON,
            MediaType.parseMediaType("application/x-pem-file"),
            MediaType.parseMediaType("application/x-x509-ca-cert"),
            MediaType.parseMediaType("application/pkix-cert")));
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new JsonNullableModule());

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(messageConverter);

        RestTemplate restTemplate = new RestTemplate(messageConverters);

        // This allows us to read the response more than once - Necessary for debugging.
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));
        return restTemplate;
    }
}
