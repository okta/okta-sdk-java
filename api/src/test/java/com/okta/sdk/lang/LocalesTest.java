/*
 * Copyright 2005-2018 the original author or authors.
 * Modifications Copyright 2018 Okta, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.lang;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.util.Arrays;
import java.util.Locale;

/**
 * Unit tests for {@link Locales}.
 */
public class LocalesTest {

    //-----------------------------------------------------------------------
    /**
     * Pass in a valid language, test toLocale.
     *
     * @param language  the language string
     */
    private static void assertValidToLocale(final String language) {
        final Locale locale = Locales.toLocale(language);
        assertNotNull("valid locale", locale);
        assertEquals(language, locale.getLanguage());
        //country and variant are empty
        assertTrue(locale.getCountry() == null || locale.getCountry().isEmpty());
        assertTrue(locale.getVariant() == null || locale.getVariant().isEmpty());
    }

    /**
     * Pass in a valid language, test toLocale.
     *
     * @param localeString to pass to toLocale()
     * @param language of the resulting Locale
     * @param country of the resulting Locale
     */
    private static void assertValidToLocale(final String localeString, final String language, final String country) {
        final Locale locale = Locales.toLocale(localeString);
        assertNotNull("valid locale", locale);
        assertEquals(language, locale.getLanguage());
        assertEquals(country, locale.getCountry());
        //variant is empty
        assertTrue(locale.getVariant() == null || locale.getVariant().isEmpty());
    }

    /**
     * Pass in a valid language, test toLocale.
     *
     * @param localeString to pass to toLocale()
     * @param language of the resulting Locale
     * @param country of the resulting Locale
     * @param variant of the resulting Locale
     */
    private static void assertValidToLocale(
            final String localeString, final String language,
            final String country, final String variant) {
        final Locale locale = Locales.toLocale(localeString);
        assertNotNull("valid locale", locale);
        assertEquals(language, locale.getLanguage());
        assertEquals(country, locale.getCountry());
        assertEquals(variant, locale.getVariant());
    }

    /**
     * Test toLocale() method.
     */
    @Test
    public void testToLocale_1Part() {
        assertNull(Locales.toLocale(null));

        assertValidToLocale("us");
        assertValidToLocale("fr");
        assertValidToLocale("de");
        assertValidToLocale("zh");
        // Valid format but lang doesn't exist, should make instance anyway
        assertValidToLocale("qq");
        // LANG-941: JDK 8 introduced the empty locale as one of the default locales
        assertValidToLocale("");

        try {
            Locales.toLocale("Us");
            fail("Should fail if not lowercase");
        } catch (final IllegalArgumentException iae) {}
        try {
            Locales.toLocale("US");
            fail("Should fail if not lowercase");
        } catch (final IllegalArgumentException iae) {}
        try {
            Locales.toLocale("uS");
            fail("Should fail if not lowercase");
        } catch (final IllegalArgumentException iae) {}
        try {
            Locales.toLocale("u#");
            fail("Should fail if not lowercase");
        } catch (final IllegalArgumentException iae) {}

        try {
            Locales.toLocale("u");
            fail("Must be 2 chars if less than 5");
        } catch (final IllegalArgumentException iae) {}

        try {
            Locales.toLocale("uu_U");
            fail("Must be 2 chars if less than 5");
        } catch (final IllegalArgumentException iae) {}
    }

    /**
     * Test toLocale() method.
     */
    @Test
    public void testToLocale_2Part() {
        assertValidToLocale("us_EN", "us", "EN");
        //valid though doesn't exist
        assertValidToLocale("us_ZH", "us", "ZH");

        try {
            Locales.toLocale("us-EN");
            fail("Should fail as not underscore");
        } catch (final IllegalArgumentException iae) {}
        try {
            Locales.toLocale("us_En");
            fail("Should fail second part not uppercase");
        } catch (final IllegalArgumentException iae) {}
        try {
            Locales.toLocale("us_en");
            fail("Should fail second part not uppercase");
        } catch (final IllegalArgumentException iae) {}
        try {
            Locales.toLocale("us_eN");
            fail("Should fail second part not uppercase");
        } catch (final IllegalArgumentException iae) {}
        try {
            Locales.toLocale("uS_EN");
            fail("Should fail first part not lowercase");
        } catch (final IllegalArgumentException iae) {}
        try {
            Locales.toLocale("us_E3");
            fail("Should fail second part not uppercase");
        } catch (final IllegalArgumentException iae) {}
    }

    /**
     * Test toLocale() method.
     */
    @Test
    public void testToLocale_3Part() {
        assertValidToLocale("us_EN_A", "us", "EN", "A");
        assertValidToLocale("us_EN_a", "us", "EN", "a");
        assertValidToLocale("us_EN_SFsafdFDsdfF", "us", "EN", "SFsafdFDsdfF");

        try {
            Locales.toLocale("us_EN-a");
            fail("Should fail as not underscore");
        } catch (final IllegalArgumentException iae) {}
        try {
            Locales.toLocale("uu_UU_");
            fail("Must be 3, 5 or 7+ in length");
        } catch (final IllegalArgumentException iae) {}
    }

    /**
     * Test for 3-chars locale, further details at LANG-915
     *
     */
    @Test
    public void testThreeCharsLocale() {
        for (final String str : Arrays.asList("udm", "tet")) {
            final Locale locale = Locales.toLocale(str);
            assertNotNull(locale);
            assertEquals(str, locale.getLanguage());
            assertTrue(Strings.isEmpty(locale.getCountry()));
            assertEquals(new Locale(str), locale);
        }
    }

    /**
     * Tests #LANG-328 - only language+variant
     */
    @Test
    public void testLang328() {
        assertValidToLocale("fr__P", "fr", "", "P");
        assertValidToLocale("fr__POSIX", "fr", "", "POSIX");
    }

    @Test
    public void testLanguageAndUNM49Numeric3AreaCodeLang1312() {
        assertValidToLocale("en_001", "en", "001");
        assertValidToLocale("en_150", "en", "150");
        assertValidToLocale("ar_001", "ar", "001");

        // LANG-1312
        assertValidToLocale("en_001_GB", "en", "001", "GB");
        assertValidToLocale("en_150_US", "en", "150", "US");
    }

    /**
     * Tests #LANG-865, strings starting with an underscore.
     */
    @Test
    public void testLang865() {
        assertValidToLocale("_GB", "", "GB", "");
        assertValidToLocale("_GB_P", "", "GB", "P");
        assertValidToLocale("_GB_POSIX", "", "GB", "POSIX");
        try {
            Locales.toLocale("_G");
            fail("Must be at least 3 chars if starts with underscore");
        } catch (final IllegalArgumentException iae) {
        }
        try {
            Locales.toLocale("_Gb");
            fail("Must be uppercase if starts with underscore");
        } catch (final IllegalArgumentException iae) {
        }
        try {
            Locales.toLocale("_gB");
            fail("Must be uppercase if starts with underscore");
        } catch (final IllegalArgumentException iae) {
        }
        try {
            Locales.toLocale("_1B");
            fail("Must be letter if starts with underscore");
        } catch (final IllegalArgumentException iae) {
        }
        try {
            Locales.toLocale("_G1");
            fail("Must be letter if starts with underscore");
        } catch (final IllegalArgumentException iae) {
        }
        try {
            Locales.toLocale("_GB_");
            fail("Must be at least 5 chars if starts with underscore");
        } catch (final IllegalArgumentException iae) {
        }
        try {
            Locales.toLocale("_GBAP");
            fail("Must have underscore after the country if starts with underscore and is at least 5 chars");
        } catch (final IllegalArgumentException iae) {
        }
    }

    @Test
    public void testParseAllLocales() {
        final Locale[] locales = Locale.getAvailableLocales();
        int failures = 0;
        for (final Locale l : locales) {
            // Check if it's possible to recreate the Locale using just the standard constructor
            final Locale locale = new Locale(l.getLanguage(), l.getCountry(), l.getVariant());
            if (l.equals(locale)) { // it is possible for LocaleUtils.toLocale to handle these Locales
                String str = l.toString();
                // Look for the script/extension suffix
                int suff = str.indexOf("_#");
                if (suff == - 1) {
                    suff = str.indexOf("#");
                }
                if (suff >= 0) { // we have a suffix
                    try {
                        Locales.toLocale(str); // should cause IAE
                        System.out.println("Should not have parsed: " + str);
                        failures++;
                        continue; // try next Locale
                    } catch (final IllegalArgumentException iae) {
                        // expected; try without suffix
                        str = str.substring(0, suff);
                    }
                }
                final Locale loc = Locales.toLocale(str);
                if (!l.equals(loc)) {
                    System.out.println("Failed to parse: " + str);
                    failures++;
                }
            }
        }
        if (failures > 0) {
            fail("Failed "+failures+" test(s)");
        }
    }

}