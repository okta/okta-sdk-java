/*!
 * Copyright (c) 2015-2016, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.okta.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.FilterBuilder;
import com.okta.sdk.framework.JsonApiClient;
import com.okta.sdk.models.factors.OrgAuthFactor;

import java.io.IOException;
import java.util.List;

public class FactorsAdminApiClient extends JsonApiClient {

    public FactorsAdminApiClient(ApiClientConfiguration config) {
        super(config);
    }

    ////////////////////////////////////////////
    // COMMON METHODS
    ////////////////////////////////////////////

    public List<OrgAuthFactor> getOrgFactors() throws IOException {
        return get(getEncodedPath("/factors"), new TypeReference<List<OrgAuthFactor>>() { });
    }

    public List<OrgAuthFactor> getOrgFactors(FilterBuilder filterBuilder) throws IOException {
        return get(getEncodedPath("/factors?" + FILTER + "=%s", filterBuilder.toString()), new TypeReference<List<OrgAuthFactor>>() { });
    }

    // OrgAuthFactor LIFECYCLE

    public OrgAuthFactor activateOrgFactor(String orgAuthFactorId) throws IOException {
        return activateOrgFactor(orgAuthFactorId, null);
    }

    public OrgAuthFactor activateOrgFactor(String orgAuthFactorId, OrgAuthFactor orgAuthFactor) throws IOException {
        return post(getEncodedPath("/factors/%s/lifecycle/activate", orgAuthFactorId), orgAuthFactor, new TypeReference<OrgAuthFactor>() {
        });
    }

    public OrgAuthFactor deActivateOrgFactor(String orgAuthFactorId) throws IOException {
        return post(getEncodedPath("/factors/%s/lifecycle/deactivate", orgAuthFactorId), null, new TypeReference<OrgAuthFactor>() { });
    }

    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/org%s", this.apiVersion, relativePath);
    }

}
