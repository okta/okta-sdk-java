# Okta Java Management SDK Migration Guide
 
This SDK uses semantic versioning and follows Okta's [library version policy](https://developer.okta.com/code/library-versions/). In short, we do not make breaking changes unless the major version changes!

## Migrating from 1.x.x to 2.0.0

Version 2.0.0 of this SDK introduces a number of breaking changes from previous versions. 
In addition to many new classes/interfaces, some existing classes/interfaces are no longer backward compatible due to method renaming and signature changes.

#### All `*Factor` classes Renamed to `*UserFactor`
This means that you will now have to start using the new `*UserFactor` objects instead.

### Package `com.okta.sdk.client.Client`

Below methods have been renamed for the sake of clarity.
   - Renamed `createRule` to `createGroupRule`
   - Renamed `getRule` to `getGroupRule`
   - Renamed `listRules` to `listGroupRules`

### Package `com.okta.sdk.resource.log.LogEventList`

The API used to get log info has undergone a signature change (note the order swap of `until` and `since` parameters).
   - `getLogs(String until, String since, String filter, String q, String sortOrder)`
   Signature changed to `getLogs(Date since, Date until, String filter, String q, String sortOrder)`
   
### Package `com.okta.sdk.resource.group.GroupList`

The API used to list the groups to which a user belongs, has undergone a signature change. 
There is no need for supplying the `expand` parameter anymore. 
   - `listGroups(String q, String filter, String expand)`
   Signature changed to `listGroups(String q, String filter)`
   
With this removal of `expand` parameter, the caller has two options of achieving the previous result:
- Make a second API call to the Group API and fetch the results.
- You can call the Groups API endpoint (or any Okta management API endpoint) using a syntax like below example:
  ```
  // List Groups API, see: https://developer.okta.com/docs/reference/api/groups/#list-groups
  GroupsList result = client.http()
      .addQueryParameter("expand", true)
      .get("/api/v1/groups", GroupsList.class);
  ```
- Note that the support for `expand` parameter might go away anytime in future.

### Package `com.okta.sdk.resource.application.SwaThreeFieldApplicationSettingsApplication`

Below APIs have undergone a name change.
   - `getTargetUrl` Renamed to `getTargetURL`
   - `setTargetUrl` Renamed to `setTargetURL`

### Package `com.okta.sdk.resource.group.Group`

With the introduction of enum type `com.okta.sdk.resource.group.GroupType`, the get group type operation will now make 
use of this new type instead of the `String` type used earlier.
   - `getType` Return type changed from `String` to `com.okta.sdk.resource.group.GroupType`
   
### Package `com.okta.sdk.resource.group.rule.GroupRule`

The properties `allGroupsValid` & `_embedded` were not used by the backend earlier and were always set to `null`.
As part of this upgrade, we will remove it from the method signatures.
   - Removed `getAllGroupsValid` and `setAllGroupsValid` (property `allGroupsValid` is being removed; 
   this option is being removed from future versions of Okta API)
   - Removed `getEmbedded` (property `_embedded` is removed; this property is undocumented in Okta API and hence being removed)
   - `delete(Boolean removeUsers)` Signature changed to `delete()`

### Package `com.okta.sdk.resource.log.LogAuthenticationContext`

The API to get log credential provider used to return a list of objects of type `LogCredentialProvider` earlier. 
This is now fixed to return a single object of type `LogCredentialProvider`.
   - `getCredentialProvider` Return type changed from `List<com.okta.sdk.resource.log.LogCredentialProvider>` to `com.okta.sdk.resource.log.LogCredentialProvider`

The API to get log credential type used to return a list of objects of type `LogCredentialType` earlier. 
This is now fixed to return a single object of type `LogCredentialType`.
- `getCredentialType` Return type changed from `List<com.okta.sdk.resource.log.LogCredentialType>` to `com.okta.sdk.resource.log.LogCredentialType`

### Package `com.okta.sdk.resource.policy.PolicyRule`

There is no need of exposing setter for the `read-only` property `id`.
   - Removed `setId` (property `id` is `read-only`)

### Package `com.okta.sdk.resource.user.factor.VerifyFactorRequest`

The property `tokenLifetimeSeconds` will not used by the backend going forward.
   - Removed `getTokenLifetimeSeconds` & `setTokenLifetimeSeconds` (property `tokenLifetimeSeconds` is removed)

### Package `com.okta.sdk.resource.user.Role`

With the introduction of enum type `com.okta.sdk.resource.role.RoleType`, the getter/setter for role type operation will now make 
use of this new type instead of the `String` type used earlier.
   - `getType` Return type changed from `String` to `com.okta.sdk.resource.role.RoleType`
   - `setType` Param type changed from `String` to `com.okta.sdk.resource.role.RoleType`

### Package `com.okta.sdk.resource.user.User`

The below method names have been refactored to be more apt:
   - Renamed `addGroupTarget` to `addGroupTargetToRole`
   - Renamed `addRole` to `assignRole`
   - Renamed `listGroupTargetsForRole` to `listGroupTargets`
   - Renamed `removeGroupTargetFromRole` to `removeGroupTarget`
   - Renamed `addFactor` to `enrollFactor`
   - Renamed `listRoles`to `listAssignedRoles`
   
The `forgotPassword` method has been removed. Use `resetPassword` instead to achieve the same functionality.

The `endAllSessions` method has been removed. Use `clearSessions` instead to achieve the same functionality.
 
Following methods have undergone a return type change inline with the refactoring of `UserFactor*` objects as mentioned above.
   - `listSupportedFactors` Return type changed from `com.okta.sdk.resource.user.factor.FactorList` to `com.okta.sdk.resource.user.factor.UserFactorList`
   - `getFactor` Return type changed from `com.okta.sdk.resource.user.factor.Factor` to `com.okta.sdk.resource.user.factor.UserFactor`
   - `listFactors` Return type changed from `com.okta.sdk.resource.user.factor.FactorList` to `com.okta.sdk.resource.user.factor.UserFactorList` 

The `expirePassword` Return type changed from `com.okta.sdk.resource.user.TempPassword` to `com.okta.sdk.resource.user.User`

The reset password method will no longer need a provider argument.   
   - `resetPassword(String provider, Boolean sendEmail)` Signature changed to `resetPassword(Boolean sendEmail)`

### Package `com.okta.sdk.resource.user.UserCredentials`

The property `emails` was not used by the backend and was always `null` hitherto. 
   - Removed `getEmails` & `setEmails` (property `emails` was removed)

Below SDK classes/interfaces are **deprecated** and will be removed from this project.

These SDK classes were previously moved to [okta-commons-java](https://github.com/okta/okta-commons-java)).

```
- com.okta.sdk.authc.credentials.ClientCredentialsProvider
- com.okta.sdk.client.Proxy
- com.okta.sdk.http.HttpMethod
- com.okta.sdk.http.HttpRequest
- com.okta.sdk.http.HttpRequestBuilder
- com.okta.sdk.http.HttpRequests
- com.okta.sdk.http.UserAgentProvider
- com.okta.sdk.lang.Assert
- com.okta.sdk.lang.Classes
- com.okta.sdk.lang.Collections
- com.okta.sdk.lang.Duration
- com.okta.sdk.lang.InstantiationException
- com.okta.sdk.lang.Instants
- com.okta.sdk.lang.Locales
- com.okta.sdk.lang.Objects
- com.okta.sdk.lang.Strings
- com.okta.sdk.lang.UnknownClassException
```
