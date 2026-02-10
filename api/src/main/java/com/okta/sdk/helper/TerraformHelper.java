/*
 * Copyright 2025-Present, Okta, Inc.
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

package com.okta.sdk.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to manage Terraform outputs for tests.
 * 
 * This class is equivalent to the Python SDK's conftest.py pattern,
 * providing access to resources created by Terraform for testing.
 * 
 * Terraform outputs are passed via the TF_OUTPUTS environment variable as JSON.
 * The helper parses this and provides methods to access test data.
 * 
 * Usage:
 *   TerraformHelper helper = new TerraformHelper();
 *   Map<String, Object> testData = helper.getPrerequisiteDataForTest("test_createUser");
 */
public class TerraformHelper {

    private Map<String, Object> terraformOutputs;

    public TerraformHelper() {
        loadTerraformOutputs();
    }

    private void loadTerraformOutputs() {
        // Try to get TF_OUTPUTS from system property first (set by Maven), then fallback to environment variable
        String tfOutputsJson = System.getProperty("tf.outputs");
        if (tfOutputsJson == null || tfOutputsJson.isEmpty()) {
            tfOutputsJson = System.getenv("TF_OUTPUTS");
        }
        
        if (tfOutputsJson == null || tfOutputsJson.isEmpty() || tfOutputsJson.equals("{}")) {
            System.err.println("WARNING: TF_OUTPUTS not found in system properties or environment variable.");
            System.err.println("Tests require Terraform-provisioned resources to run.");
            this.terraformOutputs = new HashMap<>();
            return;
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> outputs = mapper.readValue(tfOutputsJson, Map.class);
            
            if (outputs == null) {
                this.terraformOutputs = new HashMap<>();
                return;
            }
            
            // Extract the 'test_prerequisites' nested output structure
            // Terraform outputs come in the format:
            // {
            //   "test_prerequisites": {
            //     "value": {
            //       "create_application": {...},
            //       "activate_application": {...},
            //       ...
            //     }
            //   }
            // }
            if (outputs.containsKey("test_prerequisites")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> testPrerequisites = (Map<String, Object>) outputs.get("test_prerequisites");
                
                if (testPrerequisites != null && testPrerequisites.containsKey("value")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> value = (Map<String, Object>) testPrerequisites.get("value");
                    this.terraformOutputs = value != null ? value : new HashMap<>();
                } else {
                    this.terraformOutputs = testPrerequisites != null ? testPrerequisites : new HashMap<>();
                }
            } else {
                this.terraformOutputs = outputs;
            }
        } catch (Exception e) {
            System.err.println("Warning: Failed to parse TF_OUTPUTS environment variable: " + e.getMessage());
            this.terraformOutputs = new HashMap<>();
        }
    }

    /**
     * Get all Terraform outputs.
     * 
     * @return Map of terraform outputs
     */
    public Map<String, Object> getTerraformOutputs() {
        return terraformOutputs;
    }

    /**
     * Get prerequisite data for a specific test.
     * 
     * This method looks up test-specific data that was created by Terraform.
     * For example, if a test named "test_createApplication" needs data,
     * this method retrieves the data from the "create_application" key in Terraform outputs
     * (converts camelCase to snake_case and removes "test_" prefix).
     * 
     * If the exact key is not found, tries common variations:
     * - For "add_*" operations, also tries "create_*"
     * - For "update_*" operations, also tries "replace_*" and "modify_*"
     * - For "delete_*" operations, also tries "remove_*"
     * 
     * @param testName the name of the test (e.g., "test_createApplication")
     * @return Map containing test data, or empty map if not found
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getPrerequisiteDataForTest(String testName) {
        if (!hasOutputs()) {
            return new HashMap<>();
        }
        
        // Convert test name from camelCase to snake_case
        // Example: "test_createApplication" -> "create_application"
        String key = convertCamelCaseToSnakeCase(testName);
        
        // Try the exact key first
        if (terraformOutputs.containsKey(key)) {
            Object data = terraformOutputs.get(key);
            if (data instanceof Map) {
                return (Map<String, Object>) data;
            }
        }
        
        // If exact key not found, try variations
        java.util.List<String> keyVariations = new java.util.ArrayList<>();
        
        // Add operation variations
        if (key.startsWith("add_")) {
            // For add_* operations, also try create_*
            keyVariations.add(key.replaceFirst("^add_", "create_"));
        } else if (key.startsWith("create_")) {
            // For create_* operations, also try add_*
            keyVariations.add(key.replaceFirst("^create_", "add_"));
        }
        
        if (key.startsWith("update_")) {
            // For update_* operations, also try replace_* and modify_*
            keyVariations.add(key.replaceFirst("^update_", "replace_"));
            keyVariations.add(key.replaceFirst("^update_", "modify_"));
        } else if (key.startsWith("replace_")) {
            // For replace_* operations, also try update_* and modify_*
            keyVariations.add(key.replaceFirst("^replace_", "update_"));
            keyVariations.add(key.replaceFirst("^replace_", "modify_"));
        }
        
        if (key.startsWith("delete_")) {
            // For delete_* operations, also try remove_*
            keyVariations.add(key.replaceFirst("^delete_", "remove_"));
        } else if (key.startsWith("remove_")) {
            // For remove_* operations, also try delete_*
            keyVariations.add(key.replaceFirst("^remove_", "delete_"));
        }
        
        // Try each variation
        for (String variation : keyVariations) {
            if (terraformOutputs.containsKey(variation)) {
                Object data = terraformOutputs.get(variation);
                if (data instanceof Map) {
                    return (Map<String, Object>) data;
                }
            }
        }
        
        return new HashMap<>();
    }

    /**
     * Convert camelCase test name to snake_case key name.
     * Examples:
     *   "test_createApplication" -> "create_application"
     *   "test_listApplications" -> "list_applications"
     *   "test_activateApplication" -> "activate_application"
     * 
     * @param camelCaseName the camelCase name
     * @return the snake_case name
     */
    private String convertCamelCaseToSnakeCase(String camelCaseName) {
        // Remove "test_" prefix if present
        String name = camelCaseName.startsWith("test_") ? camelCaseName.substring(5) : camelCaseName;
        
        // Convert camelCase to snake_case
        // Insert underscore before uppercase letters and convert to lowercase
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append("_");
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }

    /**
     * Get the payload (request body) from prerequisite data.
     * 
     * @return the payload object, or null if not found
     */
    public Object getPayload() {
        Map<String, Object> data = new HashMap<>(terraformOutputs);
        return data.get("payload");
    }

    /**
     * Get the prerequisite object (e.g., created user/group from Terraform).
     * 
     * @return the prerequisite object, or null if not found
     */
    public Object getPrerequisiteObject() {
        Map<String, Object> data = new HashMap<>(terraformOutputs);
        return data.get("prerequisite_object");
    }

    /**
     * Check if Terraform outputs are available.
     * 
     * @return true if outputs exist, false otherwise
     */
    public boolean hasOutputs() {
        return terraformOutputs != null && !terraformOutputs.isEmpty();
    }

    /**
     * Extract a parameter from prerequisite data with smart field name resolution.
     * 
     * This method handles the common pattern where test code uses camelCase parameter names
     * (e.g., "appId") but Terraform outputs provide snake_case field names (e.g., "app_id").
     * 
     * Field name lookup strategy:
     * 1. Try exact name in payload
     * 2. Try snake_case conversion in payload
     * 3. Try special mappings (e.g., appId -> ["app_id", "id"])
     * 4. Try the same in prerequisite_object
     * 5. For special types (application, user, group), return entire prerequisiteObject
     * 6. Return default value if not found
     * 
     * @param prerequisiteData the test data from Terraform
     * @param parameterName the parameter name to extract (e.g., "appId", "userId")
     * @param defaultValue the default value if parameter not found
     * @return the extracted parameter value, or defaultValue if not found
     */
    public static Object extractParameter(Map<String, Object> prerequisiteData, String parameterName, Object defaultValue) {
        return extractParameter(prerequisiteData, parameterName, defaultValue, null);
    }

    /**
     * Extract a parameter from prerequisite data with optional target class specification.
     * 
     * This overloaded version allows specifying a target class for object deserialization.
     * Useful when a parameter can be multiple model types (e.g., Group vs AddGroupRequest).
     * 
     * @param prerequisiteData the test data from Terraform
     * @param parameterName the parameter name to extract (e.g., "appId", "userId", "group")
     * @param defaultValue the default value if parameter not found
     * @param targetClassName optional fully qualified class name for deserialization (e.g., "com.okta.sdk.resource.model.AddGroupRequest")
     * @return the extracted parameter value, or defaultValue if not found
     */
    @SuppressWarnings("unchecked")
    public static Object extractParameter(Map<String, Object> prerequisiteData, String parameterName, Object defaultValue, String targetClassName) {
        if (prerequisiteData == null || parameterName == null) {
            return defaultValue;
        }
        
        // Field name variations to try for this parameter
        java.util.List<String> fieldNameVariations = new java.util.ArrayList<>();
        
        // 1. Try exact name first
        fieldNameVariations.add(parameterName);
        
        // 2. Try snake_case version (e.g., "appId" -> "app_id")
        fieldNameVariations.add(toSnakeCase(parameterName));
        
        // 3. For any parameter ending with "Id", also try just "id" as a fallback
        if (parameterName.endsWith("Id")) {
            fieldNameVariations.add("id");
        }
        
        Object extractedValue = null;
        
        // 4. Try each field name variation in prerequisite_object FIRST (contains the created resource)
        if (prerequisiteData.containsKey("prerequisite_object")) {
            Object objValue = prerequisiteData.get("prerequisite_object");
            if (objValue instanceof Map) {
                Map<String, Object> prerequisiteObject = (Map<String, Object>) objValue;
                if (!prerequisiteObject.isEmpty()) {
                    for (String fieldName : fieldNameVariations) {
                        if (prerequisiteObject.containsKey(fieldName)) {
                            extractedValue = prerequisiteObject.get(fieldName);
                            break;
                        }
                    }
                }
            }
        }
        
        // 5. Try each field name variation in payload SECOND (contains request/response data)
        if (extractedValue == null && prerequisiteData.containsKey("payload")) {
            Object payloadValue = prerequisiteData.get("payload");
            if (payloadValue instanceof Map) {
                Map<String, Object> payload = (Map<String, Object>) payloadValue;
                if (!payload.isEmpty()) {
                    for (String fieldName : fieldNameVariations) {
                        if (payload.containsKey(fieldName)) {
                            extractedValue = payload.get(fieldName);
                            break;
                        }
                    }
                }
            }
        }
        
        // 6. If we found a value and it's a string, try to convert it to an enum if possible
        if (extractedValue != null && extractedValue instanceof String) {
            Object enumValue = tryConvertStringToEnum((String) extractedValue, parameterName);
            if (enumValue != null && enumValue != extractedValue) {
                extractedValue = enumValue;
            }
        }
        
        // 7. Generic handling for object types - try targetClassName if provided,
        // otherwise try to deserialize from the data object generically
        if (extractedValue == null && targetClassName != null && !targetClassName.isEmpty()) {
            // If explicit target class is provided, use it
            Object objectToDeserialize = prerequisiteData.get(parameterName);
            
            if (objectToDeserialize == null || isEmptyMap(objectToDeserialize)) {
                // If the direct parameter is empty/missing, try payload
                objectToDeserialize = prerequisiteData.getOrDefault("payload", null);
            }
            
            if (objectToDeserialize == null) {
                // If still no data, try prerequisite_object
                objectToDeserialize = prerequisiteData.getOrDefault("prerequisite_object", null);
            }
            
            if (objectToDeserialize != null) {
                extractedValue = convertMapToSdkObject(objectToDeserialize, targetClassName);
            }
        }
        
        return extractedValue != null ? extractedValue : defaultValue;
    }

    /**
     * Extract a parameter with optional type hint for intelligent class inference.
     * This overload supports the generated test templates that need intelligent type deserialization.
     *
     * @param prerequisiteData the Terraform prerequisite data Map
     * @param parameterName the name of the parameter to extract
     * @param defaultValue the default value if parameter not found
     * @param targetClassName optional fully qualified class name for deserialization
     * @param typeHint optional parameter name hint for intelligent type inference (e.g., "certificate" -> "DomainCertificate")
     * @return the extracted parameter value, or defaultValue if not found
     */
    public static Object extractParameter(Map<String, Object> prerequisiteData, String parameterName, Object defaultValue, String targetClassName, String typeHint) {
        // If targetClassName is provided and not empty, use it directly
        if (targetClassName != null && !targetClassName.isEmpty()) {
            return extractParameter(prerequisiteData, parameterName, defaultValue, targetClassName);
        }
        
        // Otherwise, use the typeHint to infer the target class name
        if (typeHint != null && !typeHint.isEmpty()) {
            String inferredClassName = inferClassNameFromTypeHint(typeHint);
            if (inferredClassName != null) {
                return extractParameter(prerequisiteData, parameterName, defaultValue, inferredClassName);
            }
        }
        
        // Fall back to basic extraction without type conversion
        return extractParameter(prerequisiteData, parameterName, defaultValue);
    }

    /**
     * Convert a Map (typically from Terraform JSON) to an SDK model object via Jackson deserialization.
     * Similar to the Python conftest pattern where raw dictionaries are converted to model objects.
     * 
     * This method attempts to deserialize to the target class. If that fails, it tries common
     * alternative class names (e.g., for a class "Group", it also tries "AddGroupRequest",
     * "UpdateGroupRequest", etc.) to handle cases where request models and response models
     * have similar structure but different names.
     * 
     * When a String value is provided for an object type, it wraps the string into a Map
     * using the inferred property name (e.g., string certificate → {certificate: "..."} → DomainCertificate).
     * 
     * @param obj the object to convert (typically a Map/LinkedHashMap from Terraform JSON)
     * @param targetClassName the fully qualified class name of the target SDK model
     * @return the converted SDK object, or the original object if conversion fails
     */
    public static Object convertMapToSdkObject(Object obj, String targetClassName) {
        return convertMapToSdkObject(obj, targetClassName, null);
    }
    
    /**
     * Internal version of convertMapToSdkObject with typeHint support for String wrapping.
     */
    @SuppressWarnings("unchecked")
    public static Object convertMapToSdkObject(Object obj, String targetClassName, String typeHint) {
        if (obj == null) {
            return null;
        }
        
        // If it's already the right type, return as-is
        try {
            Class<?> targetClass = Class.forName(targetClassName);
            if (targetClass.isInstance(obj)) {
                return obj;
            }
        } catch (ClassNotFoundException e) {
            // Class not found, try conversion anyway
        }
        
        try {
            Class<?> targetClass = Class.forName(targetClassName);
            
            // Handle enum types - try to convert string to enum
            if (targetClass.isEnum() && obj instanceof String) {
                String stringValue = (String) obj;
                // Try to find enum constant with this value
                try {
                    Class<Enum<?>> enumClass = (Class<Enum<?>>) (Object) targetClass;
                    Object[] constants = enumClass.getEnumConstants();
                    for (Object constant : constants) {
                        if (constant.toString().equals(stringValue)) {
                            return constant;
                        }
                    }
                    return obj;
                } catch (Exception e) {
                    // Enum constant not found, return original
                    return obj;
                }
            }
        } catch (ClassNotFoundException e) {
            // Will handle below
        }
        
        if (!(obj instanceof Map)) {
            return obj;  // Not a map, can't convert via JSON
        }
        
        try {
            // Convert Map to target SDK class using Jackson
            ObjectMapper mapper = new ObjectMapper();
            // Lenient deserialization: ignore unknown properties
            mapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            // Also allow empty beans
            mapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
            
            String json = mapper.writeValueAsString(obj);
            
            // Try the target class first
            try {
                Class<?> targetClass = Class.forName(targetClassName);
                return mapper.readValue(json, targetClass);
            } catch (Exception primaryException) {
                // Primary target class failed, try alternative class names
                String[] alternativeNames = generateAlternativeClassNames(targetClassName);
                
                for (String altClassName : alternativeNames) {
                    try {
                        Class<?> altClass = Class.forName(altClassName);
                        Object result = mapper.readValue(json, altClass);
                        // Success! Return the result
                        return result;
                    } catch (Exception altException) {
                        // This alternative didn't work, try next one
                        continue;
                    }
                }
                
                // All attempts failed, return the original object as-is
                // The caller (test) will need to handle the type mismatch
                return obj;
            }
        } catch (Exception e) {
            // If all conversion attempts fail, return the original object
            return obj;
        }
    }
    
    /**
     * Generate alternative class names to try when deserialization fails.
     * For example, for "com.okta.sdk.resource.model.Group", it generates:
     *   - "...model.AddGroupRequest"
     *   - "...model.UpdateGroupRequest"
     *   - etc.
     * 
     * This handles the case where request models (AddXxx, UpdateXxx) have similar
     * structure to response models (Xxx) but different names.
     * 
     * @param targetClassName the target class name
     * @return array of alternative class names to try
     */
    private static String[] generateAlternativeClassNames(String targetClassName) {
        java.util.List<String> alternatives = new java.util.ArrayList<>();
        
        // Extract the simple class name (e.g., "Group" from "com.okta.sdk.resource.model.Group")
        String simpleClassName = targetClassName.substring(targetClassName.lastIndexOf('.') + 1);
        String packageName = targetClassName.substring(0, targetClassName.lastIndexOf('.'));
        
        // Generate common alternative names for request models
        String[] patterns = {
            "Add{0}Request",      // AddGroupRequest, AddApplicationRequest, etc.
            "Update{0}Request",   // UpdateGroupRequest, UpdateApplicationRequest, etc.
            "Create{0}Request",   // CreateGroupRequest, CreateApplicationRequest, etc.
            "{0}Request",         // GroupRequest, ApplicationRequest, etc.
            "Okta{0}",           // OktaGroup, OktaApplication, etc.
        };
        
        for (String pattern : patterns) {
            String altName = pattern.replace("{0}", simpleClassName);
            // Only add if different from original
            if (!altName.equals(simpleClassName)) {
                alternatives.add(packageName + "." + altName);
            }
        }
        
        return alternatives.toArray(new String[0]);
    }

    /**
     * Try to convert a string value to an enum type.
     * Attempts to find an enum type in the com.okta.sdk.resource.model package that matches the parameter name
     * and can convert the string to that enum.
     * 
     * Examples:
     *   "ACTIVE", "lifecycle" -> FeatureLifecycle.ACTIVE
     *   "OKTA", "type" -> ApplicationType.OKTA
     * 
     * @param stringValue the string value to convert
     * @param parameterName the parameter name (used to infer the enum class name)
     * @return the converted enum value, or null if conversion fails
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object tryConvertStringToEnum(String stringValue, String parameterName) {
        if (stringValue == null || stringValue.isEmpty() || parameterName == null) {
            return null;
        }
        
        // Generate possible enum class names based on the parameter name
        // Examples: "lifecycle" -> "FeatureLifecycle", "type" -> "ApplicationType", etc.
        java.util.List<String> possibleEnumClasses = new java.util.ArrayList<>();
        
        // Try to convert camelCase/snake_case parameter name to PascalCase enum name
        String pascalCaseName = toPascalCase(parameterName);
        
        // Common patterns for enum class names in the SDK
        possibleEnumClasses.add("com.okta.sdk.resource.model." + pascalCaseName);
        
        // If the parameter ends with "Lifecycle", it's likely a *Lifecycle enum
        if (parameterName.contains("lifecycle") || parameterName.contains("Lifecycle")) {
            possibleEnumClasses.add("com.okta.sdk.resource.model.FeatureLifecycle");
            possibleEnumClasses.add("com.okta.sdk.resource.model.ApplicationLifecycle");
        }
        
        // Try each possible enum class
        for (String enumClassName : possibleEnumClasses) {
            try {
                Class<?> enumClass = Class.forName(enumClassName);
                
                if (enumClass.isEnum()) {
                    try {
                        // Try direct enum constant lookup
                        return Enum.valueOf((Class) enumClass, stringValue);
                    } catch (IllegalArgumentException e1) {
                        // Try uppercase version
                        try {
                            return Enum.valueOf((Class) enumClass, stringValue.toUpperCase());
                        } catch (IllegalArgumentException e2) {
                            // Try with "fromValue" method if it exists (some enums use this pattern)
                            try {
                                java.lang.reflect.Method fromValueMethod = enumClass.getMethod("fromValue", String.class);
                                return fromValueMethod.invoke(null, stringValue);
                            } catch (Exception e3) {
                                // Continue to next class
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                // Class doesn't exist, try next one
            }
        }
        
        return null;
    }

    /**
     * Convert camelCase/snake_case string to PascalCase.
     * Examples:
     *   "lifecycle" -> "Lifecycle"
     *   "app_type" -> "AppType"
     *   "featureId" -> "FeatureId"
     * 
     * @param input the input string
     * @return the PascalCase string
     */
    private static String toPascalCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        // First convert to camelCase if it's snake_case
        String camelCase = input;
        if (input.contains("_")) {
            StringBuilder sb = new StringBuilder();
            boolean capitalizeNext = false;
            for (char c : input.toCharArray()) {
                if (c == '_') {
                    capitalizeNext = true;
                } else if (capitalizeNext) {
                    sb.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    sb.append(c);
                }
            }
            camelCase = sb.toString();
        }
        
        // Now capitalize first letter
        return camelCase.substring(0, 1).toUpperCase() + camelCase.substring(1);
    }

    /**
     * Convert camelCase string to snake_case.
     * Examples:
     *   "appId" -> "app_id"
     *   "userId" -> "user_id"
     *   "groupId" -> "group_id"
     * 
     * @param camelCase the camelCase string
     * @return the snake_case string
     */
    private static String toSnakeCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append("_");
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }

    /**
     * Check if a Map is empty (has no entries).
     * Handles both Map objects and other types.
     * 
     * @param obj the object to check
     * @return true if the object is an empty Map, false otherwise
     */
    @SuppressWarnings("unchecked")
    private static boolean isEmptyMap(Object obj) {
        if (obj instanceof Map) {
            return ((Map<String, Object>) obj).isEmpty();
        }
        return false;
    }

    /**
     * Extract an ID from an object, similar to Python's PresetHelper.get_id().
     * Tries multiple common ID field names: id, app_id, user_id, group_id, etc.
     * 
     * @param obj the object to extract the ID from
     * @param preferredKey the preferred key to look for
     * @return the ID value, or null if not found
     */
    @SuppressWarnings("unchecked")
    public static Object getId(Object obj, String preferredKey) {
        if (obj == null) {
            return null;
        }
        
        if (!(obj instanceof Map)) {
            return obj;  // Return the object itself if it's already an ID
        }
        
        Map<String, Object> map = (Map<String, Object>) obj;
        
        // Try the preferred key first
        if (preferredKey != null && map.containsKey(preferredKey)) {
            return map.get(preferredKey);
        }
        
        // Try common ID field names
        String[] idKeys = {"id", "app_id", "user_id", "group_id", "oauth_app_id", "policy_id", "server_id"};
        for (String key : idKeys) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        
        return null;
    }

    /**
     * Convert snake_case string to camelCase.
     * Examples:
     *   "certificate" -> "certificate"
     *   "certificate_chain" -> "certificateChain"
     *   "app_id" -> "appId"
     * 
     * @param snakeCase the snake_case string
     * @return the camelCase string
     */
    private static String toCamelCase(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return snakeCase;
        }
        
        // If there are no underscores, it's already in camelCase form
        if (!snakeCase.contains("_")) {
            return snakeCase;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }

    /**
     * Infer the SDK model class name from a parameter type hint.
     * 
     * For example: "certificate" -> "com.okta.sdk.resource.model.DomainCertificate"
     * 
     * Uses pattern matching to generate likely class names and validates them.
     * 
     * @param typeHint the parameter name hint (e.g., "certificate", "domain", "group")
     * @return the fully qualified class name if found, or null if not found
     */
    private static String inferClassNameFromTypeHint(String typeHint) {
        if (typeHint == null || typeHint.isEmpty()) {
            return null;
        }
        
        String modelPrefix = "com.okta.sdk.resource.model.";
        
        // Generate list of possible class names from the typeHint
        java.util.List<String> possibleClassNames = new java.util.ArrayList<>();
        
        // Pattern 1: Direct camelCase (e.g., "certificate" -> "Certificate")
        String camelCase = toCamelCase(typeHint);
        String directName = camelCase.substring(0, 1).toUpperCase() + camelCase.substring(1);
        possibleClassNames.add(modelPrefix + directName);
        
        // Pattern 2: With "Response" suffix (e.g., "email" -> "EmailResponse")
        possibleClassNames.add(modelPrefix + directName + "Response");
        
        // Pattern 3: With "Request" suffix
        possibleClassNames.add(modelPrefix + directName + "Request");
        
        // Pattern 4: With "Data" suffix
        possibleClassNames.add(modelPrefix + directName + "Data");
        
        // Try each possible class name
        for (String className : possibleClassNames) {
            try {
                Class.forName(className);
                return className; // Found it!
            } catch (ClassNotFoundException e) {
                // Try next pattern
            }
        }
        
        return null; // None of the patterns matched
    }
}
