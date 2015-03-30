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

UsersApiClient usersClient = new UsersApiClient(oktaSettings);
```

###CRUD
```java
// Create
User newUser = usersClient.createUser(
                "First",
                "Last",
                "login@example.com",
                "email@example.com",
                true);

// Read
newUser = usersClient.getUser(newUser.getId());

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
