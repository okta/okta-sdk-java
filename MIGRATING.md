# Okta Java Management SDK Migration Guide
 
This SDK uses semantic versioning and follows Okta's [library version policy](https://developer.okta.com/code/library-versions/). In short, we do not make breaking changes unless the major version changes!

## Migrating from 8.x.x to 9.0.0-beta

Version 9.0.0 contains models generated out of the v3 spec of Open API for Okta.

Below are the changes that comes with this v3 spec code regeneration:
- All resources have been grouped in a common root package `com.okta.sdk.resource`.
- All operations have moved to the `com.okta.sdk.client.Client` object. This means that, all
  API operations including CRUD operations will have to be invoked on the `Client` object.

## Migrating from 7.x.x to 8.0.0

Version 8.0.0 of this SDK introduces few breaking changes from previous versions.

### Package `com.okta.sdk.resource.brand.Theme`

Below methods will now take `file` object as additional param. This was missing from earlier release (bug) and this release fixes it.
- `updateBrandThemeBackgroundImage` 
- `updateBrandThemeFavicon`
- `uploadBrandThemeLogo`

### Package `com.okta.sdk.resource.org.OrgSetting`
- `updateOrgLogo` will now take `file` object as a parameter. This was missing from earlier release (bug) and this release fixes it. 

## Migrating from 6.x.x to 7.0.0

### Package `com.okta.sdk.client.Client`

Below methods have been added.
- `Brand getBrand(String brandId)`
- `ThemeResponse getBrandTheme(String brandId, String themeId)`
- `BrandList listBrands()`
- `ThemeResponseList listBrandThemes(String brandId)`

Below methods have been moved to `com.okta.sdk.resource.org.OrgSetting`.
- `getOktaCommunicationSettings()`
- `getOrgContactTypes()`
- `getOrgContactUser(String contactType)`
- `getOrgOktaSupportSettings()`
- `getOrgPreferences()`

### Package `com.okta.sdk.resource.user.User`

- `assignRole(AssignRoleRequest request, String disableNotifications)` changed signature to 
  `assignRole(AssignRoleRequest request, Boolean disableNotifications)`

### Package `com.okta.sdk.resource.policy`

New Interfaces have been introduced.
- `com.okta.sdk.resource.policy.VerificationMethod`
- `com.okta.sdk.resource.policy.UserTypeCondition`
- `com.okta.sdk.resource.policy.ProfileEnrollmentPolicyRuleProfileAttribute`
- `com.okta.sdk.resource.policy.ProfileEnrollmentPolicyRuleActivationRequirement`
- `com.okta.sdk.resource.policy.ProfileEnrollmentPolicyRuleActions`
- `com.okta.sdk.resource.policy.ProfileEnrollmentPolicyRuleAction`
- `com.okta.sdk.resource.policy.ProfileEnrollmentPolicyRule`
- `com.okta.sdk.resource.policy.ProfileEnrollmentPolicy`
- `com.okta.sdk.resource.policy.PreRegistrationInlineHook`
- `com.okta.sdk.resource.policy.PossessionConstraint`
- `com.okta.sdk.resource.policy.KnowledgeConstraint`
- `com.okta.sdk.resource.policy.DeviceAccessPolicyRuleCondition`
- `com.okta.sdk.resource.policy.AccessPolicyRuleCustomCondition`
- `com.okta.sdk.resource.policy.AccessPolicyRuleConditions`
- `com.okta.sdk.resource.policy.AccessPolicyRuleApplicationSignOn`
- `com.okta.sdk.resource.policy.AccessPolicyRuleActions`
- `com.okta.sdk.resource.policy.AccessPolicyRule`
- `com.okta.sdk.resource.policy.AccessPolicyConstraints`
- `com.okta.sdk.resource.policy.AccessPolicyConstraint`
- `com.okta.sdk.resource.policy.AccessPolicy`

### Package `com.okta.sdk.resource.identity.provider.IdentityProviderBuilders`

Identity Provider of type String can be constructed with the new method introduced.
- `ofType(java.lang.String)`

### Package `com.okta.sdk.resource.identity.provider.IdentityProvider`

- `setType(com.okta.sdk.resource.identity.provider.IdentityProvider$TypeEnum)` has changed signature to 
  `setType(String identityProvider)`.
- `getType()` would now return `String` instead of `com.okta.sdk.resource.identity.provider.IdentityProvider$TypeEnum` type.

### Package `com.okta.sdk.resource.brand`

New models related to Theme and Brands API have been introduced.
- `Theme`
- `Brand`
- `BrandList`
- `ThemeResponse`
- `ImageUploadResponse`
- `SignInPageTouchPointVariant`
- `ErrorPageTouchPointVariant`
- `EndUserDashboardTouchPointVariant`

### Package `com.okta.sdk.resource.authenticator`

New Interfaces have been introduced.
- `ChannelBinding`
- `Compliance`
- `AuthenticatorProviderConfigurationUserNamePlate`
- `AuthenticatorProviderConfiguration`
- `AuthenticatorProvider`

New methods have been added to `Authenticator` interface.
- `AuthenticatorProvider getProvider()`
- `setProvider(AuthenticatorProvider authenticationProvider)`
- `Authenticator update`

### Package `com.okta.sdk.resource.application`

New methods have been added to `SwaApplicationSettingsApplication` interface.
- `String getCheckbox`
- `String getRedirectUrl`
- `SwaApplicationSettingsApplication setCheckbox(String checkBox)`
- `SwaApplicationSettingsApplication setRedirectUrl(String redirectUrl)`

New method have been added to `OIDCApplicationBuilder` interface.
- `OIDCApplicationBuilder setPostLogoutRedirectUris(List uris)`

New methods have been added to `ApplicationCredentialsUsernameTemplate` interface.
- `String getPushStatus()`
- `ApplicationCredentialsUsernameTemplate setPushStatus(String pushStatus)`

## Migrating from 5.x.x to 6.0.0

Version 6.0.0 of this SDK introduces a number of breaking changes from previous versions.
In addition to many new classes/interfaces, some existing classes/interfaces are no longer backward compatible due to method renaming and signature changes.

### Package `com.okta.sdk.client.Client`

Below methods have been added.
- `Authenticator getAuthenticator(String authenticatorId)`
- `GroupSchema getGroupSchema()`
- `OrgOktaCommunicationSetting getOktaCommunicationSettings()`
- `OrgContactTypeObjList getOrgContactTypes()`
- `OrgContactUser getOrgContactUser(String contactType)`
- `OrgOktaSupportSettingsObj getOrgOktaSupportSettings()`
- `OrgPreferences getOrgPreferences()`
- `OrgSetting getOrgSettings()`
- `AuthenticatorList listAuthenticators()`
- `GroupSchema updateGroupSchema()`
- `GroupSchema updateGroupSchema(GroupSchema body)`

Below method has undergone a signature change.
- `DomainList listDomains()` signature changed to `DomainListResponse listDomains()`

### Package `com.okta.sdk.resource.application.ApplicationVisibility`

Below methods have been added.
- `Boolean getAutoLaunch()`
- `ApplicationVisibility setAutoLaunch(Boolean autoLaunch)`

### Package `com.okta.sdk.resource.authenticator.AllowedForEnum`

This is a newly created enum with fields listed below.
- `ANY("ANY")`
- `NONE("NONE")`
- `RECOVERY("RECOVERY")`
- `SSO("SSO")`

### Package `com.okta.sdk.resource.authenticator.Authenticator`

This is a newly created interface with methods listed below.

- `Authenticator activate()`
- `Authenticator deactivate()`
- `Date getCreated()`
- `String getId()`
- `String getKey()`
- `Date getLastUpdated()`
- `Map<String, Object> getLinks()`
- `String getName()`
- `AuthenticatorSettings getSettings()`
- `AuthenticatorStatus getStatus()`
- `AuthenticatorType getType()`
- `Authenticator setKey(String key)`
- `Authenticator setName(String name)`
- `Authenticator setSettings(AuthenticatorSettings settings)`
- `Authenticator setStatus(AuthenticatorStatus status)`
- `Authenticator setType(AuthenticatorType type)`

### Package `com.okta.sdk.resource.authenticator.AuthenticatorSettings`

This is a newly created interface with methods listed below.

- `AllowedForEnum getAllowedFor()`
- `Integer getTokenLifetimeInMinutes()`
- `AuthenticatorSettings setAllowedFor(AllowedForEnum allowedFor)`
- `AuthenticatorSettings setTokenLifetimeInMinutes(Integer tokenLifetimeInMinutes)`

### Package `com.okta.sdk.resource.authenticator.AuthenticatorStatus`

This is a newly created enum with fields listed below.
- `ACTIVE("ACTIVE")`
- `INACTIVE("INACTIVE")`

### Package `com.okta.sdk.resource.authenticator.AuthenticatorType`

This is a newly created enum with fields listed below.
- `APP("APP")`
- `EMAIL("EMAIL")`
- `PASSWORD("PASSWORD")`
- `PHONE("PHONE")`
- `SECURITY_KEY("SECURITY_KEY")`
- `SECURITY_QUESTION("SECURITY_QUESTION")`

### Package `com.okta.sdk.resource.domain.DomainListResponse`

This is a newly created interface with methods listed below.

- `DomainList getDomains()`
- `DomainListResponse setDomains(DomainList domains)`

### Package `com.okta.sdk.resource.group.schema.GroupSchema`

This is a newly created interface with methods listed below.

- `String getCreated()`
- `GroupSchemaDefinitions getDefinitions()`
- `String getDescription()`
- `String getId()`
- `String getLastUpdated()`
- `Map<String, Object> getLinks()`
- `String getName()`
- `UserSchemaProperties getProperties()`
- `String getSchema()`
- `String getTitle()`
- `String getType()`
- `GroupSchema setDefinitions(GroupSchemaDefinitions definitions)`
- `GroupSchema setDescription(String description)`
- `GroupSchema setTitle(String title)`

### Package `com.okta.sdk.resource.group.schema.GroupSchemaAttribute`

This is a newly created interface with methods listed below.

- `String getDescription()`
- `List<String> getEnum()`
- `String getExternalName()`
- `String getExternalNamespace()`
- `UserSchemaAttributeItems getItems()`
- `UserSchemaAttributeMaster getMaster()`
- `Integer getMaxLength()`
- `Integer getMinLength()`
- `String getMutability()`
- `List<UserSchemaAttributeEnum> getOneOf()`
- `List<UserSchemaAttributePermission> getPermissions()`
- `Boolean getRequired()`
- `UserSchemaAttributeScope getScope()`
- `String getTitle()`
- `UserSchemaAttributeType getType()`
- `UserSchemaAttributeUnion getUnion()`
- `String getUnique()`
- `GroupSchemaAttribute setDescription(String description)`
- `GroupSchemaAttribute setEnum(List<String> _enum)`
- `GroupSchemaAttribute setExternalName(String externalName)`
- `GroupSchemaAttribute setExternalNamespace(String externalNamespace)`
- `GroupSchemaAttribute setItems(UserSchemaAttributeItems items)`
- `GroupSchemaAttribute setMaster(UserSchemaAttributeMaster master)`
- `GroupSchemaAttribute setMaxLength(Integer maxLength)`
- `GroupSchemaAttribute setMinLength(Integer minLength)`
- `GroupSchemaAttribute setMutability(String mutability)`
- `GroupSchemaAttribute setOneOf(List<UserSchemaAttributeEnum> oneOf)`
- `GroupSchemaAttribute setPermissions(List<UserSchemaAttributePermission> permissions)`
- `GroupSchemaAttribute setRequired(Boolean required)`
- `GroupSchemaAttribute setScope(UserSchemaAttributeScope scope)`
- `GroupSchemaAttribute setTitle(String title)`
- `GroupSchemaAttribute setType(UserSchemaAttributeType type)`
- `GroupSchemaAttribute setUnion(UserSchemaAttributeUnion union)`
- `GroupSchemaAttribute setUnique(String unique)`

### Package `com.okta.sdk.resource.group.schema.GroupSchemaBase`

This is a newly created interface with methods listed below.

- `String getId()`
- `GroupSchemaBaseProperties getProperties()`
- `List<String> getRequired()`
- `String getType()`
- `GroupSchemaBase setProperties(GroupSchemaBaseProperties properties)`
- `GroupSchemaBase setRequired(List<String> required)`
- `GroupSchemaBase setType(String type)`

### Package `com.okta.sdk.resource.group.schema.GroupSchemaBaseProperties`

This is a newly created interface with methods listed below.

- `GroupSchemaAttribute getDescription()`
- `GroupSchemaAttribute getName()`
- `GroupSchemaBaseProperties setDescription(GroupSchemaAttribute description)`
- `GroupSchemaBaseProperties setName(GroupSchemaAttribute name)`

### Package `com.okta.sdk.resource.group.schema.GroupSchemaCustom`

This is a newly created interface with methods listed below.

- `String getId()`
- `Map<String, GroupSchemaAttribute> getProperties()`
- `List<String> getRequired()`
- `String getType()`
- `GroupSchemaCustom setProperties(Map<String, GroupSchemaAttribute> properties)`
- `GroupSchemaCustom setRequired(List<String> required)`
- `GroupSchemaCustom setType(String type)`

### Package `com.okta.sdk.resource.group.schema.GroupSchemaDefinitions`

This is a newly created interface with methods listed below.

- `GroupSchemaBase getBase()`
- `GroupSchemaCustom getCustom()`
- `GroupSchemaDefinitions setBase(GroupSchemaBase base)`
- `GroupSchemaDefinitions setCustom(GroupSchemaCustom custom)`

### Package `com.okta.sdk.resource.identity.provider.IdentityProvider$IssuerModeEnum`

Below enum value has been changed.
- `CUSTOM_URL_DOMAIN("CUSTOM_URL_DOMAIN")` changed to `CUSTOM_URL("CUSTOM_URL")`

### Package `com.okta.sdk.resource.identity.provider.SocialAuthToken`

Below methods have undergone a signature change.
- `TokenTypeEnum getTokenType()` signature changed to `String getTokenType()`
- `SocialAuthToken setTokenType(TokenTypeEnum tokenType)` signature changed to `SocialAuthToken setTokenType(String tokenType)`

### Package `com.okta.sdk.resource.identity.provider.SocialAuthToken`

The Enum `com.okta.sdk.resource.identity.provider.SocialAuthToken$TokenTypeEnum` has been removed.

### Package `com.okta.sdk.resource.org.OrgContactType`

This is a newly created enum with fields listed below.
- `BILLING("BILLING")`
- `TECHNICAL("TECHNICAL")`

### Package `com.okta.sdk.resource.org.OrgContactTypeObj`

This is a newly created interface with methods listed below.

- `OrgContactType getContactType()`
- `OrgContactTypeObj setContactType(OrgContactType contactType)`

### Package `com.okta.sdk.resource.org.OrgOktaCommunicationSetting`

This is a newly created interface with methods listed below.

- `Boolean getOptOutEmailUsers()`
- `OrgOktaCommunicationSetting optInUsersToOktaCommunicationEmails()`
- `OrgOktaCommunicationSetting optOutUsersFromOktaCommunicationEmails()`

### Package `com.okta.sdk.resource.org.OrgOktaSupportSetting`

This is a newly created enum with fields listed below.
- `DISABLED("DISABLED")`
- `ENABLED("ENABLED")`

### Package `com.okta.sdk.resource.org.OrgOktaSupportSettingsObj`

This is a newly created interface with methods listed below.

- `OrgOktaSupportSettingsObj extendOktaSupport()`
- `Date getExpiration()`
- `OrgOktaSupportSetting getSupport()`
- `OrgOktaSupportSettingsObj grantOktaSupport()`
- `OrgOktaSupportSettingsObj revokeOktaSupport()`

### Package `com.okta.sdk.resource.org.OrgPreferences`

This is a newly created interface with methods listed below.

- `Boolean getShowEndUserFooter()`
- `OrgPreferences hideEndUserFooter()`
- `OrgPreferences showEndUserFooter()`

### Package `com.okta.sdk.resource.org.OrgSetting`

This is a newly created interface with methods listed below.

- `String getAddress1()`
- `String getAddress2()`
- `String getCity()`
- `String getCompanyName()`
- `String getCountry()`
- `Date getCreated()`
- `String getEndUserSupportHelpURL()`
- `Date getExpiresAt()`
- `String getId()`
- `Date getLastUpdated()`
- `String getPhoneNumber()`
- `String getPostalCode()`
- `String getState()`
- `String getStatus()`
- `String getSubdomain()`
- `String getSupportPhoneNumber()`
- `String getWebsite()`
- `OrgSetting partialUpdate()`
- `OrgSetting setAddress1(String address1)`
- `OrgSetting setAddress2(String address2)`
- `OrgSetting setCity(String city)`
- `OrgSetting setCompanyName(String companyName)`
- `OrgSetting setCountry(String country)`
- `OrgSetting setEndUserSupportHelpURL(String endUserSupportHelpURL)`
- `OrgSetting setPhoneNumber(String phoneNumber)`
- `OrgSetting setPostalCode(String postalCode)`
- `OrgSetting setState(String state)`
- `OrgSetting setSupportPhoneNumber(String supportPhoneNumber)`
- `OrgSetting setWebsite(String website)`
- `OrgSetting update()`

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
