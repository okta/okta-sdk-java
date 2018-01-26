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
import com.okta.sdk.resource.ResourceException
import com.okta.sdk.resource.group.rule.GroupRule
import com.okta.sdk.resource.user.UserStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OktaOrgCleaner {

    private final static Logger log = LoggerFactory.getLogger(OktaOrgCleaner)
    
    static void main(String[] args) {

        String uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"

        Client client = Clients.builder().build()

        log.info("Deleting Active Users:")
        client.listUsers().stream()
            .filter { it.getProfile().getEmail().endsWith("@example.com") }
            .forEach {
                log.info("\t ${it.getProfile().getEmail()}")
                it.deactivate()
                it.delete()
            }

        client.listUsers(null, null, null, "status eq \"${UserStatus.DEPROVISIONED}\"", null).stream()
            .forEach {
                log.info("Deleting deactivated user: ${it.getProfile().getEmail()}")
                it.delete()
            }

        log.info("Deleting Applications:")
        client.listApplications().stream()
            .filter { it.getLabel().matches(".*-${uuidRegex}.*")}
            .forEach {
                log.info("\t ${it.getLabel()}")
                it.deactivate()
                it.delete()
            }

        log.info("Deleting Groups:")
        client.listGroups().stream()
                .filter { it.getProfile().getName().matches(".*-${uuidRegex}.*")}
                .forEach {
                    log.info("\t ${it.getProfile().getName()}")
                    it.delete()
                }

        log.info("Deleting Group Rules:")
        client.listRules().stream()
                .filter { it.getName().matches("rule\\+${uuidRegex}.*")}
                .forEach {
                    GroupRule rule = it
                    log.info("\t ${rule.getName()}")
                    Util.ignoring(ResourceException) {
                        rule.deactivate()
                    }
                    rule.delete()
                }
    }
}