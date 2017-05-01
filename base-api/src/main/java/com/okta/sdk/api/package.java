/*
 * Copyright 2017 Okta
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
/**
 * Support for securing your application's REST API with {@link com.okta.sdk.api.ApiKey ApiKeys}.
 *
 * <p>HTTP requests authenticated with {@link com.okta.sdk.api.ApiKey ApiKeys} and sent to your application are
 * asserted via {@link com.okta.sdk.application.Application#authenticateAccount(AuthenticationRequest)} method.</p>
 *
 * @see com.okta.sdk.api.ApiKey ApiKey
 * @see com.okta.sdk.account.Account#getApiKeys() account.getApiKeys()
 * @see com.okta.sdk.account.Account#createApiKey() account.createApiKey()
 * @see com.okta.sdk.application.Application#authenticateAccount(AuthenticationRequest)}
 * @since 1.0.RC
 */
package com.okta.sdk.api;


