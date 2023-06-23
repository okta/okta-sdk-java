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
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PagedList<T> extends ArrayList<T> {

    private String self;
    private String nextPage;

    private String after;

    public PagedList() { }

    public PagedList(List<T> items, String self, String nextPage, String after) {
        super(items);
        this.self = self;
        this.nextPage = nextPage;
        this.after = after;
    }

    public String getSelf() {
        return self;
    }

    public String getNextPage() {
        return nextPage;
    }

    public String getAfter() {
        URL url;
        try {
            url = new URL(nextPage);
            after = splitQuery(url).get("after");
            return after;
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            return null;
        }
    }

    public static <T> T constructPagedList(HttpResponse response, T value) {
        Assert.notNull(response);
        Assert.isTrue(value instanceof List);
        Header[] linkHeaders = response.getHeaders("link");
        if (linkHeaders == null || linkHeaders.length == 0) {
            return value;
        }
        String nextPage = null;
        String self = null;
        for (Header link : linkHeaders) {
            String[] parts = link.getValue().split("; *");
            String url = parts[0]
                .replaceAll("<", "")
                .replaceAll(">", "");
            String rel = parts[1];
            if (rel.equals("rel=\"next\"")) {
                nextPage = url;
            } else if (rel.equals("rel=\"self\"")) {
                self = url;
            }
        }
        if (nextPage == null && self == null) {
            return value;
        }
        return (T) new PagedList((List) value, self, nextPage, null);
    }

    /**
     * Split a URL with query strings into name value pairs.
     *
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

    public boolean hasMoreItems() {
        return getAfter() != null;
    }
}
