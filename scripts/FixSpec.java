// Pre-process the OpenAPI spec before code generation.
//
// Mirrors the role of okta-sdk-nodejs's scripts/fixSpec.cjs: normalize spec
// constructs that are valid OpenAPI but crash or mis-generate with
// openapi-generator, so the checked-in/refreshed spec can be regenerated
// without hand-patching the source YAML.
//
// Fixes applied (see GENERATION_FIXES.md for the full write-up of each):
//
// 1. discriminator-missing-enum: a schema has `discriminator.mapping`
//    pointing at sibling/child schemas (via oneOf/anyOf, not necessarily
//    allOf inheritance). openapi-generator's setEnumDiscriminatorDefaultValue
//    requires the discriminator propertyName on each mapped schema to be an
//    enum so it can resolve a default value; when it is a bare
//    `type: string` with no `enum:`, DefaultCodegen.getEnumValueForProperty
//    NPEs ("var.allowableValues" is null) and generation aborts entirely.
//    Fix: inject `enum: [<mapping-key>]` on that property when missing -
//    but only when the property is declared directly on the child (not
//    inherited via allOf), matching the exact condition that crashes.
//
// Run with JDK 11+'s single-file source launcher, e.g.:
//   java -cp <snakeyaml jar> FixSpec.java --input a.yaml --output b.yaml
//
// Depends on org.yaml:snakeyaml (already a build dependency of this project
// for openapi-generator-maven-plugin) being on the classpath.

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

public class FixSpec {

    record Fix(String baseName, String childName, String propName, String discValue) {
        String describe() {
            return "discriminator-missing-enum: " + baseName + " -> " + childName + "." + propName
                + " had no enum; injected enum: [" + discValue + "]";
        }
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> asMap(Object o) {
        return (o instanceof Map) ? (Map<String, Object>) o : null;
    }

    // Finds `propName` directly on schema's own `properties`, deliberately NOT
    // resolving through `allOf` inheritance. openapi-generator only visits a
    // model's own (non-inherited) vars when resolving discriminator enum
    // defaults; a child that inherits the property via `allOf: [$ref: Parent]`
    // never hits that code path for it. Resolving through allOf here would
    // also risk mutating the shared parent schema's property object.
    static Map<String, Object> findOwnProperty(Map<String, Object> schema, String propName) {
        Map<String, Object> props = asMap(schema.get("properties"));
        return props == null ? null : asMap(props.get(propName));
    }

    static List<Fix> fixDiscriminatorMissingEnum(Map<String, Object> spec) {
        List<Fix> fixes = new ArrayList<>();
        Map<String, Object> components = asMap(spec.get("components"));
        if (components == null) {
            return fixes;
        }
        Map<String, Object> schemas = asMap(components.get("schemas"));
        if (schemas == null) {
            return fixes;
        }

        for (Map.Entry<String, Object> entry : schemas.entrySet()) {
            Map<String, Object> schema = asMap(entry.getValue());
            if (schema == null) {
                continue;
            }
            Map<String, Object> discriminator = asMap(schema.get("discriminator"));
            if (discriminator == null) {
                continue;
            }
            Map<String, Object> mapping = asMap(discriminator.get("mapping"));
            if (mapping == null) {
                continue;
            }
            String propName = (String) discriminator.get("propertyName");

            for (Map.Entry<String, Object> mapEntry : mapping.entrySet()) {
                String discValue = mapEntry.getKey();
                String ref = String.valueOf(mapEntry.getValue());
                String childName = ref.substring(ref.lastIndexOf('/') + 1);
                Map<String, Object> child = asMap(schemas.get(childName));
                if (child == null) {
                    continue;
                }
                Map<String, Object> prop = findOwnProperty(child, propName);
                if (prop == null) {
                    continue;
                }
                if (!"string".equals(prop.get("type"))) {
                    continue;
                }
                if (prop.containsKey("enum")) {
                    continue;
                }
                prop.put("enum", new ArrayList<>(List.of(discValue)));
                fixes.add(new Fix(entry.getKey(), childName, propName, discValue));
            }
        }
        return fixes;
    }

    // Resolves a schema's `allOf` into its ($ref) parent schema and its own
    // inline `{properties: {...}}` branch, or (null, null) if the schema
    // doesn't have that exact shape.
    record ParentAndOwnBranch(Map<String, Object> parent, Map<String, Object> ownBranch) {}

    static ParentAndOwnBranch resolveAllOfParentAndOwnBranch(Map<String, Object> schemas, Map<String, Object> schema) {
        Object allOfObj = schema.get("allOf");
        if (!(allOfObj instanceof List)) {
            return new ParentAndOwnBranch(null, null);
        }
        Map<String, Object> parent = null;
        Map<String, Object> ownBranch = null;
        for (Object branch : (List<?>) allOfObj) {
            Map<String, Object> branchMap = asMap(branch);
            if (branchMap == null) {
                continue;
            }
            if (branchMap.containsKey("$ref")) {
                String ref = String.valueOf(branchMap.get("$ref"));
                parent = asMap(schemas.get(ref.substring(ref.lastIndexOf('/') + 1)));
            } else if (branchMap.containsKey("properties")) {
                ownBranch = branchMap;
            }
        }
        return new ParentAndOwnBranch(parent, ownBranch);
    }

    // 2. override-type-mismatch: a schema composed via `allOf: [$ref: Parent,
    //    {properties: {...}}]` redeclares a property the parent already
    //    declares, in a shape Java can't treat as a valid method override:
    //      a) the child's own property has no `type`/`$ref`/allOf/oneOf/anyOf
    //         at all (e.g. only `example:`), so the generator infers some
    //         other type (or emits the raw example as an invalid unquoted
    //         Java literal) instead of the parent's actual declared type; or
    //      b) both parent and child declare the property as a Map
    //         (`type: object` + `additionalProperties`), but the child's map
    //         *value* schema has its own non-empty `properties`, so the
    //         generator synthesizes a distinct named value class for the
    //         child (e.g. `AccessPolicyAllOfEmbedded`) instead of reusing the
    //         parent's generic `Map<String, Object>` - an incompatible
    //         covariant return type.
    //    Fix: replace the child's property declaration with the parent's
    //    declaration verbatim, so the generated accessor is exactly the same
    //    type on both sides (always a valid, trivial override).
    static List<String> fixOverrideTypeMismatch(Map<String, Object> spec) {
        List<String> fixes = new ArrayList<>();
        Map<String, Object> components = asMap(spec.get("components"));
        if (components == null) {
            return fixes;
        }
        Map<String, Object> schemas = asMap(components.get("schemas"));
        if (schemas == null) {
            return fixes;
        }

        for (Map.Entry<String, Object> entry : schemas.entrySet()) {
            Map<String, Object> schema = asMap(entry.getValue());
            if (schema == null) {
                continue;
            }
            ParentAndOwnBranch resolved = resolveAllOfParentAndOwnBranch(schemas, schema);
            if (resolved.parent() == null || resolved.ownBranch() == null) {
                continue;
            }
            Map<String, Object> parentProps = asMap(resolved.parent().get("properties"));
            Map<String, Object> ownProps = asMap(resolved.ownBranch().get("properties"));
            if (parentProps == null || ownProps == null) {
                continue;
            }

            for (Map.Entry<String, Object> propEntry : ownProps.entrySet()) {
                String propName = propEntry.getKey();
                Map<String, Object> childProp = asMap(propEntry.getValue());
                Map<String, Object> parentProp = asMap(parentProps.get(propName));
                if (childProp == null || parentProp == null) {
                    continue;
                }

                boolean childHasTypeHint = childProp.containsKey("type") || childProp.containsKey("$ref")
                    || childProp.containsKey("allOf") || childProp.containsKey("oneOf") || childProp.containsKey("anyOf");

                // Both sides are Map-like (type: object + additionalProperties), but the
                // child ALSO declares its own top-level `properties` (explicit named
                // fields alongside the map's free-form extras) while the parent doesn't -
                // this makes the generator synthesize a dedicated named value class for
                // the child (e.g. AccessPolicyAllOfEmbedded) instead of reusing the
                // parent's generic Map<String, Object>.
                boolean childPropsNonEmpty = childProp.get("properties") instanceof Map
                    && !((Map<?, ?>) childProp.get("properties")).isEmpty();
                boolean parentPropsEmpty = !(parentProp.get("properties") instanceof Map)
                    || ((Map<?, ?>) parentProp.get("properties")).isEmpty();
                boolean bothAreMapsWithDivergentValueShape = "object".equals(childProp.get("type"))
                    && "object".equals(parentProp.get("type"))
                    && childProp.containsKey("additionalProperties")
                    && parentProp.containsKey("additionalProperties")
                    && childPropsNonEmpty
                    && parentPropsEmpty;

                if (!childHasTypeHint || bothAreMapsWithDivergentValueShape) {
                    propEntry.setValue(new LinkedHashMap<>(parentProp));
                    fixes.add("override-type-mismatch: " + entry.getKey() + "." + propName
                        + " redeclared its parent's property in a Java-incompatible shape; replaced with parent's declaration");
                }
            }
        }
        return fixes;
    }

    // 2. missing-type-for-map: a schema declares `additionalProperties` (i.e. it
    //    is a Map) but has no `type: object` of its own (and isn't a $ref). The
    //    generator can't resolve a concrete Map value class without it, so
    //    helper methods like `putXxxItem` end up with an empty/unresolvable
    //    initializer (`this.embedded = ;`) instead of `new HashMap<>()`.
    //    Fix: inject `type: object` alongside the existing `additionalProperties`.
    //
    // 3. missing-type-for-scalar: a property has no `type`, no `$ref`, and no
    //    allOf/oneOf/anyOf, but does carry `example` and/or `enum` - i.e. it was
    //    clearly meant to be a scalar but the `type:` line is missing. Left
    //    alone, the generator either emits the raw `example` value unquoted as
    //    a Java literal (e.g. `JsonNullable.<Object>of(token:hotp)` - invalid
    //    syntax), or infers a type for a child schema's redeclared property
    //    that silently disagrees with the parent's declared type, producing
    //    "incompatible return type" override errors between parent/child
    //    accessor methods. Fix: inject a `type:` inferred from the enum/example
    //    value's own YAML scalar kind (defaulting to string).
    static String inferScalarType(Object value) {
        if (value instanceof Boolean) {
            return "boolean";
        }
        if (value instanceof Integer || value instanceof Long) {
            return "integer";
        }
        if (value instanceof Double || value instanceof Float) {
            return "number";
        }
        return "string";
    }

    static void walkSchemaNodes(Object node, java.util.function.Consumer<Map<String, Object>> visitor) {
        if (node instanceof Map) {
            Map<String, Object> map = asMap(node);
            visitor.accept(map);
            for (Object value : map.values()) {
                walkSchemaNodes(value, visitor);
            }
        } else if (node instanceof List) {
            for (Object item : (List<?>) node) {
                walkSchemaNodes(item, visitor);
            }
        }
    }

    // Like walkSchemaNodes, but tracks whether the current node is itself a
    // direct member of an enclosing `allOf:` list - i.e. a composition branch
    // meant to merge with its siblings (commonly used to narrow/extend an
    // inherited type, e.g. `status: {allOf: [{$ref: LifecycleStatus}, {enum:
    // [DISABLED]}]}`). Such branches routinely omit `type` on purpose because
    // they inherit it from a sibling in the same allOf list; treating them as
    // a standalone type-less scalar (as fixMissingTypeForScalar must NOT do)
    // makes the generator synthesize a separate, wrongly-named enum type.
    static void walkSchemaNodesTrackingAllOf(
            Object node, boolean isAllOfBranch, java.util.function.BiConsumer<Map<String, Object>, Boolean> visitor) {
        if (node instanceof Map) {
            Map<String, Object> map = asMap(node);
            visitor.accept(map, isAllOfBranch);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                boolean childIsAllOfBranch = "allOf".equals(entry.getKey()) && entry.getValue() instanceof List;
                if (childIsAllOfBranch) {
                    for (Object item : (List<?>) entry.getValue()) {
                        walkSchemaNodesTrackingAllOf(item, true, visitor);
                    }
                } else {
                    walkSchemaNodesTrackingAllOf(entry.getValue(), false, visitor);
                }
            }
        } else if (node instanceof List) {
            for (Object item : (List<?>) node) {
                walkSchemaNodesTrackingAllOf(item, isAllOfBranch, visitor);
            }
        }
    }

    // 4. single-item-allof-map-wrapper: a property is declared as
    //    `{allOf: [{additionalProperties: {...}}]}` - a redundant one-item
    //    allOf around what is really just a map schema. A 1-item allOf is
    //    semantically identical to its single member, but this generator
    //    version does not always flatten it when deciding whether the
    //    property itself is a Map, leaving `putXxxItem`-style helper methods
    //    unable to resolve a concrete value type to instantiate (emitting
    //    `this.embedded = ;` with no right-hand side). Fix: flatten the
    //    wrapper by merging the single allOf member's keys directly onto the
    //    property and dropping the `allOf` list, exactly as if it had been
    //    written inline in the first place.
    static List<String> fixSingleItemAllOfMapWrapper(Map<String, Object> spec) {
        List<String> fixes = new ArrayList<>();
        Map<String, Object> components = asMap(spec.get("components"));
        if (components == null) {
            return fixes;
        }
        Map<String, Object> schemas = asMap(components.get("schemas"));
        if (schemas == null) {
            return fixes;
        }
        for (Map.Entry<String, Object> entry : schemas.entrySet()) {
            walkSchemaNodes(entry.getValue(), node -> {
                Object allOfObj = node.get("allOf");
                if (!(allOfObj instanceof List) || ((List<?>) allOfObj).size() != 1) {
                    return;
                }
                Map<String, Object> onlyMember = asMap(((List<?>) allOfObj).get(0));
                if (onlyMember == null || !onlyMember.containsKey("additionalProperties")) {
                    return;
                }
                node.remove("allOf");
                for (Map.Entry<String, Object> memberEntry : onlyMember.entrySet()) {
                    node.putIfAbsent(memberEntry.getKey(), memberEntry.getValue());
                }
                fixes.add("single-item-allof-map-wrapper: " + entry.getKey()
                    + " had a map schema redundantly wrapped in a 1-item allOf; flattened it");
            });
        }
        return fixes;
    }

    static List<String> fixMissingTypeForMap(Map<String, Object> spec) {
        List<String> fixes = new ArrayList<>();
        Map<String, Object> components = asMap(spec.get("components"));
        if (components == null) {
            return fixes;
        }
        Map<String, Object> schemas = asMap(components.get("schemas"));
        if (schemas == null) {
            return fixes;
        }
        for (Map.Entry<String, Object> entry : schemas.entrySet()) {
            walkSchemaNodes(entry.getValue(), node -> {
                if (node.containsKey("additionalProperties") && !node.containsKey("type") && !node.containsKey("$ref")) {
                    node.put("type", "object");
                    fixes.add("missing-type-for-map: " + entry.getKey()
                        + " had a schema with additionalProperties but no type; injected type: object");
                }
            });
        }
        return fixes;
    }

    static List<String> fixMissingTypeForScalar(Map<String, Object> spec) {
        List<String> fixes = new ArrayList<>();
        Map<String, Object> components = asMap(spec.get("components"));
        if (components == null) {
            return fixes;
        }
        Map<String, Object> schemas = asMap(components.get("schemas"));
        if (schemas == null) {
            return fixes;
        }
        for (Map.Entry<String, Object> entry : schemas.entrySet()) {
            walkSchemaNodesTrackingAllOf(entry.getValue(), false, (node, isAllOfBranch) -> {
                if (isAllOfBranch) {
                    return;
                }
                boolean hasTypeHint = node.containsKey("type") || node.containsKey("$ref")
                    || node.containsKey("allOf") || node.containsKey("oneOf") || node.containsKey("anyOf");
                if (hasTypeHint) {
                    return;
                }
                Object example = node.get("example");
                List<?> enumValues = node.get("enum") instanceof List ? (List<?>) node.get("enum") : null;
                if (example == null && (enumValues == null || enumValues.isEmpty())) {
                    return;
                }
                Object sample = enumValues != null ? enumValues.get(0) : example;
                String inferred = inferScalarType(sample);
                node.put("type", inferred);
                // A type-less property whose only signal is a single `example` (no
                // `enum`) reads as "this subtype's value is always exactly this
                // literal" - the same intent as a single-value enum. Express it as
                // one: openapi-generator renders enum constants through a properly
                // string-escaping code path, whereas raw `example` values are
                // (for this generator version) emitted as unquoted Java literals,
                // which is invalid syntax for anything containing e.g. a ':'.
                String fixKind = "missing-type-for-scalar";
                if (enumValues == null) {
                    node.put("enum", new ArrayList<>(List.of(example)));
                    fixKind = "missing-type-and-enum-for-scalar";
                }
                fixes.add(fixKind + ": " + entry.getKey()
                    + " had a property with no type but example/enum present; injected type: " + inferred
                    + (enumValues == null ? " and enum: [" + example + "]" : ""));
            });
        }
        return fixes;
    }



    static String requireArg(Map<String, String> args, String name) {
        String value = args.get(name);
        if (value == null) {
            throw new IllegalArgumentException("missing required --" + name);
        }
        return value;
    }

    static Map<String, String> parseArgs(String[] argv) {
        Map<String, String> args = new LinkedHashMap<>();
        for (int i = 0; i < argv.length - 1; i += 2) {
            String key = argv[i];
            if (!key.startsWith("--")) {
                throw new IllegalArgumentException("expected flag, got: " + key);
            }
            args.put(key.substring(2), argv[i + 1]);
        }
        return args;
    }

    public static void main(String[] argv) throws IOException {
        Map<String, String> args = parseArgs(argv);
        Path input = Paths.get(requireArg(args, "input"));
        Path output = Paths.get(requireArg(args, "output"));
        String logPath = args.get("log");

        // Match the project's own codepoint-limit workaround (see .mvn/jvm.config);
        // SnakeYAML's Java API only honors this via LoaderOptions, not a system property.
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setCodePointLimit(3_500_000);
        Yaml yaml = new Yaml(loaderOptions);
        Map<String, Object> spec;
        try (var reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
            spec = yaml.load(reader);
        }

        List<Fix> discriminatorFixes = fixDiscriminatorMissingEnum(spec);
        List<String> overrideFixes = fixOverrideTypeMismatch(spec);
        List<String> allOfWrapperFixes = fixSingleItemAllOfMapWrapper(spec);
        List<String> mapFixes = fixMissingTypeForMap(spec);
        List<String> scalarFixes = fixMissingTypeForScalar(spec);

        List<String> allFixDescriptions = new ArrayList<>();
        for (Fix fix : discriminatorFixes) {
            allFixDescriptions.add(fix.describe());
        }
        allFixDescriptions.addAll(overrideFixes);
        allFixDescriptions.addAll(allOfWrapperFixes);
        allFixDescriptions.addAll(mapFixes);
        allFixDescriptions.addAll(scalarFixes);

        Files.createDirectories(output.toAbsolutePath().getParent());
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setWidth(4096);
        Yaml dumper = new Yaml(options);
        try (Writer writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            dumper.dump(spec, writer);
        }

        System.out.println("[fix-spec] wrote fixed spec to " + output);
        if (allFixDescriptions.isEmpty()) {
            System.out.println("[fix-spec] no fixes needed (spec already clean)");
        } else {
            System.out.println("[fix-spec] applied " + allFixDescriptions.size() + " fix(es):");
            for (String description : allFixDescriptions) {
                System.out.println("  - " + description);
            }
        }

        if (logPath != null) {
            try (FileWriter log = new FileWriter(logPath, true)) {
                log.write("\n## fix-spec run " + LocalDateTime.now() + "\n");
                log.write("input=" + input + "\noutput=" + output + "\n");
                if (allFixDescriptions.isEmpty()) {
                    log.write("- no fixes needed\n");
                } else {
                    for (String description : allFixDescriptions) {
                        log.write("- " + description + "\n");
                    }
                }
            }
        }
    }
}
