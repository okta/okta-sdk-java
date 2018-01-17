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
package com.okta.sdk.impl.resource;

import com.okta.sdk.lang.Collections;
import com.okta.sdk.resource.Resource;
import com.okta.sdk.resource.application.AppUser;
import com.okta.sdk.resource.application.Application;
import com.okta.sdk.resource.group.Group;
import com.okta.sdk.resource.group.rule.GroupRule;

import java.util.Map;

/**
 * The default HREF resolver return a HAL self link if available ({@code _links.self.href}). If the {@code self} link is
 * missing, it will be statically built for known objects (or null will be returned).
 * <p>
 * Known Objects:
 * <ul>
 *     <li>AppUser</li>
 *     <li>Application</li>
 *     <li>Group</li>
 *     <li>GroupRule</li>
 * </ul>
 *
 * @since 0.11.0
 */
public class DefaultResourceHrefResolver implements ResourceHrefResolver {

    @Override
    public <R extends Resource> String resolveHref(Map<String, ?> properties, Class<R> clazz, String baseUrl) {
        Map<String, ?> links = getMapValue(properties, "_links");
        Map<String, ?> self = getMapValue(links, "self");
        if (!Collections.isEmpty(self)) {
            return (String) self.get("href");
        }
        return fixSelfHref(properties, clazz, baseUrl);
    }

    private <R extends Resource> String fixSelfHref(Map<String, ?> properties, Class<R> clazz, String baseUrl) {

        // the AppUsers object does NOT contain a self link, in this case we need build it based on the 'app' link
        Map<String, ?> links = getMapValue(properties, "_links");
        if (links != null) {
            if (AppUser.class.isAssignableFrom(clazz)) {
                Map<String, ?> self = getMapValue(links, "app");
                if (!Collections.isEmpty(self)) {
                    return self.get("href") + "/users/" + properties.get("id");
                }
            }
            if (Application.class.isAssignableFrom(clazz)) {
                Map<String, ?> self = getMapValue(links, "users");
                if (!Collections.isEmpty(self)) {
                    String href = self.get("href").toString();
                    return href.substring(0, href.lastIndexOf("/users"));
                }
            }
        }

        if (baseUrl != null) {
            if (Group.class.isAssignableFrom(clazz)) {
                return baseUrl + "/api/v1/groups/" + properties.get("id");
            }

            if (GroupRule.class.isAssignableFrom(clazz)) {
                return baseUrl + "/api/v1/groups/rules/" + properties.get("id");
            }
        }

        return null;
    }

    private Map<String, ?> getMapValue(Map<String, ?> properties, String key) {
        if (!Collections.isEmpty(properties)) {
            return (Map<String, ?>) properties.get(key);
        }
        return null;
    }
}