/*
 * Copyright 2020-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.impl.resource;

import com.okta.commons.lang.Collections;
import com.okta.commons.lang.Strings;
import com.okta.sdk.helper.PolicyApiHelper;
import com.okta.sdk.resource.policy.PasswordPolicyBuilder;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.PolicyApi;
import org.openapitools.client.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultPasswordPolicyBuilder extends DefaultPolicyBuilder<PasswordPolicyBuilder> implements PasswordPolicyBuilder {

    private PasswordPolicyAuthenticationProviderType provider;
    private List<String> groupIds = new ArrayList<>();
    private List<String> userIds = new ArrayList<>();
    private Boolean excludePasswordDictionary;
    private Boolean excludeUserNameInPassword;
    private Integer minPasswordLength;
    private Integer minLowercase;
    private Integer minUpperCase;
    private Integer minNumbers;
    private Integer minSymbols;
    private Boolean skipUnlock;
    private Integer pwdExpireWarnDays;
    private Integer pwdHistoryCount;
    private Integer pwdMaxAgeDays;
    private Integer pwdMinMinutes;
    private Integer pwdAutoUnlockMinutes;
    private Integer pwdMaxAttempts;
    private Boolean showLockoutFailures;
    private PasswordPolicyRecoveryFactorSettings pwdRecoveryOktaCall;
    private PasswordPolicyRecoveryFactorSettings pwdRecoveryOktaSMS;
    private PasswordPolicyRecoveryFactorSettings pwdPolicyRecoveryEmailStatus;
    private Integer pwdRecoveryTokenLifeMinutes;

    public DefaultPasswordPolicyBuilder() {
        this.policyType = PolicyType.PASSWORD;
    }

    @Override
    public PasswordPolicyBuilder setAuthProvider(PasswordPolicyAuthenticationProviderType provider) {
        this.provider = provider;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setGroups(List<String> groupIds) {
        this.groupIds=groupIds;
        return this;
    }

    @Override
    public PasswordPolicyBuilder addGroup(String groupId) {
        this.groupIds.add(groupId);
        return this;
    }

    @Override
    public PasswordPolicyBuilder setUsers(List<String> userIds) {
        this.userIds = userIds;
        return this;
    }

    @Override
    public PasswordPolicyBuilder addUser(String userId) {
        this.userIds.add(userId);
        return this;
    }

    @Override
    public PasswordPolicyBuilder setExcludePasswordDictionary(Boolean enablePasswordDictionary) {
        this.excludePasswordDictionary = enablePasswordDictionary;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setExcludeUserNameInPassword(Boolean excludeUserNameInPassword) {
        this.excludeUserNameInPassword = excludeUserNameInPassword;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setMinPasswordLength(Integer minPasswordLength) {
        this.minPasswordLength = minPasswordLength;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setMinLowerCase(Integer minLowerCase) {
        this.minLowercase = minLowerCase;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setMinUpperCase(Integer minUpperCase) {
        this.minUpperCase = minUpperCase;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setMinNumbers(Integer minNumbers) {
        this.minNumbers = minNumbers;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setMinSymbols(Integer minSymbols) {
        this.minSymbols = minSymbols;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setSkipUnlock(Boolean skipUnlock) {
        this.skipUnlock = skipUnlock;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setPasswordExpireWarnDays(Integer pwdExpireWarnDays) {
        this.pwdExpireWarnDays = pwdExpireWarnDays;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setPasswordHistoryCount(Integer pwdHistoryCount) {
        this.pwdHistoryCount = pwdHistoryCount;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setPasswordMaxAgeDays(Integer pwdMaxAgeDays) {
        this.pwdMaxAgeDays = pwdMaxAgeDays;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setPasswordMinMinutes(Integer pwdMinMinutes) {
        this.pwdMinMinutes = pwdMinMinutes;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setPasswordAutoUnlockMinutes(Integer pwdAutoUnlockMinutes) {
        this.pwdAutoUnlockMinutes = pwdAutoUnlockMinutes;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setPasswordMaxAttempts(Integer pwdMaxAttempts) {
        this.pwdMaxAttempts = pwdMaxAttempts;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setShowLockoutFailures(Boolean showLockoutFailures) {
        this.showLockoutFailures = showLockoutFailures;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setPasswordRecoveryOktaCall(PasswordPolicyRecoveryFactorSettings pwdRecoveryOktaCall) {
        this.pwdRecoveryOktaCall = pwdRecoveryOktaCall;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setPasswordRecoveryOktaSMS(PasswordPolicyRecoveryFactorSettings pwdRecoveryOktaSMS) {
        this.pwdRecoveryOktaSMS = pwdRecoveryOktaSMS;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setPasswordPolicyRecoveryEmailStatus(PasswordPolicyRecoveryFactorSettings status) {
        this.pwdPolicyRecoveryEmailStatus = status;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setPasswordRecoveryTokenLifeMinutes(Integer pwdRecoveryTokenLifeMinutes) {
        this.pwdRecoveryTokenLifeMinutes = pwdRecoveryTokenLifeMinutes;
        return this;
    }

    @Override
    public PasswordPolicy buildAndCreate(PolicyApi client) throws ApiException {
        return new PolicyApiHelper<>(new PolicyApiHelper<>(client.getApiClient()))
            .createPolicyOfType(PasswordPolicy.class, build(), false);
    }

    private PasswordPolicy build() {
        PasswordPolicy policy = new PasswordPolicy();

        // PasswordPolicyConditions
        policy.setConditions(new PasswordPolicyConditions());
        PasswordPolicyConditions passwordPolicyConditions = policy.getConditions();

        // PasswordPolicySettings
        policy.setSettings(new PasswordPolicySettings());
        PasswordPolicySettings passwordPolicySettings = policy.getSettings();

        if (Strings.hasText(name)) policy.setName(name);
        if (Strings.hasText(description)) policy.setDescription(description);
        if (priority != null)
            policy.setPriority(priority);

        if (PolicyType.PASSWORD.equals(policyType))
            policy.setType(policyType);
        else
            throw new IllegalArgumentException("PolicyType should be 'PASSWORD', please use PolicyBuilder for other policy types.");

        if (Objects.nonNull(status))
            policy.setStatus(status);

        if (Objects.nonNull(provider)) {
            PasswordPolicyAuthenticationProviderCondition passwordPolicyAuthenticationProviderCondition = new PasswordPolicyAuthenticationProviderCondition();
            passwordPolicyAuthenticationProviderCondition.setProvider(provider);
            passwordPolicyConditions.setAuthProvider(passwordPolicyAuthenticationProviderCondition);
        }

        if (!Collections.isEmpty(groupIds)) {
            GroupCondition groupCondition = new GroupCondition();
            groupCondition.setInclude(groupIds);
            PolicyPeopleCondition policyPeopleCondition = new PolicyPeopleCondition();
            policyPeopleCondition.setGroups(groupCondition);
            passwordPolicyConditions.setPeople(policyPeopleCondition);
        }

        if (!Collections.isEmpty(userIds)) {
            UserCondition userCondition = new UserCondition();
            userCondition.setInclude(userIds);
            PolicyPeopleCondition policyPeopleCondition = new PolicyPeopleCondition();
            policyPeopleCondition.setUsers(userCondition);
            passwordPolicyConditions.setPeople(policyPeopleCondition);
        }

        // Password Settings instantiation
        PasswordPolicyPasswordSettings passwordPolicyPasswordSettings = new PasswordPolicyPasswordSettings();

        // Setting password complexity attributes
        PasswordPolicyPasswordSettingsComplexity passwordPolicyPasswordSettingsComplexity = new PasswordPolicyPasswordSettingsComplexity();

        if (Objects.nonNull(excludePasswordDictionary)) {
            PasswordDictionaryCommon passwordDictionaryCommon = new PasswordDictionaryCommon();
            passwordDictionaryCommon.setExclude(excludePasswordDictionary);
            PasswordDictionary passwordDictionary = new PasswordDictionary();
            passwordDictionary.setCommon(passwordDictionaryCommon);
            passwordPolicyPasswordSettingsComplexity.setDictionary(passwordDictionary);
            passwordPolicyPasswordSettings.setComplexity(passwordPolicyPasswordSettingsComplexity);
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings);
        }

        if (Objects.nonNull(excludeUserNameInPassword)) {
            passwordPolicyPasswordSettingsComplexity.setExcludeUsername(excludeUserNameInPassword);
            passwordPolicyPasswordSettings.setComplexity(passwordPolicyPasswordSettingsComplexity);
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings);
        }

        if (Objects.nonNull(minPasswordLength)) {
            passwordPolicyPasswordSettingsComplexity.setMinLength(minPasswordLength);
            passwordPolicyPasswordSettings.setComplexity(passwordPolicyPasswordSettingsComplexity);
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings);
        }

        if (Objects.nonNull(minLowercase)) {
            passwordPolicyPasswordSettingsComplexity.setMinLowerCase(minLowercase);
            passwordPolicyPasswordSettings.setComplexity(passwordPolicyPasswordSettingsComplexity);
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings);
        }

        if (Objects.nonNull(minUpperCase)) {
            passwordPolicyPasswordSettingsComplexity.setMinUpperCase(minUpperCase);
            passwordPolicyPasswordSettings.setComplexity(passwordPolicyPasswordSettingsComplexity);
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings);
        }

        if (Objects.nonNull(minNumbers)) {
            passwordPolicyPasswordSettingsComplexity.setMinNumber(minNumbers);
            passwordPolicyPasswordSettings.setComplexity(passwordPolicyPasswordSettingsComplexity);
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings);
        }

        if (Objects.nonNull(minSymbols)) {
            passwordPolicyPasswordSettingsComplexity.setMinSymbol(minSymbols);
            passwordPolicyPasswordSettings.setComplexity(passwordPolicyPasswordSettingsComplexity);
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings);
        }

        // Delegation
        if (Objects.nonNull(skipUnlock)) {
            PasswordPolicyDelegationSettingsOptions passwordPolicyDelegationSettingsOptions = new PasswordPolicyDelegationSettingsOptions();
            passwordPolicyDelegationSettingsOptions.setSkipUnlock(skipUnlock);
            PasswordPolicyDelegationSettings passwordPolicyDelegationSettings = new PasswordPolicyDelegationSettings();
            passwordPolicyDelegationSettings.setOptions(passwordPolicyDelegationSettingsOptions);
            passwordPolicySettings.setDelegation(passwordPolicyDelegationSettings);
        }

        // Age

        PasswordPolicyPasswordSettingsAge passwordPolicyPasswordSettingsAge = new PasswordPolicyPasswordSettingsAge();

        if (Objects.nonNull(pwdExpireWarnDays)) {
            passwordPolicyPasswordSettingsAge.setExpireWarnDays(pwdExpireWarnDays);
            passwordPolicyPasswordSettings.setAge(passwordPolicyPasswordSettingsAge);
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings);
        }

        if (Objects.nonNull(pwdHistoryCount)) {
            passwordPolicyPasswordSettingsAge.setHistoryCount(pwdHistoryCount);
            passwordPolicyPasswordSettings.setAge(passwordPolicyPasswordSettingsAge);
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings);
        }

        if (Objects.nonNull(pwdMaxAgeDays)) {
            passwordPolicyPasswordSettingsAge.setMaxAgeDays(pwdMaxAgeDays);
            passwordPolicyPasswordSettings.setAge(passwordPolicyPasswordSettingsAge);
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings);
        }

        if (Objects.nonNull(pwdMinMinutes)) {
            passwordPolicyPasswordSettingsAge.setMinAgeMinutes(pwdMinMinutes);
            passwordPolicyPasswordSettings.setAge(passwordPolicyPasswordSettingsAge);
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings);
        }

        // Lockout
        PasswordPolicyPasswordSettingsLockout passwordPolicyPasswordSettingsLockout = new PasswordPolicyPasswordSettingsLockout();

        if (Objects.nonNull(pwdAutoUnlockMinutes)) {
            passwordPolicyPasswordSettingsLockout.setAutoUnlockMinutes(pwdAutoUnlockMinutes);
            passwordPolicyPasswordSettings.setLockout(passwordPolicyPasswordSettingsLockout);
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings);
        }

        if (Objects.nonNull(pwdMaxAttempts)) {
            passwordPolicyPasswordSettingsLockout.setMaxAttempts(pwdMaxAttempts);
            passwordPolicyPasswordSettings.setLockout(passwordPolicyPasswordSettingsLockout);
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings);
        }

        if (Objects.nonNull(showLockoutFailures)) {
            passwordPolicyPasswordSettingsLockout.setShowLockoutFailures(showLockoutFailures);
            passwordPolicyPasswordSettings.setLockout(passwordPolicyPasswordSettingsLockout);
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings);
        }

        //Instantiate PasswordRecoverySettings
        PasswordPolicyRecoverySettings passwordPolicyRecoverySettings = new PasswordPolicyRecoverySettings();

        //RecoveryFactors
        PasswordPolicyRecoveryFactors passwordPolicyRecoveryFactors = new PasswordPolicyRecoveryFactors();

        if (Objects.nonNull(pwdRecoveryOktaCall)) {
            PasswordPolicyRecoveryFactorSettings passwordPolicyRecoveryFactorSettings = new PasswordPolicyRecoveryFactorSettings();
            passwordPolicyRecoveryFactorSettings.setStatus(pwdRecoveryOktaCall.getStatus());
            passwordPolicyRecoveryFactors.setOktaCall(passwordPolicyRecoveryFactorSettings);
            passwordPolicyRecoverySettings.setFactors(passwordPolicyRecoveryFactors);
            passwordPolicySettings.setRecovery(passwordPolicyRecoverySettings);
        }

        if (Objects.nonNull(pwdRecoveryOktaSMS)) {
            PasswordPolicyRecoveryFactorSettings passwordPolicyRecoveryFactorSettings = new PasswordPolicyRecoveryFactorSettings();
            passwordPolicyRecoveryFactorSettings.setStatus(pwdRecoveryOktaSMS.getStatus());
            passwordPolicyRecoveryFactors.setOktaSms(passwordPolicyRecoveryFactorSettings);
            passwordPolicyRecoverySettings.setFactors(passwordPolicyRecoveryFactors);
            passwordPolicySettings.setRecovery(passwordPolicyRecoverySettings);
        }

        PasswordPolicyRecoveryEmail passwordPolicyRecoveryEmail = new PasswordPolicyRecoveryEmail();

        if (Objects.nonNull(pwdPolicyRecoveryEmailStatus)) {
            passwordPolicyRecoveryEmail.setStatus(pwdPolicyRecoveryEmailStatus.getStatus());
        }

        if (Objects.nonNull(pwdRecoveryTokenLifeMinutes)) {
            PasswordPolicyRecoveryEmailRecoveryToken passwordPolicyRecoveryEmailRecoveryToken = new PasswordPolicyRecoveryEmailRecoveryToken();
            passwordPolicyRecoveryEmailRecoveryToken.setTokenLifetimeMinutes(pwdRecoveryTokenLifeMinutes);
            PasswordPolicyRecoveryEmailProperties passwordPolicyRecoveryEmailProperties = new PasswordPolicyRecoveryEmailProperties();
            passwordPolicyRecoveryEmailProperties.setRecoveryToken(passwordPolicyRecoveryEmailRecoveryToken);

            passwordPolicyRecoveryEmail.setProperties(passwordPolicyRecoveryEmailProperties);
            passwordPolicyRecoveryFactors.setOktaEmail(passwordPolicyRecoveryEmail);
            passwordPolicyRecoverySettings.setFactors(passwordPolicyRecoveryFactors);

            passwordPolicySettings.setRecovery(passwordPolicyRecoverySettings);
        }

        return policy;
    }
}