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

import com.okta.sdk.api.ApiKey;
import com.okta.sdk.api.ApiKeyList;
import com.okta.sdk.impl.api.ApiKeyParameter;
import com.okta.sdk.impl.api.DefaultApiKeyCriteria;
import com.okta.sdk.impl.api.DefaultApiKeyList;
import com.okta.sdk.impl.ds.DefaultResourceDataRequest;
import com.okta.sdk.impl.ds.Filter;
import com.okta.sdk.impl.ds.FilterChain;
import com.okta.sdk.impl.ds.ResourceAction;
import com.okta.sdk.impl.ds.ResourceDataRequest;
import com.okta.sdk.impl.ds.ResourceDataResult;
import com.okta.sdk.impl.http.CanonicalUri;
import com.okta.sdk.impl.http.QueryString;
import com.okta.sdk.impl.http.QueryStringFactory;
import com.okta.sdk.impl.http.support.DefaultCanonicalUri;
import com.okta.sdk.impl.query.DefaultEqualsExpressionFactory;
import com.okta.sdk.impl.security.DefaultSaltGenerator;
import com.okta.sdk.impl.security.SaltGenerator;
import com.okta.sdk.lang.Collections;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.okta.sdk.impl.api.DefaultApiKeyCriteria.DEFAULT_ENCRYPTION_ITERATIONS;
import static com.okta.sdk.impl.api.DefaultApiKeyCriteria.DEFAULT_ENCRYPTION_SIZE;

/**
 * @since 1.0.RC
 */
public class ApiKeyQueryFilter implements Filter {

    private static String ENCRYPT_SECRET = ApiKeyParameter.ENCRYPT_SECRET.getName();
    private static String ENCRYPTION_KEY_SALT = ApiKeyParameter.ENCRYPTION_KEY_SALT.getName();
    private static String ENCRYPTION_KEY_SIZE = ApiKeyParameter.ENCRYPTION_KEY_SIZE.getName();
    private static String ENCRYPTION_KEY_ITERATIONS = ApiKeyParameter.ENCRYPTION_KEY_ITERATIONS.getName();
    private static String ENCRYPTION_METADATA = ApiKeyParameter.ENCRYPTION_METADATA.getName();

    private final SaltGenerator saltGenerator;
    private final QueryStringFactory queryStringFactory;

    public ApiKeyQueryFilter(QueryStringFactory queryStringFactory) {
        saltGenerator = new DefaultSaltGenerator();
        this.queryStringFactory = queryStringFactory;
    }

    @Override
    public ResourceDataResult filter(ResourceDataRequest request, FilterChain chain) {

        if (request.getAction() == ResourceAction.DELETE) {
            return chain.filter(request);
        }

        Class clazz = request.getResourceClass();

        if (!(ApiKey.class.isAssignableFrom(clazz) || ApiKeyList.class.isAssignableFrom(clazz))) {
            return chain.filter(request);
        }

        QueryString query = request.getUri().getQuery();

        boolean isQueryEmpty = Collections.isEmpty(query);

        boolean addEncryptionCriteria = isQueryEmpty || !query.containsKey(ENCRYPT_SECRET);

        //If encryptSecret is present, we assume encryptionKeySalt is there as well. If not the request to the server
        //will error.
        boolean addEncryptionMetadata = addEncryptionCriteria || Boolean.parseBoolean(query.get(ENCRYPT_SECRET));

        if (addEncryptionCriteria) {

            DefaultApiKeyCriteria criteria = new DefaultApiKeyCriteria();
            criteria.add(new DefaultEqualsExpressionFactory(ENCRYPT_SECRET).eq(Boolean.TRUE));
            criteria.add(new DefaultEqualsExpressionFactory(ENCRYPTION_KEY_SIZE).eq(DEFAULT_ENCRYPTION_SIZE));
            criteria.add(new DefaultEqualsExpressionFactory(ENCRYPTION_KEY_ITERATIONS).eq(DEFAULT_ENCRYPTION_ITERATIONS));
            criteria.add(new DefaultEqualsExpressionFactory(ENCRYPTION_KEY_SALT).eq(saltGenerator.generate()));

            QueryString encryptionQueryParams = queryStringFactory.createQueryString(criteria);

            if (query == null) {
                CanonicalUri uri = new DefaultCanonicalUri(request.getUri().getAbsolutePath(), encryptionQueryParams);
                request = new DefaultResourceDataRequest(request.getAction(), uri, request.getResourceClass(), request.getData());
            } else {
                query.putAll(encryptionQueryParams);
            }
        }

        ResourceDataResult result = chain.filter(request);

        if (addEncryptionMetadata) {

            query = request.getUri().getQuery();

            Map<String, Object> encryptionMetadata = new LinkedHashMap<String, Object>();

            String salt = query.get(ENCRYPTION_KEY_SALT);

            Integer size = query.containsKey(ENCRYPTION_KEY_SIZE) ? Integer.valueOf(query.get(ENCRYPTION_KEY_SIZE)) : DEFAULT_ENCRYPTION_SIZE;

            Integer iterations = query.containsKey(ENCRYPTION_KEY_ITERATIONS) ? Integer.valueOf(query.get(ENCRYPTION_KEY_ITERATIONS)) : DEFAULT_ENCRYPTION_ITERATIONS;

            encryptionMetadata.put(ENCRYPTION_KEY_SALT, salt);
            encryptionMetadata.put(ENCRYPTION_KEY_SIZE, size);
            encryptionMetadata.put(ENCRYPTION_KEY_ITERATIONS, iterations);

            Map<String, Object> data = result.getData();

            if (DefaultApiKeyList.isCollectionResource(data)) {

                @SuppressWarnings("unchecked")
                Collection<Map<String, Object>> items = (Collection<Map<String, Object>>) data.get(DefaultApiKeyList.ITEMS_PROPERTY_NAME);

                for (Map<String, Object> item : items) {
                    item.put(ENCRYPTION_METADATA, encryptionMetadata);
                }
            } else {
                data.put(ENCRYPTION_METADATA, encryptionMetadata);
            }
        }
        return result;
    }
}
