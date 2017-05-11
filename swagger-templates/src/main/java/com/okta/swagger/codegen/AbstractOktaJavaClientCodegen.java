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
import io.swagger.codegen.CliOption;
import io.swagger.codegen.CodegenConstants;
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
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class AbstractOktaJavaClientCodegen extends AbstractJavaCodegen
        implements BeanValidationFeatures, PerformBeanValidationFeatures, GzipFeatures
{

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

        cliOptions.add(CliOption.newBoolean(PARCELABLE_MODEL, "Whether to generate models for Android that implement Parcelable with the okhttp-gson library."));
        cliOptions.add(CliOption.newBoolean(USE_BEANVALIDATION, "Use BeanValidation API annotations"));
        cliOptions.add(CliOption.newBoolean(PERFORM_BEANVALIDATION, "Perform BeanValidation"));
        cliOptions.add(CliOption.newBoolean(USE_GZIP_FEATURE, "Send gzip-encoded requests"));

        supportedLibraries.put("okhttp-gson", "HTTP client: OkHttp 2.7.5. JSON processing: Gson 2.6.2. Enable Parcelable modles on Android using '-DparcelableModel=true'. Enable gzip request encoding using '-DuseGzipFeature=true'.");

        CliOption libraryOption = new CliOption(CodegenConstants.LIBRARY, "library template (sub-template) to use");
        libraryOption.setEnum(supportedLibraries);
        // set okhttp-gson as the default
        libraryOption.setDefault("okhttp-gson");
        cliOptions.add(libraryOption);
        setLibrary("okhttp-gson");

        apiTemplateFiles.clear();
        modelTemplateFiles.clear();

    }

    @Override
    public void preprocessSwagger(Swagger swagger) {
        vendorExtensions.put("basePath", swagger.getBasePath());
        super.preprocessSwagger(swagger);
        addListModels(swagger);
        moveOperationsToSingleClient(swagger);

        // TODO: https://github.com/okta/openapi/issues/23
        swagger.getPaths().values().forEach(path ->
                path.getOperations().forEach(operation ->
                    operation.getParameters().stream()
                            .filter(param -> "body".equals(param.getName())).forEach(param ->
                                param.setRequired(true)
                            )
                        ));

        // we want to move any operations defined by the 'x-okta-links' vendor extension to the model
        Map<String, Model> modelMap = swagger.getDefinitions().entrySet().stream()
                .filter(e -> e.getValue().getVendorExtensions().containsKey("x-okta-links"))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));


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

                        // now any params that match the models we need to use the model value directly
                        // for example if the path contained {id} we would call getId() instead

                        List<CodegenParameter> cgParamAllList = new ArrayList<>();
                        List<CodegenParameter> cgParamModelList = new ArrayList<>();


                        cgOperation.pathParams.forEach(param -> {
                            if (model.getProperties().containsKey(param.paramName)) {
                                cgParamModelList.add(param);

                                CodegenProperty cgProperty = fromProperty(param.paramName, model.getProperties().get(param.paramName));
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
    public void processOpts() {
        super.processOpts();

        if (additionalProperties.containsKey(PARCELABLE_MODEL)) {
            this.setParcelableModel(Boolean.valueOf(additionalProperties.get(PARCELABLE_MODEL).toString()));
        }
        // put the boolean value back to PARCELABLE_MODEL in additionalProperties
        additionalProperties.put(PARCELABLE_MODEL, parcelableModel);

        if (additionalProperties.containsKey(USE_BEANVALIDATION)) {
            this.setUseBeanValidation(convertPropertyToBooleanAndWriteBack(USE_BEANVALIDATION));
        }

        if (additionalProperties.containsKey(PERFORM_BEANVALIDATION)) {
            this.setPerformBeanValidation(convertPropertyToBooleanAndWriteBack(PERFORM_BEANVALIDATION));
        }

        if (additionalProperties.containsKey(USE_GZIP_FEATURE)) {
            this.setUseGzipFeature(convertPropertyToBooleanAndWriteBack(USE_GZIP_FEATURE));
        }
    }

    /**
     *  Prioritizes consumes mime-type list by moving json-vendor and json mime-types up front, but 
     *  otherwise preserves original consumes definition order. 
     *  [application/vnd...+json,... application/json, ..as is..]  
     *  
     * @param consumes consumes mime-type list
     * @return 
     */
    static List<Map<String, String>> prioritizeContentTypes(List<Map<String, String>> consumes) {
        if ( consumes.size() <= 1 )
            return consumes;
        
        List<Map<String, String>> prioritizedContentTypes = new ArrayList<>(consumes.size());
        
        List<Map<String, String>> jsonVendorMimeTypes = new ArrayList<>(consumes.size());
        List<Map<String, String>> jsonMimeTypes = new ArrayList<>(consumes.size());
        
        for ( Map<String, String> consume : consumes) {
            if ( isJsonVendorMimeType(consume.get(MEDIA_TYPE))) {
                jsonVendorMimeTypes.add(consume);
            }
            else if ( isJsonMimeType(consume.get(MEDIA_TYPE))) {
                jsonMimeTypes.add(consume);
            }
            else
                prioritizedContentTypes.add(consume);
            
            consume.put("hasMore", "true");
        }
        
        prioritizedContentTypes.addAll(0, jsonMimeTypes);
        prioritizedContentTypes.addAll(0, jsonVendorMimeTypes);
        
        prioritizedContentTypes.get(prioritizedContentTypes.size()-1).put("hasMore", null);
        
        return prioritizedContentTypes;
    }
    
    private static boolean isMultipartType(List<Map<String, String>> consumes) {
        Map<String, String> firstType = consumes.get(0);
        if (firstType != null) {
            if ("multipart/form-data".equals(firstType.get(MEDIA_TYPE))) {
                return true;
            }
        }
        return false;
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


    public void setParcelableModel(boolean parcelableModel) {
        this.parcelableModel = parcelableModel;
    }

    public void setUseBeanValidation(boolean useBeanValidation) {
        this.useBeanValidation = useBeanValidation;
    }

    public void setPerformBeanValidation(boolean performBeanValidation) {
        this.performBeanValidation = performBeanValidation;
    }

    public void setUseGzipFeature(boolean useGzipFeature) {
        this.useGzipFeature = useGzipFeature;
    }

    final private static Pattern JSON_MIME_PATTERN = Pattern.compile("(?i)application\\/json(;.*)?");
    final private static Pattern JSON_VENDOR_MIME_PATTERN = Pattern.compile("(?i)application\\/vnd.(.*)+json(;.*)?"); 

    /**
     * Check if the given MIME is a JSON MIME.
     * JSON MIME examples:
     *   application/json
     *   application/json; charset=UTF8
     *   APPLICATION/JSON
     */
    static boolean isJsonMimeType(String mime) {
        return mime != null && ( JSON_MIME_PATTERN.matcher(mime).matches());
    }

    /**
     * Check if the given MIME is a JSON Vendor MIME.
     * JSON MIME examples:
     *   application/vnd.mycompany+json
     *   application/vnd.mycompany.resourceA.version1+json
     */
    static boolean isJsonVendorMimeType(String mime) {
        return mime != null && JSON_VENDOR_MIME_PATTERN.matcher(mime).matches();
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
                        System.out.println("Model: "+ key);
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


    //FIXME, these methods are the keys to dealing with generating lists

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
