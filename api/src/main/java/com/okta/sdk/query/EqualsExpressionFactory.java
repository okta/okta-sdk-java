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
package com.okta.sdk.query;

/**
 * An {@code EqualsExpressionFactory} creates {@code equals} {@link Criterion} for specific resource properties.
 *
 * @since 1.0.0
 */
public interface EqualsExpressionFactory {

    /**
     * Returns a new equals expression reflecting a resource property name and the specified value.
     *
     * @param value the value that should equal the property value.
     * @return a new equals expression reflecting the property name and the specified value.
     */
    Criterion eq(Object value);
}
