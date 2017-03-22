/*!
 * Copyright (c) 2015-2017, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.okta.sdk.framework;

import org.joda.time.DateTime;

public class FilterBuilder {

    /**
     * Static boolean operators for creating filter queries.
     */
    private static final String EQUAL_SIGN = " eq ";
    private static final String CONTAIN_SIGN = " co ";
    private static final String STARTS_WITH_SIGN = " sw ";
    private static final String PRESENT_SIGN = " pr";
    private static final String GREATER_THAN_SIGN = " gt ";
    private static final String GREATER_THAN_OR_EQUAL_SIGN = " ge ";
    private static final String LESS_THAN_SIGN = " lt ";
    private static final String LESS_THAN_OR_EQUAL_SIGN = " le ";

    /**
     * Static logical operators for creating filter queries.
     */
    private static final String AND_SIGN = " and ";
    private static final String OR_SIGN = " or ";

    /**
     * Value where the filter is stored.
     */
    private final StringBuilder filterBuilder = new StringBuilder();

    /**
     * Default constructor for the FilterBuilder class.
     */
    public FilterBuilder() { }

    /**
     * Constructor to create a new FilterBuilder
     * @param filter {@link String}
     */
    public FilterBuilder(String filter) {
        filterBuilder.append(filter);
    }

    /**
     * Overriding method to return String representation
     * of the FilterBuilder.
     *
     * @return {@link String}
     */
    @Override
    public String toString() {
        return this.filterBuilder.toString();
    }

    /**
     * Comparison method.
     *
     * @param attr {@link String}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder where(String attr) {
        return this.attr(attr);
    }

    /**
     * Comparison method.
     *
     * @param filterBuilder {@link FilterBuilder}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder where(FilterBuilder filterBuilder) {
        this.filterBuilder.append("(" + filterBuilder.toString() + ")");
        return this;
    }

    /**
     * Helper method to add new attribute to filter.
     *
     * @param attr {@link String}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder attr(String attr) {
        filterBuilder.append(attr);
        return this;
    }

    /**
     * Helper method to add new value to filter.
     *
     * @param value {@link String}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder value(String value) {
        filterBuilder.append('"' + value + '"');
        return this;
    }

    /**
     * Helper method to add new value to filter.
     *
     * @param value {@link Boolean}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder value(boolean value) {
        filterBuilder.append(String.valueOf(value).toLowerCase());
        return this;
    }

    /**
     * Helper method to add new value to filter.
     *
     * @param value {@link Integer}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder value(int value) {
        filterBuilder.append(value);
        return this;
    }

    /**
     * Helper method to add new value to filter.
     *
     * @param value {@link DateTime}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder value(DateTime value) {
        filterBuilder.append('"' + Utils.convertDateTimeToString(value) + '"');
        return this;
    }

    /**
     * Comparison method for filter.
     *
     * @param value {@link String}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder equalTo(String value) {
        return equalTo().value(value);
    }

    /**
     * Comparison method for filter.
     *
     * @param value {@link Integer}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder equalTo(int value) {
        return equalTo().value(value);
    }

    /**
     * Comparison method for filter.
     *
     * @param value {@link Boolean}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder equalTo(Boolean value) {
        return equalTo().value(value);
    }

    /**
     * Comparison method for filter.
     *
     * @param value {@link DateTime}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder equalTo(DateTime value) {
        return equalTo().value(value);
    }

    /**
     * Contains method for filter.
     *
     * @param value {@link String}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder contains(String value) {
        return contains().value(value);
    }

    /**
     * Contains method for filter.
     *
     * @param value {@link Integer}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder contains(int value) {
        return contains().value(value);
    }

    /**
     * Comparison method for filter to check
     * if the value starts with the param.
     *
     * @param value {@link String}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder startsWith(String value) {
        return startsWith().value(value);
    }

    /**
     * Comparison method for filter to check
     * if the value starts with the param.
     *
     * @param value {@link Integer}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder startsWith(int value) {
        return startsWith().value(value);
    }

    /**
     * Helper method to include "pr" in filter.
     *
     * @return {@link FilterBuilder}
     */
    public FilterBuilder present() {
        filterBuilder.append(PRESENT_SIGN);
        return this;
    }

    /**
     * Helper method to set present value in filter.
     *
     * @param value {@link String}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder present(String value) {
        return value(value).present();
    }

    /**
     * Helper method to check if greater than supplied String value.
     *
     * @param value {@link String}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder greaterThan(String value) {
        return greaterThan().value(value);
    }

    /**
     * Helper method to check if greater than supplied int value.
     *
     * @param value {@link Integer}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder greaterThan(int value) {
        return greaterThan().value(value);
    }

    /**
     * Helper method to check if greater than supplied date.
     *
     * @param value {@link DateTime}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder greaterThan(DateTime value) {
        return greaterThan().value(value);
    }

    /**
     * Helper method to check if greater than or equal
     * to the supplied value.
     *
     * @param value {@link String}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder greaterThanOrEqual(String value) {
        return greaterThanOrEqual().value(value);
    }

    /**
     * Helper method to check if greater than or equal
     * to the supplied value.
     *
     * @param value {@link Integer}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder greaterThanOrEqual(int value) {
        return greaterThanOrEqual().value(value);
    }

    /**
     * Helper method to check if greater than or equal
     * to the supplied value.
     *
     * @param value {@link DateTime}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder greaterThanOrEqual(DateTime value) {
        return greaterThanOrEqual().value(value);
    }

    /**
     * Helper method to check if less than
     * the supplied value.
     *
     * @param value {@link String}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder lessThan(String value) {
        return lessThan().value(value);
    }

    /**
     * Helper method to check if less than
     * the supplied value.
     *
     * @param value {@link Integer}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder lessThan(int value) {
        return lessThan().value(value);
    }

    /**
     * Helper method to check if less than
     * the supplied value.
     *
     * @param value {@link DateTime}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder lessThan(DateTime value) {
        return lessThan().value(value);
    }

    /**
     * Helper method to check if less than or equal to.
     *
     * @return {@link FilterBuilder}
     */
    private FilterBuilder lessThanOrEqual() {
        filterBuilder.append(LESS_THAN_OR_EQUAL_SIGN);
        return this;
    }

    /**
     * Helper method to check if less than or equal
     * to the supplied value.
     *
     * @param value {@link String}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder lessThanOrEqual(String value) {
        return lessThanOrEqual().value(value);
    }

    /**
     * Helper method to check if less than or equal
     * to the supplied value.
     *
     * @param value {@link Integer}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder lessThanOrEqual(int value) {
        return lessThanOrEqual().value(value);
    }

    /**
     * Helper method to check if less than or equal
     * to the supplied value.
     *
     * @param value {@link DateTime}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder lessThanOrEqual(DateTime value) {
        return lessThanOrEqual().value(value);
    }

    /**
     * Helper method to include "and" in filter.
     *
     * @return {@link FilterBuilder}
     */
    public FilterBuilder and() {
        filterBuilder.append(AND_SIGN);
        return this;
    }

    /**
     * Helper method to include "and" in filter.
     *
     * @param filterBuilder {@link FilterBuilder}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder and(FilterBuilder filterBuilder) {
        this.filterBuilder.append(AND_SIGN);
        return where(filterBuilder);
    }

    /**
     * Helper method to include "or" in filter.
     *
     * @return {@link FilterBuilder}
     */
    public FilterBuilder or() {
        filterBuilder.append(OR_SIGN);
        return this;
    }

    /**
     * Helper method to include "or" in filter.
     *
     * @param filterBuilder {@link FilterBuilder}
     * @return {@link FilterBuilder}
     */
    public FilterBuilder or(FilterBuilder filterBuilder) {
        this.filterBuilder.append(OR_SIGN);
        return where(filterBuilder);
    }

    /**
     * Helper method to include "eq" in filter.
     *
     * @return {@link FilterBuilder}
     */
    private FilterBuilder equalTo() {
        filterBuilder.append(EQUAL_SIGN);
        return this;
    }

    /**
     * Helper method to include "co" in filter.
     *
     * @return {@link FilterBuilder}
     */
    private FilterBuilder contains() {
        filterBuilder.append(CONTAIN_SIGN);
        return this;
    }

    /**
     * Helper method to include "sw" in filter.
     *
     * @return {@link FilterBuilder}
     */
    private FilterBuilder startsWith() {
        filterBuilder.append(STARTS_WITH_SIGN);
        return this;
    }

    /**
     * Helper method to include "gt" in filter.
     *
     * @return {@link FilterBuilder}
     */
    private FilterBuilder greaterThan() {
        filterBuilder.append(GREATER_THAN_SIGN);
        return this;
    }

    /**
     * Helper method to include "ge" in filter.
     *
     * @return {@link FilterBuilder}
     */
    private FilterBuilder greaterThanOrEqual() {
        filterBuilder.append(GREATER_THAN_OR_EQUAL_SIGN);
        return this;
    }

    /**
     * Helper method to include "lt" in filter.
     *
     * @return {@link FilterBuilder}
     */
    private FilterBuilder lessThan() {
        filterBuilder.append(LESS_THAN_SIGN);
        return this;
    }

}
