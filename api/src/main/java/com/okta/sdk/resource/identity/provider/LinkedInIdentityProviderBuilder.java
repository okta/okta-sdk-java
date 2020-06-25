package com.okta.sdk.resource.identity.provider;

import com.okta.commons.lang.Classes;

public interface LinkedInIdentityProviderBuilder extends IdentityProviderBuilder<LinkedInIdentityProviderBuilder> {

    static LinkedInIdentityProviderBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.identity.provider.DefaultLinkedInIdentityProviderBuilder");
    }

    LinkedInIdentityProviderBuilder setIsProfileMaster(Boolean isProfileMaster);

    LinkedInIdentityProviderBuilder setUserNameTemplate(String userNameTemplate);
}
