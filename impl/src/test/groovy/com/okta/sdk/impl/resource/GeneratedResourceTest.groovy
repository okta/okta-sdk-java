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
import com.okta.sdk.lang.Classes
import org.testng.annotations.Test

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*


/**
 * Reflection based tests for basic getter and setter methods on generated code.
 */
class GeneratedResourceTest {

    @Test
    void testGetterAndSetters() {

        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

        Arrays.stream(getClasses("com.okta.sdk.impl.resource"))
//            .filter { clazz -> clazz.superclass != null && clazz.superclass.simpleName == "AbstractResource"}
            .filter { clazz -> clazz.superclass != null}
            .filter { clazz -> clazz.superclass == AbstractInstanceResource}
            .filter { clazz -> !clazz.name.contains("Test")}
            .filter { clazz -> !clazz.name.contains("Abstract")}
//            .filter { clazz -> clazz.isAssignableFrom(AbstractResource) }
            .forEach { clazz ->

                def resource = (AbstractResource) Classes.instantiate(clazz.getDeclaredConstructor(InternalDataStore, Map), null, null)
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
                        }

                        Arrays.stream(clazz.getDeclaredMethods())
                            .filter { it.name.startsWith("get") }
                            .forEach {
                                if (it.returnType == String) {
                                    assertThat it.invoke(resource), equalToObject("value for: ${it.name}".toString())
                                }
                                else if (it.returnType == Integer){
                                    assertThat it.invoke(resource), equalToObject(42)
                                }
                                else if (it.returnType == Date) {
                                    assertThat it.invoke(resource), equalToObject(df1.parse("2001-07-04T12:08:56.235-0700"))
                                }

                            }
            }
    }

    /**
     * Camelize name (parameter, property, method, etc)
     *
     * @param word string to be camelize
     * @param lowercaseFirstLetter lower case for first letter if set to true
     * @return camelized string
     */
    static String camelize(String word, boolean lowercaseFirstLetter) {
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
                assert !file.getName().contains(".")
                classes.addAll(findClasses(file, packageName + "." + file.getName()))
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)))
            }
        }
        return classes;
    }

}
