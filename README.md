[<img src="https://devforum.okta.com/uploads/oktadev/original/1X/bf54a16b5fda189e4ad2706fb57cbb7a1e5b8deb.png" align="right" width="256px"/>](https://devforum.okta.com/)
[![Maven Central](https://img.shields.io/maven-central/v/com.okta.sdk/okta-sdk-api.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.okta.sdk%22%20a%3A%22okta-sdk-api%22)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Support](https://img.shields.io/badge/support-Developer%20Forum-blue.svg)](https://devforum.okta.com/)

# okta-sdk-java

This SDK is in EA, so all existing features are supported by Okta in a production setting.

This version of the Okta Java SDK supports CRUD (Create, Read, Update, Delete) operations for the following resource:
- User
- Group
- Group Membership Rules

# Usage

## Javadocs

You can see this project's Javadocs at https://developer.okta.com/okta-sdk-java/apidocs/. 

## Dependencies

The only compile time dependency you will need is `okta-sdk-api`.  You will also need to add the implementation dependencies too: `okta-sdk-impl` and `okta-sdk-httpclient`.

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

## SNAPSHOT Dependencies

Snapshots are deployed off of the 'master' branch to OSSRH and can be consumed using the following repository configured for Apache Maven or Gradle:
```
https://oss.sonatype.org/content/repositories/snapshots/
```

## Client configuration

There are a few ways to configure the client, but the easiest way is to create a `~/.okta/okta.yaml`file and set the `token` and `orgUrl` values:

``` yaml
okta:
  client:
    token: <your-api-token>
    orgUrl: https://dev-123456.oktapreview.com
```

## Creating a Client

 Once you create your `okta.yaml` file, you can create a client with a couple of lines:

``` java
// Instantiate a builder for your Client. If needed, settings like Proxy and Caching can be defined here.
ClientBuilder builder = Clients.builder();

// No need to define anything else; build the Client instance. The ClientCredential information will be automatically found
// in pre-defined locations.
Client client = builder.build();
```

For more details see: [Creating a Client](https://github.com/okta/okta-sdk-java/wiki/Creating-a-Client)

## Client CRUD Operations

The client is used to perform CRUD operations against Okta's management APIs. 

Create a group:
```java

UserGroup group = GroupBuilder.instance()
        .setName("my-user-group-" + UUID.randomUUID().toString())
        .setDescription("Quickstart created Group")
        .buildAndCreate(client);

// print a couple of the attributes
println("Group: '" + group.getId() + "' was last updated on: " + group.getLastUpdated());

```


Create a User Account:

``` java
String email = "joe.coder+" + UUID.randomUUID().toString() + "@example.com";

User user = UserBuilder.instance()
    .setEmail(email)
    .setFirstName("Joe")
    .setLastName("Coder")
    .setPassword("Password1")
    .setSecurityQuestion("Favorite security question?")
    .setSecurityQuestionAnswer("None of them!")
    .putProfileProperty("division", "Seven") // key/value pairs predefined in the user profile schema
    .setActive(true)
    .buildAndCreate(client);

String userId = user.getId();
println("User created with ID: " + userId);
```

Add user to the newly created group:
``` java 
user.addToGroup(group.getId());
```

User lookup by ID or email:
``` java
// You can look up user by ID
println("User lookup by ID: "+ client.getUser(userId).getProfile().getLogin());

// or by Email
println("User lookup by Email: "+ client.getUser(email).getProfile().getLogin());
```

## Paging

Paging is handled automatically when iterating over a any collection.

``` java
// get the list of users
UserList users = client.listUsers();

// get the first user in the collection
println("First user in collection: " + users.iterator().next().getProfile().getEmail());

// or loop through all of them (paging is automatic)
int ii = 0;
for (User tmpUser : users) {
    println("["+ ii++ +"] User: " + tmpUser.getProfile().getEmail());
}
```

## Contribute to the Project

Take a look at the (contribution guide)[CONTRIBUTING.md] and the [build instructions wiki](https://github.com/okta/okta-sdk-java/wiki/Build-It) (though just cloning the repo and running `mvn install` should get you going).
