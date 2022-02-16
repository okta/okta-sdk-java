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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samskivert.mustache.Mustache;
import io.swagger.codegen.v3.CodegenConstants;
import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.codegen.v3.CodegenParameter;
import io.swagger.codegen.v3.CodegenProperty;
import io.swagger.codegen.v3.CodegenType;
import io.swagger.codegen.v3.generators.java.AbstractJavaCodegen;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BinarySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractOktaJavaClientCodegen extends AbstractJavaCodegen {

    @SuppressWarnings("hiding")
    private final Logger log = LoggerFactory.getLogger(AbstractOktaJavaClientCodegen.class);

    private static final String NON_OPTIONAL_PRAMS = "nonOptionalParams";
    private static final String X_CODEGEN_REQUEST_BODY_NAME = "x-codegen-request-body-name";

    protected Map<String, String> modelTagMap = new HashMap<>();
    protected Set<String> enumList = new HashSet<>();
    protected Map<String, Discriminator> discriminatorMap = new HashMap<>();
    protected Map<String, String> reverseDiscriminatorMap = new HashMap<>();
    protected Set<String> topLevelResources = new HashSet<>();
    protected Map<String, Object> rawSwaggerConfig;
    private final String codeGenName;

    public AbstractOktaJavaClientCodegen(String codeGenName, String modelPackage) {
        super();
        this.codeGenName = codeGenName;
        this.dateLibrary = "legacy";
        this.modelPackage = modelPackage;
        apiPackage = "com.okta.sdk.client";
        apiTemplateFiles.clear();
    }

    @Override
    public void preprocessOpenAPI(OpenAPI openAPI) {

        //Reading raw config
        try {
            rawSwaggerConfig = new Yaml().loadAs(inputSpec, Map.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse inputSpec variable", e);
        }
        preprocessRequestBodyName(openAPI);

        super.preprocessOpenAPI(openAPI);
        tagEnums(openAPI);
        buildTopLevelResourceList(openAPI);
        addListModels(openAPI);
        buildModelTagMap(openAPI);
        removeListAfterAndLimit(openAPI);
        moveOperationsToSingleClient(openAPI);
        buildDiscriminationMap(openAPI);
        buildGraalVMReflectionConfig(openAPI);
    }

    /**
     * Figure out which models are top level models (directly returned from an endpoint).
     * @param openAPI The instance of OpenAPI.
     */
    protected void buildTopLevelResourceList(OpenAPI openAPI) {

        Set<String> resources = new HashSet<>();

        // Loop through all the operations looking for the models that are used as the response and body params
        openAPI.getPaths().forEach((pathName, path) -> {

            // find all body params
            Stream.of(path.getPost(), path.getPut()).filter(Objects::nonNull).forEach(operation -> {
                String bodyType = getRequestBodyType(operation);
                if (bodyType != null) {
                    resources.add(bodyType);
                }
            });

            // response objects are a more complicated, start with filter for only the 200 responses
            Stream.of(path.getGet(), path.getPost(), path.getPut(), path.getDelete())
                .filter(Objects::nonNull).forEach(operation -> {
                    operation.getResponses().entrySet().stream()
                        .filter(entry -> "200".equals(entry.getKey()) && entry.getValue().getContent() != null)
                        .filter(entry -> entry.getValue().getContent().get("application/json") != null)
                        .forEach(entry -> {
                            Schema schema = entry.getValue().getContent().get("application/json").getSchema();
                            if (schema instanceof ArraySchema) {
                                String ref = ((ArraySchema) schema).getItems().get$ref();
                                String requestType = refToSimpleName(ref);
                                resources.add(requestType);
                            }
                        });
                });
        });

        // find any children of these resources
        openAPI.getComponents().getSchemas().forEach((name, model) -> {
            String parent = getParentModelRef(model);
            if (parent != null) {
                parent = refToSimpleName(parent);

                if (resources.contains(parent)) {
                    resources.add(name);
                }
            }
        });

        // mark each model with a 'top-level' vendorExtension
        resources.stream()
            .forEach(resourceName -> {
                if (openAPI.getComponents().getSchemas().get(resourceName).getExtensions() == null) {
                    openAPI.getComponents().getSchemas().get(resourceName).setExtensions(new HashMap<>());
                }
                openAPI.getComponents().getSchemas().get(resourceName).getExtensions().put("top-level", true);
            });

        this.topLevelResources = resources;
    }

    private String getRequestBodyType(Operation operation) {
        assert operation != null;
        if(operation.getRequestBody() == null || operation.getRequestBody().getContent() == null) {
            return null;
        }
        MediaType mediaType = operation.getRequestBody().getContent().get("application/json");
        if(mediaType == null || mediaType.getSchema() == null) {
            return null;
        }
        return refToSimpleName(mediaType.getSchema().get$ref());
    }

    private String refToSimpleName(String ref) {
        return ref == null ? null : ref.substring(ref.lastIndexOf("/") + 1);
    }

    protected void buildDiscriminationMap(OpenAPI openAPI) {
        openAPI.getComponents().getSchemas().forEach((name, model) -> {
            if (model.getDiscriminator() != null && model.getDiscriminator().getMapping() != null) {
                String propertyName = model.getDiscriminator().getPropertyName();
                Map<String, String> mapping = model.getDiscriminator().getMapping().entrySet().stream()
                    .collect(
                        Collectors.toMap(
                            e -> e.getValue().substring(e.getValue().lastIndexOf('/') + 1),
                            Map.Entry::getKey,
                            (oldValue, newValue) -> newValue
                        )
                    );
                mapping.forEach((key, value) -> reverseDiscriminatorMap.put(key, name));
                discriminatorMap.put(name, new Discriminator(name, propertyName, mapping));
            }
        });
    }

    protected void buildGraalVMReflectionConfig(OpenAPI openAPI) {

        try {
            List<Map<String, ?>> reflectionConfig = openAPI.getComponents().getSchemas().keySet().stream()
                .filter(it -> !enumList.contains(it)) // ignore enums
                .map(this::fqcn)
                .map(this::reflectionConfig)
                .collect(Collectors.toList());

            // this is slightly error-prone, but this project only has `api` and `impl`
            File projectDir = new File(outputFolder(), "../../..").getCanonicalFile();
            String projectName = projectDir.getName();

            File reflectionConfigFile = new File(projectDir, "target/classes/META-INF/native-image/com.okta.sdk/okta-sdk-" + projectName + "/generated-reflection-config.json");
            if (!(reflectionConfigFile.getParentFile().exists() || reflectionConfigFile.getParentFile().mkdirs())) {
                throw new IllegalStateException("Failed to create directory: "+ reflectionConfigFile.getParent());
            }
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(reflectionConfigFile, reflectionConfig);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write generated-reflection-config.json file", e);
        }
    }

    protected Map<String, ?> reflectionConfig(String fqcn) {
        return Collections.singletonMap("name", fqcn);
    }

    protected void tagEnums(OpenAPI openAPI) {
        openAPI.getComponents().getSchemas().forEach((name, model) -> {
            if (model.getEnum() != null) {
                enumList.add(name);
            }
        });
    }

    protected void preprocessRequestBodyName(OpenAPI openAPI) {
        Map<String, Object> allPaths = castToMap(rawSwaggerConfig.get("paths"));

        if (allPaths != null) {
            allPaths.forEach((pathName, value) -> {
                Map<String, Object> pathItem = castToMap(value);
                if (pathItem != null) {
                    Map<String, Object> postOperation = castToMap(pathItem.get("post"));
                    if (postOperation != null && postOperation.containsKey(X_CODEGEN_REQUEST_BODY_NAME)) {
                        String requestBodyName = postOperation.get(X_CODEGEN_REQUEST_BODY_NAME).toString();
                        if(requestBodyName != null) {
                            RequestBody requestBody = openAPI.getPaths().get(pathName)
                                .getPost().getRequestBody();
                            if(requestBody.getExtensions() == null) {
                                requestBody.setExtensions(new HashMap<>());
                            }
                            requestBody.getExtensions().put(X_CODEGEN_REQUEST_BODY_NAME, requestBodyName);
                        }
                    }

                    Map<String, Object> putOperation = castToMap(pathItem.get("put"));
                    if (putOperation != null && putOperation.containsKey(X_CODEGEN_REQUEST_BODY_NAME)) {
                        String requestBodyName = putOperation.get(X_CODEGEN_REQUEST_BODY_NAME).toString();
                        if (requestBodyName != null) {
                            RequestBody requestBody = openAPI.getPaths().get(pathName)
                                .getPut().getRequestBody();
                            if (requestBody.getExtensions() == null) {
                                requestBody.setExtensions(new HashMap<>());
                            }
                            requestBody.getExtensions().put(X_CODEGEN_REQUEST_BODY_NAME, requestBodyName);
                        }
                    }
                }
            });
        }
    }

    protected void buildModelTagMap(OpenAPI openAPI) {

        openAPI.getComponents().getSchemas().forEach((key, definition) -> {
            if(definition.getExtensions() != null ) {
                Object tags = definition.getExtensions().get("x-okta-tags");
                if (tags != null) {
                    // if tags is NOT null, then assume it is an array
                    if (tags instanceof List) {
                        if (!((List) tags).isEmpty()) {
                            addToModelTagMap(key);
                        }
                    } else {
                        throw new RuntimeException("Model: " + key + " contains 'x-okta-tags' that is NOT a List.");
                    }
                }
            }
        });
    }

    protected void addToModelTagMap(String modelName) {
        // always tag to root package
        modelTagMap.put(modelName, "");
    }

    public void removeListAfterAndLimit(OpenAPI openAPI) {
        openAPI.getPaths().forEach((pathName, path) -> {
            Stream.of(path.getGet(), path.getPost(), path.getPut(), path.getDelete())
                .filter(Objects::nonNull)
                .filter(operation -> operation.getParameters() != null)
                .forEach(operation -> {
                    operation.getParameters().removeIf(
                        param -> !param.getRequired()
                            && ("limit".equals(param.getName()) || "after".equals(param.getName()))
                    );
                });
        });
    }

    private void moveOperationsToSingleClient(OpenAPI openAPI) {
        openAPI.getPaths().values().forEach(path -> {
            if(path.getGet() != null) {
                path.getGet().setTags(Collections.singletonList("client"));
            }
            if(path.getPost() != null) {
                path.getPost().setTags(Collections.singletonList("client"));
            }
            if(path.getDelete() != null) {
                path.getDelete().setTags(Collections.singletonList("client"));
            }
            if(path.getPut() != null) {
                path.getPut().setTags(Collections.singletonList("client"));
            }
        });
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
    public String toBooleanGetter(String name) {
        return "get" + getterAndSetterCapitalize(name);
    }

    @Override
    public String toModelFilename(String name) {
        if (modelTagMap.containsKey(name)) {
            String tag = modelTagMap.get(name);
            return tag.replaceAll("\\.","/") +"/"+ super.toModelFilename(name);
        }
        return super.toModelFilename(name);
    }

    @Override
    public String toModelImport(String name) {
        if (languageSpecificPrimitives.contains(name)) {
            throw new IllegalStateException("Cannot import primitives: "+ name);
        }
        if (modelTagMap.containsKey(name)) {
            return modelPackage() +"."+ modelTagMap.get(name) +"."+ name;
        }
        return super.toModelImport(name);
    }

    protected String fqcn(String name) {
        String className = toApiName(name);
        if (modelTagMap.containsKey(className)) {
            return modelPackage() +"."+ modelTagMap.get(className) +"."+ className;
        }
        return super.toModelImport(className);
    }

    @Override
    public CodegenModel fromModel(String name, Schema model, Map<String, Schema> allDefinitions) {
        CodegenModel codegenModel = super.fromModel(name, model, allDefinitions);
        // super add these imports, and we don't want that dependency
        codegenModel.imports.remove("ApiModel");

        if (model.getExtensions() != null && model.getExtensions().containsKey("x-baseType")) {
            String baseType = (String) model.getExtensions().get("x-baseType");
            codegenModel.vendorExtensions.put("baseType", toModelName(baseType));
        }

        if (codegenModel.parent != null) {
            // handle correctly model inheritance described as
            // allOf:
            // - $ref:
            codegenModel.parent = toApiName(codegenModel.parent);
            codegenModel.getVendorExtensions().remove("top-level");
        }

        return codegenModel;
    }

    private List<CodegenOperation> sortOperations(Collection<CodegenOperation> operations) {

        return operations.stream()
                .sorted(Comparator.comparing(o -> ((CodegenOperation) o).path)
                                  .thenComparing(o -> ((CodegenOperation) o).httpMethod))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> postProcessOperations(Map<String, Object> objs) {

        Map<String, Object> resultMap = super.postProcessOperations(objs);

        Map<String, Object> operations = (Map<String, Object>) objs.get("operations");
        List<CodegenOperation> codegenOperations = (List<CodegenOperation>) operations.get("operation");
        operations.put("operation", sortOperations(codegenOperations));
        return resultMap;
    }

    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        super.postProcessModelProperty(model, property);

        if(!BooleanUtils.toBoolean(model.getIsEnum())) {

            //Do not use JsonWebKeyList because it's based on Map<K,V> but API require a simple List<JsonWebKey>
            if(model.name.equals("OpenIdConnectApplicationSettingsClientKeys")) {
                property.datatypeWithEnum = property.baseType + "<" + property.complexType + ">";
            }

            if(property.getVendorExtensions().containsKey("x-okta-known-values")) {
                property.getVendorExtensions().put("x-okta-known-values-exists", true);
                property.getVendorExtensions()
                    .put("x-okta-known-values-class-name", property.getNameInCamelCase() + "Values");
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

        model.vendorExtensions.put("optionalClassnamePartial", (Mustache.Lambda) (frag, out) -> {
            String templateResource = "/" + templateDir + "/" + model.classname + ".mustache";
            URL optionalClassnameTemplate = getClass().getResource(templateResource);

            Mustache.Compiler compiler = Mustache.compiler().withLoader((name) -> {
                if (optionalClassnameTemplate != null) {
                    return new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(templateResource)), StandardCharsets.UTF_8);
                }
                return new StringReader("");
            });
            processCompiler(compiler).compile("{{> " + model.classname + "}}").execute(frag.context(), out);
        });
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
                if (Boolean.TRUE.equals(cm.getIsEnum()) && cm.allowableValues != null) {
                    cm.imports.add(importMapping.get("SerializedName"));
                    Map<String, String> item = new HashMap<>();
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
                                          Map<String, Schema> definitions,
                                          OpenAPI openAPI) {
        CodegenOperation co = super.fromOperation(path,
                httpMethod,
                operation,
                definitions,
            openAPI);

        // Deep copy for vendorExtensions Map
        co.vendorExtensions = new LinkedHashMap<>(co.vendorExtensions);

        // mark the operation as having optional params, so we can take advantage of it in the template
        addOptionalExtensionAndBackwardCompatibleArgs(co, co.allParams);

        // if the body and the return type are the same mark the body param
        co.bodyParams.forEach(bodyParam -> {
            if (bodyParam.dataType.equals(co.returnType)) {
                co.vendorExtensions.put("updateBody", true);
            }
        });

        return co;
    }

    @Override
    public CodegenParameter fromRequestBody(RequestBody body, String name, Schema schema, Map<String, Schema> schemas, Set<String> imports) {
        CodegenParameter codegenParameter = super.fromRequestBody(body, name, schema, schemas, imports);
        if (schema instanceof BinarySchema) {
            codegenParameter.dataType = "InputStream";
            codegenParameter.baseType = "InputStream";
            codegenParameter.getVendorExtensions().put(CodegenConstants.IS_BINARY_EXT_NAME, Boolean.TRUE);
        }

        return codegenParameter;
    }

    private void addOptionalExtensionAndBackwardCompatibleArgs(CodegenOperation co, List<CodegenParameter> params) {
        addOptionalArgs(co, params);
        addBackwardCompatibleArgs(co, params);
    }

    private void addOptionalArgs(CodegenOperation co, List<CodegenParameter> params) {
        if (params.parallelStream().anyMatch(param -> !param.required)) {
            co.vendorExtensions.put("hasOptional", true);

            List<CodegenParameter> nonOptionalParams = params.stream()
                .filter(param -> param.required)
                .map(CodegenParameter::copy)
                .collect(Collectors.toList());

            if (!nonOptionalParams.isEmpty()) {
                co.vendorExtensions.put(NON_OPTIONAL_PRAMS, nonOptionalParams);
            }

            // remove the nonOptionalParams if we have trimmed down the list.
            if (co.vendorExtensions.get(NON_OPTIONAL_PRAMS) != null && nonOptionalParams.isEmpty()) {
                co.vendorExtensions.remove(NON_OPTIONAL_PRAMS);
            }

            // remove the body parameter if it was optional
            if (co.bodyParam != null && !co.bodyParam.required) {
                co.vendorExtensions.put("optionalBody", true);
            }
        }
    }

    private void addBackwardCompatibleArgs(CodegenOperation co, List<CodegenParameter> params) {
        // add backward compatible args only for major revisions greater than 1
        if (params.parallelStream().anyMatch(param -> param.vendorExtensions.containsKey("x-okta-added-version") &&
            Integer.parseInt(param.vendorExtensions.get("x-okta-added-version").toString().substring(0, 1)) > 1)) {

            // capture the backward compat params
            Map<String, List<CodegenParameter>> versionedParamsMap = new LinkedHashMap<>();

            // loop through first and build the keys
            List<String> paramVersions = params.stream()
                .filter(param -> param.vendorExtensions.containsKey("x-okta-added-version"))
                .map(param -> param.vendorExtensions.get("x-okta-added-version").toString())
                .collect(Collectors.toList());
            Collections.reverse(paramVersions);
            paramVersions.add("preversion"); // anything without 'x-okta-added-version'

            paramVersions.forEach(version -> versionedParamsMap.put(version, new ArrayList<>()));

            // now loop through again and figure out which buckets each param goes into
            params.forEach(param -> {
                String version = param.vendorExtensions.getOrDefault("x-okta-added-version", "preversion").toString();
                for (Map.Entry<String, List<CodegenParameter>> entry : versionedParamsMap.entrySet()) {

                    String versionKey = entry.getKey();
                    List<CodegenParameter> versionedParams = entry.getValue();

                    // add the param, the break if we are adding to the final version
                    versionedParams.add(param.copy());
                    if (version.equals(versionKey)) {
                        break;
                    }
                }
            });

            // remove the latest version as this is equivalent to the expected swagger flow
            String latestVersion = paramVersions.get(0);
            versionedParamsMap.remove(latestVersion);

            // also remove any versions that are empty
            Map<String, List<CodegenParameter>> resultVersionedParamsMap = versionedParamsMap.entrySet().stream()
                .filter(entry ->
                    !entry.getValue().isEmpty() // not empty
                    && entry.getValue().stream().anyMatch(param -> param.getIsQueryParam() || param.getIsHeaderParam())) // has query or header params
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (resultVersionedParamsMap.size() > 0) {
                // it'd be nice if we could just add new CodegenOperations, but the swagger lib does NOT support this
                // instead we will add them as a vendorExtension and process them in a template
                co.vendorExtensions.put("hasBackwardsCompatibleParams", true);
                co.vendorExtensions.put("backwardsCompatibleParamsEntrySet", resultVersionedParamsMap.entrySet());
            }
        }
    }

    @Override
    public String toVarName(String name) {
        String originalResult = super.toVarName(name);
        if (originalResult.contains("oauth")) {
            originalResult = originalResult.replaceAll("oauth", "oAuth");
        }
        return originalResult;
    }

    @Override
    public String toApiName(String name) {
        if (name.length() == 0) {
            return "Client";
        }

        name = sanitizeName(name);
        return camelize(name);
    }

    private Schema getArrayPropertyFromOperation(Operation operation) {
        if (operation != null && operation.getResponses() != null) {
            ApiResponse response = operation.getResponses().get("200");
            if (response != null && response.getContent() != null) {
                MediaType mediaType = response.getContent().get("application/json");
                if(mediaType != null) {
                    return mediaType.getSchema();
                }
            }
        }
        return null;
    }

    public void addListModels(OpenAPI openAPI) {

        Map<String, Schema> listSchemas = new LinkedHashMap<>();

        // lists in paths
        for (PathItem path : openAPI.getPaths().values()) {
            List<Schema> properties = new ArrayList<>();
            Stream.of(path.getGet(), path.getPost(), path.getPatch(), path.getPut())
                .filter(Objects::nonNull)
                .forEach(operation -> {
                    properties.add(getArrayPropertyFromOperation(operation));
                });
            listSchemas.putAll(processListsFromProperties(properties, null, openAPI));
        }

        openAPI.getComponents().getSchemas()
            .entrySet().stream()
            .filter(entry -> topLevelResources.contains(entry.getKey()))
            .forEach(entry -> {
                Schema model = entry.getValue();
                if (model != null && model.getProperties() != null) {
                    listSchemas.putAll(processListsFromProperties(model.getProperties().values(), model, openAPI));
                }
            });

        listSchemas.forEach((key, value) -> {
            openAPI.getComponents().addSchemas(key, value);
        });
    }

    private Map<String, Schema> processListsFromProperties(Collection<Schema> properties, Schema baseModel, OpenAPI openAPI) {

        Map<String, Schema> result = new LinkedHashMap<>();

        for (Schema schema : properties) {
            if (schema instanceof ArraySchema) {
                ArraySchema arraySchema = (ArraySchema) schema;
                if (arraySchema.getItems() != null) {
                    String baseName = ((ArraySchema) schema).getItems().get$ref();
                    baseName = refToSimpleName(baseName);
                    // Do not generate List wrappers for primitives (or strings)
                    if (!languageSpecificPrimitives.contains(baseName) && topLevelResources.contains(baseName)) {

                        String modelName = baseName + "List";

                        ObjectSchema objectSchema = new ObjectSchema();
                        objectSchema.setName(modelName);
                        objectSchema.setExtensions(new HashMap<>());
                        objectSchema.setDescription("Collection List for " + baseName);

                        if (baseModel == null) {
                            baseModel = openAPI.getComponents().getSchemas().get(baseName);
                        }

                        // only add the tags from the base model
                        if (baseModel.getExtensions().containsKey("x-okta-tags")) {
                            objectSchema.getExtensions().put("x-okta-tags", baseModel.getExtensions().get("x-okta-tags"));
                        }

                        objectSchema.getExtensions().put("x-isResourceList", true);
                        objectSchema.getExtensions().put("x-baseType", baseName);
                        objectSchema.setType(modelName);

                        result.put(modelName, objectSchema);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public String getTypeDeclaration(Schema propertySchema) {

        if ("password".equals(propertySchema.getFormat())) {
            return "char[]";
        }
        if (propertySchema instanceof ArraySchema) {
            ArraySchema ap = (ArraySchema) propertySchema;
            Schema inner = ap.getItems();
            if (inner == null) {
                // mimic super behavior
                log.warn("{} (array property) does not have a proper inner type defined", ap.getName());
                return null;
            }

            String type = super.getTypeDeclaration(inner);
            if (!languageSpecificPrimitives.contains(type) && topLevelResources.contains(type)) {
                return type + "List";
            }
        }
        return super.getTypeDeclaration(propertySchema);
    }

    private String getParentModelRef(Schema model) {
        if (model.getExtensions() != null && model.getExtensions().get("x-okta-parent") != null) {
            return (String) model.getExtensions().get("x-okta-parent");
        } else if (model instanceof ComposedSchema) {
            // Assumes first entry is the parent $ref
            ComposedSchema composed = (ComposedSchema) model;
            if (composed.getAllOf() != null && !composed.getAllOf().isEmpty()) {
                return composed.getAllOf().get(0).get$ref();
            }
        }
        return null;
    }

    @Override
    protected void fixUpParentAndInterfaces(CodegenModel codegenModel, Map<String, CodegenModel> allModels) {
        //Override parent method. Doing nothing here.
    }

    private Map<String, Object> castToMap(Object object) {
        if (object instanceof Map) {
            return (Map<String, Object>) object;
        }
        return null;
    }
}
