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
package com.okta.sdk.resource

import groovy.json.JsonSlurper
import org.testng.annotations.Test

import java.util.Map.Entry

/**
 * Simple tests to validate the api.yaml spec.  We want to check for methods that likely SHOULD belong to a
 * model and NOT the Client directly.
 */
class ApiSpecTest {

    /**
     * This doesn't do any actual validation at the moment, it is just a helper method to output the missing
     * x-okta-operations json.
     */
    @Test(enabled = false)
    void checkForModelOperations() {

        def opPathMap = new LinkedHashMap()
        def json = new JsonSlurper().parse(new File("../src/swagger/api.yaml"))
        for(def pathE : json.paths) {

            for(def opE : pathE.value) {
                def op = opE.value
                opPathMap.put(op.operationId, new Pair(pathE.key, opE.key))
//                println(op.operationId +" : "+ pathE.key)
            }
        }

        def opModelMap = new LinkedHashMap()
        for (def defE : json.definitions) {
            String modelName = defE.key
            def links = defE.value['x-okta-operations']
            if (links) {
                for (def link : links) {
                    opModelMap.put(link.operationId, modelName)
//                    println(link.operationId +" : "+ modelName)
                }
            }

        }

        // now we have our lookup maps, now it is time to compare them

        for (Iterator iter = opPathMap.iterator(); iter.hasNext(); ) {
            Map.Entry entry = iter.next()
            if (opModelMap.keySet().contains(entry.key)) {
                iter.remove()
            }
        }

        printMethodsThatShouldBeLinked(opPathMap, "User", "/api/v1/users/{userId}", "userId", "id")
        printMethodsThatShouldBeLinked(opPathMap, "PublicAppInstance", "/api/v1/apps/{appId}", "appId", "id")
        printMethodsThatShouldBeLinked(opPathMap, "UserGroup", "/api/v1/groups/{groupId}", "groupId", "id")
        printMethodsThatShouldBeLinked(opPathMap, "MediationPolicy", "/api/v1/policies/{policyId}", "policyId", "id")
        printMethodsThatShouldBeLinked(opPathMap, "IdpTransaction", "/api/v1/idps/tx/{transactionId}", "transactionId", "id")

        printMethodsThatShouldBeLinked(opPathMap, "CVDAppUserProfile", "/api/v1/apps/user/types/{typeId}", "typeId", "id")
        printMethodsThatShouldBeLinked(opPathMap, "IdpTrust", "/api/v1/idps/{idpId}", "idpId", "id")

        printMethodsThatShouldBeLinked(opPathMap, "GroupMembershipMediationRule", "/api/v1/groups/rules/{ruleId}", "ruleId", "id")
        printMethodsThatShouldBeLinked(opPathMap, "OrgCustomSmsMediationTemplate", "/api/v1/templates/sms/{templateId}", "templateId", "id")



    }

    void printMethodsThatShouldBeLinked(Map opPathMap, String model, String startsWithPath, String pathDest, String pathSrc) {

        Map toBeLinked = new LinkedHashMap()
        for (Entry entry : opPathMap) {
//            println(entry.value +" : "+ entry.key)

            if (entry.value.name.startsWith(startsWithPath)) {
                if (!((entry.value.name.equals(startsWithPath)) && "get".equals(entry.value.value))) {
                    toBeLinked.put(entry.key, entry.value.name)
                }
            }
        }

        if (!toBeLinked.isEmpty()) {
            println(model)

            println('\t"x-okta-operations": [')
            StringBuilder sb = new StringBuilder()
            for (Entry entry : toBeLinked) {

                sb.append("""
        {
            "alias": "${entry.key}",
            "operationId": "${entry.key}",
            "arguments": [
                {
                  "dest": "${pathDest}",
                  "src": "${pathSrc}"
                }
            ]
        },""")
            }

            sb.deleteCharAt(sb.length()-1)
            sb.append('\n\t],')
            println(sb.toString())
        }

    }

    static class Pair {
        def name
        def value

        Pair(def name, def value) {
            this.name = name
            this.value = value
        }
    }

}
