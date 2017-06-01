/*
 * Copyright 2017 Okta
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
package com.okta.sdk.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Provider;
import java.security.Security;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @since 1.0.0
 */
public final class DefaultRuntimeEnvironment implements RuntimeEnvironment {

    private static final Logger log = LoggerFactory.getLogger(DefaultRuntimeEnvironment.class);

    public static final DefaultRuntimeEnvironment INSTANCE = new DefaultRuntimeEnvironment();

    private DefaultRuntimeEnvironment() {
    }

    private static final String BC_PROVIDER_CLASS_NAME = "org.bouncycastle.jce.provider.BouncyCastleProvider";

    private static final AtomicBoolean bcLoaded = new AtomicBoolean(false);

    private static void enableBouncyCastleIfPossible() {

        if (bcLoaded.get()) {
            return;
        }

        try {
            Class clazz = Classes.forName(BC_PROVIDER_CLASS_NAME);

            //check to see if the user has already registered the BC provider:

            Provider[] providers = Security.getProviders();

            for (Provider provider : providers) {
                if (clazz.isInstance(provider)) {
                    bcLoaded.set(true);
                    return;
                }
            }

            //bc provider not enabled - add it:
            Security.addProvider((Provider) Classes.newInstance(clazz));
            bcLoaded.set(true);

        } catch (UnknownClassException e) {
            log.debug("Unable to load BouncyCastle.  This is an acceptable outcome and this exception " +
                "does not necessarily reflect a problem and can be ignored.", e);
            //not available
        }
    }

    static {
        enableBouncyCastleIfPossible();
    }

    @Override
    public boolean isClassAvailable(String fqcn) {
        return Classes.isAvailable(fqcn);
    }
}
