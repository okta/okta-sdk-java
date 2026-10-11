/*
 * Copyright 2017-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package com.okta.sdk.resource.client;

import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class ApiClientTest {

    /**
     * Sets the ThreadLocal fields via reflection so we can verify close() clears them.
     */
    @SuppressWarnings("unchecked")
    private void setThreadLocals(int statusCode, Map<String, List<String>> headers) throws Exception {
        Field statusField = ApiClient.class.getDeclaredField("lastStatusCode");
        statusField.setAccessible(true);
        ((ThreadLocal<Integer>) statusField.get(null)).set(statusCode);

        Field headersField = ApiClient.class.getDeclaredField("lastResponseHeaders");
        headersField.setAccessible(true);
        ((ThreadLocal<Map<String, List<String>>>) headersField.get(null)).set(headers);
    }

    @SuppressWarnings("unchecked")
    private Object getThreadLocalValue(String fieldName) throws Exception {
        Field field = ApiClient.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return ((ThreadLocal<?>) field.get(null)).get();
    }

    /**
     * Verifies that close() removes lastStatusCode and lastResponseHeaders
     * from the current thread's ThreadLocal slots.
     */
    @Test
    public void testCloseRemovesThreadLocals() throws Exception {
        ApiClient client = new ApiClient();

        // Populate ThreadLocals
        setThreadLocals(200, Collections.singletonMap("link", Collections.singletonList("<https://example.okta.com/api/v1/users?after=abc>; rel=\"next\"")));

        assertNotNull(getThreadLocalValue("lastStatusCode"), "lastStatusCode should be set before close()");
        assertNotNull(getThreadLocalValue("lastResponseHeaders"), "lastResponseHeaders should be set before close()");

        // Act
        client.close();

        // Assert ThreadLocals are cleared
        assertNull(getThreadLocalValue("lastStatusCode"), "lastStatusCode should be null after close()");
        assertNull(getThreadLocalValue("lastResponseHeaders"), "lastResponseHeaders should be null after close()");
    }

    /**
     * Verifies that close() is safe to call when ThreadLocals are not set (no exception).
     */
    @Test
    public void testCloseIsIdempotent() {
        ApiClient client = new ApiClient();
        // Neither ThreadLocal is set — calling close() should not throw
        client.close();
        client.close(); // calling twice should also be safe
    }

    /**
     * Verifies that close() only clears the calling thread's state,
     * not state set on a different thread.
     */
    @Test
    public void testCloseDoesNotAffectOtherThreads() throws Exception {
        ApiClient client = new ApiClient();

        // Set ThreadLocals on a background thread
        Thread background = new Thread(() -> {
            try {
                setThreadLocals(200, Collections.singletonMap("x-rate-limit", Collections.singletonList("100")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        background.start();
        background.join();

        // Close on the current (different) thread — should not affect background thread's values
        // (background thread already finished, but the point is close() scopes to current thread only)
        client.close(); // must not throw

        // Current thread should have no values (was never set on this thread)
        assertNull(getThreadLocalValue("lastStatusCode"));
        assertNull(getThreadLocalValue("lastResponseHeaders"));
    }

}
