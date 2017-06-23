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
import com.okta.sdk.resource.user.UserBuilder

/**
 * Tests for /api/v1/users
 * @since 0.5.0
 */
class UsersIT implements CrudTestSupport {

    def email = "joe.coder+" + UUID.randomUUID().toString() + "@example.com"

    @Override
    def create(Client client) {
        return UserBuilder.instance()
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
