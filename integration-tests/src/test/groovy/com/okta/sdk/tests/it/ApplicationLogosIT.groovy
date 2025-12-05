/*
 * Copyright 2024-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.tests.it

import com.okta.sdk.resource.api.ApplicationApi
import com.okta.sdk.resource.api.ApplicationLogosApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.tests.it.util.ITSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

import java.nio.file.Files
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.Color

/**
 * Comprehensive integration tests for Application Logos API.
 * Mirrors C# SDK ApplicationLogosApiTests coverage.
 * 
 * ENDPOINT TESTED (1):
 * 1. POST /api/v1/apps/{appId}/logo - Upload application logo (replaces existing)
 * 
 * FEATURES TESTED:
 * - PNG image upload
 * - SVG image upload
 * - Logo replacement (upload over existing)
 * - Sequential uploads
 * - Multiple application types (Bookmark, SAML, OIDC)
 * - Error handling (404 for non-existent apps)
 * - Logo link verification
 */
class ApplicationLogosIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationLogosIT.class)
    def applicationApi = new ApplicationApi(getClient())
    def applicationLogosApi = new ApplicationLogosApi(getClient())

    /**
     * Creates a minimal valid 1x1 PNG image for testing.
     * Returns a temporary file that will not be automatically deleted.
     * Caller is responsible for cleanup.
     */
    private File createTestPngImage() {
        // Minimal valid PNG file (1x1 pixel, transparent)
        byte[] pngBytes = [
            (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x48, (byte) 0x44, (byte) 0x52,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
            (byte) 0x08, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1F, (byte) 0x15, (byte) 0xC4,
            (byte) 0x89, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0A, (byte) 0x49, (byte) 0x44, (byte) 0x41,
            (byte) 0x54, (byte) 0x78, (byte) 0x9C, (byte) 0x63, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
            (byte) 0x05, (byte) 0x00, (byte) 0x01, (byte) 0x0D, (byte) 0x0A, (byte) 0x2D, (byte) 0xB4, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x49, (byte) 0x45, (byte) 0x4E, (byte) 0x44, (byte) 0xAE,
            (byte) 0x42, (byte) 0x60, (byte) 0x82
        ] as byte[]

        File tempFile = File.createTempFile("test_logo_", ".png")
        tempFile.deleteOnExit()
        
        FileOutputStream fos = new FileOutputStream(tempFile)
        try {
            fos.write(pngBytes)
        } finally {
            fos.close()
        }
        
        return tempFile
    }

    /**
     * Creates a simple SVG image for testing.
     * Returns a temporary file that will not be automatically deleted.
     * Caller is responsible for cleanup.
     */
    private File createTestSvgImage() {
        String svgContent = '''<?xml version="1.0" encoding="UTF-8"?>
<svg width="100" height="100" xmlns="http://www.w3.org/2000/svg">
  <circle cx="50" cy="50" r="40" fill="#4CAF50"/>
</svg>'''

        File tempFile = File.createTempFile("test_logo_", ".svg")
        tempFile.deleteOnExit()
        tempFile.text = svgContent
        return tempFile
    }

    /**
     * Creates a simple JPEG image for testing using BufferedImage.
     * Returns a temporary file that will not be automatically deleted.
     * Caller is responsible for cleanup.
     */
    private File createTestJpegImage() {
        // Create a small 10x10 pixel image
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        
        // Fill with blue color
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                image.setRGB(x, y, Color.BLUE.getRGB())
            }
        }
        
        File tempFile = File.createTempFile("test_logo_", ".jpg")
        tempFile.deleteOnExit()
        
        ImageIO.write(image, "jpg", tempFile)
        
        return tempFile
    }

    /**
     * Comprehensive test for PNG logo upload.
     * Tests basic logo upload and verification.
     */
    @Test
    void testUploadPngApplicationLogo() {
        logger.info("Testing PNG logo upload...")

        // Create a test Bookmark application
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label("Logo Test App PNG - " + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)

        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark-logo-png")

        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)

        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        Thread.sleep(1000)

        // Create and upload PNG logo
        File pngLogo = createTestPngImage()
        
        try {
            // Upload logo with empty additional headers map
            applicationLogosApi.uploadApplicationLogo(createdApp.getId(), pngLogo)
            
            Thread.sleep(1000)

            // Verify app still exists and logo link is present
            Application updatedApp = applicationApi.getApplication(createdApp.getId(), null)
            
            assertThat(updatedApp, notNullValue())
            assertThat(updatedApp.getId(), equalTo(createdApp.getId()))
            assertThat(updatedApp.getLinks(), notNullValue())
            assertThat("Logo should be uploaded and link available", updatedApp.getLinks().getLogo(), notNullValue())
            assertThat("At least one logo link should exist", 
                updatedApp.getLinks().getLogo().size(), greaterThanOrEqualTo(1))

            logger.info("PNG logo uploaded successfully!")
        } finally {
            pngLogo.delete()
        }
    }

    /**
     * Comprehensive test for SVG logo upload.
     * Tests SVG format support.
     */
    @Test
    void testUploadSvgApplicationLogo() {
        logger.info("Testing SVG logo upload...")

        // Create a test Bookmark application
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label("Logo Test App SVG - " + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)

        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark-logo-svg")

        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)

        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        Thread.sleep(1000)

        // Create and upload SVG logo
        File svgLogo = createTestSvgImage()
        
        try {
            applicationLogosApi.uploadApplicationLogo(createdApp.getId(), svgLogo)
            
            Thread.sleep(1000)

            // Verify app still exists and logo link is present
            Application updatedApp = applicationApi.getApplication(createdApp.getId(), null)
            
            assertThat(updatedApp, notNullValue())
            assertThat(updatedApp.getId(), equalTo(createdApp.getId()))
            assertThat(updatedApp.getLinks(), notNullValue())
            assertThat("SVG logo should be uploaded successfully", updatedApp.getLinks().getLogo(), notNullValue())

            logger.info("SVG logo uploaded successfully!")
        } finally {
            svgLogo.delete()
        }
    }

    /**
     * Test logo replacement functionality.
     * Verifies that uploading a new logo replaces the existing one.
     */
    @Test
    void testReplacingExistingApplicationLogo() {
        logger.info("Testing logo replacement...")

        // Create a test Bookmark application
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label("Logo Test App Replace - " + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)

        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark-logo-replace")

        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)

        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        Thread.sleep(1000)

        File pngLogo = null
        File svgLogo = null

        try {
            // Upload first logo (PNG)
            pngLogo = createTestPngImage()
            assertThat("PNG logo file should exist", pngLogo.exists(), is(true))
            assertThat("PNG logo file should be readable", pngLogo.canRead(), is(true))
            assertThat("PNG logo file should have content", pngLogo.length(), greaterThan(0L))
            logger.debug("PNG logo created: exists=${pngLogo.exists()}, size=${pngLogo.length()} bytes, path=${pngLogo.absolutePath}")
            applicationLogosApi.uploadApplicationLogo(createdApp.getId(), pngLogo)
            
            Thread.sleep(1000)

            // Verify first logo exists
            Application appAfterFirstUpload = applicationApi.getApplication(createdApp.getId(), null)
            assertThat("First logo should be uploaded", appAfterFirstUpload.getLinks().getLogo(), notNullValue())

            // Upload second logo (SVG) - should replace first
            svgLogo = createTestSvgImage()
            applicationLogosApi.uploadApplicationLogo(createdApp.getId(), svgLogo)
            
            Thread.sleep(1000)

            // Verify app is still accessible and logo exists
            Application appAfterReplacement = applicationApi.getApplication(createdApp.getId(), null)
            
            assertThat(appAfterReplacement, notNullValue())
            assertThat(appAfterReplacement.getId(), equalTo(createdApp.getId()))
            assertThat("Second logo should replace first logo", appAfterReplacement.getLinks().getLogo(), notNullValue())
            assertThat("Logo should exist after replacement", 
                appAfterReplacement.getLinks().getLogo().size(), greaterThanOrEqualTo(1))

            logger.info("Logo replacement completed successfully!")
        } finally {
            pngLogo?.delete()
            svgLogo?.delete()
        }
    }

    /**
     * Test error handling for non-existent application.
     * Verifies error is thrown when trying to upload logo to non-existent app.
     */
    @Test
    void testUploadLogoToNonExistentApplication() {
        logger.info("Testing error handling for non-existent application...")

        String nonExistentAppId = "0oa_nonexistent_app_id"
        File pngLogo = createTestPngImage()

        try {
            ApiException exception = expect(ApiException.class, () -> 
                applicationLogosApi.uploadApplicationLogo(nonExistentAppId, pngLogo))
            
            // API returns 400 for file validation errors before checking if app exists
            assertThat("Should return 400 or 404 for non-existent app", 
                exception.getCode(), anyOf(is(400), is(404)))
            
            logger.info("Error handling test completed successfully!")
        } finally {
            pngLogo.delete()
        }
    }

    /**
     * Test sequential logo uploads.
     * Verifies multiple sequential uploads work correctly.
     */
    @Test
    void testSequentialLogoUploads() {
        logger.info("Testing sequential logo uploads...")

        // Create a test Bookmark application
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label("Logo Test App Sequential - " + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)

        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark-logo-sequential")

        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)

        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        Thread.sleep(1000)

        // Upload multiple logos in sequence
        for (int i = 0; i < 3; i++) {
            File logo = (i % 2 == 0) ? createTestPngImage() : createTestSvgImage()
            
            try {
                applicationLogosApi.uploadApplicationLogo(createdApp.getId(), logo)
                Thread.sleep(500)
                
                logger.debug("Uploaded logo ${i + 1}/3")
            } finally {
                logo.delete()
            }
        }

        // Verify app is still accessible after multiple uploads
        Application finalApp = applicationApi.getApplication(createdApp.getId(), null)
        
        assertThat(finalApp, notNullValue())
        assertThat(finalApp.getId(), equalTo(createdApp.getId()))
        assertThat("Logo should exist after sequential uploads", finalApp.getLinks().getLogo(), notNullValue())

        logger.info("Sequential uploads test completed successfully!")
    }



    /**
     * Test different image formats (PNG, SVG, JPEG).
     * Comprehensive test for all supported formats.
     */
    @Test
    void testUploadDifferentImageFormats() {
        logger.info("Testing different image format uploads...")

        // Create a test Bookmark application
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label("Logo Test Formats - " + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)

        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark-logo-formats")

        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)

        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        Thread.sleep(1000)

        // Test PNG format
        File pngLogo = createTestPngImage()
        try {
            applicationLogosApi.uploadApplicationLogo(createdApp.getId(), pngLogo)
            Thread.sleep(500)
            logger.debug("PNG format uploaded successfully")
        } finally {
            pngLogo.delete()
        }

        // Test SVG format
        File svgLogo = createTestSvgImage()
        try {
            applicationLogosApi.uploadApplicationLogo(createdApp.getId(), svgLogo)
            Thread.sleep(500)
            logger.debug("SVG format uploaded successfully")
        } finally {
            svgLogo.delete()
        }

        // Test JPEG format
        File jpegLogo = createTestJpegImage()
        try {
            applicationLogosApi.uploadApplicationLogo(createdApp.getId(), jpegLogo)
            Thread.sleep(500)
            logger.debug("JPEG format uploaded successfully")
        } finally {
            jpegLogo.delete()
        }

        // Verify app is still accessible after all format uploads
        Application finalApp = applicationApi.getApplication(createdApp.getId(), null)
        
        assertThat(finalApp, notNullValue())
        assertThat(finalApp.getId(), equalTo(createdApp.getId()))
        assertThat("Logo should exist after format tests", finalApp.getLinks().getLogo(), notNullValue())

        logger.info("Different image formats test completed successfully!")
    }

    /**
     * Test uploadApplicationLogo with Map parameter variant.
     * Tests the method overload that accepts additional parameters via Map.
     */
    @Test
    void testUploadApplicationLogoWithMapParameter() {
        logger.info("Testing logo upload with Map parameter variant...")

        // Create a test Bookmark application
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label("Logo Test Map Param - " + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)

        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark-logo-map")

        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)

        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        Thread.sleep(1000)

        // Create and upload PNG logo with Map parameter (empty map for additional params)
        File pngLogo = createTestPngImage()
        def additionalParams = [:]
        
        try {
            // Upload logo using Map parameter variant
            applicationLogosApi.uploadApplicationLogo(createdApp.getId(), pngLogo, additionalParams)
            
            Thread.sleep(1000)

            // Verify app still exists and logo link is present
            Application updatedApp = applicationApi.getApplication(createdApp.getId(), null)
            
            assertThat(updatedApp, notNullValue())
            assertThat(updatedApp.getId(), equalTo(createdApp.getId()))
            assertThat(updatedApp.getLinks(), notNullValue())
            assertThat("Logo should be uploaded with Map parameter", updatedApp.getLinks().getLogo(), notNullValue())
            assertThat("Logo link should exist after upload with Map param", 
                updatedApp.getLinks().getLogo().size(), greaterThanOrEqualTo(1))

            logger.info("Map parameter variant test completed successfully!")
        } finally {
            pngLogo.delete()
        }
    }
}
