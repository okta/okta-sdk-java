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
package com.okta.sdk.impl.http.authc;

import com.okta.sdk.client.AuthenticationScheme;
import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.impl.http.support.RequestAuthenticationException;
import com.okta.commons.lang.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

/**
 * This default factory is responsible of creating a {@link RequestAuthenticator} out of a given {@link AuthenticationScheme}
 * <p>
 * This implementation returns a {@link SswsAuthenticator} when the authentication scheme is undefined.
 *
 * @since 0.5.0
 */
public class DefaultRequestAuthenticatorFactory implements RequestAuthenticatorFactory {

    private final Logger log = LoggerFactory.getLogger(DefaultRequestAuthenticatorFactory.class);

    /**
     * Creates a {@link RequestAuthenticator} out of the given {@link AuthenticationScheme}.
     *
     * @param scheme the authentication scheme enum defining the request authenticator to be created
     * @return the corresponding `RequestAuthenticator` for the given `AuthenticationScheme`. Returns `SswsAuthenticator` if
     * the authentication scheme is undefined.
     */
    @SuppressWarnings("unchecked")
    public RequestAuthenticator create(AuthenticationScheme scheme, ClientCredentials clientCredentials) {

        if (scheme == null) {
            //By default, this factory creates a digest authentication when a scheme is not defined
            return new SswsAuthenticator(clientCredentials);
        }

        try {
            Class requestAuthenticatorClass = Classes.forName(scheme.getRequestAuthenticatorClassName());
            Constructor<RequestAuthenticator> ctor = Classes.getConstructor(requestAuthenticatorClass, ClientCredentials.class);
            return Classes.instantiate(ctor, clientCredentials);
        } catch (RuntimeException ex) {
            String errormessage = "There was an error instantiating " + scheme.getRequestAuthenticatorClassName();
            log.error(errormessage, ex);
            throw new RequestAuthenticationException(errormessage);
        }
    }

}
