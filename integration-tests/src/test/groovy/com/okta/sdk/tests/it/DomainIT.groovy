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

import com.okta.sdk.resource.ResourceException
import com.okta.sdk.resource.domain.Domain
import com.okta.sdk.resource.domain.DomainCertificateSourceType
import com.okta.sdk.resource.domain.DomainResponse
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.Assert
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/domains}.
 */
class DomainIT extends ITSupport {

    @Test(groups = "group1")
    void customTemplatesCrudTest() {

        String domain = "java-sdk-it-${UUID.randomUUID().toString()}.example.com"

        def domainCreated = client.createDomain(
            client.instantiate(Domain)
            .setDomain(domain)
            .setCertificateSourceType(DomainCertificateSourceType.MANUAL)
        )

        assertThat(domainCreated, notNullValue())
        assertThat(domainCreated.getId(), notNullValue())
        assertThat(domainCreated.getDomain(), equalTo(domain))
        assertThat(domainCreated.getCertificateSourceType(), equalTo(DomainResponse.CertificateSourceTypeEnum.MANUAL))

        def domainFetched = client.getDomain(domainCreated.getId())

        assertThat(domainFetched, notNullValue())
        assertThat(domainFetched.getId(), equalTo(domainCreated.getId()))

        client.deleteDomain(domainCreated.getId())

        try {
            client.verifyDomain(domainCreated.getId())
            Assert.fail("Expected ResourceException (404)")
        } catch (ResourceException e) {
            assertThat(e.status, equalTo(404))
        }
    }
}
