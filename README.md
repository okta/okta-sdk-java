# oktasdk-javas

This SDK is in EA, so all existing features are supported by Okta in a production setting.

Please visit [Okta's Management Java SDK](http://developer.okta.com/docs/sdk/core/java_api_sdk/index.html) for complete documentation.

To build and install:

1. Clone the repo
2. Navigate to the repo directory. It should contain `pom.xml`
3. Build with tests `mvn install` or without tests `mvn -Dmaven.test.skip=true install`

### Client configuration
```java
import com.okta.sdk.framework.ApiClientConfiguration;

ApiClientConfiguration oktaSettings = new ApiClientConfiguration(
                                        "https://your_org.okta.com",
                                        "your_api_key");

// Init with custom headers
Map<String, String> headers = new HashMap<String, String>();

headers.put("OktaAuthorization", "SWSS {{your_api_key}}");
headers.put("Authorization", "Basic {{AUTHKEY}}");

ApiClientConfiguration oktaSettings = new ApiClientConfiguration(
                                        "https://your_org.okta.com",
                                        "your_api_key",
                                        headers
);
```

### AuthApiClient
This client is used to authenticate and validate user credentials. 
The [Authentication API](http://developer.okta.com/docs/api/resources/authn.html) has more information about the various states of authentication.
```java
AuthApiClient authClient = new AuthApiClient(oktaSettings);

// Check if the user credentials are valid
AuthResult result = authClient.authenticate(username, password, someRelayState);
// The result has a getStatus method which is a string of status of the request.
// Example - SUCCESS for successful authentication
String status = result.getStatus();
```

### UserApiClient and CRUD
This client is used to perform CRUD operations on [`User` objects](http://developer.okta.com/docs/api/resources/users.html).

```java
import com.okta.sdk.clients.UserApiClient;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.FilterBuilder;
import com.okta.sdk.models.users.LoginCredentials;
import com.okta.sdk.models.users.Password;
import com.okta.sdk.models.users.User;
import com.okta.sdk.models.users.UserProfile;

UserApiClient userApiClient = new UserApiClient(oktaSettings);

// Create a new user
// First Name, Last Name, Email and Login are required. Password is optional.
// The boolean variable is for activate which means that activate the user as soon as 
// it is created.
User newUser = userApiClient.createUser(
                "First",
                "Last",
                "login@example.com",
                "email@example.com",
                true);

// Create a new user with a password.
// For this you need to create a UserProfile object and a LoginCredentials object
UserProfile userProfile = new UserProfile();
userProfile.setFirstName("First");
userProfile.setLastName("Last");
userProfile.setEmail("email@example.com");
userProfile.setLogin("login@example.com");

LoginCredentials loginCredentials = new LoginCredentials();
Password password = new Password();
password.setValue("Password");
loginCredentials.setPassword(password);

User user = new User();
user.setProfile(userProfile);
user.setCredentials(loginCredentials);

boolean activate = true;
userApiClient.createUser(user, activate);

// Update the user's first name
user.getProfile().setFirstName("newFirstName");
userApiClient.updateUser(user);

// Deactivate the user
userApiClient.deleteUser(user.getId());
```

#### Filter, Search, and Query for Users
To retrieve a `User`, use the `getUser` method when the ID, login, or email is known:

```java
// All searches are case sensitive
User userById = userApiClient.getUser("00u0abcdefghIjklmo7");
User userByEmail = userApiClient.getUser("email@example.com");
User userByUsername = userApiClient.getUser("email");
```

You can apply a [filter](http://developer.okta.com/docs/api/resources/users.html#filters) using the `FilterBuilder` class:
```java
// Return all users whose first name is John
String firstName = "John";
FilterBuilder filterBuilder = new FilterBuilder("profile.firstName eq \"" + firstName + "\"");
List<User> users = userApiClient.getUsersWithFilter(filterBuilder);
```

To query for users, use the `getUsersWithQuery` method:
```java
// Return all users whose firstName, lastName, or email constinas "test"
List<User> users = userApiClient.getUsersWithQuery("test");

```

For any `User` profile attribute, custom defined profile attribute, and top-level attributes (`id`, `status`, `created`, `activated`, `statusChanged`, and `lastUpdated`), use the `getUsersWithAdvancedSearch` method.

This performs a **case insensitive** search against all fields specified.
```java
// Return all users with custom profile attribute "flightNumber" matching "A415"
FilterBuilder filterBuilder = new FilterBuilder("profile.flightNumber eq \"A415\"");
List<User> users = userApiClient.getUsersWithAdvancedSearch(filterBuilder);
```
**Note:** The most upd to date data can be delayed up to a few seconds, so use for convenience.

### Paging
```java
PagedResults<User> pagedResults = userApiClient.getUsersPagedResultsWithLimit(10);
processUsers(pagedResults);

while (!pagedResults.isLastPage()) {
    pagedResults = userApiClient.getUsersPagedResultsByUrl(pagedResults.getNextUrl());
    processUsers(pagedResults);
}

void processUsers(PagedResults<User>) {
    for (User user : pagedResults.getResult()) {
        // Perform user action
    }
}
```

### Get Custom Properties
Universal Directory enables the ability to add properties that are not mapped to an object.

```java
UserApiClient usersClient = new UserApiClient(oktaSettings);
User user = userApiClient.getUser("ID/loginName/loginShortName");
Map unmappedProperties = user.getProfile().getUnmapped());
```

### GroupsClient and CRUD
This client is used to perform CRUD operations on [`Group` objects](http://developer.okta.com/docs/api/resources/groups.html).
```java
import com.okta.sdk.clients.UserGroupApiClient;
import com.okta.sdk.models.usergroups.UserGroup;
import com.okta.sdk.models.usergroups.UserGroupProfile;

UserGroupApiClient groupsClient = new UserGroupApiClient(oktaSettings);

// Create and add group
UserGroup group = new UserGroup();
UserGroupProfile profile = new UserGroupProfile();

profile.setName("admins");
profile.setDescription("Admins of org");
group.setProfile(profile);

groupsClient.createUserGroup(group);

// Retrieve the group by name
List<UserGroup> groups = groupsClient.getUserGroupsWithQuery("admins");
UserGroup admins = groups.get(0);

// Or get by ID
UserGroup admins = groupsClient.getUserGroup("00g0abcdefghIjklmo7");

// Update the description of the group
admins.getProfile().setDescription("Updated Admins of org");
groupsClient.updateUserGroup(admins.getId(), admins);

// Remove the group
groupsClient.deleteUserGroup(admins.getId());
```

### Additional configuration

#### DNS TTL Caching
When using a [Security Manager](https://docs.oracle.com/javase/tutorial/essential/environment/security.html) or Java <1.6, the default JDK DNS cache policy is set to cache forever. This can cause service issues if the server at your cached IP goes down. We recommend setting a TTL of 30 seconds. To do so, uncomment and set `networkaddress.cache.ttl` to 30 in `$JRE_HOME/lib/security/java.security`.
