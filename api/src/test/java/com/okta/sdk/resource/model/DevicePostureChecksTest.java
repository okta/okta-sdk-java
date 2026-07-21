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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * OKTA-1221543: DevicePostureChecks.include had no `items` type in the spec, so the generator defaulted
 * to List&lt;String&gt; while the real API shape is a list of {@code {variableName, value}} pairs. Verifies
 * it now deserializes into the properly typed DevicePostureCheckMapping.
 */
public class DevicePostureChecksTest {

    @Test
    public void deserialize_includeArray_populatesTypedMappings() throws Exception {
        String json = "{\"include\":["
            + "{\"variableName\":\"macOSFirewall\",\"value\":\"1\"},"
            + "{\"variableName\":\"windowsFirewall\",\"value\":\"1\"}"
            + "]}";

        DevicePostureChecks checks = new ObjectMapper().readValue(json, DevicePostureChecks.class);

        assertEquals(checks.getInclude().size(), 2);
        assertEquals(checks.getInclude().get(0).getVariableName(), "macOSFirewall");
        assertEquals(checks.getInclude().get(0).getValue(), "1");
        assertEquals(checks.getInclude().get(1).getVariableName(), "windowsFirewall");
    }
}
