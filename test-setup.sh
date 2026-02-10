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
      "test_application_api"
  "test_application_groups_api"
  "test_application_policies_api"
  "test_application_sso_api"
  "test_application_users_api"
  "test_authorization_server_api"
   "test_behavior_api"
   "test_captcha_api"
   "test_custom_domain_api"
   "test_customization_api"
   "test_device_assurance_api"
   "test_email_domain_api"
   "test_email_server_api"
  "test_event_hook_api"
   "test_feature_api"
   "test_group_api"
   "test_identity_provider_api"
   "test_inline_hook_api"
   "test_linked_object_api"
   "test_log_stream_api"
  "test_network_zone_api"
#   "test_org_setting_api"
   "test_policy_api"
  "test_profile_mapping_api"
   "test_push_provider_api"
   "test_rate_limit_settings_api"
   "test_realm_api"
   "test_resource_set_api"
   "test_role_api"
   "test_role_assignment_api"
   "test_role_target_api"
   "test_schema_api"
   "test_subscription_api"
   "test_system_log_api"
    "test_template_api"
   "test_threat_insight_api"
   "test_trusted_origin_api"
#  "test_user_api"
   "test_user_type_api"
#   "test_authenticator_api"
)

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
            # Pass TF_OUTPUTS as a Maven system property because environment variables don't always propagate to subprocesses
            # Note: We don't fail on test failures anymore so coverage reports are still generated
            mvn -U -pl api test \
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
                echo "--- Continuing to next test and coverage report generation ---"
                GLOBAL_EXIT_CODE=1
            fi
        ) &
    done

    wait
    
    echo "=========================================="
    echo "--- Phase 2b: Generating combined coverage report ---"
    echo "=========================================="
    
    # Run coverage report generation without failing on test failures
    # This aggregates all the coverage data from individual test runs
    mvn -U -pl coverage \
        jacoco:report-aggregate \
        -DskipTests \
        2>&1
    
    COVERAGE_EXIT_CODE=$?
    if [ $COVERAGE_EXIT_CODE -eq 0 ]; then
        echo "--- ✅ Coverage report generated successfully ---"
        echo "--- Coverage report available at: $BASE_DIR/coverage/target/site/jacoco/index.html ---"
    else
        echo "--- ⚠️  Coverage report generation had issues (exit code: $COVERAGE_EXIT_CODE) ---"
        echo "--- Check the coverage module configuration ---"
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
    echo "--- Coverage report available at: ./coverage/target/site/jacoco/index.html ---"
else
    echo "--- ⚠️  Phase '$PHASE' completed with some test failures. ---"
    echo "--- Coverage report has been generated regardless of test results. ---"
    echo "--- Coverage report available at: ./coverage/target/site/jacoco/index.html ---"
    echo "--- Review test output above for failure details. ---"
fi
echo "=========================================="

exit $GLOBAL_EXIT_CODE
