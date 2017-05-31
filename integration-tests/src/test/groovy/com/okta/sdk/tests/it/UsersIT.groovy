package com.okta.sdk.tests.it

import com.okta.sdk.client.Client
import com.okta.sdk.client.Clients
import com.okta.sdk.resource.User
import com.okta.sdk.resource.UserBuilder
import org.testng.annotations.Test
import org.testng.collections.Lists

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

/**
 * Tests for /api/v1/users
 * @since 0.5.0
 */
class UsersIT {

    @Test
    void basicCrudTest() {

        Client client = Clients.builder().build()

        // get the size of initial list of users
        int preUserCount = getUserCount(client)

        // create a user
        String email = "joe.coder+" + UUID.randomUUID().toString() + "@example.com"
        User user = UserBuilder.INSTANCE
                .setEmail(email)
                .setFirstName("Joe")
                .setLastName("Code")
                .setPassword("Password1")
                .setSecurityQuestion("Favorite security question?")
                .setSecurityQuestionAnswer("None of them!")
                .buildAndCreate(client)
        def userId = user.id

        // get the count of users after adding
        int userCount = getUserCount(client)
        assertThat userCount, is(preUserCount + 1)

        // update the user name
        user.profile.lastName = "Coder"
        user.updateUser(user) // TODO: this needs a body link

        userCount = getUserCount(client)
        assertThat userCount, is(preUserCount + 1)

        // delete the user
        user.deactivateOrDeleteUser()

        userCount = getUserCount(client)
        assertThat userCount, is(preUserCount)
    }

    int getUserCount(Client client) {
        return client.listUsers().iterator().size()
    }
}
