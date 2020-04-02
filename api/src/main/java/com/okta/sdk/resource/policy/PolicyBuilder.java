package com.okta.sdk.resource.policy;

import com.okta.commons.lang.Classes;
import com.okta.sdk.client.Client;


public interface PolicyBuilder {

    static PolicyBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.DefaultPolicyBuilder");
    }

    PolicyBuilder setName(String name);

    PolicyBuilder setDescription(String description);

    PolicyBuilder setType(PolicyType policyType);

    PolicyBuilder setPriority(Integer priority);

    PolicyBuilder setStatus(Policy.StatusEnum status);

    Policy buildAndCreate(Client client);

}
