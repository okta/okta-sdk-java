/*
 * Copyright 2014 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.impl.ds.api;

import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.impl.ds.DefaultResourceDataResult;
import com.okta.sdk.impl.ds.Filter;
import com.okta.sdk.impl.ds.FilterChain;
import com.okta.sdk.impl.ds.ResourceAction;
import com.okta.sdk.impl.ds.ResourceDataRequest;
import com.okta.sdk.impl.ds.ResourceDataResult;
import com.okta.sdk.impl.security.ApiKeySecretEncryptionService;
import com.okta.sdk.impl.security.EncryptionService;
import com.okta.sdk.lang.Assert;
import com.okta.sdk.resource.Resource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0.0
 */
public class DecryptApiKeySecretFilter implements Filter {

//    private static String ENCRYPTION_KEY_SALT = ApiKeyParameter.ENCRYPTION_KEY_SALT.getName();
//    private static String ENCRYPTION_KEY_SIZE = ApiKeyParameter.ENCRYPTION_KEY_SIZE.getName();
//    private static String ENCRYPTION_KEY_ITERATIONS = ApiKeyParameter.ENCRYPTION_KEY_ITERATIONS.getName();
//    private static String ENCRYPTION_METADATA = ApiKeyParameter.ENCRYPTION_METADATA.getName();

    private final ClientCredentials clientCredentials;

    private final String SECRET_PROPERTY_NAME = "secret";

    public DecryptApiKeySecretFilter(ClientCredentials clientCredentials) {
        Assert.notNull(clientCredentials);
        this.clientCredentials = clientCredentials;
    }

    @Override
    public ResourceDataResult filter(ResourceDataRequest request, FilterChain chain) {

        ResourceDataResult result = chain.filter(request);

        if (request.getAction() == ResourceAction.DELETE) {
            return result;
        }

        Class<? extends Resource> clazz = result.getResourceClass();

        if (!(ClientCredentials.class.isAssignableFrom(clazz))) {
            return result;
        }

        Map<String, Object> data = result.getData();

        return new DefaultResourceDataResult(result.getAction(), result.getUri(), clazz, null);//clone(data)); // FIXME: fix clone method
    }

//    private Map<String, Object> clone(Map<String, Object> input) {
//
//        if (!input.containsKey(ENCRYPTION_METADATA)) {
//            return input;
//        }
//
//        @SuppressWarnings("unchecked")
//        Map<String, Object> metadata = (Map<String, Object>) input.get(ENCRYPTION_METADATA);
//
//        byte[] base64Salt = ((String) metadata.get(ENCRYPTION_KEY_SALT)).getBytes();
//        Integer iterations = (Integer) metadata.get(ENCRYPTION_KEY_ITERATIONS);
//        Integer size = (Integer) metadata.get(ENCRYPTION_KEY_SIZE);
//
//        EncryptionService service = new ApiKeySecretEncryptionService.Builder().setPassword(apiKeyCredentials.getSecret().toCharArray()).setKeySize(size)
//                .setIterations(iterations).setBase64Salt(base64Salt).build();
//
//        String encryptedSecret = (String) input.get(SECRET_PROPERTY_NAME);
//
//        Map<String, Object> clonedData = new LinkedHashMap<String, Object>();
//
//        for (Map.Entry<String, Object> entry : input.entrySet()) {
//
//            String key = entry.getKey();
//
//            if (key.equals(ENCRYPTION_METADATA)) {
//                continue;
//            }
//
//            if (key.equals(SECRET_PROPERTY_NAME)) {
//                clonedData.put(key, service.decryptBase64String(encryptedSecret));
//                continue;
//            }
//
//            clonedData.put(key, entry.getValue());
//        }
//
//        return clonedData;
//    }

}
