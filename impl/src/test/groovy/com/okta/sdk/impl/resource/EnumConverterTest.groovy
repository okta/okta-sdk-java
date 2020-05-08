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
package com.okta.sdk.impl.resource


import org.testng.annotations.Test

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.MatcherAssert.assertThat

/**
 * Tests for {@link EnumConverter}.
 * @since 0.8.1
 */
class EnumConverterTest {

    @Test
    void validLookup() {
        EnumConverter converter = new EnumConverter()
        assertThat converter.fromValue(FactorType, "token:software:totp"), equalTo(FactorType.TOKEN_SOFTWARE_TOTP)
        assertThat converter.fromValue(FactorType, "TOKEN_SOFTWARE_TOTP"), equalTo(FactorType.TOKEN_SOFTWARE_TOTP)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void invalidLookup() {
        new EnumConverter().fromValue(FactorType, "invalid")
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void nullLookup() {
        new EnumConverter().fromValue(FactorType, null)
    }
}