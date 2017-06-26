package com.okta.sdk.tests.it.spec.steps

import com.okta.sdk.client.Client
import com.okta.sdk.resource.ResourceException
import com.okta.sdk.resource.user.ChangePasswordRequest
import com.okta.sdk.resource.user.PasswordCredential
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.resource.user.UserStatus
import com.okta.sdk.tests.it.util.ClientProvider
import com.okta.sdk.tests.it.util.Util
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static com.okta.sdk.tests.it.util.Util.validateUser

class UserStepsdefs implements ClientProvider {

    private User user
    private String originalPassword

    @Given("^A user exists:")
    void createTestUser(List<UserInfo> userInfos) throws Throwable {

        userInfos.each { userInfo ->
            // clean before create
            Util.ignoring(ResourceException) {
                User user = client.getUser(userInfo.email)
                if (user.status != UserStatus.DEPROVISIONED) {
                    user.deactivate()
                }
                user.delete()
            }
        }

        userInfos.each { userInfo ->
            originalPassword = userInfo.password
            user = UserBuilder.instance()
                    .setEmail(userInfo.email)
                    .setFirstName(userInfo.firstName)
                    .setLastName(userInfo.lastName)
                    .setPassword(userInfo.password)
                    .buildAndCreate(client, userInfo.active)

            registerForCleanup(user)
            validateUser(user, userInfo.firstName, userInfo.lastName, userInfo.email)
        }
    }

    @When("^The user's password is updated to (.*)")
    void updateUserPassword(String newPassword) throws Throwable {

        assertThat user, notNullValue()
        Client client = getClient()
//        sleep(1000) // minimum lastUpdated resolution is 1 second

        ChangePasswordRequest request = client.instantiate(ChangePasswordRequest)
        request.setOldPassword(client.instantiate(PasswordCredential).setValue(originalPassword))
        request.setNewPassword(client.instantiate(PasswordCredential).setValue(newPassword))
        user.changePassword(request)
    }

    @Then("^Users passwordChanged field is updated")
    void validateUserLastUpdateField() throws Throwable {
        assertThat(client.getUser(user.profile.email).passwordChanged, greaterThan(user.passwordChanged))
    }
}
