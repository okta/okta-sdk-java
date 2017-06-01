package com.okta.sdk.tests.it

import com.okta.sdk.client.Client
import com.okta.sdk.resource.PublicAppInstance
import com.okta.sdk.resource.PublicAppInstanceSettings
import com.okta.sdk.resource.PublicAppInstanceVisibility
import com.okta.sdk.resource.UserBuilder

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
