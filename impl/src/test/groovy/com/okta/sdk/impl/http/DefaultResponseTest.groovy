/*
 * Copyright 2019-Present Okta, Inc.
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
package com.okta.sdk.impl.http

import com.okta.sdk.impl.http.support.DefaultResponse
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import java.nio.charset.StandardCharsets

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class DefaultResponseTest {

    @Test(dataProvider = "hasBody-bodyContentExpected")
    void hasBodyTest(String content, int size, boolean expectedHasBodyResult) {
        def inputStream = content != null ? new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8.toString())) : null
        def response = new DefaultResponse(204, MediaType.TEXT_PLAIN, inputStream, size)
        assertThat "Expected hasBody to return ${expectedHasBodyResult}", response.hasBody(), is(expectedHasBodyResult)
    }

    @DataProvider(name = "hasBody-bodyContentExpected")
    static Object[][] hasBodyTestData() {
      return [
          ["", -1, false],
          ["", 0, false],
          [null, -1, false],
          [null, 0, false],
          ["something", 9, true],
          ["something", -1, true],
          ["something", 0, false] // This would be a server error, but if the content length is set, use it
      ]
   }
}