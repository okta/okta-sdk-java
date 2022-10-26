/*
 * Copyright (c) 2022-Present, Okta, Inc.
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
package com.okta.sdk.resource.common;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PagedList<T> {

    private List<T> items;
    private String self;
    private String nextPage;

    public List<T> getItems() {
        return items;
    }

    public String getSelf() {
        return self;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public void addItems(List<T> itemsToAdd) {
        this.items = (List<T>) flatten(itemsToAdd);
    }

    public List<T> items() {
        return getItems();
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

    private List<?> flatten(List<?> list) {
        return list.stream()
            .flatMap(e -> e instanceof List ? flatten((List) e).stream() : Stream.of(e))
            .collect(Collectors.toList());
    }
}
