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
package com.okta.sdk.impl.resource;

import com.okta.sdk.lang.Assert;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Converts Strings to enum's based on the {code}.toString(){code} or {code}name{code} of the enum.  Reverse lookup maps
 * from value-to-enum will be cached forever.
 *
 * @since 0.8.1
 */
final class EnumConverter {

    private final Map<Class<Enum>, Map<String, Enum>> cachedEnums = new ConcurrentHashMap<>();

    /**
     * Lookup enum by {code}toString(){code} or {code}name{code}. If the enum cannot be found based on the
     * {code}toString(){code} value then {code}Enum.valueOf{code} is used.
     *
     * @param <E> The enum type whose constant is to be returned
     * @param enumType the {@code Class} object of the enum type from which to return a constant
     * @param value the toString() or name value of the enum to return
     * @return the enum constant of the specified enum type with the specified value
     */
    <E extends Enum<E>> E fromValue(Class<E> enumType, String value) {

        Assert.notNull(value, "[Assertion failed] - 'value' is required; it must not be null");

        // Get the reverse enum map
        Map<String, E> reverseMap = getReverseLookupMap(enumType);

        // lookup the value
        E result = reverseMap.get(value);

        // if null, then revert to Enum.valueOf
        if (result == null) {
            result = Enum.valueOf(enumType, value);
        }
        return result;
    }

    private <E extends Enum> Map<String, E> getReverseLookupMap(Class<E> type) {

        Map<String, E> result = (Map<String, E>) cachedEnums.get(type);
        if (result == null) {
            result = Arrays.stream(type.getEnumConstants())
                .collect(Collectors.toMap(Enum::toString, e -> e));
            cachedEnums.put((Class<Enum>) type, (Map<String, Enum>) result);
        }
        return result;
    }
}