import com.okta.sdk.framework.FilterBuilder;
import com.okta.sdk.framework.Filters;
import com.okta.sdk.framework.Utils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FilterBuilderTest {
    @Test
    public void TestFilter() throws Exception {
        FilterBuilder filterBuilder = new FilterBuilder()
                .where(Filters.User.STATUS)
                .equalTo("ACTIVE")
                .and()
                .where(Filters.User.LAST_UPDATED)
                .greaterThan(new DateTime(2014, 1, 1, 1, 1, DateTimeZone.forOffsetHours(-8)));

        Assert.assertEquals(filterBuilder.toString(), "status eq \"ACTIVE\" and lastUpdated gt \"2014-01-01T01:01:00.000-08:00\"");
    }

    @Test( expectedExceptions = { RuntimeException.class})
    public void TestFilterException() throws Exception {
        FilterBuilder filterBuilder = new FilterBuilder()
                .where(Filters.User.STATUS)
                .equalTo("ACTIVE")
                .or()
                .where(Filters.User.LAST_UPDATED)
                .greaterThan(new DateTime(2014, 1, 1, 1, 1, DateTimeZone.forOffsetHours(-8)));
        filterBuilder.toString();
    }

    @Test(expectedExceptions = { RuntimeException.class })
    public void TestTooManyNestedFieldsFilterException() throws Exception {
        FilterBuilder nestedFilterBuilder = new FilterBuilder()
                .where(Filters.User.STATUS)
                .equalTo("ACTIVE");

        FilterBuilder filterBuilder = new FilterBuilder()
                .where(Filters.User.LAST_UPDATED)
                .greaterThan(new DateTime(2014, 1, 1, 1, 1, DateTimeZone.forOffsetHours(-8)))
                .or(nestedFilterBuilder); // Should Throw an error here
    }

    @Test()
    public void TestMultipleFieldsWithOr() throws Exception {
        FilterBuilder filterBuilder = new FilterBuilder()
                .where(Filters.User.STATUS)
                .equalTo("LOCKED_OUT")
                .or()
                .where(Filters.User.STATUS) // Should be fine
                .equalTo("PASSWORD_EXPIRED");

        FilterBuilder filterBuilderWithUtil = Utils.getFilter(Filters.User.STATUS, "LOCKED_OUT", "PASSWORD_EXPIRED");
    }
}
