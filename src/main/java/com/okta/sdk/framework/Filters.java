/*!
 * Copyright (c) 2015-2016, Okta, Inc. and/or its affiliates. All rights reserved.
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

public class Filters {

    public static class User {
        public final static String ID = "id";
        public final static String STATUS = "status";
        public final static String LAST_UPDATED = "lastUpdated";
    }

    public static class AppUser {
        public final static String ORIGIN = "scope";
    }

    public static class AppInstance {
        public final static String STATUS = "status";
        public final static String GROUP_ID = "group.id";
        public final static String USER_ID = "user.id";
    }

    public static class OrgAuthFactor {
        public final static String STATUS = "status";
    }

}
