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
package com.okta.sdk.impl.resource

import com.okta.sdk.resource.application.AppUser
import com.okta.sdk.resource.application.Application
import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.group.rule.GroupRule
import com.okta.sdk.resource.user.User
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

class DefaultResourceHrefResolverTest {

    final static String BASE_URL = "https://okta.example.com"

    @Test
    void missingHrefReturnsNullTest() {

        ResourceHrefResolver resolver = new DefaultResourceHrefResolver()

        Map<String, ?> nullHrefProps = [
            _links: [
                self: [
                    href: null
            ]]]

        Map<String, ?> missingHrefProps = [
            _links: [
                self: [
                    foo: "bar"
            ]]]

        assertThat resolver.resolveHref(Collections.emptyMap(), User, BASE_URL), nullValue()
        assertThat resolver.resolveHref(nullHrefProps, User, BASE_URL), nullValue()
        assertThat resolver.resolveHref(missingHrefProps, User, BASE_URL), nullValue()
    }

    @Test
    void resourceWithSelfLinkTest() {

        String selfHref = "https://okta-test.example.com/my/href"
        Map<String, ?> props = [
            _links: [
                self: [
                    href: selfHref
            ]]]

        ResourceHrefResolver resolver = new DefaultResourceHrefResolver()
        assertThat resolver.resolveHref(props, User, BASE_URL), equalTo(selfHref)
        assertThat resolver.resolveHref(props, null, BASE_URL), equalTo(selfHref) // clazz doesn't matter when self link is set
    }

    @Test
    void appUserHrefTest() {

        Map<String, ?> props = [
            id: "this-user-id",
            _links: [
                app: [
                    href: "https://okta-test.example.com/api/v1/apps/an-app-id"
                ],
                user: [
                    href: "https://okta-test.example.com/api/v1/users/a-user-id"
                ],
                group: [
                    name: "Everyone",
                    href: "https://okta-test.example.com/api/v1/groups/everyone-id"
                ]
            ]
        ]

        ResourceHrefResolver resolver = new DefaultResourceHrefResolver()
        assertThat resolver.resolveHref(props, AppUser, BASE_URL), equalTo("https://okta-test.example.com/api/v1/apps/an-app-id/users/this-user-id")
        assertThat resolver.resolveHref(props, User, BASE_URL), nullValue() // wrong class, so this should return null
    }

    @Test
    void applicationHrefTest() {

        String appId = "this-app-id"
        Map<String, ?> props = [
            id: appId,
            _links: [
                appLinks: [
                    [
                        name: "oidc_client_link",
                        href: "https://okta-test.example.com/home/oidc_client/${appId}/aln5z7uhkbM6y7bMy0g7",
                        type: "text/html"
                    ]
                ],
                groups: [
                    href: "https://okta-test.example.com/api/v1/apps/${appId}/groups"
                ],
                logo: [
                    [
                        name: "medium",
                        href: "https://op1static.oktacdn.com/assets/img/logos/default.some-image.png",
                        type: "image/png"
                    ]
                ],
                users: [
                    href: "https://okta-test.example.com/api/v1/apps/${appId}/users"
                ],
                deactivate: [
                    href: "https://okta-test.example.com/api/v1/apps/${appId}/lifecycle/deactivate"
                ]
            ]
        ]

        ResourceHrefResolver resolver = new DefaultResourceHrefResolver()
        assertThat resolver.resolveHref(props, Application, BASE_URL), equalTo("https://okta-test.example.com/api/v1/apps/this-app-id")
        assertThat resolver.resolveHref(props, User, BASE_URL), nullValue() // wrong class, so this should return null
    }

    @Test
    void groupHrefTest() {

        String groupId = "this-group-id"
        Map<String, ?> props = [
            id: groupId,
            _links: [
                logo: [
                    [
                        name: "medium",
                        href: "https://op1static.oktacdn.com/assets/img/logos/groups/okta-medium.some-image.png",
                        type: "image/png"
                    ],
                    [
                        name: "large",
                        href: "https://op1static.oktacdn.com/assets/img/logos/groups/okta-large.some-image.png",
                        type: "image/png"
                    ]
                ],
                users: [
                    href: "https://okta-test.example.com/api/v1/groups/${groupId}/users"
                ],
                apps: [
                    href: "https://okta-test.example.com/api/v1/groups/${groupId}/apps"
                ]
            ]
        ]

        ResourceHrefResolver resolver = new DefaultResourceHrefResolver()
        assertThat resolver.resolveHref(props, Group, BASE_URL), equalTo("${BASE_URL}/api/v1/groups/this-group-id".toString())
        assertThat resolver.resolveHref(props, User, BASE_URL), nullValue() // wrong class, so this should return null
    }

    @Test
    void groupRuleHrefTest() {

        String groupRuleId = "this-group-rule-id"
        Map<String, ?> props = [
            id: groupRuleId
        ]

        ResourceHrefResolver resolver = new DefaultResourceHrefResolver()
        assertThat resolver.resolveHref(props, GroupRule, BASE_URL), equalTo("${BASE_URL}/api/v1/groups/rules/this-group-rule-id".toString())
        assertThat resolver.resolveHref(props, User, BASE_URL), nullValue() // wrong class, so this should return null
    }

}
