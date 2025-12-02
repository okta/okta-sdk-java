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
package com.okta.sdk.helper;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.okta.commons.lang.Assert;
import com.okta.sdk.resource.client.ApiClient;

/**
 * Helper class for Pagination related functions.
 *
 * @since 16.0.0
 */
public class PaginationUtil {

    private final static Logger log = LoggerFactory.getLogger(PaginationUtil.class);

    /**
     * Gets the 'after' resource id from ApiClient instance.
     * e.g. a next page URL such as  <a href="https://example.okta.com/api/v1/users?after=100uekbsy586JToqdQ1d7&limit=100">...</a> will fetch
     * an output 'after' string value of 100uekbsy586JToqdQ1d7
     *
     * @param apiClient {@link ApiClient} instance
     * @return the 'after' resource id
     * @deprecated Use {@link com.okta.sdk.resource.client.PagedIterable} instead for automatic pagination
     */
    @Deprecated(forRemoval = true, since = "24.1.0")
    public static String getAfter(ApiClient apiClient) {
        return getAfter(getNextPage(apiClient));
    }

    /**
     * Gets the 'after' resource id from the next page URL string.
     * e.g. a next page URL such as  <a href="https://example.okta.com/api/v1/users?after=100uekbsy586JToqdQ1d7&limit=100">...</a> will fetch
     * an output 'after' string value of 100uekbsy586JToqdQ1d7
     *
     * @param nextPage the next page URL string
     * @return the 'after' resource id
     */
    private static String getAfter(String nextPage) {

        String after;
        URL url;

        try {
            url = new URL(nextPage);
            after = splitQuery(url).get("after");
            log.debug("after: {}", after);
            return after;
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * Gets the Next Page URL (to paginate) from ApiClient response header.
     *
     * @param apiClient the {@link ApiClient} instance
     * @return the next page URL string
     */
    @SuppressWarnings("removal")
    private static String getNextPage(ApiClient apiClient) {

        Assert.notNull(apiClient, "apiClient cannot be null");
        Assert.notNull(apiClient.getResponseHeaders(), "apiClient is missing response headers");
        Assert.notNull(apiClient.getResponseHeaders().get("link"), "apiClient is missing 'link' response headers");

        List<String> linkHeaders = apiClient.getResponseHeaders().get("link");

        String nextPage = null;

        for (String linkHeader : linkHeaders) {
            String[] parts = linkHeader.split("; *");
            String url = parts[0]
                .replaceAll("<", "")
                .replaceAll(">", "");
            String rel = parts[1];
            if (rel.equals("rel=\"next\"")) {
                nextPage = url;
            }
        }

        log.debug("Next Page: {}", nextPage);
        return nextPage;
    }

    /**
     * Split a URL with query strings into name value pairs.
     *
     * @param url the url to split
     * @return map of query string name value pairs
     * @throws UnsupportedEncodingException If character encoding needs to be consulted
     */
    private static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {

        Assert.notNull(url, "url cannot be null");

        Map<String, String> query_pairs = new LinkedHashMap<>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int index = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, index), StandardCharsets.UTF_8.name()),
                URLDecoder.decode(pair.substring(index + 1), StandardCharsets.UTF_8.name()));
        }
        return query_pairs;
    }
}
