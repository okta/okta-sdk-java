package com.okta.sdk.resource.identity.provider;

import com.okta.commons.lang.Classes;

public interface GoogleIdentityProviderBuilder extends IdentityProviderBuilder<GoogleIdentityProviderBuilder> {
    
    static GoogleIdentityProviderBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.identity.provider.DefaultGoogleIdentityProviderBuilder");
    }

    GoogleIdentityProviderBuilder setIsProfileMaster(Boolean isProfileMaster);

    GoogleIdentityProviderBuilder setUserNameTemplate(String userNameTemplate);
}
