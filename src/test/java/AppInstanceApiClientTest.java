import com.okta.sdk.clients.AppInstanceApiClient;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.models.apps.AppInstance;
import com.okta.sdk.models.apps.AppSettings;
import com.okta.sdk.models.apps.Settings;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Random;

public class AppInstanceApiClientTest {

    static ApiClientConfiguration oktaSettings;
    static AppInstanceApiClient appInstanceApiClient;
    static Random random;

    @BeforeClass
    public void setUp() throws Exception {
        oktaSettings = new ApiClientConfiguration(TestConstants.getUrlPath(), TestConstants.getApiKey());

        appInstanceApiClient = new AppInstanceApiClient(oktaSettings);

        random = new Random();
    }

    @Test
    public void testCRUD() throws Exception {
        // Create
        AppInstance newApp = new AppInstance();
        newApp.setName("template_sps");
        newApp.setLabel("Example app " + Integer.toString(random.nextInt()));
        newApp.setSignOnMode("SECURE_PASSWORD_STORE");

        Settings settings = new Settings();

        AppSettings appSettings = new AppSettings();

        String url = "https://example.com/sample.htm";
        appSettings.setUrl(url);

        appSettings.setUsernameField("username");
        appSettings.setPasswordField("password");
        appSettings.setButtonField("button");

        String optionalFieldName = "fieldName";
        String optionalFieldValue = "fieldValue";
        appSettings.setOptionalField1(optionalFieldName);
        appSettings.setOptionalField1Value(optionalFieldValue);

        settings.setAppSettings(appSettings);
        newApp.setSettings(settings);
        newApp = appInstanceApiClient.createAppInstance(newApp);

        Assert.assertNotNull(newApp.getId());

        // Read
        newApp = appInstanceApiClient.getAppInstance(newApp.getId());

        Assert.assertEquals(newApp.getSettings().getAppSettings().getUrl(), url);
        Assert.assertEquals(newApp.getSettings().getAppSettings().getOptionalField1(), optionalFieldName);
        Assert.assertEquals(newApp.getSettings().getAppSettings().getOptionalField1Value(), optionalFieldValue);

        // Update
        String nextUrl = "https://example.com/sample2.htm";
        newApp.getSettings().getAppSettings().setUrl(nextUrl);
        newApp = appInstanceApiClient.updateAppInstance(newApp.getId(), newApp);

        Assert.assertEquals(newApp.getSettings().getAppSettings().getUrl(), nextUrl);

        // Delete
        appInstanceApiClient.deactivateAppInstance(newApp.getId());
        appInstanceApiClient.deleteAppInstance(newApp.getId());
    }
}