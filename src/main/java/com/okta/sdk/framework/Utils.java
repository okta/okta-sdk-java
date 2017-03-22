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
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Utils {

    /**
     * Global values for dateformatter and results limit.
     */
    protected static DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();
    protected static int DEFAULT_RESULTS_LIMIT = 200;

    /**
     * Returns the date formatted as a String.
     *
     * @param dateTime {@link DateTime}
     * @return {@link String}
     */
    public static String convertDateTimeToString(DateTime dateTime) {
        return DATE_TIME_FORMATTER.print(dateTime);
    }

    /**
     * Returns the date as a date object.
     *
     * @param dateTimeString {@link String}
     * @return {@link DateTime}
     */
    public static DateTime convertStringToDateTime(String dateTimeString) {
        return DATE_TIME_FORMATTER.parseDateTime(dateTimeString);
    }

    /**
     * Helper method to build filter for API calls.
     *
     * @param name {@link String}
     * @param objects {@link Object} array.
     * @return {@link FilterBuilder}
     */
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

    /**
     * Returns the current version of the Okta SDK.
     * @return {@link String}
     */
    public static String getSdkVersion() {
        return "0.0.4";
    }

    /**
     * Returns the default results limit.
     * @return {@link Integer} 
     */
    public static int getDefaultResultsLimit() {
        return DEFAULT_RESULTS_LIMIT;
    }
}
