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
package com.okta.sdk.lang;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public abstract class Collections {

    /**
     * Return {@code true} if the supplied Collection is {@code null}
     * or empty. Otherwise, return {@code false}.
     *
     * @param collection the Collection to check
     * @return whether the given Collection is empty
     */
    public static boolean isEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Returns the collection's size or {@code 0} if the collection is {@code null}.
     *
     * @param collection the collection to check.
     * @return the collection's size or {@code 0} if the collection is {@code null}.
     */
    public static int size(Collection collection) {
        return collection == null ? 0 : collection.size();
    }

    /**
     * Returns the map's size or {@code 0} if the map is {@code null}.
     *
     * @param map the map to check
     * @return the map's size or {@code 0} if the map is {@code null}.
     */
    public static int size(Map map) {
        return map == null ? 0 : map.size();
    }

    /**
     * Return {@code true} if the supplied Map is {@code null}
     * or empty. Otherwise, return {@code false}.
     *
     * @param map the Map to check
     * @return whether the given Map is empty
     */
    public static boolean isEmpty(Map map) {
        return (map == null || map.isEmpty());
    }

    /**
     * Convert the supplied array into a List. A primitive array gets
     * converted into a List of the appropriate wrapper type.
     * <p>A {@code null} source value will be converted to an
     * empty List.
     *
     * @param source the (potentially primitive) array
     * @return the converted List result
     * @see Objects#toObjectArray(Object)
     */
    public static List arrayToList(Object source) {
        return Arrays.asList(Objects.toObjectArray(source));
    }

    /**
     * a new List that contains the specified elements or an empty collection if the elements are null or empty.
     *
     * @param elements the elements to put in the list.
     * @param <T>      the type of elements in the collection
     * @return a new List that contains the specified elements or an empty collection if the elements are null or empty.
     */
    public static <T> List<T> toList(T... elements) {
        if (elements == null || elements.length == 0) {
            return java.util.Collections.emptyList();
        }
        // Avoid integer overflow when a large array is passed in
        int capacity = computeListCapacity(elements.length);
        ArrayList<T> list = new ArrayList<>(capacity);
        java.util.Collections.addAll(list, elements);
        return list;
    }

    /**
     * a new List that contains the specified elements or an empty collection if the elements are null or empty.
     *
     * @param elements the elements to put in the list.
     * @param <T>      the type of elements in the collection
     * @return a new List that contains the specified elements or an empty collection if the elements are null or empty.
     */
    public static <T> List<T> toList(Collection<T> elements) {
        if (elements instanceof List) {
            return (List<T>) elements;
        }
        if (isEmpty(elements)) {
            return java.util.Collections.emptyList();
        }
        // Avoid integer overflow when a large array is passed in
        int capacity = computeListCapacity(elements.size());
        ArrayList<T> list = new ArrayList<>(capacity);
        list.addAll(elements);
        return list;
    }

    /**
     * Returns a new {@link Set} that contains the specified elements or an empty Set if the elements are null or empty.
     *
     * @param elements elements to add to the new set
     * @param <E>      the type of elements in the set
     * @return a new {@link Set} that contains the specified elements or an empty Set if the elements are null or empty.
     */
    public static <E> Set<E> toSet(E... elements) {
        if (elements == null || elements.length == 0) {
            return java.util.Collections.emptySet();
        }
        LinkedHashSet<E> set = new LinkedHashSet<>(elements.length * 4 / 3 + 1);
        java.util.Collections.addAll(set, elements);
        return set;
    }

    //since 1.0
    private static int computeListCapacity(int arraySize) {
        return (int) Math.min(5L + arraySize + (arraySize / 10), Integer.MAX_VALUE);
    }

    /**
     * Merge the given array into the given Collection.
     *
     * @param array      the array to merge (may be {@code null})
     * @param collection the target Collection to merge the array into
     */
    @SuppressWarnings("unchecked")
    public static void mergeArrayIntoCollection(Object array, Collection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        Object[] arr = Objects.toObjectArray(array);
        java.util.Collections.addAll(collection, arr);
    }

    /**
     * Merge the given Properties instance into the given Map,
     * copying all properties (key-value pairs) over.
     * <p>Uses {@code Properties.propertyNames()} to even catch
     * default properties linked into the original Properties instance.
     *
     * @param props the Properties instance to merge (may be {@code null})
     * @param map   the target Map to merge the properties into
     */
    @SuppressWarnings("unchecked")
    public static void mergePropertiesIntoMap(Properties props, Map map) {
        if (map == null) {
            throw new IllegalArgumentException("Map must not be null");
        }
        if (props != null) {
            for (Enumeration en = props.propertyNames(); en.hasMoreElements(); ) {
                String key = (String) en.nextElement();
                Object value = props.getProperty(key);
                if (value == null) {
                    // Potentially a non-String value...
                    value = props.get(key);
                }
                map.put(key, value);
            }
        }
    }


    /**
     * Check whether the given Iterator contains the given element.
     *
     * @param iterator the Iterator to check
     * @param element  the element to look for
     * @return {@code true} if found, {@code false} else
     */
    public static boolean contains(Iterator iterator, Object element) {
        if (iterator != null) {
            while (iterator.hasNext()) {
                Object candidate = iterator.next();
                if (Objects.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check whether the given Enumeration contains the given element.
     *
     * @param enumeration the Enumeration to check
     * @param element     the element to look for
     * @return {@code true} if found, {@code false} else
     */
    public static boolean contains(Enumeration enumeration, Object element) {
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                Object candidate = enumeration.nextElement();
                if (Objects.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check whether the given Collection contains the given element instance.
     * <p>Enforces the given instance to be present, rather than returning
     * {@code true} for an equal element as well.
     *
     * @param collection the Collection to check
     * @param element    the element to look for
     * @return {@code true} if found, {@code false} else
     */
    public static boolean containsInstance(Collection collection, Object element) {
        if (collection != null) {
            for (Object candidate : collection) {
                if (candidate == element) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return {@code true} if any element in '{@code candidates}' is
     * contained in '{@code source}'; otherwise returns {@code false}.
     *
     * @param source     the source Collection
     * @param candidates the candidates to search for
     * @return whether any of the candidates has been found
     */
    public static boolean containsAny(Collection source, Collection candidates) {
        if (isEmpty(source) || isEmpty(candidates)) {
            return false;
        }
        for (Object candidate : candidates) {
            if (source.contains(candidate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the first element in '{@code candidates}' that is contained in
     * '{@code source}'. If no element in '{@code candidates}' is present in
     * '{@code source}' returns {@code null}. Iteration order is
     * {@link Collection} implementation specific.
     *
     * @param source     the source Collection
     * @param candidates the candidates to search for
     * @return the first present object, or {@code null} if not found
     */
    public static Object findFirstMatch(Collection source, Collection candidates) {
        if (isEmpty(source) || isEmpty(candidates)) {
            return null;
        }
        for (Object candidate : candidates) {
            if (source.contains(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * Find a single value of the given type in the given Collection.
     *
     * @param collection the Collection to search
     * @param type       the type to look for
     * @param <T> type type of object to find
     * @return a value of the given type found if there is a clear match,
     * or {@code null} if none or more than one such value found
     */
    @SuppressWarnings("unchecked")
    private static <T> T findValueOfType(Collection<?> collection, Class<T> type) {
        if (isEmpty(collection)) {
            return null;
        }
        T value = null;
        for (Object element : collection) {
            if (type == null || type.isInstance(element)) {
                if (value != null) {
                    // More than one value found... no clear single value.
                    return null;
                }
                value = (T) element;
            }
        }
        return value;
    }

    /**
     * Find the first single value of the given type in the given Collection or {@code null} if none was found
     *
     * @param collection the Collection to search
     * @param type       the type to look for
     * @param <T> type type of object to find
     * @return a value of the given type found or {@code null} if none found.
     */
    @SuppressWarnings("unchecked")
    public static <T> T findFirstValueOfType(Collection<?> collection, Class<T> type) {

        Assert.notNull(type, "type argument cannot be null.");

        if (!isEmpty(collection)) {
            for (Object element : collection) {
                if (element != null && type.isInstance(element)) {
                    return (T) element;
                }
            }
        }

        return null;
    }

    /**
     * Find a single value of one of the given types in the given Collection:
     * searching the Collection for a value of the first type, then
     * searching for a value of the second type, etc.
     *
     * @param collection the collection to search
     * @param types      the types to look for, in prioritized order
     * @return a value of one of the given types found if there is a clear match,
     * or {@code null} if none or more than one such value found
     */
    public static Object findValueOfType(Collection<?> collection, Class<?>[] types) {
        if (isEmpty(collection) || Objects.isEmpty(types)) {
            return null;
        }
        for (Class<?> type : types) {
            Object value = findValueOfType(collection, type);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * Determine whether the given Collection only contains a single unique object.
     *
     * @param collection the Collection to check
     * @return {@code true} if the collection contains a single reference or
     * multiple references to the same instance, {@code false} else
     */
    public static boolean hasUniqueObject(Collection collection) {
        if (isEmpty(collection)) {
            return false;
        }
        boolean hasCandidate = false;
        Object candidate = null;
        for (Object elem : collection) {
            if (!hasCandidate) {
                hasCandidate = true;
                candidate = elem;
            } else if (candidate != elem) {
                return false;
            }
        }
        return true;
    }

    /**
     * Find the common element type of the given Collection, if any.
     *
     * @param collection the Collection to check
     * @return the common element type, or {@code null} if no clear
     * common type has been found (or the collection was empty)
     */
    public static Class<?> findCommonElementType(Collection collection) {
        if (isEmpty(collection)) {
            return null;
        }
        Class<?> candidate = null;
        for (Object val : collection) {
            if (val != null) {
                if (candidate == null) {
                    candidate = val.getClass();
                } else if (candidate != val.getClass()) {
                    return null;
                }
            }
        }
        return candidate;
    }

    /**
     * Marshal the elements from the given enumeration into an array of the given type.
     * Enumeration elements must be assignable to the type of the given array. The array
     * returned will be a different instance than the array given.
     * @param enumeration source enumeration
     * @param array the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @param <A> type of array
     * @param <E> type of enumeration
     * @return an array with the contents of the enumeration
     */
    public static <A, E extends A> A[] toArray(Enumeration<E> enumeration, A[] array) {
        ArrayList<A> elements = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            elements.add(enumeration.nextElement());
        }
        return elements.toArray(array);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Collection<T> c, Class<T> type) {
        T[] array = (T[])Array.newInstance(type, size(c));
        return c.toArray(array);
    }

    /**
     * Adapt an enumeration to an iterator.
     *
     * @param enumeration the enumeration
     * @param <E> type of enumeration
     * @return the iterator
     */
    public static <E> Iterator<E> toIterator(Enumeration<E> enumeration) {
        return new EnumerationIterator<>(enumeration);
    }

    /**
     * Iterator wrapping an Enumeration.
     */
    private static class EnumerationIterator<E> implements Iterator<E> {

        private final Enumeration<E> enumeration;

        EnumerationIterator(Enumeration<E> enumeration) {
            this.enumeration = enumeration;
        }

        public boolean hasNext() {
            return this.enumeration.hasMoreElements();
        }

        public E next() {
            return this.enumeration.nextElement();
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported");
        }
    }
}
