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
package com.okta.swagger.codegen;

import io.swagger.codegen.CodegenModel;
import io.swagger.codegen.CodegenOperation;
import io.swagger.codegen.CodegenProperty;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Swagger;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Map;

public class OktaJavaClientImplCodegen extends AbstractOktaJavaClientCodegen
{

    private final String overrideModelPackage;

    public OktaJavaClientImplCodegen() {
        super("okta_java_impl", "OktaJavaImpl", "com.okta.sdk.impl.model");

        modelTemplateFiles.put("model.mustache", ".java");
        overrideModelPackage = "com.okta.sdk.model";
        apiPackage         = "com.okta.sdk.impl.api";
        vendorExtensions().put("overrideModelPackage", overrideModelPackage);
        vendorExtensions().put("overrideApiPackage", "com.okta.sdk.api");

        apiTemplateFiles.put("api.mustache", ".java");
    }
    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        super.postProcessModelProperty(model, property);
        if (!BooleanUtils.toBoolean(model.isEnum)) {

            String propertyType;
            String propertyTypeMethod;
            boolean forceCast = false;

            if (property.isEnum) {
                propertyType = "EnumProperty";
                propertyTypeMethod = "getEnumProperty";
                property.vendorExtensions.put("constructorTypeExtra", ", " + property.datatype + ".class");
                property.vendorExtensions.put("typeClassExtra", Boolean.TRUE);
            }
            else if(property.isListContainer) {
                propertyType = "ListProperty";
                propertyTypeMethod = "getListProperty";
                forceCast = true;
            }
            else if(property.isMapContainer || "Object".equals(property.datatype)) {
                propertyType = "MapProperty";
                propertyTypeMethod = "getMap";
            }
            else {

                switch (property.baseType) {
                    case "String":
                        propertyType = "StringProperty";
                        propertyTypeMethod = "getString";
                        break;
                    case "Boolean":
                        propertyType = "BooleanProperty";
                        propertyTypeMethod = "getBoolean";
                        break;
                    case "Integer":
                        propertyType = "IntegerProperty";
                        propertyTypeMethod = "getInt";
                        break;
                    default:
                        propertyType = "ResourceReference";
                        propertyTypeMethod = "getResourceProperty";
                        property.vendorExtensions.put("constructorTypeExtra", ", " + property.datatype + ".class");
                        property.vendorExtensions.put("typeClassExtra", Boolean.TRUE);
                }
            }

            property.vendorExtensions.put("forceCast", forceCast);
            property.vendorExtensions.put("propertyType", propertyType);
            property.vendorExtensions.put("propertyTypeMethod", propertyTypeMethod);

        }
    }

    public CodegenOperation fromOperation(String path,
                                          String httpMethod,
                                          Operation operation,
                                          Map<String, Model> definitions,
                                          Swagger swagger) {
        CodegenOperation co = super.fromOperation(path,
                                                  httpMethod,
                                                  operation,
                                                  definitions,
                                                  swagger);

        co.vendorExtensions.put("resourceReturnType", co.returnType);

        if ("put".equals(httpMethod) ) {

            co.vendorExtensions.put("dsMethod", "create");
            co.vendorExtensions.put("isPut", true);

            if (co.bodyParam == null) {
                co.vendorExtensions.put("resourceReturnType", "VoidResource");
            }
            else {
                co.vendorExtensions.put("dsMethod", "save");
            }

            if (co.allParams.size() == 2 &&
                co.bodyParam != null) { // TODO clean this up
                    co.vendorExtensions.put("isSimpleUpdate", true);
                    co.vendorExtensions.put("resource", co.bodyParam);
            }
        }

        if ("post".equals(httpMethod) ) {

            co.vendorExtensions.put("dsMethod", "create");
            co.vendorExtensions.put("isPost", true);

            if (co.allParams.size() == 1 &&
                co.bodyParam != null) {
                    co.vendorExtensions.put("isSimpleCreate", true);
                    co.vendorExtensions.put("resource", co.bodyParam);
            }
        }

        if ("get".equals(httpMethod)) {
            co.vendorExtensions.put("dsMethod", "getResource");
            co.vendorExtensions.put("isGet", true);
        }

        else if ("delete".equals(httpMethod)) {
            co.vendorExtensions.put("dsMethod", "delete");
            co.vendorExtensions.put("isDelete", true);
        }

        // pre interpolate the resource href

        // this gets a little tricky, if the vendor extension 'fromModel' is set, we need to use a method name
        if (co.vendorExtensions.containsKey("fromModel") ) {
            String f = null;
        }

        co.vendorExtensions.put("hrefFiltered", co.path
                .replaceAll("\\{", "\" + ")
                .replaceAll("\\}", "+ \""));

        return co;
    }

    @Override
    public String toApiName(String name) {
        return "Default" + super.toApiName(name);
    }

    @Override
    public String toModelFilename(String name) {
        return "Default" + super.toModelDocFilename(name);
    }

    @Override
    public Map<String, Object> postProcessOperations(Map<String, Object> objs) {

        objs.entrySet().stream()
                .filter(e -> "operations".equals(e.getKey()) && e.getValue() instanceof Map)
                .filter(e -> ((Map<String, Object>) e.getValue()).containsKey("classname"))
                .forEach(e -> {
                        Map<String, Object> ops = (Map<String, Object>) e.getValue();
                        String interfaceClassname = ops.get("classname").toString().replaceFirst("^Default", "");
                        ops.put("interfaceClassname", interfaceClassname);
                });

        return super.postProcessOperations(objs);
    }

    public String toModelImport(String name) {
        if ("".equals(modelPackage())) {
            return name;
        } else {
            return overrideModelPackage + "." + name;
        }
    }

}
