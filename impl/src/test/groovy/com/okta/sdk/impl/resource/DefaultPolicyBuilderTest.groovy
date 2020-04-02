package com.okta.sdk.impl.resource

import com.okta.sdk.client.Client
import com.okta.sdk.resource.policy.Policy
import com.okta.sdk.resource.policy.PolicyType
import org.testng.annotations.Test

import static com.okta.sdk.impl.Util.expect
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class DefaultPolicyBuilderTest {

    @Test
    void basicUsage() {

        def client = mock(Client)
        def policy = mock(Policy)
        when(client.instantiate(Policy.class)).thenReturn(policy)

        new DefaultPolicyBuilder()
            .setName("Test Policy")
            .setDescription("dummy policy for test")
            .setPriority(1)
            .setType(PolicyType.OKTA_SIGN_ON)
            .setStatus(Policy.StatusEnum.ACTIVE)
            .buildAndCreate(client)

        verify(client).createPolicy(eq(policy))
        verify(policy).setName("Test Policy")
        verify(policy).setDescription("dummy policy for test")
    }

    @Test
    void createWithoutPolicyType() {
        def client = mock(Client)
        def policy = mock(Policy)
        when(client.instantiate(Policy.class)).thenReturn(policy)

        expect IllegalArgumentException, {
            new DefaultPolicyBuilder()
                .setName("Test Policy")
                .setDescription("dummy policy for test")
                .setPriority(1)
                .setStatus(Policy.StatusEnum.ACTIVE)
                .buildAndCreate(client)
        }
    }
}
