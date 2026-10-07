# SDK Generation Fix Pipeline

Regenerating `src/swagger/api.yaml` from a fresh `okta-oas3` spec used to
fail outright (crash during generation, or invalid/non-compiling generated
Java). This was historically worked around by hand-patching the committed
`api.yaml`, which is why it has drifted from `okta-oas3`. This pipeline
(mirroring `okta-sdk-nodejs`'s `fixSpec.cjs`/`fixGenerated.cjs`) automates
those patches so regenerating from any current spec is one build, not a
manual debugging session.

See [HOW_GENERATION_WORKS.md](HOW_GENERATION_WORKS.md) for the pipeline
mechanics and how to regenerate from a new spec.

## Fixes applied by `FixSpec.java`

Each fix only touches schemas matching the exact signature that
reproducibly breaks generation/compilation (verified by running the real
generator + `javac`), and is a no-op on specs without the defect.

### 1. `discriminator-missing-enum`

- **Error**: `RuntimeException: Could not process model 'SamlAttributeStatement'` →
  `NullPointerException: "var.allowableValues" is null` (generation aborts).
- **Cause**: a schema's `discriminator.mapping` points at sibling schemas
  (`oneOf`/`anyOf`, not `allOf` inheritance). The mapped schema redeclares the
  discriminator property as `type: string` with no `enum:`, so the generator
  can't resolve a default value and NPEs instead of failing gracefully.
- **Fix**: inject `enum: [<mapping key>]` on the discriminator property when
  it's declared directly on the child (not inherited) and has no enum.

### 2. `override-type-mismatch`

- **Error**: `cannot override getFactorType() in UserFactor — return type
  UserFactorTokenHOTP.FactorTypeEnum is not compatible with UserFactorType`
  (and similarly for `_embedded` on `AccessPolicy`/`DeviceSignalCollectionPolicy`).
- **Cause**: a child schema (`allOf: [$ref: Parent, {properties}]`) redeclares
  a property the parent already declares, in a shape that isn't Java-covariant
  with the parent's generated type — either no type info at all, or a map
  property whose value schema has its own nested `properties` while the
  parent's doesn't (generator synthesizes a distinct class per child).
- **Fix**: replace the child's property declaration with the parent's,
  verbatim, so both sides generate an identical (always-compatible) type.

### 3. `missing-type-and-enum-for-scalar` / `missing-type-for-scalar`

- **Error**: invalid Java emitted — `protected String factorType = token:hardware;`
- **Cause**: a property has no `type`/`$ref`/`allOf`/`oneOf`/`anyOf`, only an
  `example:` and/or `enum:`. The generator renders the raw `example` unquoted
  as a Java literal — invalid syntax for values containing `:`.
- **Fix**: infer `type:` from the enum/example's YAML scalar kind. If driven
  only by `example:` (no `enum:`), also inject `enum: [<example>]` so the
  generator routes through its (string-escaping) enum-constant code path
  instead of the raw-literal path.
- **Guard**: skips properties that are themselves a member of an enclosing
  `allOf:` list (composition branches intentionally omit `type`, inheriting it
  from a sibling — the first version of this fix broke `ApplicationFeature`/
  `PasswordSettingObject`/`ProfileSettingObject` by not excluding these).

### 4. `missing-type-for-map` / `single-item-allof-map-wrapper`

- **Error**: invalid Java emitted — `this.embedded = ;` (empty initializer).
- **Cause**: `UserFactorVerifyResponse._embedded` is
  `{allOf: [{additionalProperties: {...}}]}` — a redundant 1-item `allOf`
  wrapping a map with no `type: object` of its own. The generator can't
  resolve a concrete Map value class, so the "add item" helper is emitted
  with nothing on the right-hand side.
- **Fix**: flatten any `{allOf: [oneEntry]}` where `oneEntry` has
  `additionalProperties` (merge its keys, drop the wrapper); then, as a
  safety net, inject `type: object` on any remaining untyped
  `additionalProperties` schema.

## How these were found

Reproduced each failure for real rather than guessing: ran
`openapi-generator-maven-plugin:7.15.0` (the project's exact version/config)
against the raw spec in an isolated scratch project to get the generation
error; once generation succeeded, ran the real `api` module
(`mvn -pl api compile -Dokta.sdk.spec.source=...`) to get the real `javac`
errors. Fixed one defect class at a time, re-ran, repeated until green —
and re-ran against the *current* (already-working) `api.yaml` after every
change to confirm zero regressions. Finished with a full
`mvn install -DskipITs` (235 unit tests, 0 failures).

## Known limitations

- `Policy.created`/`lastUpdated`/`id` have `default: Assigned` on
  `date-time` fields, and `priority`'s default is a prose string — not
  currently fatal (generator silently drops incoercible defaults), so left
  unfixed. Worth fixing upstream for correctness.
- The `AccessPolicy`/`DeviceSignalCollectionPolicy` fix discards their
  `_embedded.resourceType` typing in favor of the parent's generic map —
  if that typed shape matters, the real fix belongs upstream in `okta-oas3`.
- `FixGenerated.java` has no registered fixes yet; nothing currently needs
  post-generation patching once `FixSpec.java`'s fixes are applied.
- Validated against `specs/monolith/dist/codegen/management-minimal.yaml` as
  it exists on disk — a gitignored, locally-built artifact that can lag
  behind `okta-oas3`'s actual source. A freshly rebuilt spec may surface
  defects not in this list; re-run the reproduce → fix → regression-test
  loop above rather than assuming this list is final.
