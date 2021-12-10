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
import com.okta.sdk.resource.policy.*
import org.testng.annotations.Test

import static com.okta.sdk.impl.Util.expect
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class DefaultOktaSignOnPolicyBuilderTest {

    @Test
    void basicUsage(){
        def client = mock(Client)
        def policy = mock(Policy)
        def oktaSignOnPolicy = mock(OktaSignOnPolicy)
        def oktaSignOnPolicyConditions = mock(OktaSignOnPolicyConditions)
        def policyPeopleCondition = mock(PolicyPeopleCondition)
        def groupCondition = mock(GroupCondition)

        when(client.instantiate(Policy.class)).thenReturn(policy)
        when(client.instantiate(OktaSignOnPolicy.class)).thenReturn(oktaSignOnPolicy)
        when(client.instantiate(OktaSignOnPolicyConditions)).thenReturn(oktaSignOnPolicyConditions)
        when(client.instantiate(PolicyPeopleCondition.class)).thenReturn(policyPeopleCondition)
        when(client.instantiate(GroupCondition.class)).thenReturn(groupCondition)
        when(oktaSignOnPolicy.getConditions()).thenReturn(oktaSignOnPolicyConditions)

        new DefaultOktaSignOnPolicyBuilder()
            .setName("dummy_policy")
            .setDescription("dummy")
            .setPriority(1)
            .setStatus(LifecycleStatus.ACTIVE)
            .setType(PolicyType.OKTA_SIGN_ON)
            .addGroup("abcdef123456")
            .buildAndCreate(client);

        verify(client).createPolicy(eq(oktaSignOnPolicy), eq(true))
        verify(oktaSignOnPolicy).setName("dummy_policy")
        verify(oktaSignOnPolicy).setDescription("dummy")
        verify(oktaSignOnPolicy).setPriority(1)
        verify(oktaSignOnPolicy).setStatus(LifecycleStatus.ACTIVE)
        verify(oktaSignOnPolicy).setType(PolicyType.OKTA_SIGN_ON)
        verify(groupCondition).setInclude(new ArrayList<String>(Arrays.asList("abcdef123456")))

    }

    @Test
    void createWithNonOktaSignOnPolicyType(){

        def client = mock(Client)
        def policy = mock(Policy)
        def oktaSignOnPolicy = mock(OktaSignOnPolicy)
        when(client.instantiate(Policy.class)).thenReturn(policy)
        when(client.instantiate(OktaSignOnPolicy.class)).thenReturn(oktaSignOnPolicy)
        List groupList = new ArrayList(Arrays.asList("dsfjhgsjhg"))
        expect IllegalArgumentException, {
            new DefaultOktaSignOnPolicyBuilder()
                .setName("Test Policy")
                .setDescription("dummy policy for test")
                .setPriority(1)
                .setGroups(groupList)
                .setType(PolicyType.PASSWORD)
                .setStatus(LifecycleStatus.ACTIVE)
                .buildAndCreate(client)
        }
    }

}
