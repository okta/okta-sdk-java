/*
 * Copyright 2023-Present Okta, Inc.
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

import static org.testng.Assert.assertEquals

/**
 * @since 14.1.0
 */
class DefaultUserBuilderTest {

    var userBuilder = new DefaultUserBuilder()

    @Test
    void testSetBcryptPasswordHash() {
        userBuilder.setBcryptPasswordHash("\$2a\$10\$Ro0CUfOqk6cXEKf3dyaM7OhSCvnwM9s4wIX9JeLapehKK5YdLxKcm")

        assertEquals(userBuilder.passwordHashProperties.size(), 4)
        assertEquals(userBuilder.passwordHashProperties.salt, "Ro0CUfOqk6cXEKf3dyaM7O")
        assertEquals(userBuilder.passwordHashProperties.workFactor, 10)
        assertEquals(userBuilder.passwordHashProperties.value, "hSCvnwM9s4wIX9JeLapehKK5YdLxKcm")
        assertEquals(userBuilder.passwordHashProperties.algorithm, "BCRYPT")
    }
}
