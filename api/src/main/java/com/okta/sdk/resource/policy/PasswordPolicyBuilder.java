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
package com.okta.sdk.resource.policy;

import com.okta.commons.lang.Classes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface PasswordPolicyBuilder extends PolicyBuilder<PasswordPolicyBuilder> {

    static PasswordPolicyBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.DefaultPasswordPolicyBuilder");
    }

    PasswordPolicyBuilder setAuthProvider(PasswordPolicyAuthenticationProviderCondition.ProviderEnum provider);

    default PasswordPolicyBuilder setGroups(String... groupIds) {
        return setGroups(Arrays.stream(groupIds).collect(Collectors.toList()));
    }

    PasswordPolicyBuilder setGroups(List<String> groupIds);

    PasswordPolicyBuilder addGroup(String groupId);

    default PasswordPolicyBuilder setUsers(String... userIds) {
        return setGroups(Arrays.stream(userIds).collect(Collectors.toList()));
    }

    PasswordPolicyBuilder setUsers(List<String> userIds);

    PasswordPolicyBuilder addUser(String userId);

    PasswordPolicyBuilder setExcludePasswordDictionary(Boolean excludePasswordDictionary);

    PasswordPolicyBuilder setExcludeUserNameInPassword(Boolean excludeUserNameInPassword);

    PasswordPolicyBuilder setMinPasswordLength(Integer minPasswordLength);

    PasswordPolicyBuilder setMinLowerCase(Integer minLowerCase);

    PasswordPolicyBuilder setMinUpperCase(Integer minUpperCase);

    PasswordPolicyBuilder setMinNumbers(Integer minNumbers);

    PasswordPolicyBuilder setMinSymbols(Integer minSymbols);

    PasswordPolicyBuilder setSkipUnlock(Boolean skipUnlock);

    PasswordPolicyBuilder setPasswordExpireWarnDays(Integer pwdExpireWarnDays);

    PasswordPolicyBuilder setPasswordHistoryCount(Integer pwdHistoryCount);

    PasswordPolicyBuilder setPasswordMaxAgeDays(Integer pwdMaxAgeDays);

    PasswordPolicyBuilder setPasswordMinMinutes(Integer pwdMinMinutes);

    PasswordPolicyBuilder setPasswordAutoUnlockMinutes(Integer pwdAutoUnlockMinutes);

    PasswordPolicyBuilder setPasswordMaxAttempts(Integer pwdMaxAttempts);

    PasswordPolicyBuilder setShowLockoutFailures(Boolean showLockoutFailures);

    PasswordPolicyBuilder setPasswordRecoveryOktaCall(PasswordPolicyRecoveryFactorSettings.StatusEnum pwdRecoveryOktaCall);

    PasswordPolicyBuilder setPasswordRecoveryOktaSMS(PasswordPolicyRecoveryFactorSettings.StatusEnum pwdRecoveryOktaSMS);

    PasswordPolicyBuilder setPasswordPolicyRecoveryEmailStatus(PasswordPolicyRecoveryFactorSettings.StatusEnum status);

    PasswordPolicyBuilder setPasswordRecoveryTokenLifeMinutes(Integer pwdRecoveryTokenLifeMinutes);
}
