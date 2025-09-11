package com.okta.sdk.impl.util;

import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.*;

public class SoftHashMapTest {

    @Test
    public void testPutGetAndSize() {
        SoftHashMap<String, String> map = new SoftHashMap<>(10);
        assertTrue(map.isEmpty());
        assertNull(map.put("a", "A"));
        assertEquals(map.size(), 1);
        assertEquals(map.get("a"), "A");
        assertFalse(map.isEmpty());
    }

    @Test
    public void testPutReturnsPrevious() {
        SoftHashMap<String, String> map = new SoftHashMap<>(5);
        assertNull(map.put("k", "v1"));
        assertEquals(map.put("k", "v2"), "v1");
        assertEquals(map.get("k"), "v2");
        assertEquals(map.size(), 1);
    }

    @Test
    public void testContainsKeyAndValue() {
        SoftHashMap<String, String> map = new SoftHashMap<>(5);
        map.put("x", "X");
        assertTrue(map.containsKey("x"));
        assertTrue(map.containsValue("X"));
        assertFalse(map.containsKey("y"));
        assertFalse(map.containsValue("Y"));
    }

    @Test
    public void testRemove() {
        SoftHashMap<String, String> map = new SoftHashMap<>(5);
        map.put("k1", "v1");
        assertEquals(map.remove("k1"), "v1");
        assertNull(map.remove("k1"));
        assertFalse(map.containsKey("k1"));
        assertEquals(map.size(), 0);
    }

    @Test
    public void testClear() {
        SoftHashMap<String, String> map = new SoftHashMap<>(5);
        map.put("a", "A");
        map.put("b", "B");
        assertEquals(map.size(), 2);
        map.clear();
        assertEquals(map.size(), 0);
        assertTrue(map.isEmpty());
        assertNull(map.get("a"));
    }

    @Test
    public void testPutAll() {
        Map<String, String> src = new HashMap<>();
        src.put("a", "A");
        src.put("b", "B");
        SoftHashMap<String, String> map = new SoftHashMap<>(5);
        map.putAll(src);
        assertEquals(map.size(), 2);
        assertEquals(map.get("a"), "A");
        assertEquals(map.get("b"), "B");
    }

    @Test
    public void testEntrySetAndValues() {
        SoftHashMap<String, String> map = new SoftHashMap<>(5);
        map.put("k1", "v1");
        map.put("k2", "v2");
        Set<Map.Entry<String, String>> entries = map.entrySet();
        assertEquals(entries.size(), 2);
        assertTrue(map.values().contains("v1"));
        assertTrue(map.values().contains("v2"));
    }

    @Test
    public void testKeySetReflectsRemovals() {
        SoftHashMap<String, String> map = new SoftHashMap<>(5);
        map.put("k1", "v1");
        map.put("k2", "v2");
        assertTrue(map.keySet().contains("k1"));
        map.remove("k1");
        assertFalse(map.keySet().contains("k1"));
        assertEquals(map.size(), 1);
    }

    @Test
    public void testRetentionSizeZero() {
        SoftHashMap<String, String> map = new SoftHashMap<>(0);
        map.put("a", "A");
        map.put("b", "B");
        assertEquals(map.size(), 2);
        // Still retrievable (SoftReference not yet cleared)
        assertEquals(map.get("a"), "A");
    }

    @Test
    public void testConstructorWithSourceAndRetention() {
        Map<String, String> source = new HashMap<>();
        source.put("one", "1");
        source.put("two", "2");
        SoftHashMap<String, String> map = new SoftHashMap<>(source, 1);
        assertEquals(map.size(), 2);
        assertEquals(map.get("one"), "1");
    }

    @Test
    public void testPutAllNullOrEmptySafe() {
        SoftHashMap<String, String> map = new SoftHashMap<>(5);
        map.putAll(null);
        map.putAll(Collections.emptyMap());
        assertEquals(map.size(), 0);
    }

    @Test
    public void testConcurrencyBasic() throws InterruptedException {
        final int threads = 8;
        final int perThread = 250;
        SoftHashMap<Integer, Integer> map = new SoftHashMap<>(50);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger counter = new AtomicInteger();

        for (int t = 0; t < threads; t++) {
            new Thread(() -> {
                try {
                    start.await();
                    for (int i = 0; i < perThread; i++) {
                        int k = counter.incrementAndGet();
                        map.put(k, k * 10);
                        assertEquals(map.get(k), Integer.valueOf(k * 10));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    fail("Interrupted");
                } finally {
                    done.countDown();
                }
            }).start();
        }

        start.countDown();
        done.await();

        assertEquals(map.size(), threads * perThread);
        // spot check a few
        assertEquals(map.get(1), Integer.valueOf(10));
        assertEquals(map.get(threads * perThread), Integer.valueOf((threads * perThread) * 10));
    }
}
