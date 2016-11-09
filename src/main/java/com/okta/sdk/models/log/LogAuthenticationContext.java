package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

import java.util.Objects;

public final class LogAuthenticationContext extends ApiObject {

    /**
     * The authentication provider
     */
    private final String authenticationProvider;

    /**
     * The credential provider
     */
    private final String credentialProvider;

    /**
     * Type of credential
     */
    private final String credentialType;

    /**
     * Issuer of the authentication
     */
    private final LogIssuer issuer;

    /**
     * The authentication interface
     */
    @JsonProperty(value = "interface")
    private final String authenticationInterface;

    /**
     * The step in the authentication process
     */
    private final int authenticationStep;

    /**
     * The id of the external session
     */
    private final String externalSessionId;

    @JsonCreator
    public LogAuthenticationContext(
            @JsonProperty("authenticationProvider") String authenticationProvider,
            @JsonProperty("credentialProvider") String credentialProvider,
            @JsonProperty("credentialType") String credentialType,
            @JsonProperty("issuer") LogIssuer issuer,
            @JsonProperty("interface") String authenticationInterface,
            @JsonProperty("authenticationStep") int authenticationStep,
            @JsonProperty("externalSessionId") String externalSessionId
    ) {
        this.authenticationProvider = authenticationProvider;
        this.credentialProvider = credentialProvider;
        this.credentialType = credentialType;
        this.issuer = issuer;
        this.authenticationInterface = authenticationInterface;
        this.authenticationStep = authenticationStep;
        this.externalSessionId = externalSessionId;
    }

    public String getAuthenticationProvider() {
        return authenticationProvider;
    }

    public String getCredentialProvider() {
        return credentialProvider;
    }

    public String getCredentialType() {
        return credentialType;
    }

    public LogIssuer getIssuer() {
        return issuer;
    }

    public String getAuthenticationInterface() {
        return authenticationInterface;
    }

    public int getAuthenticationStep() {
        return authenticationStep;
    }

    public String getExternalSessionId() {
        return externalSessionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                authenticationProvider,
                credentialProvider,
                credentialType,
                issuer,
                authenticationInterface,
                authenticationStep,
                externalSessionId
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LogAuthenticationContext)) {
            return false;
        }
        final LogAuthenticationContext other = (LogAuthenticationContext) obj;
        return Objects.equals(this.authenticationProvider, other.authenticationProvider)
                && Objects.equals(this.credentialProvider, other.credentialProvider)
                && Objects.equals(this.credentialType, other.credentialType)
                && Objects.equals(this.issuer, other.issuer)
                && Objects.equals(this.authenticationInterface, other.authenticationInterface)
                && Objects.equals(this.authenticationStep, other.authenticationStep)
                && Objects.equals(this.externalSessionId, other.externalSessionId);
    }
}
