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
package com.okta.sdk.resource.group.rule;

import com.okta.commons.lang.Classes;
import com.okta.sdk.client.Client;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface GroupRuleBuilder {

    static GroupRuleBuilder instance(){ return Classes.newInstance("com.okta.sdk.impl.resource.DefaultGroupRuleBuilder");}

    GroupRuleBuilder setName(String name);

    GroupRuleBuilder setType(String type);

    GroupRuleBuilder setAssignUserToGroups(List<String> assignUserToGroups);

    default GroupRuleBuilder setGroups(String... groupIds) {
        return setGroups(Arrays.stream(groupIds).collect(Collectors.toList()));
    }

    GroupRuleBuilder setGroups(List<String> groupIds);

    GroupRuleBuilder addGroup(String groupId);

    default GroupRuleBuilder setUsers(String... userIds) {
        return setUsers(Arrays.stream(userIds).collect(Collectors.toList()));
    }

    GroupRuleBuilder setUsers(List<String> userIds);

    GroupRuleBuilder addUser(String userId);

    GroupRuleBuilder setGroupRuleExpressionType(String groupRuleExpressionType);

    GroupRuleBuilder setGroupRuleExpressionValue(String groupRuleExpressionValue);

    GroupRule buildAndCreate(Client client);


}
