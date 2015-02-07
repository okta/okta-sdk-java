import com.okta.sdk.exceptions.SdkException;
import com.okta.sdk.framework.Filter;
import com.okta.sdk.framework.Filters;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FilterTest {
    @Test
    public void TestFilter() throws Exception {
        Filter filter = new Filter()
                .where(Filters.User.STATUS)
                .equalTo("ACTIVE")
                .and()
                .where(Filters.User.LAST_UPDATED)
                .greaterThan(new DateTime(2014, 1, 1, 1, 1, DateTimeZone.forOffsetHours(-8)));

        Assert.assertEquals(filter.toString(), "status eq \"ACTIVE\" and lastUpdated gt \"2014-01-01T01:01:00.000-08:00\"");
    }

    @Test( expectedExceptions = { RuntimeException.class})
    public void TestFilterException() throws Exception {
        Filter filter = new Filter()
                .where(Filters.User.STATUS)
                .equalTo("ACTIVE")
                .or()
                .where(Filters.User.LAST_UPDATED)
                .greaterThan(new DateTime(2014, 1, 1, 1, 1, DateTimeZone.forOffsetHours(-8)));
        filter.toString();
    }
}
