# Integration Tests

The ITs run automatically as part of the build.  You must have a `~/.okta/okta.yaml` configured, or equivalent System Properties/environment variables.

See [`src/test/resources/okta.yaml.sample`](src/test/resources/okta.yaml.sample) for a fully-annotated template.

To run the ITs against a test server [okta/okta-sdk-test-server](https://github.com/okta/okta-sdk-test-server) use the profile: `-Psdk-test-server` for example: `mvn install -Psdk-test-server`.

## Required OAuth2 scopes

When using **OAuth2 / Private Key** authentication the service app in your Okta org must be granted the following scopes. Missing scopes cause Okta to return `401` _before_ evaluating path parameters, which changes the expected error codes in several tests.

| Scope | Required by |
|-------|-------------|
| `okta.users.read` / `okta.users.manage` | User & lifecycle tests |
| `okta.groups.read` / `okta.groups.manage` | Group tests |
| `okta.apps.read` / `okta.apps.manage` | Application tests |
| `okta.policies.read` / `okta.policies.manage` | Policy tests |
| **`okta.roles.read`** | `SubscriptionIT` — `GET /api/v1/roles/{roleRef}/subscriptions*` |
| **`okta.roles.manage`** | `SubscriptionIT` — `POST /api/v1/roles/{roleRef}/subscriptions*/subscribe\|unsubscribe` |
| `okta.idps.read` / `okta.idps.manage` | Identity Provider tests |
| `okta.authorizationServers.read` / `okta.authorizationServers.manage` | AuthServer tests |
| `okta.inlineHooks.read` / `okta.inlineHooks.manage` | InlineHook tests |
| `okta.schemas.read` / `okta.schemas.manage` | Schema tests |
| `okta.realm.read` / `okta.realm.manage` | Realm tests |
| `okta.userTypes.read` / `okta.userTypes.manage` | UserType tests |
| `okta.logs.read` | Syslog tests |

> **Note on `okta.roles.*`:** Without these scopes, `SubscriptionIT.testRoleSubscriptionLifecycle`
> receives HTTP `401` (scope check fails) instead of `404`/`400` (invalid `roleRef` value).
> The test accepts all three codes; add the scopes to get the more precise `404`/`400` responses.

## Configuration options

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