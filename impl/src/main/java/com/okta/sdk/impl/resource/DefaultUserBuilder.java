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

import com.okta.commons.lang.Collections;
import com.okta.commons.lang.Strings;
import com.okta.sdk.resource.user.UserBuilder;
import org.openapitools.client.api.UserApi;
import org.openapitools.client.model.AuthenticationProvider;
import org.openapitools.client.model.CreateUserRequest;
import org.openapitools.client.model.PasswordCredential;
import org.openapitools.client.model.PasswordCredentialHash;
import org.openapitools.client.model.PasswordCredentialHook;
import org.openapitools.client.model.RecoveryQuestionCredential;
import org.openapitools.client.model.User;
import org.openapitools.client.model.UserCredentials;
import org.openapitools.client.model.UserProfile;
import org.openapitools.client.model.UserType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private AuthenticationProvider provider;
    private UserType userType;
    private String userTypeId;
    private String nextLogin;
    private List<String> groupIds = new ArrayList<>();
    private Map<String, Object> passwordHashProperties;
    private String passwordHookImportType;

    public UserBuilder setPassword(char[] password) {
        this.password = Arrays.copyOf(password, password.length);
        return this;
    }

    public UserBuilder usePasswordHookForImport() {
        return usePasswordHookForImport("default");
    }

    public UserBuilder usePasswordHookForImport(String type) {
        passwordHookImportType = type;
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

    public UserBuilder setProvider(AuthenticationProvider provider) {
        this.provider = provider;
        return this;
    }

    @Override
    public UserBuilder setType(UserType userType) {
        this.userType = userType;
        return this;
    }

    @Override
    public UserBuilder setType(String userTypeId) {
        this.userTypeId = userTypeId;
        return this;
    }

    public UserBuilder setGroups(List<String> groupIds) {
        this.groupIds = groupIds;
        return this;
    }

    public UserBuilder addGroup(String groupId) {
        this.groupIds.add(groupId);
        return this;
    }

    @Override
    public UserBuilder setNextLogin(String nextLogin) {
        this.nextLogin = nextLogin;
        return this;
    }

    private CreateUserRequest build() {

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setProfile(new UserProfile());
        UserProfile userProfile = createUserRequest.getProfile();
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

        if (Strings.hasText(userTypeId)) {
            UserType userType = new UserType();
            userType.setId(userTypeId);
            createUserRequest.setType(userType);
        }
        else if (userType != null) {
            createUserRequest.setType(userType);
        }

        if (!Collections.isEmpty(groupIds)) {
            createUserRequest.setGroupIds(groupIds);
        }

        // security question
        if (Strings.hasText(securityQuestion)) {
            RecoveryQuestionCredential question = new RecoveryQuestionCredential();
            question.setQuestion(securityQuestion);
            question.setAnswer(securityQuestionAnswer);
            createCredentialsIfNeeded(createUserRequest).setRecoveryQuestion(question);
        }

        // authentication provider
        if (provider != null) {
            createCredentialsIfNeeded(createUserRequest).setProvider(provider);
        }

        // user password
        if (password != null && password.length > 0) {

            if (passwordHashProperties != null) {
                throw new IllegalArgumentException("Cannot specify both password and password hash, use one or the other.");
            }

            PasswordCredential passwordCredential = new PasswordCredential();
            passwordCredential.setValue(new String(password));
            createCredentialsIfNeeded(createUserRequest).setPassword(passwordCredential);
        }

        // direct password import
        if (passwordHashProperties != null) {
            PasswordCredential passwordCredential = new PasswordCredential();
            PasswordCredentialHash passwordCredentialHash = new PasswordCredentialHash();
            passwordCredentialHash.setAlgorithm((String) passwordHashProperties.get("algorithm"));
            passwordCredentialHash.setWorkFactor((Integer) passwordHashProperties.get("workFactor"));
            passwordCredentialHash.setSalt((String) passwordHashProperties.get("salt"));
            passwordCredentialHash.setValue((String) passwordHashProperties.get("value"));
            passwordCredentialHash.setSaltOrder((String) passwordHashProperties.get("saltOrder"));
            passwordCredential.setHash(passwordCredentialHash);
            createCredentialsIfNeeded(createUserRequest).setPassword(passwordCredential);
        }

        // password hook import
        if (passwordHookImportType != null) {
            PasswordCredential passwordCredential = new PasswordCredential();
            PasswordCredentialHook passwordCredentialHook = new PasswordCredentialHook();
            passwordCredentialHook.setType(passwordHookImportType);
            passwordCredential.setHook(passwordCredentialHook);
            createCredentialsIfNeeded(createUserRequest).setPassword(passwordCredential);
        }

        return createUserRequest;
    }

    private UserCredentials createCredentialsIfNeeded(CreateUserRequest createUserRequest) {
        if (createUserRequest.getCredentials() == null) {
            UserCredentials credentials = new UserCredentials();
            createUserRequest.setCredentials(credentials);
        }
        return createUserRequest.getCredentials();
    }

    public UserBuilder setBcryptPasswordHash(String value, String salt, int workFactor) {
        passwordHashProperties = new HashMap<>();
        passwordHashProperties.put("algorithm", "BCRYPT");
        passwordHashProperties.put("workFactor", workFactor);
        passwordHashProperties.put("salt", salt);
        passwordHashProperties.put("value", value);
        return this;
    }

    public UserBuilder setSha256PasswordHash(String value, String salt, String saltOrder) {
        return setShaPasswordHash("SHA-256", value, salt, saltOrder);
    }

    public UserBuilder setSha512PasswordHash(String value, String salt, String saltOrder) {
        return setShaPasswordHash("SHA-512", value, salt, saltOrder);
    }

    public UserBuilder setSha1PasswordHash(String value, String salt, String saltOrder) {
        return setShaPasswordHash("SHA-1", value, salt, saltOrder);
    }

    private UserBuilder setShaPasswordHash(String shaAlgorithm, String value, String salt, String saltOrder) {
        passwordHashProperties = new HashMap<>();
        passwordHashProperties.put("algorithm", shaAlgorithm);
        passwordHashProperties.put("salt", salt);
        passwordHashProperties.put("value", value);
        passwordHashProperties.put("saltOrder", saltOrder);
        return this;
    }

    @Override
    public User buildAndCreate(UserApi client) {
        return client.createUser(build(), active, provider != null, nextLogin);
    }
}