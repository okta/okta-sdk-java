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
package com.okta.sdk.impl.resource

import com.okta.sdk.impl.ds.InternalDataStore
import com.okta.commons.lang.Assert
import com.okta.commons.lang.Classes
import com.okta.sdk.resource.Resource
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.testng.annotations.Test

import java.lang.reflect.Method
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Stream

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.mockito.Mockito.*
import static org.testng.Assert.fail


/**
 * Reflection based tests for basic getter and setter methods on generated code.  These tests provide very basic
 * coverage and do NOT test anything specific.
 */
class GeneratedResourceTest {

    /**
     * Sets values via setter methods then validates the value was set by using the corresponding getter method.
     */
    @Test
    void testGetterAndSetters() {

        InternalDataStore dataStore = createMockDataStore()

        clazzStream().forEach { clazz ->

                def resource = (AbstractResource) Classes.instantiate(
                        clazz.getDeclaredConstructor(InternalDataStore, Map), dataStore, null)

                setViaSetter(resource)
                validateViaGetter(clazz, resource, true)
            }
    }

    /**
     * Sets values via property methods then validates the value was set by using the corresponding getter method.
     */
    @Test
    void testPropertySetThenGetters() {

        InternalDataStore dataStore = createMockDataStore()

        clazzStream().forEach { clazz ->

                    def resource = (AbstractResource) Classes.instantiate(
                            clazz.getDeclaredConstructor(InternalDataStore, Map), dataStore, null)

                    setViaProperty(resource)
                    validateViaGetter(clazz, resource, true)
                }
    }

    /**
     * Tests calling Resource(InternalDataStore) does NOT throw an exception and the internal properties
     * results in an empty map.
     */
    @Test
    void testConstructorsDoNotThrowException() {
        InternalDataStore dataStore = createMockDataStore()

        clazzStream().forEach { clazz ->

                    // just make sure this doesn't throw an exception.
                    def resource = (AbstractResource) Classes.instantiate(clazz.getDeclaredConstructor(InternalDataStore), dataStore)
                    assertThat resource.getInternalProperties(), anEmptyMap()
                }

    }

    Stream<Class> clazzStream() {
        return Arrays.stream(getClasses("com.okta.sdk.impl.resource"))
                .filter { clazz -> clazz.superclass != null}
                .filter { clazz -> AbstractInstanceResource.isAssignableFrom(clazz)}
                .filter { clazz -> !clazz.name.contains("Test")}
                .filter { clazz -> !clazz.name.contains("Abstract")}
    }

    void validateViaGetter(Class clazz, Resource resource, boolean withSettersOnly = true) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

        Arrays.stream(clazz.getDeclaredMethods())
                .filter { it.name.startsWith("get") }
                .filter { !withSettersOnly || setterFromGetter(clazz, it) != null }
                .forEach {
            def result = it.invoke(resource)
            if (it.returnType == String) {
                assertThat it.invoke(resource), equalToObject("value for: ${it.name}".toString())
            }
            else if (it.returnType == Integer){
                assertThat it.invoke(resource), equalToObject(42)
            }
            else if (it.returnType == Date) {
                assertThat it.invoke(resource), equalToObject(df.parse("2001-07-04T12:08:56.235-0700"))
            }
            else if (it.returnType == Map) {
                assertThat it.invoke(resource), instanceOf(Map)
            }
            else if (it.returnType == List && !withSettersOnly) { // TODO: Lists are only being set via property right now
                assertThat it.invoke(resource), instanceOf(List)
            }
            else if (it.returnType.interfaces.contains(Resource)) {
                assertThat "method ${it.toString()} returned null value.", result, notNullValue()
            }
            else if (it.returnType.isEnum()) {
                assertThat "method ${it.toString()} returned null value.", result, notNullValue()
            }
        }
    }

    static void setViaProperty(def resource) {
        resource.getPropertyDescriptors()
                .forEach { key, property ->
            if (property instanceof StringProperty) {
                resource.setProperty(property, "value for: get${camelize(key, false)}")
            }
            else if (property instanceof IntegerProperty) {
                resource.setProperty(property, 42)
            }
            else if (property instanceof DateProperty) {
                resource.setProperty(property, "2001-07-04T12:08:56.235-0700")
            }
            else if (property instanceof MapProperty) {
                resource.setProperty(property, [one: "two"])
            }
            else if (property instanceof ResourceReference) {
                resource.setProperty(property, [one: "two"])
            }
            else if (property instanceof BooleanProperty) {
                resource.setProperty(property, true)
            }
            else if (property instanceof EnumProperty) {
                def value = property.type.getEnumConstants()[0]
                resource.setProperty(property, value.name())
            }
        }
        resource.materialize()
    }

    ///////////////////////
    // Test Util methods //
    ///////////////////////

    static Method setterFromGetter(Class clazz, Method method) {
        try {
            return clazz.getMethod(method.name.replaceFirst("get", "set"), method.returnType)
        }
        catch (NoSuchMethodException e) {
            // ignored
        }
        return null
    }

    static Method setterFromProperty(AbstractResource resource, Property property) {
        try {
            def methodName = "set${camelize(property.name, false)}"
            return resource.getClass().getMethod(methodName, property.getType())
        }
        catch (NoSuchMethodException e) {
            // ignored
        }
        return null
    }

    static void setViaSetter(AbstractResource resource) {
        resource.getPropertyDescriptors()
                .forEach { key, property ->

            def method = setterFromProperty(resource, property)
            if (method != null) {
                def value = null

                if (property instanceof StringProperty) {
                    value = "value for: get${camelize(key, false)}".toString()
                } else if (property instanceof IntegerProperty) {
                    value = 42
                } else if (property instanceof DateProperty) {
                    value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                        .parse("2001-07-04T12:08:56.235-0700")
                } else if (property instanceof MapProperty) {
                    value = [one: "two"]
                } else if (property instanceof ResourceReference) {
                    value = mock(property.getType())
                } else if (property instanceof BooleanProperty) {
                    value = true
                } else if (property instanceof ListProperty) {
                    value = new ArrayList()
                } else if (property instanceof EnumProperty) {
                     value = property.type.getEnumConstants()[0]
                }

                try {
                    method.invoke(resource, value)
                } catch(IllegalArgumentException e) {
                    fail("Illegal argument for method '${method.name}', resource class: '${resource.getClass()}', attempted value type '${value.getClass()}'", e)
                }
            }

        }
        resource.materialize()
    }

    InternalDataStore createMockDataStore() {
        InternalDataStore dataStore = mock(InternalDataStore)
        when(dataStore.getResource(Mockito.nullable(String), Mockito.any(Class.class)))
                .then(new Answer<Object>() {
            @Override
            Object answer(InvocationOnMock invocation) throws Throwable {
                def clazz = (Class)invocation.arguments[1]
                def resource = (AbstractResource) Classes.instantiate(
                        clazz.getDeclaredConstructor(InternalDataStore, Map), dataStore, [three: "four"])
                return resource

            }
        })

        when(dataStore.instantiate(Mockito.any(Class.class), Mockito.any(Map)))
                .then(new Answer<Object>() {
            @Override
            Object answer(InvocationOnMock invocation) throws Throwable {
                def clazz = (Class)invocation.arguments[0]
                clazz = Classes.forName("${clazz.package.name.replace("sdk.resource", "sdk.impl.resource")}.Default${clazz.simpleName}")
                def resource = (AbstractResource) Classes.instantiate(
                        clazz.getDeclaredConstructor(InternalDataStore, Map), dataStore, [three: "four"])
                return resource
            }
        })

        return dataStore
    }

    /**
     * Camelize name (parameter, property, method, etc)
     *
     * @param word string to be camelize
     * @param lowercaseFirstLetter lower case for first letter if set to true
     * @return camelized string
     */
    static String camelize(String word, boolean lowercaseFirstLetter) {

        // special cases
        if (word.toLowerCase(Locale.ENGLISH).contains("oauth")) {
            // set to oAuth, 'lowercaseFirstLetter' will be resolved below
            word = word.replaceAll("oauth", "oAuth")
        }

        // Replace all slashes with dots (package separator)
        Pattern p = Pattern.compile("\\/(.?)");
        Matcher m = p.matcher(word);
        while (m.find()) {
            word = m.replaceFirst("." + m.group(1)/*.toUpperCase()*/); // FIXME: a parameter should not be assigned. Also declare the methods parameters as 'final'.
            m = p.matcher(word);
        }

        // case out dots
        String[] parts = word.split("\\.");
        StringBuilder f = new StringBuilder();
        for (String z : parts) {
            if (z.length() > 0) {
                f.append(Character.toUpperCase(z.charAt(0))).append(z.substring(1));
            }
        }
        word = f.toString();

        m = p.matcher(word);
        while (m.find()) {
            word = m.replaceFirst("" + Character.toUpperCase(m.group(1).charAt(0)) + m.group(1).substring(1)/*.toUpperCase()*/);
            m = p.matcher(word);
        }

        // Uppercase the class name.
        p = Pattern.compile('''(\\.?)(\\w)([^\\.]*)$''');
        m = p.matcher(word);
        if (m.find()) {
            String rep = m.group(1) + m.group(2).toUpperCase() + m.group(3);
            rep = rep.replaceAll('''\\$''', '''\\\\\\$''');
            word = m.replaceAll(rep);
        }

        // Remove all underscores
        p = Pattern.compile("(_)(.)");
        m = p.matcher(word);
        while (m.find()) {
            word = m.replaceFirst(m.group(2).toUpperCase());
            m = p.matcher(word);
        }

        if (lowercaseFirstLetter && word.length() > 0) {
            word = word.substring(0, 1).toLowerCase() + word.substring(1);
        }

        word = word.replace('#', '')
        word = word.replace('$', '')

        return word;
    }


    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader()
        assert classLoader != null
        String path = packageName.replace('.', '/')
        Enumeration<URL> resources = classLoader.getResources(path)
        List<File> dirs = new ArrayList<File>()
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement()
            dirs.add(new File(resource.getFile()))
        }
        ArrayList<Class> classes = new ArrayList<Class>()
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()])
    }

/**
 * Recursive method used to find all classes in a given directory and subdirs.
 *
 * @param directory   The base directory
 * @param packageName The package name for classes found inside the base directory
 * @return The classes
 * @throws ClassNotFoundException
 */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>()
        if (!directory.exists()) {
            return classes
        }
        File[] files = directory.listFiles()
        for (File file : files) {
            if (file.isDirectory()) {
                Assert.isTrue(!file.getName().contains("."), "Expected directory containing '.' in path")
                classes.addAll(findClasses(file, packageName + "." + file.getName()))
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)))
            }
        }
        return classes;
    }

}
