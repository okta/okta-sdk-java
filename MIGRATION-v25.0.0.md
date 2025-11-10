
-----

## 🚨 Migration Alert: Okta Admin Management API Update

This document outlines the changes for the Okta Admin Management API. This update includes **significant breaking changes** that require your immediate attention, as well as new features and deprecations.

The API changes **broke backward compatibility**. Please review the "Breaking Changes" section carefully to update your integration.

-----

### 1\. 🚨 Breaking Changes (Action Required)

This update includes numerous schema and response changes that are not backward-compatible. Any application logic that relies on the specific structure of the old request or response bodies for these endpoints will fail.

#### Key Breaking Change: User Object Schema

The structure of the `User` object has changed. Many endpoints now **no longer include** the `type` object and its sub-properties (`type.created`, `type.createdBy`, `type.default`, etc.) in the response.

This change affects all endpoints that return a `User` or list of `User` objects, including but not limited to:

* `GET /api/v1/devices`
* `GET /api/v1/devices/{deviceId}/users`
* `GET /api/v1/groups/{groupId}/users`
* `GET /api/v1/users`
* `POST /api/v1/users`
* `GET /api/v1/users/{id}`
* `PUT /api/v1/users/{id}`
* `POST /api/v1/users/{id}`
* `POST /api/v1/users/{userId}/lifecycle/expire_password`
* `POST /api/v1/users/{userId}/lifecycle/expire_password_with_temp_password`

**Action Required:** Audit your code for any dependencies on `user.type` properties and remove them.

-----

#### Schema and Endpoint-Specific Breaking Changes

Below are other endpoints with breaking changes, grouped by resource.

\<details\>
\<summary\>\<strong\>Authenticator Endpoints (\<code\>/api/v1/authenticators\</code\>)\</strong\>\</summary\>

* **Endpoints:**
    * `GET /api/v1/authenticators`
    * `POST /api/v1/authenticators`
    * `GET /api/v1/authenticators/{authenticatorId}`
    * `PUT /api/v1/authenticators/{authenticatorId}`
    * `POST /api/v1/authenticators/{authenticatorId}/lifecycle/activate`
    * `POST /api/v1/authenticators/{authenticatorId}/lifecycle/deactivate`
    * `GET /api/v1/authenticators/{authenticatorId}/methods`
    * `GET /api/v1/authenticators/{authenticatorId}/methods/{methodType}`
    * `PUT /api/v1/authenticators/{authenticatorId}/methods/{methodType}`
    * `POST /api/v1/authenticators/{authenticatorId}/methods/{methodType}/lifecycle/activate`
    * `POST /api/v1/authenticators/{authenticatorId}/methods/{methodType}/lifecycle/deactivate`
* **Change:** The response schemas for these endpoints have broken compatibility. Review the new API specification for the updated `Authenticator` and `AuthenticatorMethod` models.

\</details\>

\<details\>
\<summary\>\<strong\>User Factor Endpoints (\<code\>/api/v1/users/{userId}/factors\</code\>)\</strong\>\</summary\>

* **Endpoints:**
    * `GET /api/v1/users/{userId}/factors` (Response schema)
    * `POST /api/v1/users/{userId}/factors` (Request & Response schemas)
    * `GET /api/v1/users/{userId}/factors/catalog` (Response schema)
    * `GET /api/v1/users/{userId}/factors/{factorId}` (Response schema)
    * `POST /api/v1/users/{userId}/factors/{factorId}/lifecycle/activate` (Request schema)
    * `POST /api/v1/users/{userId}/factors/{factorId}/verify` (Request & Response schemas)
    * `GET /api/v1/users/{userId}/factors/{factorId}/transactions/{transactionId}` (Response schema)
* **Change:** The request and/or response schemas for these endpoints have broken compatibility. Notably, the `_links.resend` property type has changed.

\</details\>

\<details\>
\<summary\>\<strong\>Policy Endpoints (\<code\>/api/v1/policies\</code\>)\</strong\>\</summary\>

* **Endpoints:**
    * `GET /api/v1/policies`
    * `POST /api/v1/policies`
    * `GET /api/v1/policies/{policyId}`
    * `PUT /api/v1/policies/{policyId}`
    * `POST /api/v1/policies/{policyId}/clone`
    * `GET /api/v1/policies/{policyId}/rules`
    * `POST /api/v1/policies/{policyId}/rules`
    * `GET /api/v1/policies/{policyId}/rules/{ruleId}`
    * `PUT /api/v1/policies/{policyId}/rules/{ruleId}`
* **Change:** The `status` property type has changed in the request/response for all policy and policy rule endpoints.

\</details\>

\<details\>
\<summary\>\<strong\>Identity Provider (IdP) Endpoints (\<code\>/api/v1/idps\</code\>)\</strong\>\</summary\>

* **Endpoints:**
    * `GET /api/v1/idps`
    * `POST /api/v1/idps`
    * `GET /api/v1/idps/{idpId}`
    * `PUT /api/v1/idps/{idpId}`
    * `POST /api/v1/idps/{idpId}/lifecycle/activate`
    * `POST /api/v1/idps/{idpId}/lifecycle/deactivate`
    * `GET /api/v1/users/{userId}/idps`
* **Change:** The `protocol` property in the request/response for IdP objects has changed from a simple type to an object, breaking schema compatibility.

\</details\>

\<details\>
\<summary\>\<strong\>Resource Set & IAM Endpoints (\<code\>/api/v1/iam\</code\>)\</strong\>\</summary\>

* **Endpoints:**
    * `GET /api/v1/iam/assignees/users`
    * `GET /api/v1/iam/resource-sets`
    * `POST /api/v1/iam/resource-sets`
    * `GET /api/v1/iam/resource-sets/{resourceSetIdOrLabel}`
    * `PUT /api/v1/iam/resource-sets/{resourceSetIdOrLabel}`
    * `GET /api/v1/iam/resource-sets/{resourceSetIdOrLabel}/bindings` (Missing `_links.bindings`)
    * `POST /api/v1/iam/resource-sets/{resourceSetIdOrLabel}/bindings` (Missing `id`)
    * `GET /api/v1/iam/resource-sets/{resourceSetIdOrLabel}/bindings/{roleIdOrLabel}` (Missing `_links.bindings`)
    * `PATCH /api/v1/iam/resource-sets/{resourceSetIdOrLabel}/bindings/{roleIdOrLabel}/members` (Missing `id`)
    * `GET /api/v1/iam/resource-sets/{resourceSetIdOrLabel}/bindings/{roleIdOrLabel}/members`
    * `PATCH /api/v1/iam/resource-sets/{resourceSetIdOrLabel}/resources`
    * `POST /api/v1/iam/roles`
    * `GET /api/v1/iam/roles/{roleIdOrLabel}/permissions/{permissionType}`
    * `PUT /api/v1/iam/roles/{roleIdOrLabel}/permissions/{permissionType}`
    * `GET /api/v1/iam/roles/{roleIdOrLabel}/permissions`
* **Change:** Numerous endpoints related to IAM, roles, and resource sets have response schema changes, including missing properties and type changes.

\</details\>

\<details\>
\<summary\>\<strong\>Inline Hook Endpoints (\<code\>/api/v1/inlineHooks\</code\>)\</strong\>\</summary\>

* **Endpoints:**
    * `GET /api/v1/inlineHooks` (Missing `[n].metadata`)
    * `POST /api/v1/inlineHooks` (Missing `metadata`, `status`, `_links`)
    * `GET /api/v1/inlineHooks/{inlineHookId}` (Missing `metadata`)
    * `PUT /api/v1/inlineHooks/{inlineHookId}` (Missing `metadata`, `status`, `type`, `_links`)
    * `POST /api/v1/inlineHooks/{inlineHookId}` (Missing `metadata`, `status`, `type`, `_links`)
    * `POST /api/v1/inlineHooks/{inlineHookId}/execute`
    * `POST /api/v1/inlineHooks/{inlineHookId}/lifecycle/activate` (Missing `metadata`)
    * `POST /api/v1/inlineHooks/{inlineHookId}/lifecycle/deactivate` (Missing `metadata`)
* **Change:** The request and response schemas for inline hooks have had multiple properties removed (see notes above), which will break integrations.

\</details\>

\<details\>
\<summary\>\<strong\>Schema Endpoints (\<code\>/api/v1/meta/schemas\</code\>)\</strong\>\</summary\>

* **Endpoints:**
    * `GET /api/v1/meta/schemas/apps/{appId}/default`
    * `POST /api/v1/meta/schemas/apps/{appId}/default`
    * `GET /api/v1/meta/schemas/group/default`
    * `POST /api/v1/meta/schemas/group/default`
    * `GET /api/v1/meta/schemas/user/{schemaId}`
    * `POST /api/v1/meta/schemas/user/{schemaId}`
* **Change:** The schemas returned by these endpoints have breaking changes. Many properties (e.g., `costCenter`, `countryCode`, `department`) have had their `enum` and `unique` property types changed.

\</details\>

\<details\>
\<summary\>\<strong\>App JWKS Endpoints (\<code\>/api/v1/apps/.../jwks\</code\>)\</strong\>\</summary\>

* **Endpoints:**
    * `GET /api/v1/apps/{appId}/credentials/jwks`
    * `POST /api/v1/apps/{appId}/credentials/jwks`
    * `GET /api/v1/apps/{appId}/credentials/jwks/{keyId}`
    * `POST /api/v1/apps/{appId}/credentials/jwks/{keyId}/lifecycle/activate`
    * `POST /api/v1/apps/{appId}/credentials/jwks/{keyId}/lifecycle/deactivate`
* **Change:** The response schemas for these endpoints have broken compatibility.

\</details\>

\<details\>
\<summary\>\<strong\>Role Endpoints (\<code\>/api/v1/groups/.../roles\</code\>, \<code\>/api/v1/users/.../roles\</code\>, \<code\>/oauth2/.../roles\</code\>)\</strong\>\</summary\>

* **Endpoints:**
    * `GET /api/v1/groups/{groupId}/roles`
    * `POST /api/v1/groups/{groupId}/roles`
    * `GET /api/v1/groups/{groupId}/roles/{roleAssignmentId}`
    * `GET /api/v1/users/{userId}/roles`
    * `POST /api/v1/users/{userId}/roles`
    * `GET /api/v1/users/{userId}/roles/{roleAssignmentId}`
    * `GET /oauth2/v1/clients/{clientId}/roles`
    * `POST /oauth2/v1/clients/{clientId}/roles`
    * `GET /oauth2/v1/clients/{clientId}/roles/{roleAssignmentId}`
* **Change:** The response schemas for role assignments have broken compatibility.

\</details\>

\<details\>
\<summary\>\<strong\>Miscellaneous Breaking Changes\</strong\>\</summary\>

* **`GET /.well-known/okta-organization`**: Broken compatibility. The `settings` (object) property is missing from the response.
* **`POST /api/v1/apps` & `PUT /api/v1/apps/{appId}`**: The request body schema has broken compatibility.
* **`POST /api/v1/authorizationServers/{authServerId}/policies` & `PUT /api/v1/authorizationServers/{authServerId}/policies/{policyId}`**: The request body schema has broken compatibility.
* **`GET /api/v1/behaviors`, `POST /api/v1/behaviors`, `GET /api/v1/behaviors/{behaviorId}`, `PUT /api/v1/behaviors/{behaviorId}`**: Response schemas have property type changes for `created` and `lastUpdated`.
* **`GET /api/v1/logs`**: The response schema has broken compatibility.
* **`POST /api/v1/meta/types/user`**: The request schema has broken compatibility.
* **`GET /api/v1/org/factors/yubikey_token/tokens`**: Response schema `_links.resend` property type has changed.
* **`GET /api/v1/principal-rate-limits`**: All endpoints for principal rate limits have broken response schemas.
* **`POST /api/v1/users/{userId}/credentials/change_recovery_question`**: Request schema has broken compatibility.
* **`POST /api/v1/users/{userId}/credentials/forgot_password_recovery_question`**: Request schema has broken compatibility.
* **`POST /webauthn-registration/api/v1/initiate-fulfillment-request`**: Request schema has a property type change for `fulfillmentData`.
* **`GET /.well-known/app-authenticator-configuration`**: Response schema has broken compatibility.

\</details\>

-----

### 2\. ⚠️ Deprecated Endpoints

The following endpoints are now deprecated and will be removed in a future release.

* `POST /api/v1/org/privacy/oktaSupport/extend`
* `POST /api/v1/org/privacy/oktaSupport/grant`
* `POST /api/v1/org/privacy/oktaSupport/revoke`
    * **Note:** These three `oktaSupport` endpoints have also been **changed**. They no longer return a `200 OK` and instead return a **`301 Moved Permanently`**. You must update your client to handle this redirect.
* `POST /api/v1/risk/events/ip`
* `GET /api/v1/risk/providers`
* `POST /api/v1/risk/providers`
* `DELETE /api/v1/risk/providers/{riskProviderId}`
* `GET /api/v1/risk/providers/{riskProviderId}`
* `PUT /api/v1/risk/providers/{riskProviderId}`

**Action Required:** Plan to migrate away from these endpoints. Update any logic calling the `oktaSupport` endpoints to handle the new `301` response.

-----

### 3\. ✨ New Endpoints & Features

A large number of new endpoints have been added to the API.

\<details\>
\<summary\>\<strong\>Click to view all new endpoints\</strong\>\</summary\>

* `GET /.well-known/apple-app-site-association`
* `GET /.well-known/assetlinks.json`
* `GET /.well-known/webauthn`
* `GET /api/v1/apps/{appId}/connections/default/jwks`
* `GET /api/v1/apps/{appId}/cwo/connections`
* `POST /api/v1/apps/{appId}/cwo/connections`
* `GET /api/v1/apps/{appId}/cwo/connections/{connectionId}`
* `DELETE /api/v1/apps/{appId}/cwo/connections/{connectionId}`
* `PATCH /api/v1/apps/{appId}/cwo/connections/{connectionId}`
* `GET /api/v1/apps/{appId}/federated-claims`
* `POST /api/v1/apps/{appId}/federated-claims`
* `GET /api/v1/apps/{appId}/federated-claims/{claimId}`
* `PUT /api/v1/apps/{appId}/federated-claims/{claimId}`
* `DELETE /api/v1/apps/{appId}/federated-claims/{claimId}`
* `GET /api/v1/apps/{appId}/group-push/mappings`
* `POST /api/v1/apps/{appId}/group-push/mappings`
* `GET /api/v1/apps/{appId}/group-push/mappings/{mappingId}`
* `DELETE /api/v1/apps/{appId}/group-push/mappings/{mappingId}`
* `PATCH /api/v1/apps/{appId}/group-push/mappings/{mappingId}`
* `GET /api/v1/authenticators/{authenticatorId}/aaguids`
* `POST /api/v1/authenticators/{authenticatorId}/aaguids`
* `GET /api/v1/authenticators/{authenticatorId}/aaguids/{aaguid}`
* `PUT /api/v1/authenticators/{authenticatorId}/aaguids/{aaguid}`
* `DELETE /api/v1/authenticators/{authenticatorId}/aaguids/{aaguid}`
* `PATCH /api/v1/authenticators/{authenticatorId}/aaguids/{aaguid}`
* `GET /api/v1/authorizationServers/{authServerId}/resourceservercredentials/keys`
* `POST /api/v1/authorizationServers/{authServerId}/resourceservercredentials/keys`
* `GET /api/v1/authorizationServers/{authServerId}/resourceservercredentials/keys/{keyId}`
* `DELETE /api/v1/authorizationServers/{authServerId}/resourceservercredentials/keys/{keyId}`
* `POST /api/v1/authorizationServers/{authServerId}/resourceservercredentials/keys/{keyId}/lifecycle/activate`
* `POST /api/v1/authorizationServers/{authServerId}/resourceservercredentials/keys/{keyId}/lifecycle/deactivate`
* `GET /api/v1/brands/{brandId}/well-known-uris`
* `GET /api/v1/brands/{brandId}/well-known-uris/{path}`
* `GET /api/v1/brands/{brandId}/well-known-uris/{path}/customized`
* `PUT /api/v1/brands/{brandId}/well-known-uris/{path}/customized`
* `GET /api/v1/device-integrations`
* `GET /api/v1/device-integrations/{deviceIntegrationId}`
* `POST /api/v1/device-integrations/{deviceIntegrationId}/lifecycle/activate`
* `POST /api/v1/device-integrations/{deviceIntegrationId}/lifecycle/deactivate`
* `GET /api/v1/device-posture-checks`
* `POST /api/v1/device-posture-checks`
* `GET /api/v1/device-posture-checks/default`
* `GET /api/v1/device-posture-checks/{postureCheckId}`
* `PUT /api/v1/device-posture-checks/{postureCheckId}`
* `DELETE /api/v1/device-posture-checks/{postureCheckId}`
* `GET /api/v1/iam/governance/bundles`
* `POST /api/v1/iam/governance/bundles`
* `GET /api/v1/iam/governance/bundles/{bundleId}`
* `PUT /api/v1/iam/governance/bundles/{bundleId}`
* `DELETE /api/v1/iam/governance/bundles/{bundleId}`
* `GET /api/v1/iam/governance/bundles/{bundleId}/entitlements`
* `GET /api/v1/iam/governance/bundles/{bundleId}/entitlements/{entitlementId}/values`
* `GET /api/v1/iam/governance/optIn`
* `POST /api/v1/iam/governance/optIn`
* `POST /api/v1/iam/governance/optOut`
* `GET /api/v1/idps/{idpId}/credentials/keys/active`
* `GET /api/v1/org/privacy/oktaSupport/cases`
* `PATCH /api/v1/org/privacy/oktaSupport/cases/{caseNumber}`
* `GET /api/v1/org/settings/autoAssignAdminAppSetting`
* `POST /api/v1/org/settings/autoAssignAdminAppSetting`
* `GET /api/v1/ssf/stream/status`
* `POST /api/v1/ssf/stream/verification`
* `GET /api/v1/users/{userId}/authenticator-enrollments`
* `POST /api/v1/users/{userId}/authenticator-enrollments/phone`
* `POST /api/v1/users/{userId}/authenticator-enrollments/tac`
* `GET /api/v1/users/{userId}/authenticator-enrollments/{enrollmentId}`
* `DELETE /api/v1/users/{userId}/authenticator-enrollments/{enrollmentId}`
* `GET /api/v1/users/{userId}/classification`
* `PUT /api/v1/users/{userId}/classification`
* `GET /api/v1/users/{userId}/devices`
* `GET /api/v1/users/{userId}/risk`
* `PUT /api/v1/users/{userId}/risk`
* `GET /api/v1/users/{userId}/roles/{roleIdOrEncodedRoleId}/targets`
* `GET /device-access/api/v1/desktop-mfa/enforce-number-matching-challenge-settings`
* `PUT /device-access/api/v1/desktop-mfa/enforce-number-matching-challenge-settings`
* `PUT /okta-personal-settings/api/v1/edit-feature`
* `GET /okta-personal-settings/api/v1/export-blocklists`
* `PUT /okta-personal-settings/api/v1/export-blocklists`
* `GET /privileged-access/api/v1/service-accounts`
* `POST /privileged-access/api/v1/service-accounts`
* `GET /privileged-access/api/v1/service-accounts/{id}`
* `DELETE /privileged-access/api/v1/service-accounts/{id}`
* `PATCH /privileged-access/api/v1/service-accounts/{id}`
* `POST /webauthn-registration/api/v1/users/{userId}/enrollments/{authenticatorEnrollmentId}/mark-error`
* `GET /api/v1/users/{userId}/roles/{roleAssignmentId}/governance`
* `GET /api/v1/users/{userId}/roles/{roleAssignmentId}/governance/{grantId}`
* `GET /api/v1/users/{userId}/roles/{roleAssignmentId}/governance/{grantId}/resources`
* `GET /api/v1/iam/resource-sets/{resourceSetIdOrLabel}/resources/{resourceId}`
* `PUT /api/v1/iam/resource-sets/{resourceSetIdOrLabel}/resources/{resourceId}`
* `POST /api/v1/iam/resource-sets/{resourceSetIdOrLabel}/resources`

\</details\>

-----

### 4\. 🔄 Other Backward-Compatible Changes

In addition to the changes listed above, **over 200 endpoints** received backward-compatible updates. These changes typically include:

* Adding new, optional parameters to a request.
* Adding new properties to a JSON response.
* Adding new response codes (e.g., `409 Conflict` on `POST /api/v1/ssf/stream`).
* Marking path or query parameters as changed (e.g., `poolId` in `GET /api/v1/agentPools/{poolId}/updates/settings`).

These changes are non-breaking, and your existing integrations will continue to work. However, we recommend reviewing the updated API specification for any endpoints you use to take advantage of new features or properties.