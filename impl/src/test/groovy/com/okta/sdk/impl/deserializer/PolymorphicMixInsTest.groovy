package com.okta.sdk.impl.deserializer

import com.okta.sdk.impl.client.DefaultClientBuilder
import com.okta.sdk.impl.test.RestoreEnvironmentVariables
import com.okta.sdk.impl.test.RestoreSystemProperties
import com.okta.sdk.resource.model.AwsRegion
import com.okta.sdk.resource.model.LogStream
import com.okta.sdk.resource.model.LogStreamAws
import com.okta.sdk.resource.model.LogStreamSettingsAws
import com.okta.sdk.resource.model.LogStreamSettingsSplunk
import com.okta.sdk.resource.model.LogStreamSplunk
import com.okta.sdk.resource.model.LogStreamType
import com.okta.sdk.resource.model.SplunkEdition
import org.testng.annotations.Listeners
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.core.IsEqual.equalTo
import static org.hamcrest.core.IsInstanceOf.instanceOf

@Listeners([RestoreSystemProperties, RestoreEnvironmentVariables])
class PolymorphicMixInsTest {

    def mapper = new DefaultClientBuilder().build().getObjectMapper()

    @Test
    void testLogStreamAws() {
        def settings = new LogStreamSettingsAws()
                .accountId("123456")
                .region(AwsRegion.US_EAST_1)
                .eventSourceName("source")
        def logStream = new LogStreamAws()
                .settings(settings)
                .type(LogStreamType.AWS_EVENTBRIDGE)
                .name("test")
        def asString = mapper.writeValueAsString(logStream)
        def afterRoundTrip = mapper.readValue(asString, LogStream.class)
        assertThat(afterRoundTrip, instanceOf(LogStreamAws.class))
        assertThat(((LogStreamAws)afterRoundTrip).settings, equalTo(settings))
    }

    @Test
    void testLogStreamSplunk() {
        def settings = new LogStreamSettingsSplunk()
                .edition(SplunkEdition.AWS)
                .host("host")
                .token("token")
        def logStream = new LogStreamSplunk()
                .settings(settings)
                .type(LogStreamType.SPLUNK_CLOUD_LOGSTREAMING)
                .name("test")
        def asString = mapper.writeValueAsString(logStream)
        def afterRoundTrip = mapper.readValue(asString, LogStream.class)
        assertThat(afterRoundTrip, instanceOf(LogStreamSplunk.class))
        assertThat(((LogStreamSplunk)afterRoundTrip).settings, equalTo(settings))
    }

}
