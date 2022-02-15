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

import com.okta.sdk.resource.NetworkZone
import com.okta.sdk.resource.NetworkZoneAddress
import com.okta.sdk.resource.NetworkZoneAddressType
import com.okta.sdk.resource.NetworkZoneStatus
import com.okta.sdk.resource.NetworkZoneType
import com.okta.sdk.resource.NetworkZoneUsage
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.assertNotPresent
import static com.okta.sdk.tests.it.util.Util.assertPresent
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/zones}.
 * @since 4.1.0
 */
class NetworkZoneIT extends ITSupport {

    @Test (groups = "group2")
    void listNetworkZonesTest() {

        client.listNetworkZones()
            .forEach({ networkZone ->
                assertThat(networkZone.getId(), notNullValue())
                assertThat(networkZone.getName(), notNullValue())
                assertThat(networkZone.getType(), notNullValue())
                assertThat(networkZone.getType() instanceof NetworkZoneType, equalTo(true))
                assertThat(networkZone.getStatus(), notNullValue())
                assertThat(networkZone.getStatus() instanceof NetworkZoneStatus, equalTo(true))
                assertThat(networkZone.getUsage(), notNullValue())
                assertThat(networkZone.getUsage() instanceof NetworkZoneUsage, equalTo(true))
            })
    }

    @Test (groups = "group2")
    void createAndDeleteNetworkZoneTest() {

        String networkZoneName = SPINE_NAME_PREFIX + "${uniqueTestName}"

        NetworkZone networkZone = client.instantiate(NetworkZone)
            .setType(NetworkZoneType.IP)
            .setName(networkZoneName)
            .setStatus(NetworkZoneStatus.ACTIVE)
            .setGateways(Arrays.asList(
                client.instantiate(NetworkZoneAddress)
                    .setType(NetworkZoneAddressType.CIDR)
                    .setValue("1.2.3.4/24"),
                client.instantiate(NetworkZoneAddress)
                    .setType(NetworkZoneAddressType.CIDR)
                    .setValue("2.3.4.5/24")
            ))
            .setProxies(Arrays.asList(
                client.instantiate(NetworkZoneAddress)
                    .setType(NetworkZoneAddressType.CIDR)
                    .setValue("2.2.3.4/24"),
                client.instantiate(NetworkZoneAddress)
                    .setType(NetworkZoneAddressType.CIDR)
                    .setValue("3.3.4.5/24")
            ))

        def createdNetworkZone = client.createNetworkZone(networkZone)
        assertThat(createdNetworkZone, notNullValue())
        registerForCleanup(createdNetworkZone)

        assertThat(createdNetworkZone.getType(), equalTo(NetworkZoneType.IP))
        assertThat(createdNetworkZone.getName(), equalTo(networkZoneName))
        assertThat(createdNetworkZone.getStatus(), equalTo(NetworkZoneStatus.ACTIVE))
        assertThat(createdNetworkZone.getGateways(), iterableWithSize(networkZone.getGateways().size()))
        assertThat(createdNetworkZone.getProxies(), iterableWithSize(networkZone.getProxies().size()))
        assertPresent(client.listNetworkZones(), createdNetworkZone)

        client.deleteNetworkZone(createdNetworkZone.getId())
        assertNotPresent(client.listNetworkZones(), createdNetworkZone)
    }

    @Test (groups = "group2")
    void updateNetworkZoneTest() {

        String networkZoneName = SPINE_NAME_PREFIX + "${uniqueTestName}"

        NetworkZone networkZone = client.instantiate(NetworkZone)
            .setType(NetworkZoneType.IP)
            .setName(networkZoneName)
            .setStatus(NetworkZoneStatus.ACTIVE)
            .setGateways(Arrays.asList(
                client.instantiate(NetworkZoneAddress)
                    .setType(NetworkZoneAddressType.CIDR)
                    .setValue("1.2.3.4/24"),
                client.instantiate(NetworkZoneAddress)
                    .setType(NetworkZoneAddressType.CIDR)
                    .setValue("2.3.4.5/24")
            ))
            .setProxies(Arrays.asList(
                client.instantiate(NetworkZoneAddress)
                    .setType(NetworkZoneAddressType.CIDR)
                    .setValue("2.2.3.4/24"),
                client.instantiate(NetworkZoneAddress)
                    .setType(NetworkZoneAddressType.CIDR)
                    .setValue("3.3.4.5/24")
            ))

        def createdNetworkZone = client.createNetworkZone(networkZone)
        assertThat(createdNetworkZone, notNullValue())
        registerForCleanup(createdNetworkZone)

        createdNetworkZone.setGateways(
            networkZone.getGateways().stream()
                .filter({ x -> (x.getValue() == "2.3.4.5/24") })
                .collect().asList()
        )
        createdNetworkZone.setProxies(
            networkZone.getProxies().stream()
                .filter({ x -> (x.getValue() == "3.3.4.5/24") })
                .collect().asList()
        )
        client.updateNetworkZone(createdNetworkZone, createdNetworkZone.getId())

        def updatedNetworkZone = client.getNetworkZone(createdNetworkZone.getId())
        assertThat(updatedNetworkZone, notNullValue())
        assertThat(updatedNetworkZone.getId(), equalTo(createdNetworkZone.getId()))
        assertThat(updatedNetworkZone.getGateways(), iterableWithSize(1))
        assertThat(
            updatedNetworkZone.getGateways().find { nz -> (nz.getValue() == "2.3.4.5/24") }
            , notNullValue()
        )
        assertThat(updatedNetworkZone.getProxies(), iterableWithSize(1))
        assertThat(
            updatedNetworkZone.getProxies().find { nz -> (nz.getValue() == "3.3.4.5/24") }
            , notNullValue()
        )
    }

    @Test (groups = "group2")
    void activateDeactivateNetworkZoneTest() {

        String networkZoneName = SPINE_NAME_PREFIX + "${uniqueTestName}"

        NetworkZone networkZone = client.instantiate(NetworkZone)
            .setType(NetworkZoneType.IP)
            .setName(networkZoneName)
            .setStatus(NetworkZoneStatus.ACTIVE)
            .setGateways(Arrays.asList(
                client.instantiate(NetworkZoneAddress)
                    .setType(NetworkZoneAddressType.CIDR)
                    .setValue("1.2.3.4/24")
            ))
            .setProxies(Arrays.asList(
                client.instantiate(NetworkZoneAddress)
                    .setType(NetworkZoneAddressType.CIDR)
                    .setValue("2.2.3.4/24")
            ))

        def createdNetworkZone = client.createNetworkZone(networkZone)
        assertThat(createdNetworkZone, notNullValue())
        registerForCleanup(createdNetworkZone)

        client.deactivateNetworkZone(createdNetworkZone.getId())

        def deactivatedNetworkZone = client.getNetworkZone(createdNetworkZone.getId())
        assertThat(deactivatedNetworkZone.getStatus(), equalTo(NetworkZoneStatus.INACTIVE))

        client.activateNetworkZone(deactivatedNetworkZone.getId())
        def activatedNetworkZone = client.getNetworkZone(deactivatedNetworkZone.getId())
        assertThat(activatedNetworkZone.getStatus(), equalTo(NetworkZoneStatus.ACTIVE))
    }
}


