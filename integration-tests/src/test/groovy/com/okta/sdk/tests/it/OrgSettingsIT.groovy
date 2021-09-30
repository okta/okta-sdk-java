/*
 * Copyright 2021-Present Okta, Inc.
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

import com.okta.sdk.resource.org.OrgContactType
import com.okta.sdk.resource.org.OrgContactTypeObj
import com.okta.sdk.resource.org.OrgContactUser
import com.okta.sdk.resource.org.OrgOktaSupportSetting
import com.okta.sdk.resource.org.OrgSetting
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test
import org.testng.util.Strings
import wiremock.org.apache.commons.lang3.RandomStringUtils

import java.util.concurrent.TimeUnit

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/org}.
 * @since 6.x.x
 */
class OrgSettingsIT extends ITSupport {

    @Test (groups = "group1")
    @Scenario("get-partial-update-org-settings")
    void getPartialUpdateOrgSettingsTest() {

        OrgSetting orgSetting = client.getOrgSettings()
        assertThat(orgSetting, notNullValue())
        assertThat(orgSetting.getId(), notNullValue())
        assertThat(orgSetting.getSubdomain(), notNullValue())
        assertThat(orgSetting.getCompanyName(), notNullValue())
        assertThat(orgSetting.getStatus(), notNullValue())
        assertThat(orgSetting.getCreated(), notNullValue())
        assertThat(orgSetting.getWebsite(), notNullValue())

        String companyName = orgSetting.getCompanyName()
        String website = orgSetting.getWebsite()
        String phoneNumber = Strings.getValueOrEmpty(orgSetting.getPhoneNumber())
        String endUserSupportHelpURL = Strings.getValueOrEmpty(orgSetting.getEndUserSupportHelpURL())
        String supportPhoneNumber = Strings.getValueOrEmpty(orgSetting.getSupportPhoneNumber())
        String address1 = Strings.getValueOrEmpty(orgSetting.getAddress1())
        String address2 = Strings.getValueOrEmpty(orgSetting.getAddress2())
        String city = Strings.getValueOrEmpty(orgSetting.getCity())
        String state = orgSetting.getState()
        String country = orgSetting.getCountry()
        String postalCode = Strings.getValueOrEmpty(orgSetting.getPostalCode())

        String customCompanyName = "Java SDK IT " + RandomStringUtils.randomAlphanumeric(5)

        orgSetting.setCompanyName(customCompanyName)
        orgSetting.setWebsite("https://okta.com")
        orgSetting.setPhoneNumber("+1-555-415-1337")
        orgSetting.setEndUserSupportHelpURL("https://support.okta.com")
        orgSetting.setSupportPhoneNumber("+1-555-514-1337")
        orgSetting.setAddress1("301 Brannan St.")
        orgSetting.setAddress2("Unit 100")
        orgSetting.setCity("San Francisco")
        orgSetting.setState("California")
        orgSetting.setCountry("United States of America")
        orgSetting.setPostalCode("94107")

        OrgSetting updatedOrgSetting = orgSetting.partialUpdate()
        assertThat(updatedOrgSetting.getCompanyName(), equalTo(customCompanyName))
        assertThat(updatedOrgSetting.getWebsite(), equalTo("https://okta.com"))
        assertThat(updatedOrgSetting.getPhoneNumber(), equalTo("+1-555-415-1337"))
        assertThat(updatedOrgSetting.getEndUserSupportHelpURL(), equalTo("https://support.okta.com"))
        assertThat(updatedOrgSetting.getSupportPhoneNumber(), equalTo("+1-555-514-1337"))
        assertThat(updatedOrgSetting.getAddress1(), equalTo("301 Brannan St."))
        assertThat(updatedOrgSetting.getAddress2(), equalTo("Unit 100"))
        assertThat(updatedOrgSetting.getCity(), equalTo("San Francisco"))
        assertThat(updatedOrgSetting.getState(), equalTo("California"))
        assertThat(updatedOrgSetting.getCountry(), equalTo("United States of America"))
        assertThat(updatedOrgSetting.getPostalCode(), equalTo("94107"))

        //revert settings to previous state
        orgSetting.setCompanyName(companyName)
        orgSetting.setWebsite(website)
        orgSetting.setPhoneNumber(phoneNumber)
        orgSetting.setEndUserSupportHelpURL(endUserSupportHelpURL)
        orgSetting.setSupportPhoneNumber(supportPhoneNumber)
        orgSetting.setAddress1(address1)
        orgSetting.setAddress2(address2)
        orgSetting.setCity(city)
        orgSetting.setState(state)
        orgSetting.setCountry(country)
        orgSetting.setPostalCode(postalCode)
        orgSetting.partialUpdate()
    }

    @Test (groups = "group2")
    @Scenario("get-org-contacts")
    void getOrgContactsTest() {
        def contactTypes = client.getOrgContactTypes()

        contactTypes.asList().stream().forEach(contact -> {
            assertThat(contact, instanceOf(OrgContactTypeObj))
            assertThat(contact.getContactType(), instanceOf(OrgContactType))
            assertThat(contact.getContactType(), oneOf(OrgContactType.BILLING, OrgContactType.TECHNICAL))
        })
    }

    @Test (groups = "group3")
    @Scenario("get-user-of-contact-type-test")
    void getUserOfContactTypeTest() {
        def orgContactUser = client.getOrgContactUser(OrgContactType.BILLING.toString())
        assertThat(orgContactUser, notNullValue())
        assertThat(orgContactUser.getUserId(), notNullValue())

        User billingUser = client.getUser(orgContactUser.getUserId())
        assertThat(billingUser, notNullValue())
        assertThat(billingUser.getId(), equalTo(orgContactUser.getUserId()))

        String newBillingUserEmail = "joe.coder.billing.user.${RandomStringUtils.randomAlphanumeric(5)}@example.com"
        User newBillingUser = UserBuilder.instance()
            .setEmail(newBillingUserEmail)
            .setFirstName("Joe")
            .setLastName("Code")
            .setPassword("Password1".toCharArray())
            .setSecurityQuestion("Favorite security question?")
            .setSecurityQuestionAnswer("None of them!")
            .buildAndCreate(client)
        registerForCleanup(newBillingUser)

        def updatedOrgContactUser = client.instantiate(OrgContactUser)
            .setUserId(newBillingUser.getId()).updateContactUser(OrgContactType.BILLING.toString())
        assertThat(updatedOrgContactUser, notNullValue())
        assertThat(updatedOrgContactUser.getUserId(), equalTo(newBillingUser.getId()))

        //restore previous value
        updatedOrgContactUser = client.instantiate(OrgContactUser)
            .setUserId(billingUser.getId()).updateContactUser(OrgContactType.BILLING.toString())
        assertThat(updatedOrgContactUser, notNullValue())
        assertThat(updatedOrgContactUser.getUserId(), equalTo(billingUser.getId()))
    }

    @Test (groups = "group1")
    @Scenario("get-org-preferences-test")
    void getOrgPreferencesTest() {
        def orgPreferences = client.getOrgPreferences()
        assertThat(orgPreferences, notNullValue())
        assertThat(orgPreferences.getShowEndUserFooter(), notNullValue())
        assertThat(orgPreferences.getShowEndUserFooter(), oneOf(true, false))

        boolean showEndUserFooter = orgPreferences.getShowEndUserFooter()
        if(showEndUserFooter) {
            orgPreferences.hideEndUserFooter()
            assertThat(client.getOrgPreferences().getShowEndUserFooter(), is(false))
            //revert to previous state
            orgPreferences.showEndUserFooter()
            assertThat(client.getOrgPreferences().getShowEndUserFooter(), is(true))
        } else {
            orgPreferences.showEndUserFooter()
            assertThat(client.getOrgPreferences().getShowEndUserFooter(), is(true))
            //revert to previous state
            orgPreferences.hideEndUserFooter()
            assertThat(client.getOrgPreferences().getShowEndUserFooter(), is(false))
        }
    }

    @Test (groups = "group2")
    @Scenario("get-okta-communication-settings-test")
    void getOktaCommunicationSettingsTest() {
        def commSettings = client.getOktaCommunicationSettings()
        assertThat(commSettings, notNullValue())
        assertThat(commSettings.getOptOutEmailUsers(), notNullValue())
        assertThat(commSettings.getOptOutEmailUsers(), oneOf(true, false))

        boolean optOutEmailUsers = commSettings.getOptOutEmailUsers()
        if(optOutEmailUsers) {
            commSettings.optInUsersToOktaCommunicationEmails()
            assertThat(client.getOktaCommunicationSettings().getOptOutEmailUsers(), is(false))
            //revert to previous state
            commSettings.optOutUsersFromOktaCommunicationEmails()
            assertThat(client.getOktaCommunicationSettings().getOptOutEmailUsers(), is(true))
        } else {
            commSettings.optOutUsersFromOktaCommunicationEmails()
            assertThat(client.getOktaCommunicationSettings().getOptOutEmailUsers(), is(true))
            //revert to previous state
            commSettings.optInUsersToOktaCommunicationEmails()
            assertThat(client.getOktaCommunicationSettings().getOptOutEmailUsers(), is(false))
        }
    }

    @Test (groups = "group3")
    @Scenario("get-org-okta-support-settings-test")
    void getOrgOktaSupportSettingsTest() {
        def supportSettings = client.getOrgOktaSupportSettings()
        assertThat(supportSettings, notNullValue())
        assertThat(supportSettings.getSupport(), notNullValue())
        assertThat(supportSettings.getSupport(), oneOf(OrgOktaSupportSetting.ENABLED, OrgOktaSupportSetting.DISABLED))

        def orgOktaSupportSettings = supportSettings.getSupport()
        if(orgOktaSupportSettings == OrgOktaSupportSetting.DISABLED) {
            assertThat(supportSettings.getExpiration(), nullValue())

            supportSettings.grantOktaSupport()
            assertThat(client.getOrgOktaSupportSettings().getSupport(), is(OrgOktaSupportSetting.ENABLED))
            assertThat(client.getOrgOktaSupportSettings().getExpiration(), notNullValue())
            Date expiration = client.getOrgOktaSupportSettings().getExpiration()

            supportSettings.extendOktaSupport()
            assertThat(client.getOrgOktaSupportSettings().getSupport(), is(OrgOktaSupportSetting.ENABLED))
            assertThat(client.getOrgOktaSupportSettings().getExpiration(), notNullValue())
            Date extendedExpiration = client.getOrgOktaSupportSettings().getExpiration()
            def hoursExtended = (int)TimeUnit.HOURS
                .convert(extendedExpiration.getTime() - expiration.getTime(), TimeUnit.MILLISECONDS)
            assertThat(hoursExtended, is(greaterThanOrEqualTo(24)))

            //revert to previous state
            supportSettings.revokeOktaSupport()
            assertThat(client.getOrgOktaSupportSettings().getSupport(), is(OrgOktaSupportSetting.DISABLED))
        } else {
            assertThat(supportSettings.getExpiration(), notNullValue())
        }
    }
}
