/*
 * Copyright 2020-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.tests.it

import com.okta.sdk.resource.network.zone.NetworkZone
import com.okta.sdk.resource.network.zone.NetworkZoneAddress
import com.okta.sdk.resource.network.zone.NetworkZoneAddressType
import com.okta.sdk.resource.network.zone.NetworkZoneStatus
import com.okta.sdk.resource.network.zone.NetworkZoneType
import com.okta.sdk.resource.threat.insight.ThreatInsightConfiguration
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.anyOf
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.iterableWithSize
import static org.hamcrest.Matchers.notNullValue

/**
 * Tests for {@code /api/v1/threats/configuration}.
 * @since 4.1.0
 */
class ThreatInsightConfigurationIT extends ITSupport {

    @Test
    void getThreatInsightConfigurationTest() {

        ThreatInsightConfiguration currentConfiguration = getClient().getCurrentConfiguration()
        assertThat(currentConfiguration, notNullValue())
        assertThat(currentConfiguration.getAction(), anyOf(is("none"), is("audit"), is("block")))
        assertThat(currentConfiguration.getLinks(), notNullValue())
        assertThat(currentConfiguration.getLinks().containsKey("self"), equalTo(true))
        assertThat(currentConfiguration.getLinks().get("self").get("href"), notNullValue())
        assertThat(currentConfiguration.getLinks().get("self").get("hints"), notNullValue())
        assertThat(currentConfiguration.getExcludeZones(), notNullValue())
    }

    @Test
    void updateThreatInsightConfigurationWithNetworkZoneTest() {

        def networkZoneName = "network-zone-it-${uniqueTestName}"

        NetworkZone networkZone = getClient().instantiate(NetworkZone)
            .setType(NetworkZoneType.IP)
            .setName(networkZoneName)
            .setStatus(NetworkZoneStatus.ACTIVE)
            .setGateways(Arrays.asList(
                getClient().instantiate(NetworkZoneAddress)
                .setType(NetworkZoneAddressType.CIDR)
                .setValue("1.2.3.4/24")
            ))
            .setProxies(Arrays.asList(
                getClient().instantiate(NetworkZoneAddress)
                    .setType(NetworkZoneAddressType.CIDR)
                    .setValue("3.3.4.5/24")
            ))

        def createdNetworkZone = getClient().createNetworkZone(networkZone)
        assertThat(createdNetworkZone, notNullValue())
        assertThat(createdNetworkZone.getId(), notNullValue())
        registerForCleanup(createdNetworkZone)

        ThreatInsightConfiguration currentConfiguration = getClient().getCurrentConfiguration()
        def prevActionValue = currentConfiguration.getAction()
        String newActionValue
        if(prevActionValue.equals("audit")) {
            newActionValue = "none"
        } else if(prevActionValue.equals("none")) {
            newActionValue = "audit"
        } else {
            newActionValue = prevActionValue
        }
        currentConfiguration.setAction(newActionValue)
        currentConfiguration.setExcludeZones(Arrays.asList(createdNetworkZone.getId()))

        ThreatInsightConfiguration updatedConfiguration = currentConfiguration.update()
        assertThat(updatedConfiguration, notNullValue())
        assertThat(updatedConfiguration.getAction(), equalTo(newActionValue))
        assertThat(updatedConfiguration.getExcludeZones(), iterableWithSize(1))
        assertThat(updatedConfiguration.getExcludeZones().get(0), equalTo(createdNetworkZone.getId()))

        //restore ThreatInsightConfiguration's action
        updatedConfiguration.setAction(prevActionValue)
        updatedConfiguration.setExcludeZones(Arrays.asList())
        ThreatInsightConfiguration restoredConfiguration = updatedConfiguration.update()
        assertThat(restoredConfiguration, notNullValue())
        assertThat(restoredConfiguration.getAction(), equalTo(prevActionValue))
        assertThat(restoredConfiguration.getExcludeZones(), iterableWithSize(0))
    }
}


