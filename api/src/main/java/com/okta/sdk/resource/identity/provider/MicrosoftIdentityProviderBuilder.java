package com.okta.sdk.resource.identity.provider;

import com.okta.commons.lang.Classes;

public interface MicrosoftIdentityProviderBuilder extends IdentityProviderBuilder<MicrosoftIdentityProviderBuilder> {

    static MicrosoftIdentityProviderBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.identity.provider.DefaultMicrosoftIdentityProviderBuilder");
    }

    MicrosoftIdentityProviderBuilder setIsProfileMaster(Boolean isProfileMaster);

    MicrosoftIdentityProviderBuilder setUserNameTemplate(String userNameTemplate);
}
