/*
 * Copyright 2017 Okta
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
package com.okta.sdk.tests.it.util

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.BasicMappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.matching.RequestPattern
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import com.okta.commons.lang.Strings
import de.sstoehr.harreader.HarReader
import de.sstoehr.harreader.model.Har
import de.sstoehr.harreader.model.HarRequest
import de.sstoehr.harreader.model.HarResponse
import de.sstoehr.harreader.model.HttpMethod
import org.testng.collections.CollectionUtils

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

/**
 * Starts the WireMock based test server using the 'har' files from @okta/okta-sdk-test-server.
 */
class TestServer {
    public static final TEST_SERVER_BASE_URL = "okta.testServer.baseUrl"

    private WireMockServer wireMockServer
    private int port

    int getMockPort() {
        if (port == 0) {
            port = new ServerSocket(0).withCloseable {it.getLocalPort()}
        }
        return port
    }

    void configureHttpMock(WireMockServer wireMockServer, List<String> scenarios) {
        scenarios.each {loadScenario(wireMockServer, it)}
    }

    TestServer start(List<String> scenarios) {
        if (CollectionUtils.hasElements(scenarios)) {
            wireMockServer = new WireMockServer(wireMockConfig().port(getMockPort()))
            configureHttpMock(wireMockServer, scenarios)
            wireMockServer.start()
        }
        return this
    }

    void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop()
        }
    }

    void verify() {

        if (wireMockServer != null) {
            wireMockServer.stubMappings.each {

                RequestPattern request = it.request
                RequestPatternBuilder requestPatternBuilder = new RequestPatternBuilder(request.method, WireMock.urlPathEqualTo(request.urlPath))
                request.headers.entrySet().each {
                    requestPatternBuilder.withHeader(it.key, it.value.valuePattern)
                }

                if (request.queryParameters != null) {
                    request.queryParameters.entrySet().each {
                        requestPatternBuilder.withQueryParam(it.key, it.value.valuePattern)
                    }
                }

                if (request.bodyPatterns != null) {
                    requestPatternBuilder.withRequestBody(request.bodyPatterns.get(0))
                }

                wireMockServer.verify(requestPatternBuilder)
            }
        }
    }

    private void loadScenario(WireMockServer wireMockServer, String scenario) {

        String scenarioName = scenario.replaceAll("\\.har\$", "")

        HarReader harReader = new HarReader()
        Har har = harReader.readFromString(getClass().getResource("/scenarios/${scenarioName}.har").text)

        // force scenario order
        int requestNum = 0

        har.log.entries.forEach {

            HarRequest request = it.getRequest()
            HarResponse response = it.getResponse()

            String path = request.url.replace("https://test.example.com", "/${scenarioName}").replaceAll("\\?.*", "")

            def builder = new BasicMappingBuilder(RequestMethod.fromString(request.method.name()), WireMock.urlPathEqualTo(path))
            builder.inScenario(scenarioName)

            if (requestNum == 0) {
                builder.whenScenarioStateIs(com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED)
            }
            else {
                builder.whenScenarioStateIs("state-${scenarioName}-${requestNum}")
            }
            builder.willSetStateTo("state-${scenarioName}-${++requestNum}")

            // match headers exactly
            request.headers.forEach {
                // strip content type, that is non-matching for the request (recording error)
                if ("content-type" != it.name ||
                        (request.method == HttpMethod.POST || request.method == HttpMethod.PUT)) {
                    builder.withHeader(it.name, equalTo(it.value))
                }
            }
            // match query string exactly
            request.queryString.forEach {
                builder.withQueryParam(it.name, equalTo(it.value))
            }
            if (!Strings.isEmpty(request.postData.text)) {
                builder.withRequestBody(equalToJson(request.postData.text, false, true))
            }

            // response
            def mockResponse = aResponse()
            // include headers
            response.headers.forEach {
                mockResponse.withHeader(it.name, it.value)
            }
            mockResponse.withStatus(response.status)
            mockResponse.withBody(response.content.text)

            wireMockServer.stubFor(builder.willReturn(mockResponse))
        }
    }
}