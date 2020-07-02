# Okta Java Management SDK Migration Guide
 
This SDK uses semantic versioning and follows Okta's [library version policy](https://developer.okta.com/code/library-versions/). In short, we don't make breaking changes unless the major version changes!

## Migrating from 1.x.x to 2.0.0

Version 2.0.0 of this SDK introduces a number of breaking changes from previous versions. 
In addition to many new classes/interfaces, some existing class/interfaces are no longer backward compatible due to method renaming and signature changes, see [Breaking Changes](#breaking-changes).

### Breaking Changes

Following is a list of changes that break backward compatibility in version 2.0.0.

- `com.okta.sdk.client.Client`
   - Renamed `createRule` to `createGroupRule`
   - Renamed `getRule` to `getGroupRule`
   - Renamed `listRules` to `listGroupRules`
   
- `com.okta.sdk.resource.log.LogEventList` 
   - `getLogs(String until, String since, String filter, String q, String sortOrder)`
   Signature changed to `getLogs(Date since, Date until, String filter, String q, String sortOrder)`
   
- `com.okta.sdk.resource.group.GroupList` 
   - `listGroups(String q, String filter, String expand)`
   Signature changed to `listGroups(String q, String filter)`

- `com.okta.sdk.resource.application.SwaThreeFieldApplicationSettingsApplication`
   - `getTargetUrl` Renamed to `getTargetURL`
   - `setTargetUrl` Renamed to `setTargetURL`

- `com.okta.sdk.resource.group.Group`
   - `getType` Return type changed from `String` to `com.okta.sdk.resource.group.GroupType`
   
- `com.okta.sdk.resource.group.rule.GroupRule`
   - `delete(Boolean removeUsers)` Signature changed to `delete()`
   - Removed `getAllGroupsValid` and `setAllGroupsValid` (property `allGroupsValid` is removed)
   - Removed `getEmbedded` (property `_embedded` is removed)

- `com.okta.sdk.resource.log.LogAuthenticationContext`
   - `getCredentialProvider` Return type changed from `List` to `com.okta.sdk.resource.log.LogCredentialProvider`
   - `getCredentialType` Return type changed from `List` to `com.okta.sdk.resource.log.LogCredentialType`

- `com.okta.sdk.resource.policy.PolicyRule`
   - Removed `setId` (property `id` is read-only)

- `com.okta.sdk.resource.user.factor.VerifyFactorRequest`
   - Removed `getTokenLifetimeSeconds` & `setTokenLifetimeSeconds` (property `tokenLifetimeSeconds` is removed)

- `com.okta.sdk.resource.user.Role`
   - `getType` Return type changed from `String` to `com.okta.sdk.resource.role.RoleType`
   - `setType` Param type changed from `String` to `com.okta.sdk.resource.role.RoleType`

- All `*Factor` classes Renamed to `*UserFactor`

- `com.okta.sdk.resource.user.User`
   - Renamed `addGroupTarget` to `addGroupTargetToRole`
   - Renamed `addRole` to `assignRole`
   - Renamed `listGroupTargetsForRole` to `listGroupTargets`
   - Renamed `removeGroupTargetFromRole` to `removeGroupTarget`
   - Renamed `addFactor` to `enrollFactor`
   - Renamed `listRoles`to `listAssignedRoles`
   - Removed `forgotPassword` (use `resetPassword` instead)
   - Removed `endAllSessions` (use `clearSessions` instead)
   - `listSupportedFactors` Return type changed from `com.okta.sdk.resource.user.factor.FactorList` to `com.okta.sdk.resource.user.factor.UserFactorList`
   - `expirePassword` Return type changed from `com.okta.sdk.resource.user.TempPassword` to `com.okta.sdk.resource.user.User`
   - `getFactor` Return type changed from `com.okta.sdk.resource.user.factor.Factor` to `com.okta.sdk.resource.user.factor.UserFactor`
   - `listFactors` Return type changed from `com.okta.sdk.resource.user.factor.FactorList` to `com.okta.sdk.resource.user.factor.UserFactorList` 
   - `resetPassword(String provider, Boolean sendEmail)` Signature changed to `resetPassword(Boolean sendEmail)`

- `com.okta.sdk.resource.user.UserCredentials`
   - Removed `getEmails` & `setEmails` () (property `emails` is removed)
