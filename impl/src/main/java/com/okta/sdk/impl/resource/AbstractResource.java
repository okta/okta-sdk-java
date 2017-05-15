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
package com.okta.sdk.impl.resource;

import com.okta.sdk.impl.ds.Enlistment;
import com.okta.sdk.impl.ds.InternalDataStore;
import com.okta.sdk.lang.Assert;
import com.okta.sdk.lang.Strings;
import com.okta.sdk.resource.CollectionResource;
import com.okta.sdk.resource.Resource;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @since 1.0.0
 */
public abstract class AbstractResource extends AbstractPropertyRetriever implements Resource {

    protected final Map<String, Object> dirtyProperties;  //Protected by read/write lock
    protected final Set<String> deletedPropertyNames;     //Protected by read/write lock
    protected final ReferenceFactory referenceFactory;
    private final InternalDataStore dataStore;
    protected Map<String, Object> properties;       //Protected by read/write lock
    private String href = null;
    protected volatile boolean dirty;
    private volatile boolean materialized;

    protected AbstractResource(InternalDataStore dataStore) {
        this(dataStore, null);
    }

    protected AbstractResource(InternalDataStore dataStore, Map<String, Object> properties) {
        this.referenceFactory = new ReferenceFactory();
        this.dataStore = dataStore;
        this.dirtyProperties = new LinkedHashMap<>();
        this.deletedPropertyNames = new HashSet<>();
        if (properties instanceof Enlistment) {
            this.properties = properties;
        } else {
            this.properties = new LinkedHashMap<>();
        }
        setInternalProperties(properties);
    }

    /**
     * Returns {@code true} if the specified data map represents a materialized resource data set, {@code false}
     * otherwise.
     *
     * @param props the data properties to test
     * @return {@code true} if the specified data map represents a materialized resource data set, {@code false}
     * otherwise.
     */
    public static boolean isMaterialized(Map<String, ?> props) {
        return props != null && props.size() > 1;
    }

    protected static Map<String, Property> createPropertyDescriptorMap(Property... props) {
        Map<String, Property> m = new LinkedHashMap<String, Property>();
        for (Property prop : props) {
            m.put(prop.getName(), prop);
        }
        return m;
    }

    public abstract Map<String, Property> getPropertyDescriptors();

    public void setInternalProperties(Map<String, Object> properties) {
        writeLock.lock();
        try {
            this.dirtyProperties.clear();
            this.dirty = false;
            if (properties != null && !properties.isEmpty()) {
                if (this.properties instanceof Enlistment && this.properties != properties) {
                    this.properties.clear();
                    this.properties.putAll(properties);
                } else {
                    this.properties = properties;
                }
                // Don't consider this resource materialized if it is only a reference.  A reference is any object that
                // has only one 'href' property.

                // TODO: validate this flow
//                boolean hrefOnly = this.properties.size() == 1 && this.properties.containsKey(HREF_PROP_NAME);
//                this.materialized = !hrefOnly;
                this.materialized = this.properties.size() > 0;
            } else {
                this.materialized = false;
            }
        } finally {
            writeLock.unlock();
        }
    }

    public String getResourceHref() {
        return this.href;
    }

    public void setResourceHref(String href) {
        this.href = href;
    }

    protected final InternalDataStore getDataStore() {
        return this.dataStore;
    }

    public final boolean isMaterialized() {
        return this.materialized;
    }

    /**
     * Returns {@code true} if the resource's properties have been modified in anyway since the resource instance was
     * created.
     *
     * @return {@code true} {@code true} if the resource's properties have been modified in anyway since the resource
     * instance was created
     */
    public final boolean isDirty() {
        return this.dirty;
    }

    /**
     * Returns {@code true} if the resource doesn't yet have an assigned 'href' property, {@code false} otherwise.
     *
     * @return {@code true} if the resource doesn't yet have an assigned 'href' property, {@code false} otherwise.
     */
    protected final boolean isNew() {
        String href = getResourceHref();
        return !Strings.hasText(href);
    }

    public void materialize() {
        if (this.materialized) {
            return;
        }
        AbstractResource resource = dataStore.getResource(getResourceHref(), getClass());
        writeLock.lock();
        try {
            if (this.properties != resource.properties) {
                if (!(this.properties instanceof Enlistment)) {
                    this.properties = resource.properties;
                } else {
                    this.properties.clear();
                    this.properties.putAll(resource.properties);
                }
            }

            //retain dirty properties:
            this.properties.putAll(this.dirtyProperties);

            this.materialized = true;
        } finally {
            writeLock.unlock();
        }
    }

    public Set<String> getPropertyNames() {
        readLock.lock();
        try {
            Set<String> keys = this.properties.keySet();
            return new LinkedHashSet<>(keys);
        } finally {
            readLock.unlock();
        }
    }

    public Set<String> getUpdatedPropertyNames() {
        readLock.lock();
        try {
            Set<String> keys = this.dirtyProperties.keySet();
            return new LinkedHashSet<>(keys);
        } finally {
            readLock.unlock();
        }
    }

    protected Set<String> getDeletedPropertyNames() {
        readLock.lock();
        try {
            return new LinkedHashSet<>(this.deletedPropertyNames);
        } finally {
            readLock.unlock();
        }
    }

    public Object getProperty(String name) {
            //not the href/id, must be a property that requires materialization:
            if (!isNew() && !isMaterialized()) {

                //only materialize if the property hasn't been set previously (no need to execute a server
                // request since we have the most recent value already):
                boolean present = false;
                readLock.lock();
                try {
                    present = this.dirtyProperties.containsKey(name);
                } finally {
                    readLock.unlock();
                }

                if (!present) {
                    //exhausted present properties - we require a server call:
                    materialize();
                }
            }

        return readProperty(name);
    }

    /**
     * Returns {@code true} if this resource has a property with the specified name, {@code false} otherwise.
     *
     * @param name the name of the property to check for existence
     * @return {@code true} if this resource has a property with the specified name, {@code false} otherwise.
     */
    public boolean hasProperty(String name) {
        readLock.lock();
        try {
            return !this.deletedPropertyNames.contains(name) &&
                (this.dirtyProperties.containsKey(name) || this.properties.containsKey(name));
        } finally {
            readLock.unlock();
        }
    }

    private Object readProperty(String name) {
        readLock.lock();
        try {
            if (this.deletedPropertyNames.contains(name)) {
                return null;
            }
            Object value = this.dirtyProperties.get(name);
            if (value == null) {
                value = this.properties.get(name);
            }
            return value;
        } finally {
            readLock.unlock();
        }
    }

    /**
     */
    protected Object setProperty(String name, Object value, final boolean dirty) {
        return setProperty(name, value, dirty, false);
    }

    /**
     * Use this method and the set the isNullable flag to true, to set the value to
     * null for the Property. Certain properties can have a value=null in the REST API
     * and therefore, this method will allow to explicitly do that.
     * All other overloaded implementations of setProperty method will assume isNullable=false
     * and therefore setting the value to null by calling those methods, will take no effect and
     * retain the old/previous value for the property.
     * @param property the key of the value to set
     * @param value the actual value to be set
     * @param dirty mark the object as dirty if true
     * @param isNullable true if the value can be set to {@code null}
     */
    protected void setProperty(Property property, Object value, final boolean dirty, final boolean isNullable) {
        setProperty(property.getName(), value, dirty, isNullable);
    }

    private Object setProperty(String name, Object value, final boolean dirty, final boolean isNullable) {
        writeLock.lock();
        Object previous;
        try {
            previous = this.dirtyProperties.put(name, value);
            if (previous == null) {
                previous = this.properties.get(name);
            }
            this.dirty = dirty;

            /*
             * The instance variable "deletedPropertyNames" is overloaded here.
             * For "CustomData" value=null means that the property/field has been deleted from custom data,
             * hence it is added to "deletedPropertyNames". See DefaultCustomData.java
             * In this case, where value=null and the field is nullable, adding it to "deletedPropertyNames" forces
             * and makes sure that the property is saved with value=null (but not deleted).
             * e.g. matchingProperty in AccountLinkingPolicy
             */
            if (isNullable && value == null) { //fix for https://github.com/stormpath/stormpath-sdk-java/issues/966
                this.deletedPropertyNames.add(name);
            } else {
                if (this.deletedPropertyNames.contains(name)) {
                    this.deletedPropertyNames.remove(name);
                }
            }
        } finally {
            writeLock.unlock();
        }
        return previous;
    }

    @SuppressWarnings("unchecked")
    protected <T extends Resource> T getResourceProperty(ResourceReference<T> property) {
        String key = property.getName();
        Class<T> clazz = property.getType();

        Object value = getProperty(key);
        if (value == null) {

            if (property.isCreateOnAccess()) {
                // create the new resource as an empty object so the developer does not need to deal with
                // calling .instantiate directly
                T resource = dataStore.instantiate(clazz);
                setProperty(key, resource, false);
                return resource;
            }
            return null;
        }

        if (clazz.isInstance(value)) {
            return (T) value;
        }

        if (value instanceof Map) {
            T resource = dataStore.instantiate(clazz, (Map<String, Object>) value);

            //replace the existing link object (map with an href) with the newly constructed Resource instance.
            //Don't dirty the instance - we're just swapping out a property that already exists for the materialized version.
            //let's not materialize internal collection resources, so they are always retrieved from the backend: https://github.com/stormpath/stormpath-sdk-java/issues/160
            if (!CollectionResource.class.isAssignableFrom(clazz)) {
                setProperty(key, resource, false);
            }
            return resource;
        }

        String msg = "'" + key + "' property value type does not match the specified type.  Specified type: " +
            clazz.getName() + ".  Existing type: " + value.getClass().getName();
        msg += (isPrintableProperty(key) ? ".  Value: " + value : ".");
        throw new IllegalArgumentException(msg);
    }

    /**
     * Returns the {@link List} property identified by {@code key}
     * @param property identifier
     * @return property identified by {@code property}
     */
    protected List getListProperty(ListProperty property){
        return getListProperty(property.getName());
    }

    /**
     * Returns the {@link List} property identified by {@code key}
     *
     */
    protected List getListProperty(String key){
        Object list = getProperty(key);
        return (List) list;
    }

    /**
     * Returns the {@link Set} property identified by {@code key}
     *
     */
    protected Set getSetProperty(String key) {
        Object set = getProperty(key);
        if (set instanceof List) {
            return new HashSet((List) set);
        }
        return (Set) set;
    }

//    /**
//
//     */
//    @SuppressWarnings("unchecked")
//    protected <T extends Resource, R extends T> R getSpecificResourceProperty(ResourceReference<T> property, Class<>) {
//        String key = property.getName();
//        Class<T> clazz = property.getType();
//
//        Object value = getProperty(key);
//        if (value == null) {
//            return null;
//        }
//        if (clazz.isInstance(value)) {
//            return (R) value;
//        }
//        if (value instanceof Map && !((Map) value).isEmpty()) {
//            T resource = dataStore.instantiate(clazz, (Map<String, Object>) value);
//
//
//            //replace the existing link object (map with an href) with the newly constructed Resource instance.
//            //Don't dirty the instance - we're just swapping out a property that already exists for the materialized version.
//            setProperty(key, resource, false);
//            return resource;
//        }
//
//        String msg = "'" + key + "' property value type does not match the specified type.  Specified type: " +
//                clazz.getName() + ".  Existing type: " + value.getClass().getName();
//        msg += (isPrintableProperty(key) ? ".  Value: " + value : ".");
//        throw new IllegalArgumentException(msg);
//    }

    protected <T extends Resource> void setResourceProperty(ResourceReference<T> property, Resource value) {
        Assert.notNull(property, "Property argument cannot be null.");
        String name = property.getName();
        Map<String, String> reference = this.referenceFactory.createReference(name, value);
        setProperty(name, reference);
    }


    /**
     * This method is able to set a Reference to a resource (<code>value</code>) even though resource has not yet an href value
     * <p>Note that this is method is analogous to the {@link #setResourceProperty(ResourceReference, Resource)} method (in fact
     * it relies on it when the resource alredy has an href value) but this method does not complain when the href of the resource is missing.</p>
     *
     * @param property the property whose value is going to be set to <code>value</code>
     * @param value    the value to be set to <code>property</code>
     * @param <T>      the type of Resource returned
     */
    protected <T extends Resource> void setMaterializableResourceProperty(ResourceReference<T> property, Resource value) {
        Assert.notNull(property, "Property argument cannot be null.");
        Assert.isNull(value.getResourceHref(), "Resource must not have an 'href' property ");
        if (((AbstractResource) value).isMaterialized()) {
            setResourceProperty(property, value);
        } else {
            String name = property.getName();
            Map<String, String> reference = this.referenceFactory.createUnmaterializedReference(name, value);
            setProperty(name, reference);
        }
    }

    public String toString() {
        readLock.lock();
        try {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> entry : this.properties.entrySet()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                String key = entry.getKey();
                //prevent printing of any sensitive values:
                if (isPrintableProperty(key)) {
                    sb.append(key).append(": ").append(String.valueOf(entry.getValue()));
                }
            }
            return sb.toString();
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Returns {@code true} if the internal property is safe to print in toString(), {@code false} otherwise.
     *
     * @param name The name of the property to check for safe printing
     * @return {@code true} if the internal property is safe to print in toString(), {@code false} otherwise.
     */
    protected boolean isPrintableProperty(String name) {
        return true;
    }

    @Override
    public int hashCode() {
        readLock.lock();
        try {
            return this.properties.isEmpty() ? 0 : this.properties.hashCode();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!o.getClass().equals(getClass())) {
            return false;
        }
        AbstractResource other = (AbstractResource) o;
        readLock.lock();
        try {
            other.readLock.lock();
            try {
                return this.properties.equals(other.properties);
            } finally {
                other.readLock.unlock();
            }
        } finally {
            readLock.unlock();
        }
    }

    @Override
    protected Map<String, Object> getInternalProperties() {
        return properties;
    }

}
