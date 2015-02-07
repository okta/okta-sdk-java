public class TestConstants {

    private static String urlPath = System.getenv("OKTA_TEST_URL");
    private static String apiKey = System.getenv("OKTA_TEST_KEY");
    private static String adminUserName = System.getenv("OKTA_TEST_ADMIN_NAME");
    private static String adminPassword = System.getenv("OKTA_TEST_ADMIN_PASSWORD");

    public static String getUrlPath() {
        return urlPath;
    }

    public static void setUrlPath(String urlPath) {
        TestConstants.urlPath = urlPath;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static void setApiKey(String apiKey) {
        TestConstants.apiKey = apiKey;
    }

    public static String getAdminUserName() {
        return adminUserName;
    }

    public static void setAdminUserName(String adminUserName) {
        TestConstants.adminUserName = adminUserName;
    }

    public static String getAdminPassword() {
        return adminPassword;
    }

    public static void setAdminPassword(String adminPassword) {
        TestConstants.adminPassword = adminPassword;
    }
}
