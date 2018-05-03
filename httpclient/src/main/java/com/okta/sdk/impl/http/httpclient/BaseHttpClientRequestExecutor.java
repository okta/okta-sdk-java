package com.okta.sdk.impl.http.httpclient;

import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.client.AuthenticationScheme;
import com.okta.sdk.client.Proxy;
import com.okta.sdk.impl.http.HttpHeaders;
import com.okta.sdk.impl.http.MediaType;
import com.okta.sdk.impl.http.QueryString;
import com.okta.sdk.impl.http.Request;
import com.okta.sdk.impl.http.RequestExecutor;
import com.okta.sdk.impl.http.Response;
import com.okta.sdk.impl.http.RestException;
import com.okta.sdk.impl.http.authc.DefaultRequestAuthenticatorFactory;
import com.okta.sdk.impl.http.authc.RequestAuthenticator;
import com.okta.sdk.impl.http.authc.RequestAuthenticatorFactory;
import com.okta.sdk.impl.http.support.DefaultRequest;
import com.okta.sdk.impl.http.support.DefaultResponse;
import com.okta.sdk.lang.Assert;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

abstract class BaseHttpClientRequestExecutor implements RequestExecutor {

    private static final Logger log = LoggerFactory.getLogger(BaseHttpClientRequestExecutor.class);

    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = Integer.MAX_VALUE/2;
    private static final String MAX_CONNECTIONS_PER_ROUTE_PROPERTY_KEY = "com.okta.sdk.impl.http.httpclient.HttpClientRequestExecutor.connPoolControl.maxPerRoute";
    private static final int MAX_CONNECTIONS_PER_ROUTE;

    private static final int DEFAULT_MAX_CONNECTIONS_TOTAL = Integer.MAX_VALUE;
    private static final String MAX_CONNECTIONS_TOTAL_PROPERTY_KEY = "com.okta.sdk.impl.http.httpclient.HttpClientRequestExecutor.connPoolControl.maxTotal";
    private static final int MAX_CONNECTIONS_TOTAL;

    private final RequestAuthenticator requestAuthenticator;

    private HttpClient httpClient;

    private HttpClientRequestFactory httpClientRequestFactory;

    static {
        int connectionMaxPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE;
        String connectionMaxPerRouteString = System.getProperty(MAX_CONNECTIONS_PER_ROUTE_PROPERTY_KEY);
        if (connectionMaxPerRouteString != null) {
            try {
                connectionMaxPerRoute = Integer.parseInt(connectionMaxPerRouteString);
            } catch (NumberFormatException nfe) {
                log.warn(
                    "Bad max connection per route value: {}. Using default: {}.",
                    connectionMaxPerRouteString, DEFAULT_MAX_CONNECTIONS_PER_ROUTE, nfe
                );
            }
        }
        MAX_CONNECTIONS_PER_ROUTE = connectionMaxPerRoute;

        int connectionMaxTotal = DEFAULT_MAX_CONNECTIONS_TOTAL;
        String connectionMaxTotalString = System.getProperty(MAX_CONNECTIONS_TOTAL_PROPERTY_KEY);
        if (connectionMaxTotalString != null) {
            try {
                connectionMaxTotal = Integer.parseInt(connectionMaxTotalString);
            } catch (NumberFormatException nfe) {
                log.warn(
                    "Bad max connection total value: {}. Using default: {}.",
                    connectionMaxTotalString, DEFAULT_MAX_CONNECTIONS_TOTAL, nfe
                );
            }
        }
        MAX_CONNECTIONS_TOTAL = connectionMaxTotal;
    }

    /**
     * Creates a new {@code HttpClientRequestExecutor} using the specified {@code ClientCredentials} and optional {@code Proxy}
     * configuration.
     * @param clientCredentials the Okta account API Key that will be used to authenticate the client with Okta's API sever
     * @param proxy the HTTP proxy to be used when communicating with the Okta API server (can be null)
     * @param authenticationScheme the HTTP authentication scheme to be used when communicating with the Okta API server.
     *                             If null, then SSWS will be used.
     */
    BaseHttpClientRequestExecutor(ClientCredentials clientCredentials, Proxy proxy, AuthenticationScheme authenticationScheme, RequestAuthenticatorFactory requestAuthenticatorFactory, Integer connectionTimeout) {
        Assert.notNull(clientCredentials, "clientCredentials argument is required.");
        Assert.isTrue(connectionTimeout >= 0, "Timeout cannot be a negative number.");

        RequestAuthenticatorFactory factory = (requestAuthenticatorFactory != null)
                ? requestAuthenticatorFactory
                : new DefaultRequestAuthenticatorFactory();

        this.requestAuthenticator = factory.create(authenticationScheme, clientCredentials);

        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();

        if (MAX_CONNECTIONS_TOTAL >= MAX_CONNECTIONS_PER_ROUTE) {
            connMgr.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
            connMgr.setMaxTotal(MAX_CONNECTIONS_TOTAL);
        } else {
            connMgr.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
            connMgr.setMaxTotal(DEFAULT_MAX_CONNECTIONS_TOTAL);

            log.warn(
                "{} ({}) is less than {} ({}). " +
                "Reverting to defaults: connectionMaxTotal ({}) and connectionMaxPerRoute ({}).",
                MAX_CONNECTIONS_TOTAL_PROPERTY_KEY, MAX_CONNECTIONS_TOTAL,
                MAX_CONNECTIONS_PER_ROUTE_PROPERTY_KEY, MAX_CONNECTIONS_PER_ROUTE,
                DEFAULT_MAX_CONNECTIONS_TOTAL, DEFAULT_MAX_CONNECTIONS_PER_ROUTE
            );
        }

        // The connectionTimeout value is specified in seconds in Okta configuration settings.
        // Therefore, multiply it by 1000 to be milliseconds since RequestConfig expects milliseconds.
        int connectionTimeoutAsMilliseconds = connectionTimeout * 1000;

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectionTimeoutAsMilliseconds)
                .setSocketTimeout(connectionTimeoutAsMilliseconds).setRedirectsEnabled(false).build();

        ConnectionConfig connectionConfig = ConnectionConfig.custom().setCharset(Consts.UTF_8).build();

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig)
                .disableCookieManagement().setDefaultConnectionConfig(connectionConfig).setConnectionManager(connMgr);

        this.httpClientRequestFactory = new HttpClientRequestFactory(requestConfig);

        if (proxy != null) {
            //We have some proxy setting to use!
            HttpHost httpProxyHost = new HttpHost(proxy.getHost(), proxy.getPort());
            httpClientBuilder.setProxy(httpProxyHost);

            if (proxy.isAuthenticationRequired()) {
                AuthScope authScope = new AuthScope(proxy.getHost(), proxy.getPort());
                Credentials credentials = new UsernamePasswordCredentials(proxy.getUsername(), proxy.getPassword());
                CredentialsProvider credentialsProviderProvider = new BasicCredentialsProvider();
                credentialsProviderProvider.setCredentials(authScope, credentials);
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProviderProvider);
            }
        }

        this.httpClient = httpClientBuilder.build();
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Response executeRequest(Request request) throws RestException {

        Assert.notNull(request, "Request argument cannot be null.");

        int retryCount = 0;
        URI redirectUri = null;
        HttpEntity entity = null;
        RestException exception = null;

        // Make a copy of the original request params and headers so that we can
        // permute them in the loop and start over with the original every time.
        QueryString originalQuery = new QueryString();
        originalQuery.putAll(request.getQueryString());

        HttpHeaders originalHeaders = new HttpHeaders();
        originalHeaders.putAll(request.getHeaders());

        while (true) {

            if (redirectUri != null) {
                request = new DefaultRequest(
                        request.getMethod(),
                        redirectUri.toString(),
                        null,
                        null,
                        request.getBody(),
                        request.getHeaders().getContentLength()
                );
            }

            if (retryCount > 0) {
                request.setQueryString(originalQuery);
                request.setHeaders(originalHeaders);
            }


            // Sign the request
            this.requestAuthenticator.authenticate(request);

            HttpRequestBase httpRequest = this.httpClientRequestFactory.createHttpClientRequest(request, entity);

            if (httpRequest instanceof HttpEntityEnclosingRequest) {
                entity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();
            }


            HttpResponse httpResponse = null;
            try {
                // We don't want to treat a redirect like a retry,
                // so if redirectUri is not null, we won't pause
                // before executing the request below.
                if (retryCount > 0 && redirectUri == null) {
                    delay(exception, retryCount);
                    if (entity != null) {
                        InputStream content = entity.getContent();
                        if (content.markSupported()) {
                            content.reset();
                        }
                    }
                }

                // reset redirectUri so that if there is an exception, we will pause on retry
                redirectUri = null;
                exception = null;
                retryCount++;

                httpResponse = httpClient.execute(httpRequest);

                if (isRedirect(httpResponse)) {
                    Header[] locationHeaders = httpResponse.getHeaders("Location");
                    String location = locationHeaders[0].getValue();
                    log.debug("Redirecting to: {}", location);
                    redirectUri = URI.create(location);
                    httpRequest.setURI(redirectUri);
                } else {

//                    int httpStatus = response.getHttpStatus();

//                    if (httpStatus == 429) {
//                        throw new RestException("HTTP 429: Too Many Requests.  Exceeded request rate limit in the allotted amount of time.");
//                    }
                    if (shouldRetry(httpRequest, httpResponse, null, retryCount)) {
                        //allow the loop to continue to execute a retry request
                        continue;
                    }

                    Response response = toSdkResponse(httpResponse);
                    return response;
                }
            } catch (Throwable t) {
                log.warn("Unable to execute HTTP request: ", t.getMessage(), t);

                if (t instanceof RestException) {
                    exception = (RestException)t;
                }

                if (!shouldRetry(httpRequest, httpResponse, t, retryCount)) {
                    throw new RestException("Unable to execute HTTP request: " + t.getMessage(), t);
                }
            } finally {
                try {
                    httpResponse.getEntity().getContent().close();
                } catch (Throwable ignored) { // NOPMD
                }
            }
        }
    }

    /**
     * Returns {@code true} if the exception resulted from a throttling error, {@code false} otherwise.
     *
     * @param re The exception to test.
     * @return {@code true} if the exception resulted from a throttling error, {@code false} otherwise.
     */
    private boolean isThrottlingException(RestException re) {
        String msg = re.getMessage();
        return msg != null && msg.contains("HTTP 429");
    }

    protected byte[] toBytes(HttpEntity entity) throws IOException {
        return EntityUtils.toByteArray(entity);
    }

    protected Response toSdkResponse(HttpResponse httpResponse) throws IOException {

        int httpStatus = httpResponse.getStatusLine().getStatusCode();

        HttpHeaders headers = getHeaders(httpResponse);
        MediaType mediaType = headers.getContentType();

        HttpEntity entity = getHttpEntity(httpResponse);

        InputStream body = entity != null ? entity.getContent() : null;
        long contentLength = entity != null ? entity.getContentLength() : -1;

        //ensure that the content has been fully acquired before closing the http stream
        if (body != null) {
            byte[] bytes = toBytes(entity);

            if(bytes != null) {
                body = new ByteArrayInputStream(bytes);
            }  else {
                body = null;
            }
        }

        Response response = new DefaultResponse(httpStatus, mediaType, body, contentLength);

        response.getHeaders().add(HttpHeaders.OKTA_REQUEST_ID, headers.getOktaRequestId());
        response.getHeaders().put(HttpHeaders.LINK, headers.getLinkHeaders());

        return response;
    }

    private HttpEntity getHttpEntity(HttpResponse response) {

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            Header contentEncodingHeader = entity.getContentEncoding();
            if (contentEncodingHeader != null) {
                for (HeaderElement element : contentEncodingHeader.getElements()) {
                    if (element.getName().equalsIgnoreCase("gzip")) {
                        return new GzipDecompressingEntity(response.getEntity());
                    }
                }
            }
        }
        return entity;
    }

    private HttpHeaders getHeaders(HttpResponse response) {

        HttpHeaders headers = new HttpHeaders();

        Header[] httpHeaders = response.getAllHeaders();

        if (httpHeaders != null) {
            for (Header httpHeader : httpHeaders) {
                headers.add(httpHeader.getName(), httpHeader.getValue());
            }
        }

        return headers;
    }

    private boolean isRedirect(HttpResponse response) {
        int status = response.getStatusLine().getStatusCode();
        return (status == HttpStatus.SC_MOVED_PERMANENTLY ||
                status == HttpStatus.SC_MOVED_TEMPORARILY ||
                status == HttpStatus.SC_TEMPORARY_REDIRECT) &&
                response.getHeaders("Location") != null &&
                response.getHeaders("Location").length > 0;
    }

    abstract boolean shouldRetry(HttpRequestBase method, HttpResponse response, Throwable t, int retries);

    abstract void delay(Throwable t, int retries);
}