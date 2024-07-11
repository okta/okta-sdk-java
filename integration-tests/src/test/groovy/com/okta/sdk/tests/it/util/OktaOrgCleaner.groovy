/*
 * Copyright 2017-Present Okta, Inc.
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

import com.okta.sdk.client.Clients
import com.okta.sdk.resource.client.ApiClient
import com.okta.sdk.resource.api.ApplicationApi
import com.okta.sdk.resource.api.AuthorizationServerApi
import com.okta.sdk.resource.api.EventHookApi
import com.okta.sdk.resource.api.GroupApi
import com.okta.sdk.resource.api.IdentityProviderApi
import com.okta.sdk.resource.api.InlineHookApi
import com.okta.sdk.resource.api.LinkedObjectApi
import com.okta.sdk.resource.api.PolicyApi
import com.okta.sdk.resource.api.TemplateApi
import com.okta.sdk.resource.api.UserApi
import com.okta.sdk.resource.api.UserTypeApi
import com.okta.sdk.resource.model.GroupRule

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OktaOrgCleaner {

    private final static Logger log = LoggerFactory.getLogger(OktaOrgCleaner)

    static void main(String[] args) {

        String prefix = "java-sdk-it-"
        String uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"

        ApiClient client = Clients.builder().build()

        log.info("Cleaning Okta Org: {}", client.getBasePath())

        UserApi userApi = new UserApi(client)

        log.info("Deleting Active Users:")
        userApi.listUsers(null, null, null, 'status eq \"ACTIVE\"', null, null, null)
            .stream()
            .filter { it.getProfile().getEmail().endsWith("@example.com") }
            .forEach {
                log.info("\t ${it.getProfile().getEmail()}")
                // deactivate
                userApi.deactivateUser(it.getId(),false)
                // delete
                userApi.deleteUser(it.getId(), false)
            }

        userApi.listUsers(null, null, null, 'status eq \"DEPROVISIONED\"', null, null, null)
            .forEach {
                log.info("Deleting deactivated user: ${it.getProfile().getEmail()}")
                userApi.deleteUser(it.getId(), false)
            }

        ApplicationApi applicationApi = new ApplicationApi(client)

        log.info("Deleting Applications:")
        applicationApi.listApplications(null, null, 100, null, null, true).stream()
            .filter { it.getLabel().startsWith(prefix) && it.getLabel().matches(".*-${uuidRegex}.*") }
            .forEach {
                log.info("\t ${it.getLabel()}")
                applicationApi.deactivateApplication(it.getId())
                applicationApi.deleteApplication(it.getId())
            }

        GroupApi groupApi = new GroupApi(client)

        log.info("Deleting Groups:")
        groupApi.listGroups(null, null, null, 100, null, null, null, null).stream()
            .filter { it.getProfile().getName().matches(".*-${uuidRegex}.*") }
            .forEach {
                log.info("\t ${it.getProfile().getName()}")
                groupApi.deleteGroup(it.getId())
            }

        log.info("Deleting Group Rules:")
        groupApi.listGroupRules(1000, null, null, null).stream()
            .filter { it.getName().startsWith(prefix) && it.getName().matches(".*-${uuidRegex}.*") }
            .forEach {
                GroupRule rule = it
                log.info("\t ${rule.getName()}")
                Util.ignoring(ResourceException) {
                    groupApi.deactivateGroupRule(rule.getId())
                }
                groupApi.deleteGroupRule(rule.getId(), false)
            }

        PolicyApi policyApi = new PolicyApi(client)

        log.info("Deleting Policies:")
        policyApi.listPolicies("OKTA_SIGN_ON", null, null, null, null, null).stream()
            .filter { it.getName().startsWith(prefix) && it.getName().matches(".*-${uuidRegex}.*") }
            .forEach {
                log.info("\t ${it.getName()}")
                policyApi.deactivatePolicy(it.getId())
                policyApi.deletePolicy(it.getId())
            }

        LinkedObjectApi linkedObjectApi = new LinkedObjectApi(client)

        log.info("Deleting LinkedObjectDefinitions:")
        linkedObjectApi.listLinkedObjectDefinitions().stream()
            .filter { it.getPrimary().getName().startsWith("java_sdk_it_") }
            .forEach {
                log.info("\t ${it.getPrimary().getName()}")
                linkedObjectApi.deleteLinkedObjectDefinition(it.getPrimary().getName())
            }

        InlineHookApi inlineHookApi = new InlineHookApi(client)

        log.info("Deleting InlineHooks:")
        inlineHookApi.listInlineHooks(null).stream()
            .filter { it.getName().startsWith(prefix) && it.getName().matches(".*-${uuidRegex}.*") }
            .forEach {
                log.info("\t ${it.getName()}")
                inlineHookApi.deactivateInlineHook(it.getId())
                inlineHookApi.deleteInlineHook(it.getId())
            }

        EventHookApi eventHookApi = new EventHookApi(client)

        log.info("Deleting EventHooks:")
        eventHookApi.listEventHooks().stream()
            .filter { it.getName().startsWith(prefix) && it.getName().matches(".*-${uuidRegex}.*") }
            .forEach {
                log.info("\t ${it.getName()}")
                eventHookApi.deactivateEventHook(it.getId())
                eventHookApi.deleteEventHook(it.getId())
            }

        UserTypeApi userTypeApi = new UserTypeApi(client)

        log.info("Deleting UserTypes:")
        userTypeApi.listUserTypes().stream()
            .filter { it.getName().startsWith(prefix.replaceAll("-","_")) && !it.getDefault() }
            .forEach {
                log.info("\t ${it.getName()}")
                userTypeApi.deleteUserType(it.getId())
            }

        AuthorizationServerApi authorizationServerApi = new AuthorizationServerApi(client)

        log.info("Deleting AuthorizationServers:")
        authorizationServerApi.listAuthorizationServers(null, 100, null).stream()
            .filter { it.getName().startsWith(prefix) && it.getName().matches(".*-${uuidRegex}.*") }
            .forEach {
                log.info("\t ${it.getName()}")
                authorizationServerApi.deactivateAuthorizationServer(it.getId())
                authorizationServerApi.deleteAuthorizationServer(it.getId())
            }

        IdentityProviderApi identityProviderApi = new IdentityProviderApi(client)

        log.info("Deleting IdentityProviders:")
        identityProviderApi.listIdentityProviders(null, null, 100, null).stream()
            .filter { it.getName().startsWith(prefix) && it.getName().matches(".*-${uuidRegex}.*") }
            .forEach {
                log.info("\t ${it.getName()}")
                identityProviderApi.deactivateIdentityProvider(it.getId())
                identityProviderApi.deleteIdentityProvider(it.getId())
            }

        TemplateApi templateApi = new TemplateApi(client)

        log.info("Deleting SmsTemplates:")
        templateApi.listSmsTemplates(null).stream()
            .filter { it.getName().matches(".*-${uuidRegex}.*") }
            .forEach {
                log.info("\t ${it.getName()}")
                templateApi.deleteSmsTemplate(it.getId())
            }
    }
}