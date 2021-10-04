package com.okta.sdk.tests;

import com.okta.sdk.tests.it.util.ITSupport;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.SkipException;

import java.lang.reflect.Method;

public class ConditionalSkipTestAnalyzer implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {
        IInvokedMethodListener.super.beforeInvocation(invokedMethod, testResult);
        Method method = testResult.getMethod().getConstructorOrMethod().getMethod();
        if (method == null) {
            return;
        }
        if (method.isAnnotationPresent(NonOIEEnvironmentOnly.class) && ITSupport.isOIEEnvironment) {
            throw new SkipException("The " + method.getName() + " test shouldn't be run for OIE Environment");
        }
    }
}
