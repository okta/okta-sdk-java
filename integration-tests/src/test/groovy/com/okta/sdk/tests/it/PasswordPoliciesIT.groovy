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
package com.okta.sdk.tests.it


import com.okta.sdk.client.Client
import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.policy.*
import com.okta.sdk.tests.it.util.ITSupport

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class PasswordPoliciesIT extends ITSupport implements CrudTestSupport  {
    @Override
    def create(Client client) {
        Group group = randomGroup()
        Policy passwordPolicy = PasswordPolicyBuilder.instance()
                                    .setAuthProvider(PasswordPolicyAuthenticationProviderCondition.ProviderEnum.OKTA)
                                    .setExcludePasswordDictionary(false)
                                    .setExcludeUserNameInPassword(false)
                                    .setMinPasswordLength(8)
                                    .setMinLowerCase(1)
                                    .setMinUpperCase(1)
                                    .setMinNumbers(1)
                                    .setMinSymbols(1)
                                    .addGroup(group.getId())
                                    .setSkipUnlock(false)
                                    .setPasswordExpireWarnDays(85)
                                    .setPasswordHistoryCount(5)
                                    .setPasswordMaxAgeDays(90)
                                    .setPasswordMinMinutes(2)
                                    .setPasswordAutoUnlockMinutes(5)
                                    .setPasswordMaxAttempts(3)
                                    .setShowLockoutFailures(true)
                                    .setPasswordRecoveryOktaSMS(PasswordPolicyRecoveryFactorSettings.StatusEnum.ACTIVE)
                                    .setType(PolicyType.PASSWORD)
                                    .setStatus(Policy.StatusEnum.ACTIVE)
                                    .setPriority(1)
                                    .setDescription("Dummy policy for sdk test")
                                    .setName("SDK policy "+ UUID.randomUUID().toString())
                                    .buildAndCreate(client) ;
        return (PasswordPolicy) passwordPolicy;

    }

    @Override
    def read(Client client, String id) {
        return client.getPolicy(id)
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listPolicies(PolicyType.PASSWORD.toString()).iterator()
    }

    @Override
    void update(Client client, def policy) {
        policy.setDescription("Dummy policy for sdk test - Updated")
        policy.settings.password.lockout.maxAttempts = 5
        policy.update()

    }

    @Override
    void assertUpdate(Client client, def policy) {
        assertThat policy.description, is("Dummy policy for sdk test - Updated")
        assertThat policy.settings.password.lockout.maxAttempts, is(5)
        assertThat policy.settings.password.complexity.minLength, is(8)
        assertThat policy.settings.recovery.factors.okta_email.status, is("ACTIVE")
    }
}
