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
    "test_group_api"
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
        terraform init --upgrade

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
    done
}

# Phase 2: Run Tests
run_phase_test() {
    echo "=========================================="
    echo "--- Phase 2: Running tests in parallel ---"
    echo "=========================================="

    for API in "${APIS[@]}"; do
        TF_DIR="$TERRAFORM_DIR/${API}"

        if [ ! -d "$TF_DIR" ]; then
            continue
        fi

        # Get Terraform outputs BEFORE subshell so they can be exported properly
        cd "$TF_DIR" || exit 1
        echo "--- 🔍 DEBUG: Getting Terraform outputs from $TF_DIR ---"
        echo "--- 🔍 DEBUG: PWD = $(pwd) ---"
        echo "--- 🔍 DEBUG: terraform output -json test_prerequisites output: ---"
        terraform output -json test_prerequisites 2>&1 | head -100
        echo "--- 🔍 DEBUG: Exit code: $? ---"
        TF_OUTPUTS_DATA=$(terraform output -json test_prerequisites 2>&1)
        TF_EXIT_CODE=$?
        echo "--- 🔍 DEBUG: TF command exit code: $TF_EXIT_CODE ---"
        echo "--- 🔍 DEBUG: TF_OUTPUTS_DATA length: ${#TF_OUTPUTS_DATA} ---"
        echo "--- 🔍 DEBUG: TF_OUTPUTS_DATA first 200 chars: ${TF_OUTPUTS_DATA:0:200} ---"
        cd "$BASE_DIR" || exit 1

        (
            # Export in subshell so it's available to Maven subprocess
            export TF_OUTPUTS="$TF_OUTPUTS_DATA"
            echo "--- 🔍 DEBUG: Exported TF_OUTPUTS length: ${#TF_OUTPUTS} ---"
            echo "--- 🔍 DEBUG: TF_OUTPUTS contains 'activate_application': $(echo "$TF_OUTPUTS" | grep -c 'activate_application' || echo '0') ---"

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
            echo "--- 🔍 DEBUG: About to run Maven with TF_OUTPUTS length: ${#TF_OUTPUTS} ---"
            echo "--- 🔍 DEBUG: TF_OUTPUTS value in Maven context: ${TF_OUTPUTS:0:300} ---"
            
            # Run the specific test class with all Okta variables exported and available to Maven subprocess
            # Pass TF_OUTPUTS as a Maven system property because environment variables don't always propagate to subprocesses
            if mvn -pl api test \
               -Dtest="com.okta.sdk.resource.api.${API_CLASS}" \
               -DfailIfNoTests=true \
               -Dokta.org.name="$OKTA_ORG_NAME" \
               -Dokta.base.url="$OKTA_BASE_URL" \
               -Dokta.api.token="$OKTA_API_TOKEN" \
               -Dokta.client.orgurl="$OKTA_CLIENT_ORGURL" \
               -Dokta.client.token="$OKTA_CLIENT_TOKEN" \
               -Dtf.outputs="$TF_OUTPUTS" \
               -e 2>&1; then
                echo "--- ✅ $API tests passed ---"
            else
                echo "--- ❌ $API tests failed ---"
                echo "--- Check output above for details ---"
                exit 1
            fi
        ) &
    done

    wait
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
        terraform destroy -auto-approve
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
else
    echo "--- ❌ Phase '$PHASE' failed. Check logs above. ---"
fi
echo "=========================================="

exit $GLOBAL_EXIT_CODE
