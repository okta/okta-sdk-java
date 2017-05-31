package com.okta.sdk.tests.it

import com.okta.sdk.client.Client
import com.okta.sdk.client.Clients
import com.okta.sdk.resource.GroupBuilder
import com.okta.sdk.resource.User
import com.okta.sdk.resource.UserBuilder
import com.okta.sdk.resource.UserGroup
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
