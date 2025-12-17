# Migration Guide: Upgrading to Okta Java SDK v25.0.0

## 📋 Table of Contents

1. [Overview](#overview)
2. [Quick Start Checklist](#quick-start-checklist)
3. [Breaking Changes](#breaking-changes)
4. [API-Specific Migration](#api-specific-migration)
5. [Code Examples](#code-examples)
6. [New Features](#new-features)
7. [Deprecated Features](#deprecated-features)
8. [FAQ](#faq)
9. [Getting Help](#getting-help)

---

## Overview

Okta Java SDK v25.0.0 introduces significant changes based on the updated Okta Admin Management API (OpenAPI spec v5.1.0). This guide will help you migrate your existing codebase from v24.x to v25.0.0.

### What's Changed

| Aspect | v24.x | v25.0.0 |
|--------|-------|---------|
| OpenAPI Spec Version | 2024.08.3 | 5.1.0 |
| API YAML Lines | 66,987 | 83,953 |
| Integration Tests | Limited | 35 comprehensive suites |
| Custom Deserializers | 3 | 9 |

### Breaking Change Summary

| Category | Number of Affected Endpoints |
|----------|------------------------------|
| User Object Schema | 10+ |
| Authenticator APIs | 12 |
| User Factor APIs | 7 |
| Policy APIs | 9 |
| Identity Provider APIs | 7 |
| Role Assignment APIs | 9 |
| Other APIs | 20+ |

---

## Quick Start Checklist

Use this checklist to ensure a smooth migration:

- [ ] **Step 1**: Update SDK dependency to v25.0.0
- [ ] **Step 2**: Search codebase for `user.getType()` or `user.type` references
- [ ] **Step 3**: Review Authenticator API usage
- [ ] **Step 4**: Review User Factor API usage
- [ ] **Step 5**: Review Policy API usage
- [ ] **Step 6**: Review Identity Provider API usage
- [ ] **Step 7**: Review Role Assignment API usage
- [ ] **Step 8**: Update deprecated endpoint usage
- [ ] **Step 9**: Run tests and verify functionality
- [ ] **Step 10**: Update error handling for new response codes

---

## Breaking Changes

### 1. User Object Schema Changes (CRITICAL)

**Impact Level**: 🔴 High

The `User` object no longer includes the `type` property and its sub-properties in API responses.

#### Before (v24.x)

```java
User user = userApi.getUser(userId);
UserType type = user.getType();
String typeId = type.getId();
String typeName = type.getName();
Boolean isDefault = type.getDefault();
Date created = type.getCreated();
String createdBy = type.getCreatedBy();
```

#### After (v25.0.0)

```java
User user = userApi.getUser(userId);
// user.getType() may return null or different structure
// Use UserTypeApi to get type information separately

UserTypeApi userTypeApi = new UserTypeApi(client);
List<UserType> types = userTypeApi.listUserTypes();
// Find user's type from the types list or user's _links
```

#### Affected Endpoints

- `GET /api/v1/devices`
- `GET /api/v1/devices/{deviceId}/users`
- `GET /api/v1/groups/{groupId}/users`
- `GET /api/v1/users`
- `POST /api/v1/users`
- `GET /api/v1/users/{id}`
- `PUT /api/v1/users/{id}`
- `POST /api/v1/users/{id}`
- `POST /api/v1/users/{userId}/lifecycle/expire_password`
- `POST /api/v1/users/{userId}/lifecycle/expire_password_with_temp_password`

---

### 2. Authenticator API Changes

**Impact Level**: 🟡 Medium

The response schemas for Authenticator endpoints have changed. Review the new `Authenticator` and `AuthenticatorMethod` models.

#### Affected Endpoints

| Endpoint | Change Type |
|----------|-------------|
| `GET /api/v1/authenticators` | Response schema |
| `POST /api/v1/authenticators` | Response schema |
| `GET /api/v1/authenticators/{authenticatorId}` | Response schema |
| `PUT /api/v1/authenticators/{authenticatorId}` | Response schema |
| `POST .../lifecycle/activate` | Response schema |
| `POST .../lifecycle/deactivate` | Response schema |
| `GET .../methods` | Response schema |
| `GET .../methods/{methodType}` | Response schema |
| `PUT .../methods/{methodType}` | Response schema |

#### Migration Example

```java
// Before (v24.x)
AuthenticatorApi authenticatorApi = new AuthenticatorApi(client);
Authenticator auth = authenticatorApi.getAuthenticator(authId);
// Access properties that may have changed

// After (v25.0.0)
AuthenticatorApi authenticatorApi = new AuthenticatorApi(client);
Authenticator auth = authenticatorApi.getAuthenticator(authId);
// Review the new Authenticator model structure
// Some properties may be in different locations or have different types
```

---

### 3. User Factor API Changes

**Impact Level**: 🟡 Medium

The request and response schemas for Factor endpoints have changed, including the `_links.resend` property type.

#### Affected Endpoints

| Endpoint | Change |
|----------|--------|
| `GET /api/v1/users/{userId}/factors` | Response schema |
| `POST /api/v1/users/{userId}/factors` | Request & Response schemas |
| `GET /api/v1/users/{userId}/factors/catalog` | Response schema |
| `GET /api/v1/users/{userId}/factors/{factorId}` | Response schema |
| `POST .../lifecycle/activate` | Request schema |
| `POST .../verify` | Request & Response schemas |
| `GET .../transactions/{transactionId}` | Response schema |

#### Migration Example

```java
// Before (v24.x)
UserFactorApi factorApi = new UserFactorApi(client);
List<UserFactor> factors = factorApi.listFactors(userId);
UserFactor factor = factors.get(0);
// Access _links.resend as previous type

// After (v25.0.0)
UserFactorApi factorApi = new UserFactorApi(client);
List<UserFactor> factors = factorApi.listFactors(userId);
UserFactor factor = factors.get(0);
// _links.resend property type has changed
// Review the new structure and update access patterns
```

---

### 4. Policy API Changes

**Impact Level**: 🟡 Medium

The `status` property type has changed in Policy and PolicyRule objects.

#### Affected Endpoints

| Endpoint | Change |
|----------|--------|
| `GET /api/v1/policies` | Response schema |
| `POST /api/v1/policies` | Request & Response schemas |
| `GET /api/v1/policies/{policyId}` | Response schema |
| `PUT /api/v1/policies/{policyId}` | Request & Response schemas |
| `POST /api/v1/policies/{policyId}/clone` | Response schema |
| `GET .../rules` | Response schema |
| `POST .../rules` | Request & Response schemas |
| `GET .../rules/{ruleId}` | Response schema |
| `PUT .../rules/{ruleId}` | Request & Response schemas |

#### Migration Example

```java
// Before (v24.x)
PolicyApi policyApi = new PolicyApi(client);
Policy policy = policyApi.getPolicy(policyId, null);
String status = policy.getStatus(); // May have been different type

// After (v25.0.0)
PolicyApi policyApi = new PolicyApi(client);
Policy policy = policyApi.getPolicy(policyId, null);
PolicyStatus status = policy.getStatus(); // Now uses enum type
```

---

### 5. Identity Provider API Changes

**Impact Level**: 🟡 Medium

The `protocol` property changed from a simple type to an object.

#### Affected Endpoints

| Endpoint | Change |
|----------|--------|
| `GET /api/v1/idps` | Response schema |
| `POST /api/v1/idps` | Request & Response schemas |
| `GET /api/v1/idps/{idpId}` | Response schema |
| `PUT /api/v1/idps/{idpId}` | Request & Response schemas |
| `POST .../lifecycle/activate` | Response schema |
| `POST .../lifecycle/deactivate` | Response schema |
| `GET /api/v1/users/{userId}/idps` | Response schema |

#### Migration Example

```java
// Before (v24.x)
IdentityProviderApi idpApi = new IdentityProviderApi(client);
IdentityProvider idp = idpApi.getIdentityProvider(idpId);
// protocol was a simple type

// After (v25.0.0)
IdentityProviderApi idpApi = new IdentityProviderApi(client);
IdentityProvider idp = idpApi.getIdentityProvider(idpId);
Protocol protocol = idp.getProtocol(); // Now an object with sub-properties
String protocolType = protocol.getType();
```

---

### 6. Role Assignment API Changes

**Impact Level**: 🟡 Medium

Role assignment responses now use polymorphic types handled by custom deserializers.

#### Affected Endpoints

| Endpoint | Model Used |
|----------|------------|
| `GET /api/v1/groups/{groupId}/roles` | `ListGroupAssignedRoles200ResponseInner` |
| `POST /api/v1/groups/{groupId}/roles` | `AssignRoleToGroup200Response` |
| `GET /api/v1/users/{userId}/roles` | Uses polymorphic response |
| `POST /api/v1/users/{userId}/roles` | `AssignRoleToUser201Response` |
| `GET /oauth2/v1/clients/{clientId}/roles` | Uses polymorphic response |
| `POST /oauth2/v1/clients/{clientId}/roles` | `AssignRoleToClient200Response` |

#### Migration Example

```java
// v25.0.0 - Works automatically with custom deserializers
RoleAssignmentApi roleApi = new RoleAssignmentApi(client);
List<ListGroupAssignedRoles200ResponseInner> roles = 
    roleApi.listGroupAssignedRoles(groupId, null);

for (ListGroupAssignedRoles200ResponseInner role : roles) {
    System.out.println("Role Type: " + role.getType());
    System.out.println("Assignment Type: " + role.getAssignmentType());
    System.out.println("Status: " + role.getStatus());
}
```

---

## API-Specific Migration

### User APIs

#### User Lifecycle Operations

```java
// v25.0.0
UserApi userApi = new UserApi(client);

// Create user
User newUser = new User();
newUser.setProfile(profile);
User created = userApi.createUser(newUser, true, false, null);

// Activate user
userApi.activateUser(userId, true);

// Deactivate user
userApi.deactivateUser(userId, false);

// Suspend user
userApi.suspendUser(userId);

// Unsuspend user
userApi.unsuspendUser(userId);
```

#### User Credentials

```java
// v25.0.0
UserApi userApi = new UserApi(client);

// Change password
ChangePasswordRequest request = new ChangePasswordRequest();
request.setOldPassword(new PasswordCredential().value("oldPassword"));
request.setNewPassword(new PasswordCredential().value("newPassword"));
userApi.changePassword(userId, request, true);

// Expire password
userApi.expirePassword(userId);

// Reset password
ResetPasswordToken token = userApi.resetPassword(userId, true);
```

### Application APIs

#### Application SSO Credentials

```java
// v25.0.0
ApplicationCredentialsApi credApi = new ApplicationCredentialsApi(client);

// List JWKs
List<ListJwk200ResponseInner> jwks = credApi.listJwks(appId);
for (ListJwk200ResponseInner jwk : jwks) {
    System.out.println("Key ID: " + jwk.getKid());
    System.out.println("Key Type: " + jwk.getKty());
    System.out.println("Use: " + jwk.getUse()); // "sig" or "enc"
}
```

### Group APIs

#### Group Owner Management

```java
// v25.0.0
GroupOwnerApi ownerApi = new GroupOwnerApi(client);

// Assign owner
AssignGroupOwnerRequestBody request = new AssignGroupOwnerRequestBody();
request.setId(userId);
request.setType(GroupOwnerType.USER);
GroupOwner owner = ownerApi.assignGroupOwner(groupId, request);

// List owners
List<GroupOwner> owners = ownerApi.listGroupOwners(groupId, null, null, null);
```

### User Risk APIs (New in v25.0.0)

```java
// v25.0.0 - New API
UserRiskApi riskApi = new UserRiskApi(client);

// Get user risk level
UserRisk risk = riskApi.getUserRisk(userId);
System.out.println("Risk Level: " + risk.getLevel());

// Update user risk level
UserRisk updateRequest = new UserRisk();
updateRequest.setLevel(RiskLevel.LOW);
UserRisk updated = riskApi.updateUserRisk(userId, updateRequest);
```

---

## Code Examples

### Before and After Comparison

#### Example 1: Listing Users

```java
// Before (v24.x)
UserApi userApi = new UserApi(client);
List<User> users = userApi.listUsers(null, null, 200, null, null, null, null);
for (User user : users) {
    UserType type = user.getType();
    if (type != null) {
        System.out.println("Type: " + type.getName());
    }
}

// After (v25.0.0)
UserApi userApi = new UserApi(client);
List<User> users = userApi.listUsers(null, null, 200, null, null, null, null);
for (User user : users) {
    // Type information may not be directly available
    // Use UserTypeApi if type information is needed
    System.out.println("User: " + user.getProfile().getLogin());
}
```

#### Example 2: Working with Factors

```java
// Before (v24.x)
UserFactorApi factorApi = new UserFactorApi(client);
UserFactor factor = factorApi.enrollFactor(userId, factorRequest, true, null, null, false);
// Access resend links directly

// After (v25.0.0)
UserFactorApi factorApi = new UserFactorApi(client);
UserFactor factor = factorApi.enrollFactor(userId, factorRequest, true, null, null, false);
// Review the new _links structure for resend information
```

#### Example 3: Policy Management

```java
// Before (v24.x)
PolicyApi policyApi = new PolicyApi(client);
Policy policy = policyApi.getPolicy(policyId, null);
String status = policy.getStatus().toString();

// After (v25.0.0)
PolicyApi policyApi = new PolicyApi(client);
Policy policy = policyApi.getPolicy(policyId, null);
PolicyStatus status = policy.getStatus();
String statusValue = status.getValue(); // Or use enum directly
```

---

## New Features

### 1. Custom Deserializers

The SDK now includes 9 custom deserializers for proper handling of polymorphic API responses:

| Deserializer | Purpose |
|-------------|---------|
| `RoleAssignmentDeserializer` | StandardRole/CustomRole polymorphism |
| `AssignRoleToGroupResponseDeserializer` | Group role assignments |
| `AssignRoleToUserResponseDeserializer` | User role assignments |
| `AssignRoleToClientResponseDeserializer` | Client role assignments |
| `JwkResponseDeserializer` | JWK signing/encryption keys |
| `GroupOwnerDeserializer` | Non-ISO date format handling |
| `FlexibleOffsetDateTimeDeserializer` | Global flexible date parsing |

### 2. New API Endpoints

Over 70 new endpoints added, including:

- Device integrations and posture checks
- Governance bundles
- User authenticator enrollments
- User classification
- User risk management
- Service accounts
- And more...

### 3. Enhanced Test Coverage

35 comprehensive integration test suites covering all major Okta APIs.

### 4. Improved Pagination with PagedIterable

The SDK now provides a new `PagedIterable<T>` class for automatic, thread-safe pagination. This replaces the deprecated `PaginationUtil.getAfter()` method.

#### Why PagedIterable?

| Feature | Old (PaginationUtil) | New (PagedIterable) |
|---------|---------------------|---------------------|
| Thread Safety | ❌ Shared state issues | ✅ Isolated iterator state |
| Memory Efficiency | ❌ Manual collection building | ✅ Lazy loading |
| Code Simplicity | ❌ Manual loop with `do-while` | ✅ Simple `for-each` |
| Error Prone | ❌ Easy to forget null checks | ✅ Handles edge cases |

#### Before (v24.x) - Deprecated Approach

```java
// Old approach using PaginationUtil (DEPRECATED)
UserApi userApi = new UserApi(client);
List<User> allUsers = new ArrayList<>();
String after = null;

do {
    List<User> page = userApi.listUsers("application/json", null, after, 200, null, null, null, null);
    allUsers.addAll(page);
    after = PaginationUtil.getAfter(userApi.getApiClient());  // Deprecated!
} while (StringUtils.isNotBlank(after));
```

#### After (v25.0.0) - Recommended Approach

```java
// New approach using PagedIterable (RECOMMENDED)
UserApi userApi = new UserApi(client);

// Option 1: Iterate directly (lazy loading, memory efficient)
PagedIterable<User> users = userApi.listUsersPagedIterable(null, null, 200, null, null, null, null);
for (User user : users) {
    System.out.println("User: " + user.getProfile().getLogin());
}

// Option 2: Collect all to a list (if you need all items)
List<User> allUsers = new ArrayList<>();
for (User user : userApi.listUsersPagedIterable(null, null, 200, null, null, null, null)) {
    allUsers.add(user);
}

// Option 3: Using Java Streams (requires StreamSupport)
import java.util.stream.StreamSupport;
List<User> filteredUsers = StreamSupport.stream(users.spliterator(), false)
    .filter(user -> user.getProfile().getEmail().endsWith("@example.com"))
    .collect(Collectors.toList());
```

#### Key Benefits

1. **Thread-Safe**: Each call to `iterator()` creates a new, independent `PagedIterator` instance
2. **Lazy Loading**: Pages are fetched only when needed, reducing memory footprint
3. **Simple API**: Works naturally with Java for-each loops and streams
4. **No Manual State Management**: No need to track `after` cursors

#### Migration Notes

- `PaginationUtil.getAfter()` is marked `@Deprecated(forRemoval = true, since = "24.1.0")`
- Look for methods ending with `PagedIterable` suffix for paginated operations
- The underlying API calls are the same, only the iteration mechanism changes

---

## Deprecated Features

### Deprecated Pagination Utility

The `PaginationUtil.getAfter()` method is deprecated and will be removed in a future release.

| Method | Status | Replacement |
|--------|--------|-------------|
| `PaginationUtil.getAfter(ApiClient)` | `@Deprecated(forRemoval = true, since = "24.1.0")` | `PagedIterable<T>` |

**Migration Required**: Replace manual pagination loops with `PagedIterable`. See [Improved Pagination](#4-improved-pagination-with-pagediterable) above.

### Deprecated Endpoints

The following endpoints are deprecated and will be removed in a future release:

| Endpoint | Note |
|----------|------|
| `POST /api/v1/org/privacy/oktaSupport/extend` | Returns 301 redirect |
| `POST /api/v1/org/privacy/oktaSupport/grant` | Returns 301 redirect |
| `POST /api/v1/org/privacy/oktaSupport/revoke` | Returns 301 redirect |
| `POST /api/v1/risk/events/ip` | Deprecated |
| `GET/POST/DELETE /api/v1/risk/providers` | Deprecated |
| `GET/PUT /api/v1/risk/providers/{riskProviderId}` | Deprecated |

**Action Required**: Update code to handle 301 redirects for `oktaSupport` endpoints.

---

## FAQ

### Q: Do I need to update all my code at once?

A: No. Most existing code will continue to work. Focus on:
1. Code that accesses `user.type` properties
2. Code that handles specific response structures from affected endpoints

### Q: Will the custom deserializers work automatically?

A: Yes. When you use `DefaultClientBuilder` to create the API client, all custom deserializers are automatically registered.

### Q: How do I handle the removed `user.type` property?

A: Use the `UserTypeApi` to fetch user type information separately:

```java
UserTypeApi userTypeApi = new UserTypeApi(client);
List<UserType> types = userTypeApi.listUserTypes();
```

### Q: What if I encounter deserialization errors?

A: The custom deserializers should handle most cases. If you encounter errors, please [open an issue](https://github.com/okta/okta-sdk-java/issues) with the JSON response that caused the error.

---

## Getting Help

### Resources

- [API Reference](https://developer.okta.com/docs/api/) - Official API documentation
- [GitHub Issues](https://github.com/okta/okta-sdk-java/issues) - Report bugs or request features
- [Developer Forum](https://devforum.okta.com) - Community support
- [MIGRATION-v25.0.0.md](MIGRATION-v25.0.0.md) - Detailed breaking change documentation

### Contact

If you encounter issues during migration:

1. Check the [GitHub Issues](https://github.com/okta/okta-sdk-java/issues) for similar problems
2. Review the [examples/quickstart](examples/quickstart) directory for working code samples
3. Post on the [Developer Forum](https://devforum.okta.com) for community help
4. Open a new GitHub issue with:
   - SDK version
   - Java version
   - Error message/stack trace
   - Relevant code snippet
