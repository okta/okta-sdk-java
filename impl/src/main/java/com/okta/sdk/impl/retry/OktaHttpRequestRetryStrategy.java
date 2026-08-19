/*
 * Copyright 2023-Present Okta, Inc.
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
package com.okta.sdk.impl.retry;

import com.okta.commons.lang.Assert;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.core5.concurrent.CancellableDependency;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class OktaHttpRequestRetryStrategy extends DefaultHttpRequestRetryStrategy {

    private static final Logger logger = LoggerFactory.getLogger(OktaHttpRequestRetryStrategy.class);

    private final int maxRetries;
    private final Set<Class<? extends IOException>> nonRetriableIOExceptionClasses;
    private final Set<Integer> retriableCodes;
    private final long maxRetryDelayMillis;

    public OktaHttpRequestRetryStrategy(int maxRetries, Collection<Class<? extends IOException>> clazzes, Collection<Integer> codes, int retryMaxElapsedSeconds) {
        Assert.isTrue(maxRetries >= 0, "maxRetries should be >= 0");
        this.maxRetries = maxRetries;
        this.nonRetriableIOExceptionClasses = new HashSet<>(clazzes);
        this.retriableCodes = new HashSet<>(codes);
        // A server (or a MitM) can return an arbitrarily large 429 rate-limit-reset value, so a
        // client-side ceiling is always enforced. When the caller has not opted into a specific
        // elapsed-time budget (retryMaxElapsedSeconds <= 0), fall back to the same cap already
        // used for other retriable responses (503/504/IOExceptions). Callers who need a 429 wait
        // longer than that fallback can still opt in via ClientBuilder#setRetryMaxElapsed /
        // okta.client.requestTimeout, which is threaded through as retryMaxElapsedSeconds here.
        this.maxRetryDelayMillis = retryMaxElapsedSeconds > 0
            ? TimeUnit.SECONDS.toMillis(retryMaxElapsedSeconds)
            : RetryUtil.DEFAULT_MAX_BACKOFF_IN_MILLISECONDS;
    }

    public OktaHttpRequestRetryStrategy(int maxRetries, Collection<Class<? extends IOException>> clazzes, Collection<Integer> codes) {
        this(maxRetries, clazzes, codes, 0);
    }

    public OktaHttpRequestRetryStrategy(int maxRetries, int retryMaxElapsedSeconds) {
        this(maxRetries, Arrays.asList(InterruptedIOException.class,
                UnknownHostException.class, ConnectException.class, ConnectionClosedException.class,
                NoRouteToHostException.class, SSLException.class),
            Arrays.asList(429, 503, 504), retryMaxElapsedSeconds);
    }

    public OktaHttpRequestRetryStrategy(int maxRetries) {
        this(maxRetries, 0);
    }

    @Override
    public boolean retryRequest(HttpRequest request, IOException exception, int execCount, HttpContext context) {
        Assert.notNull(request, "request");
        Assert.notNull(exception, "exception");
        if (execCount > this.maxRetries) {
            return false;
        } else if (this.nonRetriableIOExceptionClasses.contains(exception.getClass())) {
            return false;
        } else {
            Iterator var5 = this.nonRetriableIOExceptionClasses.iterator();

            Class rejectException;
            do {
                if (!var5.hasNext()) {
                    if (request instanceof CancellableDependency && ((CancellableDependency) request).isCancelled()) {
                        return false;
                    }

                    return this.handleAsIdempotent(request);
                }

                rejectException = (Class) var5.next();
            } while (!rejectException.isInstance(exception));

            return false;
        }
    }

    @Override
    public boolean retryRequest(HttpResponse response, int execCount, HttpContext context) {
        Assert.notNull(response, "response");
        if (this.maxRetries == 0) {
            return false;
        }
        return execCount <= this.maxRetries && this.retriableCodes.contains(response.getCode());
    }

    @Override
    public TimeValue getRetryInterval(HttpResponse response, int execCount, HttpContext context) {
        Assert.notNull(response, "response");

        long delay;
        if (response.getCode() == 429) {
            delay = RetryUtil.get429DelayMillis(response, maxRetryDelayMillis);
        } else {
            delay = RetryUtil.getDefaultDelayMillis(execCount);
        }

        logger.info("Retry # {} after {} ms delay", execCount, delay);

        return TimeValue.of(delay, TimeUnit.MILLISECONDS);
    }
}
