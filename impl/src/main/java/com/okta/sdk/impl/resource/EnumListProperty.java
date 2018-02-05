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

/**
 * @since 1.0
 */
public class EnumListProperty<T> extends Property<T> {

    public EnumListProperty(String name, Class<T> type) {
        super(name, type);
        Assert.isTrue(Enum.class.isAssignableFrom(type), "List type must be an Enum.");
    }
}