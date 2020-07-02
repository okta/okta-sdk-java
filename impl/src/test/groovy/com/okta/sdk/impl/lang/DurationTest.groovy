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
package com.okta.sdk.impl.lang

import java.time.Duration
import org.testng.annotations.Test

import static org.testng.Assert.assertTrue

/**
 * @since 0.5.0
 */
class DurationTest {

    @Test
    void testGreaterThan() {

        //1,800,801 millis = 30 minutes + 1 millis
        def duration = Duration.ofMillis(1800001)
        def thirtyMin = Duration.ofMinutes(30)

        assertTrue duration.compareTo(thirtyMin) > 0
    }

    @Test
    void testLessThan() {

        //1,799,999 millis = 30 minutes - 1 millis
        def duration = Duration.ofMillis(1799999)
        def thirtyMin = Duration.ofMinutes(30)

        assertTrue duration.compareTo(thirtyMin) < 0
    }

    @Test
    void testEqualTo() {

        //1,800,000 millis = 30 minutes
        def duration = Duration.ofMillis(1800000)
        def thirtyMin = Duration.ofMinutes(30)

        assertTrue duration.compareTo(thirtyMin) == 0
    }

}
