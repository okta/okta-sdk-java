[<img src="https://devforum.okta.com/uploads/oktadev/original/1X/bf54a16b5fda189e4ad2706fb57cbb7a1e5b8deb.png" align="right" width="256px"/>](https://devforum.okta.com/)
[![Maven Central](https://img.shields.io/maven-central/v/com.okta.sdk/okta-sdk-api.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.okta.sdk%22%20a%3A%22okta-sdk-api%22)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Support](https://img.shields.io/badge/support-Developer%20Forum-blue.svg)][devforum]
[![API Reference](https://img.shields.io/badge/docs-reference-lightgrey.svg)][javadocs]

# Okta Java Management SDK

* [Release status](#release-status)
* [Need help?](#need-help)
* [Getting started](#getting-started)
* [Usage guide](#usage-guide)
* [Configuration reference](#configuration-reference)
* [Building the SDK](#building-the-sdk)
* [Contributing](#contributing)

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

:heavy_check_mark: The current stable major version series is: 1.x

| Version | Status                    |
| ------- | ------------------------- |
| 0.0.x | :warning: Retiring on 2019-04-09 ([migration guide](https://github.com//okta/okta-sdk-java/wiki/Migration-from-v0.0.4-to-1.x)) |
| 1.x   | :heavy_check_mark: Stable |
 
The latest release can always be found on the [releases page][github-releases].
 
## Need help?
 
If you run into problems using the SDK, you can
 
* Ask questions on the [Okta Developer Forums][devforum]
* Post [issues][github-issues] here on GitHub (for code errors)
 
## Getting started
 
To use this SDK you will need to include the following dependencies:

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

You'll also need:

* An Okta account, called an _organization_ (sign up for a free [developer organization](https://developer.okta.com/signup) if you need one)
* An [API token](https://developer.okta.com/docs/api/getting_started/getting_a_token)
 
Construct a client instance by passing it your Okta domain name and API token:
 
[//]: # (NOTE: code snippets in this README are updated automatically via a Maven plugin by running: mvn okta-code-snippet:snip.okta:okta-code-snippet-maven-plugin:snip)
 
[//]: # (method: createClient)
```java
Client client = Clients.builder()
    .setOrgUrl("{yourOktaDomain}")
    .setClientCredentials(new TokenClientCredentials("{apiToken}"))
    .build();
```
[//]: # (end: createClient)
 
Hard-coding the Okta domain and API token works for quick tests, but for real projects you should use a more secure way of storing these values (such as environment variables). This library supports a few different configuration sources, covered in the [configuration reference](#configuration-reference) section.
 
## Usage guide

These examples will help you understand how to use this library. You can also browse the full [API reference documentation][javadocs].

Once you initialize a `Client`, you can call methods to make requests to the Okta API.

### Authenticate a User

This library should be used with the Okta management API. For authentication, we recommend using an OAuth 2.0 or OpenID Connect library such as [Spring Security OAuth](https://spring.io/projects/spring-security-oauth) or [Okta's Spring Boot integration](https://github.com/okta/okta-spring-boot). For [Okta Authentcation API](https://developer.okta.com/docs/api/resources/authn) you can use [Authentication SDK](https://github.com/okta/okta-auth-java).

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
FactorList factors = user.listFactors();
```
[//]: # (end: listUserFactors)

### Enroll a User in a new Factor

[//]: # (method: enrollUserInFactor)
```java
SmsFactor smsFactor = client.instantiate(SmsFactor.class);
smsFactor.getProfile().setPhoneNumber("555 867 5309");
user.addFactor(smsFactor);
```
[//]: # (end: enrollUserInFactor)

### Activate a Factor

[//]: # (method: activateFactor)
```java
Factor factor = user.getFactor("factorId");
VerifyFactorRequest verifyFactorRequest = client.instantiate(VerifyFactorRequest.class);
verifyFactorRequest.setPassCode("123456");
factor.activate(verifyFactorRequest);
```
[//]: # (end: activateFactor)

### Verify a Factor

[//]: # (method: verifyFactor)
```java
Factor factor = user.getFactor("factorId");
VerifyFactorRequest verifyFactorRequest = client.instantiate(VerifyFactorRequest.class);
verifyFactorRequest.setPassCode("123456");
VerifyFactorResponse response = factor.verify(verifyFactorRequest);
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

## Configuration reference
  
This library looks for configuration in the following sources:

0. An `okta.yaml` at the root of the applications classpath
0. An `okta.yaml` file in a `.okta` folder in the current user's home directory (`~/.okta/okta.yaml` or `%userprofile\.okta\okta.yaml`)
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
    orgUrl: "https://{yourOktaDomain}" # i.e. https://dev-123456.oktapreview.com
    proxy:
      port: null
      host: null
      username: null
      password: null
    token: {apiToken}
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

Each one of of the configuration values written in 'dot' notation to be used as a Java system property:
* `okta.client.connectionTimeout`
* `okta.client.token`
* and so on

## Connection Retry / Rate Limiting

By default this SDK will retry requests that are return with a `503`, `504`, `429`, or socket/connection exceptions.  To disable this functionality set `okta.client.requestTimeout` and `okta.client.rateLimit.maxRetries` to `0`.

Setting only one of the values to zero will disable that check. Meaning, by default, four retry attempts will be made. If you set `okta.client.requestTimeout` to `45` seconds and `okta.client.rateLimit.maxRetries` to `0`. This SDK will continue to retry indefinitely for `45` seconds.  If both values are non zero, this SDK will attempt to retry until either of the conditions are met (not both).

## Building the SDK
 
In most cases, you won't need to build the SDK from source. If you want to build it yourself, take a look at the [build instructions wiki](https://github.com/okta/okta-sdk-java/wiki/Build-It) (though just cloning the repo and running `mvn install` should get you going).
 
## Contributing
 
We're happy to accept contributions and PRs! Please see the [contribution guide](CONTRIBUTING.md) to understand how to structure a contribution.

[devforum]: https://devforum.okta.com/
[javadocs]: https://developer.okta.com/okta-sdk-java/
[lang-landing]: https://developer.okta.com/code/java/
[github-issues]: https://github.com/okta/okta-sdk-java/issues
[github-releases]: https://github.com/okta/okta-sdk-java/releases
