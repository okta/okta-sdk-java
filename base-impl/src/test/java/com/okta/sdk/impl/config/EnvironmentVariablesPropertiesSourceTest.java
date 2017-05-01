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
package com.okta.sdk.impl.config;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.mockStaticPartial;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.testng.Assert.assertEquals;

/**
 * @since 1.0.RC9
 *
 * NOTE: This *exact* same test was not working in groovy, which is why it's implemented in Java
 */
@PrepareForTest(EnvironmentVariablesPropertiesSource.class)
public class EnvironmentVariablesPropertiesSourceTest extends PowerMockTestCase {

    @Test
    public void testGetProperties() {

        Map<String, String> mockProps = new HashMap<String, String>();
        mockProps.put("my_special_key", "my_special_value");

        mockStaticPartial(System.class, "getenv");
        expect(System.getenv()).andReturn(mockProps);

        replayAll();

        Map<String, String> props = new EnvironmentVariablesPropertiesSource().getProperties();

        assertEquals(props.get("my_special_key"), "my_special_value");
    }

    @Test
    public void testGetPropertiesEmpty() {

        mockStaticPartial(System.class, "getenv");
        expect(System.getenv()).andReturn(null);

        replayAll();

        Map<String, String> props = new EnvironmentVariablesPropertiesSource().getProperties();

        assertEquals(props.size(), 0);
    }

}
