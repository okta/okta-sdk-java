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
import io.swagger.codegen.CodegenProperty;
import org.apache.commons.lang3.BooleanUtils;

public class OktaJavaClientImplCodegen extends AbstractOktaJavaClientCodegen
{

    private final String overrideApiPackage;

    public OktaJavaClientImplCodegen() {
        super("okta_java_impl", "OktaJavaImpl", "com.okta.sdk.impl.model");

        modelTemplateFiles.put("modelImpl.mustache", ".java");
        this.overrideApiPackage = "com.okta.sdk.api.model";
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

    @Override
    public String toModelFilename(String name) {
        return "Default" + super.toModelDocFilename(name);
    }

    public String toModelImport(String name) {
        if ("".equals(modelPackage())) {
            return name;
        } else {
            return overrideApiPackage + "." + name;
        }
    }

}
