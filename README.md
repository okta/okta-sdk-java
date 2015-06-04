# oktasdk-java

This SDK is in EA, so all existing features are supported by Okta in a production setting

To build and install:

1. Clone the repo
2. Navigate to the repo directory. It should contain pom.xml
2. `mvn -Dmaven.test.skip=true install`

To run the build with tests:

1. Set the following environment variables:
    * OKTA_TEST_URL
    * OKTA_TEST_KEY
    * OKTA_TEST_ADMIN_NAME
    * OKTA_TEST_ADMIN_PASSWORD
2. `mvn build`


###Client configuration
```java
import com.okta.sdk.framework.ApiClientConfiguration;

ApiClientConfiguration oktaSettings = new ApiClientConfiguration(
                                        "https://your_org.okta.com",
                                        "your_api_key");
```

###AuthApiClient
This client is used to authenticate and validate user credentials. 
Information about the various states of authentication are available at http://developer.okta.com/docs/api/resources/authn.html
```java
AuthApiClient authClient = new AuthApiClient(oktaSettings);

// Check if the user credentials are valid
AuthResult result = authClient.authenticate(username, password, someRelayState);
// The result has a getStatus method which is a string of status of the request.
// Example - SUCCESS for successful authentication
String status = result.getStatus();
```

###UserApiClient and CRUD
This client is used to perform CRUD operations on user objects 
(http://developer.okta.com/docs/api/resources/users.html).
```java
UsersApiClient usersClient = new UsersApiClient(oktaSettings);

// Create a new user
// First Name, Last Name, Email and Login are required. Password is optional.
// The boolean variable is for activate which means that activate the user as soon as 
// it is created.
User newUser = usersClient.createUser(
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

// true is for activate user as soon as it is created
userClient.createUser(user, true);

// Read/Search
// There are plenty of methods for reading users.
// 1. Search user when user ID/loginName/loginShortName is known
User user = usersClient.getUser("ID/loginName/loginShortName");

// 2. Search user using filters. You can query the API for searching a user
// with the help of filters mentioned at - http://developer.okta.com/docs/api/resources/users.html#filters
// Example - search for first name. Returns a list of users matching that query
FilterBuilder filterBuilder = new FilterBuilder("profile.firstName eq \"" + firstName + "\"");
List<User> users = userClient.getUsersWithFilter(filterBuilder);

// 3. Search users only on firstName, lastName or email
// The parameter passed is searched in the attributes - firstName, lastName and email of all Users.
List<User> users = userClient.getUsersWithQuery("firstName/lastName/email");

// Update
newUser.getProfile().setLastName("NewLast");
usersClient.updateUser(newUser);

// Delete (for Users this is the same as deactivate)
usersClient.deleteUser(newUser.getId());
```

###Paging
```java
PagedResults<User> pagedResults = usersClient.getUsersPagedResultsWithLimit(10);

int counter = 0;
do {
    if(!pagedResults.isFirstPage()) {
        pagedResults = usersClient.getUsersPagedResultsByUrl(pagedResults.getNextUrl());
    }

    for(User user : pagedResults.getResult()) {
        // Do something with user
    }
}
while(!pagedResults.isLastPage());
```
