package com.okta.sdk.inject.cdi

import org.apache.deltaspike.cdise.api.CdiContainer
import org.apache.deltaspike.cdise.api.CdiContainerLoader
import org.apache.deltaspike.core.api.config.ConfigResolver
import org.apache.deltaspike.core.api.projectstage.ProjectStage
import org.apache.deltaspike.core.api.provider.BeanProvider
import org.apache.deltaspike.core.spi.config.ConfigSource
import org.apache.deltaspike.core.util.ProjectStageProducer
import org.testng.annotations.AfterMethod
import org.testng.annotations.AfterSuite
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import org.testng.annotations.BeforeSuite

import javax.enterprise.context.RequestScoped
import javax.enterprise.context.SessionScoped
import javax.enterprise.context.spi.CreationalContext
import javax.enterprise.inject.spi.AnnotatedType
import javax.enterprise.inject.spi.BeanManager
import javax.enterprise.inject.spi.InjectionTarget
import javax.inject.Qualifier


abstract class CdiTestSupport {

    protected static volatile CdiContainer cdiContainer
    // nice to know, since testng executes tests in parallel.
    protected static int containerRefCount = 0

    /**
     * Starts container
     * @throws Exception in case of severe problem
     */
    @BeforeMethod
    final void beforeMethod() throws Exception {
        containerRefCount++

        if (cdiContainer == null) {
            // setting up the Apache DeltaSpike ProjectStage
            ProjectStageProducer.setProjectStage(ProjectStage.valueOf(ProjectStage.UnitTest.getName()))

            cdiContainer = CdiContainerLoader.getCdiContainer()

            cdiContainer.boot()
            cdiContainer.getContextControl().startContexts()
        }
        else {
            cleanInstances()
        }
    }

    static CdiContainer getCdiContainer() {
        return cdiContainer
    }

    /**
     * This will fill all the InjectionPoints of the current test class for you
     */
    @BeforeClass
    final void beforeClass() throws Exception {
        beforeMethod()

        // perform injection into the very own test class
        BeanManager beanManager = cdiContainer.getBeanManager()

        CreationalContext creationalContext = beanManager.createCreationalContext(null)

        AnnotatedType annotatedType = beanManager.createAnnotatedType(this.getClass())
        InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType)
        injectionTarget.inject(this, creationalContext)
    }

    /**
     * Shuts down container.
     * @throws Exception in case of severe problem
     */
    @AfterMethod
    final void afterMethod() throws Exception {
        if (cdiContainer != null) {
            cleanInstances()
            containerRefCount--
        }
    }

    /**
     * clean the NormalScoped contextual instances by stopping and restarting
     * some contexts. You could also restart the ApplicationScoped context
     * if you have some caches in your classes.
     */
    final void cleanInstances() throws Exception {
        cdiContainer.getContextControl().stopContext(RequestScoped.class)
        cdiContainer.getContextControl().startContext(RequestScoped.class)
        cdiContainer.getContextControl().stopContext(SessionScoped.class)
        cdiContainer.getContextControl().startContext(SessionScoped.class)
    }

    @AfterSuite
    synchronized void shutdownContainer() throws Exception {
        if (cdiContainer != null) {
            cdiContainer.shutdown()
            cdiContainer = null
        }
    }

    void finalize() throws Throwable {
        shutdownContainer()
        super.finalize()
    }

    protected <T> T getInstance(Class<T> type, Qualifier... qualifiers) {
        return BeanProvider.getContextualReference(type, qualifiers)
    }
}