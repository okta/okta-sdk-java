package com.okta.sdk.tests.it

import com.okta.sdk.client.Client
import com.okta.sdk.resource.User
import com.okta.sdk.resource.UserBuilder

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

/**
 * Tests for /api/v1/users
 * @since 0.5.0
 */
class UsersIT implements CrudTestSupport {

    def email = "joe.coder+" + UUID.randomUUID().toString() + "@example.com"

    @Override
    def create(Client client) {
        return UserBuilder.INSTANCE
                .setEmail(email)
                .setFirstName("Joe")
                .setLastName("Code")
                .setPassword("Password1")
                .setSecurityQuestion("Favorite security question?")
                .setSecurityQuestionAnswer("None of them!")
                .buildAndCreate(client)
    }

    @Override
    def read(Client client, String id) {
        return client.getUser(id)
    }

    @Override
    void update(Client client, def user) {
        user.profile.lastName = "Coder"
        user.update()
    }

    @Override
    void delete(Client client, def user) {
        user.delete()
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listUsers().iterator()
    }
}
