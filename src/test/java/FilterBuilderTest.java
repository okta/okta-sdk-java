import com.okta.sdk.framework.FilterBuilder;
import com.okta.sdk.framework.Filters;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FilterBuilderTest {

    @Test
    public void testFilterWithAndOperatorAndMultipleAttributes() {
        FilterBuilder filterBuilder = new FilterBuilder()
                .where(Filters.User.STATUS)
                .equalTo("ACTIVE")
                .and()
                .where(Filters.User.LAST_UPDATED)
                .greaterThan(new DateTime(2014, 1, 1, 1, 1, DateTimeZone.forOffsetHours(-8)));

        assertThat(filterBuilder.toString(), is("status eq \"ACTIVE\" and lastUpdated gt \"2014-01-01T01:01:00.000-08:00\""));
    }

    @Test
    public void testFilterWithOrOperatorAndMultipleAttributes() {
        FilterBuilder filterBuilder = new FilterBuilder()
                .where(Filters.User.STATUS)
                .equalTo("LOCKED_OUT")
                .or()
                .where(Filters.User.LAST_UPDATED)
                .lessThan(new DateTime(2014, 1, 1, 1, 1, DateTimeZone.forOffsetHours(-8)));

        assertThat(filterBuilder.toString(), is("status eq \"LOCKED_OUT\" or lastUpdated lt \"2014-01-01T01:01:00.000-08:00\""));
    }

    @Test
    public void testFilterWithComplexFilter() throws Exception {
        FilterBuilder filterBuilder = new FilterBuilder()
                .where(Filters.User.STATUS).equalTo("ACTIVE").and()
                .where(Filters.User.STATUS).equalTo("ACTIVE").and()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").and()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").and()
                .where(Filters.User.STATUS).equalTo("ACTIVE").and()
                .where(Filters.User.STATUS).equalTo("ACTIVE").and()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").and()
                .where(Filters.User.STATUS).equalTo("ACTIVE").and()
                .where(Filters.User.STATUS).equalTo("ACTIVE").and()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE");
        assertThat(filterBuilder.toString(), is("status eq \"ACTIVE\" and status eq \"ACTIVE\"" +
                " and status eq \"ACTIVE\" or status eq \"ACTIVE\" or status eq \"ACTIVE\" or " +
                "status eq \"ACTIVE\" and status eq \"ACTIVE\" or status eq \"ACTIVE\" or status" +
                " eq \"ACTIVE\" or status eq \"ACTIVE\" and status eq \"ACTIVE\" and status eq" +
                " \"ACTIVE\" and status eq \"ACTIVE\" or status eq \"ACTIVE\" or status eq \"ACTIVE\"" +
                " or status eq \"ACTIVE\" and status eq \"ACTIVE\" and status eq \"ACTIVE\" and " +
                "status eq \"ACTIVE\" or status eq \"ACTIVE\" or status eq \"ACTIVE\""));    }

    @Test
    public void testFilterWithComplexFilterUsingNestedFields() throws Exception {
        FilterBuilder filterBuilder = new FilterBuilder()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").and()
                .where(Filters.User.STATUS).equalTo("ACTIVE").and()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE");
        filterBuilder = new FilterBuilder(filterBuilder.toString()).or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").and()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").and()
                .where(Filters.User.STATUS).equalTo("ACTIVE").and()
                .where(Filters.User.STATUS).equalTo("ACTIVE").and()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE").or()
                .where(Filters.User.STATUS).equalTo("ACTIVE");
        assertThat(filterBuilder.toString(), is("status eq \"ACTIVE\" or status eq \"ACTIVE\"" +
                " or status eq \"ACTIVE\" and status eq \"ACTIVE\" and status eq \"ACTIVE\" or " +
                "status eq \"ACTIVE\" or status eq \"ACTIVE\" or status eq \"ACTIVE\" or status" +
                " eq \"ACTIVE\" or status eq \"ACTIVE\" or status eq \"ACTIVE\" and status eq" +
                " \"ACTIVE\" or status eq \"ACTIVE\" or status eq \"ACTIVE\" or status eq \"ACTIVE\"" +
                " and status eq \"ACTIVE\" and status eq \"ACTIVE\" and status eq \"ACTIVE\" or status " +
                "eq \"ACTIVE\" or status eq \"ACTIVE\" or status eq \"ACTIVE\""));
    }

}
