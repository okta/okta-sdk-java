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
package com.okta.sdk.impl.resource;


import com.okta.sdk.client.Client;
import com.okta.sdk.lang.Collections;
import com.okta.sdk.lang.Strings;
import com.okta.sdk.resource.user.RecoveryQuestionCredential;
import com.okta.sdk.resource.user.UserBuilder;
import com.okta.sdk.resource.user.PasswordCredential;
import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.UserCredentials;
import com.okta.sdk.resource.user.UserProfile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DefaultUserBuilder implements UserBuilder {


    private char[] password;
    private String securityQuestion;
    private String securityQuestionAnswer;
    private String email;
    private String login;
    private String secondEmail;
    private String firstName;
    private String lastName;
    private String mobilePhone;
    private Boolean active;
    private Boolean provider;
    private Set<String> groupIds = new HashSet<>();

    private Map<String, Object> customProfileAttributes = new LinkedHashMap<>();

    public UserBuilder setPassword(char[] password) {
        this.password = Arrays.copyOf(password, password.length);
        return this;
    }

    public UserBuilder setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
        return this;
    }

    public UserBuilder setSecurityQuestionAnswer(String answer) {
        this.securityQuestionAnswer = answer;
        return this;
    }

    public UserBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder setLogin(String login) {
        this.login = login;
        return this;
    }

    public UserBuilder setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
        return this;
    }

    public UserBuilder setSecondEmail(String secondEmail) {
        this.secondEmail = secondEmail;
        return this;
    }

    public UserBuilder setActive(Boolean active) {
        this.active = active;
        return this;
    }

    public UserBuilder setProvider(Boolean provider) {
        this.provider = provider;
        return this;
    }

    public UserBuilder setProfileProperties(Map<String, Object> profileProperties) {

        this.customProfileAttributes.clear();
        return putAllProfileProperties(profileProperties);
    }

    public UserBuilder putAllProfileProperties(Map<String, Object> profileProperties) {

        this.customProfileAttributes.putAll(profileProperties);
        return this;
    }

    public UserBuilder putProfileProperty(String key, Object value) {
        this.customProfileAttributes.put(key, value);
        return this;
    }

    public UserBuilder setGroups(Set<String> groupIds) {
        this.groupIds = groupIds;
        return this;
    }

    public UserBuilder addGroup(String groupId) {
        this.groupIds.add(groupId);
        return this;
    }

    private User build(Client client) {

        User user = client.instantiate(User.class);
        user.setProfile(client.instantiate(UserProfile.class));
        UserProfile userProfile = user.getProfile();
        if (Strings.hasText(firstName)) userProfile.setFirstName(firstName);
        if (Strings.hasText(lastName)) userProfile.setLastName(lastName);
        if (Strings.hasText(email)) userProfile.setEmail(email);
        if (Strings.hasText(secondEmail)) userProfile.setSecondEmail(secondEmail);
        if (Strings.hasText(mobilePhone)) userProfile.setMobilePhone(mobilePhone);

        if (Strings.hasText(login)) {
            userProfile.setLogin(login);
        }
        else {
            userProfile.setLogin(email);
        }

        if (!Collections.isEmpty(groupIds)) {
            if (user instanceof AbstractResource) {
                ((AbstractResource) user).setProperty("groupIds", groupIds, true);
            } else {
                throw new IllegalArgumentException("'User' is not an instance of 'AbstractResource', so 'groupIds' cannot be set. This would only happen if the implementation of 'User' has been customized.");
            }
        }

        userProfile.putAll(customProfileAttributes);

        if (password != null && password.length > 0 || Strings.hasText(securityQuestion)) {
            UserCredentials credentials = client.instantiate(UserCredentials.class);
            user.setCredentials(credentials);

            if (Strings.hasText(securityQuestion)) {
                RecoveryQuestionCredential question = client.instantiate(RecoveryQuestionCredential.class);
                question.setQuestion(securityQuestion);
                question.setAnswer(securityQuestionAnswer);
                credentials.setRecoveryQuestion(question);
            }

            if (password != null && password.length > 0) {
                PasswordCredential passwordCredential = client.instantiate(PasswordCredential.class);
                credentials.setPassword(passwordCredential.setValue(password));
            }
        }

        return user;
    }

    @Override
    public User buildAndCreate(Client client) {
        return client.createUser(build(client), active, provider);
    }
}
