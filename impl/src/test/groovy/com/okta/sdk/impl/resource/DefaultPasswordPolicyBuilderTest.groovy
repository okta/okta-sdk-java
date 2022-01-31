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
package com.okta.sdk.impl.resource

import com.okta.sdk.client.Client
import com.okta.sdk.resource.authorization.server.*
import com.okta.sdk.resource.*
import com.okta.sdk.resource.common.*
import org.testng.annotations.Test

import static com.okta.sdk.impl.Util.expect
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class DefaultPasswordPolicyBuilderTest {

    @Test
    void basicUsage(){
        def client = mock(Client)
        def policy = mock(Policy)
        def passwordPolicy = mock(PasswordPolicy)
        def passwordPolicyAuthenticationProviderCondition = mock(PasswordPolicyAuthenticationProviderCondition)
        def passwordPolicyConditions = mock(PasswordPolicyConditions)
        def passwordPolicySettings = mock(PasswordPolicySettings)
        def policyPeopleCondition = mock(PolicyPeopleCondition)
        def groupCondition = mock(GroupCondition)
        def passwordPolicyPasswordSettings = mock(PasswordPolicyPasswordSettings)
        def passwordPolicyPasswordSettingsComplexity = mock(PasswordPolicyPasswordSettingsComplexity)
        def passwordDictionary = mock(PasswordDictionary)
        def passwordDictionaryCommon = mock(PasswordDictionaryCommon)
        def passwordPolicyDelegationSettings = mock(PasswordPolicyDelegationSettings)
        def passwordPolicyDelegationSettingsOptions = mock(PasswordPolicyDelegationSettingsOptions)
        def passwordPolicyPasswordSettingsAge = mock(PasswordPolicyPasswordSettingsAge)
        def passwordPolicyPasswordSettingsLockout = mock(PasswordPolicyPasswordSettingsLockout)
        def passwordPolicyRecoverySettings = mock(PasswordPolicyRecoverySettings)
        def passwordPolicyRecoveryFactors = mock(PasswordPolicyRecoveryFactors)
        def passwordPolicyRecoveryFactorSettings = mock(PasswordPolicyRecoveryFactorSettings)
        def passwordPolicyRecoveryEmail = mock(PasswordPolicyRecoveryEmail)
        def passwordPolicyRecoveryEmailProperties = mock(PasswordPolicyRecoveryEmailProperties)
        def passwordPolicyRecoveryEmailRecoveryToken = mock(PasswordPolicyRecoveryEmailRecoveryToken)


        when(client.instantiate(Policy.class)).thenReturn(policy)
        when(client.instantiate(PasswordPolicy.class)).thenReturn(passwordPolicy)
        when(client.instantiate(PasswordPolicyAuthenticationProviderCondition.class)).thenReturn(passwordPolicyAuthenticationProviderCondition)
        when(client.instantiate(PasswordPolicyConditions.class)).thenReturn(passwordPolicyConditions)
        when(client.instantiate(PasswordPolicySettings.class)).thenReturn(passwordPolicySettings)
        when(client.instantiate(PasswordPolicy.class).getConditions()).thenReturn(passwordPolicyConditions)
        when(client.instantiate(PasswordPolicy.class).getSettings()).thenReturn(passwordPolicySettings)
        when(client.instantiate(PolicyPeopleCondition.class)).thenReturn(policyPeopleCondition)
        when(client.instantiate(GroupCondition.class)).thenReturn(groupCondition)
        when(client.instantiate(PasswordPolicyPasswordSettings.class)).thenReturn(passwordPolicyPasswordSettings)
        when(client.instantiate(PasswordPolicyPasswordSettingsComplexity.class)).thenReturn(passwordPolicyPasswordSettingsComplexity)
        when(client.instantiate(PasswordDictionaryCommon.class)).thenReturn(passwordDictionaryCommon)
        when(client.instantiate(PasswordDictionary.class)).thenReturn(passwordDictionary)
        when(client.instantiate(PasswordPolicyDelegationSettings.class)).thenReturn(passwordPolicyDelegationSettings)
        when(client.instantiate(PasswordPolicyDelegationSettingsOptions.class)).thenReturn(passwordPolicyDelegationSettingsOptions)
        when(client.instantiate(PasswordPolicyPasswordSettingsAge.class)).thenReturn(passwordPolicyPasswordSettingsAge)
        when(client.instantiate(PasswordPolicyPasswordSettingsLockout.class)).thenReturn(passwordPolicyPasswordSettingsLockout)
        when(client.instantiate(PasswordPolicyRecoverySettings.class)).thenReturn(passwordPolicyRecoverySettings)
        when(client.instantiate(PasswordPolicyRecoveryFactors.class)).thenReturn(passwordPolicyRecoveryFactors)
        when(client.instantiate(PasswordPolicyRecoveryFactorSettings.class)).thenReturn(passwordPolicyRecoveryFactorSettings)
        when(client.instantiate(PasswordPolicyRecoveryEmail.class)).thenReturn(passwordPolicyRecoveryEmail)
        when(client.instantiate(PasswordPolicyRecoveryEmailProperties.class)).thenReturn(passwordPolicyRecoveryEmailProperties)
        when(client.instantiate(PasswordPolicyRecoveryEmailRecoveryToken.class)).thenReturn(passwordPolicyRecoveryEmailRecoveryToken)

        new DefaultPasswordPolicyBuilder()
            .setAuthProvider(PasswordPolicyAuthenticationProviderType.OKTA)
            .setExcludePasswordDictionary(false)
            .setExcludeUserNameInPassword(false)
            .setMinPasswordLength(8)
            .setMinLowerCase(1)
            .setMinUpperCase(1)
            .setMinNumbers(1)
            .setMinSymbols(1)
            .addGroup("shvcgfhs345sdfjhg")
            .setSkipUnlock(false)
            .setPasswordExpireWarnDays(85)
            .setPasswordHistoryCount(5)
            .setPasswordMaxAgeDays(90)
            .setPasswordMinMinutes(2)
            .setPasswordAutoUnlockMinutes(5)
            .setPasswordMaxAttempts(3)
            .setShowLockoutFailures(true)
            .setPasswordRecoveryOktaSMS(LifecycleStatus.ACTIVE)
            .setPasswordRecoveryOktaCall(LifecycleStatus.ACTIVE)
            .setPasswordRecoveryTokenLifeMinutes(500)
            .setType(PolicyType.PASSWORD)
            .setStatus(LifecycleStatus.ACTIVE)
            .setPriority(1)
            .setDescription("Dummy policy for sdk test")
            .setName("SDK created policy")
            .buildAndCreate(client)


        verify(client).createPolicy(eq(passwordPolicy), eq(true))
        verify(passwordPolicy).setName("SDK created policy")
        verify(passwordPolicy).setDescription("Dummy policy for sdk test")
        verify(passwordPolicy).setPriority(1)
        verify(passwordPolicy).setStatus(LifecycleStatus.ACTIVE)
        verify(passwordPolicy).setType(PolicyType.PASSWORD)
        verify(passwordPolicyPasswordSettingsLockout).setShowLockoutFailures(true)
        verify(passwordPolicyRecoveryFactors).setOktaSms(passwordPolicyRecoveryFactorSettings.setStatus(LifecycleStatus.ACTIVE))
        verify(passwordPolicyRecoveryFactors).setOktaCall(passwordPolicyRecoveryFactorSettings.setStatus(LifecycleStatus.ACTIVE))
        verify(passwordPolicyRecoveryEmailRecoveryToken).setTokenLifetimeMinutes(500)
        verify(passwordPolicyPasswordSettingsLockout).setMaxAttempts(3)
        verify(passwordPolicyPasswordSettingsLockout).setAutoUnlockMinutes(5)
        verify(passwordPolicyPasswordSettingsAge).setMinAgeMinutes(2)
        verify(passwordPolicyPasswordSettingsAge).setMinAgeMinutes(2)
        verify(passwordPolicyPasswordSettingsAge).setMinAgeMinutes(2)
        verify(passwordPolicyPasswordSettingsAge).setMaxAgeDays(90)
        verify(passwordPolicyPasswordSettingsAge).setExpireWarnDays(85)
        verify(passwordPolicyPasswordSettingsAge).setHistoryCount(5)
        verify(passwordPolicyDelegationSettingsOptions).setSkipUnlock(false)
        verify(groupCondition).setInclude(new ArrayList<String>(Arrays.asList("shvcgfhs345sdfjhg")))
        verify(passwordPolicyPasswordSettingsComplexity).setMinSymbol(1)
        verify(passwordPolicyPasswordSettingsComplexity).setMinNumber(1)
        verify(passwordPolicyPasswordSettingsComplexity).setMinLowerCase(1)
        verify(passwordPolicyPasswordSettingsComplexity).setMinUpperCase(1)
        verify(passwordPolicyPasswordSettingsComplexity).setMinLength(8)
        verify(passwordPolicyPasswordSettingsComplexity).setExcludeUsername(false)
        verify(passwordDictionaryCommon).setExclude(false)
        verify(passwordPolicyAuthenticationProviderCondition).setProvider(PasswordPolicyAuthenticationProviderType.OKTA)
    }

    @Test
    void createWithNonPasswordPolicyType(){

        def client = mock(Client)
        def policy = mock(Policy)
        def passwordPolicy = mock(PasswordPolicy)
        when(client.instantiate(Policy.class)).thenReturn(policy)
        when(client.instantiate(PasswordPolicy.class)).thenReturn(passwordPolicy)
        List groupList = new ArrayList(Arrays.asList("dsfjhgsjhg"))
        expect IllegalArgumentException, {
            new DefaultPasswordPolicyBuilder()
                .setName("Test Password Policy")
                .setDescription("dummy policy for test")
                .setPriority(1)
                .setGroups(groupList)
                .setType(PolicyType.OKTA_SIGN_ON)
                .setStatus(LifecycleStatus.ACTIVE)
                .buildAndCreate(client)
        }
    }
}
