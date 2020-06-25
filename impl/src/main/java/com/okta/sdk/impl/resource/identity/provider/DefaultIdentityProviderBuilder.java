package com.okta.sdk.impl.resource.identity.provider;

import com.okta.sdk.client.Client;
import com.okta.sdk.resource.identity.provider.IdentityProvider;
import com.okta.sdk.resource.identity.provider.IdentityProviderBuilder;
import com.okta.sdk.resource.policy.PolicySubjectMatchType;

import java.util.List;

@SuppressWarnings("rawtypes")
public class DefaultIdentityProviderBuilder<T extends IdentityProviderBuilder> implements IdentityProviderBuilder<T> {

    protected String name;
    protected List<String> scopes;
    protected String clientId;
    protected String clientSecret;
    protected Integer maxClockSkew;
    protected PolicySubjectMatchType policySubjectMatchType;

    @Override
    public T setName(String name) {
        this.name = name;
        return self();
    }

    @Override
    public T setScopes(List<String> scopes) {
        this.scopes = scopes;
        return self();
    }

    @Override
    public T setClientId(String clientId) {
        this.clientId = clientId;
        return self();
    }

    @Override
    public T setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return self();
    }

    @Override
    public T setMaxClockSkew(Integer maxClockSkew) {
        this.maxClockSkew = maxClockSkew;
        return self();
    }

    @Override
    public T setPolicySubjectMatchType(PolicySubjectMatchType policySubjectMatchType) {
        this.policySubjectMatchType = policySubjectMatchType;
        return self();
    }

    @Override
    public IdentityProvider buildAndCreate(Client client) {
        return client.createIdentityProvider(client.instantiate(IdentityProvider.class));
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }
}
