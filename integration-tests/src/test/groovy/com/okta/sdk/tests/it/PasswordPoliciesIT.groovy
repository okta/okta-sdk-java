/*
 * Copyright 2018-Present Okta, Inc.
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
import com.okta.sdk.resource.policy.OktaSignOnPolicy
import com.okta.sdk.resource.policy.PasswordPolicy
import com.okta.sdk.resource.policy.Policy
import com.okta.sdk.resource.policy.PolicyType

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class PasswordPoliciesIT implements CrudTestSupport {

    @Override
    def create(Client client) {
        return client.createPolicy(client.instantiate(PasswordPolicy)
            .setName("policy+" + UUID.randomUUID().toString())
            .setStatus(Policy.StatusEnum.ACTIVE)
            .setDescription("IT created Policy - password CRUD"))
    }

    @Override
    def read(Client client, String id) {
        return client.getPolicy(id)
    }

    @Override
    void update(Client client, def policy) {
        policy.setDescription("IT created Policy - Updated")
        policy.update()
    }

    @Override
    void assertUpdate(Client client, def policy) {
        assertThat policy.description, is("IT created Policy - Updated")
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listPolicies(PolicyType.PASSWORD.toString()).iterator()
    }
}
