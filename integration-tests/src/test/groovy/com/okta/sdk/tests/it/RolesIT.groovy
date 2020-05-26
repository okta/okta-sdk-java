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
package com.okta.sdk.tests.it

import com.okta.sdk.client.Client
import com.okta.sdk.resource.ExtensibleResource
import com.okta.sdk.resource.Resource
import com.okta.sdk.resource.ResourceException
import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.policy.PasswordPolicyPasswordSettings
import com.okta.sdk.resource.policy.PasswordPolicyPasswordSettingsAge
import com.okta.sdk.resource.policy.PasswordPolicyRule
import com.okta.sdk.resource.policy.PasswordPolicyRuleAction
import com.okta.sdk.resource.policy.PasswordPolicyRuleActions
import com.okta.sdk.resource.policy.PasswordPolicyRuleConditions
import com.okta.sdk.resource.policy.PasswordPolicySettings
import com.okta.sdk.resource.policy.PolicyNetworkCondition
import com.okta.sdk.resource.user.AuthenticationProviderType
import com.okta.sdk.resource.user.ChangePasswordRequest
import com.okta.sdk.resource.user.ForgotPasswordResponse
import com.okta.sdk.resource.user.PasswordCredential
import com.okta.sdk.resource.user.RecoveryQuestionCredential
import com.okta.sdk.resource.user.ResetPasswordToken
import com.okta.sdk.resource.user.Role
import com.okta.sdk.resource.user.TempPassword
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.resource.user.UserCredentials
import com.okta.sdk.resource.user.UserList
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.time.Duration
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.stream.Collectors

import static com.okta.sdk.tests.it.util.Util.assertGroupTargetPresent
import static com.okta.sdk.tests.it.util.Util.assertNotPresent
import static com.okta.sdk.tests.it.util.Util.assertPresent
import static com.okta.sdk.tests.it.util.Util.expect
import static com.okta.sdk.tests.it.util.Util.validateGroup
import static com.okta.sdk.tests.it.util.Util.validateUser
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * @since 2.0.0
 */
class RolesIT extends ITSupport implements CrudTestSupport {

    @BeforeClass
    void initCustomProperties() {
        ensureCustomProperties()
    }

    @Test
    void assignSuperAdminRoleTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Group-Target'
        def email = "john-${uniqueTestName}@example.com"
        def groupName = "Group-Target Test Group ${uniqueTestName}"

        // 1. Create a user
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(false)
            .buildAndCreate(client)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        Role role = user.addRole(client.instantiate(Role)
            .setType(''))
    }

    @Override
    def create(Client client) {
        def email = "joe.coder+${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Joe")
            .setLastName("Code")
            .setPassword("Password1".toCharArray())
            .setSecurityQuestion("Favorite security question?")
            .setSecurityQuestionAnswer("None of them!")
            .buildAndCreate(client)
        registerForCleanup(user)
        return user
    }

    @Override
    def read(Client client, String id) {
        return client.getUser(id)
    }

    @Override
    void update(Client client, def user) {
        user.getProfile().lastName = "Coder"
        user.update()
    }

    @Override
    void assertUpdate(Client client, Object resource) {
        assertThat resource.getProfile().lastName, equalTo("Coder")
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listUsers().iterator()
    }

    private void ensureCustomProperties() {
        def userSchemaUri = "/api/v1/meta/schemas/user/default"

        ExtensibleResource userSchema = getClient().http().get(userSchemaUri, ExtensibleResource)
        Map customProperties = userSchema.get("definitions").get("custom").get("properties")

        boolean needsUpdate =
            ensureCustomProperty("customNumber", [type: "number"], customProperties) &&
                ensureCustomProperty("customBoolean", [type: "boolean"], customProperties) &&
                ensureCustomProperty("customInteger", [type: "integer"], customProperties) &&
                ensureCustomProperty("customStringArray", [type: "array", items: [type: "string"]], customProperties) &&
                ensureCustomProperty("customNumberArray", [type: "array", items: [type: "number"]], customProperties) &&
                ensureCustomProperty("customIntegerArray", [type: "array", items: [type: "integer"]], customProperties)

        if (needsUpdate)  {
            getClient().http()
                .setBody(userSchema)
                .post(userSchemaUri, ExtensibleResource)
        }
    }

    private static boolean ensureCustomProperty(String name, Map body, Map<String, Object> customProperties) {
        boolean addProperty = !customProperties.containsKey(name)
        if (addProperty) {
            body.putAll([
                title: name
            ])
            customProperties.put(name, body)
        }
        return addProperty
    }

    private String hashPassword(String password, String salt) {
        def messageDigest = MessageDigest.getInstance("SHA-512")
        messageDigest.update(salt.getBytes(StandardCharsets.UTF_8))
        def bytes = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(bytes)
    }
}
