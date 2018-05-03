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
package com.okta.sdk.impl.http.httpclient;

import com.okta.sdk.client.AuthenticationScheme;
import com.okta.sdk.client.Proxy;
import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.impl.http.RestException;
import com.okta.sdk.impl.http.authc.RequestAuthenticatorFactory;
import com.okta.sdk.impl.http.support.BackoffStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * {@code RequestExecutor} implementation that uses the
 * <a href="http://hc.apache.org/httpcomponents-client-ga">Apache HttpClient</a> implementation to
 * execute http requests.
 *
 * @since 0.5.0
 */
public class HttpClientRequestExecutor extends BaseHttpClientRequestExecutor {

    private static final Logger log = LoggerFactory.getLogger(HttpClientRequestExecutor.class);

    private static final int DEFAULT_MAX_RETRIES = 4;

    /**
     * Maximum exponential back-off time before retrying a request
     */
    private static final int MAX_BACKOFF_IN_MILLISECONDS = 20 * 1000;

    private int numRetries = DEFAULT_MAX_RETRIES;

    private BackoffStrategy backoffStrategy;

    private final Set<Integer> retryStatusCodes = new HashSet<>(Arrays.asList(503, 504, 429));

    //doesn't need to be SecureRandom: only used in backoff strategy, not for crypto:
    private final Random random = new Random();

    public HttpClientRequestExecutor(ClientCredentials clientCredentials, Proxy proxy, AuthenticationScheme authenticationScheme, RequestAuthenticatorFactory requestAuthenticatorFactory, Integer connectionTimeout) {
        super(clientCredentials, proxy, authenticationScheme, requestAuthenticatorFactory, connectionTimeout);
    }

    public int getNumRetries() {
        return numRetries;
    }

    public void setNumRetries(int numRetries) {
        this.numRetries = numRetries;
    }

    public BackoffStrategy getBackoffStrategy() {
        return this.backoffStrategy;
    }

    public void setBackoffStrategy(BackoffStrategy backoffStrategy) {
        this.backoffStrategy = backoffStrategy;
    }

    @Override
    void delay(Throwable t, int retries) {
        pauseExponentially(retries, t);
    }

    /**
     * Exponential sleep on failed request to avoid flooding a service with
     * retries.
     *
     * @param retries           Current retry count.
     * @param throwable Exception information for the previous attempt, if any.
     */
    private void pauseExponentially(int retries, Throwable throwable) {
        long delay;
        if (backoffStrategy != null) {
            delay = this.backoffStrategy.getDelayMillis(retries);
        } else {
            long scaleFactor = 300;
            if (throwable != null) {
                scaleFactor = 500 + random.nextInt(100);
            }
            delay = (long) (Math.pow(2, retries) * scaleFactor);
        }

        delay = Math.min(delay, MAX_BACKOFF_IN_MILLISECONDS);
        log.debug("Retryable condition detected, will retry in {}ms, attempt number: {}", delay, retries);

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RestException(e.getMessage(), e);
        }
    }

    /**
     * Returns true if a failed request should be retried.
     *
     * @param method  The current HTTP method being executed.
     * @param t       The throwable from the failed request.
     * @param retries The number of times the current request has been attempted.
     * @return True if the failed request should be retried.
     */
    boolean shouldRetry(HttpRequestBase method, HttpResponse response, Throwable t, int retries) {

        int httpStatus = response.getStatusLine().getStatusCode();
        if (!retryStatusCodes.contains(httpStatus)) {
            return false;
        }

        if (retries > this.numRetries) {
            return false;
        }

        if (method instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) method).getEntity();
            if (entity != null && !entity.isRepeatable()) {
                return false;
            }
        }

        if (t instanceof NoHttpResponseException ||
                t instanceof SocketException ||
                t instanceof SocketTimeoutException ||
                t instanceof ConnectTimeoutException) {
            log.debug("Retrying on {}: {}", t.getClass().getName(), t.getMessage());
            return true;
        }

        return false;
    }
}
