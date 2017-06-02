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
import com.okta.sdk.resource.user.User;

import java.util.Map;
import java.util.Set;

public interface UserBuilder {

    UserBuilder INSTANCE = Classes.newInstance("com.okta.sdk.impl.resource.DefaultUserBuilder");

    UserBuilder setPassword(String password);

    UserBuilder setSecurityQuestion(String question);

    UserBuilder setSecurityQuestionAnswer(String answer);

    UserBuilder setEmail(String email);

    UserBuilder setFirstName(String firstName);

    UserBuilder setLastName(String lastName);

    UserBuilder setLogin(String login);

    UserBuilder setMobilePhone(String mobilePhone);

    UserBuilder setSecondEmail(String secondEmail);

    UserBuilder setProfileProperties(Map<String, Object> profileProperties);

    UserBuilder putAllProfileProperties(Map<String, Object> profileProperties);

    UserBuilder putProfileProperty(String key, Object value);

    User buildAndCreate(Client client);
    User buildAndCreate(Client client, boolean active);
}
