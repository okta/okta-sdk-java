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
package com.okta.sdk.impl.ds.cache;

import com.okta.sdk.cache.Cache;
import com.okta.sdk.impl.ds.CacheMapInitializer;
import com.okta.sdk.impl.ds.DefaultCacheMapInitializer;
import com.okta.sdk.impl.ds.DefaultResourceFactory;
import com.okta.sdk.impl.ds.FilterChain;
import com.okta.sdk.impl.ds.ResourceAction;
import com.okta.sdk.impl.ds.ResourceDataRequest;
import com.okta.sdk.impl.ds.ResourceDataResult;
import com.okta.sdk.impl.http.CanonicalUri;
import com.okta.sdk.impl.resource.AbstractInstanceResource;
import com.okta.sdk.impl.resource.AbstractResource;
import com.okta.sdk.impl.resource.ArrayProperty;
import com.okta.sdk.impl.resource.Property;
import com.okta.sdk.impl.resource.ReferenceFactory;
import com.okta.sdk.impl.resource.ResourceReference;
import com.okta.sdk.impl.resource.SetProperty;
import com.okta.sdk.impl.util.BaseUrlResolver;
import com.okta.sdk.lang.Assert;
import com.okta.sdk.lang.Collections;
import com.okta.sdk.resource.CollectionResource;
import com.okta.sdk.resource.Resource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class WriteCacheFilter extends AbstractCacheFilter {

    private final BaseUrlResolver baseUrlResolver;
    private final ReferenceFactory referenceFactory;
    private final CacheMapInitializer cacheMapInitializer;


    public WriteCacheFilter(BaseUrlResolver baseUrlResolver, CacheResolver cacheResolver, boolean collectionCachingEnabled, ReferenceFactory referenceFactory) {
        super(cacheResolver, collectionCachingEnabled);
        Assert.notNull(referenceFactory, "referenceFactory cannot be null.");
        Assert.notNull(baseUrlResolver, "baseUrlResolver cannot be null.");
        this.referenceFactory = referenceFactory;
        this.cacheMapInitializer = new DefaultCacheMapInitializer();
        this.baseUrlResolver = baseUrlResolver;
    }

    @Override
    public ResourceDataResult filter(ResourceDataRequest request, FilterChain chain) {

        if (request.getAction() == ResourceAction.DELETE) {
            String key = getCacheKey(request);
            uncache(key, request.getResourceClass());
        }

        ResourceDataResult result = chain.filter(request);

        if (isCacheable(request, result)) {
            cache(result.getResourceClass(), result.getData(), result.getUri());
        }

        return result;
    }

    private boolean isCacheable(ResourceDataRequest request, ResourceDataResult result) {

        if (Collections.isEmpty(result.getData())) {
            return false;
        }

        Class<? extends Resource> clazz = result.getResourceClass();

        return
            //@since 0.5.0
            AbstractResource.isMaterialized(result.getData());
    }


    @SuppressWarnings("unchecked")
    private void cache(Class<? extends Resource> clazz, Map<String, ?> data, CanonicalUri uri) {

        Assert.notEmpty(data, "Resource data cannot be null or empty.");
        String href = uri.getAbsolutePath();

        if (isDirectlyCacheable(clazz, data)) {
            Assert.notNull(href, "Resource data must contain an 'href' attribute.");
            Assert.isTrue(data.size() > 1, "Resource data must be materialized to be cached " +
                    "(need more than just an 'href' attribute)."); // TODO: this likely is not valid for Okta
        }

        //create a map to reflect the resource's canonical representation - this is what will be cached:
        Map<String, Object> cacheValue = cacheMapInitializer.initialize(clazz, data, uri.getQuery());

        for (Map.Entry<String, ?> entry : data.entrySet()) {

            String name = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {

                Map<String, ?> nested = (Map<String, ?>) value;

                if (AbstractResource.isMaterialized(nested) && false) { // FIXME: cannot cache submodels yet
                    //If there is more than one attribute (more than just 'href') it is not just a simple reference
                    //anymore - it has been materialized to its full set of attributes.  Because we have a full
                    //materialized resource, we need to recursively cache it (and any of its referenced materialized
                    //resources) and so on.

                    //find the type of object this attribute name represents:
                    Property property = getPropertyDescriptor(clazz, name);
                    Assert.isTrue(property instanceof ResourceReference,
                                  "It is expected that only ResourceReference properties are complex objects.");

                    //cache this materialized reference:
                    //we pass 'null' in as the querystring param because the querystring is only valid for
                    //the top-most item being cached - we don't want to propagate it for nested resources because the nested
                    //resource wasn't acquired w/ that query string.
                    cache(property.getType(), nested, null);

                    //Because the materialized reference has now been cached, we don't need to store
                    //all of its properties again in the 'toCache' instance.  Instead, we just want to store
                    //an unmaterialized reference (a Map with just the 'href' attribute).
                    //If the a caller attempts to materialize the reference, we will hit the cached version and
                    //use that data instead of issuing a request.
                    value = toCanonicalReference(name, nested);
                }
            } else if (value instanceof Collection && name.equals("items") && data.get("href") != null) { //array property, i.e. the 'items' collection resource property
                Collection c = (Collection) value;
                //Create a new collection that has only references, recursively caching any materialized references:
                List list = new ArrayList(c.size());

                //if the values in the collection are materialized, we need to cache that materialized reference.
                //If the value is not materialized, we don't do anything.

                //find the type of objects this collection contains:
                Property property = getPropertyDescriptor(clazz, name);

                boolean isCollection = property instanceof SetProperty || property instanceof ArrayProperty;

                Assert.isTrue(isCollection, "It is expected that only ArrayProperty or SetProperty properties represent collection items.");

                Property itemsProperty;
                Class itemType;
                if(property instanceof SetProperty){
                    itemsProperty = SetProperty.class.cast(property);
                }else {
                    itemsProperty = ArrayProperty.class.cast(property);
                }
                itemType = itemsProperty.getType();

                for (Object o : c) {
                    Object element = o;
                    if (o instanceof Map) {
                        Map referenceData = (Map) o;
                        if (AbstractResource.isMaterialized(referenceData)) {
                            //we pass 'null' in as the querystring param because the querystring is only valid for
                            //the top-most item being cached - we don't want to propagate it for nested resources because the nested
                            //resource wasn't acquired w/ that query string.
//                            cache(itemType, referenceData, null); // TODO: we cannot cache anything with a null url
                            element = toCanonicalReference(null, referenceData);
                        }
                    }
                    list.add(element);
                }

                value = list;
            }
        }

        if (isDirectlyCacheable(clazz, cacheValue)) {
            Cache cache = getCache(clazz);
            String cacheKey = getCacheKey(href, uri.getQuery(), clazz);
            cache.put(cacheKey, cacheValue);
        }
    }


    private Map<String,?> toCanonicalReference(String name, Map<String,?> resourceData) {

        //If the resource data reflects a materialized instance resource (not a collection resource), we can convert it
        //to a link since it will cached in shared cache.  This way any time the link is resolved (across any
        //collection), the same shared cache instance data will be returned, instead of potentially having different
        //representations of the same resource in different collections.
        if (AbstractInstanceResource.isInstanceResource(resourceData)) {
            return this.referenceFactory.createReference(name, resourceData);
        }

        //Collections are not yet placed in the shared cache due to the significant challenge of coherency, so we
        // don't want to 'lose' the fidelity of the collection's properties by converting it to just a link.  So
        // we return the actual collection:
        return resourceData;
    }

    private <T extends Resource> Property getPropertyDescriptor(Class<T> clazz, String propertyName) {
//        clazz = SubtypeDispatchingResourceFactory.getImplementationClass(clazz, propertyName); // FIXME

        Map<String, Property> descriptors = getPropertyDescriptors(clazz);
        return descriptors.get(propertyName);
    }

    @SuppressWarnings("unchecked")
    private <T extends Resource> Map<String, Property> getPropertyDescriptors(Class<T> clazz) {
        Class<T> implClass = DefaultResourceFactory.getImplementationClass(clazz);
        String propertyDescriptors = "PROPERTY_DESCRIPTORS";
        Map<String, Property> returnValue;
        try {
            Field field = implClass.getDeclaredField(propertyDescriptors);
            field.setAccessible(true);
            returnValue = (Map<String, Property>) field.get(null);
            while(implClass.getSuperclass() != null && Resource.class.isAssignableFrom(implClass)){
                implClass = (Class<T>) implClass.getSuperclass();
                try{
                    field = implClass.getDeclaredField(propertyDescriptors);
                    field.setAccessible(true);
                    returnValue.putAll((Map<String, Property>) field.get(null));
                }
                catch(NoSuchFieldException nsfe){
                    // It is not guaranteed that PROPERTY_DESCRIPTORS is part of every super class of the type resource.
                }
            }
            return returnValue;
        } catch (Exception e) {
            throw new IllegalStateException(
                "Unable to access PROPERTY_DESCRIPTORS static field on implementation class " + clazz.getName(), e);
        }
    }

    /**
     * Quick fix for <a href="https://github.com/stormpath/stormpath-sdk-java/issues/17">Issue #17</a>.
     *
     */
    private boolean isDirectlyCacheable(Class<? extends Resource> clazz, Map<String, ?> data) {

        return AbstractResource.isMaterialized(data) &&
               (!CollectionResource.class.isAssignableFrom(clazz) ||
                (CollectionResource.class.isAssignableFrom(clazz) && isCollectionCachingEnabled()));
    }

    @SuppressWarnings("unchecked")
    private void uncache(String cacheKey, Class<? extends Resource> resourceType) {
        Assert.hasText(cacheKey, "cacheKey cannot be null or empty.");
        Assert.notNull(resourceType, "resourceType cannot be null.");
        Cache<String, Map<String, ?>> cache = getCache(resourceType);
        cache.remove(cacheKey);
    }
}
