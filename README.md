# okta-sdk-java

This SDK is in EA, so all existing features are supported by Okta in a production setting

To build and install:

1. Clone the repo
2. Navigate to the repo directory. It should contain pom.xml
3. Build with tests `mvn install`

### Client configuration

There are a few ways to configure the client, but the easiest way is to create a `~/.okta/okta.yaml`file and set the `token` and `orgUrl` values:

``` yaml
okta:
  client:
    token: <your-api-token>
    orgUrl: https://dev-123456.oktapreview.com
```

### Creating a Client

 Once you create your `okta.yaml` file, you can create a client with a couple of lines:

``` java
// Instantiate a builder for your Client. If needed, settings like Proxy and Caching can be defined here.
ClientBuilder builder = Clients.builder();

// No need to define anything else; build the Client instance. The ClientCredential information will be automatically found
// in pre-defined locations.
Client client = builder.build();
```

### Client CRUD Operations

The client is used to perform CRUD operations against Okta's management APIs. 

```java

// Create a new group
UserGroup group = client.instantiate(UserGroup.class);

// set the name
group.getProfile().setName("my-user-group");

// save it
group = client.createGroup(group);

// print a couple of the attributes
System.out.println("Group: '"+ group.getId()+ "' was last updated on: "+ group.getLastUpdated());

// list all users (pageing is handled automatically)
UserList users = client.listUsers();

// get the first user in the collection
System.out.println("First user: "+ users.iterator().next().getProfile().getEmail());
```

### Paging

Paging is handled automatically when iterating over a any collection.
