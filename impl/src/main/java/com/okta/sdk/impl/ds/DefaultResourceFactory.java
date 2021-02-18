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
package com.okta.sdk.impl.ds;

import com.okta.commons.lang.Classes;
import com.okta.commons.lang.Strings;
import com.okta.sdk.resource.Resource;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

/**
 * @since 0.5.0
 */
public class DefaultResourceFactory implements ResourceFactory {

    private final InternalDataStore dataStore;

    private static final Set<String> SUPPORTED_PACKAGES = getSupportedPackages();
    private static final String IMPL_PACKAGE_NAME_FRAGMENT = "impl";
    private static final String IMPL_PACKAGE_NAME = IMPL_PACKAGE_NAME_FRAGMENT + ".";
    private static final String IMPL_CLASS_PREFIX = "Default";

    private final DiscriminatorRegistry registry = new DefaultDiscriminatorRegistry();

    public DefaultResourceFactory(InternalDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public <T extends Resource> T instantiate(Class<T> clazz, Object... constructorArgs) {

        if (clazz == null) {
            throw new NullPointerException("Resource class cannot be null.");
        }

        Class<T> classToResolve = clazz;

        Object[] ctorArgs = createConstructorArgs(constructorArgs);

        if (ctorArgs.length >= 2 && ctorArgs[1] instanceof Map) {

            Map data = (Map) ctorArgs[1];
            if (registry.supportedClass(clazz)) {
                classToResolve = registry.resolve(clazz, data);
            }
        }

        Class<T> implClass = getImplementationClass(classToResolve);

        Constructor<T> ctor;

        if (ctorArgs.length == 1) {
            ctor = Classes.getConstructor(implClass, InternalDataStore.class);
        } else if (ctorArgs.length == 2) {
            ctor = Classes.getConstructor(implClass, InternalDataStore.class, Map.class);
        } else {
            //collection resource - we want to retain the query parameters (3rd ctor argument):
            ctor = Classes.getConstructor(implClass, InternalDataStore.class, Map.class, Map.class);
        }

        return Classes.instantiate(ctor, ctorArgs);
    }

    private static <T extends Resource> Class<T> getImplementationClass(Class<T> clazz) {
        if (clazz.isInterface()) {
            return convertToImplClass(clazz);
        }
        return clazz;
    }

    public static <T extends Resource> Class<T> getInterfaceClass(Class<T> clazz) {
        if (clazz.isInterface()) {
            return clazz;
        }
        return convertToInterfaceClass(clazz);
    }

    static <T extends Resource> Class<T> convertToInterfaceClass(Class<T> clazz) {
        String fqcn = clazz.getName();

        String basePackage = SUPPORTED_PACKAGES.stream()
                .filter(fqcn::startsWith)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not determine interface for class: '" + fqcn +"'"));

        String afterBase = fqcn.substring(basePackage.length());

        //e.g. if impl is com.okta.sdk.impl.account.DefaultAccount, 'afterBase' is impl.account.DefaultAccount

        //split interface simple name and the remainder of the package structure:

        if (afterBase.startsWith(IMPL_PACKAGE_NAME)) {
            afterBase = afterBase.substring(IMPL_PACKAGE_NAME.length());
        }

        //now afterBase is account.DefaultAccount

        //now append the intermediate package name to the base package:
        int index = afterBase.lastIndexOf('.');
        String beforeSimpleName = afterBase.substring(0, index);

        String simpleName = clazz.getSimpleName();

        //strip off any 'Default' prefix:
        index = simpleName.indexOf(IMPL_CLASS_PREFIX);
        simpleName = simpleName.substring(index + IMPL_CLASS_PREFIX.length());

        String ifaceFqcn = basePackage + beforeSimpleName + "." + simpleName;

        return Classes.forName(ifaceFqcn);
    }

    @SuppressWarnings("unchecked")
    static <T extends Resource> Class<T> convertToImplClass(Class<T> clazz) {
        String fqcn = clazz.getName();

        String basePackage = SUPPORTED_PACKAGES.stream()
                .filter(fqcn::startsWith)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not determine impl for class: '" + fqcn +"'"));

        String afterBase = fqcn.substring(basePackage.length());
        //e.g. if interface is com.okta.sdk.account.Account, 'afterBase' is account.Account

        //split interface simple name and the remainder of the package structure:

        //if this is an impl-module-specific class, strip off the existing 'impl.' package:
        if (afterBase.startsWith(IMPL_PACKAGE_NAME)) {
            afterBase = afterBase.substring(IMPL_PACKAGE_NAME.length());
        }

        int index = afterBase.lastIndexOf('.');

        String implFqcn;

        if (index == -1) {
            implFqcn = basePackage + IMPL_PACKAGE_NAME_FRAGMENT + "." + IMPL_CLASS_PREFIX + clazz.getSimpleName();
        } else {
            String beforeConcreteClassName = afterBase.substring(0, index);
            implFqcn = basePackage + IMPL_PACKAGE_NAME_FRAGMENT + "." +
                beforeConcreteClassName + "." + IMPL_CLASS_PREFIX + clazz.getSimpleName();
        }

        return Classes.forName(implFqcn);
    }

    private Object[] createConstructorArgs(Object[] existing) {
        int argsLength = existing != null ? existing.length : 0;
        argsLength += 1; //account for the 'DataStore' instance that is required for every implementation.

        List<Object> args = new ArrayList<>(argsLength);
        args.add(this.dataStore); //always first arg
        if (existing != null) {
            Collections.addAll(args, existing);
        }

        return args.toArray();
    }

    private static Set<String> getSupportedPackages() {
        Set<String> result = new HashSet<>();
        stream(ServiceLoader.load(ResourceFactoryConfig.class).spliterator(), false)
                    .map(config -> config.getSupportedPackages().stream()
                               .map(it -> it + ".")
                               .collect(Collectors.toSet()))
                    .forEach(result::addAll);
        return result;
    }
}
