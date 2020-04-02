package com.okta.sdk.resource.policy;

import com.okta.commons.lang.Classes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface PasswordPolicyBuilder extends PolicyBuilder{

    static PasswordPolicyBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.DefaultPasswordPolicyBuilder");
    }

    PasswordPolicyBuilder setAuthProvider(PasswordPolicyAuthenticationProviderCondition.ProviderEnum provider);

    default PasswordPolicyBuilder setGroups(String... groupIds) {
        return setGroups(Arrays.stream(groupIds).collect(Collectors.toList()));
    }

    PasswordPolicyBuilder setGroups(List<String> groupIds);

    PasswordPolicyBuilder addGroup(String groupId);

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
