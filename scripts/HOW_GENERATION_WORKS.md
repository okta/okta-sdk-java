# How SDK Generation Works

## Files

- **`FixSpec.java`** — runs before codegen. Loads the input spec, applies 4
  fixes (see [GENERATION_FIXES.md](GENERATION_FIXES.md)), writes the fixed
  spec to `api/target/generated-sources/spec-fixed/api.yaml`.
- **`FixGenerated.java`** — runs after codegen, before `javac`. Walks the
  generated Java sources and applies any registered post-generation fixes.
  No fixes registered today (none currently needed).

Both are plain `.java` files executed via the JDK single-file source
launcher (`java FixSpec.java ...`) — no compile step, no npm/pip.

## What happens when you build

```
mvn generate-sources   (or compile / install / ...)
  1. exec:exec (fix-spec)        -> FixSpec.java reads ${okta.sdk.spec.source},
                                     writes fixed spec to target/generated-sources/spec-fixed/
  2. openapi-generator-maven-plugin -> generates Java from the fixed spec
  3. exec:exec (fix-generated)   -> FixGenerated.java patches generated sources
  4. maven-compiler-plugin       -> javac compiles everything
```

Default `okta.sdk.spec.source` is the checked-in `src/swagger/api.yaml`.

## Regenerating from the latest spec

```bash
mvn install -DskipITs -Dokta.sdk.spec.source=/path/to/management-minimal.yaml
```

- Build succeeds → the new spec's defects (if any) were already covered by
  the existing fixes in `FixSpec.java`.
- Build fails → reproduce with the scratch-project method in
  GENERATION_FIXES.md, identify the new defect class, add a fix function,
  re-test against both the new spec and the current `api.yaml` (regression
  check), then re-run the full `mvn install -DskipITs`.

## Verified state

- Current `api.yaml`: unaffected, full reactor install passes, 235 unit
  tests green.
- `specs/monolith/dist/codegen/management-minimal.yaml` (oas3, as of this
  writing): generates and compiles cleanly through the 4 existing fixes.
