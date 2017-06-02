/*
 * Copyright 2017 Okta
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
import com.okta.sdk.client.Clients
import com.okta.sdk.resource.GroupBuilder
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

/**
 * Tests for /api/v1/groups
 * @since 0.5.0
 */
class GroupsIT implements CrudTestSupport {

    @Override
    def create(Client client) {
        return GroupBuilder.INSTANCE
                .setName("my-user-group-" + UUID.randomUUID().toString())
                .setDescription("IT created Group")
                .buildAndCreate(client)
    }

    @Override
    def read(Client client, String id) {
        return client.getGroup(id)
    }

    @Override
    void update(Client client, def group) {
        group.profile.description = "IT created Group - Updated"
        group.updateGroup(group) // TODO: this needs a body link
    }

    @Override
    void delete(Client client, def group) {
        group.deleteGroup()
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listGroups().iterator()
    }
}
