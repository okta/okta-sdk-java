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
package com.okta.sdk.resource.user;

import com.okta.commons.lang.Classes;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.UserApi;
import org.openapitools.client.model.AuthenticationProvider;
import org.openapitools.client.model.User;
import org.openapitools.client.model.UserNextLogin;
import org.openapitools.client.model.UserType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface UserBuilder {

    static UserBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.DefaultUserBuilder");
    }

    UserBuilder setPassword(char[] password);

    UserBuilder usePasswordHookForImport();

    UserBuilder usePasswordHookForImport(String type);

    UserBuilder setSecurityQuestion(String question);

    UserBuilder setSecurityQuestionAnswer(String answer);

    UserBuilder setEmail(String email);

    UserBuilder setFirstName(String firstName);

    UserBuilder setLastName(String lastName);

    UserBuilder setMiddleName(String middleName);

    UserBuilder setHonorificPrefix(String honorificPrefix);

    UserBuilder setHonorificSuffix(String honorificSuffix);

    UserBuilder setTitle(String title);

    UserBuilder setDisplayName(String displayName);

    UserBuilder setNickName(String nickName);

    UserBuilder setProfileUrl(String profileUrl);

    UserBuilder setPrimaryPhone(String primaryPhone);

    UserBuilder setStreetAddress(String streetAddress);

    UserBuilder setCity(String city);

    UserBuilder setState(String state);

    UserBuilder setZipCode(String zipCode);

    UserBuilder setCountryCode(String countryCode);

    UserBuilder setPostalAddress(String postalAddress);

    UserBuilder setPreferredLanguage(String preferredLanguage);

    UserBuilder setLocale(String locale);

    UserBuilder setTimezone(String timezone);

    UserBuilder setEmployeeNumber(String employeeNumber);

    UserBuilder setCostCenter(String costCenter);

    UserBuilder setOrganization(String organization);

    UserBuilder setDivision(String division);

    UserBuilder setDepartment(String department);

    UserBuilder setManagerId(String managerId);

    UserBuilder setManager(String manager);

    UserBuilder setLogin(String login);

    UserBuilder setMobilePhone(String mobilePhone);

    UserBuilder setSecondEmail(String secondEmail);

    UserBuilder setActive(Boolean active);

    UserBuilder setProvider(AuthenticationProvider provider);

    UserBuilder setType(UserType userType);

    UserBuilder setType(String userTypeId);

    default UserBuilder setGroups(String... groupIds) {
        return setGroups(Arrays.stream(groupIds).collect(Collectors.toList()));
    }

    UserBuilder setGroups(List<String> groupIds);

    UserBuilder addGroup(String groupId);

    UserBuilder setNextLogin(UserNextLogin nextLogin);

    UserBuilder setBcryptPasswordHash(String value, String salt, int workFactor);

    UserBuilder setSha256PasswordHash(String value, String salt, String saltOrder);

    UserBuilder setSha512PasswordHash(String value, String salt, String saltOrder);

    UserBuilder setSha1PasswordHash(String value, String salt, String saltOrder);

    UserBuilder setCustomProfileProperty(String key, Object value);

    User buildAndCreate(UserApi client) throws ApiException;
}
