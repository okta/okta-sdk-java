/*!
 * Copyright (c) 2015-2017, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

import java.util.Objects;

public final class LogAuthenticationContext extends ApiObject {

    /**
     * The authentication provider.
     */
    private final String authenticationProvider;

    /**
     * The credential provider.
     */
    private final String credentialProvider;

    /**
     * Type of credential.
     */
    private final String credentialType;

    /**
     * Issuer of the authentication.
     */
    private final LogIssuer issuer;

    /**
     * The authentication interface.
     */
    @JsonProperty(value = "interface")
    private final String authenticationInterface;

    /**
     * The step in the authentication process.
     */
    private final int authenticationStep;

    /**
     * The id of the external session.
     */
    private final String externalSessionId;

    /**
     * Creates the LogAuthenticationContext
     *
     * @param authenticationProvider {@link String}
     * @param credentialProvider {@link String}
     * @param credentialType {@link String}
     * @param issuer {@link LogIssuer}
     * @param authenticationInterface {@link String}
     * @param authenticationStep {@link Integer}
     * @param externalSessionId {@link String}
     */
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

    /**
     * Returns the authenticationProvider.
     * @return {@link String}
     */
    public String getAuthenticationProvider() {
        return authenticationProvider;
    }

    /**
     * Returns the credentialProvider.
     * @return {@link String}
     */
    public String getCredentialProvider() {
        return credentialProvider;
    }

    /**
     * Returns the credentialType.
     * @return {@link String}
     */
    public String getCredentialType() {
        return credentialType;
    }

    /**
     * Returns the issuer.
     * @return {@link LogIssuer}
     */
    public LogIssuer getIssuer() {
        return issuer;
    }

    /**
     * Returns the authenticationInterface.
     * @return {@link String}
     */
    public String getAuthenticationInterface() {
        return authenticationInterface;
    }

    /**
     * Returns the authenticationStep.
     * @return {@link Integer}
     */
    public int getAuthenticationStep() {
        return authenticationStep;
    }

    /**
     * Returns the externalSessionId.
     * @return {@link String}
     */
    public String getExternalSessionId() {
        return externalSessionId;
    }

    /**
     * Hashes the object.
     * @return {@link Integer}
     */
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

    /**
     * Overriding method for equals.
     *
     * @param obj {@link Object}
     * @return {@link Boolean}
     */
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
