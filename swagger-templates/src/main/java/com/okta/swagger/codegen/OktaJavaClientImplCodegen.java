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

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.codegen.CodegenModel;
import io.swagger.codegen.CodegenOperation;
import io.swagger.codegen.CodegenProperty;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Swagger;
import org.apache.commons.lang3.BooleanUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OktaJavaClientImplCodegen extends AbstractOktaJavaClientCodegen
{
    private static final String CREATE_NESTED_KEY = "x-oktaInstantiateNested";

    private final String overrideModelPackage;

    public OktaJavaClientImplCodegen() {
        super("okta_java_impl", "OktaJavaImpl", "com.okta.sdk.impl.resource");

        modelTemplateFiles.put("model.mustache", ".java");
        overrideModelPackage = "com.okta.sdk.resource";
        apiPackage           = "com.okta.sdk.impl.client";
        vendorExtensions().put("overrideModelPackage", overrideModelPackage);
        vendorExtensions().put("overrideApiPackage", "com.okta.sdk.client");

        apiTemplateFiles.put("api.mustache", ".java");
    }

    @Override
    public void preprocessSwagger(Swagger swagger) {
        super.preprocessSwagger(swagger);
        // Enum based definitions are created by OktaJavaClientApiCodegen, so they need to be removed here
        enumList.forEach(enumEntry -> swagger.getDefinitions().remove(enumEntry));
    }

    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        super.postProcessModelProperty(model, property);
        if (!BooleanUtils.toBoolean(model.isEnum)) {

            String propertyType;
            String propertyTypeMethod;
            boolean forceCast = false;

            if (property.isEnum || enumList.contains(property.datatype)) {
                propertyType = "EnumProperty";
                propertyTypeMethod = "getEnumProperty";
                property.vendorExtensions.put("itemType", property.datatypeWithEnum);
                property.vendorExtensions.put("constructorTypeExtra", ", " + property.datatypeWithEnum + ".class");
                property.vendorExtensions.put("typeClassExtra", Boolean.TRUE);
            }
            else if(property.isListContainer) {

                if (property.items.baseType.equals("String")) {
                    propertyType = "ListProperty";
                    propertyTypeMethod = "getListProperty";
                } else if(enumList.contains(property.items.baseType)) {
                    propertyType = "EnumListProperty";
                    propertyTypeMethod = "getEnumListProperty";
                    property.vendorExtensions.put("itemType", property.items.datatypeWithEnum);
                    property.vendorExtensions.put("constructorTypeExtra", ", " + property.items.datatypeWithEnum + ".class");
                    property.vendorExtensions.put("typeClassExtra", Boolean.TRUE);
                } else {
                    propertyType = "ResourceListProperty";
                    propertyTypeMethod = "getResourceListProperty";
                    property.vendorExtensions.put("itemType", property.items.datatypeWithEnum);
                    property.vendorExtensions.put("constructorTypeExtra", ", " + property.items.datatypeWithEnum + ".class");
                    property.vendorExtensions.put("typeClassExtra", Boolean.TRUE);
                }
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
                        propertyTypeMethod = "getIntProperty";
                        break;
                    case "Date":
                        propertyType = "DateProperty";
                        propertyTypeMethod = "getDateProperty";
                        break;
                    case "Double":
                        propertyType = "DoubleProperty";
                        propertyTypeMethod = "getDoubleProperty";
                        break;
                    default:
                        propertyType = "ResourceReference";
                        propertyTypeMethod = "getResourceProperty";
                        property.vendorExtensions.put("itemType", property.datatype);
                        property.vendorExtensions.put("constructorTypeExtra", buildConstructorTypeExtra(property));
                        property.vendorExtensions.put("typeClassExtra", Boolean.TRUE);
                }
            }

            property.vendorExtensions.put("forceCast", forceCast);
            property.vendorExtensions.put("propertyType", propertyType);
            property.vendorExtensions.put("propertyTypeMethod", propertyTypeMethod);

        }
    }

    private String buildConstructorTypeExtra(CodegenProperty property) {
        Collection<String> autoCreateParams = Collections.singleton("profile");
        boolean createNested = property.vendorExtensions.containsKey(CREATE_NESTED_KEY) || autoCreateParams.contains(property.name);
        return ", " + property.datatype + ".class, "+ createNested;
    }

    @Override
    public CodegenModel fromModel(String name, Model model, Map<String, Model> allDefinitions) {
        CodegenModel codegenModel = super.fromModel(name, model, allDefinitions);
        codegenModel.imports.add(toModelName(codegenModel.classname)); // The 'Default' gets added in the template

        Map<String, String> defaultValuesMap = new LinkedHashMap<>();

        ObjectNode rawDefaultValues = (ObjectNode) codegenModel.vendorExtensions.get("x-okta-defined-as");
        if (rawDefaultValues != null) {
            rawDefaultValues.fields().forEachRemaining(entry -> {
                defaultValuesMap.put(entry.getKey(), entry.getValue().textValue());
            });
        }

        // if the parent is set, we need to check for discrimination
        String parent = (String) codegenModel.vendorExtensions.get("x-okta-parent");
        if (parent != null) {
            parent = parent.substring(parent.lastIndexOf('/') + 1);
            if (discriminatorMap.containsKey(parent)) {
                Discriminator discriminator = discriminatorMap.get(parent);

                String fieldName = discriminator.getFieldName();
                String defaultValue = discriminator.getDefaultFieldValue(name);

                defaultValuesMap.put(fieldName, defaultValue);
            }
        }

        List<KeyValuePair> defaultTypeSetter = defaultValuesMap.entrySet().stream()
                .map(entry -> new KeyValuePair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        codegenModel.vendorExtensions.put("defaultSetter", defaultTypeSetter);

        return codegenModel;
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
        } else if ("post".equals(httpMethod) ) {

            co.vendorExtensions.put("dsMethod", "create");
            co.vendorExtensions.put("isPost", true);
        } else if ("get".equals(httpMethod)) {
            co.vendorExtensions.put("dsMethod", "getResource");
            co.vendorExtensions.put("isGet", true);
        } else if ("delete".equals(httpMethod)) {
            co.vendorExtensions.put("dsMethod", "delete");
            co.vendorExtensions.put("isDelete", true);
        }

        // pre interpolate the resource href
        co.vendorExtensions.put("hrefFiltered", co.path
                .replaceAll("\\{", "\" + ")
                .replaceAll("\\}", " + \""));

        return co;
    }

    @Override
    protected void addToModelTagMap(String modelName, String packageName) {
        modelTagMap.put(modelName, packageName);
        modelTagMap.put("Default" + modelName, packageName); // Also add the 'Default' impl
    }

    @Override
    public String toApiName(String name) {
        return "Default" + super.toApiName(name);
    }

    @Override
    public String toModelFilename(String name) {
        return super.toModelFilename("Default" + name);
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
        }

        if (modelTagMap.containsKey(name)) {
            return overrideModelPackage +"."+ modelTagMap.get(name) +"."+ name;
        }

        return overrideModelPackage + "." + name;
    }

    @Override
    protected void buildDiscriminationMap(Swagger swagger) {
        super.buildDiscriminationMap(swagger);

        Map<String, Object> rootConfigMap = new HashMap<>();
        Map<String, Object> destMap = new HashMap<>();
        rootConfigMap.put("config", destMap);
        discriminatorMap.values().forEach(disc -> {
            String fqn = toModelImport(disc.getParentDefName());
            String fieldName = disc.getFieldName();
            Map<String, String> valueMap = disc.getValueDefMap().entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getValue(), e -> toModelImport(e.getKey())));
            Map<String, Object> entries = new HashMap<>();
            entries.put("fieldName", fieldName);
            entries.put("values", valueMap);
            destMap.put(fqn, entries);
        });

        // now dump this to yaml
        // cheat a little here because we are assuming we are using maven, replace the LAST index of /target/ (the
        // release process will have two 'target' directories in the path
        String mavenTargetDir = outputFolder().substring(0, outputFolder.lastIndexOf("/target/") + 8);
        File destFile = new File(
                new File(mavenTargetDir), "generated-resources/swagger/" + overrideModelPackage.replace('.', '/') +
                "/discrimination.yaml");

        boolean folderCreated = destFile.getParentFile().mkdirs();
        if (!folderCreated && !destFile.getParentFile().exists()) {
            throw new RuntimeException("Directory does not exist and could not be created: " + destFile.getParentFile());
        }

        try (OutputStream outputStream = new FileOutputStream(destFile)) {

            Yaml yaml = new Yaml();
            Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            yaml.dump(rootConfigMap, writer);

        } catch (IOException e) {
            throw new RuntimeException("Failed to write discrimination map to yaml: "+ destFile.getAbsolutePath(), e);
        }
    }
}