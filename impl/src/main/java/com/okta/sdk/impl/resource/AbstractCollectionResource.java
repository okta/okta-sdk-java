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
package com.okta.sdk.impl.resource;

import com.okta.sdk.impl.ds.InternalDataStore;
import com.okta.sdk.resource.CollectionResource;
import com.okta.sdk.resource.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @since 0.5.0
 */
public abstract class AbstractCollectionResource<T extends Resource> extends AbstractResource implements CollectionResource<T> {

    private static final StringProperty NEXT_PAGE = new StringProperty("nextPage");
    private static final String ITEMS_PROPERTY_NAME = "items";

    private final Map<String, Object> queryParams;
    private String nextPageHref = null;

    private final AtomicBoolean firstPageQueryRequired = new AtomicBoolean();

    protected AbstractCollectionResource(InternalDataStore dataStore) {
        super(dataStore);
        this.queryParams = Collections.emptyMap();
    }

    protected AbstractCollectionResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
        this.queryParams = Collections.emptyMap();
        this.nextPageHref = getString(NEXT_PAGE);
    }

    protected AbstractCollectionResource(InternalDataStore dataStore, Map<String, Object> properties, Map<String, Object> queryParams) {
        super(dataStore, properties);
        this.nextPageHref = getString(NEXT_PAGE);
        if (queryParams != null) {
            this.queryParams = queryParams;
        } else {
            this.queryParams = Collections.emptyMap();
        }
    }

    private String getNextPageHref() {
        return nextPageHref;
    }

    private boolean hasNextPage() {
        return getNextPageHref() != null;
    }

    @Override
    public T single() {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            throw new IllegalStateException("This list is empty while it was expected to contain one (and only one) element.");
        }
        T itemToReturn = iterator.next();
        if (iterator.hasNext()) {
            throw new IllegalStateException("Only a single resource was expected, but this list contains more than one item.");
        }
        return itemToReturn;
    }

    protected abstract Class<T> getItemType();

    @SuppressWarnings("unchecked")
    public Page<T> getCurrentPage() {

        Collection<T> items = Collections.emptyList();

        Object value = getProperty(ITEMS_PROPERTY_NAME);

        if (value != null) {
            Collection c = null;
            if (value instanceof Map[]) {
                Map[] vals = (Map[]) value;
                if (vals.length > 0) {
                    c = Arrays.asList((Map[]) vals);
                }
            } else if (value instanceof Collection) {
                Collection vals = (Collection) value;
                if (vals.size() > 0) {
                    c = vals;
                }
            }
            if (c != null && !c.isEmpty()) {
                //do a look ahead to see if resource conversion has already taken place:
                if (!getItemType().isInstance(c.iterator().next())) {
                    //need to convert the list of links to a list of unmaterialized Resources
                    items = toResourceList(c, getItemType());
                    //replace the existing list of links with the newly constructed list of Resources.  Don't dirty
                    //the instance - we're just swapping out a property that already exists for the materialized version.
                    setProperty(ITEMS_PROPERTY_NAME, items, false);
                } else {
                    //the collection has already been converted to Resources - use it directly:
                    items = c;
                }
            }
        }

        return new DefaultPage<>(items);
    }


    @Override
    public Iterator<T> iterator() {
        //firstPageQueryRequired ensures that newly obtained collection resources don't need to query unnecessarily
        return new PaginatedIterator<>(this, firstPageQueryRequired.getAndSet(true));
    }

    @Override
    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public Spliterator<T> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
    }

    private Collection<T> toResourceList(Collection vals, Class<T> itemType) {

        List<T> list = new ArrayList<>(vals.size());

        for (Object o : vals) {
            Map<String, Object> properties = (Map<String, Object>) o;
            T resource = toResource(itemType, properties);
            list.add(resource);
        }

        return list;
    }

    protected T toResource(Class<T> resourceClass, Map<String, Object> properties) {
        return getDataStore().instantiate(resourceClass, properties);
    }

    private class PaginatedIterator<T extends Resource> implements Iterator<T> {

        private AbstractCollectionResource<T> resource;

        private Page<T> currentPage;
        private Iterator<T> currentPageIterator;

        private PaginatedIterator(AbstractCollectionResource<T> resource, boolean firstPageQueryRequired) {

            if (firstPageQueryRequired) {
                //We get a new resource in order to have different iterator instances: issue 62 (https://github.com/stormpath/stormpath-sdk-java/issues/62)
                this.resource = getDataStore().getResource(resource.getResourceHref(), resource.getClass(), resource.queryParams);
                this.currentPage = this.resource.getCurrentPage();
            } else {
                this.resource = resource;
                this.currentPage = resource.getCurrentPage();
            }

            this.currentPageIterator = this.currentPage.getItems().iterator();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean hasNext() {

            boolean hasNext = currentPageIterator.hasNext();

            //If we have already exhausted the whole collection size there is no need to contact the backend again
            if (!hasNext && hasNextPage()) {

                //if we're done with the current page, and we've exhausted the page limit (i.e. we've read a
                //full page), we will have to execute another request to check to see if another page exists.
                //We can't 'trust' the current page iterator to know if more results exist on the server since it
                //only represents a single page.

                AbstractCollectionResource nextResource =
                        getDataStore().getResource(getNextPageHref(), resource.getClass());
                Page<T> nextPage = nextResource.getCurrentPage();
                Iterator<T> nextIterator = nextPage.getItems().iterator();

                if (nextIterator.hasNext()) {
                    hasNext = true;
                    //update to reflect the new page:
                    this.resource = nextResource;
                    this.currentPage = nextPage;
                    this.currentPageIterator = nextIterator;
                    nextPageHref = nextResource.getString(NEXT_PAGE);
                }
            }

            return hasNext;
        }

        @Override
        public T next() {
            return currentPageIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported.");
        }
    }

    private static class DefaultPage<T> implements Page<T> {

        private final Collection<T> items;

        DefaultPage(Collection<T> items) {
            this.items = Collections.unmodifiableCollection(items);
        }

        @Override
        public Collection<T> getItems() {
            return this.items;
        }
    }
}