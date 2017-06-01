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
package com.okta.sdk.resource;

import com.okta.sdk.client.Client;
import com.okta.sdk.lang.Classes;

public interface GroupBuilder {

    GroupBuilder INSTANCE = Classes.newInstance("com.okta.sdk.impl.resource.DefaultGroupBuilder");

    GroupBuilder setName(String name);

    GroupBuilder setDescription(String description);

    UserGroup buildAndCreate(Client client);

}
