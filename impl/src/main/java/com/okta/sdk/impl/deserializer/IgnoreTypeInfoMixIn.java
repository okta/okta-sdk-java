/*
 * Copyright 2024-Present Okta, Inc.
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
package com.okta.sdk.impl.deserializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Mix-in class to disable @JsonTypeInfo annotations on generated model classes.
 * 
 * Some OpenAPI-generated classes have @JsonTypeInfo annotations with @JsonSubTypes
 * that reference classes which don't properly extend the parent class. This causes
 * Jackson deserialization errors like "not a subtype of X".
 * 
 * By applying this mix-in to those classes and using custom deserializers, we can
 * bypass the problematic polymorphic type handling.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public abstract class IgnoreTypeInfoMixIn {
}
