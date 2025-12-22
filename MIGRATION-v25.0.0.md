# Okta Java Management SDK Migration Guide# Okta Java Management SDK Migration Guide

 

This SDK uses semantic versioning and follows Okta's [library version policy](https://developer.okta.com/code/library-versions/). In short, we do not make breaking changes unless the major version changes!This SDK uses semantic versioning and follows Okta's [library version policy](https://developer.okta.com/code/library-versions/). In short, we do not make breaking changes unless the major version changes!

#todo delete

## Migrating from 24.x.x to 25.0.0## Migrating from 8.x.x to 10.x.x



Version 25.0.0 of this SDK introduces significant breaking changes based on the updated Okta Admin Management API (OpenAPI spec v5.1.0). This is a major release with extensive schema changes that require careful review.In previous versions we use an Open API v2 specification for the management APIs, and an Okta custom client generator to generate the SDK components. 

A new version of the Open API specification v3 has been released, and new well-known open source generators are now available to automatically generate code from this specification.

### What's Changed

This revision will embrace the Open Source [openapi-generator](https://github.com/OpenAPITools/openapi-generator) to auto generate the code from Okta's reference specification (v3) of the Management APIs.

| Aspect | v24.x | v25.0.0 |

|--------|-------|---------|`ExtensibleResource` is removed as the individual models no longer extend from this class. This is also partly due to the way the code generator tooling works when it generates the model classes. Client can directly serialize/deserialize API responses into the respective model classes. There should ideally be no need

| OpenAPI Spec Version | 2024.08.3 | 5.1.0 |for a workaround or replacement of `ExtensibleResource`. 

| API YAML Lines | 66,987 | 83,953 |

| Integration Tests | Limited | 35 comprehensive suites |### Okta client vs API clients

| Custom Deserializers | 3 | 9 |

| New Endpoints | - | 70+ |In older version, you would instantiate a global `Client` and access the Okta resources using the Management APIs. 

Now, each API area (such as Users, Groups, Applications etc.)  would have its own specific client, so you will only instantiate those clients you are interested in:

### Quick Start Checklist

Note that the below `okta-sdk-httpclient` dependency we used in previous versions is no longer needed:

Use this checklist to ensure a smooth migration:

```xml

- [ ] Update SDK dependency to v25.0.0<dependency>

- [ ] Search codebase for `user.getType()` or `user.type` references   <groupId>com.okta.sdk</groupId>

- [ ] Review Authenticator API usage   <artifactId>okta-sdk-httpclient</artifactId>

- [ ] Review User Factor API usage   <scope>runtime</scope>

- [ ] Review Policy API usage</dependency>

- [ ] Review Identity Provider API usage```

- [ ] Review Role Assignment API usage

- [ ] Replace `PaginationUtil.getAfter()` with `PagedIterable`_Earlier:_

- [ ] Run tests and verify functionality

```java

---Client client = Clients.builder()

    .setOrgUrl("https://{yourOktaDomain}")  // e.g. https://dev-123456.okta.com

## 🚨 Breaking Changes by Category    .setClientCredentials(new TokenClientCredentials("{apiToken}"))

    .build();

This section organizes all breaking changes by functional area to help you identify which parts of your codebase need updates.

User user = client.getUser("a-user-id");

### 1. Identity & Access Management (IAM)

Application app = client.getApplication("appId");

**Impact Level**: 🔴 High```



| Endpoint | Change |_Now:_

|----------|--------|

| `GET /api/v1/iam/assignees/users` | Changed property type for `_links.roles` |```java

| `GET /api/v1/iam/resource-sets` | Changed property type for `_links.resources` |ApiClient client = Clients.builder()

| `POST /api/v1/iam/resource-sets` | Changed property type for `_links.resources` |    .setOrgUrl("https://{yourOktaDomain}")  // e.g. https://dev-123456.okta.com

| `PUT /api/v1/iam/resource-sets/{id}` | Changed property type for `_links.resources` |    .setClientCredentials(new TokenClientCredentials("{apiToken}"))

| `PATCH /api/v1/iam/resource-sets/{id}` | Changed property type for `_links.resources` |    .build();

| `GET /api/v1/iam/resource-sets/{id}/bindings` | Missing `_links.bindings` property |

| `POST /api/v1/iam/resource-sets/{id}/bindings` | Missing `id` property in response |UserApi userApi = new UserApi(client);

| `GET /api/v1/iam/resource-sets/{id}/bindings/{roleId}` | Missing `_links.bindings` property |User user = userApi.getUser("userId");

| `PATCH /api/v1/iam/resource-sets/{id}/bindings/{roleId}/members` | Missing `id` property |

| `POST /api/v1/iam/roles` | Schema change |ApplicationApi applicationApi = new ApplicationApi(client);

| `GET /api/v1/iam/roles/{id}/permissions` | Changed property type for `_links.role` |Application app = applicationApi.getApplication("appId", null);

| `PUT /api/v1/iam/roles/{id}/permissions/{permissionType}` | Changed property type for `_links.role` |```



**Migration Example:**## Migrating from 7.x.x to 8.0.0



```javaVersion 8.0.0 of this SDK introduces few breaking changes from previous versions.

// Before (v24.x)

ResourceSetApi resourceSetApi = new ResourceSetApi(client);### Package `com.okta.sdk.resource.brand.Theme`

ResourceSet resourceSet = resourceSetApi.getResourceSet(resourceSetId);

// Accessing _links.resources as previous typeBelow methods will now take `file` object as additional param. This was missing from earlier release (bug) and this release fixes it.

- `updateBrandThemeBackgroundImage` 

// After (v25.0.0)- `updateBrandThemeFavicon`

ResourceSetApi resourceSetApi = new ResourceSetApi(client);- `uploadBrandThemeLogo`

ResourceSet resourceSet = resourceSetApi.getResourceSet(resourceSetId);

// _links.resources property type has changed - review new structure### Package `com.okta.sdk.resource.org.OrgSetting`

```- `updateOrgLogo` will now take `file` object as a parameter. This was missing from earlier release (bug) and this release fixes it. 



---## Migrating from 6.x.x to 7.0.0



### 2. User & Group Management### Package `com.okta.sdk.client.Client`



**Impact Level**: 🔴 High (Critical)Below methods have been added.

- `Brand getBrand(String brandId)`

#### User Object Schema Changes- `ThemeResponse getBrandTheme(String brandId, String themeId)`

- `BrandList listBrands()`

The `User` object no longer includes the `type` property and its sub-properties in API responses.- `ThemeResponseList listBrandThemes(String brandId)`



**Removed Properties from `user.type`:**Below methods have been moved to `com.okta.sdk.resource.org.OrgSetting`.

- `created`- `getOktaCommunicationSettings()`

- `createdBy`- `getOrgContactTypes()`

- `default`- `getOrgContactUser(String contactType)`

- `description`- `getOrgOktaSupportSettings()`

- `displayName`- `getOrgPreferences()`

- `lastUpdated`

- `name`### Package `com.okta.sdk.resource.user.User`

- `_links`

- `assignRole(AssignRoleRequest request, String disableNotifications)` changed signature to 

**Affected Endpoints:**  `assignRole(AssignRoleRequest request, Boolean disableNotifications)`



| Endpoint | Change |### Package `com.okta.sdk.resource.policy`

|----------|--------|

| `GET /api/v1/users` | Removed `type` metadata properties |New Interfaces have been introduced.

| `POST /api/v1/users` | Removed `type` metadata properties |- `com.okta.sdk.resource.policy.VerificationMethod`

| `GET /api/v1/users/{id}` | Removed `type` metadata properties |- `com.okta.sdk.resource.policy.UserTypeCondition`

| `PUT /api/v1/users/{id}` | Removed `type` metadata properties |- `com.okta.sdk.resource.policy.ProfileEnrollmentPolicyRuleProfileAttribute`

| `PATCH /api/v1/users/{id}` | Removed `type` metadata properties |- `com.okta.sdk.resource.policy.ProfileEnrollmentPolicyRuleActivationRequirement`

| `GET /api/v1/groups/{groupId}/users` | Removed all `type` metadata properties |- `com.okta.sdk.resource.policy.ProfileEnrollmentPolicyRuleActions`

| `GET /api/v1/devices` | Removed `type` metadata properties |- `com.okta.sdk.resource.policy.ProfileEnrollmentPolicyRuleAction`

| `GET /api/v1/devices/{deviceId}/users` | Removed `type` metadata properties |- `com.okta.sdk.resource.policy.ProfileEnrollmentPolicyRule`

- `com.okta.sdk.resource.policy.ProfileEnrollmentPolicy`

_Before (v24.x):_- `com.okta.sdk.resource.policy.PreRegistrationInlineHook`

- `com.okta.sdk.resource.policy.PossessionConstraint`

```java- `com.okta.sdk.resource.policy.KnowledgeConstraint`

User user = userApi.getUser(userId);- `com.okta.sdk.resource.policy.DeviceAccessPolicyRuleCondition`

UserType type = user.getType();- `com.okta.sdk.resource.policy.AccessPolicyRuleCustomCondition`

String typeId = type.getId();- `com.okta.sdk.resource.policy.AccessPolicyRuleConditions`

String typeName = type.getName();- `com.okta.sdk.resource.policy.AccessPolicyRuleApplicationSignOn`

Boolean isDefault = type.getDefault();- `com.okta.sdk.resource.policy.AccessPolicyRuleActions`

Date created = type.getCreated();- `com.okta.sdk.resource.policy.AccessPolicyRule`

String createdBy = type.getCreatedBy();- `com.okta.sdk.resource.policy.AccessPolicyConstraints`

```- `com.okta.sdk.resource.policy.AccessPolicyConstraint`

- `com.okta.sdk.resource.policy.AccessPolicy`

_After (v25.0.0):_

### Package `com.okta.sdk.resource.identity.provider.IdentityProviderBuilders`

```java

User user = userApi.getUser(userId);Identity Provider of type String can be constructed with the new method introduced.

// user.getType() may return null or different structure- `ofType(java.lang.String)`

// Use UserTypeApi to get type information separately

### Package `com.okta.sdk.resource.identity.provider.IdentityProvider`

UserTypeApi userTypeApi = new UserTypeApi(client);

List<UserType> types = userTypeApi.listUserTypes();- `setType(com.okta.sdk.resource.identity.provider.IdentityProvider$TypeEnum)` has changed signature to 

// Find user's type from the types list or user's _links  `setType(String identityProvider)`.

```- `getType()` would now return `String` instead of `com.okta.sdk.resource.identity.provider.IdentityProvider$TypeEnum` type.



#### User Factor Changes### Package `com.okta.sdk.resource.brand`



| Endpoint | Change |New models related to Theme and Brands API have been introduced.

|----------|--------|- `Theme`

| `GET /api/v1/users/{userId}/factors` | `_links.resend` changed from array to object |- `Brand`

| `POST /api/v1/users/{userId}/factors` | `_links.resend` changed from array to object |- `BrandList`

| `POST /api/v1/users/{userId}/factors/{factorId}/verify` | `_links.resend` changed from array to object |- `ThemeResponse`

- `ImageUploadResponse`

#### Schema Endpoints- `SignInPageTouchPointVariant`

- `ErrorPageTouchPointVariant`

| Endpoint | Change |- `EndUserDashboardTouchPointVariant`

|----------|--------|

| `GET /api/v1/meta/schemas/user/{schemaId}` | `enum` values changed from strings to objects; `unique` flags changed from strings to booleans |### Package `com.okta.sdk.resource.authenticator`

| `POST /api/v1/meta/schemas/user/{schemaId}` | Same as above |

| `GET /api/v1/meta/schemas/group/default` | `enum` values changed from integers to objects; `unique` flags changed from strings to booleans |New Interfaces have been introduced.

| `POST /api/v1/meta/schemas/group/default` | Same as above |- `ChannelBinding`

- `Compliance`

---- `AuthenticatorProviderConfigurationUserNamePlate`

- `AuthenticatorProviderConfiguration`

### 3. Application & Auth Server Credentials- `AuthenticatorProvider`



**Impact Level**: 🟡 MediumNew methods have been added to `Authenticator` interface.

- `AuthenticatorProvider getProvider()`

| Endpoint | Change |- `setProvider(AuthenticatorProvider authenticationProvider)`

|----------|--------|- `Authenticator update`

| `GET /api/v1/apps/{appId}/credentials/jwks` | Changed property types within JWKS object |

| `POST /api/v1/apps/{appId}/credentials/jwks` | Changed property types and return types |### Package `com.okta.sdk.resource.application`

| `PATCH /api/v1/apps/{appId}/credentials/jwks` | Changed property types |

| `POST /api/v1/apps` | Request schema broken compatibility |New methods have been added to `SwaApplicationSettingsApplication` interface.

| `PUT /api/v1/apps/{appId}` | Request schema broken compatibility |- `String getCheckbox`

| `POST /api/v1/authorizationServers/{id}/policies` | Request schema broken compatibility |- `String getRedirectUrl`

| `PUT /api/v1/authorizationServers/{id}/policies/{policyId}` | Request schema broken compatibility |- `SwaApplicationSettingsApplication setCheckbox(String checkBox)`

| `GET /oauth2/v1/clients/{id}/roles` | Changed property type from object to array |- `SwaApplicationSettingsApplication setRedirectUrl(String redirectUrl)`



_After (v25.0.0):_New method have been added to `OIDCApplicationBuilder` interface.

- `OIDCApplicationBuilder setPostLogoutRedirectUris(List uris)`

```java

ApplicationCredentialsApi credApi = new ApplicationCredentialsApi(client);New methods have been added to `ApplicationCredentialsUsernameTemplate` interface.

- `String getPushStatus()`

// List JWKs - now uses polymorphic response- `ApplicationCredentialsUsernameTemplate setPushStatus(String pushStatus)`

List<ListJwk200ResponseInner> jwks = credApi.listJwks(appId);

for (ListJwk200ResponseInner jwk : jwks) {## Migrating from 5.x.x to 6.0.0

    System.out.println("Key ID: " + jwk.getKid());

    System.out.println("Key Type: " + jwk.getKty());Version 6.0.0 of this SDK introduces a number of breaking changes from previous versions.

    System.out.println("Use: " + jwk.getUse()); // "sig" or "enc"In addition to many new classes/interfaces, some existing classes/interfaces are no longer backward compatible due to method renaming and signature changes.

    System.out.println("Status: " + jwk.getStatus());

}### Package `com.okta.sdk.client.Client`

```

Below methods have been added.

---- `Authenticator getAuthenticator(String authenticatorId)`

- `GroupSchema getGroupSchema()`

### 4. System & Security Settings- `OrgOktaCommunicationSetting getOktaCommunicationSettings()`

- `OrgContactTypeObjList getOrgContactTypes()`

**Impact Level**: 🟡 Medium- `OrgContactUser getOrgContactUser(String contactType)`

- `OrgOktaSupportSettingsObj getOrgOktaSupportSettings()`

| Endpoint | Change |- `OrgPreferences getOrgPreferences()`

|----------|--------|- `OrgSetting getOrgSettings()`

| `GET /.well-known/okta-organization` | Missing `settings` object |- `AuthenticatorList listAuthenticators()`

| `GET /api/v1/inlineHooks` | Removed `metadata` property |- `GroupSchema updateGroupSchema()`

| `POST /api/v1/inlineHooks` | Removed `metadata`, `status`, `_links` properties |- `GroupSchema updateGroupSchema(GroupSchema body)`

| `PUT /api/v1/inlineHooks/{id}` | Removed `metadata`, `status`, `type`, `_links` properties |

| `GET /api/v1/principal-rate-limits` | Return type schema broken compatibility |Below method has undergone a signature change.

| `POST /api/v1/principal-rate-limits` | Return type schema broken compatibility |- `DomainList listDomains()` signature changed to `DomainListResponse listDomains()`

| `PUT /api/v1/principal-rate-limits/{id}` | Return type schema broken compatibility |

| `GET /api/v1/policies` | Changed property type for `status` |### Package `com.okta.sdk.resource.application.ApplicationVisibility`

| `POST /api/v1/policies` | Changed property type for `status` |

| `PUT /api/v1/policies/{policyId}` | Changed property type for `status` |Below methods have been added.

| `GET /api/v1/behaviors` | Changed property types for `created` and `lastUpdated` timestamps |- `Boolean getAutoLaunch()`

| `POST /api/v1/behaviors` | Changed property types for `created` and `lastUpdated` timestamps |- `ApplicationVisibility setAutoLaunch(Boolean autoLaunch)`

| `PUT /api/v1/behaviors/{id}` | Changed property types for `created` and `lastUpdated` timestamps |

| `GET /api/v1/agentPools` | Changed `lastConnection` from string to integer |### Package `com.okta.sdk.resource.authenticator.AllowedForEnum`

| `POST /api/v1/agentPools` | Changed `lastConnection` from string to integer |

This is a newly created enum with fields listed below.

_Before (v24.x):_- `ANY("ANY")`

- `NONE("NONE")`

```java- `RECOVERY("RECOVERY")`

PolicyApi policyApi = new PolicyApi(client);- `SSO("SSO")`

Policy policy = policyApi.getPolicy(policyId, null);

String status = policy.getStatus(); // Was different type### Package `com.okta.sdk.resource.authenticator.Authenticator`

```

This is a newly created interface with methods listed below.

_After (v25.0.0):_

- `Authenticator activate()`

```java- `Authenticator deactivate()`

PolicyApi policyApi = new PolicyApi(client);- `Date getCreated()`

Policy policy = policyApi.getPolicy(policyId, null);- `String getId()`

PolicyStatus status = policy.getStatus(); // Now uses enum type- `String getKey()`

String statusValue = status.getValue();- `Date getLastUpdated()`

```- `Map<String, Object> getLinks()`

- `String getName()`

---- `AuthenticatorSettings getSettings()`

- `AuthenticatorStatus getStatus()`

### 5. Specialized Authenticators- `AuthenticatorType getType()`

- `Authenticator setKey(String key)`

**Impact Level**: 🟡 Medium- `Authenticator setName(String name)`

- `Authenticator setSettings(AuthenticatorSettings settings)`

| Endpoint | Change |- `Authenticator setStatus(AuthenticatorStatus status)`

|----------|--------|- `Authenticator setType(AuthenticatorType type)`

| `GET /api/v1/authenticators` | Broad schema broken compatibility |

| `POST /api/v1/authenticators` | Broad schema broken compatibility |### Package `com.okta.sdk.resource.authenticator.AuthenticatorSettings`

| `PUT /api/v1/authenticators/{id}` | Broad schema broken compatibility |

| `POST /api/v1/authenticators/{id}/lifecycle/activate` | Schema changes |This is a newly created interface with methods listed below.

| `POST /api/v1/authenticators/{id}/lifecycle/deactivate` | Schema changes |

| `GET /api/v1/authenticators/{id}/methods` | Schema changes |- `AllowedForEnum getAllowedFor()`

| `PUT /api/v1/authenticators/{id}/methods/{methodType}` | Schema changes |- `Integer getTokenLifetimeInMinutes()`

| `POST /webauthn-registration/.../initiate-fulfillment-request` | Changed `fulfillmentData` from object to array |- `AuthenticatorSettings setAllowedFor(AllowedForEnum allowedFor)`

| `GET /.well-known/app-authenticator-configuration` | Schema broken compatibility |- `AuthenticatorSettings setTokenLifetimeInMinutes(Integer tokenLifetimeInMinutes)`



---### Package `com.okta.sdk.resource.authenticator.AuthenticatorStatus`



### 6. Role Assignment ChangesThis is a newly created enum with fields listed below.

- `ACTIVE("ACTIVE")`

**Impact Level**: 🟡 Medium- `INACTIVE("INACTIVE")`



Role assignment responses now use polymorphic types handled by custom deserializers.### Package `com.okta.sdk.resource.authenticator.AuthenticatorType`



| Endpoint | Model Used |This is a newly created enum with fields listed below.

|----------|------------|- `APP("APP")`

| `GET /api/v1/groups/{groupId}/roles` | `ListGroupAssignedRoles200ResponseInner` |- `EMAIL("EMAIL")`

| `POST /api/v1/groups/{groupId}/roles` | `AssignRoleToGroup200Response` |- `PASSWORD("PASSWORD")`

| `GET /api/v1/users/{userId}/roles` | Uses polymorphic response |- `PHONE("PHONE")`

| `POST /api/v1/users/{userId}/roles` | `AssignRoleToUser201Response` |- `SECURITY_KEY("SECURITY_KEY")`

| `GET /oauth2/v1/clients/{clientId}/roles` | Uses polymorphic response |- `SECURITY_QUESTION("SECURITY_QUESTION")`

| `POST /oauth2/v1/clients/{clientId}/roles` | `AssignRoleToClient200Response` |

### Package `com.okta.sdk.resource.domain.DomainListResponse`

_After (v25.0.0):_

This is a newly created interface with methods listed below.

```java

// Works automatically with custom deserializers- `DomainList getDomains()`

RoleAssignmentApi roleApi = new RoleAssignmentApi(client);- `DomainListResponse setDomains(DomainList domains)`

List<ListGroupAssignedRoles200ResponseInner> roles = 

    roleApi.listGroupAssignedRoles(groupId, null);### Package `com.okta.sdk.resource.group.schema.GroupSchema`



for (ListGroupAssignedRoles200ResponseInner role : roles) {This is a newly created interface with methods listed below.

    System.out.println("Role Type: " + role.getType());

    System.out.println("Assignment Type: " + role.getAssignmentType());- `String getCreated()`

    System.out.println("Status: " + role.getStatus());- `GroupSchemaDefinitions getDefinitions()`

}- `String getDescription()`

```- `String getId()`

- `String getLastUpdated()`

---- `Map<String, Object> getLinks()`

- `String getName()`

## SDK-Specific Changes- `UserSchemaProperties getProperties()`

- `String getSchema()`

### Custom Deserializers for Polymorphic Types- `String getTitle()`

- `String getType()`

The SDK now includes custom Jackson deserializers to handle polymorphic API responses. These are automatically registered when using `DefaultClientBuilder`.- `GroupSchema setDefinitions(GroupSchemaDefinitions definitions)`

- `GroupSchema setDescription(String description)`

| Deserializer | Target Class | Purpose |- `GroupSchema setTitle(String title)`

|-------------|--------------|---------|

| `RoleAssignmentDeserializer` | `ListGroupAssignedRoles200ResponseInner` | StandardRole/CustomRole polymorphism |### Package `com.okta.sdk.resource.group.schema.GroupSchemaAttribute`

| `AssignRoleToGroupResponseDeserializer` | `AssignRoleToGroup200Response` | Group role assignments |

| `AssignRoleToUserResponseDeserializer` | `AssignRoleToUser201Response` | User role assignments |This is a newly created interface with methods listed below.

| `AssignRoleToClientResponseDeserializer` | `AssignRoleToClient200Response` | Client role assignments |

| `JwkResponseDeserializer` | `ListJwk200ResponseInner` | JWK signing/encryption keys |- `String getDescription()`

| `GroupOwnerDeserializer` | `GroupOwner` | Non-ISO date format handling |- `List<String> getEnum()`

| `FlexibleOffsetDateTimeDeserializer` | Various | Global flexible date parsing |- `String getExternalName()`

| `GroupProfileDeserializer` | `GroupProfile` | Group profile polymorphism |- `String getExternalNamespace()`

| `UserProfileDeserializer` | `UserProfile` | User profile polymorphism |- `UserSchemaAttributeItems getItems()`

- `UserSchemaAttributeMaster getMaster()`

### Custom Serializers- `Integer getMaxLength()`

- `Integer getMinLength()`

| Serializer | Target Class | Purpose |- `String getMutability()`

|-----------|--------------|---------|- `List<UserSchemaAttributeEnum> getOneOf()`

| `GroupProfileSerializer` | `GroupProfile` | Proper serialization of group profiles |- `List<UserSchemaAttributePermission> getPermissions()`

| `UserProfileSerializer` | `UserProfile` | Proper serialization of user profiles |- `Boolean getRequired()`

| `OktaUserGroupProfileSerializer` | `OktaUserGroupProfile` | Proper serialization of Okta user group profiles |- `UserSchemaAttributeScope getScope()`

- `String getTitle()`

---- `UserSchemaAttributeType getType()`

- `UserSchemaAttributeUnion getUnion()`

## Pagination Changes- `String getUnique()`

- `GroupSchemaAttribute setDescription(String description)`

### `PaginationUtil.getAfter()` is Deprecated- `GroupSchemaAttribute setEnum(List<String> _enum)`

- `GroupSchemaAttribute setExternalName(String externalName)`

The `PaginationUtil.getAfter()` method is deprecated and will be removed in v26.0.0. Use `PagedIterable` instead.- `GroupSchemaAttribute setExternalNamespace(String externalNamespace)`

- `GroupSchemaAttribute setItems(UserSchemaAttributeItems items)`

| Feature | Old (PaginationUtil) | New (PagedIterable) |- `GroupSchemaAttribute setMaster(UserSchemaAttributeMaster master)`

|---------|---------------------|---------------------|- `GroupSchemaAttribute setMaxLength(Integer maxLength)`

| Thread Safety | ❌ Shared state issues | ✅ Isolated iterator state |- `GroupSchemaAttribute setMinLength(Integer minLength)`

| Memory Efficiency | ❌ Manual collection building | ✅ Lazy loading |- `GroupSchemaAttribute setMutability(String mutability)`

| Code Simplicity | ❌ Manual loop with `do-while` | ✅ Simple `for-each` |- `GroupSchemaAttribute setOneOf(List<UserSchemaAttributeEnum> oneOf)`

- `GroupSchemaAttribute setPermissions(List<UserSchemaAttributePermission> permissions)`

_Before (v24.x) - DEPRECATED:_- `GroupSchemaAttribute setRequired(Boolean required)`

- `GroupSchemaAttribute setScope(UserSchemaAttributeScope scope)`

```java- `GroupSchemaAttribute setTitle(String title)`

// DEPRECATED - Will be removed in v26.0.0- `GroupSchemaAttribute setType(UserSchemaAttributeType type)`

UserApi userApi = new UserApi(client);- `GroupSchemaAttribute setUnion(UserSchemaAttributeUnion union)`

List<User> allUsers = new ArrayList<>();- `GroupSchemaAttribute setUnique(String unique)`

String after = null;

### Package `com.okta.sdk.resource.group.schema.GroupSchemaBase`

do {

    List<User> page = userApi.listUsers("application/json", null, after, 200, null, null, null, null);This is a newly created interface with methods listed below.

    allUsers.addAll(page);

    after = PaginationUtil.getAfter(userApi.getApiClient());- `String getId()`

} while (StringUtils.isNotBlank(after));- `GroupSchemaBaseProperties getProperties()`

```- `List<String> getRequired()`

- `String getType()`

_After (v25.0.0) - RECOMMENDED:_- `GroupSchemaBase setProperties(GroupSchemaBaseProperties properties)`

- `GroupSchemaBase setRequired(List<String> required)`

```java- `GroupSchemaBase setType(String type)`

UserApi userApi = new UserApi(client);

### Package `com.okta.sdk.resource.group.schema.GroupSchemaBaseProperties`

// Option 1: Iterate directly (lazy loading, memory efficient)

PagedIterable<User> users = userApi.listUsersPagedIterable(null, null, 200, null, null, null, null);This is a newly created interface with methods listed below.

for (User user : users) {

    System.out.println("User: " + user.getProfile().getLogin());- `GroupSchemaAttribute getDescription()`

}- `GroupSchemaAttribute getName()`

- `GroupSchemaBaseProperties setDescription(GroupSchemaAttribute description)`

// Option 2: Collect all to a list- `GroupSchemaBaseProperties setName(GroupSchemaAttribute name)`

List<User> allUsers = new ArrayList<>();

for (User user : userApi.listUsersPagedIterable(null, null, 200, null, null, null, null)) {### Package `com.okta.sdk.resource.group.schema.GroupSchemaCustom`

    allUsers.add(user);

}This is a newly created interface with methods listed below.



// Option 3: Using Java Streams- `String getId()`

List<User> filteredUsers = StreamSupport.stream(users.spliterator(), false)- `Map<String, GroupSchemaAttribute> getProperties()`

    .filter(user -> user.getProfile().getEmail().endsWith("@example.com"))- `List<String> getRequired()`

    .collect(Collectors.toList());- `String getType()`

```- `GroupSchemaCustom setProperties(Map<String, GroupSchemaAttribute> properties)`

- `GroupSchemaCustom setRequired(List<String> required)`

---- `GroupSchemaCustom setType(String type)`



## New Features in v25.0.0### Package `com.okta.sdk.resource.group.schema.GroupSchemaDefinitions`



### New API ClientsThis is a newly created interface with methods listed below.



| API Client | Purpose |- `GroupSchemaBase getBase()`

|------------|---------|- `GroupSchemaCustom getCustom()`

| `UserRiskApi` | Manage user risk levels |- `GroupSchemaDefinitions setBase(GroupSchemaBase base)`

| `UserClassificationApi` | Manage user classification |- `GroupSchemaDefinitions setCustom(GroupSchemaCustom custom)`

| `GroupOwnerApi` | Manage group owners |

| `DeviceIntegrationsApi` | Manage device integrations |### Package `com.okta.sdk.resource.identity.provider.IdentityProvider$IssuerModeEnum`

| `DevicePostureCheckApi` | Manage device posture checks |

| `GovernanceBundleApi` | Manage governance bundles |Below enum value has been changed.

| `ServiceAccountApi` | Manage service accounts |- `CUSTOM_URL_DOMAIN("CUSTOM_URL_DOMAIN")` changed to `CUSTOM_URL("CUSTOM_URL")`



### Code Examples### Package `com.okta.sdk.resource.identity.provider.SocialAuthToken`



**Working with User Risk (New):**Below methods have undergone a signature change.

- `TokenTypeEnum getTokenType()` signature changed to `String getTokenType()`

```java- `SocialAuthToken setTokenType(TokenTypeEnum tokenType)` signature changed to `SocialAuthToken setTokenType(String tokenType)`

UserRiskApi riskApi = new UserRiskApi(client);

### Package `com.okta.sdk.resource.identity.provider.SocialAuthToken`

// Get user risk level

UserRisk risk = riskApi.getUserRisk(userId);The Enum `com.okta.sdk.resource.identity.provider.SocialAuthToken$TokenTypeEnum` has been removed.

System.out.println("Risk Level: " + risk.getLevel());

### Package `com.okta.sdk.resource.org.OrgContactType`

// Update user risk level

UserRisk updateRequest = new UserRisk();This is a newly created enum with fields listed below.

updateRequest.setLevel(RiskLevel.LOW);- `BILLING("BILLING")`

UserRisk updated = riskApi.updateUserRisk(userId, updateRequest);- `TECHNICAL("TECHNICAL")`

```

### Package `com.okta.sdk.resource.org.OrgContactTypeObj`

**Working with Group Owners (New):**

This is a newly created interface with methods listed below.

```java

GroupOwnerApi ownerApi = new GroupOwnerApi(client);- `OrgContactType getContactType()`

- `OrgContactTypeObj setContactType(OrgContactType contactType)`

// Assign a user as group owner

AssignGroupOwnerRequestBody request = new AssignGroupOwnerRequestBody();### Package `com.okta.sdk.resource.org.OrgOktaCommunicationSetting`

request.setId(userId);

request.setType(GroupOwnerType.USER);This is a newly created interface with methods listed below.

GroupOwner owner = ownerApi.assignGroupOwner(groupId, request);

- `Boolean getOptOutEmailUsers()`

// List group owners- `OrgOktaCommunicationSetting optInUsersToOktaCommunicationEmails()`

List<GroupOwner> owners = ownerApi.listGroupOwners(groupId, null, null, null);- `OrgOktaCommunicationSetting optOutUsersFromOktaCommunicationEmails()`

```

### Package `com.okta.sdk.resource.org.OrgOktaSupportSetting`

---

This is a newly created enum with fields listed below.

## Deprecated Endpoints- `DISABLED("DISABLED")`

- `ENABLED("ENABLED")`

The following endpoints are deprecated and will be removed in a future release:

### Package `com.okta.sdk.resource.org.OrgOktaSupportSettingsObj`

| Endpoint | Note |

|----------|------|This is a newly created interface with methods listed below.

| `POST /api/v1/org/privacy/oktaSupport/extend` | Returns 301 redirect |

| `POST /api/v1/org/privacy/oktaSupport/grant` | Returns 301 redirect |- `OrgOktaSupportSettingsObj extendOktaSupport()`

| `POST /api/v1/org/privacy/oktaSupport/revoke` | Returns 301 redirect |- `Date getExpiration()`

| `POST /api/v1/risk/events/ip` | Deprecated |- `OrgOktaSupportSetting getSupport()`

| `GET /api/v1/risk/providers` | Deprecated |- `OrgOktaSupportSettingsObj grantOktaSupport()`

| `POST /api/v1/risk/providers` | Deprecated |- `OrgOktaSupportSettingsObj revokeOktaSupport()`

| `DELETE /api/v1/risk/providers/{id}` | Deprecated |

| `GET /api/v1/risk/providers/{id}` | Deprecated |### Package `com.okta.sdk.resource.org.OrgPreferences`

| `PUT /api/v1/risk/providers/{id}` | Deprecated |

This is a newly created interface with methods listed below.

**Action Required:** Update code to handle 301 redirects for `oktaSupport` endpoints.

- `Boolean getShowEndUserFooter()`

---- `OrgPreferences hideEndUserFooter()`

- `OrgPreferences showEndUserFooter()`

## FAQ

### Package `com.okta.sdk.resource.org.OrgSetting`

### Q: Do I need to update all my code at once?

This is a newly created interface with methods listed below.

A: No. Most existing code will continue to work. Focus on:

1. Code that accesses `user.type` properties- `String getAddress1()`

2. Code that handles specific response structures from affected endpoints- `String getAddress2()`

3. Code using `PaginationUtil.getAfter()`- `String getCity()`

- `String getCompanyName()`

### Q: Will the custom deserializers work automatically?- `String getCountry()`

- `Date getCreated()`

A: Yes. When you use `DefaultClientBuilder` to create the API client, all custom deserializers are automatically registered.- `String getEndUserSupportHelpURL()`

- `Date getExpiresAt()`

### Q: How do I handle the removed `user.type` property?- `String getId()`

- `Date getLastUpdated()`

A: Use the `UserTypeApi` to fetch user type information separately:- `String getPhoneNumber()`

- `String getPostalCode()`

```java- `String getState()`

UserTypeApi userTypeApi = new UserTypeApi(client);- `String getStatus()`

List<UserType> types = userTypeApi.listUserTypes();- `String getSubdomain()`

```- `String getSupportPhoneNumber()`

- `String getWebsite()`

### Q: What if I encounter deserialization errors?- `OrgSetting partialUpdate()`

- `OrgSetting setAddress1(String address1)`

A: The custom deserializers should handle most cases. If you encounter errors, please [open an issue](https://github.com/okta/okta-sdk-java/issues) with the JSON response that caused the error.- `OrgSetting setAddress2(String address2)`

- `OrgSetting setCity(String city)`

---- `OrgSetting setCompanyName(String companyName)`

- `OrgSetting setCountry(String country)`

## Getting Help- `OrgSetting setEndUserSupportHelpURL(String endUserSupportHelpURL)`

- `OrgSetting setPhoneNumber(String phoneNumber)`

If you encounter issues during migration:- `OrgSetting setPostalCode(String postalCode)`

- `OrgSetting setState(String state)`

1. **Check the API documentation**: Review the [Okta API Reference](https://developer.okta.com/docs/reference/) for the latest endpoint specifications- `OrgSetting setSupportPhoneNumber(String supportPhoneNumber)`

2. **Review the examples**: Check the `examples/quickstart` directory for working code samples- `OrgSetting setWebsite(String website)`

3. **File an issue**: [Open an issue](https://github.com/okta/okta-sdk-java/issues) on GitHub- `OrgSetting update()`

4. **Join the community**: Visit the [Okta Developer Forum](https://devforum.okta.com/) for community support

### Package `com.okta.sdk.resource.org.UserIdString`

This is a newly created interface with methods listed below.

- `String getUserId()`
- `UserIdString setUserId(String userId)`

### Package `com.okta.sdk.resource.role.RoleType`

Below enum value has been changed.
- `GROUP_MEMBERSHIP_ADMIN("GROUP_MEMBERSHIP_ADMIN")`

### Package `com.okta.sdk.resource.user.User`

This is a newly created interface with methods listed below.

- `Role getRole(String roleId)`

## Migrating from 4.x.x to 5.0.0

Version 5.0.0 of this SDK introduces a number of breaking changes from previous versions.
In addition to many new classes/interfaces, some existing classes/interfaces are no longer backward compatible due to method renaming and signature changes.

### Package `com.okta.sdk.client.Client`

Below methods have been added.
- `Domain createDomain(Domain domain)`
- `NetworkZone createNetworkZone(NetworkZone zone)`
- `void deleteDomain(String domainId)`
- `ThreatInsightConfiguration getCurrentConfiguration()`
- `Domain getDomain(String domainId)`
- `NetworkZone getNetworkZone(String zoneId)`
- `ProfileMapping getProfileMapping(String mappingId)`
- `DomainList listDomains()`
- `NetworkZoneList listNetworkZones(String filter)`
- `NetworkZoneList listNetworkZones()`
- `ProfileMappingList listProfileMappings(String sourceId, String targetId)`
- `ProfileMappingList listProfileMappings()`
- `Domain verifyDomain(String domainId)`

### Package `com.okta.sdk.resource.application.ApplicationSettings`

Below methods have been added.
- `ApplicationSettingsNotes getNotes()`
- `ApplicationSettings setNotes(ApplicationSettingsNotes notes)`

### Package `com.okta.sdk.resource.application.ApplicationSettingsNotes`

This is a newly created interface with methods listed below.

- `String getAdmin()`
- `String getEnduser()`
- `ApplicationSettingsNotes setAdmin(String admin)`
- `ApplicationSettingsNotes setEnduser(String enduser)`

### Package `com.okta.sdk.resource.application.OpenIdConnectApplicationSettingsClient` 

Below methods have been added.
- `void activate()`
- `void deactivate()`

### Package `com.okta.sdk.resource.application.SamlApplicationSettingsSignOn`

Below methods have been added.
- `List<SignOnInlineHook> getInlineHooks()`
- `SamlApplicationSettingsSignOn setInlineHooks(List<SignOnInlineHook> inlineHooks)`

### Package `com.okta.sdk.resource.application.SignOnInlineHook`

This is a newly created interface with methods listed below.

- `String getId()`
- `SignOnInlineHook setId(String hookId)`

### Package `com.okta.sdk.resource.authorization.server.AuthorizationServerPolicy`

Below methods have been added.
- `void activate(String authServerId)`
- `void deactivate(String authServerId)`

### Package `com.okta.sdk.resource.authorization.server.policy.AuthorizationServerPolicyRule`

Below methods have undergone a signature change.
- `void activate()` signature changed to `void activate(String authServerId)`
- `void deactivate()` signature changed to `void deactivate(String authServerId)`

### Package `com.okta.sdk.resource.authorization.server.policy.TokenAuthorizationServerPolicyRuleAction`

Below methods have been added.
- `TokenAuthorizationServerPolicyRuleActionInlineHook getInlineHook()`
- `TokenAuthorizationServerPolicyRuleAction setInlineHook(TokenAuthorizationServerPolicyRuleActionInlineHook inlineHook)`

### Package `com.okta.sdk.resource.authorization.server.policy.TokenAuthorizationServerPolicyRuleActionInlineHook`

This is a newly created interface with methods listed below.

- `String getId()`
- `TokenAuthorizationServerPolicyRuleActionInlineHook setId(String hookId)`

### Package `com.okta.sdk.resource.domain.DNSRecord`

This is a newly created interface with methods listed below.

- `String getExpiration()`
- `String getFqdn()`
- `DNSRecordType getRecordType()`
- `List<String> getValues()`
- `DNSRecord setExpiration(String exp)`
- `DNSRecord setFqdn(String fqdn)`
- `DNSRecord setRecordType(DNSRecordType recordType)`
- `DNSRecord setValues(List<String> values)`

### Package `com.okta.sdk.resource.domain.DNSRecordType`

This is a newly created enum with fields listed below.
- `CNAME("CNAME")`
- `TXT("TXT")`

### Package `com.okta.sdk.resource.domain.Domain`

This is a newly created interface with methods listed below.

- `DomainCertificateSourceType getCertificateSourceType()`
- `List<DNSRecord> getDnsRecords()`
- `String getDomain()`
- `String getId()`
- `DomainCertificateMetadata getPublicCertificate()`
- `DomainValidationStatus getValidationStatus()`
- `Domain setCertificateSourceType(DomainCertificateSourceType value)`
- `Domain setDnsRecords(List<DNSRecord> records)`
- `Domain setDomain(String domain)`
- `Domain setPublicCertificate(DomainCertificateMetadata value)`
- `Domain setValidationStatus(DomainValidationStatus status)`

### Package `com.okta.sdk.resource.domain.DomainCertificate`

This is a newly created interface with methods listed below.

- `void createCertificate(String cert)`
- `String getCertificate()`
- `String getCertificateChain()`
- `String getPrivateKey()`
- `DomainCertificateType getType()`
- `DomainCertificate setCertificate(String cert)`
- `DomainCertificate setCertificateChain(String certificateChain)`
- `DomainCertificate setPrivateKey(String privateKey)`
- `DomainCertificate setType(DomainCertificateType value)`

### Package `com.okta.sdk.resource.domain.DomainCertificateMetadata`

This is a newly created interface with methods listed below.

- `String getExpiration()`
- `String getFingerprint()`
- `String getSubject()`
- `DomainCertificateMetadata setExpiration(String exp)`
- `DomainCertificateMetadata setFingerprint(String fingerprint)`
- `DomainCertificateMetadata setSubject(String subject)`

### Package `com.okta.sdk.resource.domain.DomainCertificateSourceType`

This is a newly created enum with field listed below.
- `MANUAL("MANUAL")`

### Package `com.okta.sdk.resource.domain.DomainCertificateType`

This is a newly created enum with field listed below.
- `PEM("PEM")`

### Package `com.okta.sdk.resource.domain.DomainValidationStatus`

This is a newly created enum with fields listed below.
- `COMPLETED("COMPLETED")`
- `IN_PROGRESS("IN_PROGRESS")`
- `NOT_STARTED("NOT_STARTED")`
- `VERIFIED("VERIFIED")`

### Package `com.okta.sdk.resource.group.rule.GroupRule`

Below methods has been added.
- `void delete(Boolean removeUsers)`

### Package `com.okta.sdk.resource.network.zone.NetworkZone`

This is a newly created interface with methods listed below.

- `NetworkZone activate()`
- `NetworkZone deactivate()`
- `void delete()`
- `List<String> getAsns()`
- `Date getCreated()`
- `List<NetworkZoneAddress> getGateways()`
- `String getId()`
- `Date getLastUpdated()`
- `Map<String, Object> getLinks()`
- `List<NetworkZoneLocation> getLocations()`
- `String getName()`
- `List<NetworkZoneAddress> getProxies()`
- `String getProxyType()`
- `NetworkZoneStatus getStatus()`
- `Boolean getSystem()`
- `NetworkZoneType getType()`
- `NetworkZoneUsage getUsage()`
- `NetworkZone setAsns(List<String> asns)`
- `NetworkZone setGateways(List<NetworkZoneAddress> gateways)`
- `NetworkZone setLocations(List<NetworkZoneLocation> locations)`
- `NetworkZone setName(String name)`
- `NetworkZone setProxies(List<NetworkZoneAddress> proxies)`
- `NetworkZone setProxyType(String proxyType)`
- `NetworkZone setStatus(NetworkZoneStatus status)`
- `NetworkZone setSystem(Boolean system)`
- `NetworkZone setType(NetworkZoneType type)`
- `NetworkZone setUsage(NetworkZoneUsage usage)`
- `NetworkZone update()`

### Package `com.okta.sdk.resource.network.zone.NetworkZoneAddress`

This is a newly created interface with methods listed below.

- `NetworkZoneAddressType getType()`
- `String getValue()`
- `NetworkZoneAddress setType(NetworkZoneAddressType type)`
- `NetworkZoneAddress setValue(String value)`

### Package `com.okta.sdk.resource.network.zone.NetworkZoneAddressType`

This is a newly created enum with fields listed below.
- `CIDR("CIDR")`
- `RANGE("RANGE")`

### Package `com.okta.sdk.resource.network.zone.NetworkZoneLocation`

This is a newly created interface with methods listed below.
- `String getCountry()`
- `String getRegion()`
- `NetworkZoneLocation setCountry(String country)`
- `NetworkZoneLocation setRegion(String region)`

### Package `com.okta.sdk.resource.network.zone.NetworkZoneStatus`

This is a newly created enum with fields listed below.
- `ACTIVE("ACTIVE")`
- `INACTIVE("INACTIVE")`

### Package `com.okta.sdk.resource.network.zone.NetworkZoneType`

This is a newly created enum with fields listed below.
- `DYNAMIC("DYNAMIC")`
- `IP("IP")`

### Package `com.okta.sdk.resource.network.zone.NetworkZoneUsage`

This is a newly created enum with fields listed below.
- `BLOCKLIST("BLOCKLIST")`
- `POLICY("POLICY")`

### Package `com.okta.sdk.resource.policy.PolicyRule`

Below methods have been added.
- `PolicyRuleActions getActions()`
- `PolicyRuleConditions getConditions()`
- `String getName()`
- `PolicyRule setActions(PolicyRuleActions actions)`
- `PolicyRule setConditions(PolicyRuleConditions conditions)`
- `PolicyRule setName(String name)`

### Package `com.okta.sdk.resource.policy.PolicyRuleActions`

This is a newly created interface with methods listed below.
- `PolicyRuleActionsEnroll getEnroll()`
- `PasswordPolicyRuleAction getPasswordChange()`
- `PasswordPolicyRuleAction getSelfServicePasswordReset()`
- `PasswordPolicyRuleAction getSelfServiceUnlock()`
- `OktaSignOnPolicyRuleSignonActions getSignon()`
- `PolicyRuleActions setEnroll(PolicyRuleActionsEnroll enroll)`
- `PolicyRuleActions setPasswordChange(PasswordPolicyRuleAction passwordChange)`
- `PolicyRuleActions setSelfServicePasswordReset(PasswordPolicyRuleAction selfServicePasswordReset)`
- `PolicyRuleActions setSelfServiceUnlock(PasswordPolicyRuleAction selfServiceUnlock)`
- `PolicyRuleActions setSignon(OktaSignOnPolicyRuleSignonActions signon)`

### Package `com.okta.sdk.resource.policy.PolicyRuleActionsEnroll`

This is a newly created interface with methods listed below.
- `PolicyRuleActionsEnrollSelf getSelf()`
- `PolicyRuleActionsEnroll setSelf(PolicyRuleActionsEnrollSelf self)`

### Package `com.okta.sdk.resource.policy.PolicyRuleActionsEnrollSelf`

This is a newly created enum with fields listed below.
- `CHALLENGE("CHALLENGE")`
- `LOGIN("LOGIN")`
- `NEVER("NEVER")`

### Package `com.okta.sdk.resource.profile.mapping.ProfileMapping`

This is a newly created interface with methods listed below.
- `String getId()`
- `Map<String, Object> getLinks()`
- `Map<String, ProfileMappingProperty> getProperties()`
- `ProfileMappingSource getSource()`
- `ProfileMappingSource getTarget()`
- `ProfileMapping setSource(ProfileMappingSource source)`
- `ProfileMapping setTarget(ProfileMappingSource target)`
- `ProfileMapping update(ProfileMapping profileMapping)`

### Package `com.okta.sdk.resource.profile.mapping.ProfileMappingProperty`

This is a newly created interface with methods listed below.
- `String getExpression()`
- `ProfileMappingPropertyPushStatus getPushStatus()`
- `ProfileMappingProperty setExpression(String expression)`
- `ProfileMappingProperty setPushStatus(ProfileMappingPropertyPushStatus pushStatus)`

### Package `com.okta.sdk.resource.profile.mapping.ProfileMappingPropertyPushStatus`

This is a newly created enum with fields listed below.
- `DONT_PUSH("DONT_PUSH")`
- `PUSH("PUSH")`

### Package `com.okta.sdk.resource.profile.mapping.ProfileMappingSource`

This is a newly created interface with methods listed below.
- `String getId()`
- `Map<String, Object> getLinks()`
- `String getName()`
- `String getType()`

### Package `com.okta.sdk.resource.threat.insight.ThreatInsightConfiguration`

This is a newly created interface with methods listed below.
- `String getAction()`
- `Date getCreated()`
- `List<String> getExcludeZones()`
- `Date getLastUpdated()`
- `Map<String, Object> getLinks()`
- `ThreatInsightConfiguration setAction(String action)`
- `ThreatInsightConfiguration setExcludeZones(List<String> excludeZones)`
- `ThreatInsightConfiguration update()`

### Package `com.okta.sdk.resource.user.factor.FactorType`

Enum `FactorType` has the below new field definition:
- `HOTP("HOTP")`

### Package `com.okta.sdk.resource.user.schema.UserSchema`

Below method has undergone a signature change.
- `User getProperties()` signature changed to `UserSchemaProperties getProperties()`

### Package `com.okta.sdk.resource.user.schema.UserSchemaAttribute`

Below methods have undergone a signature change.
- `String getScope()` signature changed to `UserSchemaAttributeScope getScope()`
- `String getType()` signature changed to `UserSchemaAttributeType getType()`
- `UserSchemaAttribute setScope(String scope)` signature changed to `UserSchemaAttribute setScope(UserSchemaAttributeScope scope)`
- `UserSchemaAttribute setType(String type)` signature changed to `UserSchemaAttribute setType(UserSchemaAttributeType type)`

Below methods have been added.
- `List<String> getEnum()`
- `String getExternalName()`
- `String getExternalNamespace()`
- `UserSchemaAttributeItems getItems()`
- `List<UserSchemaAttributeEnum> getOneOf()`
- `String getPattern()`
- `UserSchemaAttributeUnion getUnion()`
- `String getUnique()`
- `UserSchemaAttribute setEnum(List<String> value)`
- `UserSchemaAttribute setExternalName(String externalName)`
- `UserSchemaAttribute setExternalNamespace(String externalNamespace)`
- `UserSchemaAttribute setItems(UserSchemaAttributeItems items)`
- `UserSchemaAttribute setOneOf(List<UserSchemaAttributeEnum> oneOf)`
- `UserSchemaAttribute setPattern(String pattern)`
- `UserSchemaAttribute setUnion(UserSchemaAttributeUnion union)`
- `UserSchemaAttribute setUnique(String unique)`

### Package `com.okta.sdk.resource.user.schema.UserSchemaAttributeEnum`

This is a newly created interface with methods listed below.
- `String getConst()`
- `String getTitle()`
- `UserSchemaAttributeEnum setConst(String value)`
- `UserSchemaAttributeEnum setTitle(String title)`

### Package `com.okta.sdk.resource.user.schema.UserSchemaAttributeItems`

This is a newly created interface with methods listed below.
- `List<String> getEnum()`
- `List<UserSchemaAttributeEnum> getOneOf()`
- `String getType()`
- `UserSchemaAttributeItems setEnum(List<String> values)`
- `UserSchemaAttributeItems setOneOf(List<UserSchemaAttributeEnum> oneOf)`
- `UserSchemaAttributeItems setType(String type)`

### Package `com.okta.sdk.resource.user.schema.UserSchemaAttributeMaster`

Below methods have undergone a signature change.
- `String getType()` signature changed to `UserSchemaAttributeMasterType getType()`
- `UserSchemaAttributeMaster setType(String type)` signature changed to `UserSchemaAttributeMaster setType(UserSchemaAttributeMasterType type)`

Below methods have been added.
- `List<UserSchemaAttributeMasterPriority> getPriority()`
- `UserSchemaAttributeMaster setPriority(List<UserSchemaAttributeMasterPriority> priority)`

### Package `com.okta.sdk.resource.user.schema.UserSchemaAttributeMasterPriority`

This is a newly created interface with methods listed below.
- `String getType()`
- `String getValue()`
- `UserSchemaAttributeMasterPriority setType(String type)`
- `UserSchemaAttributeMasterPriority setValue(String value)`

### Package `com.okta.sdk.resource.user.schema.UserSchemaAttributeMasterType`

This is a newly created enum with fields listed below.
- `OKTA("OKTA")`
- `OVERRIDE("OVERRIDE")`
- `PROFILE_MASTER("PROFILE_MASTER")`

### Package `com.okta.sdk.resource.user.schema.UserSchemaAttributeScope`

This is a newly created enum with fields listed below.
- `NONE("NONE")`
- `SELF("SELF")`

### Package `com.okta.sdk.resource.user.schema.UserSchemaAttributeType`

This is a newly created enum with fields listed below.
- `ARRAY("ARRAY")`
- `BOOLEAN("BOOLEAN")`
- `INTEGER("INTEGER")`
- `NUMBER("NUMBER")`
- `STRING("STRING")`

### Package `com.okta.sdk.resource.user.schema.UserSchemaAttributeUnion`

This is a newly created enum with fields listed below.
- `DISABLE("DISABLE")`
- `ENABLE("ENABLE")`

### Package `com.okta.sdk.resource.user.schema.UserSchemaProperties`

This is a newly created interface with methods listed below.
- `UserSchemaPropertiesProfile getProfile()`
- `UserSchemaProperties setProfile(UserSchemaPropertiesProfile profile)`

### Package `com.okta.sdk.resource.user.schema.UserSchemaPropertiesProfile`

This is a newly created interface with methods listed below.
- `List<UserSchemaPropertiesProfileItem> getAllOf()`
- `UserSchemaPropertiesProfile setAllOf(List<UserSchemaPropertiesProfileItem> allOf)`

### Package `com.okta.sdk.resource.user.schema.UserSchemaPropertiesProfileItem`

This is a newly created interface with methods listed below.
- `String getRef()`
- `UserSchemaPropertiesProfileItem setRef(String ref)`

## Migrating from 3.x.x to 4.0.0

Version 4.0.0 of this SDK introduces a number of breaking changes from previous versions.
In addition to many new classes/interfaces, some existing classes/interfaces are no longer backward compatible due to method renaming and signature changes.

### Package `com.okta.sdk.client.Client`

Below methods have undergone a signature change.
- `User createUser(User user, Boolean active, Boolean provider, UserNextLogin nextLogin)` signature changed to `User createUser(CreateUserRequest createUserRequest, Boolean active, Boolean provider, UserNextLogin nextLogin)`
- `User createUser(User user)` signature changed to `User createUser(CreateUserRequest createUserRequest)`
    - First argument type changed from `User` to `CreateUserRequest`

Below methods have been added.
- `UserSchema updateApplicationUserProfile(String appInstanceId, UserSchema userSchema)`
- `UserSchema updateApplicationUserProfile(String appInstanceId)`
- `UserSchema updateUserProfile(String schemaId, UserSchema userSchema)`

Below method has been removed.
- `ForgotPasswordResponse apiV1UsersUserIdCredentialsForgotPasswordPost(String userId)`

One of below listed methods could be used instead:
- `User.forgotPasswordGenerateOneTimeToken(Boolean sendEmail)`
- `User.forgotPasswordGenerateOneTimeToken()`
- `User.forgotPasswordSetNewPassword(UserCredentials userCredentials, Boolean sendEmail)`
- `User.forgotPasswordSetNewPassword(UserCredentials userCredentials)`
    
### Package `com.okta.sdk.resource.user.type.UserType`

Class `com.okta.sdk.resource.user.UserType` moved to `com.okta.sdk.resource.user.type.UserType`

### Package `com.okta.sdk.resource.user.type.UserTypeList`

Class `com.okta.sdk.resource.user.UserTypeList` moved to `com.okta.sdk.resource.user.type.UserTypeList`

### Package `com.okta.sdk.resource.application.JsonWebKey`

Below methods have been added.
- `JsonWebKey setAlg(String alg)`
- `JsonWebKey setCreated(Date created)`
- `JsonWebKey setE(String e)`
- `JsonWebKey setExpiresAt(Date expiresAt)`
- `JsonWebKey setKeyOps(List<String> keyOps)`
- `JsonWebKey setKid(String kid)`
- `JsonWebKey setKty(String kty)`
- `JsonWebKey setLastUpdated(Date lastUpdated)`
- `JsonWebKey setN(String n)`
- `JsonWebKey setStatus(String status)`
- `JsonWebKey setUse(String use)`
- `JsonWebKey setX5t(String x5t)`
- `JsonWebKey setX5tS256(String x5tS256)`
- `JsonWebKey setX5u(String x5u)`

### Package `com.okta.sdk.resource.application.OIDCApplicationBuilder`

The interface has been renamed in the interest of naming consistency.
- From `OIdCApplicationBuilder` to `OIDCApplicationBuilder`

Below method has been added.
- `OIDCApplicationBuilder setJwks(List<JsonWebKey> jsonWebKeyList)`

### Package `com.okta.sdk.resource.application.OpenIdConnectApplicationIdpInitiatedLogin`

This is a newly created interface with methods listed below.

- `List<String> getDefaultScope()`
- `OpenIdConnectApplicationIdpInitiatedLogin setDefaultScope(List<String> defaultScope)`
- `String getMode()`
- `OpenIdConnectApplicationIdpInitiatedLogin setMode(String mode)`

### Package `com.okta.sdk.resource.application.OpenIdConnectApplicationSettingsClient`

Below methods have been added.

- `OpenIdConnectApplicationIdpInitiatedLogin getIdpInitiatedLogin()`
- `OpenIdConnectApplicationSettingsClient setIdpInitiatedLogin(OpenIdConnectApplicationIdpInitiatedLogin idpInitiatedLogin)`

### Package `com.okta.sdk.resource.application.SamlApplicationSettingsSignOn`

Below methods have been added.

- `SingleLogout getSlo()`
- `SpCertificate getSpCertificate()`
- `SamlApplicationSettingsSignOn setSlo(SingleLogout slo)`
- `SamlApplicationSettingsSignOn setSpCertificate(SpCertificate spCertificate)`

### Package `com.okta.sdk.resource.application.SamlApplicationV1`

The Interface `com.okta.sdk.resource.application.SamlApplicationV1` has been removed.
The Interface `com.okta.sdk.resource.application.SamlApplication` should be used instead.


### Package `com.okta.sdk.resource.application.SingleLogout`

This is a newly created interface with methods listed below.

- `Boolean getEnabled()`
- `String getIssuer()`
- `String getLogoutUrl()`
- `SingleLogout setEnabled(Boolean enabled)`
- `SingleLogout setIssuer(String issuer)`
- `SingleLogout setLogoutUrl(String logoutUrl)`

### Package `com.okta.sdk.resource.application.SpCertificate`

This is a newly created interface with methods listed below.

- `List<String> getX5c()`
- `SpCertificate setX5c(List<String> x5c)`

### Package `com.okta.sdk.resource.authorization.server.AuthorizationServerPolicy`

Below method has been renamed in the interest of naming consistency.
- Renamed `deletePolicy` to `delete`

Below method has undergone a signature change.
- `void deletePolicyRule(String ruleId)` signature changed to `void deletePolicyRule(String authServerId, String ruleId)`

Below method has undergone a signature change and renamed in the interest of naming consistency.
- `AuthorizationServerPolicy updatePolicy(String authServerId, AuthorizationServerPolicy authServerPolicy)` changed to `AuthorizationServerPolicy update(String authServerId)`

### Package `com.okta.sdk.resource.authorization.server.policy.AuthorizationServerPolicyRule`

Below method has been renamed in the interest of naming consistency.
- Renamed `deletePolicyRule` to `delete`

### Package `com.okta.sdk.resource.inline.hook.InlineHookChannelConfig`

Below methods have been added.

- `String getMethod()`
- `InlineHookChannelConfig setMethod(String method)`

### Package `com.okta.sdk.resource.linked.object.LinkedObject`

Below methods have been removed.
- `String getName()`
  - `LinkedObject.getPrimary().getName()` should be used instead
- `LinkedObject setName(String name)`
  - `LinkedObject.getPrimary().setName()` should be used instead

### Package `com.okta.sdk.resource.log.LogCredentialProvider`

Enum `LogCredentialProvider` has the below new field definition:
- `OKTA_CREDENTIAL_PROVIDER("OKTA_CREDENTIAL_PROVIDER")`

### Package `com.okta.sdk.resource.policy.DevicePolicyRuleConditionPlatform$SupportedMDMFrameworksEnum`

Interface `com.okta.sdk.resource.policy.MDMFrameworks` moved to `com.okta.sdk.resource.policy.DevicePolicyRuleConditionPlatform$SupportedMDMFrameworksEnum`

### Package `com.okta.sdk.resource.policy.DevicePolicyRuleConditionPlatform$TypesEnum`

Interface `com.okta.sdk.resource.policy.Platforms` moved to `com.okta.sdk.resource.policy.DevicePolicyRuleConditionPlatform$TypesEnum`

### Package `com.okta.sdk.resource.user.factor.PushUserFactor` 

Below method has been added
- `PushUserFactor setExpiresAt(Date expiresAt)`

### Package `com.okta.sdk.resource.user.factor.UserFactor`

Below method has been renamed in the interest of naming consistency.
- Renamed `deleteFactor` to `delete`

### Package `com.okta.sdk.resource.user.schema.UserSchema`

This is a newly created interface with methods listed below.
- `String getCreated()`
- `UserSchemaDefinitions getDefinitions()`
- `String getId()`
- `String getLastUpdated()`
- `Map getLinks()`
- `String getName()`
- `Map getProperties()`
- `String getSchema()`
- `String getTitle()`
- `String getType()`
- `UserSchema setDefinitions(UserSchemaDefinitions definitions)`
- `UserSchema setTitle(String title)`

### Package `com.okta.sdk.resource.user.schema.UserSchemaAttribute`

This is a newly created interface with methods listed below.
- `String getDescription()`
- `UserSchemaAttributeMaster getMaster()`
- `Integer getMaxLength()`
- `Integer getMinLength()`
- `String getMutability()`
- `List getPermissions()`
- `Boolean getRequired()`
- `String getScope()`
- `String getTitle()`
- `String getType()`
- `UserSchemaAttribute setDescription(String description)`
- `UserSchemaAttribute setMaster(UserSchemaAttributeMaster master)`
- `UserSchemaAttribute setMaxLength(Integer maxLength)`
- `UserSchemaAttribute setMinLength(Integer minLength)`
- `UserSchemaAttribute setMutability(String mutability)`
- `UserSchemaAttribute setPermissions(List permissions)`
- `UserSchemaAttribute setRequired(Boolean required)`
- `UserSchemaAttribute setScope(String scope)`
- `UserSchemaAttribute setTitle(String title)`
- `UserSchemaAttribute setType(String type)`

### Package `com.okta.sdk.resource.user.schema.UserSchemaAttributeMaster`

This is a newly created interface with methods listed below.
- `String getType()`
- `UserSchemaAttributeMaster setType(String type)`

### Package `com.okta.sdk.resource.user.schema.UserSchemaAttributePermission`

This is a newly created interface with methods listed below.
- `String getAction()`
- `String getPrincipal()`
- `UserSchemaAttributePermission setAction(String action)`
- `UserSchemaAttributePermission setPrincipal(String principal)`

### Package `com.okta.sdk.resource.user.schema.UserSchemaBase`

This is a newly created interface with methods listed below.
- `String getId()`
- `UserSchemaBaseProperties getProperties()`
- `List<String> getRequired()`
- `String getType()`
- `UserSchemaBase setProperties(UserSchemaBaseProperties properties)`
- `UserSchemaBase setRequired(List<String> required)`
- `UserSchemaBase setType(String type)`

### Package `com.okta.sdk.resource.user.schema.UserSchemaBaseProperties`

This is a newly created interface with methods listed below.
- `UserSchemaAttribute getCity()`
- `UserSchemaAttribute getCostCenter()`
- `UserSchemaAttribute getCountryCode()`
- `UserSchemaAttribute getDepartment()`
- `UserSchemaAttribute getDisplayName()`
- `UserSchemaAttribute getDivision()`
- `UserSchemaAttribute getEmail()`
- `UserSchemaAttribute getEmployeeNumber()`
- `UserSchemaAttribute getFirstName()`
- `UserSchemaAttribute getHonorificPrefix()`
- `UserSchemaAttribute getHonorificSuffix()`
- `UserSchemaAttribute getLastName()`
- `UserSchemaAttribute getLocale()`
- `UserSchemaAttribute getLogin()`
- `UserSchemaAttribute getManager()`
- `UserSchemaAttribute getManagerId()`
- `UserSchemaAttribute getMiddleName()`
- `UserSchemaAttribute getMobilePhone()`
- `UserSchemaAttribute getNickName()`
- `UserSchemaAttribute getOrganization()`
- `UserSchemaAttribute getPostalAddress()`
- `UserSchemaAttribute getPreferredLanguage()`
- `UserSchemaAttribute getPrimaryPhone()`
- `UserSchemaAttribute getProfileUrl()`
- `UserSchemaAttribute getSecondEmail()`
- `UserSchemaAttribute getState()`
- `UserSchemaAttribute getStreetAddress()`
- `UserSchemaAttribute getTimezone()`
- `UserSchemaAttribute getTitle()`
- `UserSchemaAttribute getUserType()`
- `UserSchemaAttribute getZipCode()`
- `UserSchemaBaseProperties setCity(UserSchemaAttribute city)`
- `UserSchemaBaseProperties setCostCenter(UserSchemaAttribute costCenter)`
- `UserSchemaBaseProperties setCountryCode(UserSchemaAttribute countryCode)`
- `UserSchemaBaseProperties setDepartment(UserSchemaAttribute department)`
- `UserSchemaBaseProperties setDisplayName(UserSchemaAttribute displayName)`
- `UserSchemaBaseProperties setDivision(UserSchemaAttribute division)`
- `UserSchemaBaseProperties setEmail(UserSchemaAttribute email)`
- `UserSchemaBaseProperties setEmployeeNumber(UserSchemaAttribute employeeNumber)`
- `UserSchemaBaseProperties setFirstName(UserSchemaAttribute firstName)`
- `UserSchemaBaseProperties setHonorificPrefix(UserSchemaAttribute honorificPrefix)`
- `UserSchemaBaseProperties setHonorificSuffix(UserSchemaAttribute honorificSuffix)`
- `UserSchemaBaseProperties setLastName(UserSchemaAttribute lastName)`
- `UserSchemaBaseProperties setLocale(UserSchemaAttribute locale)`
- `UserSchemaBaseProperties setLogin(UserSchemaAttribute login)`
- `UserSchemaBaseProperties setManager(UserSchemaAttribute manager)`
- `UserSchemaBaseProperties setManagerId(UserSchemaAttribute managerId)`
- `UserSchemaBaseProperties setMiddleName(UserSchemaAttribute middleName)`
- `UserSchemaBaseProperties setMobilePhone(UserSchemaAttribute mobilePhone)`
- `UserSchemaBaseProperties setNickName(UserSchemaAttribute nickName)`
- `UserSchemaBaseProperties setOrganization(UserSchemaAttribute organization)`
- `UserSchemaBaseProperties setPostalAddress(UserSchemaAttribute postalAddress)`
- `UserSchemaBaseProperties setPreferredLanguage(UserSchemaAttribute preferredLanguage)`
- `UserSchemaBaseProperties setPrimaryPhone(UserSchemaAttribute primaryPhone)`
- `UserSchemaBaseProperties setProfileUrl(UserSchemaAttribute profileUrl)`
- `UserSchemaBaseProperties setSecondEmail(UserSchemaAttribute secondEmail)`
- `UserSchemaBaseProperties setState(UserSchemaAttribute state)`
- `UserSchemaBaseProperties setStreetAddress(UserSchemaAttribute streetAddress)`
- `UserSchemaBaseProperties setTimezone(UserSchemaAttribute timezone)`
- `UserSchemaBaseProperties setTitle(UserSchemaAttribute title)`
- `UserSchemaBaseProperties setUserType(UserSchemaAttribute userType)`
- `UserSchemaBaseProperties setZipCode(UserSchemaAttribute zipCode)`

### Package `com.okta.sdk.resource.user.schema.UserSchemaDefinitions`

This is a newly created interface with methods listed below.
- `UserSchemaBase getBase()`
- `UserSchemaPublic getCustom()`
- `UserSchemaDefinitions setBase(UserSchemaBase base)`
- `UserSchemaDefinitions setCustom(UserSchemaPublic custom)`

### Package `com.okta.sdk.resource.user.schema.UserSchemaPublic`

This is a newly created interface with methods listed below.
- `String getId()`
- `Map<String, Object> getProperties()`
- `List<String> getRequired()`
- `String getType()`
- `UserSchemaPublic setProperties(Map<String, Object> properties)`
- `UserSchemaPublic setRequired(List<String> required)`
- `UserSchemaPublic setType(String type)`

### Package `com.okta.sdk.resource.user.User`

Below method has undergone a signature change in the interest of naming consistency.
- `void addAllAppsAsTargetToRole()` to `void addAllAppsAsTarget(String roleId)`
- `void deleteFactor()` to `void deleteFactor(String factorId)`

Below methods have been added.
- `ForgotPasswordResponse forgotPasswordGenerateOneTimeToken(Boolean sendEmail)`
- `ForgotPasswordResponse forgotPasswordGenerateOneTimeToken()`
- `ForgotPasswordResponse forgotPasswordSetNewPassword(UserCredentials userCredentials, Boolean sendEmail)`
- `ForgotPasswordResponse forgotPasswordSetNewPassword(UserCredentials userCredentials)`

### Package `com.okta.sdk.resource.user.UserBuilder`

Below method has undergone a signature change.
- `UserBuilder setProvider(Boolean provider)` signature changed to `UserBuilder setProvider(AuthenticationProvider provider)`

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
