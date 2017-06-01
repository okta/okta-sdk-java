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
package com.okta.sdk.impl.query;

import com.okta.sdk.impl.resource.CollectionReference;
import com.okta.sdk.impl.resource.ResourceReference;
import com.okta.sdk.lang.Assert;
import com.okta.sdk.query.Options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 0.8
 */
public class DefaultOptions<T extends Options> implements Options, Expandable {

    protected final List<Expansion> expansions;

    protected DefaultOptions() {
        this.expansions = new ArrayList<Expansion>();
    }

    protected T expand(ResourceReference property) {
        Assert.notNull(property, "property argument cannot be null.");
        this.expansions.add(new Expansion(property.getName()));
        return (T) this;
    }

    protected T expand(CollectionReference property, int limit) {
        return expand(property, limit, Pagination.DEFAULT_OFFSET.getValue());
    }

    protected T expand(CollectionReference property, int limit, int offset) {
        Assert.notNull(property, "property argument cannot be null.");
        int sLimit = Pagination.sanitizeLimit(limit);
        int sOffset = Pagination.sanitizeOffset(offset);
        this.expansions.add(new CollectionExpansion(property.getName(), sLimit, sOffset));
        return (T) this;
    }

    public List<Expansion> getExpansions() {
        return Collections.unmodifiableList(this.expansions);
    }

    public boolean isEmpty() {
        return this.expansions.isEmpty();
    }
}
