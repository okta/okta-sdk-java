package com.okta.sdk.impl.http;

/**
 * A partial model of a 'rfc5988' link implementation.
 */
public interface Link {

    String getRelationType();
    String getHref();
}
