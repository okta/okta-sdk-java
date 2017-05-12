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
package com.okta.sdk.impl.test;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * TestNG Listener that will restore environment variables after test methods.
 * All changes to environment variables are reverted after the test.
 * <pre>
 * public class EnvironmentVariablesTest {
 *   &#064;Test
 *   public void test() {
 *     setEnvironmentVariable("name", "value");
 *     assertEquals("value", System.getenv("name"));
 *   }
 * }
 * </pre>
 * <p><b>Warning:</b> This rule uses reflection for modifying internals of the
 * environment variables map. It fails if your {@code SecurityManager} forbids
 * such modifications.
 *
 * Based on: https://github.com/stefanbirkner/system-rules/blob/master/src/main/java/org/junit/contrib/java/lang/system/EnvironmentVariables.java
 */
public class RestoreEnvironmentVariables implements IInvokedMethodListener {

    private Map<String, String> originalVariables;

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        originalVariables = new HashMap<>(System.getenv());
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        restoreOriginalVariables();
    }


    void restoreOriginalVariables() {
        restoreVariables(getEditableMapOfVariables());
        Map<String, String> theCaseInsensitiveEnvironment
                = getTheCaseInsensitiveEnvironment();
        if (theCaseInsensitiveEnvironment != null)
            restoreVariables(theCaseInsensitiveEnvironment);
    }

    void restoreVariables(Map<String, String> variables) {
        variables.clear();
        variables.putAll(originalVariables);
    }

    /**
     * Set the value of an environment variable. You can delete an environment
     * variable by setting it to {@code null}.
     *
     * @param name the environment variable's name.
     * @param value the environment variable's new value.
     */
    public static void setEnvironmentVariable(String name, String value) {
        set(getEditableMapOfVariables(), name, value);
        set(getTheCaseInsensitiveEnvironment(), name, value);
    }

    /**
     * Clears all environment variables.
     */
    public static void clearEnvironmentVariables() {
        getEditableMapOfVariables().clear();
    }

    private static void set(Map<String, String> variables, String name, String value) {
        if (variables != null) //theCaseInsensitiveEnvironment may be null
            if (value == null)
                variables.remove(name);
            else
                variables.put(name, value);
    }

    private static Map<String, String> getEditableMapOfVariables() {
        Class<?> classOfMap = System.getenv().getClass();
        try {
            return getFieldValue(classOfMap, System.getenv(), "m");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("System Rules cannot access the field"
                    + " 'm' of the map System.getenv().", e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("System Rules expects System.getenv() to"
                    + " have a field 'm' but it has not.", e);
        }
    }

    /*
     * The names of environment variables are case-insensitive in Windows.
     * Therefore it stores the variables in a TreeMap named
     * theCaseInsensitiveEnvironment.
     */
    private static Map<String, String> getTheCaseInsensitiveEnvironment() {
        try {
            Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");
            return getFieldValue(
                    processEnvironment, null, "theCaseInsensitiveEnvironment");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("System Rules expects the existence of"
                    + " the class java.lang.ProcessEnvironment but it does not"
                    + " exist.", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("System Rules cannot access the static"
                    + " field 'theCaseInsensitiveEnvironment' of the class"
                    + " java.lang.ProcessEnvironment.", e);
        } catch (NoSuchFieldException e) {
            //this field is only available for Windows
            return null;
        }
    }

    private static Map<String, String> getFieldValue(Class<?> klass,
                                                     Object object, String name)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = klass.getDeclaredField(name);
        field.setAccessible(true);
        return (Map<String, String>) field.get(object);
    }
}
