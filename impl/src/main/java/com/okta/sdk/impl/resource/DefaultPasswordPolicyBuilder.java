/*
 * Copyright 2020 Okta
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
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.policy.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultPasswordPolicyBuilder extends DefaultPolicyBuilder<PasswordPolicyBuilder> implements PasswordPolicyBuilder {

    private PasswordPolicyAuthenticationProviderCondition.ProviderEnum provider;
    private List<String> groupIds = new ArrayList<>();
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
    private PasswordPolicyRecoveryFactorSettings.StatusEnum pwdRecoveryOktaCall;
    private PasswordPolicyRecoveryFactorSettings.StatusEnum pwdRecoveryOktaSMS;
    private PasswordPolicyRecoveryFactorSettings.StatusEnum pwdPolicyRecoveryEmailStatus;
    private Integer pwdRecoveryTokenLifeMinutes;

    public DefaultPasswordPolicyBuilder() {
        this.policyType = PolicyType.PASSWORD;
    }

    @Override
    public PasswordPolicyBuilder setAuthProvider(PasswordPolicyAuthenticationProviderCondition.ProviderEnum provider) {
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
    public PasswordPolicyBuilder setPasswordRecoveryOktaCall(PasswordPolicyRecoveryFactorSettings.StatusEnum pwdRecoveryOktaCall) {
        this.pwdRecoveryOktaCall = pwdRecoveryOktaCall;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setPasswordRecoveryOktaSMS(PasswordPolicyRecoveryFactorSettings.StatusEnum pwdRecoveryOktaSMS) {
        this.pwdRecoveryOktaSMS = pwdRecoveryOktaSMS;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setPasswordPolicyRecoveryEmailStatus(PasswordPolicyRecoveryFactorSettings.StatusEnum status) {
        this.pwdPolicyRecoveryEmailStatus = status;
        return this;
    }

    @Override
    public PasswordPolicyBuilder setPasswordRecoveryTokenLifeMinutes(Integer pwdRecoveryTokenLifeMinutes) {
        this.pwdRecoveryTokenLifeMinutes = pwdRecoveryTokenLifeMinutes;
        return this;
    }

    @Override
    public PasswordPolicy buildAndCreate(Client client) {
        return (PasswordPolicy) client.createPolicy(build(client));
    }

    private PasswordPolicy build(Client client) {
        PasswordPolicy policy = client.instantiate(PasswordPolicy.class);
        
        // PasswordPolicyConditions
        policy.setConditions(client.instantiate(PasswordPolicyConditions.class));
        PasswordPolicyConditions passwordPolicyConditions = policy.getConditions();
        
        // PasswordPolicySettings
        policy.setSettings(client.instantiate(PasswordPolicySettings.class));
        PasswordPolicySettings passwordPolicySettings = policy.getSettings();

        if (Strings.hasText(name)) policy.setName(name);
        if (Strings.hasText(description)) policy.setDescription(description);
        if (priority != null)
            policy.setPriority(priority);

        if (Objects.nonNull(policyType))
            if (policyType.equals(PolicyType.PASSWORD))
                policy.setType(policyType);
            else
                throw new IllegalArgumentException("PolicyType should be 'PASSWORD', please use PolicyBuilder for other policy types.");
        else
            throw new IllegalArgumentException("PolicyType cannot be blank, needs to be specified.");

        if (Objects.nonNull(status))
            policy.setStatus(status);

        if (Objects.nonNull(provider))
            passwordPolicyConditions.setAuthProvider(client.instantiate(PasswordPolicyAuthenticationProviderCondition.class).setProvider(provider));

        if (!Collections.isEmpty(groupIds))
            passwordPolicyConditions.setPeople(client.instantiate(PolicyPeopleCondition.class)
                                    .setGroups(client.instantiate(GroupCondition.class).setInclude(groupIds)));

        // Password Settings instantiation
        PasswordPolicyPasswordSettings passwordPolicyPasswordSettings = client.instantiate(PasswordPolicyPasswordSettings.class);

        // Setting password complexity attributes
        PasswordPolicyPasswordSettingsComplexity passwordPolicyPasswordSettingsComplexity = client.instantiate(PasswordPolicyPasswordSettingsComplexity.class);

        if (Objects.nonNull(excludePasswordDictionary))
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings
                .setComplexity(passwordPolicyPasswordSettingsComplexity.setDictionary(client.instantiate(PasswordDictionary.class)
                    .setCommon(client.instantiate(PasswordDictionaryCommon.class).setExclude(excludePasswordDictionary)))));

        if (Objects.nonNull(excludeUserNameInPassword))
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings
                .setComplexity(passwordPolicyPasswordSettingsComplexity.setExcludeUsername(excludeUserNameInPassword)));

        if (Objects.nonNull(minPasswordLength))
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings
                .setComplexity(passwordPolicyPasswordSettingsComplexity.setMinLength(minPasswordLength)));

        if (Objects.nonNull(minLowercase))
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings
                .setComplexity(passwordPolicyPasswordSettingsComplexity.setMinLowerCase(minLowercase)));

        if (Objects.nonNull(minUpperCase))
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings
                .setComplexity(passwordPolicyPasswordSettingsComplexity.setMinUpperCase(minUpperCase)));

        if (Objects.nonNull(minNumbers))
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings
                .setComplexity(passwordPolicyPasswordSettingsComplexity.setMinNumber(minNumbers)));

        if (Objects.nonNull(minSymbols))
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings
                .setComplexity(passwordPolicyPasswordSettingsComplexity.setMinSymbol(minSymbols)));

        // Delegation
        if (Objects.nonNull(skipUnlock))
            passwordPolicySettings.setDelegation(client.instantiate(PasswordPolicyDelegationSettings.class)
                .setOptions(client.instantiate(PasswordPolicyDelegationSettingsOptions.class).setSkipUnlock(skipUnlock)));

        // Age

        PasswordPolicyPasswordSettingsAge passwordPolicyPasswordSettingsAge = client.instantiate(PasswordPolicyPasswordSettingsAge.class);

        if (Objects.nonNull(pwdExpireWarnDays))
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings
                .setAge(passwordPolicyPasswordSettingsAge.setExpireWarnDays(pwdExpireWarnDays)));

        if (Objects.nonNull(pwdHistoryCount))
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings
                .setAge(passwordPolicyPasswordSettingsAge.setHistoryCount(pwdHistoryCount)));

        if (Objects.nonNull(pwdMaxAgeDays))
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings
                .setAge(passwordPolicyPasswordSettingsAge.setMaxAgeDays(pwdMaxAgeDays)));

        if (Objects.nonNull(pwdMinMinutes))
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings
                .setAge(passwordPolicyPasswordSettingsAge.setMinAgeMinutes(pwdMinMinutes)));

        // Lockout
        PasswordPolicyPasswordSettingsLockout passwordPolicyPasswordSettingsLockout = client.instantiate(PasswordPolicyPasswordSettingsLockout.class);

        if (Objects.nonNull(pwdAutoUnlockMinutes))
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings
                .setLockout(passwordPolicyPasswordSettingsLockout.setAutoUnlockMinutes(pwdAutoUnlockMinutes)));

        if (Objects.nonNull(pwdMaxAttempts))
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings
                .setLockout(passwordPolicyPasswordSettingsLockout.setMaxAttempts(pwdMaxAttempts)));

        if (Objects.nonNull(showLockoutFailures))
            passwordPolicySettings.setPassword(passwordPolicyPasswordSettings
                .setLockout(passwordPolicyPasswordSettingsLockout.setShowLockoutFailures(showLockoutFailures)));

        //Instantiate PasswordRecoverySettings
        PasswordPolicyRecoverySettings passwordPolicyRecoverySettings = client.instantiate(PasswordPolicyRecoverySettings.class);

        //RecoveryFactors
        PasswordPolicyRecoveryFactors passwordPolicyRecoveryFactors = client.instantiate(PasswordPolicyRecoveryFactors.class);

        if (Objects.nonNull(pwdRecoveryOktaCall))
            passwordPolicySettings.setRecovery(passwordPolicyRecoverySettings
                .setFactors(passwordPolicyRecoveryFactors.setOktaCall(client.instantiate(PasswordPolicyRecoveryFactorSettings.class)
                    .setStatus(pwdRecoveryOktaCall))));

        if (Objects.nonNull(pwdRecoveryOktaSMS))
            passwordPolicySettings.setRecovery(passwordPolicyRecoverySettings
                .setFactors(passwordPolicyRecoveryFactors.setOktaSms(client.instantiate(PasswordPolicyRecoveryFactorSettings.class)
                    .setStatus(pwdRecoveryOktaSMS))));

        PasswordPolicyRecoveryEmail passwordPolicyRecoveryEmail = client.instantiate(PasswordPolicyRecoveryEmail.class);

        if (Objects.nonNull(pwdPolicyRecoveryEmailStatus))
            ((AbstractResource) passwordPolicyRecoveryEmail).setProperty("status", pwdPolicyRecoveryEmailStatus, true);

        if (Objects.nonNull(pwdRecoveryTokenLifeMinutes))
            passwordPolicySettings.setRecovery(passwordPolicyRecoverySettings
                .setFactors(passwordPolicyRecoveryFactors.setOktaEmail(passwordPolicyRecoveryEmail
                        .setProperties(client.instantiate(PasswordPolicyRecoveryEmailProperties.class).setRecoveryToken(client.instantiate(PasswordPolicyRecoveryEmailRecoveryToken.class)
                            .setTokenLifetimeMinutes(pwdRecoveryTokenLifeMinutes)))
                    )
                )
            );


        return policy;
    }
}
