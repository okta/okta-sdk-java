package com.okta.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.Filter;
import com.okta.sdk.framework.JsonApiClient;
import com.okta.sdk.models.factors.OrgAuthFactor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FactorsAdminApiClient extends JsonApiClient {

    public FactorsAdminApiClient(ApiClientConfiguration config) {
        super(config);
    }

    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/org%s", this.apiVersion, relativePath);
    }

    ////////////////////////////////////////////
    // COMMON METHODS
    ////////////////////////////////////////////

    public List<OrgAuthFactor> getOrgFactors() throws IOException {
        return get(getEncodedPath("/factors"), new TypeReference<List<OrgAuthFactor>>() { });
    }

    public List<OrgAuthFactor> getOrgFactors(Filter filter) throws IOException {
        return get(getEncodedPath("/factors?filter=%s", filter.toString()), new TypeReference<List<OrgAuthFactor>>() { });
    }

    // OrgAuthFactor LIFECYCLE

    public OrgAuthFactor activateOrgFactor(String orgAuthFactorId) throws IOException {
        return activateOrgFactor(orgAuthFactorId, null);
    }

    public OrgAuthFactor activateOrgFactor(String orgAuthFactorId, OrgAuthFactor orgAuthFactor) throws IOException {
        return post(getEncodedPath("/factors/%s/lifecycle/activate", orgAuthFactorId), orgAuthFactor, new TypeReference<OrgAuthFactor>() { });
    }

    public OrgAuthFactor deActivateOrgFactor(String orgAuthFactorId) throws IOException {
        return post(getEncodedPath("/factors/%s/lifecycle/deactivate", orgAuthFactorId), null, new TypeReference<OrgAuthFactor>() { });
    }
}
