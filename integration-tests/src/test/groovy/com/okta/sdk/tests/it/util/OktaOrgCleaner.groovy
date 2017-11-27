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
package com.okta.sdk.tests.it.util

import com.okta.sdk.client.Client
import com.okta.sdk.client.Clients
import com.okta.sdk.resource.Deletable
import com.okta.sdk.resource.user.UserStatus

import java.util.stream.Stream
import java.util.stream.StreamSupport

class OktaOrgCleaner {

    static void main(String[] args) {

        String uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"

        Client client = Clients.builder().build()

        toStream(client.listUsers().iterator())
            .filter { it.getProfile().getEmail().endsWith("@example.com") }
            .forEach {
                it.deactivate()
                it.delete()
            }

        toStream(client.listUsers(null, null, null, "status eq \"${UserStatus.DEPROVISIONED}\"", null).iterator())
            .forEach { it.delete() }

        toStream(client.listApplications().iterator())
            .filter { it.getLabel().matches(".*-${uuidRegex}.*")}
            .forEach {
                it.deactivate()
                it.delete()
            }

        toStream(client.listGroups().iterator())
                .filter { it.getProfile().getName().matches(".*-${uuidRegex}.*")}
                .forEach {
            it.delete()
        }
    }

    static Stream<Deletable> toStream(Iterator<Deletable> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),false)
    }
}
