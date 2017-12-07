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

import com.okta.sdk.impl.ds.InternalDataStore;

import java.util.Map;

public class StubResource extends AbstractResource {

    public final BooleanProperty booleanProp = new BooleanProperty("booleanPropKey");
    public final  DateProperty dateProp = new DateProperty("datePropKey");
    public final  EnumListProperty<StubEnum> enumListProp = new EnumListProperty<>("enumListPropKey", StubEnum.class);
    public final  EnumProperty<StubEnum> enumProp = new EnumProperty<>("enumPropKey", StubEnum.class);
    public final  IntegerProperty integerProp = new IntegerProperty("intPropKey");
    public final  ListProperty listProp = new ListProperty("listPropKey");
    public final  MapProperty mapProp = new MapProperty("mapPropKey");
    public final  ResourceListProperty<NestedStubResource> resourceListProp = new ResourceListProperty<>("resourceListPropKey", NestedStubResource.class);
    public final  StringProperty stringProp = new StringProperty("stringPropKey");

    private final Map<String, Property> propertyDescriptors = AbstractResource.createPropertyDescriptorMap(booleanProp, dateProp, enumListProp, enumProp, integerProp, listProp, mapProp, resourceListProp, stringProp);

    protected StubResource(InternalDataStore dataStore) {
        super(dataStore);
    }

    protected StubResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return propertyDescriptors;
    }
}
