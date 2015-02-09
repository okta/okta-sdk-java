package com.okta.sdk.framework;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Utils {
    protected static String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
    protected static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_TIME_PATTERN);
    protected static int DEFAULT_RESULTS_LIMIT = 200;

    public static String convertDateTimeToString(DateTime dateTime) {
        return DATE_TIME_FORMATTER.print(dateTime);
    }

    public static DateTime convertStringToDateTime(String dateTimeString) {
        return DATE_TIME_FORMATTER.parseDateTime(dateTimeString);
    }

    public static FilterBuilder getFilter(String name, Object... objects) {
        FilterBuilder filterBuilder = new FilterBuilder();
        for (int i = 0; i < objects.length; i++) {
            filterBuilder.where(name).equalTo(objects[i].toString());

            // If it's not the last item in the array
            if (i != objects.length - 1) {
                filterBuilder.or();
            }
        }
        return filterBuilder;
    }

    public static String getSdkVersion() {
        return "0.0.1";
    }

    public static int getDefaultResultsLimit() {
        return DEFAULT_RESULTS_LIMIT;
    }
}
