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

import com.okta.sdk.resource.brand.Brand
import com.okta.sdk.resource.brand.BrandList
import com.okta.sdk.resource.brand.EmailTemplateTouchPointVariant
import com.okta.sdk.resource.brand.EndUserDashboardTouchPointVariant
import com.okta.sdk.resource.brand.ErrorPageTouchPointVariant
import com.okta.sdk.resource.brand.SignInPageTouchPointVariant
import com.okta.sdk.resource.brand.Theme
import com.okta.sdk.resource.brand.ThemeResponse
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test
import org.testng.util.Strings

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/brands}.
 * @since 7.x.x
 */
class BrandIT extends ITSupport {

    @Test (groups = "bacon")
    @Scenario("basic-brand")
    void basicBrandTest() {

        Brand brand = client.getBrand(getBrandId())
        //remember origin values
        String customPrivacyPolicyUrl = brand.getCustomPrivacyPolicyUrl()
        Boolean removePoweredByOkta = brand.getRemovePoweredByOkta()

        brand.setAgreeToCustomPrivacyPolicy(true)
        brand.setCustomPrivacyPolicyUrl("https://custom-privacy-policy@example.com")
        brand.setRemovePoweredByOkta(!removePoweredByOkta)
        brand.update()
        assertThat(brand.getCustomPrivacyPolicyUrl(), equalTo("https://custom-privacy-policy@example.com"))
        assertThat(brand.getRemovePoweredByOkta(), equalTo(!removePoweredByOkta))

        //restore previous values
        if(!Strings.isNullOrEmpty(customPrivacyPolicyUrl)) {
            brand.setAgreeToCustomPrivacyPolicy(true)
        }
        brand.setCustomPrivacyPolicyUrl(customPrivacyPolicyUrl)
        brand.setRemovePoweredByOkta(removePoweredByOkta)
        brand.update()

        assertThat(brand.getCustomPrivacyPolicyUrl(), equalTo(customPrivacyPolicyUrl))
        assertThat(brand.getRemovePoweredByOkta(), equalTo(removePoweredByOkta))
    }

    @Test (groups = "bacon")
    @Scenario("basic-brand-theme")
    void basicBrandThemeTest() {

        String brandId = getBrandId()
        String themeId = getThemeId(brandId)

        ThemeResponse themeResponse = client.getBrandTheme(brandId, themeId)
        String primaryColorHex = themeResponse.getPrimaryColorHex()
        String secondaryColorHex = themeResponse.getSecondaryColorHex()
        SignInPageTouchPointVariant signInPageTPV = themeResponse.getSignInPageTouchPointVariant()
        EndUserDashboardTouchPointVariant endUserDashboardTPV = themeResponse.getEndUserDashboardTouchPointVariant()
        ErrorPageTouchPointVariant errorPageTPV = themeResponse.getErrorPageTouchPointVariant()
        EmailTemplateTouchPointVariant emailTemplateTPV = themeResponse.getEmailTemplateTouchPointVariant()

        Theme themeToUpdate = client.instantiate(Theme)
            .setPrimaryColorHex("#1662dd")
            .setSecondaryColorHex("#ebebed")
            .setSignInPageTouchPointVariant(SignInPageTouchPointVariant.BACKGROUND_IMAGE)
            .setEndUserDashboardTouchPointVariant(EndUserDashboardTouchPointVariant.FULL_THEME)
            .setErrorPageTouchPointVariant(ErrorPageTouchPointVariant.BACKGROUND_IMAGE)
            .setEmailTemplateTouchPointVariant(EmailTemplateTouchPointVariant.FULL_THEME)
        ThemeResponse updatedThemeResponse = themeToUpdate.update(brandId, themeId, themeToUpdate)

        assertThat(updatedThemeResponse.getPrimaryColorHex(), equalTo("#1662dd"))
        assertThat(updatedThemeResponse.getSecondaryColorHex(), equalTo("#ebebed"))
        assertThat(updatedThemeResponse.getSignInPageTouchPointVariant(), equalTo(SignInPageTouchPointVariant.BACKGROUND_IMAGE))
        assertThat(updatedThemeResponse.getEndUserDashboardTouchPointVariant(), equalTo(EndUserDashboardTouchPointVariant.FULL_THEME))
        assertThat(updatedThemeResponse.getErrorPageTouchPointVariant(), equalTo(ErrorPageTouchPointVariant.BACKGROUND_IMAGE))
        assertThat(updatedThemeResponse.getEmailTemplateTouchPointVariant(), equalTo(EmailTemplateTouchPointVariant.FULL_THEME))

        //restore previous state
        Theme themeToRestore = client.instantiate(Theme)
            .setPrimaryColorHex(primaryColorHex)
            .setSecondaryColorHex(secondaryColorHex)
            .setSignInPageTouchPointVariant(signInPageTPV)
            .setEndUserDashboardTouchPointVariant(endUserDashboardTPV)
            .setErrorPageTouchPointVariant(errorPageTPV)
            .setEmailTemplateTouchPointVariant(emailTemplateTPV)
        themeToRestore.update(brandId, themeId, themeToRestore)
    }

    /**
     * https://developer.okta.com/docs/reference/api/brands/#use-examples
     * Currently, only one Brand per org is supported.
     */
    String getBrandId() {

        BrandList brandList = client.listBrands()
        assertThat(brandList, notNullValue())

        assertThat(brandList.asList().size(), equalTo(1))
        Brand brandFromList = brandList[0]
        return brandFromList.getId()
    }

    /**
     * https://developer.okta.com/docs/reference/api/brands/#use-examples-4
     * Currently, only one Theme per Brand is supported.
     */
    String getThemeId(String brandId) {

        def themeList = client.listBrandThemes(brandId)
        assertThat(themeList, notNullValue())

        assertThat(themeList.asList().size(), equalTo(1))
        ThemeResponse themeFromList = themeList[0]
        return themeFromList.getId()
    }
}
