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

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.okta.sdk.lang.Assert;
import com.okta.sdk.resource.PropertyRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Refactored methods from {@link AbstractResource} to make them common to subclasses.
 *
 * @since 0.5.0
 */
public abstract class AbstractPropertyRetriever implements PropertyRetriever {

    private static final Logger log = LoggerFactory.getLogger(AbstractPropertyRetriever.class);

    private final DateFormat dateFormatter = new ISO8601DateFormat();
    private final EnumConverter enumConverter = new EnumConverter();

    protected final Lock readLock;

    protected final Lock writeLock;


    protected AbstractPropertyRetriever() {
        ReadWriteLock rwl = new ReentrantReadWriteLock();
        this.readLock = rwl.readLock();
        this.writeLock = rwl.writeLock();
    }

    public abstract Object getProperty(String name);

    @Override
    public String getString(String key) {
        return getStringProperty(key);
    }

    @Override
    public Integer getInteger(String key) {
        return getIntProperty(key);
    }

    @Override
    public Double getNumber(String key) {
        return getDoubleProperty(key);
    }

    @Override
    public Boolean getBoolean(String key) {
        return getNullableBooleanProperty(key);
    }

    @Override
    public List<String> getStringList(String key) {
        return getListProperty(key);
    }

    @Override
    public List<Integer> getIntegerList(String key) {
        return getListProperty(key);
    }

    @Override
    public List<Double> getNumberList(String key) {
        return getListProperty(key);
    }

    protected String getString(StringProperty property) {
        return getStringProperty(property.getName());
    }

    protected String getStringProperty(String key) {
        Object value = getProperty(key);
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    protected int getInt(IntegerProperty property) {
        Integer value = getIntProperty(property.getName());
        return (value == null) ? -1 : value;
    }

    protected Integer getIntProperty(IntegerProperty property) {
        return getIntProperty(property.getName());
    }

    protected Integer getIntProperty(String key) {
        Object value = getProperty(key);
        if (value != null) {
            if (value instanceof String) {
                return parseInt((String) value);
            } else if (value instanceof Number) {
                return ((Number) value).intValue();
            }
        }
        return null;
    }

    protected Double getDoubleProperty(DoubleProperty property) {
        return getDoubleProperty(property.getName());
    }

    protected Double getDoubleProperty(String key) {
        Object value = getProperty(key);
        if (value != null) {
            if (value instanceof String) {
                return parseDouble((String) value);
            } else if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        }
        return null;
    }

    protected boolean getBoolean(BooleanProperty property) {
        return getBooleanProperty(property.getName());
    }

    /**
     * Returns an actual boolean value instead of a possible null Boolean value since desired usage
     * is to have either a true or false.
     * @param key the identifier
     * @return a boolean representation of the value (null == false)
     */
    protected Boolean getBooleanProperty(String key) {
        return Boolean.TRUE.equals(getNullableBooleanProperty(key));
    }

    protected Boolean getNullableBoolean(BooleanProperty property) {
        return getNullableBooleanProperty(property.getName());
    }

    protected Boolean getNullableBooleanProperty(String key) {
        Object value = getProperty(key);
        if (value != null) {
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof String) {
                return Boolean.valueOf((String) value);
            }
        }
        return null;
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            if (log.isErrorEnabled()) {
                String msg = "Unable to parse string '{}' into an integer value.  Defaulting to -1";
                log.error(msg, e);
            }
        }
        return -1;
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            if (log.isErrorEnabled()) {
                String msg = "Unable to parse string '{}' into an double value.  Defaulting to -1";
                log.error(msg, e);
            }
        }
        return -1;
    }

    protected Date getDateProperty(DateProperty key) {
        Object value = getProperty(key.getName());
        if (value == null) {
            return null;
        }

        try {
            return dateFormatter.parse(String.valueOf(value));
        } catch (ParseException e) {
            if (log.isErrorEnabled()) {
                String msg = "Unabled to parse string '{}' into an date value.  Defaulting to null.";
                log.error(msg, e);
            }
        }
        return null;
    }

    /**
     * Returns the {@link List} property identified by {@code key}
     * @param key the identifier
     * @return a List
     *
     */
    protected List getListProperty(String key) {
        return (List) getProperty(key);
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
     * @param property identifier
     * @return property identified by {@code property}
     */
    protected List getEnumListProperty(EnumListProperty property){
        List<String> rawList = (List) getProperty(property.getName());

        return (rawList == null) ? null : (List) rawList.stream()
                .map(item -> enumConverter.fromValue(property.getType(), item))
                .collect(Collectors.toList());
    }

    protected Map getMap(MapProperty mapProperty) {
        return getMapProperty(mapProperty.getName());
    }

    protected Map getNonEmptyMap(MapProperty mapProperty) {
        Map result = getMap(mapProperty);
        return result != null ? result : Collections.emptyMap();
    }

    protected Map getMapProperty(String key) {
        Object value = getProperty(key);
        if (value != null) {
            if (value instanceof Map) {
                return (Map) value;
            }
            String msg = "'" + key + "' property value type does not match the specified type. Specified type: Map. " +
                    "Existing type: " + value.getClass().getName();
            msg += (isPrintableProperty(key) ? ".  Value: " + value : ".");
            throw new IllegalArgumentException(msg);
        }
        return null;
    }

    protected <E extends Enum<E>> E getEnumProperty(EnumProperty<E> enumProperty) {
        return getEnumProperty(enumProperty.getName(), enumProperty.getType());
    }

    protected <E extends Enum<E>> E getEnumProperty(String key, Class<E> type) {
        Assert.notNull(type, "type cannot be null.");

        Object value = getProperty(key);

        if (value != null) {
            if (value instanceof String) {
                return enumConverter.fromValue(type, value.toString());
            }
            if (type.isAssignableFrom(value.getClass())) {
                //noinspection unchecked
                return (E) value;
            }
        }
        return null;
    }

    protected char[] getCharArray(CharacterArrayProperty property) {

        Object value = getProperty(property.getName());
        if (value instanceof char[]) {
            return (char[]) value;
        } else if (value != null) {
            return value.toString().toCharArray();
        }
        return null;
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

    protected void setProperty(Property property, Object value) {
        setProperty(property.getName(), value, true);
    }

    public void setProperty(String name, Object value) {
        setProperty(name, value, true);
    }

    protected abstract Object setProperty(String name, Object value, final boolean dirty);

    protected abstract Map<String, Object> getInternalProperties();

}
