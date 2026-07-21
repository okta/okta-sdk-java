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
 * OKTA-1226428: the Okta API returns "validationStatus": "FAILED_TO_VERIFY" in domain responses, but the
 * SDK enum didn't declare that constant, so it fell through to UNKNOWN_DEFAULT_OPEN_API and the original
 * value was lost.
 */
public class DomainValidationStatusTest {

    @Test
    public void fromValue_failedToVerify_resolvesToRealConstant() {
        DomainValidationStatus status = DomainValidationStatus.fromValue("FAILED_TO_VERIFY");

        assertEquals(status, DomainValidationStatus.FAILED_TO_VERIFY);
        assertNotEquals(status, DomainValidationStatus.UNKNOWN_DEFAULT_OPEN_API);
        assertEquals(status.getValue(), "FAILED_TO_VERIFY");
    }
}
