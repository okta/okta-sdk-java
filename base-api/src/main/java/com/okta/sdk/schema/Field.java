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
package com.okta.sdk.schema;

import com.okta.sdk.resource.Deletable;
import com.okta.sdk.resource.Resource;
import com.okta.sdk.resource.Saveable;

/**
 * A Field represents the configuration of an editable {@link Resource} field, it determines if its required or not.
 *
 * @since 1.0.0
 */
public interface Field extends Resource, Saveable, Deletable {

    /**
     * Returns the name of the field
     *
     * @return the field's name
     */
    String getName();

    /**
     * Returns true if the given field is required or false otherwise
     *
     * @return the field's required value
     */
    boolean isRequired();

    /**
     * Sets the field as required or not
     * <p>After calling this method, you must call {@link #save()} to propagate the change to the Okta
     * servers.</p>
     *
     * @param required true if the field is required, false otherwise
     * @return the Field itself
     */
    Field setRequired(boolean required);

    /**
     * Returns the schema this field belongs to.
     *
     * @return the parent {@link Schema}
     */
    Schema getSchema();

}
