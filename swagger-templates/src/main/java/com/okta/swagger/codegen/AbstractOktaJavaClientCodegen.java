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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.codegen.CodegenModel;
import io.swagger.codegen.CodegenOperation;
import io.swagger.codegen.CodegenParameter;
import io.swagger.codegen.CodegenProperty;
import io.swagger.codegen.CodegenType;
import io.swagger.codegen.languages.AbstractJavaCodegen;
import io.swagger.codegen.languages.features.BeanValidationFeatures;
import io.swagger.codegen.languages.features.GzipFeatures;
import io.swagger.codegen.languages.features.PerformBeanValidationFeatures;
import io.swagger.models.HttpMethod;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.util.Json;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AbstractOktaJavaClientCodegen extends AbstractJavaCodegen {

    private final String codeGenName;

    static final String MEDIA_TYPE = "mediaType";

    @SuppressWarnings("hiding")
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOktaJavaClientCodegen.class);

    public static final String PARCELABLE_MODEL = "parcelableModel";

    protected boolean parcelableModel = false;
    protected boolean useBeanValidation = false;
    protected boolean performBeanValidation = false;
    protected boolean useGzipFeature = false;

    public AbstractOktaJavaClientCodegen(String codeGenName, String relativeTemplateDir, String modelPackage) {
        super();
        this.codeGenName = codeGenName;
        this.dateLibrary = "legacy";

        outputFolder = "generated-code" + File.separator + codeGenName;
        embeddedTemplateDir = templateDir = relativeTemplateDir;

        artifactId = "not_used";

        this.modelPackage = modelPackage;
        // TODO: these are hard coded for now, calling Maven Plugin does NOT set the packages correctly.
        invokerPackage = "com.okta.sdk.invoker";
        apiPackage = "com.okta.sdk.client";

        apiTemplateFiles.clear();
        modelTemplateFiles.clear();

    }

    @Override
    public void preprocessSwagger(Swagger swagger) {
        vendorExtensions.put("basePath", swagger.getBasePath());
        super.preprocessSwagger(swagger);
        addListModels(swagger);
        removeListAfterAndLimit(swagger);
        moveOperationsToSingleClient(swagger);
        handleOktaLinkedOperations(swagger);

    }

    public void removeListAfterAndLimit(Swagger swagger) {
        swagger.getPaths().forEach((pathName, path) ->
           path.getOperations().forEach(operation ->
               operation.getParameters().removeIf(param ->
                       !param.getRequired() &&
                               (("limit".equals(param.getName())) ||
                                ("after".equals(param.getName()))))
           )
        );
    }

    private void handleOktaLinkedOperations(Swagger swagger) {
        // we want to move any operations defined by the 'x-okta-links' vendor extension to the model
        Map<String, Model> modelMap = swagger.getDefinitions().entrySet().stream()
                .filter(e -> e.getValue().getVendorExtensions().containsKey("x-okta-links"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        modelMap.forEach((k, model) -> {
            List<ObjectNode> linkNodes = (List<ObjectNode>) model.getVendorExtensions().get("x-okta-links");
            linkNodes.forEach(n -> {
                String operationId = n.get("operationId").textValue();

                // find the swagger path operation
                swagger.getPaths().forEach((pathName, path) -> {
                    Optional<Map.Entry<HttpMethod, Operation>> operationEntry = path.getOperationMap().entrySet().stream().filter(e -> e.getValue().getOperationId().equals(operationId)).findFirst();

                    if (operationEntry.isPresent()) {

                        Operation operation = operationEntry.get().getValue();

                        List<CodegenOperation> operations = (List<CodegenOperation>) model.getVendorExtensions().getOrDefault("operations", new ArrayList<CodegenOperation>());

                        CodegenOperation cgOperation = fromOperation(
                                pathName,
                                operationEntry.get().getKey().name().toLowerCase(),
                                operation,
                                swagger.getDefinitions(),
                                swagger);

                        JsonNode aliasNode = n.get("alias");
                        if (aliasNode != null) {
                            cgOperation.vendorExtensions.put("alias", aliasNode.textValue());
                        }

                        // now any params that match the models we need to use the model value directly
                        // for example if the path contained {id} we would call getId() instead

                        Map<String, String> argMap = new LinkedHashMap<>();
                        ArrayNode argNodeList = (ArrayNode)  n.get("arguments");

                        if (argNodeList != null) {
                            for (Iterator argNodeIter=argNodeList.iterator(); argNodeIter.hasNext();) {
                                JsonNode argNode = (JsonNode) argNodeIter.next();
                                String src = argNode.get("src").textValue();
                                String dest = argNode.get("dest").textValue();
                                argMap.put(dest, src); // reverse lookup
                            }
                        }

                        List<CodegenParameter> cgParamAllList = new ArrayList<>();
                        List<CodegenParameter> cgParamModelList = new ArrayList<>();

                        cgOperation.pathParams.forEach(param -> {

                            if (argMap.containsKey(param.paramName)) {
                                cgParamModelList.add(param);

                                String srcPropName = argMap.get(param.paramName);
                                CodegenProperty cgProperty = fromProperty(srcPropName, model.getProperties().get(srcPropName));
                                param.vendorExtensions.put("fromModel", cgProperty);
                            }
                            else {
                                cgParamAllList.add(param);
                            }
                        });
                        cgParamAllList.addAll(cgOperation.queryParams);
                        cgParamAllList.addAll(cgOperation.bodyParams);

                        cgOperation.vendorExtensions.put("allParams", cgParamAllList);
                        cgOperation.vendorExtensions.put("fromModelPathParams", cgParamModelList);

                        operations.add(cgOperation);
                        model.getVendorExtensions().put("operations", operations);

                        // mark the operation as moved so we do NOT add it to the client
                        operation.getVendorExtensions().put("moved", true);

                    }
                });
            });
        });
    }

    private void moveOperationsToSingleClient(Swagger swagger) {
        swagger.getPaths().values().forEach(path ->
            path.getOperations().forEach(operation ->
                operation.setTags(Collections.singletonList("client"))
            )
        );
    }

    @Override
    public String apiFileFolder() {
        return outputFolder + "/" + apiPackage().replace('.', File.separatorChar);
    }

    @Override
    public String modelFileFolder() {
        return outputFolder + "/" + modelPackage().replace('.', File.separatorChar);
    }

    @Override
    public CodegenType getTag() {
        return CodegenType.CLIENT;
    }

    @Override
    public String getName() {
        return codeGenName;
    }

    @Override
    public String getHelp() {
        return "Generates a Java client library.";
    }

    @Override
    public CodegenModel fromModel(String name, Model model, Map<String, Model> allDefinitions) {
       CodegenModel codegenModel = super.fromModel(name, model, allDefinitions);
        // super add these imports, and we don't want that dependency
        codegenModel.imports.remove("ApiModel");

        if (model.getVendorExtensions().containsKey("x-baseType")) {
            String baseType = (String) model.getVendorExtensions().get("x-baseType");
            codegenModel.vendorExtensions.put("baseType", toModelName(baseType));
        }

       return codegenModel;
    }

    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        super.postProcessModelProperty(model, property);
        if(!BooleanUtils.toBoolean(model.isEnum)) {

            // super add these imports, and we don't want that dependency
            model.imports.remove("ApiModelProperty");
            model.imports.remove("ApiModel");

            //final String lib = getLibrary();
            //Needed imports for Jackson based libraries
            if(additionalProperties.containsKey("jackson")) {
                model.imports.add("JsonProperty");
            }
            if(additionalProperties.containsKey("gson")) {
                model.imports.add("SerializedName");
            }
        } else { // enum class
            //Needed imports for Jackson's JsonCreator
            if(additionalProperties.containsKey("jackson")) {
                model.imports.add("JsonCreator");
            }
        }
    }

    @Override
    public Map<String, Object> postProcessModelsEnum(Map<String, Object> objs) {
        objs = super.postProcessModelsEnum(objs);
        //Needed import for Gson based libraries
        if (additionalProperties.containsKey("gson")) {
            List<Map<String, String>> imports = (List<Map<String, String>>)objs.get("imports");
            List<Object> models = (List<Object>) objs.get("models");
            for (Object _mo : models) {
                Map<String, Object> mo = (Map<String, Object>) _mo;
                CodegenModel cm = (CodegenModel) mo.get("model");
                // for enum model
                if (Boolean.TRUE.equals(cm.isEnum) && cm.allowableValues != null) {
                    cm.imports.add(importMapping.get("SerializedName"));
                    Map<String, String> item = new HashMap<String, String>();
                    item.put("import", importMapping.get("SerializedName"));
                    imports.add(item);
                }
            }
        }
        return objs;
    }

    @Override
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

        // mark the operation as having optional params, so we can take advantage of it in the template
        if (co.allParams.parallelStream().anyMatch(param -> !param.required)) {
            co.vendorExtensions.put("hasOptional", true);

            List<CodegenParameter> nonOptionalParams = co.allParams.stream()
                    .filter(param -> param.required)
                    .map(CodegenParameter::copy)
                    .collect(Collectors.toList());

            if (!nonOptionalParams.isEmpty()) {
                CodegenParameter param = nonOptionalParams.get(nonOptionalParams.size()-1);
                param.hasMore = false;
                co.vendorExtensions.put("nonOptionalParams", nonOptionalParams);
            }
        }
        return co;
    }

    @Override
    public String toApiName(String name) {
        if (name.length() == 0) {
            return "Client";
        }

        name = sanitizeName(name);
        return camelize(name);
    }

    private Property getArrayPropertyFromOperation(Operation operation) {


        if (operation != null && operation.getResponses() != null) {
            Response response = operation.getResponses().get("200");
            if (response != null) {
                return response.getSchema();
            }
        }
        return null;
    }

    public void addListModels(Swagger swagger) {

        Map<String, Model> listModels = new LinkedHashMap<>();

        // lists in paths
        for (Path path : swagger.getPaths().values()) {

            List<Property> properties = new ArrayList<>();
            properties.add(getArrayPropertyFromOperation(path.getGet()));
            properties.add(getArrayPropertyFromOperation(path.getPost()));
            properties.add(getArrayPropertyFromOperation(path.getPatch()));
            properties.add(getArrayPropertyFromOperation(path.getPut()));

            listModels.putAll(processListsFromProperties(properties));
        }

        swagger.getDefinitions()
                .forEach((key, model) -> {
                    if (model != null && model.getProperties() != null) {
                        listModels.putAll(processListsFromProperties(model.getProperties().values()));
                    }
                });

        listModels.forEach(swagger::addDefinition);

    }

    private Map<String, Model> processListsFromProperties(Collection<Property> properties) {

        Map<String, Model> result = new LinkedHashMap<>();

        for (Property p : properties) {
            if (p != null && "array".equals(p.getType())) {

                ArrayProperty arrayProperty = (ArrayProperty) p;
                if ( arrayProperty.getItems() instanceof RefProperty) {
                    RefProperty ref = (RefProperty) arrayProperty.getItems();

                    String baseName = ref.getSimpleRef();

                    // Do not generate List wrappers for primitives (or strings)
                    if (!languageSpecificPrimitives.contains(baseName)) {
                        String modelName = baseName + "List";

                        ModelImpl model = new ModelImpl();
                        model.setName(modelName);
                        model.setAllowEmptyValue(false);
                        model.setDescription("Collection List for " + baseName);
                        model.setVendorExtension("x-isResourceList", true);
                        model.setVendorExtension("x-baseType", baseName);
                        model.setType(modelName);

                        result.put(modelName, model);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String getTypeDeclaration(Property p) {

        if (p instanceof ArrayProperty) {
            ArrayProperty ap = (ArrayProperty) p;
            Property inner = ap.getItems();
            if (inner == null) {
                // mimic super behavior
                LOGGER.warn(ap.getName() + "(array property) does not have a proper inner type defined");
                return null;
            }

            String type = super.getTypeDeclaration(inner);
            if (!languageSpecificPrimitives.contains(type)) {
                return type + "List";
            }
        }
        return super.getTypeDeclaration(p);
    }
}
