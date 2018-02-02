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
package com.okta.sdk.lang;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A duration is a scalar value paired with a unit of time. For example, 10 milliseconds and 50 minutes are both
 * durations (scalar + unit of time).
 *
 * @since 0.5.0
 */
public class Duration implements Comparable<Duration>, Cloneable {

    private final long value;

    private final TimeUnit timeUnit;

    public Duration(long value, TimeUnit timeUnit) {
        Assert.notNull(timeUnit, "TimeUnit argument cannot be null.");
        this.value = value;
        this.timeUnit = timeUnit;
    }

    public long getValue() {
        return value;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    @Override
    public int compareTo(Duration duration) {
        long converted = timeUnit.convert(duration.getValue(), duration.getTimeUnit());
        return Long.compare(this.value, converted);
    }

    public boolean isLessThan(Duration duration) {
        return compareTo(duration) < 0;
    }

    public boolean isGreaterThan(Duration duration) {
        return compareTo(duration) > 0;
    }

    public boolean isEquivalentTo(Duration duration) {
        return compareTo(duration) == 0;
    }

    public String toString() {
        return value + " " + this.timeUnit.name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Duration duration = (Duration) o;

        return value == duration.value
            && timeUnit == duration.timeUnit;
    }

    @Override
    public int hashCode() {
        int result = (int) (value ^ (value >>> 32));
        result = 31 * result + (timeUnit != null ? timeUnit.hashCode() : 0);
        return result;
    }

    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
    public Duration clone() {
        try {
            return (Duration) super.clone();
        } catch (CloneNotSupportedException e) {
            //should never happen since we subclass Object directly:
            throw new InternalError("Unable to clone Object direct subclass! " + e.getMessage());
        }
    }
}
