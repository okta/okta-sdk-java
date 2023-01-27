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
package com.okta.sdk.resource

import com.okta.sdk.resource.common.PagedList
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNull
/**
 *
 * @since 10.1.1
 */
class PagedListTest {

    @Test
    void testGetAfterMarker() {

        String nextPage = "https://example.okta.com/api/v1/users?after=000u6t5aytx1WQUaxw1d7&limit=2"

        PagedList pagedList = new PagedList()
        String afterMarker = pagedList.getAfter(nextPage)

        assertEquals(afterMarker, "000u6t5aytx1WQUaxw1d7")
        
        nextPage = "https://example.okta.com/api/v1/users?x=y"

        afterMarker = pagedList.getAfter(nextPage)

        assertNull(afterMarker)
    }
}
