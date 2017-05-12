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
package com.okta.sdk.impl.error

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.0
 */
class DefaultErrorTest {

    @Test
    void testGetPropertyDescriptors() {
        def defaultError = new DefaultError(new HashMap<String, Object>())
        finishTest(defaultError);
    }

    /*
     * see https://github.com/okta/okta-sdk-java/pull/770
     */
    @Test
    void testtestGetPropertyDescriptorsDefaultConstructor() {
        def defaultError = new DefaultError()
        finishTest(defaultError);
    }

    private void finishTest(DefaultError defaultError) {
        assertEquals defaultError.propertyDescriptors.keySet().size(), 5
        [
            DefaultError.STATUS, DefaultError.CODE, DefaultError.DEV_MESSAGE,
            DefaultError.MESSAGE, DefaultError.MORE_INFO
        ].each {
            assertTrue defaultError.propertyDescriptors.keySet().contains(it.name)
        }
    }
}
