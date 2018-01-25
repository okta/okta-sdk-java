/*
 * Copyright 2018 Okta, Inc.
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

import com.okta.sdk.resource.Resource;

import java.util.Map;

/**
 * Resolves an HREF for a given {@link Resource} class and data. This class allows for a plugable way to resolve a
 * Resource's HREF. Okta uses HAL style links, but other implementations could be used to resolve or augment the HREF.
 *
 * @since 0.11.0
 */
public interface ResourceHrefResolver {

    <R extends Resource> String resolveHref(Map<String, ?> properties, Class<R> clazz);
}
