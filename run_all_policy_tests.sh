#!/bin/bash

# Script to run all PoliciesIT tests one by one and report results

TEST_METHODS=(
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

PASSED=0
FAILED=0
SKIPPED=0

echo "========================================"
echo "Running All PoliciesIT Tests"
echo "========================================"
echo ""

for test in "${TEST_METHODS[@]}"; do
    echo "Running: $test"

    # Run the test and capture output
    ./mvnw -pl integration-tests -Dtest=PoliciesIT#$test test -DtrimStackTrace=false > /tmp/test_output.log 2>&1

    # Check the result
    if grep -q "BUILD SUCCESS" /tmp/test_output.log; then
        echo "✓ PASSED: $test"
        ((PASSED++))
    elif grep -q "SKIPPED" /tmp/test_output.log; then
        echo "⊘ SKIPPED: $test"
        ((SKIPPED++))
    else
        echo "✗ FAILED: $test"
        # Show the failure reason
        grep -A 2 "ERROR" /tmp/test_output.log | grep -E "(SocketTimeout|ApiException|AssertionError)" | head -1
        ((FAILED++))
    fi
    echo ""
done

echo "========================================"
echo "Summary:"
echo "  Passed:  $PASSED"
echo "  Failed:  $FAILED"
echo "  Skipped: $SKIPPED"
echo "  Total:   ${#TEST_METHODS[@]}"
echo "========================================"

