package com.okta.sdk.impl.util;

import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.testng.Assert.*;

public class StringInputStreamTest {

    @Test
    public void testReadFromStream() throws IOException {
        String testString = "Hello World";
        StringInputStream inputStream = new StringInputStream(testString);

        byte[] buffer = new byte[testString.length()];
        int bytesRead = inputStream.read(buffer);

        assertEquals(bytesRead, testString.length(), "Should read the expected number of bytes");
        assertEquals(new String(buffer, StandardCharsets.UTF_8), testString, "Read bytes should match original string");

        // Verify we've reached the end of the stream
        assertEquals(inputStream.read(), -1, "End of stream should return -1");
    }

    @Test
    public void testToString() {
        String testString = "Test string for toString";
        StringInputStream inputStream = new StringInputStream(testString);

        assertEquals(inputStream.toString(), testString, "toString should return the original string");
    }

    @Test
    public void testEmptyString() throws IOException {
        String emptyString = "";
        StringInputStream inputStream = new StringInputStream(emptyString);

        assertEquals(inputStream.available(), 0, "Available bytes should be 0 for empty string");
        assertEquals(inputStream.read(), -1, "Reading from empty stream should return -1");
        assertEquals(inputStream.toString(), emptyString, "toString should return empty string");
    }

    @Test
    public void testMultibyteCharacters() throws IOException {
        // String with multibyte characters
        String multibyteString = "こんにちは世界"; // "Hello World" in Japanese
        StringInputStream inputStream = new StringInputStream(multibyteString);

        byte[] expectedBytes = multibyteString.getBytes(StandardCharsets.UTF_8);
        byte[] actualBytes = new byte[expectedBytes.length];

        int bytesRead = inputStream.read(actualBytes);

        assertEquals(bytesRead, expectedBytes.length, "Should read the expected number of bytes");
        assertEquals(actualBytes, expectedBytes, "Bytes should match UTF-8 encoding of string");
        assertEquals(new String(actualBytes, StandardCharsets.UTF_8), multibyteString,
            "Decoded bytes should match original string");
    }

    @Test
    public void testReset() throws IOException {
        String testString = "Reset test";
        StringInputStream inputStream = new StringInputStream(testString);

        // Read some bytes
        byte[] buffer = new byte[5];
        inputStream.read(buffer);
        assertEquals(new String(buffer, StandardCharsets.UTF_8), "Reset", "Should read first 5 bytes");

        // Reset and read again
        inputStream.reset();
        byte[] fullBuffer = new byte[testString.length()];
        inputStream.read(fullBuffer);
        assertEquals(new String(fullBuffer, StandardCharsets.UTF_8), testString,
            "After reset, should read from beginning");
    }
}
