#!/bin/bash

# Quick test runner for PoliciesIT - runs tests in small batches

cd /Users/prachi.pandey/Documents/GitHub/okta-sdk-java

echo "=========================================="
echo "PoliciesIT Test Run Report"
echo "Date: $(date)"
echo "=========================================="
echo ""

# Core Policy Tests (High Priority)
echo "=== BATCH 1: Core Policy Tests ==="
for test in "signOnPolicyWithGroupConditions" "activateDeactivateTest" "signOnActionsTest"; do
    echo -n "  $test: "
    ./mvnw -pl integration-tests -Dtest=PoliciesIT#$test test -q 2>&1 | grep -q "BUILD SUCCESS" && echo "✅ PASS" || echo "❌ FAIL"
done
echo ""

# List & Query Tests
echo "=== BATCH 2: List & Query Tests ==="
for test in "testListAllPolicies" "testListPoliciesByType" "testListPoliciesByStatus"; do
    echo -n "  $test: "
    ./mvnw -pl integration-tests -Dtest=PoliciesIT#$test test -q 2>&1 | grep -q "BUILD SUCCESS" && echo "✅ PASS" || echo "❌ FAIL"
done
echo ""

# CRUD Tests
echo "=== BATCH 3: CRUD Tests ==="
for test in "testCreateAndGetPolicy" "testReplacePolicy" "testDeletePolicy"; do
    echo -n "  $test: "
    ./mvnw -pl integration-tests -Dtest=PoliciesIT#$test test -q 2>&1 | grep -q "BUILD SUCCESS" && echo "✅ PASS" || echo "❌ FAIL"
done
echo ""

# Policy Rule Tests
echo "=== BATCH 4: Policy Rule Tests ==="
for test in "testCreateAndGetPolicyRule" "testReplacePolicyRule" "testDeletePolicyRule"; do
    echo -n "  $test: "
    ./mvnw -pl integration-tests -Dtest=PoliciesIT#$test test -q 2>&1 | grep -q "BUILD SUCCESS" && echo "✅ PASS" || echo "❌ FAIL"
done
echo ""

# New Advanced Tests
echo "=== BATCH 5: Advanced Tests (New) ==="
for test in "testClonePolicy" "testCompletePolicyLifecycle" "testCompletePolicyRuleLifecycle"; do
    echo -n "  $test: "
    ./mvnw -pl integration-tests -Dtest=PoliciesIT#$test test -q 2>&1 | grep -q "BUILD SUCCESS" && echo "✅ PASS" || echo "❌ FAIL"
done
echo ""

echo "=========================================="
echo "Test run completed!"
echo "=========================================="

