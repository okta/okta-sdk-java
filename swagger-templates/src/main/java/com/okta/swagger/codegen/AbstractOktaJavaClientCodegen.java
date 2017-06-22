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
import io.swagger.models.HttpMethod;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerException;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractOktaJavaClientCodegen extends AbstractJavaCodegen {

    private final String codeGenName;

    static final String MEDIA_TYPE = "mediaType";

    static final String X_OPENAPI_V3_SCHEMA_REF = "x-openapi-v3-schema-ref";

    @SuppressWarnings("hiding")
    private final Logger log = LoggerFactory.getLogger(AbstractOktaJavaClientCodegen.class);

    public static final String PARCELABLE_MODEL = "parcelableModel";

    protected boolean parcelableModel = false;
    protected boolean useBeanValidation = false;
    protected boolean performBeanValidation = false;
    protected boolean useGzipFeature = false;

    protected Map<String, String> modelTagMap = new HashMap<>();
    protected Set<String> enumList = new HashSet<>();

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
        tagEnums(swagger);
        addListModels(swagger);
        buildModelTagMap(swagger);
        removeListAfterAndLimit(swagger);
        moveOperationsToSingleClient(swagger);
        handleOktaLinkedOperations(swagger);
    }

    protected void tagEnums(Swagger swagger) {
        swagger.getDefinitions().forEach((name, model) -> {
            assert model instanceof ModelImpl : "Model MUST be an instance of ModelImpl";
            if (((ModelImpl) model).getEnum() != null) {
                enumList.add(name);
            }
        });
    }

    protected void buildModelTagMap(Swagger swagger) {

        swagger.getDefinitions().forEach((key, definition) -> {
            Object tags = definition.getVendorExtensions().get("x-okta-tags");
            if (tags != null) {
                // if tags is NOT null, then assume it is an array
                if (tags instanceof List) {
                    if (!((List) tags).isEmpty()) {
                        String packageName = tagToPackageName(((List) tags).get(0).toString());
                        addToModelTagMap(key, packageName);
                        definition.getVendorExtensions().put("x-okta-package", packageName);
                    }
                }
                else {
                    throw new SwaggerException("Model: "+ key + " contains 'x-okta-tags' that is NOT a List.");
                }
            }
        });
    }

    protected void addToModelTagMap(String modelName, String packageName) {
        modelTagMap.put(modelName, packageName);
    }

    protected String tagToPackageName(String tag) {
        return tag.replaceAll("(.)(\\p{Upper})", "$1.$2").toLowerCase(Locale.ENGLISH);
    }

    public void removeListAfterAndLimit(Swagger swagger) {
        swagger.getPaths().forEach((pathName, path) ->
           path.getOperations().forEach(operation ->
               operation.getParameters().removeIf(param ->
                       !param.getRequired() &&
                               ("limit".equals(param.getName()) ||
                                "after".equals(param.getName())))
           )
        );
    }

    private void addAllIfNotNull(List<ObjectNode> destList, List<ObjectNode> srcList) {
        if (srcList != null) {
            destList.addAll(srcList);
        }
    }

    private void handleOktaLinkedOperations(Swagger swagger) {
        // we want to move any operations defined by the 'x-okta-operations' or 'x-okta-crud' vendor extension to the model
        Map<String, Model> modelMap = swagger.getDefinitions().entrySet().stream()
                .filter(e -> e.getValue().getVendorExtensions().containsKey("x-okta-operations")
                        || e.getValue().getVendorExtensions().containsKey("x-okta-crud"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        modelMap.forEach((k, model) -> {
            List<ObjectNode> linkNodes = new ArrayList<>();

            addAllIfNotNull(linkNodes, (List<ObjectNode>) model.getVendorExtensions().get("x-okta-operations"));
            addAllIfNotNull(linkNodes, (List<ObjectNode>) model.getVendorExtensions().get("x-okta-crud"));

            Map<String, CodegenOperation> operationMap = new HashMap<>();

            linkNodes.forEach(n -> {
                String operationId = n.get("operationId").textValue();

                // find the swagger path operation
                swagger.getPaths().forEach((pathName, path) -> {
                    Optional<Map.Entry<HttpMethod, Operation>> operationEntry = path.getOperationMap().entrySet().stream().filter(e -> e.getValue().getOperationId().equals(operationId)).findFirst();

                    if (operationEntry.isPresent()) {

                        Operation operation = operationEntry.get().getValue();

                        CodegenOperation cgOperation = fromOperation(
                                pathName,
                                operationEntry.get().getKey().name().toLowerCase(),
                                operation,
                                swagger.getDefinitions(),
                                swagger);

                        boolean canLinkMethod = true;

                        JsonNode aliasNode = n.get("alias");
                        if (aliasNode != null) {
                            String alias = aliasNode.textValue();
                            cgOperation.vendorExtensions.put("alias", alias);

                            if ("update".equals(alias)) {
                                model.getVendorExtensions().put("saveable", true);
                            } else if ("delete".equals(alias)) {
                                model.getVendorExtensions().put("deletable", true);
                            }
                            else if ("read".equals(alias) || "create".equals(alias)) {
                                canLinkMethod = false;
                            }
                        }

                        // we do NOT link read or create methods, those need to be on the parent object
                        if (canLinkMethod) {

                            // now any params that match the models we need to use the model value directly
                            // for example if the path contained {id} we would call getId() instead

                            Map<String, String> argMap = new LinkedHashMap<>();
                            ArrayNode argNodeList = (ArrayNode) n.get("arguments");

                            if (argNodeList != null) {
                                for (Iterator argNodeIter = argNodeList.iterator(); argNodeIter.hasNext(); ) {
                                    JsonNode argNode = (JsonNode) argNodeIter.next();

                                    if (argNode.has("src")) {
                                        String src = argNode.get("src").textValue();
                                        String dest = argNode.get("dest").textValue();
                                        if (src != null) {
                                            argMap.put(dest, src); // reverse lookup
                                        }
                                    }

                                    if (argNode.has("self")) {
                                        String dest = argNode.get("dest").textValue();
                                        argMap.put(dest, "this"); // reverse lookup
                                    }
                                }
                            }

                            List<CodegenParameter> cgParamAllList = new ArrayList<>();
                            List<CodegenParameter> cgParamModelList = new ArrayList<>();

                            cgOperation.pathParams.forEach(param -> {

                                if (argMap.containsKey(param.paramName)) {

                                    String paramName = argMap.get(param.paramName);
                                    cgParamModelList.add(param);

                                    if (model.getProperties() != null) {
                                        CodegenProperty cgProperty = fromProperty(paramName, model.getProperties().get(paramName));
                                        param.vendorExtensions.put("fromModel", cgProperty);
                                    } else {
                                        System.err.println("Model '" + model.getTitle() + "' has no properties");
                                    }

                                } else {
                                    cgParamAllList.add(param);
                                }
                            });

                            for (Iterator<CodegenParameter> iter = cgOperation.bodyParams.iterator(); iter.hasNext(); ) {
                                CodegenParameter bodyParam = iter.next();
                                if (argMap.containsKey(bodyParam.paramName)) {
                                    cgOperation.vendorExtensions.put("bodyIsSelf", true);
                                    iter.remove();
                                }
                            }

//                        if (cgOperation.getHasBodyParam()) {
//                            CodegenParameter bodyParam = cgOperation.bodyParam;
//                            if (argMap.containsKey(bodyParam.paramName)) {
//                                bodyParam.paramName = "WTF";
//                                bodyParam.vendorExtensions.put("self", true);
//                            }
//                        }


//                        // mark the last param
//                        if (!cgParamModelList.isEmpty()) {
//                            CodegenParameter param = cgParamModelList.get(cgParamModelList.size()-1);
//                            param.hasMore = false;
//                        }

                            cgParamAllList.addAll(cgOperation.queryParams);
                            cgParamAllList.addAll(cgOperation.bodyParams);

                            // set all params to have more
                            cgParamAllList.forEach(param -> param.hasMore = true);

                            // then grab the last one and mark it as the last
                            if (!cgParamAllList.isEmpty()) {
                                CodegenParameter param = cgParamAllList.get(cgParamAllList.size() - 1);
                                param.hasMore = false;
                            }

                            cgOperation.vendorExtensions.put("allParams", cgParamAllList);
                            cgOperation.vendorExtensions.put("fromModelPathParams", cgParamModelList);

                            addOptionalExtension(cgOperation, cgParamAllList);

                            operationMap.put(cgOperation.operationId, cgOperation);

                            // mark the operation as moved so we do NOT add it to the client
                            operation.getVendorExtensions().put("moved", true);

                        }
                    }
                });
            });

            model.getVendorExtensions().put("operations", operationMap.values());
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
    public String toModelFilename(String name) {
        if (modelTagMap.containsKey(name)) {
            return modelTagMap.get(name) +"/"+ super.toModelFilename(name);
        }
        return super.toModelFilename(name);
    }

    @Override
    public String toModelImport(String name) {
        if (modelTagMap.containsKey(name)) {
            return modelPackage() +"."+ modelTagMap.get(name) +"."+ name;
        }
        return super.toModelImport(name);
    }



    @Override
    public CodegenModel fromModel(String name, Model model, Map<String, Model> allDefinitions) {
       CodegenModel codegenModel = super.fromModel(name, model, allDefinitions);
        // super add these imports, and we don't want that dependency
        codegenModel.imports.remove("ApiModel");

        if (model.getVendorExtensions().containsKey("x-baseType")) {
            String baseType = (String) model.getVendorExtensions().get("x-baseType");
            codegenModel.vendorExtensions.put("baseType", toModelName(baseType));
            codegenModel.imports.add(toModelName(baseType));
        }

        Collection<CodegenOperation> operations = (Collection<CodegenOperation>) codegenModel.vendorExtensions.get("operations");
        if (operations != null) {
            operations.forEach(op -> {
                    if (op.returnType != null) {
                        codegenModel.imports.add(op.returnType);
                    }
                    if (op.allParams != null) {
                        op.allParams.stream()
                            .filter(param -> needToImport(param.dataType))
                            .forEach(param -> codegenModel.imports.add(param.dataType));
                    }
            });
        }

       return codegenModel;
    }

    @Override
    public Map<String, Object> postProcessOperations(Map<String, Object> objs) {

        Map<String, Object> resultMap = super.postProcessOperations(objs);

        List<Map<String, String>> imports = (List<Map<String, String>>) objs.get("imports");
        Map<String, Object> operations = (Map<String, Object>) objs.get("operations");
        List<CodegenOperation> codegenOperations = (List<CodegenOperation>) operations.get("operation");

        // find all of the list return values
        Set<String> importsToAdd = new HashSet<>();
        codegenOperations.stream()
                .filter(cgOp -> cgOp.returnType != null)
                .filter(cgOp -> cgOp.returnType.matches(".+List$"))
                .forEach(cgOp -> importsToAdd.add(toModelImport(cgOp.returnType)));

        // the params might have imports too
        codegenOperations.stream()
                .filter(cgOp -> cgOp.allParams != null)
                .forEach(cgOp -> cgOp.allParams.stream()
                        .filter(cgParam -> cgParam.isEnum)
                        .forEach(cgParam -> importsToAdd.add(toModelImport(cgParam.dataType))));

        // add each one as an import
        importsToAdd.forEach(className -> {
            Map<String, String> listImport = new LinkedHashMap<>();
            listImport.put("import", className);
            imports.add(listImport);
        });

        return resultMap;
    }


    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        super.postProcessModelProperty(model, property);
        if(!BooleanUtils.toBoolean(model.isEnum)) {

            String datatype = property.datatype;
            if (datatype != null
                    && datatype.matches(".+List$")
                    && needToImport(datatype)) {
                model.imports.add(datatype);
            }

            String type = property.complexType;
            if (type == null) {
                type = property.baseType;
            }

            if (needToImport(type)) {
                model.imports.add(type);
            }

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

        // scan params for X_OPENAPI_V3_SCHEMA_REF, and _correct_ the param
        co.allParams.forEach(param -> {
            if (param.vendorExtensions.containsKey(X_OPENAPI_V3_SCHEMA_REF)) {
                String enumDef = param.vendorExtensions.get(X_OPENAPI_V3_SCHEMA_REF).toString().replaceFirst(".*/","");
                param.isEnum = true;
                param.enumName = enumDef;
                param.dataType = enumDef;
            }
        });

        // mark the operation as having optional params, so we can take advantage of it in the template
        addOptionalExtension(co, co.allParams);

        return co;
    }

    private void addOptionalExtension(CodegenOperation co, List<CodegenParameter> params) {

        if (params.parallelStream().anyMatch(param -> !param.required)) {
            co.vendorExtensions.put("hasOptional", true);

            List<CodegenParameter> nonOptionalParams = params.stream()
                    .filter(param -> param.required)
                    .map(CodegenParameter::copy)
                    .collect(Collectors.toList());

            if (!nonOptionalParams.isEmpty()) {
                CodegenParameter param = nonOptionalParams.get(nonOptionalParams.size()-1);
                param.hasMore = false;
                co.vendorExtensions.put("nonOptionalParams", nonOptionalParams);
            }

            // remove the noOptionalParams if we have trimmed down the list.
            if (co.vendorExtensions.get("nonOptionalParams") != null && nonOptionalParams.isEmpty()) {
                co.vendorExtensions.remove("nonOptionalParams");
            }

            // remove th body parameter if it was optional
            if (co.bodyParam != null && !co.bodyParam.required) {
                co.vendorExtensions.put("optionalBody", true);
            }
        }
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

            listModels.putAll(processListsFromProperties(properties, null, swagger));
        }

        swagger.getDefinitions()
                .forEach((key, model) -> {
                    if (model != null && model.getProperties() != null) {
                        listModels.putAll(processListsFromProperties(model.getProperties().values(), model, swagger));
                    }
                });

        listModels.forEach(swagger::addDefinition);

    }

    private Map<String, Model> processListsFromProperties(Collection<Property> properties, Model baseModel, Swagger swagger) {

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

                        if (baseModel == null) {
                            baseModel = swagger.getDefinitions().get(baseName);
                        }

                        // only add the tags from the base model
                        if (baseModel.getVendorExtensions().containsKey("x-okta-tags")) {
                            model.setVendorExtension("x-okta-tags", baseModel.getVendorExtensions().get("x-okta-tags"));
                        }

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
                log.warn("{} (array property) does not have a proper inner type defined", ap.getName());
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
