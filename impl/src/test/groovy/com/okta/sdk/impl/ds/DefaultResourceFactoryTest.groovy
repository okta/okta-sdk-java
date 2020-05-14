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
package com.okta.sdk.impl.ds


import com.okta.sdk.resource.user.factor.FactorType
import com.okta.sdk.resource.user.factor.TotpUserFactor
import com.okta.sdk.resource.user.factor.UserFactor
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.instanceOf

/**
 * Tests for {@link DefaultResourceFactory}.
 */
class DefaultResourceFactoryTest {

    /**
     * @since 0.8.1
     */
    @Test
    void discriminatorTest() {
        DefaultResourceFactory resourceFactory = new DefaultResourceFactory(null)

        def map = [
                factorType: "token:software:totp"
        ]
        UserFactor factor = resourceFactory.instantiate(UserFactor, map)
        assertThat factor, instanceOf(TotpUserFactor)
        assertThat factor.getFactorType(), equalTo(FactorType.TOKEN_SOFTWARE_TOTP)
    }
}