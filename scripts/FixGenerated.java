// Post-process generated Java sources after code generation.
//
// Mirrors the role of okta-sdk-nodejs's scripts/fixGenerated.cjs: repairs
// generated-code issues that the spec-level fixes in FixSpec.java can't
// address because they only manifest in openapi-generator's Java output.
// Empty by default - add a fix method here and register it in FIXES
// whenever a new post-generation defect is found, and record it in
// GENERATION_FIXES.md.
//
// Run with JDK 11+'s single-file source launcher, e.g.:
//   java FixGenerated.java --generated-dir <path> [--log <path>]

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FixGenerated {

    // Register (name, fix) pairs here as new post-generation defects are found.
    static final List<Function<Path, List<String>>> FIXES = List.of();

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
        String generatedDirArg = args.get("generated-dir");
        if (generatedDirArg == null) {
            throw new IllegalArgumentException("missing required --generated-dir");
        }
        Path generatedDir = Path.of(generatedDirArg);
        String logPath = args.get("log");

        List<String> log = new ArrayList<>();
        for (Function<Path, List<String>> fix : FIXES) {
            log.addAll(fix.apply(generatedDir));
        }

        if (log.isEmpty()) {
            System.out.println("[fix-generated] no fixes registered/needed");
        } else {
            System.out.println("[fix-generated] applied " + log.size() + " fix(es):");
            for (String line : log) {
                System.out.println("  - " + line);
            }
        }

        if (logPath != null) {
            try (FileWriter writer = new FileWriter(logPath, true)) {
                writer.write("\n## fix-generated run " + LocalDateTime.now() + "\n");
                writer.write("generated_dir=" + generatedDir + "\n");
                if (log.isEmpty()) {
                    writer.write("- no fixes registered/needed\n");
                } else {
                    for (String line : log) {
                        writer.write("- " + line + "\n");
                    }
                }
            }
        }
    }
}
