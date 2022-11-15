[<img src="https://www.okta.com/sites/default/files/Dev_Logo-01_Large-thumbnail.png" align="right" width="256px"/>](https://devforum.okta.com/)
[![Maven Central](https://img.shields.io/maven-central/v/com.okta.sdk/okta-sdk-api.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.okta.sdk%22%20a%3A%22okta-sdk-api%22)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Support](https://img.shields.io/badge/support-Developer%20Forum-blue.svg)][devforum]
[![API Reference](https://img.shields.io/badge/docs-reference-lightgrey.svg)][javadocs]

# Okta Java Management SDK

* [Release status](#release-status)
* [Need help?](#need-help)
* [Getting started](#getting-started)
* [Usage guide](#usage-guide)
* [Spring Support](#spring-support)
* [Configuration reference](#configuration-reference)
* [Building the SDK](#building-the-sdk)
* [Contributing](#contributing)

<a href="https://foojay.io/today/works-with-openjdk">
   <img align="right" 
        src="https://github.com/foojayio/badges/raw/main/works_with_openjdk/Works-with-OpenJDK.png"   
        width="100">
</a>

This repository contains the Okta management SDK for Java. This SDK can be used in your server-side code to interact with the Okta management API and:
 
* Create and update users with the [Users API](https://developer.okta.com/docs/api/resources/users)
* Add security factors to users with the [Factors API](https://developer.okta.com/docs/api/resources/factors)
* Manage groups with the [Groups API](https://developer.okta.com/docs/api/resources/groups)
* Manage applications with the [Apps API](https://developer.okta.com/docs/api/resources/apps)
* Manage logs with the [Logs API](https://developer.okta.com/docs/api/resources/system_log)
* Manage sessions with the [Sessions API](https://developer.okta.com/docs/api/resources/sessions)
* Manage templates with the [Custom Templates API](https://developer.okta.com/docs/reference/api/templates/)
* Manage identity providers with the [Identity Providers API](https://developer.okta.com/docs/reference/api/idps/)
* Manage authorization servers with the [Authorization Servers API](https://developer.okta.com/docs/reference/api/authorization-servers/)
* Manage event hooks with the [Event Hooks Management API](https://developer.okta.com/docs/reference/api/event-hooks/)
* Manage inline hooks with the [Inline Hooks Management API](https://developer.okta.com/docs/reference/api/inline-hooks/).
* Manage features with the [Features API](https://developer.okta.com/docs/reference/api/features/).
* Manage linked objects with the [Linked Objects API](https://developer.okta.com/docs/reference/api/linked-objects/).
* Manage trusted origins with the [Trusted Origins API](https://developer.okta.com/docs/reference/api/trusted-origins/).
* Manage user types with the [User Types API](https://developer.okta.com/docs/reference/api/user-types/).
* Manage custom domains with the [Domains API](https://developer.okta.com/docs/reference/api/domains/).
* Manage network zones with the [Zones API](https://developer.okta.com/docs/reference/api/zones/).
* Much more!
 
We also publish these libraries for Java:
 
* [Spring Boot Integration](https://github.com/okta/okta-spring-boot/)
* [Okta JWT Verifier for Java](https://github.com/okta/okta-jwt-verifier-java)
* [Authentication SDK](https://github.com/okta/okta-auth-java)
 
You can learn more on the [Okta + Java][lang-landing] page in our documentation.
 
## Release status

This library uses semantic versioning and follows Okta's [library version policy](https://developer.okta.com/code/library-versions/).

:heavy_check_mark: The latest stable major version series is: 10.x.x

| Version                  | Status                                                                                                                                                                                                              |
| ------- | ------------------------- |
| 0.0.x, 1.x, 2.x.x, 3.x.x | :warning: Retired                                                                                                                                                                                                   |
| 4.x.x                    | :heavy_check_mark: Stable ([migration guide](https://github.com/okta/okta-sdk-java/blob/master/MIGRATING.md#migrating-from-3xx-to-400))                                                                             |
| 5.x.x                    | :heavy_check_mark: Stable ([migration guide](https://github.com/okta/okta-sdk-java/blob/master/MIGRATING.md#migrating-from-4xx-to-500))                                                                             |
| 6.x.x                    | :heavy_check_mark: Stable ([migration guide](https://github.com/okta/okta-sdk-java/blob/master/MIGRATING.md#migrating-from-5xx-to-600))                                                                             |
| 7.x.x                    | :heavy_check_mark: Stable ([migration guide](https://github.com/okta/okta-sdk-java/blob/master/MIGRATING.md#migrating-from-6xx-to-700))                                                                             |
| 8.x.x                    | :heavy_check_mark: Stable ([migration guide](https://github.com/okta/okta-sdk-java/blob/master/MIGRATING.md#migrating-from-7xx-to-800))                                                                             |
| 9.x.x-beta | :warning: Beta release located in [branch](https://github.com/okta/okta-sdk-java/tree/swagger_v3) - Discontinued                                                                                    |
| 10.x.x-beta | :warning: Beta release located in `oasv3` [branch](https://github.com/okta/okta-sdk-java/tree/oasv3) ([migration guide](https://github.com/okta/okta-sdk-java/blob/master/MIGRATING.md#migrating-from-8xx-to-10xx)) |
| 10.0.0 | :heavy_check_mark: Stable ([migration guide](https://github.com/okta/okta-sdk-java/blob/master/MIGRATING.md#migrating-from-8xx-to-10xx))                                                                            |

The latest release can always be found on the [releases page][github-releases].
 
## Need help?
 
If you run into problems using the SDK, you can
 
* Ask questions on the [Okta Developer Forums][devforum]
* Post [issues][github-issues] here on GitHub (for code errors)
 
## Getting started

### Prerequisites

* JDK 8 or later

To use this SDK, you will need to include the following dependencies:

For Apache Maven:

``` xml
<dependency>
    <groupId>com.okta.sdk</groupId>
    <artifactId>okta-sdk-api</artifactId>
    <version>${okta.sdk.version}</version>
</dependency>
<dependency>
    <groupId>com.okta.sdk</groupId>
    <artifactId>okta-sdk-impl</artifactId>
    <version>${okta.sdk.version}</version>
    <scope>runtime</scope>
</dependency>
```

For Gradle:

```groovy
compile "com.okta.sdk:okta-sdk-api:${okta.sdk.version}"
runtime "com.okta.sdk:okta-sdk-impl:${okta.sdk.version}"
```

where ${okta.sdk.version} is the latest published version in [Maven Central](https://search.maven.org/search?q=g:com.okta.sdk%20a:okta-sdk-api).

### SNAPSHOT Dependencies

Snapshots are deployed off of the 'master' branch to [OSSRH](https://oss.sonatype.org/) and can be consumed using the following repository configured for Apache Maven or Gradle:

```txt
https://oss.sonatype.org/content/repositories/snapshots/
```

You will also need:

* An Okta account, called an _organization_ (sign up for a free [developer organization](https://developer.okta.com/signup) if you need one)
* An [API token](https://developer.okta.com/docs/api/getting_started/getting_a_token)
 
Construct a client instance by passing it your Okta domain name and API token:
 
[//]: # (NOTE: code snippets in this README are updated automatically via a Maven plugin by running: mvn okta-code-snippet:snip)
 
[//]: # (method: createClient)
```java
ApiClient client = Clients.builder()
    .setOrgUrl("https://{yourOktaDomain}")  // e.g. https://dev-123456.okta.com
    .setClientCredentials(new TokenClientCredentials("{apiToken}"))
    .build();
```
[//]: # (end: createClient)
 
Hard-coding the Okta domain and API token works for quick tests, but for real projects you should use a more secure way of storing these values (such as environment variables). This library supports a few different configuration sources, covered in the [configuration reference](#configuration-reference) section.
 
## Usage guide

These examples will help you understand how to use this library. You can also browse the full [API reference documentation][javadocs].

Once you initialize a `ApiClient` instance, you can pass this instance to the constructor of any API area clients (such as `UserApi`, `GroupApi`, `ApplicationApi` etc.).
You can start using these clients to call management APIs relevant to the chosen API area.

### Authenticate a User

This library should be used with the Okta management API. For authentication, we recommend using an OAuth 2.0 or OpenID Connect library such as [Spring Security OAuth](https://spring.io/projects/spring-security-oauth) or [Okta's Spring Boot integration](https://github.com/okta/okta-spring-boot). For [Okta Authentication API](https://developer.okta.com/docs/api/resources/authn) you can use [Authentication SDK](https://github.com/okta/okta-auth-java).

### Get a User

[//]: # (method: getUser)
```java
UserApi userApi = new UserApi(client);
User user = userApi.getUser("userId");
```
[//]: # (end: getUser)

### List all Users

[//]: # (method: listAllUsers)
```java
UserApi userApi = new UserApi(client);
List<User> users = userApi.listUsers(null, null, 5, null, null, null, null);

// stream
users.stream()
    .forEach(user -> {
      // do something
    });
```
[//]: # (end: listAllUsers)

For more examples of handling collections see the [paging](#paging) section below.

### Filter or search for Users

[//]: # (method: userSearch)
```java
UserApi userApi = new UserApi(client);

// search by email
List<User> users = userApi.listUsers(null, null, 5, null, "jcoder@example.com", null, null);

// filter parameter
users = userApi.listUsers(null, null, null, "status eq \"ACTIVE\"",null, null, null);
```
[//]: # (end: userSearch)

### Create a User

[//]: # (method: createUser)
```java
UserApi userApi = new UserApi(client);
User user = UserBuilder.instance()
    .setEmail("joe.coder@example.com")
    .setFirstName("Joe")
    .setLastName("Code")
    .buildAndCreate(userApi);
```
[//]: # (end: createUser)

### Create a User with Group(s)

[//]: # (method: createUserWithGroups)
```java
UserApi userApi = new UserApi(client);
User user = UserBuilder.instance()
    .setEmail("joe.coder@example.com")
    .setFirstName("Joe")
    .setLastName("Code")
    .setGroups(Arrays.asList("groupId-1", "groupId-2"))
    .buildAndCreate(userApi);
```
[//]: # (end: createUserWithGroups)

### Update a User

[//]: # (method: updateUser)
```java
UserApi userApi = new UserApi(client);
UpdateUserRequest updateUserRequest = new UpdateUserRequest();
UserProfile userProfile = new UserProfile();
userProfile.setNickName("Batman");
updateUserRequest.setProfile(userProfile);
userApi.updateUser(user.getId(), updateUserRequest, true);
```
[//]: # (end: updateUser)
 
### Remove a User

[//]: # (method: deleteUser)
```java
UserApi userApi = new UserApi(client);

// deactivate first
userApi.deactivateUser(user.getId(), false);

// then delete
userApi.deleteUser(user.getId(), false);
```
[//]: # (end: deleteUser)

### List a User's Groups

[//]: # (method: listUsersGroup)
```java
GroupApi groupApi = new GroupApi(client);
List<Group> groups = groupApi.listGroups(null, null, null, 10, null, null);
```
[//]: # (end: listUsersGroup)

### Create a Group

[//]: # (method: createGroup)
```java
GroupApi groupApi = new GroupApi(client);
Group group = GroupBuilder.instance()
    .setName("a-group-name")
    .setDescription("Example Group")
    .buildAndCreate(groupApi);
```
[//]: # (end: createGroup)

### Add a User to a Group

[//]: # (method: addUserToGroup)
```java
user.addToGroup("groupId");
```
[//]: # (end: addUserToGroup)

### List a User's enrolled Factors

[//]: # (method: listUserFactors)
```java
UserFactorApi userFactorApi = new UserFactorApi(client);
List<UserFactor> userFactors = userFactorApi.listFactors("userId");
```
[//]: # (end: listUserFactors)

### Enroll a User in a new Factor

[//]: # (method: enrollUserInFactor)
```java
UserFactorApi userFactorApi = new UserFactorApi(client);
SmsUserFactor smsFactor = new SmsUserFactor();
smsFactor.getProfile().setPhoneNumber("555 867 5309");
UserFactor userFactor = userFactorApi.enrollFactor("userId", smsFactor, true, "templateId", 30, true);
```
[//]: # (end: enrollUserInFactor)

### Activate a Factor

[//]: # (method: activateFactor)
```java
UserFactorApi userFactorApi = new UserFactorApi(client);
UserFactor userFactor = userFactorApi.getFactor("userId", "factorId");
ActivateFactorRequest activateFactorRequest = new ActivateFactorRequest();
activateFactorRequest.setPassCode("123456");
UserFactor activatedUserFactor = userFactorApi.activateFactor("userId", "factorId", activateFactorRequest);
```
[//]: # (end: activateFactor)

### Verify a Factor

[//]: # (method: verifyFactor)
```java
UserFactorApi userFactorApi = new UserFactorApi(client);
UserFactor userFactor = userFactorApi.getFactor("userId", "factorId");
VerifyFactorRequest verifyFactorRequest = new VerifyFactorRequest();
verifyFactorRequest.setPassCode("123456");
VerifyUserFactorResponse verifyUserFactorResponse =
    userFactorApi.verifyFactor("userId", "factorId", "templateId", 10, "xForwardedFor", "userAgent", "acceptLanguage", verifyFactorRequest);
```
[//]: # (end: verifyFactor)

### List all Applications

[//]: # (method: listApplication)
```java
ApplicationApi applicationApi = new ApplicationApi(client);
List<Application> applications = applicationApi.listApplications(null, null, 10, null, null, true);
```
[//]: # (end: listApplication)

### Get an Application

[//]: # (method: getApplication)
```java
ApplicationApi applicationApi = new ApplicationApi(client);
Application app = applicationApi.getApplication("appId", null);
```
[//]: # (end: getApplication)

### Create a SWA Application

[//]: # (method: createSwaApplication)
```java
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
```
[//]: # (end: createSwaApplication)

### List System Logs
[//]: # (method: listSysLogs)
```java
SystemLogApi systemLogApi = new SystemLogApi(client);

// use a filter (start date, end date, filter, or query, sort order) all options are nullable
List<LogEvent> logEvents = systemLogApi.listLogEvents(null, null, null, "interestingURI.com", 100, "ASCENDING", null);
```
[//]: # (end: listSysLogs)

### Call other API endpoints

Not every API endpoint is represented by a method in this library. You can call any Okta management API endpoint using this generic syntax:

[//]: # (method: callAnotherEndpoint)
```java
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
```
[//]: # (end: callAnotherEndpoint)

### Thread Safety

Every instance of the SDK `Client` is thread-safe. You **should** use the same instance throughout the entire lifecycle of your application. Each instance has its own Connection pool and Caching resources that are automatically released when the instance is garbage collected.

## Paging

Paging is handled automatically when iterating over a collection.

[//]: # (method: paging)
```java
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
usersPagedListOne.getItems().forEach(tmpUser -> log.info("User: {}", tmpUser.getProfile().getEmail()));
```
[//]: # (end: paging)

<a name="spring-support"></a>
## Inject the Okta Java SDK in Spring

To integrate the Okta Java SDK into your Spring Boot application you just need to add a dependency:

```xml
<dependency>
    <groupId>com.okta.spring</groupId>
    <artifactId>okta-spring-sdk</artifactId>
    <version>${okta.spring.version}</version>
</dependency>
```

Then define the following properties:

| Key | Description |
------|--------------
| `okta.client.orgUrl` | Your Okta Url: `https://{yourOktaDomain}`, i.e. `https://dev-123456.okta.com` |
| `okta.client.token` | An Okta API token, see [creating an API token](https://developer.okta.com/docs/api/getting_started/getting_a_token) for more info. |

**NOTE:** The configuration techniques described in the [configuration reference](#configuration-reference) section will work as well.

All that is left is to inject the client (`com.okta.sdk.client.Client`)! Take a look at [this post](https://spring.io/blog/2007/07/11/setter-injection-versus-constructor-injection-and-the-use-of-required/) for more info on the best way to inject your beans.

For more information check out the [Okta Spring Boot Starter](https://github.com/okta/okta-spring-boot/) project!

## Configuration reference
  
This library looks for configuration in the following sources:

0. An `okta.yaml` at the root of the applications classpath
0. An `okta.yaml` file in a `.okta` folder in the current user's home directory (`~/.okta/okta.yaml` or `%userprofile%\.okta\okta.yaml`)
0. Environment variables
0. Java System Properties
0. Configuration explicitly set programmatically (see the example in [Getting started](#getting-started))
 
Higher numbers win. In other words, configuration passed via the constructor will override configuration found in environment variables, which will override configuration in `okta.yaml` (if any), and so on.
 
### YAML configuration
 
The full YAML configuration looks like:
 
```yaml
okta:
  client:
    connectionTimeout: 30 # seconds
    orgUrl: "https://{yourOktaDomain}" # i.e. https://dev-123456.okta.com
    proxy:
      port: null
      host: null
      username: null
      password: null
    token: yourApiToken
    requestTimeout: 0 # seconds
    rateLimit:
      maxRetries: 4
```
 
### Environment variables
 
Each one of the configuration values above can be turned into an environment variable name with the `_` (underscore) character:
 
* `OKTA_CLIENT_CONNECTIONTIMEOUT`
* `OKTA_CLIENT_TOKEN`
* and so on

### System properties

Each one of the configuration values written in 'dot' notation to be used as a Java system property:
* `okta.client.connectionTimeout`
* `okta.client.token`
* and so on

## Connection Retry

By default, this SDK will **retry** requests that return with response code `503`, `504`, `429`(caused by rate limiting), or socket/connection exceptions.

Default configuration tells SDK to retry requests up to 4 times without time limitation:
```properties
okta.client.requestTimeout = 0 //Sets the maximum number of seconds to wait when retrying before giving up.
okta.client.rateLimit.maxRetries = 4 //Sets the maximum number of attempts to retrying before giving up.
```

For interactive clients (i.e. web pages) it is optimal to set `requestTimeout` to be 10 sec (or less, based on your needs), and the `maxRetries` attempts to be 0.
This means the requests will retry as many times as possible within 10 seconds:

```properties
okta.client.requestTimeout = 10
okta.client.rateLimit.maxRetries = 0
```

or
 
```java
import com.okta.sdk.client.Client;
import com.okta.sdk.client.Clients;

Client client = Clients.builder()
                .setRetryMaxElapsed(10)
                .setRetryMaxAttempts(0)
                .build();
```

For batch/non-interactive processes optimal values are opposite. It is optimal to set `requestTimeout` to be 0, and the `maxRetries` attempts to be 5.
The SDK will retry requests up to 5 times before failing:
```properties
okta.client.requestTimeout = 0
okta.client.rateLimit.maxRetries = 5
```

If you need to limit execution time and retry attempts, you can set both `requestTimeout` and the `maxRetries`.
For example, the following example would retry up to 15 times within 30 seconds:
```properties
okta.client.requestTimeout = 30
okta.client.rateLimit.maxRetries = 15
```

To disable the retry functionality you need to set both variables to zero:
```properties
okta.client.requestTimeout = 0
okta.client.rateLimit.maxRetries = 0
```

## Caching

By default, a simple production-grade in-memory CacheManager will be enabled when the Client instance is created. This CacheManager implementation has the following characteristics:

- It assumes a default time-to-live and time-to-idle of 1 hour for all cache entries.
- It auto-sizes itself based on your application's memory usage. It will not cause OutOfMemoryExceptions.

**The default cache manager is not suitable for an application deployed across multiple JVMs.**

This is because the default implementation is 100% in-memory (in-process) in the current JVM. If more than one JVM is deployed with the same application codebase - for example, a web application deployed on multiple identical hosts for scaling or high availability - each JVM would have it's own in-memory cache.

As a result, if your application that uses an Okta Client instance is deployed across multiple JVMs, you SHOULD ensure that the Client is configured with a CacheManager implementation that uses coherent and clustered/distributed memory.

See the [`ClientBuilder` Javadoc](https://developer.okta.com/okta-sdk-java/apidocs/com/okta/sdk/client/ClientBuilder) for more details on caching.

### Caching for applications deployed on a single JVM

If your application is deployed on a single JVM and you still want to use the default CacheManager implementation, but the default cache configuration does not meet your needs, you can specify a different configuration. For example:

[//]: # (method: complexCaching)
```java
Caches.newCacheManager()
    .withDefaultTimeToLive(300, TimeUnit.SECONDS) // default
    .withDefaultTimeToIdle(300, TimeUnit.SECONDS) //general default
    .withCache(forResource(User.class) //User-specific cache settings
        .withTimeToLive(1, TimeUnit.HOURS)
        .withTimeToIdle(30, TimeUnit.MINUTES))
    .withCache(forResource(Group.class) //Group-specific cache settings
        .withTimeToLive(1, TimeUnit.HOURS))
    //... etc ...
    .build();
```
[//]: # (end: complexCaching)

### Disable Caching

While production applications will usually enable a working CacheManager as described above, you might wish to disable caching entirely. You can do this by configuring a disabled CacheManager instance. For example:

[//]: # (method: disableCaching)
```java
ApiClient client = Clients.builder()
    .setCacheManager(Caches.newDisabledCacheManager())
    .build();
```
[//]: # (end: disableCaching)

## Building the SDK

In most cases, you won't need to build the SDK from source. If you want to build it yourself, take a look at the [build instructions wiki](https://github.com/okta/okta-sdk-java/wiki/Build-It) (though just cloning the repo and running `mvn install` should get you going).
 
## Contributing
 
We are happy to accept contributions and PRs! Please see the [contribution guide](CONTRIBUTING.md) to understand how to structure a contribution.

[devforum]: https://devforum.okta.com/
[javadocs]: https://developer.okta.com/okta-sdk-java/
[lang-landing]: https://developer.okta.com/code/java/
[github-issues]: https://github.com/okta/okta-sdk-java/issues
[github-releases]: https://github.com/okta/okta-sdk-java/releases
