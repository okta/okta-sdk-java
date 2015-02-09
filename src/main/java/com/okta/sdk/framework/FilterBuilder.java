package com.okta.sdk.framework;

import com.okta.sdk.exceptions.SdkException;
import org.joda.time.DateTime;

import java.util.HashSet;

public class FilterBuilder {
    public FilterBuilder() { }

    public FilterBuilder(String StringFilter) {
        STRING_BUILDER.append(StringFilter);
    }

    private final StringBuilder STRING_BUILDER = new StringBuilder();
    private final String EQUAL_SIGN = " eq ";
    private final String CONTAIN_SIGN = " co ";
    private final String STARTS_WITH_SIGN = " sw ";
    private final String PRESENT_SIGN = " pr";
    private final String GREATER_THAN_SIGN = " gt ";
    private final String GREATER_THAN_OR_EQUAL_SIGN = " ge ";
    private final String LESS_THAN_SIGN = " lt ";
    private final String LESS_THAN_OR_EQUAL_SIGN = " le ";
    private final String AND_SIGN = " and ";
    private final String OR_SIGN = " or ";

    private final HashSet<String> ATTRIBUTES = new HashSet<String>();
    private int orCount = 0;

    @Override
    public String toString() {
        return this.STRING_BUILDER.toString();
    }

    private boolean hasTooManyAttributesWithOr() {
        if (ATTRIBUTES.size() > 1 && orCount > 0) {
            SdkException sdkException = new SdkException("Cannot create a filter with two different attributes combined by \"or\"");
            throw new RuntimeException(sdkException);
        }
        return false;
    }

    public FilterBuilder where(String attr) {
        return this.attr(attr);
    }

    public FilterBuilder where(FilterBuilder filterBuilderBuilder) {
        ATTRIBUTES.addAll(filterBuilderBuilder.ATTRIBUTES);
        orCount += filterBuilderBuilder.orCount;
        hasTooManyAttributesWithOr(); // The nested filter could contain extra attributes
        STRING_BUILDER.append("(" + filterBuilderBuilder.toString() + ")");
        return this;
    }

    public FilterBuilder attr(String attr) {
        ATTRIBUTES.add(attr);
        hasTooManyAttributesWithOr(); // We could have added too many attributes
        STRING_BUILDER.append(attr);
        return this;
    }

    public FilterBuilder value(String value) {
        STRING_BUILDER.append('"' + value + '"');
        return this;
    }

    public FilterBuilder value(boolean value) {
        STRING_BUILDER.append(String.valueOf(value).toLowerCase());
        return this;
    }

    public FilterBuilder value(int value) {
        STRING_BUILDER.append(value);
        return this;
    }

    public FilterBuilder value(DateTime value) {
        STRING_BUILDER.append('"' + Utils.convertDateTimeToString(value) + '"');
        return this;
    }

    private FilterBuilder equalTo() {
        STRING_BUILDER.append(EQUAL_SIGN);
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

    private FilterBuilder contains() {
        STRING_BUILDER.append(CONTAIN_SIGN);
        return this;
    }

    public FilterBuilder contains(String value) {
        return contains().value(value);
    }

    public FilterBuilder contains(int value) {
        return contains().value(value);
    }

    private FilterBuilder startsWith() {
        STRING_BUILDER.append(STARTS_WITH_SIGN);
        return this;
    }

    public FilterBuilder startsWith(String value) {
        return startsWith().value(value);
    }

    public FilterBuilder startsWith(int value) {
        return startsWith().value(value);
    }

    public FilterBuilder present() {
        STRING_BUILDER.append(PRESENT_SIGN);
        return this;
    }

    public FilterBuilder present(String value) {
        return value(value).present();
    }

    private FilterBuilder greaterThan() {
        STRING_BUILDER.append(GREATER_THAN_SIGN);
        return this;
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

    private FilterBuilder greaterThanOrEqual() {
        STRING_BUILDER.append(GREATER_THAN_OR_EQUAL_SIGN);
        return this;
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

    private FilterBuilder lessThan() {
        STRING_BUILDER.append(LESS_THAN_SIGN);
        return this;
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
        STRING_BUILDER.append(LESS_THAN_OR_EQUAL_SIGN);
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
        STRING_BUILDER.append(AND_SIGN);
        return this;
    }

    public FilterBuilder and(FilterBuilder filterBuilder) {
        STRING_BUILDER.append(AND_SIGN);
        return where(filterBuilder);
    }

    public FilterBuilder or() {
        orCount++;
        STRING_BUILDER.append(OR_SIGN);
        return this;
    }

    public FilterBuilder or(FilterBuilder filterBuilder) {
        orCount++;
        STRING_BUILDER.append(OR_SIGN);
        return where(filterBuilder);
    }
}
