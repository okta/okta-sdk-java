/*
 * Copyright 2018 Okta, Inc.
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
import static japicmp.model.JApiCompatibilityChange.*
import static japicmp.model.JApiChangeStatus.*

println("japicmp report, changes from previous release:")
def it = jApiClasses.iterator()
while (it.hasNext()) {
    def jApiClass = it.next()
    def fqn = jApiClass.getFullyQualifiedName()

    // false positive due to moving methods to new parent BaseClient
    // future changes would be reported under the parent class directly anyway
    if (fqn.startsWith("com.okta.sdk.impl.client.DefaultClient")) {
        jApiClass.getCompatibilityChanges().remove(METHOD_REMOVED_IN_SUPERCLASS)
    }

    if (jApiClass.getChangeStatus() != UNCHANGED) {
        println("class ${fqn}: ${jApiClass.getChangeStatus()}")

        def methodIt = jApiClass.getMethods().iterator()
        while (methodIt.hasNext()) {
            def method = methodIt.next()
            def methodChanges = method.getCompatibilityChanges()
            methodChanges.remove(METHOD_NEW_DEFAULT)

            if (methodChanges) {
                println("  method ${method.getName()} ${methodChanges}")

            }
        }
    }
}
return jApiClasses