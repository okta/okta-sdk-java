#!/bin/bash
 
# Use the 1.7 JDK
JAVA_HOME=${JAVA7_HOME}
export PATH=${PATH}:${JAVA_HOME}/bin

# Define the location of artifactory
export ARTIFACTORY_URL=https://artifacts.aue1d.saasure.com/artifactory
 
# This is the Aperture test_suite_id for the TestSuite we want to start
BUILD_AND_TEST_TEST_SUITE_ID=771f3f55-7db9-11e5-80ff-22000b5181e9
 
# Mark the start of our TestSuite
start_test_suite ${BUILD_AND_TEST_TEST_SUITE_ID}
 
# Run the build and tests and deploy, don't fail on error
function build_and_test() {
  mvn -Pci-run clean deploy -Dmaven.test.failure.ignore=true -s ${MAVEN_SETTINGS}
}
 
# Catch compilation errors and log to s3 (fail/finish_test_suite will do this)
if ! build_and_test; then
  fail_test_suite
  exit 1
fi
finish_test_suite build