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
class GroupsIT {

    @Test
    void basicCrudTest() {

        Client client = Clients.builder().build()

        // get the size of initial list of users
        int preCount = getCount(client)

        // Create a group
        UserGroup group = GroupBuilder.INSTANCE
                .setName("my-user-group-" + UUID.randomUUID().toString())
                .setDescription("IT created Group")
                .buildAndCreate(client)
        def groupId = group.id

        // get the count of users after adding
        int count = getCount(client)
        assertThat count, is(preCount + 1)

        // update the group
        group.profile.description = "IT created Group - Updated"
        group.updateGroup(group) // TODO: this needs a body link

        count = getCount(client)
        assertThat count, is(preCount + 1)

        // delete the user
        group.deleteGroup()

        count = getCount(client)
        assertThat count, is(preCount)
    }

    int getCount(Client client) {
        return client.listGroups().iterator().size()
    }
}
