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

import com.okta.commons.lang.Assert;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public String getAfter(String nextPageUrl) {
        URL url;
        try {
            url = new URL(nextPageUrl);
            return splitQuery(url).get("after");
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            return null;
        }
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

    public static <T> PagedList<T> constructPagedList(final ResponseEntity<List<T>> responseEntity) {
        PagedList<T> pagedList = new PagedList<>();
        Assert.notNull(responseEntity);
        pagedList.addItems(responseEntity.getBody());
        List<String> linkHeaders = responseEntity.getHeaders().get("link");
        Assert.notNull(linkHeaders);
        for (String link : linkHeaders) {
            String[] parts = link.split("; *");
            String url = parts[0]
                .replaceAll("<", "")
                .replaceAll(">", "");
            String rel = parts[1];
            if (rel.equals("rel=\"next\"")) {
                pagedList.setNextPage(url);
            } else if (rel.equals("rel=\"self\"")) {
                pagedList.setSelf(url);
            }
        }
        return pagedList;
    }

    /**
     * Split a URL with query strings into name value pairs.
     * @param url the url to split
     * @return map of query string name value pairs
     */
    private static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int index = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, index), "UTF-8"), URLDecoder.decode(pair.substring(index + 1), "UTF-8"));
        }
        return query_pairs;
    }
}
