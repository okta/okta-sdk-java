/*
 * Copyright 2002-2017 the original author or authors.
 * Modifications Copyright 2017 Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.okta.sdk.lang;

import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.testng.AssertJUnit.*;
import static com.okta.sdk.lang.Objects.*;

/**
 * Unit tests for {@link Objects}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rick Evans
 * @author Sam Brannen
 */
public class ObjectsTest {

    @Test
    public void isCheckedException() {
        assertTrue(Objects.isCheckedException(new Exception()));
        assertTrue(Objects.isCheckedException(new SQLException()));

        assertFalse(Objects.isCheckedException(new RuntimeException()));
        assertFalse(Objects.isCheckedException(new IllegalArgumentException("")));

        // Any Throwable other than RuntimeException and Error
        // has to be considered checked according to the JLS.
        assertTrue(Objects.isCheckedException(new Throwable()));
    }

    @Test
    public void isCompatibleWithThrowsClause() {
        Class<?>[] empty = new Class[0];
        Class<?>[] exception = new Class[] {Exception.class};
        Class<?>[] sqlAndIO = new Class[] {SQLException.class, IOException.class};
        Class<?>[] throwable = new Class[] {Throwable.class};

        assertTrue(Objects.isCompatibleWithThrowsClause(new RuntimeException()));
        assertTrue(Objects.isCompatibleWithThrowsClause(new RuntimeException(), empty));
        assertTrue(Objects.isCompatibleWithThrowsClause(new RuntimeException(), exception));
        assertTrue(Objects.isCompatibleWithThrowsClause(new RuntimeException(), sqlAndIO));
        assertTrue(Objects.isCompatibleWithThrowsClause(new RuntimeException(), throwable));

        assertFalse(Objects.isCompatibleWithThrowsClause(new Exception()));
        assertFalse(Objects.isCompatibleWithThrowsClause(new Exception(), empty));
        assertTrue(Objects.isCompatibleWithThrowsClause(new Exception(), exception));
        assertFalse(Objects.isCompatibleWithThrowsClause(new Exception(), sqlAndIO));
        assertTrue(Objects.isCompatibleWithThrowsClause(new Exception(), throwable));

        assertFalse(Objects.isCompatibleWithThrowsClause(new SQLException()));
        assertFalse(Objects.isCompatibleWithThrowsClause(new SQLException(), empty));
        assertTrue(Objects.isCompatibleWithThrowsClause(new SQLException(), exception));
        assertTrue(Objects.isCompatibleWithThrowsClause(new SQLException(), sqlAndIO));
        assertTrue(Objects.isCompatibleWithThrowsClause(new SQLException(), throwable));

        assertFalse(Objects.isCompatibleWithThrowsClause(new Throwable()));
        assertFalse(Objects.isCompatibleWithThrowsClause(new Throwable(), empty));
        assertFalse(Objects.isCompatibleWithThrowsClause(new Throwable(), exception));
        assertFalse(Objects.isCompatibleWithThrowsClause(new Throwable(), sqlAndIO));
        assertTrue(Objects.isCompatibleWithThrowsClause(new Throwable(), throwable));
    }

    @Test
    public void isEmptyNull() {
        assertTrue(isEmpty(null));
    }

    @Test
    public void isEmptyArray() {
        assertTrue(isEmpty(new char[0]));
        assertTrue(isEmpty(new Object[0]));
        assertTrue(isEmpty(new Integer[0]));

        assertFalse(isEmpty(new int[] { 42 }));
        assertFalse(isEmpty(new Integer[] { new Integer(42) }));
    }

    @Test
    public void isEmptyCollection() {
        assertTrue(isEmpty(Collections.emptyList()));
        assertTrue(isEmpty(Collections.emptySet()));

        Set<String> set = new HashSet<>();
        set.add("foo");
        assertFalse(isEmpty(set));
        assertFalse(isEmpty(Arrays.asList("foo")));
    }

    @Test
    public void isEmptyMap() {
        assertTrue(isEmpty(Collections.emptyMap()));

        HashMap<String, Object> map = new HashMap<>();
        map.put("foo", 42L);
        assertFalse(isEmpty(map));
    }

    @Test
    public void isEmptyCharSequence() {
        assertTrue(isEmpty(new StringBuilder()));
        assertTrue(isEmpty(""));

        assertFalse(isEmpty(new StringBuilder("foo")));
        assertFalse(isEmpty("   "));
        assertFalse(isEmpty("\t"));
        assertFalse(isEmpty("foo"));
    }

    @Test
    public void isEmptyUnsupportedObjectType() {
        assertFalse(isEmpty(42L));
        assertFalse(isEmpty(new Object()));
    }

    @Test
    public void toObjectArray() {
        int[] a = new int[] {1, 2, 3, 4, 5};
        Integer[] wrapper = (Integer[]) Objects.toObjectArray(a);
        assertTrue(wrapper.length == 5);
        for (int i = 0; i < wrapper.length; i++) {
            assertEquals(a[i], wrapper[i].intValue());
        }
    }

    @Test
    public void toObjectArrayWithNull() {
        Object[] objects = Objects.toObjectArray(null);
        assertNotNull(objects);
        assertEquals(0, objects.length);
    }

    @Test
    public void toObjectArrayWithEmptyPrimitiveArray() {
        Object[] objects = Objects.toObjectArray(new byte[] {});
        assertNotNull(objects);
        assertEquals(0, objects.length);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void toObjectArrayWithNonArrayType() {
        Objects.toObjectArray("Not an []");
    }

    @Test
    public void toObjectArrayWithNonPrimitiveArray() {
        String[] source = new String[] {"Bingo"};
        assertArrayEquals(source, Objects.toObjectArray(source));
    }

    @Test
    public void addObjectToArraySunnyDay() {
        String[] array = new String[] {"foo", "bar"};
        String newElement = "baz";
        Object[] newArray = Objects.addObjectToArray(array, newElement);
        assertEquals(3, newArray.length);
        assertEquals(newElement, newArray[2]);
    }

    @Test
    public void addObjectToArrayWhenEmpty() {
        String[] array = new String[0];
        String newElement = "foo";
        String[] newArray = Objects.addObjectToArray(array, newElement);
        assertEquals(1, newArray.length);
        assertEquals(newElement, newArray[0]);
    }

    @Test
    public void addObjectToSingleNonNullElementArray() {
        String existingElement = "foo";
        String[] array = new String[] {existingElement};
        String newElement = "bar";
        String[] newArray = Objects.addObjectToArray(array, newElement);
        assertEquals(2, newArray.length);
        assertEquals(existingElement, newArray[0]);
        assertEquals(newElement, newArray[1]);
    }

    @Test
    public void addObjectToSingleNullElementArray() {
        String[] array = new String[] {null};
        String newElement = "bar";
        String[] newArray = Objects.addObjectToArray(array, newElement);
        assertEquals(2, newArray.length);
        assertEquals(null, newArray[0]);
        assertEquals(newElement, newArray[1]);
    }

    @Test
    public void addObjectToNullArray() throws Exception {
        String newElement = "foo";
        String[] newArray = Objects.addObjectToArray(null, newElement);
        assertEquals(1, newArray.length);
        assertEquals(newElement, newArray[0]);
    }

    @Test
    public void addNullObjectToNullArray() throws Exception {
        Object[] newArray = Objects.addObjectToArray(null, null);
        assertEquals(1, newArray.length);
        assertEquals(null, newArray[0]);
    }

    @Test
    public void nullSafeEqualsWithArrays() throws Exception {
        assertTrue(Objects.nullSafeEquals(new String[] {"a", "b", "c"}, new String[] {"a", "b", "c"}));
        assertTrue(Objects.nullSafeEquals(new int[] {1, 2, 3}, new int[] {1, 2, 3}));
    }

    @Test
    public void identityToString() {
        Object obj = new Object();
        String expected = obj.getClass().getName() + "@" + Objects.getIdentityHexString(obj);
        String actual = Objects.identityToString(obj);
        assertEquals(expected, actual);
    }

    @Test
    public void identityToStringWithNullObject() {
        assertEquals("", Objects.identityToString(null));
    }

    @Test
    public void nullSafeHashCodeWithBooleanArray() {
        int expected = 31 * 7 + Boolean.TRUE.hashCode();
        expected = 31 * expected + Boolean.FALSE.hashCode();

        boolean[] array = {true, false};
        int actual = Objects.nullSafeHashCode(array);

        assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithBooleanArrayEqualToNull() {
        assertEquals(0, Objects.nullSafeHashCode((boolean[]) null));
    }

    @Test
    public void nullSafeHashCodeWithByteArray() {
        int expected = 31 * 7 + 8;
        expected = 31 * expected + 10;

        byte[] array = {8, 10};
        int actual = Objects.nullSafeHashCode(array);

        assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithByteArrayEqualToNull() {
        assertEquals(0, Objects.nullSafeHashCode((byte[]) null));
    }

    @Test
    public void nullSafeHashCodeWithCharArray() {
        int expected = 31 * 7 + 'a';
        expected = 31 * expected + 'E';

        char[] array = {'a', 'E'};
        int actual = Objects.nullSafeHashCode(array);

        assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithCharArrayEqualToNull() {
        assertEquals(0, Objects.nullSafeHashCode((char[]) null));
    }

    @Test
    public void nullSafeHashCodeWithDoubleArray() {
        long bits = Double.doubleToLongBits(8449.65);
        int expected = 31 * 7 + (int) (bits ^ (bits >>> 32));
        bits = Double.doubleToLongBits(9944.923);
        expected = 31 * expected + (int) (bits ^ (bits >>> 32));

        double[] array = {8449.65, 9944.923};
        int actual = Objects.nullSafeHashCode(array);

        assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithDoubleArrayEqualToNull() {
        assertEquals(0, Objects.nullSafeHashCode((double[]) null));
    }

    @Test
    public void nullSafeHashCodeWithFloatArray() {
        int expected = 31 * 7 + Float.floatToIntBits(9.6f);
        expected = 31 * expected + Float.floatToIntBits(7.4f);

        float[] array = {9.6f, 7.4f};
        int actual = Objects.nullSafeHashCode(array);

        assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithFloatArrayEqualToNull() {
        assertEquals(0, Objects.nullSafeHashCode((float[]) null));
    }

    @Test
    public void nullSafeHashCodeWithIntArray() {
        int expected = 31 * 7 + 884;
        expected = 31 * expected + 340;

        int[] array = {884, 340};
        int actual = Objects.nullSafeHashCode(array);

        assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithIntArrayEqualToNull() {
        assertEquals(0, Objects.nullSafeHashCode((int[]) null));
    }

    @Test
    public void nullSafeHashCodeWithLongArray() {
        long lng = 7993l;
        int expected = 31 * 7 + (int) (lng ^ (lng >>> 32));
        lng = 84320l;
        expected = 31 * expected + (int) (lng ^ (lng >>> 32));

        long[] array = {7993l, 84320l};
        int actual = Objects.nullSafeHashCode(array);

        assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithLongArrayEqualToNull() {
        assertEquals(0, Objects.nullSafeHashCode((long[]) null));
    }

    @Test
    public void nullSafeHashCodeWithObject() {
        String str = "Luke";
        assertEquals(str.hashCode(), Objects.nullSafeHashCode(str));
    }

    @Test
    public void nullSafeHashCodeWithObjectArray() {
        int expected = 31 * 7 + "Leia".hashCode();
        expected = 31 * expected + "Han".hashCode();

        Object[] array = {"Leia", "Han"};
        int actual = Objects.nullSafeHashCode(array);

        assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithObjectArrayEqualToNull() {
        assertEquals(0, Objects.nullSafeHashCode((Object[]) null));
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingBooleanArray() {
        Object array = new boolean[] {true, false};
        int expected = Objects.nullSafeHashCode((boolean[]) array);
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingByteArray() {
        Object array = new byte[] {6, 39};
        int expected = Objects.nullSafeHashCode((byte[]) array);
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingCharArray() {
        Object array = new char[] {'l', 'M'};
        int expected = Objects.nullSafeHashCode((char[]) array);
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingDoubleArray() {
        Object array = new double[] {68930.993, 9022.009};
        int expected = Objects.nullSafeHashCode((double[]) array);
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingFloatArray() {
        Object array = new float[] {9.9f, 9.54f};
        int expected = Objects.nullSafeHashCode((float[]) array);
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingIntArray() {
        Object array = new int[] {89, 32};
        int expected = Objects.nullSafeHashCode((int[]) array);
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingLongArray() {
        Object array = new long[] {4389, 320};
        int expected = Objects.nullSafeHashCode((long[]) array);
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingObjectArray() {
        Object array = new Object[] {"Luke", "Anakin"};
        int expected = Objects.nullSafeHashCode((Object[]) array);
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingShortArray() {
        Object array = new short[] {5, 3};
        int expected = Objects.nullSafeHashCode((short[]) array);
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectEqualToNull() {
        assertEquals(0, Objects.nullSafeHashCode((Object) null));
    }

    @Test
    public void nullSafeHashCodeWithShortArray() {
        int expected = 31 * 7 + 70;
        expected = 31 * expected + 8;

        short[] array = {70, 8};
        int actual = Objects.nullSafeHashCode(array);

        assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithShortArrayEqualToNull() {
        assertEquals(0, Objects.nullSafeHashCode((short[]) null));
    }

    @Test
    public void nullSafeToStringWithBooleanArray() {
        boolean[] array = {true, false};
        assertEquals("{true, false}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithBooleanArrayBeingEmpty() {
        boolean[] array = {};
        assertEquals("{}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithBooleanArrayEqualToNull() {
        assertEquals("null", Objects.nullSafeToString((boolean[]) null));
    }

    @Test
    public void nullSafeToStringWithByteArray() {
        byte[] array = {5, 8};
        assertEquals("{5, 8}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithByteArrayBeingEmpty() {
        byte[] array = {};
        assertEquals("{}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithByteArrayEqualToNull() {
        assertEquals("null", Objects.nullSafeToString((byte[]) null));
    }

    @Test
    public void nullSafeToStringWithCharArray() {
        char[] array = {'A', 'B'};
        assertEquals("{'A', 'B'}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithCharArrayBeingEmpty() {
        char[] array = {};
        assertEquals("{}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithCharArrayEqualToNull() {
        assertEquals("null", Objects.nullSafeToString((char[]) null));
    }

    @Test
    public void nullSafeToStringWithDoubleArray() {
        double[] array = {8594.93, 8594023.95};
        assertEquals("{8594.93, 8594023.95}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithDoubleArrayBeingEmpty() {
        double[] array = {};
        assertEquals("{}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithDoubleArrayEqualToNull() {
        assertEquals("null", Objects.nullSafeToString((double[]) null));
    }

    @Test
    public void nullSafeToStringWithFloatArray() {
        float[] array = {8.6f, 43.8f};
        assertEquals("{8.6, 43.8}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithFloatArrayBeingEmpty() {
        float[] array = {};
        assertEquals("{}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithFloatArrayEqualToNull() {
        assertEquals("null", Objects.nullSafeToString((float[]) null));
    }

    @Test
    public void nullSafeToStringWithIntArray() {
        int[] array = {9, 64};
        assertEquals("{9, 64}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithIntArrayBeingEmpty() {
        int[] array = {};
        assertEquals("{}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithIntArrayEqualToNull() {
        assertEquals("null", Objects.nullSafeToString((int[]) null));
    }

    @Test
    public void nullSafeToStringWithLongArray() {
        long[] array = {434l, 23423l};
        assertEquals("{434, 23423}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithLongArrayBeingEmpty() {
        long[] array = {};
        assertEquals("{}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithLongArrayEqualToNull() {
        assertEquals("null", Objects.nullSafeToString((long[]) null));
    }

    @Test
    public void nullSafeToStringWithPlainOldString() {
        assertEquals("I shoh love tha taste of mangoes", Objects.nullSafeToString("I shoh love tha taste of mangoes"));
    }

    @Test
    public void nullSafeToStringWithObjectArray() {
        Object[] array = {"Han", new Long(43)};
        assertEquals("{Han, 43}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithObjectArrayBeingEmpty() {
        Object[] array = {};
        assertEquals("{}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithObjectArrayEqualToNull() {
        assertEquals("null", Objects.nullSafeToString((Object[]) null));
    }

    @Test
    public void nullSafeToStringWithShortArray() {
        short[] array = {7, 9};
        assertEquals("{7, 9}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithShortArrayBeingEmpty() {
        short[] array = {};
        assertEquals("{}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithShortArrayEqualToNull() {
        assertEquals("null", Objects.nullSafeToString((short[]) null));
    }

    @Test
    public void nullSafeToStringWithStringArray() {
        String[] array = {"Luke", "Anakin"};
        assertEquals("{Luke, Anakin}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithStringArrayBeingEmpty() {
        String[] array = {};
        assertEquals("{}", Objects.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithStringArrayEqualToNull() {
        assertEquals("null", Objects.nullSafeToString((String[]) null));
    }

    @Test
    public void containsConstant() {
        assertThat(Objects.containsConstant(Tropes.values(), "FOO"), is(true));
        assertThat(Objects.containsConstant(Tropes.values(), "foo"), is(true));
        assertThat(Objects.containsConstant(Tropes.values(), "BaR"), is(true));
        assertThat(Objects.containsConstant(Tropes.values(), "bar"), is(true));
        assertThat(Objects.containsConstant(Tropes.values(), "BAZ"), is(true));
        assertThat(Objects.containsConstant(Tropes.values(), "baz"), is(true));

        assertThat(Objects.containsConstant(Tropes.values(), "BOGUS"), is(false));

        assertThat(Objects.containsConstant(Tropes.values(), "FOO", true), is(true));
        assertThat(Objects.containsConstant(Tropes.values(), "foo", true), is(false));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void caseInsensitiveValueOf() {
        assertThat(Objects.caseInsensitiveValueOf(Tropes.values(), "foo"), is(Tropes.FOO));
        assertThat(Objects.caseInsensitiveValueOf(Tropes.values(), "BAR"), is(Tropes.BAR));

        Objects.caseInsensitiveValueOf(Tropes.values(), "bogus");
    }

    private void assertEqualHashCodes(int expected, Object array) {
        int actual = Objects.nullSafeHashCode(array);
        assertEquals(expected, actual);
        assertTrue(array.hashCode() != actual);
    }


    enum Tropes { FOO, BAR, baz }

}