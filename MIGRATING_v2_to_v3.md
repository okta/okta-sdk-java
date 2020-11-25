# Okta Java Management SDK Migration Guide
 
This SDK uses semantic versioning and follows Okta's 
[library version policy](https://developer.okta.com/code/library-versions/). 
In short, we do not make breaking changes unless the major version changes!

## Migrating from 2.x.x to 3.0.0

Version 3.0.0 of this SDK introduces a number of breaking changes from previous versions. 
In addition to many new classes/interfaces, some existing classes/interfaces are no longer backward compatible due to method renaming and signature changes.

### Package `com.okta.sdk.client.Client`

Below method has been added.
- `void deleteIdentityProviderKey(String keyId)`
   
Below methods have undergone a signature change.
- `listGroups(String q, String filter)` signature changed to `listGroups(String q, String filter, String expand)`
    - New param `expand` has been added. It's optional and would need to be specified only while using search queries. 

- `listPolicies(String s)` return type changed from `com.okta.sdk.resource.policy.PolicyList` to `com.okta.sdk.resource.authorization.server.AuthorizationServerPolicyList`

### Package `com.okta.sdk.resource.application.AcsEndpoint`

This is a newly created interface with methods listed below.
- `Integer getIndex()`
- `AcsEndpoint setIndex(Integer index)`
- `String getUrl()`
- `AcsEndpoint setUrl(String url)`

### Package `com.okta.sdk.resource.application.Application` 

Below method has been added.
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

Enum `OpenIdConnectRefreshTokenRotationType` has 2 fields listed below.
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

Enum `StatusEnum` has 2 fields listed below.
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

Enum `StatusEnum` has 2 fields listed below.
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

Interface has been replaced by `com.okta.sdk.resource.authorization.server.AuthorizationServerPolicyList`

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
   - The property's `expiresAt` type contains a date-time string, so it's convenient to use the `Date` type

Below method has been removed.
- `setExpiresAt(String expiresAt)`
   
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
