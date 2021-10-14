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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.samskivert.mustache.Mustache;
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
import io.swagger.models.RefModel;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerException;
import io.swagger.util.Json;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public abstract class AbstractOktaJavaClientCodegen extends AbstractJavaCodegen {

    private final String codeGenName;

    public static final String API_FILE_KEY = "apiFile";
    private static final String NON_OPTIONAL_PRAMS = "nonOptionalParams";
    static final String X_OPENAPI_V3_SCHEMA_REF = "x-openapi-v3-schema-ref";

    @SuppressWarnings("hiding")
    private final Logger log = LoggerFactory.getLogger(AbstractOktaJavaClientCodegen.class);

    protected Map<String, String> modelTagMap = new HashMap<>();
    protected Set<String> enumList = new HashSet<>();
    protected Map<String, Discriminator> discriminatorMap = new HashMap<>();
    protected Map<String, String> reverseDiscriminatorMap = new HashMap<>();
    protected Set<String> topLevelResources = new HashSet<>();
    protected Map<String, Object> rawSwaggerConfig;

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

        // make sure we have the apiFile location
        String apiFile = (String) additionalProperties.get(API_FILE_KEY);
        if (apiFile == null || apiFile.isEmpty()) {
            throw new SwaggerException("'additionalProperties."+API_FILE_KEY +" property is required. This must be " +
                    "set to the same file that Swagger is using.");
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(apiFile), StandardCharsets.UTF_8.toString())) {
            rawSwaggerConfig = new Yaml().loadAs(reader, Map.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse apiFile: "+ apiFile, e);
        }

        vendorExtensions.put("basePath", swagger.getBasePath());
        super.preprocessSwagger(swagger);
        tagEnums(swagger);
        buildTopLevelResourceList(swagger);
        addListModels(swagger);
        buildModelTagMap(swagger);
        removeListAfterAndLimit(swagger);
        moveOperationsToSingleClient(swagger);
        handleOktaLinkedOperations(swagger);
        buildDiscriminationMap(swagger);
        buildGraalVMReflectionConfig(swagger);
    }

    /**
     * Figure out which models are top level models (directly returned from a endpoint).
     * @param swagger The instance of swagger.
     */
    protected void buildTopLevelResourceList(Swagger swagger) {

        Set<String> resources = new HashSet<>();

        // Loop through all of the operations looking for the models that are used as the response and body params
        swagger.getPaths().forEach((pathName, path) ->
                path.getOperations().forEach(operation -> {
                    // find all body params
                    operation.getParameters().forEach(parameter -> {
                        if (parameter instanceof BodyParameter) {
                            resources.add(((RefModel) ((BodyParameter)parameter).getSchema()).getSimpleRef());
                        }
                    });

                    // response objects are a more complicated, start with filter for only the 200 responses
                    operation.getResponses().entrySet().stream()
                    .filter(entry -> "200".equals(entry.getKey()))
                    .forEach(entry -> {
                        // this schema could be a ref or an array property containing a ref (or null)
                        Property rawSchema = entry.getValue().getSchema();

                        if (rawSchema != null) {
                            RefProperty refProperty;
                            // detect array properties
                            if (rawSchema instanceof ArrayProperty) {
                                Property innerProp = ((ArrayProperty) rawSchema).getItems();
                                if (innerProp instanceof RefProperty) {
                                    refProperty = (RefProperty) innerProp;
                                } else {
                                    // invalid swagger config file
                                    throw new SwaggerException("Expected 'schema.items.$ref' to exist.");
                                }
                            } else if (rawSchema instanceof RefProperty) {
                                // non array, standard ref property typically in the format of '#/Definitions/MyModel'
                                refProperty = (RefProperty) rawSchema;
                            } else {
                                throw new SwaggerException("Expected 'schema' to be of type 'ArrayProperty' or 'RefProperty'.");
                            }

                            // get the simple name 'MyModel' instead of '#/Definitions/MyModel'
                            resources.add(refProperty.getSimpleRef());
                        }
                    });
                })
        );

        // find any children of these resources
        swagger.getDefinitions().forEach((name, model) -> {
            String parent = (String) model.getVendorExtensions().get("x-okta-parent");
            if (parent != null) {
                parent = parent.replaceAll(".*/", "");

                if (resources.contains(parent)) {
                    resources.add(parent);
                }
            }
        });

        // mark each model with a 'top-level' vendorExtension
        resources.stream()
                .map(resourceName -> swagger.getDefinitions().get(resourceName))
                .forEach(model -> {
                    model.getVendorExtensions().put("top-level", true);
                });

        this.topLevelResources = resources;
    }

    protected void buildDiscriminationMap(Swagger swagger) {
        swagger.getDefinitions().forEach((name, model) -> {
            ObjectNode discriminatorMapExtension =
                (ObjectNode) model.getVendorExtensions().get("x-openapi-v3-discriminator");
            if (discriminatorMapExtension != null) {
                String propertyName = discriminatorMapExtension.get("propertyName").asText();
                ObjectNode mapping = (ObjectNode) discriminatorMapExtension.get("mapping");
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> result = mapper.convertValue(mapping, Map.class);
                result = result.entrySet().stream()
                    .collect(
                        Collectors.toMap(
                            e -> e.getValue().substring(e.getValue().lastIndexOf('/') + 1),
                            e -> e.getKey(),
                            (oldValue, newValue) -> newValue
                        )
                    );
                result.forEach((key, value) -> reverseDiscriminatorMap.put(key, name));
                discriminatorMap.put(name, new Discriminator(name, propertyName, result));
            }
        });
    }

    protected void buildGraalVMReflectionConfig(Swagger swagger) {

        try {
            List<Map<String, ?>> reflectionConfig = swagger.getDefinitions().keySet().stream()
                .filter(it -> !enumList.contains(it)) // ignore enums
                .map(this::fqcn)
                .map(this::reflectionConfig)
                .collect(Collectors.toList());

            // this is slightly error prone, but this project only has `api` and `impl`
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

    protected void tagEnums(Swagger swagger) {
        swagger.getDefinitions().forEach((name, model) -> {

            if (model instanceof ModelImpl && ((ModelImpl) model).getEnum() != null) {
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
        // we want to move any operations defined by the 'x-okta-operations' or 'x-okta-crud'
        // or 'x-okta-multi-operation' vendor extension to the model
        Map<String, Model> modelMap = swagger.getDefinitions().entrySet().stream()
                .filter(e -> e.getValue().getVendorExtensions().containsKey("x-okta-operations")
                        || e.getValue().getVendorExtensions().containsKey("x-okta-crud")
                        || e.getValue().getVendorExtensions().containsKey("x-okta-multi-operation"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        modelMap.forEach((k, model) -> {
            List<ObjectNode> linkNodes = new ArrayList<>();

            addAllIfNotNull(linkNodes, (List<ObjectNode>) model.getVendorExtensions().get("x-okta-operations"));
            addAllIfNotNull(linkNodes, (List<ObjectNode>) model.getVendorExtensions().get("x-okta-crud"));
            addAllIfNotNull(linkNodes, (List<ObjectNode>) model.getVendorExtensions().get("x-okta-multi-operation"));

            Map<String, CodegenOperation> operationMap = new HashMap<>();

            linkNodes.forEach(n -> {
                String operationId = n.get("operationId").textValue();

                // find the swagger path operation
                swagger.getPaths().forEach((pathName, path) -> {
                    Optional<Map.Entry<HttpMethod, Operation>> operationEntry =
                        path.getOperationMap().entrySet().stream().filter(
                            oper -> {
                                //Looking for an operationId in paths:path:operationId
                                if (oper.getValue().getOperationId() != null
                                    && oper.getValue().getOperationId().equals(operationId)) {
                                    return true;
                                }
                                //Looking for an operationId in paths:path:method:x-okta-multi-operation:operationId
                                List<Operation> xOktaMultiOperation = getOktaMultiOperationObject(oper.getValue());
                                if (xOktaMultiOperation != null &&
                                    xOktaMultiOperation
                                        .stream()
                                        .anyMatch(multiOper -> multiOper.getOperationId().equals(operationId))
                                    ) {
                                    return true;
                                }
                                return false;
                            }
                        ).findFirst();

                    if (operationEntry.isPresent()) {

                        Operation operation = operationEntry.get().getValue();

                        //Trying to get an Operation from x-okta-multi-operation
                        Operation xOktaMultiOperation = produceOperationFromXOktaMultiOperation(operation, operationId);

                        CodegenOperation cgOperation = fromOperation(
                                pathName,
                                operationEntry.get().getKey().name().toLowerCase(),
                            xOktaMultiOperation != null ? xOktaMultiOperation : operation,
                                swagger.getDefinitions(),
                                swagger);

                        boolean canLinkMethod = true;

                        JsonNode aliasNode = n.get("alias");
                        String alias = null;
                        if (aliasNode != null) {
                            alias = aliasNode.textValue();
                            cgOperation.vendorExtensions.put("alias", alias);

                            if ("update".equals(alias)) {
                                model.getVendorExtensions().put("saveable", true);
                            } else if ("delete".equals(alias)) {
                                model.getVendorExtensions().put("deletable", true);
                                cgOperation.vendorExtensions.put("selfDelete", true);
                            }
                            else if ("read".equals(alias) || "create".equals(alias)) {
                                canLinkMethod = false;
                            }
                        }

                        // we do NOT link read or create methods, those need to be on the parent object
                        if (canLinkMethod) {

                            // now any params that match the models we need to use the model value directly
                            // for example if the path contained {id} we would call getId() instead

                            Map<String, String> argMap = createArgMap(n);

                            List<CodegenParameter> cgOtherPathParamList = new ArrayList<>();
                            List<CodegenParameter> cgParamAllList = new ArrayList<>();
                            List<CodegenParameter> cgParamModelList = new ArrayList<>();

                            cgOperation.pathParams.forEach(param -> {

                                if (argMap.containsKey(param.paramName)) {

                                    String paramName = argMap.get(param.paramName);
                                    cgParamModelList.add(param);

                                    if (model.getProperties() != null) {
                                        CodegenProperty cgProperty = fromProperty(paramName, model.getProperties().get(paramName));
                                        if(cgProperty == null && cgOperation.operationId.equals("deleteLinkedObjectDefinition")) {
                                            cgProperty = new CodegenProperty();
                                            cgProperty.getter = "getPrimary().getName";
                                        }
                                        param.vendorExtensions.put("fromModel", cgProperty);
                                    } else {
                                        System.err.println("Model '" + model.getTitle() + "' has no properties");
                                    }

                                } else {
                                    cgOtherPathParamList.add(param);
                                }
                            });

                            // remove the body param if the body is the object itself
                            for (Iterator<CodegenParameter> iter = cgOperation.bodyParams.iterator(); iter.hasNext(); ) {
                                CodegenParameter bodyParam = iter.next();
                                if (argMap.containsKey(bodyParam.paramName)) {
                                    cgOperation.vendorExtensions.put("bodyIsSelf", true);
                                    iter.remove();
                                }
                            }

                            // do not add the parrent path params to the list (they will be parsed from the href)
                            SortedSet<String> pathParents = parentPathParams(n);
                            cgOtherPathParamList.forEach(param -> {
                                if (!pathParents.contains(param.paramName)) {
                                    cgParamAllList.add(param);
                                }
                            });

                            //do not implement interface Deletable when delete method has some arguments
                            if(alias.equals("delete") && cgParamAllList.size() > 0) {
                                model.getVendorExtensions().put("deletable", false);
                            }

                            if (!pathParents.isEmpty()) {
                                cgOperation.vendorExtensions.put("hasPathParents", true);
                                cgOperation.vendorExtensions.put("pathParents", pathParents);
                            }

                            cgParamAllList.addAll(cgOperation.bodyParams);
                            cgParamAllList.addAll(cgOperation.queryParams);
                            cgParamAllList.addAll(cgOperation.headerParams);

                            // set all params to have more
                            cgParamAllList.forEach(param -> param.hasMore = true);

                            // then grab the last one and mark it as the last
                            if (!cgParamAllList.isEmpty()) {
                                CodegenParameter param = cgParamAllList.get(cgParamAllList.size() - 1);
                                param.hasMore = false;
                            }

                            cgOperation.vendorExtensions.put("allParams", cgParamAllList);
                            cgOperation.vendorExtensions.put("fromModelPathParams", cgParamModelList);

                            addOptionalExtensionAndBackwardCompatibleArgs(cgOperation, cgParamAllList);

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

    private List<Operation> getOktaMultiOperationObject(Operation operation) {
        Object multiOperationObject = operation.getVendorExtensions().get("x-okta-multi-operation");
        List<Operation> xOktaMultiOperationList = new ArrayList<>();
        if (multiOperationObject instanceof List) {
            for(Object node : (List)multiOperationObject) {
                Operation multiOperation = Json.mapper().convertValue(node, Operation.class);
                xOktaMultiOperationList.add(multiOperation);
            }
            return xOktaMultiOperationList;
        }
        return null;
    }

    private Operation produceOperationFromXOktaMultiOperation(Operation operation, String operationId) {

        Operation xOktaMultiOperation = null;

        List<Operation> xOktaMultiOperationList = getOktaMultiOperationObject(operation);
        if (xOktaMultiOperationList != null) {
            Optional<Operation> operationFromXOktaMultiOperation = xOktaMultiOperationList.stream()
                .filter(multiOper -> multiOper.getOperationId().equals(operationId)).findFirst();

            if (operationFromXOktaMultiOperation.isPresent()) {
                Operation xOktaMultiOperationTmp = operationFromXOktaMultiOperation.get();
                xOktaMultiOperation = new Operation();

                // VendorExtensions deep copy
                Map<String, Object> vendorExtensions = new LinkedHashMap<>(operation.getVendorExtensions());
                xOktaMultiOperation.setVendorExtensions(vendorExtensions);

                // Tags deep copy
                List<String> tags = new ArrayList<>(operation.getTags());
                xOktaMultiOperation.setTags(tags);

                xOktaMultiOperation.setSummary(operation.getSummary());
                xOktaMultiOperation.setDescription(xOktaMultiOperationTmp.getDescription());
                xOktaMultiOperation.setOperationId(xOktaMultiOperationTmp.getOperationId());

                // Consumes deep copy
                List<String> consumes = new ArrayList<>(operation.getConsumes());
                xOktaMultiOperation.setConsumes(consumes);

                // Produces deep copy
                List<String> produces = new ArrayList<>(operation.getProduces());
                xOktaMultiOperation.setProduces(produces);

                // Parameters deep copy
                List<Parameter> parameters = new ArrayList<>(operation.getParameters());
                xOktaMultiOperation.setParameters(parameters);

                // Responses deep copy
                Map<String, Response> responses = new LinkedHashMap<>(operation.getResponses());
                xOktaMultiOperation.setResponses(responses);

                // Security deep copy
                List<Map<String, List<String>>> security = new ArrayList<>(operation.getSecurity());
                xOktaMultiOperation.setSecurity(security);

                //Add params defined in x-okta-multi-operation
                for(Parameter p: xOktaMultiOperationTmp.getParameters()) {
                    if (p instanceof BodyParameter && ((BodyParameter) p).getSchema() != null) {
                        xOktaMultiOperation.getParameters().add(p);
                    } else if (!(p instanceof BodyParameter)) {
                        xOktaMultiOperation.getParameters().add(p);
                    }
                }
            }
        }
        return xOktaMultiOperation;
    }

    private Map<String, String> createArgMap(ObjectNode n) {

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
        return argMap;
    }

    private SortedSet<String> parentPathParams(ObjectNode n) {

        SortedSet<String> result = new TreeSet<>();
        ArrayNode argNodeList = (ArrayNode) n.get("arguments");

        if (argNodeList != null) {
            for (JsonNode argNode : argNodeList) {
                if (argNode.has("parentSrc")) {
                    String src = argNode.get("parentSrc").textValue();
                    String dest = argNode.get("dest").textValue();
                    if (src != null) {
                        result.add(dest);
                    }
                }
            }
        }
        return result;
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

        // force alias == false (likely only relevant for Lists, but something changed in swagger 2.2.3 to require this)
        codegenModel.isAlias = false;

        String parent = (String) model.getVendorExtensions().get("x-okta-parent");
        if (StringUtils.isNotEmpty(parent)) {
            codegenModel.parent = toApiName(parent.substring(parent.lastIndexOf("/")));

            // figure out the resourceClass if this model has a parent
            String discriminatorRoot = getRootDiscriminator(name);
            if (discriminatorRoot != null) {
                model.getVendorExtensions().put("discriminatorRoot", discriminatorRoot);
            }

        }

        // We use '$ref' attributes with siblings, which isn't valid JSON schema (or swagger), so we need process
        // additional attributes from the raw schema
        Map<String, Object> modelDef = getRawSwaggerDefinition(name);
        codegenModel.vars.forEach(codegenProperty -> {
            Map<String, Object> rawPropertyMap = getRawSwaggerProperty(modelDef, codegenProperty.baseName);
            codegenProperty.isReadOnly = Boolean.TRUE.equals(rawPropertyMap.get("readOnly"));
        });

       return codegenModel;
    }

    private List<CodegenOperation> sortOperations(Collection<CodegenOperation> operations) {

        return operations.stream()
                .sorted(Comparator.comparing(o -> ((CodegenOperation) o).path)
                                  .thenComparing(o -> ((CodegenOperation) o).httpMethod))
                .collect(Collectors.toList());
    }

    private String getRootDiscriminator(String name) {
        String result = reverseDiscriminatorMap.get(name);

        if (result != null) {
            String parentResult = getRootDiscriminator(result);
            if (parentResult != null) {
                result = parentResult;
            }
        }
        return result;
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
                        .filter(cgParam -> needToImport(cgParam.dataType))
                        .forEach(cgParam -> importsToAdd.add(toModelImport(cgParam.dataType))));

        // add each one as an import
        importsToAdd.forEach(className -> {
            Map<String, String> listImport = new LinkedHashMap<>();
            listImport.put("import", className);
            imports.add(listImport);
        });

        operations.put("operation", sortOperations(codegenOperations));
        return resultMap;
    }


    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        super.postProcessModelProperty(model, property);
        if(!BooleanUtils.toBoolean(model.isEnum)) {

            //Do not use JsonWebKeyList because it's based on Map<K,V> but API require a simple List<JsonWebKey>
            if(model.name.equals("OpenIdConnectApplicationSettingsClientKeys")) {
                property.datatypeWithEnum = property.baseType + "<" + property.complexType + ">";
            }

            if (property.vendorExtensions.containsKey("x-okta-known-values")) {
                String name = StringUtils.remove(WordUtils.capitalizeFully(property.name, '_'), "_");
                property.vendorExtensions.put("x-okta-known-values-exists", true);
                property.vendorExtensions.put("x-okta-known-values-class-name", name + "Values");
            }

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

        model.vendorExtensions.put("optionalClassnamePartial", (Mustache.Lambda) (frag, out) -> {
            String templateResource = "/" + templateDir + "/" + model.classname + ".mustache";
            URL optionalClassnameTemplate = getClass().getResource(templateResource);

            Mustache.Compiler compiler = Mustache.compiler().withLoader((name) -> {
                if (optionalClassnameTemplate != null) {
                    return new InputStreamReader(getClass().getResourceAsStream(templateResource), StandardCharsets.UTF_8);
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

        // Deep copy for vendorExtensions Map
        Map<String, Object> vendorExtensions = new LinkedHashMap<>();
        co.vendorExtensions.forEach(vendorExtensions::put);
        co.vendorExtensions = vendorExtensions;

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
        addOptionalExtensionAndBackwardCompatibleArgs(co, co.allParams);

        // if the body and the return type are the same mark the body param
        co.bodyParams.forEach(bodyParam -> {
            if (bodyParam.dataType.equals(co.returnType)) {
                co.vendorExtensions.put("updateBody", true);
            }
        });

        return co;
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
                CodegenParameter param = nonOptionalParams.get(nonOptionalParams.size() - 1);
                param.hasMore = false;
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
                    && entry.getValue().stream().anyMatch(param -> param.isQueryParam || param.isHeaderParam)) // has query or header params
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (resultVersionedParamsMap.size() > 0) {

                // mark the last parm on each versioned param list
                resultVersionedParamsMap.values().forEach(versionedParams -> {
                    if (!versionedParams.isEmpty()) {
                        CodegenParameter lastItem = versionedParams.get(versionedParams.size() - 1);
                        lastItem.hasMore = false;
                    }
                });

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
                .entrySet().stream()
                .filter(entry -> topLevelResources.contains(entry.getKey()))
                .forEach(entry -> {
                    Model model = entry.getValue();
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
                    if (!languageSpecificPrimitives.contains(baseName) && topLevelResources.contains(baseName)) {

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

        if ("password".equals(p.getFormat())) {
            return "char[]";
        }

        if (p instanceof ArrayProperty) {
            ArrayProperty ap = (ArrayProperty) p;
            Property inner = ap.getItems();
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
        return super.getTypeDeclaration(p);
    }

    private Map<String, Object> castToMap(Object object) {
        return (Map<String, Object>) object;
    }

    protected Map<String, Object> getRawSwaggerDefinition(String name) {
        return castToMap(castToMap(rawSwaggerConfig.get("definitions")).get(name));
    }

    protected Map<String, Object> getRawSwaggerProperty(Map<String, Object> definition, String propertyName) {
        return castToMap(castToMap(definition.get("properties")).get(propertyName));
    }


}
