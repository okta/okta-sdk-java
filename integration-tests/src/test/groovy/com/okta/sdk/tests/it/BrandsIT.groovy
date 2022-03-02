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
import com.okta.sdk.resource.brand.ImageUploadResponse
import com.okta.sdk.resource.brand.SignInPageTouchPointVariant
import com.okta.sdk.resource.brand.Theme
import com.okta.sdk.resource.brand.ThemeResponse
import com.okta.sdk.resource.brands.EmailTemplate
import com.okta.sdk.resource.brands.EmailTemplateContent
import com.okta.sdk.resource.brands.EmailTemplateCustomization
import com.okta.sdk.resource.brands.EmailTemplateCustomizationList
import com.okta.sdk.resource.brands.EmailTemplateCustomizationRequest
import com.okta.sdk.resource.brands.EmailTemplateList
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
class BrandsIT extends ITSupport {

    private final String pathToImage = "src/test/resources/okta_logo_white.png"
    private final String pathToFavicon = "src/test/resources/okta_logo_favicon.png"

    @Test (groups = "bacon")
    @Scenario("basic-brand")
    void basicBrandTest() {

        Brand brand = client.getBrand(getBrandId())
        //remember original values
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

        assertThat(updatedThemeResponse, notNullValue())
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

    @Test (groups = "bacon")
    @Scenario("brand-theme-logo")
    void brandThemeLogoTest() {

        String brandId = getBrandId()
        String themeId = getThemeId(brandId)
        File file = new File(pathToImage)

        ImageUploadResponse resp = client.instantiate(Theme).uploadBrandThemeLogo(brandId, themeId, file)
        assertThat(resp, notNullValue())
        assertThat(resp.getUrl(), notNullValue())

        client.instantiate(Theme).deleteBrandThemeLogo(brandId, themeId)
    }

    @Test (groups = "bacon")
    @Scenario("brand-theme-background-image")
    void brandThemeBackgroundImageTest() {

        String brandId = getBrandId()
        String themeId = getThemeId(brandId)
        File file = new File(pathToImage)

        ImageUploadResponse resp = client.instantiate(Theme).updateBrandThemeBackgroundImage(brandId, themeId, file)
        assertThat(resp, notNullValue())
        assertThat(resp.getUrl(), notNullValue())

        client.instantiate(Theme).deleteBrandThemeBackgroundImage(brandId, themeId)
    }

    @Test (groups = "bacon")
    @Scenario("brand-theme-favicon")
    void brandThemeFaviconTest() {

        String brandId = getBrandId()
        String themeId = getThemeId(brandId)
        File file = new File(pathToFavicon)

        ImageUploadResponse resp = client.instantiate(Theme).updateBrandThemeFavicon(brandId, themeId, file)
        assertThat(resp, notNullValue())
        assertThat(resp.getUrl(), notNullValue())

        client.instantiate(Theme).deleteBrandThemeFavicon(brandId, themeId)
    }

    @Test (groups = "bacon")
    void listEmailTemplatesTest() {

        BrandList brandList = client.listBrands()
        assertThat(brandList, notNullValue())

        Brand brand = brandList.first()
        assertThat(brand, notNullValue())

        EmailTemplateList emailTemplateList = client.listEmailTemplates(brand.getId())
        assertThat(emailTemplateList, notNullValue())

        EmailTemplate emailTemplate = emailTemplateList.first()
        assertThat(emailTemplate.getName(), notNullValue())
    }

    @Test (groups = "bacon")
    void listEmailTemplateCustomizationsTest() {

        BrandList brandList = client.listBrands()
        assertThat(brandList, notNullValue())

        Brand brand = brandList.first()
        assertThat(brand, notNullValue())

        EmailTemplateList emailTemplateList = client.listEmailTemplates(brand.getId())
        assertThat(emailTemplateList, notNullValue())

        EmailTemplate emailTemplate = emailTemplateList.first()
        assertThat(emailTemplate, notNullValue())

        EmailTemplateCustomizationList emailTemplateCustomizationList =
            emailTemplate.listEmailTemplateCustomizations(brand.getId(), emailTemplate.getName())
        assertThat(emailTemplateCustomizationList, notNullValue())
    }

    @Test (groups = "bacon")
    void getEmailTemplateCustomizationTest() {

        BrandList brandList = client.listBrands()
        assertThat(brandList, notNullValue())

        Brand brand = brandList.first()
        assertThat(brand, notNullValue())

        EmailTemplateList emailTemplateList = client.listEmailTemplates(brand.getId())
        assertThat(emailTemplateList, notNullValue())

        EmailTemplate emailTemplate = emailTemplateList.first()
        assertThat(emailTemplate, notNullValue())

        EmailTemplateCustomizationList emailTemplateCustomizationList =
            emailTemplate.listEmailTemplateCustomizations(brand.getId(), emailTemplate.getName())
        assertThat(emailTemplateCustomizationList, notNullValue())

        EmailTemplateCustomization emailTemplateCustomization =
            emailTemplate.getEmailTemplateCustomization(brand.getId(), emailTemplate.getName(), emailTemplateCustomizationList.first().getId())
        assertThat(emailTemplateCustomization, notNullValue())
        assertThat(emailTemplateCustomization.getSubject(), notNullValue())
        assertThat(emailTemplateCustomization.getBody(), notNullValue())
    }

    @Test (groups = "bacon")
    void createUpdateAndDeleteEmailTemplateCustomizationTest() {

        String language = "uk"

        BrandList brandList = client.listBrands()
        assertThat(brandList, notNullValue())

        Brand brand = brandList.first()
        assertThat(brand, notNullValue())

        EmailTemplateList emailTemplateList = client.listEmailTemplates(brand.getId())
        assertThat(emailTemplateList, notNullValue())

        EmailTemplate emailTemplate = emailTemplateList.first()
        assertThat(emailTemplate, notNullValue())

        EmailTemplateCustomizationRequest emailTemplateCustomizationRequest = client.instantiate(EmailTemplateCustomizationRequest)
            .setBody("Test Customization - \${activationLink} \${activationToken}")
            .setSubject("Test Subject - Java SDK IT")
            .setLanguage(language)
            .setIsDefault(false)

        EmailTemplateCustomization emailTemplateCustomization =
            emailTemplate.createEmailTemplateCustomization(brand.getId(), emailTemplate.getName(), emailTemplateCustomizationRequest)

        assertThat(emailTemplateCustomization, notNullValue())
        assertThat(emailTemplateCustomization.getSubject(), is("Test Subject - Java SDK IT"))
        assertThat(emailTemplateCustomization.getBody(), is("Test Customization - \${activationLink} \${activationToken}"))
        assertThat(emailTemplateCustomization.getLanguage(), is(language))

        EmailTemplateCustomizationRequest updateEmailTemplateCustomizationRequest = client.instantiate(EmailTemplateCustomizationRequest)
            .setBody("Updated - Test Customization - \${activationLink} \${activationToken}")
            .setSubject("Updated - Test Subject - Java SDK IT")
            .setLanguage(language)
            .setIsDefault(false)

        EmailTemplateCustomization updatedEmailTemplateCustomization =
            emailTemplate.updateEmailTemplateCustomization(brand.getId(), emailTemplate.getName(), emailTemplateCustomization.getId(), updateEmailTemplateCustomizationRequest)

        assertThat(updatedEmailTemplateCustomization, notNullValue())
        assertThat(updatedEmailTemplateCustomization.getSubject(), is("Updated - Test Subject - Java SDK IT"))
        assertThat(updatedEmailTemplateCustomization.getBody(), is("Updated - Test Customization - \${activationLink} \${activationToken}"))
        assertThat(updatedEmailTemplateCustomization.getLanguage(), is(language))

        EmailTemplateCustomization retrievedEmailTemplateCustomization =
            emailTemplate.getEmailTemplateCustomization(brand.getId(), emailTemplate.getName(), updatedEmailTemplateCustomization.getId())

        // delete if it is not the default one (Orgs consider the first created email template customization as a default one even if setIsDefault is set to true in create request)
        // therefore, to prevent deletion errors we check for non-default before deletion. Note that only non-default template customizations can be deleted.
        if (!retrievedEmailTemplateCustomization.getIsDefault()) {
            emailTemplate.deleteEmailTemplateCustomization(brand.getId(), emailTemplate.getName(), updatedEmailTemplateCustomization.getId())
        }
    }

    @Test (groups = "bacon")
    void getEmailTemplateCustomizationPreviewTest() {

        BrandList brandList = client.listBrands()
        assertThat(brandList, notNullValue())

        Brand brand = brandList.first()
        assertThat(brand, notNullValue())

        EmailTemplateList emailTemplateList = client.listEmailTemplates(brand.getId())
        assertThat(emailTemplateList, notNullValue())

        EmailTemplate emailTemplate = emailTemplateList.first()
        assertThat(emailTemplate, notNullValue())

        EmailTemplateCustomizationList emailTemplateCustomizationList =
            emailTemplate.listEmailTemplateCustomizations(brand.getId(), emailTemplate.getName())
        assertThat(emailTemplateCustomizationList, notNullValue())

        EmailTemplateContent emailTemplateCustomizationPreview =
            emailTemplate.getEmailTemplateCustomizationPreview(brand.getId(), emailTemplate.getName(), emailTemplateCustomizationList.first().getId())
        assertThat(emailTemplateCustomizationPreview, notNullValue())
        assertThat(emailTemplateCustomizationPreview.getBody(), notNullValue())
        assertThat(emailTemplateCustomizationPreview.getSubject(), notNullValue())
    }

    @Test (groups = "bacon")
    void getEmailTemplateDefaultContentTest() {

        BrandList brandList = client.listBrands()
        assertThat(brandList, notNullValue())

        Brand brand = brandList.first()
        assertThat(brand, notNullValue())

        EmailTemplateList emailTemplateList = client.listEmailTemplates(brand.getId())
        assertThat(emailTemplateList, notNullValue())

        EmailTemplate emailTemplate = emailTemplateList.first()
        assertThat(emailTemplate, notNullValue())

        EmailTemplateCustomizationList emailTemplateCustomizationList =
            emailTemplate.listEmailTemplateCustomizations(brand.getId(), emailTemplate.getName())
        assertThat(emailTemplateCustomizationList, notNullValue())

        EmailTemplateContent emailTemplateDefaultContent =
            emailTemplate.getEmailTemplateDefaultContent(brand.getId(), emailTemplate.getName())
        assertThat(emailTemplateDefaultContent, notNullValue())
        assertThat(emailTemplateDefaultContent.getBody(), notNullValue())
        assertThat(emailTemplateDefaultContent.getSubject(), notNullValue())
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
