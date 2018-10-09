/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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
package com.okta.sdk.impl.http.okhttp;

import com.okta.sdk.client.Proxy;
import com.okta.sdk.http.HttpMethod;
import com.okta.sdk.impl.config.ClientConfiguration;
import com.okta.sdk.impl.http.HttpHeaders;
import com.okta.sdk.impl.http.MediaType;
import com.okta.sdk.impl.http.Request;
import com.okta.sdk.impl.http.RequestExecutor;
import com.okta.sdk.impl.http.Response;
import com.okta.sdk.impl.http.RestException;
import com.okta.sdk.impl.http.authc.DefaultRequestAuthenticatorFactory;
import com.okta.sdk.impl.http.authc.RequestAuthenticator;
import com.okta.sdk.impl.http.authc.RequestAuthenticatorFactory;
import com.okta.sdk.impl.http.support.DefaultResponse;
import okhttp3.CookieJar;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 */
public class OkHttpRequestExecutor implements RequestExecutor {

    private final OkHttpClient client;

    private final RequestAuthenticator requestAuthenticator;

    public OkHttpRequestExecutor(ClientConfiguration clientConfiguration) {
        this(clientConfiguration, createOkHttpClient(clientConfiguration));
    }

    // exposed for testing
    OkHttpRequestExecutor(ClientConfiguration clientConfiguration, OkHttpClient okHttpClient) {

        this.client = okHttpClient;

        RequestAuthenticatorFactory factory = (clientConfiguration.getRequestAuthenticatorFactory() != null)
                ? clientConfiguration.getRequestAuthenticatorFactory()
                : new DefaultRequestAuthenticatorFactory();

        this.requestAuthenticator = factory.create(clientConfiguration.getAuthenticationScheme(),
                                                   clientConfiguration.getClientCredentialsResolver().getClientCredentials());
    }

    private static OkHttpClient createOkHttpClient(ClientConfiguration clientConfiguration) {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        clientBuilder.connectTimeout(clientConfiguration.getConnectionTimeout(), TimeUnit.SECONDS);
        clientBuilder.readTimeout(clientConfiguration.getConnectionTimeout(), TimeUnit.SECONDS);
        clientBuilder.writeTimeout(clientConfiguration.getConnectionTimeout(), TimeUnit.SECONDS);

        clientBuilder.cookieJar(CookieJar.NO_COOKIES);
        clientBuilder.retryOnConnectionFailure(false); // handled by SDK

        final Proxy sdkProxy = clientConfiguration.getProxy();
        if (sdkProxy != null) {
            java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(sdkProxy.getHost(), sdkProxy.getPort()));

            clientBuilder.proxy(proxy);
            if (sdkProxy.isAuthenticationRequired()) {
                clientBuilder.proxyAuthenticator((route, response) -> {
                    String credential = Credentials.basic(sdkProxy.getUsername(), sdkProxy.getPassword());
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                });
            }
        }
        return clientBuilder.build();
    }

    @Override
    public Response executeRequest(Request request) throws RestException {

        // Sign the request
        this.requestAuthenticator.authenticate(request);

        HttpUrl.Builder urlBuilder = HttpUrl.get(request.getResourceUrl()).newBuilder();

        // query parms
        request.getQueryString().forEach(urlBuilder::addQueryParameter);

        okhttp3.Request.Builder okRequestBuilder = new okhttp3.Request.Builder()
                             .url(urlBuilder.build());

        // headers
        request.getHeaders().toSingleValueMap().forEach(okRequestBuilder::addHeader);

        HttpMethod method = request.getMethod();
        switch (method) {
            case DELETE:
                okRequestBuilder.delete();
                break;
            case GET:
                okRequestBuilder.get();
                break;
            case HEAD:
                okRequestBuilder.head();
                break;
            case POST:
                okRequestBuilder.post(new InputStreamRequestBody(request.getBody(), request.getHeaders().getContentType()));
                break;
            case PUT:
                // TODO support 100-continue ?
                okRequestBuilder.put(new InputStreamRequestBody(request.getBody(), request.getHeaders().getContentType()));
                break;
            default:
                throw new IllegalArgumentException("Unrecognized HttpMethod: " + method);
        }

        try {
            okhttp3.Response okResponse = client.newCall(okRequestBuilder.build()).execute();
            return toSdkResponse(okResponse);

        } catch (IOException e) {
            throw new RestException(e.getMessage(), e);
        }
    }

    private Response toSdkResponse(okhttp3.Response okResponse) throws IOException {

        int httpStatus = okResponse.code();

        HttpHeaders headers = new HttpHeaders();
        headers.putAll(okResponse.headers().toMultimap());

        MediaType mediaType = headers.getContentType();

        ResponseBody body = okResponse.body();
        InputStream bodyInputStream = null;
        long contentLength = -1;

        //ensure that the content has been fully acquired before closing the http stream
        if (body != null) {
            contentLength = body.contentLength();
            byte[] bytes = body.bytes();

            if(bytes != null) {
                bodyInputStream = new ByteArrayInputStream(bytes);
            }
        }

        Response response = new DefaultResponse(httpStatus, mediaType, bodyInputStream, contentLength);
        response.getHeaders().putAll(headers);

        return response;
    }

    private static class InputStreamRequestBody extends RequestBody {

        private final InputStream inputStream;

        private final okhttp3.MediaType okContentType;

        private InputStreamRequestBody(InputStream inputStream, MediaType contentType) {
            this.inputStream = inputStream;
            this.okContentType = okhttp3.MediaType.parse(contentType.toString());
        }

        @Override
        public okhttp3.MediaType contentType() {
            return okContentType;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            Source source = null;
            try {
                source = Okio.source(inputStream);
                sink.writeAll(source);
            } finally {
                Util.closeQuietly(source);
            }
        }
    }
}