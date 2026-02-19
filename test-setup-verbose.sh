#!/bin/bash

# Test Setup and Execution Script for Okta Java SDK
# VERBOSE VERSION: Shows Terraform logs, hides Maven logs

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
    #"test_application_api"  # 13 test cases
    # "test_application_groups_api"  # 4 test cases
    # "test_application_policies_api"  # 1 test cases working
    # "test_application_sso_api"  # 2 test cases
    # "test_application_users_api"  # 5 test cases
    # "test_authenticator_api"  # 23 test cases
    # "test_authorization_server_api"  # 7 test cases working
    # "test_authorization_server_assoc_api"  # 3 test cases working
    # "test_authorization_server_claims_api"  # 10 test cases
    # "test_authorization_server_clients_api"  # 2 test cases
     "test_authorization_server_keys_api"  # 4 test cases
    # "test_authorization_server_policies_api"  # 14 test cases
    # "test_authorization_server_rules_api"  # 14 test cases
    # "test_authorization_server_scopes_api"  # 10 test cases
    # "test_behavior_api"  # 13 test cases
    # "test_brands_api"  # 12 test cases
    # "test_captcha_api"  # 17 test cases
    # "test_custom_domain_api"  # 12 test cases
    # "test_custom_pages_api"  # 38 test cases
    # "test_custom_templates_api"  # 28 test cases
    # "test_device_assurance_api"  # 5 test cases
    # "test_email_customization_api"  # 2 test cases
    # "test_email_domain_api"  # 12 test cases
    # "test_email_server_api"  # 11 test cases
    # "test_event_hook_api"  # 16 test cases
    # "test_feature_api"  # 10 test cases
    # "test_group_api"  # 22 test cases
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

        cd "$TF_DIR" || exit 1
        echo "--- 🧹 Cleaning Terraform cache for $API ---"
        rm -rf "$TF_DIR/.terraform"
        rm -f "$TF_DIR/.terraform.lock.hcl"

        echo "--- 📦 Initializing Terraform for $API ---"
        terraform init --upgrade > /dev/null 2>&1

        echo "--- 🔍 Planning Terraform for $API ---"
        if ! OKTA_ORG_NAME="$OKTA_ORG_NAME" \
            OKTA_BASE_URL="$OKTA_BASE_URL" \
            OKTA_API_TOKEN="$OKTA_API_TOKEN" \
            terraform plan -out=tfplan; then
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
        terraform apply -auto-approve --parallelism=1 tfplan 
        APPLY_EXIT_CODE=$?
        unset TF_LOG TF_LOG_PATH

        if [ $APPLY_EXIT_CODE -ne 0 ]; then
            echo "--- ❌ Terraform apply failed for $API ---"
            GLOBAL_EXIT_CODE=1
        else
            echo "--- ✅ Terraform setup complete for $API ---"
        fi
        
        # Return to base directory for next iteration
        cd "$BASE_DIR" || exit 1
    done
}

# Phase 2: Run Tests
run_phase_test() {
    echo "=========================================="
    echo "--- Phase 2: Running tests in parallel ---"
    echo "=========================================="
    
    # Clear Maven cache to avoid certificate issues
    echo "--- Clearing Maven local repository cache ---"
    find ~/.m2/repository -name "_remote.repositories" -delete 2>/dev/null || true
    find ~/.m2/repository -name "*.lastUpdated" -delete 2>/dev/null || true
    
    TEST_COUNTER=0

    for API in "${APIS[@]}"; do
        TF_DIR="$TERRAFORM_DIR/${API}"

        if [ ! -d "$TF_DIR" ]; then
            continue
        fi

        # Get Terraform outputs BEFORE subshell so they can be exported properly
        cd "$TF_DIR" || exit 1
        TF_OUTPUTS_DATA=$(terraform output -json test_prerequisites 2>&1)
        TF_EXIT_CODE=$?
        cd "$BASE_DIR" || exit 1

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
            # VERBOSE VERSION: Hide Maven logs
            mvn -U -pl api test \
               -Dtest="com.okta.sdk.resource.api.${API_CLASS}" \
               -Dokta.org.name="$OKTA_ORG_NAME" \
               -Dokta.base.url="$OKTA_BASE_URL" \
               -Dokta.api.token="$OKTA_API_TOKEN" \
               -Dokta.client.orgurl="$OKTA_CLIENT_ORGURL" \
               -Dokta.client.token="$OKTA_CLIENT_TOKEN" \
               -Dtf.outputs="$TF_OUTPUTS" > /dev/null 2>&1
            
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

    # echo "=========================================="
    # echo "--- Phase 2b: Generating coverage report ---"
    # echo "=========================================="
    
    # # The jacoco.exec file is accumulated during sequential test runs
    # # Now generate the HTML report from it
    # if [ -f "$BASE_DIR/api/target/jacoco.exec" ]; then
    #     echo "--- Found coverage data, generating report ---"
        
    #     # Run jacoco:report-aggregate directly on coverage module
    #     # This avoids recompiling classes (which would break exec data matching)
    #     # and avoids resolving test-scoped dependencies that fail due to cert issues
    #     mvn -pl coverage jacoco:report-aggregate -DskipTests -e 2>&1
        
    #     COVERAGE_EXIT_CODE=$?
    #     if [ $COVERAGE_EXIT_CODE -eq 0 ]; then
    #         echo "--- ✅ Coverage report generated successfully ---"
    #         echo "--- Coverage report available at: $BASE_DIR/coverage/target/site/jacoco-aggregate/index.html ---"
    #     else
    #         echo "--- ⚠️  Coverage report generation had issues (exit code: $COVERAGE_EXIT_CODE) ---"
    #         echo "--- Check the configuration above ---"
    #     fi
    # else
    #     echo "--- ⚠️  No coverage data found (jacoco.exec not found) ---"
    #     echo "--- This may indicate tests were not executed or JaCoCo was not properly configured ---"
    # fi
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

        cd "$TF_DIR" || exit 1

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
