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
package com.okta.sdk.tests.it

import com.okta.sdk.client.Client
import com.okta.sdk.resource.app.PublicAppInstance
import com.okta.sdk.resource.app.PublicAppInstanceSettings

/**
 * Tests for /api/v1/apps
 * @since 0.5.0
 */
class PublicAppInstancesIT implements CrudTestSupport {

    @Override
    def create(Client client) {

        PublicAppInstance app = client.instantiate(PublicAppInstance)
//        app.setName("TODO") // TODO missing field?
        app.setLabel("IT-App-" + UUID.randomUUID().toString())
        app.dirtyProperties.put("name", "template_basic_auth")
        app.setSignOnMode("BASIC_AUTH") // TODO should this be an enum?
        app.setSettings(client.instantiate(PublicAppInstanceSettings))

        Map<String, Object> appMap = new LinkedHashMap<>()
        appMap.put("url", "https://example.com/login.html")
        appMap.put("authURL", "https://example.com/auth.html")

        app.getSettings().setApp(appMap)

        return client.createAppInstance(app)
    }

    @Override
    def read(Client client, String id) {
        return client.getAppInstance(id)
    }

    @Override
    void update(Client client, def app) {
        app.setLabel(app.getLabel() + "-1")
        app.updateAppInstance(app) // TODO body here
    }

    @Override
    void delete(Client client, def app) {
        app.deactivateAppInstance()
        app.deleteAppInstance()
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listAppInstances().iterator()
    }
}
