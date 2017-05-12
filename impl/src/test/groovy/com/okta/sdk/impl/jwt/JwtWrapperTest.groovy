/*
 * Copyright 2014 Stormpath, Inc.
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
package com.okta.sdk.impl.jwt

import com.okta.sdk.error.jwt.InvalidJwtException
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

/**
 * @since 1.0.0
 */
class JwtWrapperTest {

    @Test
    void testNullJwt() {
        try {
            new JwtWrapper(null)
            fail "shouldn't be here"
        } catch (InvalidJwtException e) {
            assertEquals e.getMessage(), InvalidJwtException.JWT_REQUIRED_ERROR
        }
    }

    @Test
    void testgetJsonHeaderAsMapException() {
        def jwtWrapper = new JwtWrapper("invalid.jwt.here")
        setBase64JwtSignature(jwtWrapper, "base64JwtHeader",null)

        try {
            jwtWrapper.getJsonHeaderAsMap()
            fail "shouldn't be here"
        } catch (InvalidJwtException e) {
            assertEquals e.getMessage(), InvalidJwtException.INVALID_JWT_HEADER_ENCODING_ERROR
        }
    }

    @Test
    void testGetJsonPayloadAsMapException() {

        def jwtWrapper = new JwtWrapper("invalid.jwt.here")
        setBase64JwtSignature(jwtWrapper, "base64JsonPayload", null)

        try {
            jwtWrapper.getJsonPayloadAsMap()
            fail "shouldn't be here"
        } catch (InvalidJwtException e) {
            assertEquals e.getMessage(), InvalidJwtException.INVALID_JWT_BODY_ENCODING_ERROR
        }
    }

    private setBase64JwtSignature(JwtWrapper wrapper, String fieldName, String value) {
        def field = JwtWrapper.class.getDeclaredField(fieldName)
        field.setAccessible(true)
        field.set(wrapper, value)
    }
}
