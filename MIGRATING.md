# Okta Java Management SDK Migration Guide
 
This SDK uses semantic versioning and follows Okta's [library version policy](https://developer.okta.com/code/library-versions/). In short, we do not make breaking changes unless the major version changes!

## Migrating from 2.x.x to 3.0.0

Version 3.0.0 of this SDK introduces a number of breaking changes from previous versions. 
In addition to many new classes/interfaces, some existing classes/interfaces are no longer backward compatible due to method renaming and signature changes.

[Custom HOTP Factor](https://developer.okta.com/docs/reference/api/factors/#enroll-custom-hotp-factor) is supported now.
Define a separate Policy model for Authorization Servers.
Define a separate Policy Rule model for Authorization Servers.
[SAML Multiple ACS URLs](https://developer.okta.com/docs/reference/api/apps/#acs-endpoint-object) is supported now.

### Package `com.okta.sdk.client.Client`

Below method has been added.
- `void deleteIdentityProviderKey(String keyId)`
   
Below methods have undergone a signature change.
- `listGroups(String groupName, String filter)` signature changed to `listGroups(String groupName, String filter, String expand)`
    - New param `expand` has been added. It is `optional` and would need to be specified only while using search queries. 

- `listPolicies(String type)` return type changed from `com.okta.sdk.resource.policy.PolicyList` to `com.okta.sdk.resource.authorization.server.AuthorizationServerPolicyList`

### Package `com.okta.sdk.impl.client.DefaultClientBuilder`

The method `ClientBuilder setPrivateKey(String privateKey)` has been overloaded.
- The method `ClientBuilder setPrivateKey(String privateKey)` could be used to set a path to private key PEM file.
- Also, the method `ClientBuilder setPrivateKey(String privateKey)` could be used to set full PEM payload.
- The method `ClientBuilder setPrivateKey(Path privateKeyPath)` allows setting a path to private key PEM file.
- The method `ClientBuilder setPrivateKey(InputStream privateKeyStream)` allows setting an InputStream with private key PEM file content.
- The method `ClientBuilder setPrivateKey(PrivateKey privateKey)` allows setting the privateKey instance.

### Package `com.okta.sdk.resource.application.AcsEndpoint`

This is a newly created interface with methods listed below.
- `Integer getIndex()`
- `AcsEndpoint setIndex(Integer index)`
- `String getUrl()`
- `AcsEndpoint setUrl(String url)`

### Package `com.okta.sdk.resource.application.Application` 

- Added `ApplicationGroupAssignment createApplicationGroupAssignment(String groupId)` method.
    - This method allows assigning a group to an Application.

### Package `com.okta.sdk.resource.application.OpenIdConnectApplicationSettingsClient`

Below methods have been added.
- `OpenIdConnectApplicationSettingsRefreshToken getRefreshToken()`
- `OpenIdConnectApplicationSettingsRefreshToken setRefreshToken(OpenIdConnectApplicationSettingsRefreshToken refreshToken)`
- `OpenIdConnectApplicationSettingsClientKeys getJwks()`
- `OpenIdConnectApplicationSettingsClient setJwks(OpenIdConnectApplicationSettingsClientKeys jwks)`
   
### Package `com.okta.sdk.resource.application.OpenIdConnectApplicationSettingsClientKeys`

This is a newly created interface with methods listed below.
- `JsonWebKeyList getKeys()`
- `OpenIdConnectApplicationSettingsClientKeys setKeys(JsonWebKeyList keys)`

### Package `com.okta.sdk.resource.application.OpenIdConnectApplicationSettingsRefreshToken`

This is a newly created interface with methods listed below.
- `Integer getLeeway()`
- `OpenIdConnectRefreshTokenRotationType getRotationType()`
- `OpenIdConnectApplicationSettingsRefreshToken setLeeway(Integer leeway)`
- `OpenIdConnectApplicationSettingsRefreshToken setRotationType(OpenIdConnectRefreshTokenRotationType rotationType)`

With the introduction of enum type `OpenIdConnectRefreshTokenRotationType`, 
operations `getRotationType()` and `setRotationType(OpenIdConnectRefreshTokenRotationType rotationType)` will use this new enum type.

Enum `OpenIdConnectRefreshTokenRotationType` has the below fields defined:
- `ROTATE("rotate")`
- `STATIC("static")`
   
### Package `com.okta.sdk.resource.application.SamlApplicationSettingsSignOn`

Below methods have been added.
- `List getAcsEndpoints()`
- `Boolean getAllowMultipleAcsEndpoints()`
- `SamlApplicationSettingsSignOn setAcsEndpoints(List<AcsEndpoint> acsEndpoints)`
- `SamlApplicationSettingsSignOn setAllowMultipleAcsEndpoints(Boolean allowMultipleAcsEndpoints)`
   
### Package `com.okta.sdk.resource.application.SamlApplicationV1`

This is a newly created interface with methods listed below.
- `SamlApplicationSettings getSettings()`
- `SamlApplicationV1 setSettings(SamlApplicationSettings settings)`
   
### Package `com.okta.sdk.resource.application.SamlAttributeStatement` 
  
Below methods have been added.
- `String getFilterType()`
- `String getFilterValue()`
- `SamlAttributeStatement setFilterType(String filterType)`
- `SamlAttributeStatement setFilterValue(String filterValue)`

### Package `com.okta.sdk.resource.authorization.server.AuthorizationServer` 
  
Below methods have undergone a signature change.
- `createPolicy(com.okta.sdk.resource.policy.Policy policy)` signature changed to `createPolicy(com.okta.sdk.resource.authorization.server.AuthorizationServerPolicy policy)`, return value type changed from `com.okta.sdk.resource.policy.Policy` to `com.okta.sdk.resource.authorization.server.AuthorizationServerPolicy` 
- `getPolicy(String policyId)` return type changed from `com.okta.sdk.resource.policy.Policy` to `com.okta.sdk.resource.authorization.server.AuthorizationServerPolicy`
- `listPolicies()` return type changed from `com.okta.sdk.resource.policy.PolicyList` to `com.okta.sdk.resource.authorization.server.AuthorizationServerPolicyList`
- `updatePolicy(String policyId, com.okta.sdk.resource.policy.Policy policy)` signature changed to `updatePolicy(String policyId, com.okta.sdk.resource.authorization.server.AuthorizationServerPolicy policy)`, return value type changed from `com.okta.sdk.resource.policy.Policy` to `com.okta.sdk.resource.authorization.server.AuthorizationServerPolicy`

### Package `com.okta.sdk.resource.authorization.server.AuthorizationServerPolicy`

This is a newly created interface with methods listed below.
- `AuthorizationServerPolicyRule createPolicyRule(String authServerId, AuthorizationServerPolicyRule policyRule)`
- `void deletePolicy(String policyId)`
- `PolicyRuleConditions getConditions()`
- `Date getCreated()`
- `String getDescription()`
- `Map<String, Object> getEmbedded()`
- `String getId()`
- `Date getLastUpdated()`
- `Map<String, Object> getLinks()`
- `String getName()`
- `AuthorizationServerPolicyRule getPolicyRule(String authServerId, String ruleId)`
- `Integer getPriority()`
- `StatusEnum getStatus()`
- `Boolean getSystem()`
- `PolicyType getType()`
- `AuthorizationServerPolicyRuleList listPolicyRules(String authServerId)`
- `AuthorizationServerPolicy setConditions(PolicyRuleConditions conditions)`
- `AuthorizationServerPolicy setDescription(String description)`
- `AuthorizationServerPolicy setName(String name)`
- `AuthorizationServerPolicy setPriority(Integer priority)`
- `AuthorizationServerPolicy setStatus(StatusEnum status)`
- `AuthorizationServerPolicy setSystem(Boolean system)`
- `AuthorizationServerPolicy setType(PolicyType type)`
- `AuthorizationServerPolicy updatePolicy(String policyId, AuthorizationServerPolicy policy)`

With the introduction of enum type `AuthorizationServerPolicy$StatusEnum`, 
operations `getStatus()` and `setStatus(StatusEnum status)` will use this new enum type.

Enum `StatusEnum` has below fields defined:
- `ACTIVE("ACTIVE")`
- `INACTIVE("INACTIVE")`

### Package `com.okta.sdk.resource.authorization.server.policy.AuthorizationServerPolicyRule`

This is a newly created interface with methods listed below.
- `void activate()`
- `void deactivate()`
- `deletePolicyRule(String authServerId)`
- `AuthorizationServerPolicyRuleActions getActions()`
- `AuthorizationServerPolicyRuleConditions getConditions()`
- `Date getCreated()`
- `String getId()`
- `Date getLastUpdated()`
- `String getName()`
- `Integer getPriority()`
- `StatusEnum getStatus()`
- `Boolean getSystem()`
- `TypeEnum getType()`
- `AuthorizationServerPolicyRule setActions(AuthorizationServerPolicyRuleActions actions)`
- `AuthorizationServerPolicyRule setConditions(AuthorizationServerPolicyRuleConditions conditions)`
- `AuthorizationServerPolicyRule setName(String name)`
- `AuthorizationServerPolicyRule setPriority(Integer priority)`
- `AuthorizationServerPolicyRule setStatus(StatusEnum status)`
- `AuthorizationServerPolicyRule setSystem(Boolean system)`
- `AuthorizationServerPolicyRule setType(TypeEnum type)`
- `AuthorizationServerPolicyRule update(String authServerId)`

With the introduction of enum type `AuthorizationServerPolicyRule$StatusEnum`, operations `getStatus()` and `setStatus(StatusEnum status)` 
will use this new enum type.

Enum `StatusEnum` has below fields defined:
- `ACTIVE("ACTIVE")`
- `INACTIVE("INACTIVE")`

### Package `com.okta.sdk.resource.authorization.server.policy.AuthorizationServerPolicyRuleActions`

This is a newly created interface with methods listed below.
- `TokenAuthorizationServerPolicyRuleAction getToken()`
- `AuthorizationServerPolicyRuleActions setToken(TokenAuthorizationServerPolicyRuleAction token)`

### Package `com.okta.sdk.resource.authorization.server.policy.AuthorizationServerPolicyRuleConditions`

This is a newly created interface with methods listed below.
- `ClientPolicyCondition getClients()`
- `GrantTypePolicyRuleCondition getGrantTypes()`
- `PolicyPeopleCondition getPeople()`
- `OAuth2ScopesMediationPolicyRuleCondition getScopes()`
- `AuthorizationServerPolicyRuleConditions setClients(ClientPolicyCondition clients)`
- `AuthorizationServerPolicyRuleConditions setGrantTypes(GrantTypePolicyRuleCondition grantTypes)`
- `AuthorizationServerPolicyRuleConditions setPeople(PolicyPeopleCondition people)`
- `AuthorizationServerPolicyRuleConditions setScopes(OAuth2ScopesMediationPolicyRuleCondition scopes)`
   
### Package `com.okta.sdk.resource.authorization.server.policy.TokenAuthorizationServerPolicyRuleAction`

This is a newly created interface with methods listed below.
- `Integer getAccessTokenLifetimeMinutes()`
- `Integer getRefreshTokenLifetimeMinutes()`
- `Integer getRefreshTokenWindowMinutes()`
- `TokenAuthorizationServerPolicyRuleAction setAccessTokenLifetimeMinutes(Integer accessTokenLifetimeMinutes)`
- `TokenAuthorizationServerPolicyRuleAction setRefreshTokenLifetimeMinutes(Integer refreshTokenLifetimeMinutes)`
- `TokenAuthorizationServerPolicyRuleAction setRefreshTokenWindowMinutes(Integer refreshTokenWindowMinutes)`

### Package `com.okta.sdk.resource.identity.provider.IdentityProvider`

Below method has been removed.
- `deleteSigningKey(String keyId)`

### Package `com.okta.sdk.resource.policy.PolicyList` 

The Interface has been removed.
The Interface `com.okta.sdk.resource.authorization.server.AuthorizationServerPolicyList` should be used instead.

### Package `com.okta.sdk.resource.user.factor.CustomHotpUserFactor`

This is a newly created interface with methods listed below.
- `String getFactorProfileId()`
- `CustomHotpUserFactorProfile getProfile()`
- `CustomHotpUserFactor setFactorProfileId(String factorProfileId)`
- `CustomHotpUserFactor setProfile(CustomHotpUserFactorProfile profile)`

### Package `com.okta.sdk.resource.user.factor.CustomHotpUserFactorProfile`

This is a newly created interface with methods listed below.
- `String getSharedSecret()`
- `CustomHotpUserFactorProfile setSharedSecret(String sharedSecret)`

### Package `com.okta.sdk.resource.user.factor.VerifyUserFactorResponse`

Below method has undergone a signature change.
- `getExpiresAt()` return type changed from `String` to `Date`
   - The property's `expiresAt` type is a date-time string, so it becomes convenient to use `Date` type
   
### Package `com.okta.sdk.resource.user.PasswordCredentialHash`

Below methods have been renamed for the sake of clarity.
- Renamed `getWorkerFactor()` to `getWorkFactor()`
- Renamed `setWorkerFactor(Integer workFactor)` to `setWorkFactor(Integer workFactor)`

### Package `com.okta.sdk.resource.user.UserBuilder`

Below methods have been added.
- `UserBuilder setType(UserType userType)`
- `UserBuilder setType(String userTypeId)`
- `UserBuilder usePasswordHookForImport()`
- `UserBuilder usePasswordHookForImport(String type)`


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

Below API has undergone a signature change.
   - `listUsers(String q, String filter, String format, String search, String expand)`
   Signature changed to `listUsers(java.lang.String q, java.lang.String filter, java.lang.String search, java.lang.String sortBy, java.lang.String sortOrder)`

Note that the params `format` and `expand` have been removed. New params `sortBy` and `sortOrder` have been added. These are optional and would need to be specified only while using search queries. 

### Package `com.okta.sdk.resource.log.LogEventList`

The API used to get log info has undergone a signature change (note the order swap of `until` and `since` parameters).
   - `getLogs(String until, String since, String filter, String q, String sortOrder)`
   Signature changed to `getLogs(Date since, Date until, String filter, String q, String sortOrder)`
   
### Package `com.okta.sdk.resource.group.GroupList`

The API used to list the groups to which a user belongs, has undergone a signature change. 
There is no need for supplying the `expand` parameter anymore. 
   - `listGroups(String q, String filter, String expand)`
   Signature changed to `listGroups(String q, String filter)`
   
       - With this removal of `expand` parameter, the caller has two options of achieving the previous result:
       - Make a second API call to the Group API and fetch the results.
       - You can call the Groups API endpoint (or any Okta management API endpoint) using a syntax like below example:
       ```
       // List Groups API, see: https://developer.okta.com/docs/reference/api/groups/#list-groups
       GroupsList result = client.http()
         .addQueryParameter("expand", true)
         .get("/api/v1/groups", GroupsList.class);
       ```
- Note that the support for `expand` parameter might go away anytime in the future.

### Package `com.okta.sdk.resource.application.SwaThreeFieldApplicationSettingsApplication`

Below APIs have undergone a name change.
   - `getTargetUrl` Renamed to `getTargetURL`
   - `setTargetUrl` Renamed to `setTargetURL`

### Package `com.okta.sdk.resource.group.Group`

With the introduction of enum type `com.okta.sdk.resource.group.GroupType`, the get group type operation will now make 
use of this new enum type instead of the `String` type used earlier.
   - `getType` Return type changed from `String` to an enum `com.okta.sdk.resource.group.GroupType`
   
### Package `com.okta.sdk.resource.group.rule.GroupRule`

The properties `allGroupsValid` & `_embedded` were not used by the backend earlier and were always set to `null`.
As part of this upgrade, we will remove it from the method signatures.
   - Removed `getAllGroupsValid` and `setAllGroupsValid` (property `allGroupsValid` is being removed; 
   this option would be removed from future versions of Okta API)
   - Removed `getEmbedded` (property `_embedded` is removed; this property is undocumented in Okta API and hence being removed)
   - delete(Boolean removeUsers) Signature changed to delete(); optional parameter `removeUsers` which defaulted to `false` earlier is now removed. 

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
use of this new enum type instead of the `String` type used earlier.
   - `getType` Return type changed from `String` to an enum `com.okta.sdk.resource.role.RoleType`
   - `setType` Param type changed from `String` to an enum `com.okta.sdk.resource.role.RoleType`

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
