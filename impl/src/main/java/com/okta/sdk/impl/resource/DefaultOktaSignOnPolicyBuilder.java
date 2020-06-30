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
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.policy.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultOktaSignOnPolicyBuilder extends DefaultPolicyBuilder<OktaSignOnPolicyBuilder> implements OktaSignOnPolicyBuilder {

    private List<String> groupIds = new ArrayList<>();
    private List<String> userIds = new ArrayList<>();

    @Override
    public OktaSignOnPolicyBuilder setGroups(List<String> groupIds) {
        this.groupIds = groupIds;
        return this;
    }

    @Override
    public OktaSignOnPolicyBuilder addGroup(String groupId) {
        this.groupIds.add(groupId);
        return this;
    }

    @Override
    public OktaSignOnPolicyBuilder setUsers(List<String> userIds) {
        this.userIds = userIds;
        return this;
    }

    @Override
    public OktaSignOnPolicyBuilder addUser(String userId) {
        this.userIds.add(userId);
        return this;
    }

    @Override
    public OktaSignOnPolicy buildAndCreate(Client client) {
        return (OktaSignOnPolicy) client.createPolicy(build(client), isActive);
    }

    private OktaSignOnPolicy build(Client client){
        OktaSignOnPolicy policy = client.instantiate(OktaSignOnPolicy.class);

        if (Strings.hasText(name)) policy.setName(name);
        if (Strings.hasText(description)) policy.setDescription(description);
        if (priority != null)
            policy.setPriority(priority);

        if (PolicyType.OKTA_SIGN_ON.equals(policyType))
            policy.setType(policyType);
        else
            throw new IllegalArgumentException("PolicyType should be 'OKTA_SIGN_ON', please use PolicyBuilder for other policy types.");

        if (Objects.nonNull(status))
            policy.setStatus(status);

        policy.setConditions(client.instantiate(OktaSignOnPolicyConditions.class));
        OktaSignOnPolicyConditions oktaSignOnPolicyConditions = policy.getConditions();

        if (!Collections.isEmpty(groupIds))
            oktaSignOnPolicyConditions.setPeople(client.instantiate(PolicyPeopleCondition.class)
                .setGroups(client.instantiate(GroupCondition.class).setInclude(groupIds)));

        if (!Collections.isEmpty(userIds))
            oktaSignOnPolicyConditions.setPeople(client.instantiate(PolicyPeopleCondition.class)
                .setUsers(client.instantiate(UserCondition.class).setInclude(userIds)));

        return policy;
    }
}
