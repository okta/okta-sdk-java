# Release v25.0.1 - Bug Fixes and Cache Improvements

## � Bug Fixes
- **#1608**: Fixed DPoP nonce expiration not being checked on regular API calls (only checked during token requests), causing intermittent session errors after 22 hours- **#1615/#1667**: Fixed `LinksResend.resend` array type issue causing `MismatchedInputException`
- **#1618**: Fixed cache `ClassCastException` with type validation
- **#1619**: Fixed `OIDCApplicationBuilder` default name
- **#1622**: Fixed `expirePasswordWithTempPassword` return type ⚠️ **Breaking Change** - now returns `TempPassword` instead of `User`
- **#1642**: Added support for custom attributes in `OktaUserGroupProfile`
- **#1650**: Fixed `PasswordPolicyRule.equals()` to include parent attributes
- **#1653**: Added missing `rootSessionId` field to `LogAuthenticationContext`
- **#1600**: Implemented resource-specific cache configuration
- **#1657**: Upgraded Apache HttpClient5 to 5.5.1 (fixes connection pool leak)
- **#1666**: Fixed JUnit dependency scope

## 🔧 Cache System Improvements

- Multi-cache invalidation for nested resources
- Fixed path matching for `/federated-claims/` and `/group-push/mappings/`
- Cross-cache invalidation for lifecycle operations
- Defensive exception handling to prevent cache errors from masking API exceptions
- **Result**: All 431 integration tests passing

---

# Release v25.0.0 - Major SDK Refactoring and Enhanced Test Coverage

## 📋 Overview

This release introduces **Okta Java SDK v25.0.0**, a major release that significantly improves the SDK's architecture, test coverage, and developer experience. The release includes:

- **OpenAPI Spec Update**: Upgraded from v2024.08.3 to **v5.1.0** (+16,966 lines of API definitions)
- **49,132 lines added** and **7,926 lines removed** across **81 files**
- **58 commits** implementing comprehensive SDK improvements
- **35 integration test suites** covering all major Okta APIs
- **9 new custom deserializers** for proper polymorphic type handling

---

## 🎯 Key Highlights

### 📊 Impact Summary

| Metric | Value |
|--------|-------|
| Total Files Changed | 81 |
| Lines Added | 49,132 |
| Lines Removed | 7,926 |
| Net Lines | +41,206 |
| Total Commits | 58 |
| New Integration Test Files | 28 |
| Updated Integration Test Files | 7 |
| New Unit Test Files | 8 |
| New Deserializers | 9 |
| OpenAPI Spec Growth | +16,966 lines |

### Version Updates

| Component | Before | After |
|-----------|--------|-------|
| SDK Version | 24.x | **25.0.0** |
| OpenAPI Spec | 2024.08.3 | **5.1.0** |
| API YAML Lines | 66,987 | **83,953** |

---

## 🔴 Breaking Changes

### 1. User Object Schema Changes

The structure of the `User` object has changed significantly. The `type` object and its sub-properties are **no longer included** in API responses.

**Affected Endpoints:**
- `GET /api/v1/devices`
- `GET /api/v1/devices/{deviceId}/users`
- `GET /api/v1/groups/{groupId}/users`
- `GET /api/v1/users` and all user CRUD operations
- `POST /api/v1/users/{userId}/lifecycle/expire_password`

**Action Required:** Remove any dependencies on `user.type` properties in your code.

### 2. Authenticator Endpoints

**12 endpoints** affected with response schema changes:
- `GET/POST /api/v1/authenticators`
- `GET/PUT /api/v1/authenticators/{authenticatorId}`
- All lifecycle operations (`activate`, `deactivate`)
- Method-level operations

### 3. User Factor Endpoints

**7 endpoints** affected:
- `GET/POST /api/v1/users/{userId}/factors`
- Factor catalog, lifecycle, and verification operations
- Notable: `_links.resend` property type changed

### 4. Policy Endpoints

**9 endpoints** affected:
- All policy CRUD operations
- Policy rule operations
- `status` property type changed

### 5. Identity Provider (IdP) Endpoints

**7 endpoints** affected:
- All IdP CRUD operations
- `protocol` property changed from simple type to object

### 6. Role Assignment Endpoints

**9 endpoints** affected across groups, users, and OAuth clients:
- `GET/POST /api/v1/groups/{groupId}/roles`
- `GET/POST /api/v1/users/{userId}/roles`
- `GET/POST /oauth2/v1/clients/{clientId}/roles`

### 7. Additional Breaking Changes

- **Schema Endpoints**: Property type changes for `enum` and `unique` fields
- **App JWKS Endpoints**: 5 endpoints with schema changes
- **IAM/Resource Set Endpoints**: 16+ endpoints with response changes
- **Inline Hook Endpoints**: 8 endpoints with removed properties
- **Well-Known Endpoints**: Missing `settings` object

---

## 🆕 New Components

### Custom Deserializers (9 Total)

| Deserializer | Target Class | Purpose |
|-------------|--------------|---------|
| `RoleAssignmentDeserializer` | `ListGroupAssignedRoles200ResponseInner` | Handles StandardRole/CustomRole polymorphism |
| `AssignRoleToGroupResponseDeserializer` | `AssignRoleToGroup200Response` | Group role assignment responses |
| `AssignRoleToUserResponseDeserializer` | `AssignRoleToUser201Response` | User role assignment responses |
| `AssignRoleToClientResponseDeserializer` | `AssignRoleToClient200Response` | Client role assignment responses |
| `JwkResponseDeserializer` | `ListJwk200ResponseInner` | JWK signing/encryption key handling |
| `GroupOwnerDeserializer` | `GroupOwner` | Non-ISO-8601 date format handling |
| `FlexibleOffsetDateTimeDeserializer` | `OffsetDateTime` | Global flexible date parsing |
| `AbstractRoleAssignmentDeserializer` | Base class | Shared role assignment logic |
| `IgnoreTypeInfoMixIn` | Mix-in | Disables problematic @JsonTypeInfo |

### New Integration Test Suites (28 Total)

#### User Management APIs (12 Suites)
| Test Suite | Lines | Coverage |
|------------|-------|----------|
| `UserLifecycleIT.groovy` | 588 | Activate, Deactivate, Suspend, Unlock |
| `UserCredIT.groovy` | 527 | Password operations, recovery |
| `UserGrantIT.groovy` | 660 | User grants management |
| `UserOAuthIT.groovy` | 538 | OAuth token operations |
| `UserSessionsIT.groovy` | 428 | Session management |
| `UserLinkedObjectIT.groovy` | 553 | Linked objects operations |
| `UserResourcesIT.groovy` | 511 | App links, assigned apps |
| `UserAuthenticatorEnrollmentsIT.groovy` | 487 | Authenticator enrollments |
| `UserClassificationIT.groovy` | 223 | User classification |
| `UserRiskIT.groovy` | 205 | Risk level operations |
| `UserFactorIT.groovy` | 1,274 | Factor enrollment/verification |
| `UserTypeIT.groovy` | 597 | User type management |

#### Application APIs (9 Suites)
| Test Suite | Lines | Coverage |
|------------|-------|----------|
| `ApplicationGrantsIT.groovy` | 577 | OAuth grants for apps |
| `ApplicationLogosIT.groovy` | 490 | Logo upload/management |
| `ApplicationPoliciesIT.groovy` | 500 | App policy assignments |
| `ApplicationSSOIT.groovy` | 376 | SSO operations |
| `ApplicationSSOCredentialKeyIT.groovy` | 475 | SSO credential keys |
| `ApplicationSSOFederatedClaimIT.groovy` | 667 | Federated claims |
| `ApplicationTokensIT.groovy` | 308 | App token management |
| `ApplicationUsersIT.groovy` | 621 | App user assignments |
| `OktaApplicationSettingsIT.groovy` | 517 | App settings |

#### Organization & Settings APIs (7 Suites)
| Test Suite | Lines | Coverage |
|------------|-------|----------|
| `AgentPoolsIT.groovy` | 383 | Agent pool management |
| `ApiServiceIntegrationsIT.groovy` | 396 | Service integrations |
| `ApiTokenIT.groovy` | 362 | API token operations |
| `GroupPushMappingIT.groovy` | 736 | Group push mappings |
| `ProfileMappingIT.groovy` | 318 | Profile mappings |
| `RealmsIT.groovy` | 366 | Realm operations |
| `PolicyCleanupIT.groovy` | 114 | Policy cleanup utilities |

### New Unit Tests (8 Files)

| Test File | Location | Coverage |
|-----------|----------|----------|
| `RoleAssignmentDeserializerTest.java` | impl/src/test | 287 lines - Deserializer logic |
| `DPoPInterceptorTest.java` | impl/src/test | 379 lines - DPoP authentication |
| `RetryUtilTest.java` | impl/src/test | 134 lines - Retry utilities |
| `ApiExceptionHelperTest.java` | api/src/test | 64 lines - Exception handling |
| `HelperConstantsTest.java` | api/src/test | 64 lines - Constants |
| `PaginationUtilTest.java` | api/src/test | 295 lines - Pagination utilities |
| `DefaultCacheTest.groovy` | impl/src/test | Cache operations (updated) |
| `UrlResourceTest.groovy` | impl/src/test | URL resource handling (updated) |

### New Configuration Files

| File | Purpose |
|------|---------|
| `.mvn/jvm.config` | JVM settings for large YAML parsing (-Xmx2g, maxYamlCodePoints) |
| `.mvn/maven.config` | Maven configuration for SnakeYAML codepoint limits |

---

## 📊 Integration Test Coverage

### Total Integration Test Suites: 35

| Category | Count | Key Operations Tested |
|----------|-------|----------------------|
| User Management | 13 | CRUD, lifecycle, credentials, sessions, grants |
| Application | 10 | CRUD, SSO, policies, users, tokens, logos |
| Groups | 2 | CRUD, owners, members, push mappings |
| Identity Providers | 1 | CRUD, lifecycle, users |
| Policies | 2 | CRUD, rules, cleanup |
| Organization | 4 | Agent pools, API tokens, service integrations |
| Other | 3 | Realms, profile mappings, pagination |

### Test Enhancement Features

- **Retry Logic**: Added for flaky API operations (rate limiting, eventual consistency)
- **Cleanup Utilities**: Proper test isolation and resource cleanup
- **Map Parameter Variants**: Comprehensive coverage of API method overloads
- **Given-When-Then**: Consistent test naming conventions

---

## 📚 Documentation

### Migration Guides

| Document | Purpose |
|----------|---------|
| [MIGRATING.md](MIGRATING.md) | Comprehensive breaking change documentation and migration patterns |
| [README.md](README.md) | Release status, code examples |

---

## 🔧 Technical Improvements

### Deserializer Architecture

The SDK now includes a robust deserializer architecture for handling polymorphic API responses:

```java
// Mix-ins registered for polymorphic types
mapper.addMixIn(ListGroupAssignedRoles200ResponseInner.class, IgnoreTypeInfoMixIn.class);
mapper.addMixIn(AssignRoleToClient200Response.class, IgnoreTypeInfoMixIn.class);
mapper.addMixIn(AssignRoleToGroup200Response.class, IgnoreTypeInfoMixIn.class);
mapper.addMixIn(AssignRoleToUser201Response.class, IgnoreTypeInfoMixIn.class);
mapper.addMixIn(ListJwk200ResponseInner.class, IgnoreTypeInfoMixIn.class);

// Custom deserializers registered
SimpleModule module = new SimpleModule();
module.addDeserializer(ListGroupAssignedRoles200ResponseInner.class, new RoleAssignmentDeserializer());
module.addDeserializer(AssignRoleToClient200Response.class, new AssignRoleToClientResponseDeserializer());
module.addDeserializer(AssignRoleToGroup200Response.class, new AssignRoleToGroupResponseDeserializer());
module.addDeserializer(AssignRoleToUser201Response.class, new AssignRoleToUserResponseDeserializer());
module.addDeserializer(ListJwk200ResponseInner.class, new JwkResponseDeserializer());
module.addDeserializer(GroupOwner.class, new GroupOwnerDeserializer());
module.addDeserializer(OffsetDateTime.class, new FlexibleOffsetDateTimeDeserializer());
```

### DefaultClientBuilder Updates

- OAuth2 authentication handling improved with explicit casting
- Custom deserializers automatically registered
- Mix-ins applied for problematic polymorphic types
- Flexible date parsing for non-ISO-8601 formats

### Pagination Improvements

The SDK now provides `PagedIterable<T>` for automatic, thread-safe pagination:

```java
// New recommended approach - lazy, thread-safe pagination
UserApi userApi = new UserApi(client);
PagedIterable<User> users = userApi.listUsersPagedIterable(null, null, 200, null, null, null, null);
for (User user : users) {
    System.out.println("User: " + user.getProfile().getLogin());
}
```

**Benefits:**
- ✅ Thread-safe: Each iterator has isolated state
- ✅ Memory efficient: Lazy loading of pages
- ✅ Simple API: Works with for-each loops and streams
- ⚠️ `PaginationUtil.getAfter()` is deprecated

### Code Quality Improvements

- PMD violation fixes
- Improved error handling in tests
- Better test isolation and cleanup
- Enhanced retry logic for API operations

---

## 📈 Impact Analysis

### File Changes by Category

| Category | Files | Lines Added | Lines Removed |
|----------|-------|-------------|---------------|
| Integration Tests | 35 | ~15,000 | ~1,500 |
| Unit Tests | 8 | ~1,300 | ~100 |
| Deserializers | 9 | ~1,100 | 0 |
| Configuration | 8 | ~400 | ~50 |
| API Spec | 1 | ~29,934 | ~6,000 |
| Documentation | 3 | ~500 | ~50 |

### Major File Changes

| File | Changes |
|------|---------|
| `src/swagger/api.yaml` | +29,934 / -6,000 lines |
| `AppsIT.groovy` | +3,384 lines (major expansion) |
| `GroupsIT.groovy` | +2,355 lines |
| `IdpIT.groovy` | +1,607 lines |
| `UserFactorIT.groovy` | +1,274 lines (new) |
| `UsersIT.groovy` | +1,169 lines |
| `PoliciesIT.groovy` | +872 lines |

---

## ✅ Benefits

| Benefit | Description |
|---------|-------------|
| 🎯 **Better Type Safety** | Custom deserializers handle polymorphic types correctly |
| 📈 **Improved Test Coverage** | 35 integration test suites covering all major APIs |
| 📚 **Comprehensive Documentation** | Detailed migration guides with code examples |
| 🔧 **Enhanced Maintainability** | Cleaner test patterns and code organization |
| 🚀 **Updated API Support** | OpenAPI spec v5.1.0 with latest Okta APIs |
| 🔄 **Flexible Date Parsing** | Handles both ISO-8601 and custom date formats |
| ⚡ **Better Error Handling** | Improved retry logic and exception handling |

---

## 🔄 Migration Path

1. **Review** the [detailed migration guide](MIGRATING.md)
2. **Identify** code using changed endpoints (User, Authenticator, Factor, Policy, IdP)
3. **Update** any dependencies on removed `user.type` properties
4. **Test** thoroughly with the new SDK version
5. **Review** custom deserializer behavior for role assignments
6. **Update** error handling for new response codes

---

## 🧪 Testing

- ✅ All 35 API integration test suites passing
- ✅ 8 unit test classes updated and passing
- ✅ Code examples in README verified for v25.x compatibility
- ✅ Migration guide examples tested
- ✅ Custom deserializers validated with real API responses

---

## 📦 Version Updates

| Component | Old Version | New Version |
|-----------|-------------|-------------|
| SDK Version | 24.x | **25.0.0** |
| OpenAPI Spec | 2024.08.3 | **5.1.0** |
| API YAML | 66,987 lines | **83,953 lines** |

---

## 🔗 Related Links

- [Migration Guide](MIGRATING.md) - Breaking change details and migration patterns
- [Developer Forum](https://devforum.okta.com) - Community support
- [GitHub Issues](https://github.com/okta/okta-sdk-java/issues) - Report issues
- [API Reference](https://developer.okta.com/docs/api/) - Official API documentation

---

## 🙏 Acknowledgments

This release represents a significant effort to improve the Okta Java SDK's quality and maintainability. Special thanks to all contributors who helped with testing, documentation, and code reviews.

---

# Historical Releases

## v24.0.0 - v24.0.1
- OpenAPI spec version 2024.08.3
- Bug fixes and maintenance updates
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-24.0.0)

## v23.0.0 - v23.0.1
- API enhancements and bug fixes
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-23.0.0)

## v22.0.0 - v22.0.1
- API enhancements and bug fixes
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-22.0.0)

## v21.0.0
- API enhancements
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-21.0.0)

## v20.0.0 - v20.0.1
- API enhancements and bug fixes
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-20.0.0)

## v19.0.0 - v19.0.1
- API enhancements and bug fixes
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-19.0.0)

## v18.0.0
- API enhancements
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-18.0.0)

## v17.0.0
- API enhancements
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-17.0.0)

## v16.0.0
- API enhancements
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-16.0.0)

## v15.0.0
- API enhancements
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-15.0.0)

## v14.0.0
- API enhancements
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-14.0.0)

## v13.0.0 - v13.0.3
- API enhancements and bug fixes
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-13.0.0)

## v12.0.0 - v12.0.2
- API enhancements and bug fixes
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-12.0.0)

## v11.0.0 - v11.0.3
- Breaking changes with japicmp comparison available
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-11.0.0)
- [API Changes](https://github.com/okta/okta-sdk-java/blob/master/japicmp-11_0_0_vs_10_3_0.html)

## v10.0.0 - v10.3.0
- Major release with OpenAPI v3 specification
- New openapi-generator based code generation
- API client restructuring (see [Migration Guide](MIGRATING.md#migrating-from-8xx-to-10xx))
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-10.0.0)

## v8.0.0 - v8.2.5
- Bug fixes for Theme and OrgSetting file uploads
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-8.0.0)

## v7.0.0
- Added Brands and Themes API support
- New policy interfaces
- Identity Provider enhancements
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-7.0.0)

## v6.0.0
- Added Authenticator API support
- GroupSchema API
- Org settings enhancements
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-6.0.0)

## v5.0.0
- SAML application settings changes
- Authorization server policy changes
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-5.0.0)

## v4.0.0
- Added Roles and Domains API
- OpenIdConnect application settings changes
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-4.0.0)

## v3.0.0
- Configuration key updates
- Package reorganization
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-3.0.0)

## v2.0.0
- Minimum Java 8 requirement
- Package changes
- [Release Notes](https://github.com/okta/okta-sdk-java/releases/tag/okta-sdk-root-2.0.0)

## v1.x and earlier
- Initial releases
- See [GitHub Releases](https://github.com/okta/okta-sdk-java/releases) for full history
