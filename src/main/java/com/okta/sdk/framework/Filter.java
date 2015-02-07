package com.okta.sdk.framework;

import com.okta.sdk.exceptions.SdkException;
import org.joda.time.DateTime;

import java.util.HashSet;

public class Filter {
    public Filter() { }

    public Filter(String StringFilter) {
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
        try
        {
            hasTooManyAttributesWithOr();
        }
        catch (SdkException e){
            throw new RuntimeException(e);
        }

        return this.STRING_BUILDER.toString();
    }

    private boolean hasTooManyAttributesWithOr() throws SdkException{
        if (ATTRIBUTES.size() > 1 && orCount > 0) {
            throw new SdkException("Cannot create a filter with two different attributes combined by \"or\"");
        }
        return false;
    }

    public Filter where(String attr) {
        return this.attr(attr);
    }

    public Filter where(Filter filter) {
        STRING_BUILDER.append("(" + filter.toString() + ")");
        return this;
    }

    public Filter attr(String attr) {
        ATTRIBUTES.add(attr);
        STRING_BUILDER.append(attr);
        return this;
    }

    public Filter value(String value) {
        STRING_BUILDER.append('"' + value + '"');
        return this;
    }

    public Filter value(boolean value) {
        STRING_BUILDER.append(String.valueOf(value).toLowerCase());
        return this;
    }

    public Filter value(int value) {
        STRING_BUILDER.append(value);
        return this;
    }

    public Filter value(DateTime value) {
        STRING_BUILDER.append('"' + Utils.convertDateTimeToString(value) + '"');
        return this;
    }

    private Filter equalTo() {
        STRING_BUILDER.append(EQUAL_SIGN);
        return this;
    }

    public Filter equalTo(String value) {
        return equalTo().value(value);
    }

    public Filter equalTo(int value) {
        return equalTo().value(value);
    }

    public Filter equalTo(Boolean value) {
        return equalTo().value(value);
    }

    public Filter equalTo(DateTime value) {
        return equalTo().value(value);
    }

    private Filter contains() {
        STRING_BUILDER.append(CONTAIN_SIGN);
        return this;
    }

    public Filter contains(String value) {
        return contains().value(value);
    }

    public Filter contains(int value) {
        return contains().value(value);
    }

    private Filter startsWith() {
        STRING_BUILDER.append(STARTS_WITH_SIGN);
        return this;
    }

    public Filter startsWith(String value) {
        return startsWith().value(value);
    }

    public Filter startsWith(int value) {
        return startsWith().value(value);
    }

    public Filter present() {
        STRING_BUILDER.append(PRESENT_SIGN);
        return this;
    }

    public Filter present(String value) {
        return value(value).present();
    }

    private Filter greaterThan() {
        STRING_BUILDER.append(GREATER_THAN_SIGN);
        return this;
    }

    public Filter greaterThan(String value) {
        return greaterThan().value(value);
    }

    public Filter greaterThan(int value) {
        return greaterThan().value(value);
    }

    public Filter greaterThan(DateTime value) {
        return greaterThan().value(value);
    }

    private Filter greaterThanOrEqual() {
        STRING_BUILDER.append(GREATER_THAN_OR_EQUAL_SIGN);
        return this;
    }

    public Filter greaterThanOrEqual(String value) {
        return greaterThanOrEqual().value(value);
    }

    public Filter greaterThanOrEqual(int value) {
        return greaterThanOrEqual().value(value);
    }

    public Filter greaterThanOrEqual(DateTime value) {
        return greaterThanOrEqual().value(value);
    }

    private Filter lessThan() {
        STRING_BUILDER.append(LESS_THAN_SIGN);
        return this;
    }

    public Filter lessThan(String value) {
        return lessThan().value(value);
    }

    public Filter lessThan(int value) {
        return lessThan().value(value);
    }

    public Filter lessThan(DateTime value) {
        return lessThan().value(value);
    }

    private Filter lessThanOrEqual() {
        STRING_BUILDER.append(LESS_THAN_OR_EQUAL_SIGN);
        return this;
    }

    public Filter lessThanOrEqual(String value) {
        return lessThanOrEqual().value(value);
    }

    public Filter lessThanOrEqual(int value) {
        return lessThanOrEqual().value(value);
    }

    public Filter lessThanOrEqual(DateTime value) {
        return lessThanOrEqual().value(value);
    }

    public Filter and() {
        STRING_BUILDER.append(AND_SIGN);
        return this;
    }

    public Filter and(Filter filter) {
        STRING_BUILDER.append(AND_SIGN);
        return where(filter);
    }

    public Filter or() {
        orCount++;
        STRING_BUILDER.append(OR_SIGN);
        return this;
    }

    public Filter or(Filter filter) {
        orCount++;
        STRING_BUILDER.append(OR_SIGN);
        return where(filter);
    }
}
