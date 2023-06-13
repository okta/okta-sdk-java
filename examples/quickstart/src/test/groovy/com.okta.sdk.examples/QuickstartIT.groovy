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
package com.okta.sdk.examples

import org.testng.annotations.Test
import quickstart.Quickstart

import static org.testng.Assert.fail
/**
 * This integration test validates that the example code in the JSDK Quickstart Guide (http://docs.stormpath.com/java/quickstart) works
 *
 * @since 0.5.0
 */
class QuickstartIT {

    @Test()
    void testCode() {
        try {
            String[] args = {}
            Quickstart.main(args)
        } catch (Exception e) {
            fail("Quick start threw exception", e)
        }
    }
}
