/*
 * Copyright 2014 Stormpath, Inc.
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
package com.okta.sdk.impl.schema;

import com.okta.sdk.impl.ds.InternalDataStore;
import com.okta.sdk.impl.resource.AbstractInstanceResource;
import com.okta.sdk.impl.resource.CollectionReference;
import com.okta.sdk.impl.resource.Property;
import com.okta.sdk.schema.Field;
import com.okta.sdk.schema.FieldList;
import com.okta.sdk.schema.Schema;

import java.util.Map;

/**
 * @since 1.2.0
 */
public class DefaultSchema extends AbstractInstanceResource implements Schema {

    //COLLECTION RESOURCE REFERENCES:
    static final CollectionReference<FieldList, Field> FIELDS =
            new CollectionReference<>("fields", FieldList.class, Field.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            FIELDS);

    public DefaultSchema(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultSchema(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public FieldList getFields() {
        return getResourceProperty(FIELDS);
    }

}
