package com.okta.sdk.framework;

import org.joda.time.DateTime;

public class FilterBuilder {

    // Boolean operators
    private static final String EQUAL_SIGN = " eq ";
    private static final String CONTAIN_SIGN = " co ";
    private static final String STARTS_WITH_SIGN = " sw ";
    private static final String PRESENT_SIGN = " pr";
    private static final String GREATER_THAN_SIGN = " gt ";
    private static final String GREATER_THAN_OR_EQUAL_SIGN = " ge ";
    private static final String LESS_THAN_SIGN = " lt ";
    private static final String LESS_THAN_OR_EQUAL_SIGN = " le ";

    // Logical operators
    private static final String AND_SIGN = " and ";
    private static final String OR_SIGN = " or ";

    private final StringBuilder filterBuilder = new StringBuilder();

    public FilterBuilder() { }

    public FilterBuilder(String filter) {
        filterBuilder.append(filter);
    }

    @Override
    public String toString() {
        return this.filterBuilder.toString();
    }

    public FilterBuilder where(String attr) {
        return this.attr(attr);
    }

    public FilterBuilder where(FilterBuilder filterBuilder) {
        this.filterBuilder.append("(" + filterBuilder.toString() + ")");
        return this;
    }

    public FilterBuilder attr(String attr) {
        filterBuilder.append(attr);
        return this;
    }

    public FilterBuilder value(String value) {
        filterBuilder.append('"' + value + '"');
        return this;
    }

    public FilterBuilder value(boolean value) {
        filterBuilder.append(String.valueOf(value).toLowerCase());
        return this;
    }

    public FilterBuilder value(int value) {
        filterBuilder.append(value);
        return this;
    }

    public FilterBuilder value(DateTime value) {
        filterBuilder.append('"' + Utils.convertDateTimeToString(value) + '"');
        return this;
    }

    public FilterBuilder equalTo(String value) {
        return equalTo().value(value);
    }

    public FilterBuilder equalTo(int value) {
        return equalTo().value(value);
    }

    public FilterBuilder equalTo(Boolean value) {
        return equalTo().value(value);
    }

    public FilterBuilder equalTo(DateTime value) {
        return equalTo().value(value);
    }

    public FilterBuilder contains(String value) {
        return contains().value(value);
    }

    public FilterBuilder contains(int value) {
        return contains().value(value);
    }

    public FilterBuilder startsWith(String value) {
        return startsWith().value(value);
    }

    public FilterBuilder startsWith(int value) {
        return startsWith().value(value);
    }

    public FilterBuilder present() {
        filterBuilder.append(PRESENT_SIGN);
        return this;
    }

    public FilterBuilder present(String value) {
        return value(value).present();
    }

    public FilterBuilder greaterThan(String value) {
        return greaterThan().value(value);
    }

    public FilterBuilder greaterThan(int value) {
        return greaterThan().value(value);
    }

    public FilterBuilder greaterThan(DateTime value) {
        return greaterThan().value(value);
    }

    public FilterBuilder greaterThanOrEqual(String value) {
        return greaterThanOrEqual().value(value);
    }

    public FilterBuilder greaterThanOrEqual(int value) {
        return greaterThanOrEqual().value(value);
    }

    public FilterBuilder greaterThanOrEqual(DateTime value) {
        return greaterThanOrEqual().value(value);
    }

    public FilterBuilder lessThan(String value) {
        return lessThan().value(value);
    }

    public FilterBuilder lessThan(int value) {
        return lessThan().value(value);
    }

    public FilterBuilder lessThan(DateTime value) {
        return lessThan().value(value);
    }

    private FilterBuilder lessThanOrEqual() {
        filterBuilder.append(LESS_THAN_OR_EQUAL_SIGN);
        return this;
    }

    public FilterBuilder lessThanOrEqual(String value) {
        return lessThanOrEqual().value(value);
    }

    public FilterBuilder lessThanOrEqual(int value) {
        return lessThanOrEqual().value(value);
    }

    public FilterBuilder lessThanOrEqual(DateTime value) {
        return lessThanOrEqual().value(value);
    }

    public FilterBuilder and() {
        filterBuilder.append(AND_SIGN);
        return this;
    }

    public FilterBuilder and(FilterBuilder filterBuilder) {
        this.filterBuilder.append(AND_SIGN);
        return where(filterBuilder);
    }

    public FilterBuilder or() {
        filterBuilder.append(OR_SIGN);
        return this;
    }

    public FilterBuilder or(FilterBuilder filterBuilder) {
        this.filterBuilder.append(OR_SIGN);
        return where(filterBuilder);
    }

    private FilterBuilder equalTo() {
        filterBuilder.append(EQUAL_SIGN);
        return this;
    }

    private FilterBuilder contains() {
        filterBuilder.append(CONTAIN_SIGN);
        return this;
    }

    private FilterBuilder startsWith() {
        filterBuilder.append(STARTS_WITH_SIGN);
        return this;
    }

    private FilterBuilder greaterThan() {
        filterBuilder.append(GREATER_THAN_SIGN);
        return this;
    }

    private FilterBuilder greaterThanOrEqual() {
        filterBuilder.append(GREATER_THAN_OR_EQUAL_SIGN);
        return this;
    }

    private FilterBuilder lessThan() {
        filterBuilder.append(LESS_THAN_SIGN);
        return this;
    }

}
