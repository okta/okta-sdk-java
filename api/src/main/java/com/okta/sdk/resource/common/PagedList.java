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
