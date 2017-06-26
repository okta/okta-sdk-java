package com.okta.sdk.tests.it.spec

import cucumber.api.CucumberOptions
import cucumber.api.testng.AbstractTestNGCucumberTests

@CucumberOptions(
        strict = true,
        glue = "com.okta.sdk.tests.it.spec.steps",
        features = "classpath:spec/user.feature")
class SpecIT extends AbstractTestNGCucumberTests {
}
