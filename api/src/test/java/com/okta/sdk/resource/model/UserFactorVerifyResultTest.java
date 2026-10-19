/*
 * Copyright 2026-Present Okta, Inc.
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
package com.okta.sdk.resource.model;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * GH-1716: the Okta Factors API returns "factorResult": "WAITING" while polling async factor verifications
 * (e.g. Okta Verify push), but the 25.x enum dropped that constant, so it fell through to
 * UNKNOWN_DEFAULT_OPEN_API.
 */
public class UserFactorVerifyResultTest {

    @Test
    public void fromValue_waiting_resolvesToRealConstant() {
        UserFactorVerifyResult result = UserFactorVerifyResult.fromValue("WAITING");

        assertEquals(result, UserFactorVerifyResult.WAITING);
        assertNotEquals(result, UserFactorVerifyResult.UNKNOWN_DEFAULT_OPEN_API);
        assertEquals(result.getValue(), "WAITING");
    }
}
