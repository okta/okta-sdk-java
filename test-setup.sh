#!/bin/bash

# Test Setup and Execution Script for Okta Java SDK

# Load credentials from okta-config.yaml if it exists
if [ -f "./okta-config.yaml" ]; then
    echo "--- Loading Okta configuration from okta-config.yaml ---"
    
    # Parse YAML using a simple approach (requires yq or equivalent)
    # For now, we'll use grep and basic parsing
    OKTA_ORG_NAME=$(grep -A 5 "terraform:" ./okta-config.yaml | grep "orgName:" | awk '{print $2}')
    OKTA_BASE_URL=$(grep -A 5 "terraform:" ./okta-config.yaml | grep "baseUrl:" | awk '{print $2}')
    OKTA_API_TOKEN=$(grep -A 5 "terraform:" ./okta-config.yaml | grep "apiToken:" | awk '{print $2}')
    OKTA_CLIENT_ORGURL=$(grep -A 3 "client:" ./okta-config.yaml | grep "orgUrl:" | awk '{print $2}')
    OKTA_CLIENT_TOKEN=$(grep -A 3 "client:" ./okta-config.yaml | grep "token:" | awk '{print $2}')
fi

# Set Okta environment variables with defaults (or use loaded values)
export OKTA_ORG_NAME="${OKTA_ORG_NAME:-op1-arastogi}"
export OKTA_BASE_URL="${OKTA_BASE_URL:-oktapreview.com}"
export OKTA_API_TOKEN="${OKTA_API_TOKEN:-}"
export OKTA_CLIENT_ORGURL="${OKTA_CLIENT_ORGURL:-}"
export OKTA_CLIENT_TOKEN="${OKTA_CLIENT_TOKEN:-}"

# Validate that we have credentials
if [ -z "$OKTA_CLIENT_ORGURL" ] || [ -z "$OKTA_CLIENT_TOKEN" ]; then
    echo "❌ ERROR: Okta credentials not found!"
    echo "Please create okta-config.yaml with the following structure:"
    echo ""
    echo "okta:"
    echo "  client:"
    echo "    orgUrl: https://your-org.okta.com/"
    echo "    token: your-api-token"
    echo ""
    exit 1
fi

echo "✅ Okta configuration loaded successfully"
echo "  Organization: $OKTA_ORG_NAME"
echo "  Base URL: $OKTA_BASE_URL"
echo "  API Token: ${OKTA_API_TOKEN:0:10}***"

APIS=(
     "test_application_api"  # 13 test cases
    # "test_application_groups_api"  # 4 test cases
    #  "test_application_policies_api"  # 1 test cases working
    # "test_application_sso_api"  # 2 test cases
    # "test_application_users_api"  # 5 test cases
    # "test_authenticator_api"  # 23 test cases
    #   "test_authorization_server_api"  # 7 test cases working
    #  "test_authorization_server_assoc_api"  # 3 test cases working
    # "test_authorization_server_claims_api"  # 10 test cases
    # "test_authorization_server_clients_api"  # 2 test cases
    # "test_authorization_server_keys_api"  # 4 test cases
    # "test_authorization_server_policies_api"  # 14 test cases
    # "test_authorization_server_rules_api"  # 14 test cases
    # "test_authorization_server_scopes_api"  # 10 test cases
    #  "test_behavior_api"  # 13 test cases
    # "test_brands_api"  # 12 test cases
    # "test_captcha_api"  # 17 test cases
    # "test_custom_domain_api"  # 12 test cases
    # "test_custom_pages_api"  # 38 test cases
    # "test_custom_templates_api"  # 28 test cases
    # "test_device_assurance_api"  # 5 test cases
    # "test_email_customization_api"  # 2 test cases
    # "test_email_domain_api"  # 12 test cases
    # # "test_email_server_api"  # 11 test cases
    #  "test_event_hook_api"  # 16 test cases
    #  "test_feature_api"  # 10 test cases
    #  "test_group_api"  # 22 test cases
    # "test_group_owner_api"  # 7 test cases
    # "test_group_rule_api"  # 23 test cases
    # "test_identity_provider_api"  # 13 test cases
    # "test_identity_provider_keys_api"  # 8 test cases
    # "test_identity_provider_signing_keys_api"  # 18 test cases
    # "test_identity_provider_users_api"  # 11 test cases
    # "test_inline_hook_api"  # 15 test cases
    # "test_linked_object_api"  # 8 test cases
    # "test_log_stream_api"  # 14 test cases
    # "test_network_zone_api"  # 7 test cases
    # "test_org_setting_api"  # 8 test cases
    # "test_org_setting_communication_api"  # 6 test cases
    # "test_org_setting_contact_api"  # 4 test cases
    # "test_org_setting_customization_api"  # 2 test cases
    # "test_org_setting_general_api"  # 6 test cases
    # "test_org_setting_metadata_api"  # 2 test cases
    # "test_org_setting_support_api"  # 8 test cases
    # "test_policy_api"  # 40 test cases
    # "test_profile_mapping_api"  # 6 test cases
    # "test_push_provider_api"  # 9 test cases
    # "test_rate_limit_settings_api"  # 12 test cases
    # "test_realm_api"  # 9 test cases
    # "test_resource_set_api"  # 1 test cases
    # "test_role_assignment_a_user_api"  # 5 test cases
    # "test_role_assignment_b_group_api"  # 4 test cases
    # "test_role_b_target_admin_api"  # 18 test cases
    # "test_role_b_target_b_group_api"  # 16 test cases
    # "test_role_c_resource_set_api"  # 8 test cases
    # "test_role_c_resource_set_resource_api"  # 4 test cases
    # "test_role_d_resource_set_binding_api"  # 8 test cases
    # "test_role_d_resource_set_binding_member_api"  # 8 test cases
    # "test_role_e_custom_api"  # 10 test cases
    # "test_role_e_custom_permission_api"  # 10 test cases
    # "test_schema_api"  # 23 test cases
    # "test_subscription_api"  # 16 test cases
    # "test_system_log_api"  # 1 test cases
    # "test_template_api"  # 11 test cases
    # "test_themes_api"  # 18 test cases
    # "test_threat_insight_api"  # 4 test cases
    # "test_trusted_origin_api"  # 13 test cases
    # "test_user_api"  # 10 test cases
    # "test_user_cred_api"  # 7 test cases
    # "test_user_grant_api"  # 6 test cases
    # "test_user_lifecycle_api"  # 7 test cases
    # "test_user_linked_object_api"  # 2 test cases
    # "test_user_o_auth_api"  # 4 test cases
    # "test_user_resources_api"  # 3 test cases
    # "test_user_sessions_api"  # 1 test cases
    # "test_user_type_api"  # 12 test cases
)
# Total: 76 APIs with terraform prerequisite data

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BASE_DIR="$SCRIPT_DIR"
TERRAFORM_DIR="$BASE_DIR/terraform"
GLOBAL_EXIT_CODE=0
PHASE=${1:-"all"}  # all, setup, test, destroy

# Resources to remove from Terraform state before destroy
RESOURCES_TO_REMOVE=(
    'module.delete_oauth_app.okta_app_oauth.this[0]'
    'module.delete_authorization_server'
    'module.delete_authorization_server_policy_rule.okta_auth_server_policy_rule.this[0]'
    'module.delete_behavior_detection_rule.okta_behavior.this[0]'
    'module.delete_brand.okta_brand.this[0]'
    'module.delete_device_assurance_policy.okta_policy_device_assurance_android.this[0]'
    'module.splunk_cloud_ex.okta_log_stream.this[0]'
    'module.delete_push_provider.okta_push_provider.this[0]'
    'module.delete_realm.okta_realm.this[0]'
    'module.resource_set_for_delete_resource_set.okta_resource_set.this[0]'
    'module.delete_trusted_origin.okta_trusted_origin.this[0]'
    'module.delete_associated_server.okta_trusted_server.this[0]'
)

# Phase 1: Setup Terraform
run_phase_setup() {
    echo "=========================================="
    echo "--- Phase 1: Initializing and creating resources for all APIs ---"
    echo "=========================================="

    for API in "${APIS[@]}"; do
        echo "--- Processing $API ---"

        TF_DIR="$TERRAFORM_DIR/${API}"

        if [ ! -d "$TF_DIR" ]; then
            echo "--- ❌ Terraform directory not found: $TF_DIR ---"
            GLOBAL_EXIT_CODE=1
            continue
        fi

        if ! cd "$TF_DIR"; then
            echo "--- ❌ Failed to cd to $TF_DIR ---"
            GLOBAL_EXIT_CODE=1
            continue
        fi
        echo "--- 🧹 Cleaning Terraform cache for $API ---"
        rm -rf "$TF_DIR/.terraform"
        rm -f "$TF_DIR/.terraform.lock.hcl"

        echo "--- 📦 Initializing Terraform for $API ---"
        terraform init --upgrade > /dev/null 2>&1

        echo "--- 🔍 Planning Terraform for $API ---"
        if ! OKTA_ORG_NAME="$OKTA_ORG_NAME" \
            OKTA_BASE_URL="$OKTA_BASE_URL" \
            OKTA_API_TOKEN="$OKTA_API_TOKEN" \
            terraform plan -out=tfplan > /dev/null 2>&1; then
            echo "--- ❌ Terraform plan failed for $API ---"
            GLOBAL_EXIT_CODE=1
            continue
        fi

        echo "--- 📦 Creating $API resources ---"
        export TF_LOG=DEBUG
        export TF_LOG_PATH="$TF_DIR/terraform-debug.log"
        OKTA_ORG_NAME="$OKTA_ORG_NAME" \
        OKTA_BASE_URL="$OKTA_BASE_URL" \
        OKTA_API_TOKEN="$OKTA_API_TOKEN" \
        terraform apply -auto-approve --parallelism=1 tfplan > /dev/null 2>&1
        APPLY_EXIT_CODE=$?
        unset TF_LOG TF_LOG_PATH

        if [ $APPLY_EXIT_CODE -ne 0 ]; then
            echo "--- ❌ Terraform apply failed for $API ---"
            GLOBAL_EXIT_CODE=1
        else
            echo "--- ✅ Terraform setup complete for $API ---"
        fi

        # Return to base directory for next iteration
        if ! cd "$BASE_DIR"; then
            echo "--- ❌ Failed to cd back to $BASE_DIR ---"
            GLOBAL_EXIT_CODE=1
            continue
        fi
    done
}

# Phase 2: Run Tests
run_phase_test() {
    echo "=========================================="
    echo "--- Phase 2: Running tests in parallel ---"
    echo "=========================================="

    # Ensure we're in the correct directory where mvnw is located
    cd "$BASE_DIR" || {
        echo "--- ❌ Failed to cd to $BASE_DIR ---"
        GLOBAL_EXIT_CODE=1
        return
    }

    # Clear Maven cache to avoid certificate issues
    echo "--- Clearing Maven local repository cache ---"
    find ~/.m2/repository -name "_remote.repositories" -delete 2>/dev/null || true
    find ~/.m2/repository -name "*.lastUpdated" -delete 2>/dev/null || true

    # IMPORTANT: Compile once before parallel tests to avoid race conditions
    # Multiple parallel Maven processes triggering generate-sources will corrupt generated files
    echo "--- 🏗️  Pre-compiling api module (including test classes) to avoid parallel code generation issues ---"

    # Capture compilation output to temp file so we can show errors if it fails
    COMPILE_LOG=$(mktemp)
    ./mvnw clean test-compile -pl api > "$COMPILE_LOG" 2>&1
    COMPILE_EXIT_CODE=$?

    if [ $COMPILE_EXIT_CODE -ne 0 ]; then
        echo "--- ❌ Pre-compilation failed (exit code: $COMPILE_EXIT_CODE), skipping test execution ---"
        echo "--- Compilation error details: ---"
        tail -50 "$COMPILE_LOG"
        echo "--- End of compilation errors ---"
        GLOBAL_EXIT_CODE=1
        SKIP_TESTS=true
    else
        echo "--- ✅ Pre-compilation successful (source and test classes compiled) ---"
        SKIP_TESTS=false
    fi

    rm -f "$COMPILE_LOG"

    TEST_COUNTER=0

    if [ "$SKIP_TESTS" = "false" ]; then
        for API in "${APIS[@]}"; do
        TF_DIR="$TERRAFORM_DIR/${API}"

        if [ ! -d "$TF_DIR" ]; then
            continue
        fi

        # Get Terraform outputs BEFORE subshell so they can be exported properly
        if ! cd "$TF_DIR"; then
            echo "--- ❌ Failed to cd to $TF_DIR ---"
            TF_EXIT_CODE=1
            TF_OUTPUTS_DATA="{}"
        else
            TF_OUTPUTS_DATA=$(terraform output -json test_prerequisites 2>&1)
            TF_EXIT_CODE=$?
        fi

        if ! cd "$BASE_DIR"; then
            echo "--- ❌ Failed to cd back to $BASE_DIR ---"
            continue
        fi

        (
            # Export in subshell so it's available to Maven subprocess
            export TF_OUTPUTS="$TF_OUTPUTS_DATA"

            if [ -z "$TF_OUTPUTS" ] || [ "$TF_OUTPUTS" = "{}" ]; then
                echo "--- ⚠️  WARNING: No Terraform outputs found for $API ---"
                echo "--- Tests will fail without Terraform prerequisite data ---"
            else
                echo "--- ✅ Terraform outputs loaded for $API ---"
            fi

            echo "--- 🧪 Testing $API ---"
            # Extract API name for test class (e.g., test_application_api -> ApplicationApiTest)
            # Remove prefix and suffix, convert underscores to camelCase
            API_NAME="${API#test_}"  # Remove "test_" prefix
            API_NAME="${API_NAME%_api}"  # Remove "_api" suffix
            # Convert snake_case to CamelCase
            API_CLASS=$(echo "$API_NAME" | awk -F_ '{for(i=1;i<=NF;i++){printf "%s", toupper(substr($i,1,1)) substr($i,2)}}')
            API_CLASS="${API_CLASS}ApiTest"
            
            echo "--- Running test class: com.okta.sdk.resource.api.${API_CLASS} ---"
            
            # Run the specific test class with all Okta variables exported and available to Maven subprocess
            # Tests run in parallel for faster execution
            # Only run tests in the api module where the generated tests are located
            # NOTE: Using test goal but skipping compilation since we pre-compiled everything
            ./mvnw -pl api test \
               -Dmaven.compiler.skip=true \
               -Dtest="com.okta.sdk.resource.api.${API_CLASS}" \
               -Dokta.org.name="$OKTA_ORG_NAME" \
               -Dokta.base.url="$OKTA_BASE_URL" \
               -Dokta.api.token="$OKTA_API_TOKEN" \
               -Dokta.client.orgurl="$OKTA_CLIENT_ORGURL" \
               -Dokta.client.token="$OKTA_CLIENT_TOKEN" \
               -Dtf.outputs="$TF_OUTPUTS" \
               -e 2>&1
            
            TEST_EXIT_CODE=$?
            
            if [ $TEST_EXIT_CODE -eq 0 ]; then
                echo "--- ✅ $API tests passed ---"
            else
                echo "--- ⚠️  $API tests failed (exit code: $TEST_EXIT_CODE) ---"
                echo "--- Check output above for details ---"
                echo "--- Continuing to coverage report generation ---"
                GLOBAL_EXIT_CODE=1
            fi
        ) &
        
            TEST_COUNTER=$((TEST_COUNTER + 1))
        done

        wait

        echo "--- ✅ All tests completed (ran $TEST_COUNTER test suites) ---"
    else
        echo "--- ⚠️  Skipping parallel tests due to compilation failure ---"
    fi

    echo "=========================================="
    echo "--- Phase 2b: Running generated API coverage tests ---"
    echo "=========================================="
    
    # Run generated API coverage tests to touch uncovered lines in constructors, getters/setters, null validation
    # NOTE: Using test goal but skipping compilation since we pre-compiled everything
    ./mvnw -pl api test -Dmaven.compiler.skip=true -Dtest="*ApiCoverageTest" > /dev/null 2>&1
    COVERAGE_TEST_EXIT_CODE=$?
    if [ $COVERAGE_TEST_EXIT_CODE -eq 0 ]; then
        echo "--- ✅ Generated API coverage tests passed ---"
    else
        echo "--- ⚠️  Some generated API coverage tests had issues (non-blocking) ---"
    fi
    
    echo "=========================================="
    echo "--- Phase 2c: Generating coverage report ---"
    echo "=========================================="
    
    if [ -f "$BASE_DIR/api/target/jacoco.exec" ]; then
        echo "--- Found coverage data, generating report ---"
    
        # Run verify phase on the full reactor to trigger report-aggregate with proper source linking
        # The report-aggregate goal is bound to verify phase in coverage/pom.xml
        # -Dmaven.test.skip=true skips tests (already ran in Phase 1)
        # -Dmaven.javadoc.skip=true skips JavaDoc generation
        # -Dmaven.failsafe.skip=true skips integration tests
        echo "--- Generating aggregated coverage report ---"
        ./mvnw verify -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -Dmaven.failsafe.skip=true > /dev/null 2>&1
        COVERAGE_EXIT_CODE=$?
        if [ $COVERAGE_EXIT_CODE -eq 0 ]; then
            echo "--- ✅ Coverage report generated successfully ---"
            echo "--- Coverage report available at: $BASE_DIR/coverage/target/site/jacoco-aggregate/index.html ---"
        else
            echo "--- ⚠️  Coverage report generation had issues (exit code: $COVERAGE_EXIT_CODE) ---"
        fi
    else
        echo "--- ⚠️  No coverage data found (jacoco.exec not found) ---"
        echo "--- This may indicate tests were not executed or JaCoCo was not properly configured ---"
    fi
}

# Phase 3: Cleanup Terraform
run_phase_destroy() {
    echo "=========================================="
    echo "--- Phase 3: Cleaning up orphaned resources and destroying ---"
    echo "=========================================="

    for API in "${APIS[@]}"; do
        TF_DIR="$TERRAFORM_DIR/${API}"

        if [ ! -d "$TF_DIR" ]; then
            continue
        fi

        if ! cd "$TF_DIR"; then
            echo "--- ❌ Failed to cd to $TF_DIR ---"
            GLOBAL_EXIT_CODE=1
            continue
        fi

        echo "=========================================="
        echo "--- Cleaning up orphaned resources from state for $API ---"
        echo "=========================================="

        # Loop through resources and remove them from state (silently skip if not found)
        for RESOURCE in "${RESOURCES_TO_REMOVE[@]}"; do
            terraform state rm "$RESOURCE" 2>/dev/null || true
        done

        echo "--- 🗑️  Destroying $API resources ---"
        terraform destroy -auto-approve > /dev/null 2>&1
        DESTROY_EXIT_CODE=$?

        if [ $DESTROY_EXIT_CODE -ne 0 ]; then
            echo "--- ❌ Terraform destroy failed for $API ---"
            GLOBAL_EXIT_CODE=1
        else
            echo "--- ✅ $API destroy complete ---"
        fi
    done
}

# Main execution logic
case "$PHASE" in
    all)
        run_phase_setup
        run_phase_test
        run_phase_destroy
        ;;
    setup)
        run_phase_setup
        ;;
    test)
        run_phase_test
        ;;
    destroy)
        run_phase_destroy
        ;;
    *)
        echo "Invalid phase: $PHASE"
        echo "Usage: $0 [all|setup|test|destroy]"
        exit 1
        ;;
esac

echo "=========================================="
if [ $GLOBAL_EXIT_CODE -eq 0 ]; then
    echo "--- ✅ Phase '$PHASE' completed successfully! ---"
    echo "--- Coverage report available at: $BASE_DIR/coverage/target/site/jacoco-aggregate/index.html ---"
else
    echo "--- ⚠️  Phase '$PHASE' completed with some test failures. ---"
    echo "--- Coverage report has been generated regardless of test results. ---"
    echo "--- Coverage report available at: $BASE_DIR/coverage/target/site/jacoco-aggregate/index.html ---"
    echo "--- Review test output above for failure details. ---"
fi
echo "=========================================="

exit $GLOBAL_EXIT_CODE
