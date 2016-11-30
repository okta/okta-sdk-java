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
import com.okta.sdk.framework.JsonApiClient;
import com.okta.sdk.models.factors.Factor;
import com.okta.sdk.models.factors.FactorCatalogEntry;
import com.okta.sdk.models.factors.FactorDevice;
import com.okta.sdk.models.factors.FactorDeviceEnrollRequest;
import com.okta.sdk.models.factors.FactorDeviceRequest;
import com.okta.sdk.models.factors.FactorEnrollRequest;
import com.okta.sdk.models.factors.FactorUpdateRequest;
import com.okta.sdk.models.factors.FactorVerificationResponse;
import com.okta.sdk.models.factors.Question;
import com.okta.sdk.models.factors.Verification;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactorsApiClient extends JsonApiClient {

    public static final String USER_ID = "userId";
    public static final String FACTOR_ID = "factorId";
    public static final String PASSCODE = "passCode";
    public static final String NEXT_PASSCODE = "nextPassCode";

    public FactorsApiClient(ApiClientConfiguration config) {
        super(config);
    }

    ////////////////////////////////////////////
    // COMMON METHODS
    ////////////////////////////////////////////

    public List<FactorCatalogEntry> getFactorsCatalog(String userId) throws IOException {
        return get(getEncodedPath("/%s/factors/catalog", userId), new TypeReference<List<FactorCatalogEntry>>() { });
    }

    public List<Factor> getUserLifecycleFactors(String userId) throws IOException {
        return get(getEncodedPath("/%s/factors", userId), new TypeReference<List<Factor>>() { });
    }

    public List<Question> getAvailableQuestions(String userId) throws IOException {
        return get(getEncodedPath("/%s/factors/questions", userId), new TypeReference<List<Question>>() { });
    }

    // Factor CRUD

    public Factor enrollFactor(String userId, FactorEnrollRequest enrollRequest) throws IOException {
        return post(getEncodedPath("/%s/factors", userId), enrollRequest, new TypeReference<Factor>() { });
    }

    public Factor enrollSecurityQuestion(String userId, String question, String answer) throws IOException {
        FactorEnrollRequest factorEnrollRequest = new FactorEnrollRequest();
        factorEnrollRequest.setFactorType("question");
        factorEnrollRequest.setProvider("OKTA");
        Map profile = new HashMap<String, String>();
        profile.put("question", question);
        profile.put("answer", answer);
        factorEnrollRequest.setProfile(profile);

        return enrollFactor(userId, factorEnrollRequest);
    }

    public Factor enrollFactor(String userId, FactorEnrollRequest enrollRequest, boolean updatePhone) throws IOException {
        return post(getEncodedPath("/%s/factors?updatePhone=%s", userId, String.valueOf(updatePhone)), enrollRequest, new TypeReference<Factor>() { });
    }

    public Factor getFactor(String userId, String userFactorId) throws IOException {
        return get(getEncodedPath("/%s/factors/%s", userId, userFactorId), new TypeReference<Factor>() { });
    }

    public Factor updateFactor(String userId, String userFactorId, FactorUpdateRequest updateRequest) throws IOException {
        return put(getEncodedPath("/%s/factors/%s", userId, userFactorId), updateRequest, new TypeReference<Factor>() { });
    }

    public void resetFactor(String userId, String userFactorId) throws IOException {
        delete(getEncodedPath("/%s/factors/%s", userId, userFactorId));
    }

    // Factor LIFECYCLE

    public Factor activateFactor(String userId, String factorId, String passCode) throws IOException {
        return activateFactor(userId, factorId, passCode, null);
    }

    public Factor activateFactor(String userId, String factorId, String passCode, String nextPassCode) throws IOException {
        Verification verification = new Verification();
        verification.setPassCode(passCode);
        verification.setNextPassCode(nextPassCode);
        return post(getEncodedPath("/%s/factors/%s/lifecycle/activate", userId, factorId), verification, new TypeReference<Factor>() { });
    }

    public Factor resendCode(String userId, String userFactorId) throws IOException {
        return post(getEncodedPath("/%s/factors/%s/resend", userId, userFactorId), null, new TypeReference<Factor>() { });
    }

    public FactorVerificationResponse verifyFactor(String userId, String factorId, Verification verification) throws IOException {
        return post(getEncodedPath("/%s/factors/%s/verify", userId, factorId), verification, new TypeReference<FactorVerificationResponse>() { });
    }

    // FactorDevice CRUD

    public FactorDevice enrollFactorDevice(String userId, FactorDeviceEnrollRequest factorDeviceEnrollRequest) throws IOException {
        return post(getEncodedPath("/%s/devices", userId), factorDeviceEnrollRequest, new TypeReference<FactorDevice>() { });
    }
    
    public FactorDevice getFactorDevice(String userId, String userFactorId, String phoneId) throws IOException {
        return get(getEncodedPath("/%s/factors/%s/devices/%s", userId, userFactorId, phoneId), new TypeReference<FactorDevice>() { });
    }

    public FactorDevice updateFactorDevice(String userId, String userFactorId, FactorDeviceRequest updateRequest) throws IOException {
        return post(getEncodedPath("/%s/factors/%s", userId, userFactorId), updateRequest, new TypeReference<FactorDevice>() { });
    }

    // FactorDevice LIFECYCLE

    public Factor activateFactorDevice(String userId, String userFactorId, String deviceId, String passCode) throws IOException {
        Verification verification = new Verification();
        verification.setPassCode(passCode);
        return post(getEncodedPath("/%s/factors/%s/devices/%s/lifecycle/activate", userId, userFactorId, deviceId), verification, new TypeReference<Factor>() { });
    }

    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/users%s", this.apiVersion, relativePath);
    }

}
