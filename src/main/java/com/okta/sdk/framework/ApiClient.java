/*!
 * Copyright (c) 2015-2017, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.okta.sdk.framework;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.exceptions.ApiException;
import com.okta.sdk.exceptions.RateLimitExceededException;
import com.okta.sdk.exceptions.SdkException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ApiClient {

    /**
     * Number of allowed attempts.
     */
    public static final int RETRY_COUNT = 3;

    /**
     * Public static values for the ApiClient.
     */
    public static final String AFTER_CURSOR = "after";
    public static final String LIMIT = "limit";
    public static final String FILTER = "filter";
    public static final String SEARCH = "search";
    public static final String SEARCH_QUERY = "q";

    protected HttpClient httpClient;
    protected String baseUrl;
    protected int apiVersion;
    protected String token;
    protected ApiClientConfiguration configuration;
    private final Logger LOGGER = LoggerFactory.getLogger(ApiClient.class);
    private AtomicReference<HttpResponse> lastHttpResponseRef = new AtomicReference<HttpResponse>();

    /**
     * Constructor for the ApiClient.
     *
     * Bootstraps an HTTPClient to make various requests to the Okta API.
     *
     * @param config {@link ApiClientConfiguration}
     */
    public ApiClient(ApiClientConfiguration config) {
        Proxy proxy = ProxySelector.getDefault().select(URI.create(config.getBaseUrl())).iterator().next();
        HttpRoutePlanner routePlanner;
        if (Proxy.Type.HTTP.equals(proxy.type())) {
            URI proxyUri = URI.create(proxy.type() + "://" + proxy.address());
            HttpHost proxyHost = new HttpHost(proxyUri.getHost(), proxyUri.getPort(), proxyUri.getScheme());
            routePlanner = new DefaultProxyRoutePlanner(proxyHost);
        } else {
            routePlanner = new DefaultRoutePlanner(null);
        }
        StandardHttpRequestRetryHandler requestRetryHandler = new StandardHttpRequestRetryHandler(RETRY_COUNT, true);
        HttpClient client = HttpClientBuilder.create().setRetryHandler(requestRetryHandler)
                .setUserAgent("OktaSDKJava_v" + Utils.getSdkVersion()).disableCookieManagement()
                .setRoutePlanner(routePlanner)
                .build();

        this.httpClient = client;
        this.baseUrl = config.getBaseUrl();
        this.apiVersion = config.getApiVersion();
        this.configuration = config;
        this.token = config.getApiToken();

        initMarshaller();
    }

    /**
     * Abstract methods.
     */
    protected abstract void initMarshaller();

    protected abstract <T> T unmarshall(HttpResponse response, TypeReference<T> clazz) throws IOException;

    protected abstract HttpEntity buildRequestEntity(Object body) throws IOException;

    protected abstract String getFullPath(String relativePath);

    protected abstract void setAcceptHeader(HttpUriRequest httpUriRequest) throws IOException;

    protected abstract void setContentTypeHeader(HttpUriRequest httpUriRequest) throws IOException;

    ////////////////////////////////////////////
    // PUBLIC METHODS
    ////////////////////////////////////////////

    /**
     * Returns the API token from the ApiClient.
     * @return {@link String}
     */
    public String getToken() {
        return this.token;
    }

    /**
     * Sets the API token.
     * @param token {@link String}
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Returns the rate limit context for managing requests with specified limits.
     * Note: There's a limit on the number of api requests in a time frame.
     *
     * @return A way to view the rate limit information
     */
    public RateLimitContext getRateLimitContext() {
        return new RateLimitContext(lastHttpResponseRef.get());
    }

    ////////////////////////////////////////////
    // HTTP METHODS
    ////////////////////////////////////////////

    /**
     * POST HTTP methods for communicating with the API.
     *
     * @param  uri {@link String}                    URI to make request to.
     * @param  httpEntity {@link Object}             HTTP entity object request.
     * @return {@link Map}                           API Response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected Map post(String uri, Object httpEntity) throws IOException {
        return post(uri, httpEntity, new TypeReference<Map>() { });
    }

    /**
     * Generic HTTP GET method.
     *
     * @param  uri {@link String}                    URI to make request to.
     * @param  httpEntity {@link Object}             HTTP entity object request.
     * @param  clazz {@link TypeReference}           Generic type class reference.
     * @param  <T> {@link T}                         Generic type param.
     * @return {@link T}                             Returns a generic type object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected <T> T post(String uri, Object httpEntity, TypeReference<T> clazz) throws IOException {
        return post(uri, buildRequestEntity(httpEntity), clazz);
    }

    /**
     * Generic HTTP POST method.
     *
     * @param  uri {@link String}                    URI to make request to.
     * @param  httpEntity {@link Object}             HTTP entity object request.
     * @param  clazz {@link TypeReference}           Generic type class reference.
     * @param  <T> {@link T}                         Generic type param.
     * @return {@link T}                             Returns a generic type object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected <T> T post(String uri, HttpEntity httpEntity, TypeReference<T> clazz) throws IOException {
        HttpPost httpPost = new HttpPost();
        httpPost.setURI(getAbsoluteURI(uri));
        httpPost.setEntity(httpEntity);
        HttpResponse response = executeRequest(httpPost);
        return unmarshallResponse(clazz, response);
    }

    /**
     * GET HTTP methods for communicating with the API.
     *
     * @param  uri {@link String}                    URI to make request to.
     * @return {@link Map}                           API Response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected Map get(String uri) throws IOException {
        return get(uri, new TypeReference<Map>() { });
    }

    /**
     * HTTP GET method.
     *
     * @param  uri {@link String}                    URI to make request to.
     * @return {@link HttpResponse}                  API HTTP response.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected HttpResponse getHttpResponse(String uri) throws IOException {
        HttpGet httpGet = new HttpGet();
        httpGet.setURI(getAbsoluteURI(uri));
        return executeRequest(httpGet);
    }

    /**
     * Generic HTTP GET method.
     *
     * @param  uri {@link String}                    URI to make request to.
     * @param  clazz {@link TypeReference}           Generic type class reference.
     * @param  <T> {@link T}                         Generic type param.
     * @return {@link T}                             Returns a generic type object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected <T> T get(String uri, TypeReference<T> clazz) throws IOException {
        HttpGet httpGet = new HttpGet();
        httpGet.setURI(getAbsoluteURI(uri));
        HttpResponse response = executeRequest(httpGet);
        return unmarshallResponse(clazz, response);
    }

    /**
     * PUT HTTP methods for communicating with the API.
     *
     * @param  uri {@link String}                    URI to make request to.
     * @throws IOException                           If an input or output exception occurred.

     */
    protected void put(String uri) throws IOException {
        put(uri, "");
    }

    /**
     * Generic HTTP PUT method.
     *
     * @param  uri {@link String}                    URI to make request to.
     * @param  httpEntity {@link Object}             HTTP entity object request.
     * @return {@link Map}                           API Response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected Map put(String uri, Object httpEntity) throws IOException {
        return put(uri, httpEntity, new TypeReference<Map>() { });
    }

    /**
     * Generic HTTP PUT method.
     *
     * @param  uri {@link String}                    URI to make request to.
     * @param  clazz {@link TypeReference}           Generic type class reference.
     * @param  <T> {@link T}                         Generic type param.
     * @return {@link T}                             Returns a generic type object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected <T> T put(String uri, TypeReference<T> clazz) throws IOException {
        return put(uri, "", clazz);
    }

    /**
     * Generic HTTP PUT method.
     *
     * @param  uri {@link String}                    URI to make request to.
     * @param  clazz {@link TypeReference}           Generic type class reference.
     * @param  <T> {@link T}                         Generic type param.
     * @param  httpEntity {@link Object}             HTTP entity object request.
     * @return {@link T}                             Returns a generic type object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected <T> T put(String uri, Object httpEntity, TypeReference<T> clazz) throws IOException {
        return put(uri, buildRequestEntity(httpEntity), clazz);
    }

    /**
     * Generic HTTP PUT method.
     *
     * @param  uri {@link String}                    URI to make request to.
     * @param  clazz {@link TypeReference}           Generic type class reference.
     * @param  <T> {@link T}                         Generic type param.
     * @param  httpEntity {@link Object}             HTTP entity object request.
     * @return {@link T}                             Returns a generic type object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected <T> T put(String uri, HttpEntity httpEntity, TypeReference<T> clazz) throws IOException {
        HttpPut httpPut = new HttpPut();
        httpPut.setURI(getAbsoluteURI(uri));
        httpPut.setEntity(httpEntity);
        HttpResponse response = executeRequest(httpPut);
        return unmarshallResponse(clazz, response);
    }

    /**
     * DELETE HTTP methods for communicating with the API.
     *
     * @param  uri {@link String}                    URI to make request to.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected void delete(String uri) throws IOException {
        HttpDelete httpDelete = new HttpDelete();
        httpDelete.setURI(getAbsoluteURI(uri));
        HttpResponse response = executeRequest(httpDelete);
        unmarshallResponse(new TypeReference<Void>() { }, response);
    }

    /**
     * Generic HTTP DELETE method.
     *
     * @param  uri {@link String}                    URI to make request to.
     * @param  clazz {@link TypeReference}           Generic type class reference.
     * @param  <T> {@link T}                         Generic type param.
     * @return {@link T}                             Returns a generic type object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected <T> T delete(String uri, TypeReference<T> clazz) throws IOException {
        HttpDelete httpDelete = new HttpDelete();
        httpDelete.setURI(getAbsoluteURI(uri));
        HttpResponse response = executeRequest(httpDelete);
        return unmarshallResponse(clazz, response);
    }

    /**
     * Executes a base HTTP request.
     *
     * @param  httpUriRequest {@link HttpUriRequest} Current HTTP URI request object.
     * @return {@link HttpResponse}                  API Response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected HttpResponse executeRequest(HttpUriRequest httpUriRequest) throws IOException {
        logRequest(httpUriRequest);
        setHeaders(httpUriRequest);
        return doExecute(httpUriRequest);
    }

    /**
     * Logs the request method and URI to the console.
     * @param  httpUriRequest {@link HttpUriRequest} Current HTTP URI request object.
     */
    protected void logRequest(HttpUriRequest httpUriRequest) {
        LOGGER.info(String.format("%s %s", httpUriRequest.getMethod(), httpUriRequest.getURI()));
    }

    /**
     * Sets the headers in the HTTP request.
     *
     * @param  httpUriRequest {@link HttpUriRequest} Current HTTP URI request object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected void setHeaders(HttpUriRequest httpUriRequest) throws IOException {
        setTokenHeader(httpUriRequest);

        // Content type and accept header are determined by the child class
        setContentTypeHeader(httpUriRequest);
        setAcceptHeader(httpUriRequest);

        // Walk through custom headers and set
        Map<String, String> headers = this.configuration.getHeaders();
        if (headers != null) {
            // Update headers
            for (Map.Entry<String, String> header : headers.entrySet()) {
                Header extraHeader = new BasicHeader(header.getKey(), header.getValue());
                httpUriRequest.setHeader(extraHeader);
            }
        }
        
    }

    /**
     * Sets the authorization header to Okta's custom SWSS + API_TOKEN format.
     *
     * @param  httpUriRequest {@link HttpUriRequest} Current HTTP URI request object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected void setTokenHeader(HttpUriRequest httpUriRequest) throws IOException {
        Header authHeader = new BasicHeader("Authorization", String.format("SSWS %s", this.token));
        httpUriRequest.setHeader(authHeader);
    }

    /**
     * Executes/fires the request.
     *
     * @param  httpUriRequest {@link HttpUriRequest} Current HTTP URI request object.
     * @return {@link HttpResponse}                  API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected HttpResponse doExecute(HttpUriRequest httpUriRequest) throws IOException {
        HttpResponse response = httpClient.execute(httpUriRequest);
        lastHttpResponseRef.set(response);
        return response;
    }

    

    /**
     * Utility method to encode given String.
     *
     * @param  str {@link String}                    String to be encoded.
     * @return {@link String}                        Encoded string.
     * @throws UnsupportedEncodingException          If character encoding is not supported.
     */
    protected static String encode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, "UTF-8");
    }

    /**
     * Utility method to return an encoded path.
     *
     * @param  formatStr {@link String}              String to be formatted with args.
     * @param  args {@link String}                   Arguments to be encoded into body.
     * @return {@link String}                        Encoded path.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected String getEncodedPath(String formatStr, String ... args) throws IOException {
        return getFullPath(getEncodedString(formatStr, args));
    }

    /**
     * Utility method to return encoded String.
     *
     * @param  formatStr {@link String}              String to be formatted with args.
     * @param  args {@link String}                   Arguments to be encoded into body.
     * @return {@link String}                        Encoded string.
     * @throws UnsupportedEncodingException          If character encoding is not supported.
     */
    private String getEncodedString(String formatStr, String[] args) throws UnsupportedEncodingException {
        final int ARGS_LENGTH = args.length;
        String[] encodedArgs = new String[ARGS_LENGTH];
        for (int i = 0; i < ARGS_LENGTH; i++) {
            encodedArgs[i] = encode(args[i]);
        }
        return String.format(formatStr, encodedArgs);
    }

    /**
     * Utility method to encode path with query values.
     *
     * @param  formatStr {@link String}              String to be formatted with args.
     * @param  params {@link Map}                    Body params to be encoded.
     * @param  args {@link String}                   Arguments to be encoded into body.
     * @return {@link String}                        Encoded string.
     * @throws UnsupportedEncodingException          If character encoding is not supported.
     */
    protected String getEncodedPathWithQueryParams(String formatStr, Map<String, String> params, String... args) throws UnsupportedEncodingException {
        String uri = getEncodedString(formatStr, args);
        for (String key : params.keySet()) {
            uri = addParameterToUri(uri, key, params.get(key));
        }
        return getFullPath(uri);
    }

    /**
     * Utility method to add parameter to URI.
     *
     * @param  uri {@link String}                    URI to make request to.
     * @param  name {@link String}                   Name of parameter to add.
     * @param  value {@link String}                  Value of parameter to add.
     * @return {@link String}                        Encoded string.
     * @throws UnsupportedEncodingException          If character encoding is not supported.
     */
    private String addParameterToUri(String uri, String name, String value) throws UnsupportedEncodingException {
        int queryStringStart = uri.indexOf('?');
        if (queryStringStart == -1) {
            return uri + "?" + encode(name) + "=" + encode(value);
        } else if (queryStringStart == uri.length() - 1) {
            return uri + encode(name) + "=" + encode(value);
        } else {
            return uri + "&" + encode(name) + "=" + encode(value);
        }
    }

    /**
     * Utility method to return absolute URI from relative URI.
     *
     * @param  relativeURI {@link String}            Current long URI.
     * @return {@link URI}                           Absolute URI.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected URI getAbsoluteURI(String relativeURI) throws IOException {
        try {
            if(relativeURI.startsWith("http:") || relativeURI.startsWith("https:")) {
                return new URI(relativeURI);
            }
            else {
                return new URL(String.format("%s%s", this.baseUrl, relativeURI)).toURI();
            }
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    /**
     * Utility method to unmarshall HTTP response if there is content.
     * Returns "null" if API response body was empty.
     *
     * @param  <T> {@link T}                         Generic type param.
     * @param  clazz {@link TypeReference}           Generic type class reference.
     * @param  httpResponse {@link HttpResponse}     Response object.
     * @return {@link T}                             Returns a generic type object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected <T> T unmarshallResponse(TypeReference<T> clazz, HttpResponse httpResponse) throws IOException {
        boolean contentReturned = checkResponse(httpResponse);
        if (contentReturned) {
            return unmarshall(httpResponse, clazz);
        } else {
            return null;
        }
    }

    /**
     * Utility method to check the formatting of the HttpResponse received.
     *
     * @param  response {@link HttpResponse}         Response object.
     * @return {@link Boolean}                       If request was successful.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected boolean checkResponse(HttpResponse response) throws IOException {
        if(response == null) {
            throw new SdkException("A response wasn't received");
        }

        StatusLine statusLine = response.getStatusLine();
        if(statusLine == null) {
            throw new SdkException("A statusLine wasn't present in the response");
        }

        int statusCode = statusLine.getStatusCode();

        if ((statusCode == 200 || statusCode == 201) && response.getEntity() != null) {
            // success; content returned
            return true;
        } else if (statusCode == 204 && response.getEntity() == null) {
            // success; no content returned
            return false;
        } else {
            extractError(response);
            LOGGER.info("We found an api exception and didn't throw an error");
            return false; // will never hit this line
        }
    }

    /**
     * Utility method to extract the error from the HTTP response.
     *
     * @param  response {@link HttpResponse}         Response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected void extractError(HttpResponse response) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        ErrorResponse errors = unmarshall(response, new TypeReference<ErrorResponse>() { });
        if (statusCode == 429) {
            throw new RateLimitExceededException(errors);
        } else {
            throw new ApiException(statusCode, errors);
        }
    }
}
