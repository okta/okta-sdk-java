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
import com.okta.sdk.resource.policy.Policy
import com.okta.sdk.resource.policy.PolicyRule
import org.testng.annotations.Test

import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class DefaultPolicyRuleBuilderTest {

    @Test
    void basicUsage(){
        def policy = mock(Policy)
        def client = mock(Client)
        def policyRule = mock(PolicyRule)

        when(client.instantiate(Policy.class)).thenReturn(policy)
        when(client.instantiate(PolicyRule.class)).thenReturn(policyRule)

        new DefaultPolicyRuleBuilder()
            .setStatus(PolicyRule.StatusEnum.ACTIVE)
            .setPriority(1)
        .buildAndCreate(client, policy)

        verify(policy).createRule(eq(policyRule))
        verify(policyRule).setPriority(1)
        verify(policyRule).setStatus(PolicyRule.StatusEnum.ACTIVE)

    }
}
