#!/bin/bash

echo "=========================================="
echo "Testing Bug Fixes"
echo "=========================================="
echo ""

cd /Users/prachi.pandey/Documents/GitHub/okta-sdk-java

# Test 1: Check LogAuthenticationContext has rootSessionId
echo "✓ Test #1653 - LogAuthenticationContext.rootSessionId"
if grep -q "getRootSessionId()" api/target/generated-sources/openapi/src/gen/java/main/com/okta/sdk/resource/model/LogAuthenticationContext.java; then
    echo "  ✅ PASS: getRootSessionId() method exists"
else
    echo "  ❌ FAIL: getRootSessionId() method NOT found"
fi

# Note: LogAuthenticationContext is read-only, so no setter is generated (this is correct behavior)
if grep -q "private String rootSessionId" api/target/generated-sources/openapi/src/gen/java/main/com/okta/sdk/resource/model/LogAuthenticationContext.java; then
    echo "  ✅ PASS: rootSessionId field exists (read-only model)"
else
    echo "  ❌ FAIL: rootSessionId field NOT found"
fi
echo ""

# Test 2: Check PasswordPolicyRule equals includes super.equals()
echo "✓ Test #1650 - PasswordPolicyRule equals method"
if grep -A 10 "public boolean equals" api/target/generated-sources/openapi/src/gen/java/main/com/okta/sdk/resource/model/PasswordPolicyRule.java | grep -q "super.equals(o)"; then
    echo "  ✅ PASS: equals() calls super.equals()"
else
    echo "  ❌ FAIL: equals() does NOT call super.equals()"
fi
echo ""

# Test 3: Check ApiClient has cacheManager field
echo "✓ Test #1600 - ApiClient resource-specific caching"
if grep -q "private CacheManager cacheManager;" api/target/generated-sources/openapi/src/gen/java/main/com/okta/sdk/resource/client/ApiClient.java; then
    echo "  ✅ PASS: cacheManager field exists"
else
    echo "  ❌ FAIL: cacheManager field NOT found"
fi

if grep -q "private Cache<String, Object> defaultCache;" api/target/generated-sources/openapi/src/gen/java/main/com/okta/sdk/resource/client/ApiClient.java; then
    echo "  ✅ PASS: defaultCache field exists"
else
    echo "  ❌ FAIL: defaultCache field NOT found"
fi

if grep -q "cache = cacheManager.getCache(expectedType.getName())" api/target/generated-sources/openapi/src/gen/java/main/com/okta/sdk/resource/client/ApiClient.java; then
    echo "  ✅ PASS: Resource-specific cache lookup implemented"
else
    echo "  ❌ FAIL: Resource-specific cache lookup NOT found"
fi
echo ""

# Test 4: Check LinksResend.resend is array
echo "✓ Test #1615/#1667 - LinksResend.resend array type"
if grep -B5 -A5 "List<HrefObject>" api/target/generated-sources/openapi/src/gen/java/main/com/okta/sdk/resource/model/LinksResend.java | grep -q "resend"; then
    echo "  ✅ PASS: resend is List<HrefObject>"
else
    echo "  ❌ FAIL: resend is NOT List<HrefObject>"
fi
echo ""

# Test 5: Check TempPassword return type
echo "✓ Test #1622 - expirePasswordWithTempPassword return type"
if grep -A 3 "expirePasswordWithTempPassword" src/swagger/api.yaml | grep -q "TempPassword"; then
    echo "  ✅ PASS: Returns TempPassword"
else
    echo "  ❌ FAIL: Does NOT return TempPassword"
fi
echo ""

# Test 6: Check GroupProfile additionalProperties (via OktaUserGroupProfile)
echo "✓ Test #1642 - GroupProfile custom attributes"
if grep -A25 "OktaUserGroupProfile:" src/swagger/api.yaml | grep -q "additionalProperties: true"; then
    echo "  ✅ PASS: OktaUserGroupProfile has additionalProperties: true"
    echo "  ℹ️  Note: GroupProfile uses oneOf → OktaUserGroupProfile"
else
    echo "  ❌ FAIL: OktaUserGroupProfile missing additionalProperties"
fi
echo ""

# Test 7: Check OIDCApplicationBuilder default name
echo "✓ Test #1619 - OIDCApplicationBuilder default name"
if grep -q "OpenIdConnectApplication.NameEnum.OIDC_CLIENT" impl/src/main/java/com/okta/sdk/impl/resource/DefaultOIDCApplicationBuilder.java 2>/dev/null; then
    echo "  ✅ PASS: Default name set to OIDC_CLIENT"
else
    echo "  ❌ FAIL: Default name NOT set"
fi
echo ""

# Test 8: Check cache ClassCastException fix
echo "✓ Test #1618 - Cache ClassCastException fix"
if grep -B5 -A5 "expectedType.isInstance" api/src/main/resources/custom_templates/ApiClient.mustache | grep -q "Fix for GitHub issue #1618"; then
    echo "  ✅ PASS: Type validation added"
else
    echo "  ❌ FAIL: Type validation NOT found"
fi
echo ""

# Test 9: Check JUnit dependency scope
echo "✓ Test #1666 - JUnit dependency scope"
if grep -B2 -A2 "junit</artifactId>" api/pom.xml | grep -q "<scope>test</scope>"; then
    echo "  ✅ PASS: JUnit scope set to 'test'"
else
    echo "  ❌ FAIL: JUnit scope NOT set to 'test'"
fi
echo ""

# Test 10: Check httpclient5 version
echo "✓ Test #1657 - HTTP connection pool leak (httpclient5 5.5.1)"
if grep -A2 "org.apache.httpcomponents.client5.version" pom.xml | grep -q "5.5.1"; then
    echo "  ✅ PASS: httpclient5 version is 5.5.1"
else
    echo "  ❌ FAIL: httpclient5 version NOT 5.5.1"
fi
echo ""

echo "=========================================="
echo "All Bug Fix Tests Complete!"
echo "=========================================="
