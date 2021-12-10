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
import com.okta.sdk.resource.policy.PolicyType
import org.testng.annotations.Test

import static com.okta.sdk.impl.Util.expect
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class DefaultPolicyBuilderTest {

    @Test
    void basicUsage() {

        def client = mock(Client)
        def policy = mock(Policy)
        when(client.instantiate(Policy.class)).thenReturn(policy)

        new DefaultPolicyBuilder()
            .setName("Test Policy")
            .setDescription("dummy policy for test")
            .setPriority(1)
            .setType(PolicyType.OKTA_SIGN_ON)
            .setStatus(LifecycleStatus.ACTIVE)
            .buildAndCreate(client)

        verify(client).createPolicy(eq(policy), eq(true))
        verify(policy).setName("Test Policy")
        verify(policy).setDescription("dummy policy for test")
    }

    @Test
    void createWithoutPolicyType() {
        def client = mock(Client)
        def policy = mock(Policy)
        when(client.instantiate(Policy.class)).thenReturn(policy)

        expect IllegalArgumentException, {
            new DefaultPolicyBuilder()
                .setName("Test Policy")
                .setDescription("dummy policy for test")
                .setPriority(1)
                .setStatus(LifecycleStatus.ACTIVE)
                .buildAndCreate(client)
        }
    }
}
