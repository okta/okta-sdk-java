package com.okta.sdk.error;


import com.okta.sdk.resource.Resource;

public interface ErrorCause extends Resource{

    String getSummary();
}
