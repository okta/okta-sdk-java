/*
 * Copyright 2014 Stormpath, Inc.
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
package com.okta.sdk.impl.error;

import com.okta.sdk.error.Error;
import com.okta.sdk.error.authc.OAuthAuthenticationException;
import com.okta.sdk.lang.Classes;
import com.okta.sdk.lang.Strings;
import com.okta.sdk.resource.ResourceException;

import java.lang.reflect.Constructor;

/**
 * @since 0.5.0
 */
public class ApiAuthenticationExceptionFactory {

    public static final int AUTH_EXCEPTION_STATUS = 401;

    public static final String AUTH_EXCEPTION_CODE = "401";

    private static final String DEFAULT_DEVELOPER_MESSAGE = "Authentication with a valid API Key is required.";

    private static final String DEFAULT_CLIENT_MESSAGE = "Authentication Required";

    public static ResourceException newApiAuthenticationException(Class<? extends ResourceException> clazz) {

        Error error = DefaultErrorBuilder.status(AUTH_EXCEPTION_STATUS).code(AUTH_EXCEPTION_CODE).message(DEFAULT_CLIENT_MESSAGE).build();

        Constructor<? extends ResourceException> constructor = Classes.getConstructor(clazz, Error.class);

        return Classes.instantiate(constructor, error);
    }

    public static ResourceException newApiAuthenticationException(Class<? extends ResourceException> clazz, String message) {

        if (Strings.isEmpty(message)) {
            message = DEFAULT_CLIENT_MESSAGE;
        }

        Error error = DefaultErrorBuilder.status(AUTH_EXCEPTION_STATUS).code(AUTH_EXCEPTION_CODE).message(getOrDefaultErrorMessage(message)).build();


        Constructor<? extends ResourceException> constructor = getConstructorFromClass(clazz);

        return Classes.instantiate(constructor, error);
    }

    public static ResourceException newOAuthException(Class<? extends OAuthAuthenticationException> clazz, String message) {

        Error error = DefaultErrorBuilder.status(AUTH_EXCEPTION_STATUS).code(AUTH_EXCEPTION_CODE).message(getOrDefaultErrorMessage(message)).build();

        Constructor<? extends ResourceException> constructor = getConstructorFromClass(clazz);

        return Classes.instantiate(constructor, error, message);
    }

    private static Constructor<? extends ResourceException> getConstructorFromClass(Class<? extends ResourceException> clazz) {
        if (OAuthAuthenticationException.class.isAssignableFrom(clazz)) {
            return Classes.getConstructor(clazz, Error.class, String.class);
        }
        return Classes.getConstructor(clazz, Error.class);
    }

    private static String getOrDefaultErrorMessage(String nullableErrorMessage) {
        if (Strings.isEmpty(nullableErrorMessage)) {
            nullableErrorMessage = DEFAULT_CLIENT_MESSAGE;
        }
        return nullableErrorMessage;
    }

}
