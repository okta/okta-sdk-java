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
package com.okta.sdk.impl.ds

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 0.5.0
 */
class EnlistmentTest {

    @Test
    void testInstantiateNull() {

        def enlistment = new Enlistment(null)

        assertEquals enlistment.size(), 0
    }

    @Test
    void testHashCode() {

        def linkedHashMap = new LinkedHashMap<String, Object>()
        linkedHashMap.put("this", "that")

        def enlistment = new Enlistment(["this":"that"])

        assertEquals enlistment.hashCode(), linkedHashMap.hashCode()
    }

    @Test
    void testHashCodeEmpty() {

        def enlistment = new Enlistment(null)

        assertEquals enlistment.hashCode(), 0
    }

    @Test
    void testContainsValue() {

        def enlistment = new Enlistment(["this":"that"])

        assertTrue enlistment.containsValue("that")
    }

    @Test
    void testValues() {

        def enlistment = new Enlistment([
            "this":"that",
            "those":"these"
        ])

        assertEquals enlistment.values().size(), 2
        assertTrue enlistment.values().contains("that")
        assertTrue enlistment.values().contains("these")
    }
}
