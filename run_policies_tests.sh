#!/bin/bash

# Script to run PoliciesIT tests one by one and track results

cd /Users/prachi.pandey/Documents/GitHub/okta-sdk-java

# Array of test methods to run
declare -a TESTS=(
    "signOnPolicyWithGroupConditions"
    "createProfileEnrollmentPolicy"
    "createAccessPolicyRule"
    "signOnActionsTest"
    "activateDeactivateTest"
    "expandTest"
    "listPoliciesWithParams"
    "listPolicyRulesTest"
    "testListAllPolicies"
    "testCreateAndGetPolicy"
    "testReplacePolicy"
    "testDeletePolicy"
    "testActivateAndDeactivatePolicy"
    "testListPolicyRules"
    "testCreateAndGetPolicyRule"
    "testReplacePolicyRule"
    "testDeletePolicyRule"
    "testActivateAndDeactivatePolicyRule"
    "testListPoliciesByType"
    "testListPoliciesByStatus"
    "testPolicyPriority"
    "testPolicyRulePriority"
    "testClonePolicy"
    "testListPolicyMappings"
    "testMapResourceToPolicyAndGetMapping"
    "testDeletePolicyResourceMapping"
    "testCreatePolicySimulation"
    "testCompletePolicyLifecycle"
    "testCompletePolicyRuleLifecycle"
)

PASSED=()
FAILED=()
TOTAL=${#TESTS[@]}
CURRENT=0

echo "========================================"
echo "Running PoliciesIT Tests (Total: $TOTAL)"
echo "========================================"
echo ""

for test in "${TESTS[@]}"; do
    ((CURRENT++))
    echo "[$CURRENT/$TOTAL] Running: $test"

    # Run test and capture result
    ./mvnw -pl integration-tests -Dtest=PoliciesIT#$test test > /tmp/test_$test.log 2>&1

    if grep -q "BUILD SUCCESS" /tmp/test_$test.log; then
        echo "  ✅ PASSED"
        PASSED+=("$test")
    else
        echo "  ❌ FAILED"
        FAILED+=("$test")
        # Show brief error
        grep -E "ApiException|SocketTimeout|AssertionError" /tmp/test_$test.log | head -1 | sed 's/^/     /'
    fi
    echo ""
done

echo "========================================"
echo "SUMMARY"
echo "========================================"
echo "Total Tests: $TOTAL"
echo "Passed: ${#PASSED[@]}"
echo "Failed: ${#FAILED[@]}"
echo ""

if [ ${#FAILED[@]} -gt 0 ]; then
    echo "Failed Tests:"
    for test in "${FAILED[@]}"; do
        echo "  - $test"
    done
fi

echo "========================================"

