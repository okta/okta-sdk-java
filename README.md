[<img src="https://aws1.discourse-cdn.com/standard14/uploads/oktadev/original/1X/0c6402653dfb70edc661d4976a43a46f33e5e919.png" align="right" width="256px"/>](https://devforum.okta.com/)
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
* Much more!
 
We also publish these libraries for Java:
 
* [Spring Boot Integration](https://github.com/okta/okta-spring-boot/)
* [Okta JWT Verifier for Java](https://github.com/okta/okta-jwt-verifier-java)
* [Authentication SDK](https://github.com/okta/okta-auth-java)
 
You can learn more on the [Okta + Java][lang-landing] page in our documentation.
 
## Release status

This library uses semantic versioning and follows Okta's [library version policy](https://developer.okta.com/code/library-versions/).

:heavy_check_mark: The current stable major version series is: 5.x.x

| Version | Status                    |
| ------- | ------------------------- |
| 0.0.x | :warning: Retired |
| 1.x   | :warning: Retired |
| 2.x.x | :warning: Retired |
| 3.x.x | :clock9: Retiring effective November 10, 2021 |
| 4.x.x | :heavy_check_mark: Stable ([migration guide](https://github.com/okta/okta-sdk-java/blob/master/MIGRATING.md#migrating-from-3xx-to-400)) |
| 5.x.x | :heavy_check_mark: Stable ([migration guide](https://github.com/okta/okta-sdk-java/blob/master/MIGRATING.md#migrating-from-4xx-to-500)) |

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
    <version>${okta.version}</version>
</dependency>
<dependency>
    <groupId>com.okta.sdk</groupId>
    <artifactId>okta-sdk-impl</artifactId>
    <version>${okta.version}</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>com.okta.sdk</groupId>
    <artifactId>okta-sdk-httpclient</artifactId>
    <version>${okta.version}</version>
    <scope>runtime</scope>
</dependency>
```

For Gradle:

```groovy
compile "com.okta.sdk:okta-sdk-api:${okta.version}"
runtime "com.okta.sdk:okta-sdk-impl:${okta.version}"
runtime "com.okta.sdk:okta-sdk-httpclient:${okta.version}"
```

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
Client client = Clients.builder()
    .setOrgUrl("https://{yourOktaDomain}")  // e.g. https://dev-123456.okta.com
    .setClientCredentials(new TokenClientCredentials("{apiToken}"))
    .build();
```
[//]: # (end: createClient)
 
Hard-coding the Okta domain and API token works for quick tests, but for real projects you should use a more secure way of storing these values (such as environment variables). This library supports a few different configuration sources, covered in the [configuration reference](#configuration-reference) section.

In some cases, it maybe needed to check if the client is ready and able to execute requests. The _**isReady**_ method can be used for this. It does not produce exceptions if the wrong orgUrl or token have been used, but it returns a boolean indicating the client readiness.

[//]: # (method: isClientReady)
```java
boolean isClientReadyStatus = client.isReady(client::listApplications);
```
[//]: # (end: isClientReady)

## OAuth 2.0

Okta allows you to interact with Okta APIs using scoped OAuth 2.0 access tokens. Each access token enables the bearer to perform specific actions on specific Okta endpoints, with that ability controlled by which scopes the access token contains.

This SDK supports this feature only for service-to-service applications. Check out [our guides](https://developer.okta.com/docs/guides/implement-oauth-for-okta-serviceapp/overview/) to learn more about how to register a new service application using a private and public key pair.

When using this approach, you won't need an API Token because the SDK will request an access token for you. In order to use OAuth 2.0, construct a client instance by passing the following parameters:

[//]: # (method: createOAuth2Client)
```java
Client client = Clients.builder()
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
```
[//]: # (end: createOAuth2Client)
 
## Usage guide

These examples will help you understand how to use this library. You can also browse the full [API reference documentation][javadocs].

Once you initialize a `Client`, you can call methods to make requests to the Okta API.

### Authenticate a User

This library should be used with the Okta management API. For authentication, we recommend using an OAuth 2.0 or OpenID Connect library such as [Spring Security OAuth](https://spring.io/projects/spring-security-oauth) or [Okta's Spring Boot integration](https://github.com/okta/okta-spring-boot). For [Okta Authentication API](https://developer.okta.com/docs/api/resources/authn) you can use [Authentication SDK](https://github.com/okta/okta-auth-java).

### Get a User

[//]: # (method: getUser)
```java
User user = client.getUser("a-user-id");
```
[//]: # (end: getUser)

### List all Users

[//]: # (method: listAllUsers)
```java
UserList users = client.listUsers();

// stream
client.listUsers().stream()
    .forEach(user -> {
      // do something
    });
```
[//]: # (end: listAllUsers)

For more examples of handling collections see the [paging](#paging) section below.

### Filter or search for Users

[//]: # (method: userSearch)
```java
// search by email
UserList users = client.listUsers("jcoder@example.com", null, null, null, null);

// filter parameter
users = client.listUsers(null, "status eq \"ACTIVE\"", null, null, null);
```
[//]: # (end: userSearch)

### Create a User

[//]: # (method: createUser)
```java
User user = UserBuilder.instance()
    .setEmail("joe.coder@example.com")
    .setFirstName("Joe")
    .setLastName("Code")
    .buildAndCreate(client);
```
[//]: # (end: createUser)

### Create a User with Group(s)

[//]: # (method: createUserWithGroups)
```java
User user = UserBuilder.instance()
    .setEmail("joe.coder@example.com")
    .setFirstName("Joe")
    .setLastName("Code")
    .setGroups(new HashSet<>(Arrays.asList("group-id-1", "group-id-2")))
    .buildAndCreate(client);
```
[//]: # (end: createUserWithGroups)

### Update a User

[//]: # (method: updateUser)
```java
user.getProfile().setFirstName("new-first-name");
user.update();
```
[//]: # (end: updateUser)
 
### Get and set custom attributes

Custom attributes must first be defined in the Okta profile editor. Then, you can work with custom attributes on a user:

[//]: # (method: customAttributes)
```java
user.getProfile().put("customPropertyKey", "a value");
user.getProfile().get("customPropertyKey");
```
[//]: # (end: customAttributes)

### Remove a User

[//]: # (method: deleteUser)
```java
user.deactivate();
user.delete();
```
[//]: # (end: deleteUser)

### List a User's Groups

[//]: # (method: listUsersGroup)
```java
GroupList groups = user.listGroups();
```
[//]: # (end: listUsersGroup)

### Create a Group

[//]: # (method: createGroup)
```java
Group group = GroupBuilder.instance()
    .setName("a-group-name")
    .setDescription("Example Group")
    .buildAndCreate(client);
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
UserFactorList factors = user.listFactors();
```
[//]: # (end: listUserFactors)

### Enroll a User in a new Factor

[//]: # (method: enrollUserInFactor)
```java
SmsUserFactor smsFactor = client.instantiate(SmsUserFactor.class);
smsFactor.getProfile().setPhoneNumber("555 867 5309");
user.enrollFactor(smsFactor);
```
[//]: # (end: enrollUserInFactor)

### Activate a Factor

[//]: # (method: activateFactor)
```java
UserFactor factor = user.getFactor("factorId");
ActivateFactorRequest activateFactorRequest = client.instantiate(ActivateFactorRequest.class);
activateFactorRequest.setPassCode("123456");
factor.activate(activateFactorRequest);
```
[//]: # (end: activateFactor)

### Verify a Factor

[//]: # (method: verifyFactor)
```java
UserFactor factor = user.getFactor("factorId");
VerifyFactorRequest verifyFactorRequest = client.instantiate(VerifyFactorRequest.class);
verifyFactorRequest.setPassCode("123456");
VerifyUserFactorResponse verifyUserFactorResponse = factor.setVerify(verifyFactorRequest).verify();
```
[//]: # (end: verifyFactor)

### List all Applications

[//]: # (method: listApplication)
```java
ApplicationList applications = client.listApplications();
```
[//]: # (end: listApplication)

### Get an Application

[//]: # (method: getApplication)
```java
Application app = client.getApplication("appId");
```
[//]: # (end: getApplication)

### Create a SWA Application

[//]: # (method: createSwaApplication)
```java
SwaApplication swaApp = client.instantiate(SwaApplication.class)
    .setSettings(client.instantiate(SwaApplicationSettings.class)
    .setApp(client.instantiate(SwaApplicationSettingsApplication.class)
      .setButtonField("btn-login")
      .setPasswordField("txtbox-password")
      .setUsernameField("txtbox-username")
      .setUrl("https://example.com/login.html")));
```
[//]: # (end: createSwaApplication)

### List System Logs
[//]: # (method: listSysLogs)
```java
// page through all log events
LogEventList logEvents = client.getLogs();

// or use a filter (start date, end date, filter, or query, sort order) all options are nullable
logEvents = client.getLogs(null, null, null, "interestingURI.com", "ASCENDING");
```
[//]: # (end: listSysLogs)

### Call other API endpoints

Not every API endpoint is represented by a method in this library. You can call any Okta management API endpoint using this generic syntax:

[//]: # (method: callAnotherEndpoint)
```java
// Create an IdP, see: https://developer.okta.com/docs/api/resources/idps#add-identity-provider
ExtensibleResource resource = client.instantiate(ExtensibleResource.class);
ExtensibleResource protocolNode = client.instantiate(ExtensibleResource.class);
protocolNode.put("type", "OAUTH");
resource.put("protocol", protocolNode);
ExtensibleResource result = client.http()
    .setBody(resource)
    .post("/api/v1/idps", ExtensibleResource.class);
```
[//]: # (end: callAnotherEndpoint)

### Thread Safety

Every instance of the SDK `Client` is thread-safe. You **should** use the same instance throughout the entire lifecycle of your application. Each instance has its own Connection pool and Caching resources that are automatically released when the instance is garbage collected.

## Paging

Paging is handled automatically when iterating over a any collection.

[//]: # (method: paging)
```java
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

When you use OAuth 2.0, the full YAML configuration looks like:

```yaml
okta:
  client:
    connectionTimeout: 30 # seconds
    orgUrl: "https://{yourOktaDomain}" # i.e. https://dev-123456.oktapreview.com
    proxy:
      port: null
      host: null
      username: null
      password: null
    authorizationMode: "PrivateKey"
    clientId: "yourClientId"
    kid: "yourKeyId" # i.e. "92u3YfA6GgQwL1uSFbgqysQjz61kWtuAhgM2yHbmCuM". This parameter is optional
    scopes: "okta.users.read okta.apps.read"
    privateKey: |
      -----BEGIN PRIVATE KEY-----
      b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAABFwAAAAdzc2gtcn
      ...b3BlbnNzaC1rZXktdjEAAAAAAAAAAAAABAAABFwAAAAdzc2gtcn-myN3AmcmmPMS...
      CO7Hnjlg77HRNFXPAAAAFWxrYW1pcmVkZHlAdm13YXJlLmNvbQECAwQF
      -----END PRIVATE KEY-----
    # or specify a path to a PEM file
    # privateKey: "/path/to/yourPrivateKey.pem" # PEM format. This SDK supports RSA AND EC algorithms - RS256, RS384, RS512, ES256, ES384, ES512.
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

## Connection Retry / Rate Limiting

By default, this SDK will retry requests that return with response code `503`, `504`, `429`, or socket/connection exceptions.

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
         .withTimeToLive(2, TimeUnit.HOURS))
     //... etc ...
     .build();
```
[//]: # (end: complexCaching)

### Disable Caching

While production applications will usually enable a working CacheManager as described above, you might wish to disable caching entirely. You can do this by configuring a disabled CacheManager instance. For example:

[//]: # (method: disableCaching)
```java
Client client = Clients.builder()
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
