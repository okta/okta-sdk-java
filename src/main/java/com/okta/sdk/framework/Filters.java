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
