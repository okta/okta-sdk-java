/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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
package com.okta.sdk.impl.config

import com.okta.sdk.impl.io.Resource
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 0.5.0
 */
class ResourcePropertiesSourceTest {

    @Test
    void testGetProperties() {

        def testStr = "okta.web.verify.nextUri=/login?status=verified"
        def properties = new ResourcePropertiesSource(new TestStringResource(testStr)).properties

        assertEquals properties.get("okta.web.verify.nextUri"), "/login?status=verified"
    }

    @Test
    void testInvalidProperties() {

        try {
            new ResourcePropertiesSource(new BadResource()).properties
            fail("should not be here")
        } catch (IllegalArgumentException e) {
            assertTrue e.getMessage().contains("BadResource")
        }
    }
}

class BadResource implements Resource {

    @Override
    InputStream getInputStream() throws IOException {
        throw new IOException("BadResource")
    }
}