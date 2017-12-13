package com.okta.sdk.inject.cdi

import com.okta.sdk.client.Client
import org.testng.annotations.Test


import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

class ClientProducerTest extends CdiTestSupport {

    // TODO: figure out how to optionally add 'CustomClientPropertiesProducer' so we can test the optional flows
//    @Test
//    void basicInjectTest() {
//        Client client = getInstance(Client)
//        assertThat client, notNullValue()
//    }

    @Test
    void customClientProps() {
        Client client = getInstance(Client)
        assertThat client, notNullValue()
        assertThat client.dataStore.baseUrlResolver.getBaseUrl(), equalTo("https://oktatest.example.com")
    }


}
