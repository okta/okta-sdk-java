package com.okta.sdk.resource.identity.provider;

import com.okta.commons.lang.Classes;

public interface FacebookIdentityProviderBuilder extends IdentityProviderBuilder<FacebookIdentityProviderBuilder> {

    static FacebookIdentityProviderBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.identity.provider.DefaultFacebookIdentityProviderBuilder");
    }

    FacebookIdentityProviderBuilder setIsProfileMaster(Boolean isProfileMaster);

    FacebookIdentityProviderBuilder setUserNameTemplate(String userNameTemplate);
}
