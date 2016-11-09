import com.okta.sdk.clients.LogsApiClient;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.FilterBuilder;
import org.hamcrest.core.StringContains;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;

public class LogsApiClientTest {

    // ApiClientConfiguration
    private static final String BASE_URL = "https://some-org.com";
    private static final String API_TOKEN = "someApiToken";

    // Query Parameters and Validation . Note that order is not guaranteed so StringContains needs to be used
    // StartTime parameter
    private static final long UNIX_TIME_START = 1477353600000L; // Oct 25 2016 00:00:00 GMT
    private static final String SINCE_SUBSTRING = "since=2016-10-25T00%3A00%3A00.000Z";

    // limit parameter
    private static final int LIMIT = 10;
    private static final String LIMIT_SUBSTRING = "limit=10";

    // filterBuilder parameter
    private static final FilterBuilder FILTER_BUILDER = new FilterBuilder().where("someField").equalTo("someValue");
    private static final String FILTER_BUILDER_SUBSTRING = "filter=someField+eq+%22someValue%22";

    // search parameter
    private static final String SEARCH = "some search value";
    private static final String SEARCH_SUBSTRING = "q=some+search+value";

    // after parameter
    private static final String AFTER_VALUE = "some_after_value";
    private static final String AFTER_VALUE_SUBSTRING = "after=some_after_value";

    private LogsApiClient logsApiClient;

    @BeforeTest
    public void setUp() {
        logsApiClient = new LogsApiClient(
                new ApiClientConfiguration(
                        BASE_URL,
                        API_TOKEN
                )
        );
    }

    @Test
    public void testOnlyStartDatetimeCreatesCorrectUri() throws IOException {
        String res = logsApiClient.getApiUri(new DateTime(UNIX_TIME_START), null, null, null, LIMIT);
        assertThat(res, new StringContains(SINCE_SUBSTRING));
        assertThat(res, new StringContains(LIMIT_SUBSTRING));
    }

    @Test
    public void testStartDatetimeWithFilterCreatesCorrectUri() throws IOException {
        String res = logsApiClient.getApiUri(new DateTime(UNIX_TIME_START), FILTER_BUILDER, null, null, LIMIT);
        assertThat(res, new StringContains(SINCE_SUBSTRING));
        assertThat(res, new StringContains(LIMIT_SUBSTRING));
        assertThat(res, new StringContains(FILTER_BUILDER_SUBSTRING));
    }

    @Test
    public void testStartDatetimeWithSearchCreatesCorrectUri() throws IOException {
        String res = logsApiClient.getApiUri(new DateTime(UNIX_TIME_START), null, SEARCH, null, LIMIT);
        assertThat(res, new StringContains(SINCE_SUBSTRING));
        assertThat(res, new StringContains(LIMIT_SUBSTRING));
        assertThat(res, new StringContains(SEARCH_SUBSTRING));
    }

    @Test
    public void testStartDatetimeWithAfterCreatesCorrectUri() throws IOException {
        String res = logsApiClient.getApiUri(new DateTime(UNIX_TIME_START), null, null, AFTER_VALUE, LIMIT);
        assertThat(res, new StringContains(SINCE_SUBSTRING));
        assertThat(res, new StringContains(LIMIT_SUBSTRING));
        assertThat(res, new StringContains(AFTER_VALUE_SUBSTRING));
    }

    @Test
    public void testStartDatetimeWithFilterAndAfterCreatesCorrectUri() throws IOException {
        String res = logsApiClient.getApiUri(new DateTime(UNIX_TIME_START), FILTER_BUILDER, null, AFTER_VALUE, LIMIT);
        assertThat(res, new StringContains(SINCE_SUBSTRING));
        assertThat(res, new StringContains(LIMIT_SUBSTRING));
        assertThat(res, new StringContains(FILTER_BUILDER_SUBSTRING));
        assertThat(res, new StringContains(AFTER_VALUE_SUBSTRING));
    }

    @Test
    public void testStartDatetimeWithFilterAndSearchCreatesCorrectUri() throws IOException {
        String res = logsApiClient.getApiUri(new DateTime(UNIX_TIME_START), null, SEARCH, AFTER_VALUE, LIMIT);
        assertThat(res, new StringContains(SINCE_SUBSTRING));
        assertThat(res, new StringContains(LIMIT_SUBSTRING));
        assertThat(res, new StringContains(SEARCH_SUBSTRING));
        assertThat(res, new StringContains(AFTER_VALUE_SUBSTRING));
    }
}
