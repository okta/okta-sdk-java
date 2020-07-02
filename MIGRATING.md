# Okta Java Management SDK Migration Guide
 
This SDK uses semantic versioning and follows Okta's [library version policy](https://developer.okta.com/code/library-versions/). In short, we don't make breaking changes unless the major version changes!

## Migrating from 1.x.x to 2.0.0

Version 2.0.0 of this SDK introduces a number of breaking changes from previous versions. 
In addition to many new classes/interfaces, some existing class/interfaces are no longer backward compatible due to method renaming and signature changes, see [Breaking Changes](#breaking-changes).

### Breaking Changes

The following is a list of changes that break backward compatibility in version 2.0.0.

- `com.okta.sdk.client.Client`
   - Renamed `createRule` to `createGroupRule`
   - Renamed `getRule` to `getGroupRule`
   - Renamed `listRules` to `listGroupRules`
   
- `com.okta.sdk.resource.log.LogEventList` 
   - `getLogs(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)`
   Signature changed to `getLogs(java.util.Date, java.util.Date, java.lang.String, java.lang.String, java.lang.String)`
   
- `com.okta.sdk.resource.group.GroupList` 
   - `listGroups(java.lang.String,java.lang.String, java.lang.String)`
   Signature changed to `listGroups(java.lang.String, java.lang.String)`

- `com.okta.sdk.resource.application.JsonWebKey`
   - `getExpiresAt` Return type changed from `Date` to `String`

- `com.okta.sdk.resource.application.SwaThreeFieldApplicationSettingsApplication`
   - `getTargetUrl` Renamed to `getTargetURL`
   - `setTargetUrl` Renamed to `setTargetURL`

- `com.okta.sdk.resource.group.Group`
   -  `getType` Return type changed from `String` to `com.okta.sdk.resource.group.GroupType`

- `com.okta.sdk.resource.group.rule.GroupRule`
   -  Removed `delete` 
   -  Removed `getAllGroupsValid` 
   -  Removed `getEmbedded` 
   -  Removed `setAllGroupsValid`

- `com.okta.sdk.resource.log.LogAuthenticationContext`
   -  `getCredentialProvider` Return type changed from `List` to `com.okta.sdk.resource.log.LogCredentialProvider`
   -  `getCredentialType` Return type changed from `List` to `com.okta.sdk.resource.log.LogCredentialType`

- `com.okta.sdk.resource.policy.Policy`
   -  Removed `createRule`

- `com.okta.sdk.resource.policy.PolicyRule`
   - Removed `setId`

- `com.okta.sdk.resource.user.factor.VerifyFactorRequest`
   - Removed `getTokenLifetimeSeconds` 
   - Removed `setTokenLifetimeSeconds`

- `com.okta.sdk.resource.user.Role`
   - `getType` Return type changed from `String` to `com.okta.sdk.resource.role.RoleType`
   - `setType` Param type changed from `String` to `com.okta.sdk.resource.role.RoleType`

- `com.okta.sdk.resource.user.User`
   - Removed `addFactor`
   - Renamed `addGroupTarget` to `addGroupTargetToRole`
   - Renamed `addRole` to `assignRole`
   - Removed `endAllSessions`
   - `expirePassword` Return type changed from `com.okta.sdk.resource.user.TempPassword` to `com.okta.sdk.resource.user.User`
   - Removed `forgotPassword`
   - `getFactor` Return type changed from `com.okta.sdk.resource.user.factor.Factor` to `com.okta.sdk.resource.user.factor.UserFactor`
   - Removed `listAppLinks`
   - `listFactors` Return type changed from `com.okta.sdk.resource.user.factor.FactorList` to `com.okta.sdk.resource.user.factor.UserFactorList` 
   - Renamed `listGroupTargetsForRole` to `listGroupTargets`
   - Removed `listRoles`
   - `listSupportedFactors` Return type changed from `com.okta.sdk.resource.user.factor.FactorList` to `com.okta.sdk.resource.user.factor.UserFactorList`
   - Renamed `removeGroupTargetFromRole` to `removeGroupTarget`
   - `resetPassword(java.lang.String, java.lang.Boolean)` Signature changed to `resetPassword(java.lang.Boolean)`

`com.okta.sdk.resource.user.UserCredentials`
-  removed getEmails() & setEmails(java.util.List)
