package com.okta.sdk.impl.resource;

import com.okta.sdk.resource.api.PolicyApi;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.LifecycleStatus;
import com.okta.sdk.resource.model.Policy;
import com.okta.sdk.resource.model.PolicyType;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class DefaultPolicyBuilderTest {

    private DefaultPolicyBuilder<?> builder;
    private PolicyApi policyApiMock;

    @BeforeMethod
    public void setUp() {
        builder = new DefaultPolicyBuilder<>();
        policyApiMock = mock(PolicyApi.class);
    }

    @Test
    public void testDefaultConstructor() {
        assertEquals(LifecycleStatus.ACTIVE, builder.status);
        assertTrue(builder.isActive);
    }

    @Test
    public void testSetName() {
        DefaultPolicyBuilder<?> result = (DefaultPolicyBuilder<?>) builder.setName("Test Policy");
        assertEquals("Test Policy", builder.name);
        assertSame(builder, result);
    }

    @Test
    public void testSetDescription() {
        DefaultPolicyBuilder<?> result = (DefaultPolicyBuilder<?>) builder.setDescription("Test Description");
        assertEquals("Test Description", builder.description);
        assertSame(builder, result);
    }

    @Test
    public void testSetType() {
        DefaultPolicyBuilder<?> result = (DefaultPolicyBuilder<?>) builder.setType(PolicyType.PASSWORD);
        assertEquals(PolicyType.PASSWORD, builder.policyType);
        assertSame(builder, result);
    }

    @Test
    public void testSetPriority() {
        DefaultPolicyBuilder<?> result = (DefaultPolicyBuilder<?>) builder.setPriority(1);
        assertEquals(Integer.valueOf(1), builder.priority);
        assertSame(builder, result);
    }

    @Test
    public void testSetStatus() {
        // Test with ACTIVE status
        DefaultPolicyBuilder<?> result = (DefaultPolicyBuilder<?>) builder.setStatus(LifecycleStatus.ACTIVE);
        assertEquals(LifecycleStatus.ACTIVE, builder.status);
        assertTrue(builder.isActive);
        assertSame(builder, result);

        // Test with INACTIVE status
        result = (DefaultPolicyBuilder<?>) builder.setStatus(LifecycleStatus.INACTIVE);
        assertEquals(LifecycleStatus.INACTIVE, builder.status);
        assertFalse(builder.isActive);
        assertSame(builder, result);
    }

    @Test
    public void testBuildAndCreate() throws ApiException {
        // Setup
        Policy mockPolicy = new Policy();
        when(policyApiMock.createPolicy(any(Policy.class), eq(true))).thenReturn(mockPolicy);

        // Configure the builder with all properties
        builder.setName("Test Policy")
            .setDescription("Test Description")
            .setType(PolicyType.PASSWORD)
            .setPriority(1)
            .setStatus(LifecycleStatus.ACTIVE);

        // Call the method under test
        Policy result = builder.buildAndCreate(policyApiMock);

        // Verify result
        assertSame(mockPolicy, result);

        // Verify the Policy object passed to the API
        ArgumentCaptor<Policy> policyCaptor = ArgumentCaptor.forClass(Policy.class);
        verify(policyApiMock).createPolicy(policyCaptor.capture(), eq(true));

        Policy capturedPolicy = policyCaptor.getValue();
        assertEquals("Test Policy", capturedPolicy.getName());
        assertEquals("Test Description", capturedPolicy.getDescription());
        assertEquals(PolicyType.PASSWORD, capturedPolicy.getType());
        assertEquals(Integer.valueOf(1), capturedPolicy.getPriority());
        assertEquals(LifecycleStatus.ACTIVE, capturedPolicy.getStatus());
    }

    @Test
    public void testBuildWithMinimalProperties() throws ApiException {
        // Setup
        Policy mockPolicy = new Policy();
        when(policyApiMock.createPolicy(any(Policy.class), eq(true))).thenReturn(mockPolicy);

        // Configure builder with only required properties
        builder.setName("Minimal Policy")
            .setType(PolicyType.PASSWORD);

        // Call the method under test
        Policy result = builder.buildAndCreate(policyApiMock);

        // Verify result
        assertSame(mockPolicy, result);

        // Verify the Policy object passed to the API
        ArgumentCaptor<Policy> policyCaptor = ArgumentCaptor.forClass(Policy.class);
        verify(policyApiMock).createPolicy(policyCaptor.capture(), eq(true));

        Policy capturedPolicy = policyCaptor.getValue();
        assertEquals("Minimal Policy", capturedPolicy.getName());
        assertEquals(PolicyType.PASSWORD, capturedPolicy.getType());
        assertNull(capturedPolicy.getDescription());
        assertNull(capturedPolicy.getPriority());
        assertEquals(LifecycleStatus.ACTIVE, capturedPolicy.getStatus());
    }

    @Test
    public void testBuildWithNullStrings() throws ApiException {
        // Setup
        Policy mockPolicy = new Policy();
        when(policyApiMock.createPolicy(any(Policy.class), eq(true))).thenReturn(mockPolicy);

        // Configure builder with null strings
        builder.setName(null)
            .setDescription(null)
            .setType(PolicyType.PASSWORD);

        // Call the method under test
        Policy result = builder.buildAndCreate(policyApiMock);

        // Verify the Policy object passed to the API
        ArgumentCaptor<Policy> policyCaptor = ArgumentCaptor.forClass(Policy.class);
        verify(policyApiMock).createPolicy(policyCaptor.capture(), eq(true));

        Policy capturedPolicy = policyCaptor.getValue();
        assertNull(capturedPolicy.getName());
        assertNull(capturedPolicy.getDescription());
    }

    @Test
    public void testBuildWithEmptyStrings() throws ApiException {
        // Setup
        Policy mockPolicy = new Policy();
        when(policyApiMock.createPolicy(any(Policy.class), eq(true))).thenReturn(mockPolicy);

        // Configure builder with empty strings
        builder.setName("")
            .setDescription("")
            .setType(PolicyType.PASSWORD);

        // Call the method under test
        Policy result = builder.buildAndCreate(policyApiMock);

        // Verify the Policy object passed to the API
        ArgumentCaptor<Policy> policyCaptor = ArgumentCaptor.forClass(Policy.class);
        verify(policyApiMock).createPolicy(policyCaptor.capture(), eq(true));

        Policy capturedPolicy = policyCaptor.getValue();
        assertNull(capturedPolicy.getName());
        assertNull(capturedPolicy.getDescription());
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "PolicyType cannot be blank, needs to be specified.")
    public void testBuildWithoutPolicyType() throws ApiException {
        // Configure builder without policy type
        builder.setName("Test Policy");

        // Should throw an exception
        builder.buildAndCreate(policyApiMock);
    }

   @Test
   public void testBuildWithNullStatus() throws ApiException, NoSuchFieldException, IllegalAccessException {
       // Setup
       Policy mockPolicy = new Policy();
       when(policyApiMock.createPolicy(any(Policy.class), eq(true))).thenReturn(mockPolicy);

       // Configure builder
       builder.setName("Test Policy")
              .setType(PolicyType.PASSWORD);

       // Use reflection to set the protected status field to null
       Field statusField = DefaultPolicyBuilder.class.getDeclaredField("status");
       statusField.setAccessible(true);
       statusField.set(builder, null);

       // Call the method under test
       Policy result = builder.buildAndCreate(policyApiMock);

       // Verify the Policy object passed to the API
       ArgumentCaptor<Policy> policyCaptor = ArgumentCaptor.forClass(Policy.class);
       verify(policyApiMock).createPolicy(policyCaptor.capture(), eq(true));

       Policy capturedPolicy = policyCaptor.getValue();
       assertNull(capturedPolicy.getStatus());
   }

    @Test
    public void testBuildWithInactiveStatus() throws ApiException {
        // Setup
        Policy mockPolicy = new Policy();
        when(policyApiMock.createPolicy(any(Policy.class), eq(false))).thenReturn(mockPolicy);

        // Configure builder with INACTIVE status
        builder.setName("Test Policy")
            .setType(PolicyType.PASSWORD)
            .setStatus(LifecycleStatus.INACTIVE);

        // Call the method under test
        Policy result = builder.buildAndCreate(policyApiMock);

        // Verify the Policy object passed to the API with isActive=false
        verify(policyApiMock).createPolicy(any(Policy.class), eq(false));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testBuildAndCreateWithApiException() throws ApiException {
        // Setup
        when(policyApiMock.createPolicy(any(Policy.class), anyBoolean())).thenThrow(new ApiException());

        // Configure builder
        builder.setName("Test Policy")
            .setType(PolicyType.PASSWORD);

        // Should wrap ApiException in RuntimeException
        builder.buildAndCreate(policyApiMock);
    }
}
