# Integration Tests

The ITs run automatically as part of the build.  You must have a ~/.okta/okta.yaml configured, or equivalent System Properties/environment variables.

To run the ITs against a test server [okta/okta-sdk-test-server](https://github.com/okta/okta-sdk-test-server) use the profile: `-Psdk-test-server` for example: `mvn install -Psdk-test-server`.

| System Property              | Default Value  | Description | 
|------------------------------|----------------|-------------|
| okta.testServer.ref          | more-debugging | Tag or Branch of okta/okta-sdk-test-server |
| okta.testServer.verbose      | false          | Output more verbose error output |
| okta.testServer.allScenarios | false          | Run all test scenarios, useful when updating the test-server tag, this will find unimplemented tests |
 
## How to write Spec tests

ITs writen against the okat/okta-test-server must written in the following format:

``` java
@Test
// This annotation defines the scenario name, this allows the tests server to be run with the correct list of scenarios
@Scenario("list-groups")
// This annotation defines which test resources to be cleaned up automatically before the test is run
@TestResources(groups = "List Test Group")
void listGroupsTest() {
   ...
}
```