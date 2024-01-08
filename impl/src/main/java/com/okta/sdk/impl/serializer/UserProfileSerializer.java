/*
 * Copyright 2022-Present Okta, Inc.
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
package com.okta.sdk.impl.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.okta.commons.lang.Strings;
import com.okta.sdk.resource.model.UserProfile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class UserProfileSerializer extends StdSerializer<UserProfile> {

    private static final long serialVersionUID = -1159312306772670042L;

    public UserProfileSerializer() {
        this(null);
    }

    public UserProfileSerializer(Class<UserProfile> t) {
        super(t);
    }

    @Override
    public void serialize(UserProfile userProfile, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();

        // non-nullable fields

        if (Strings.hasText(userProfile.getEmail())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_EMAIL, userProfile.getEmail());
        }

        if (Strings.hasText(userProfile.getLogin())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_LOGIN, userProfile.getLogin());
        }

        if (Strings.hasText(userProfile.getFirstName())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_FIRST_NAME, userProfile.getFirstName());
        }

        if (Strings.hasText(userProfile.getLastName())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_LAST_NAME, userProfile.getLastName());
        }

        if (Strings.hasText(userProfile.getLocale())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_LOCALE, userProfile.getLocale());
        }

        // nullable fields

        if (Strings.hasText(userProfile.getCity())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_CITY, userProfile.getCity());
        }

        if (Strings.hasText(userProfile.getCountryCode())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_COUNTRY_CODE, userProfile.getCountryCode());
        }

        if (Strings.hasText(userProfile.getDepartment())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_DEPARTMENT, userProfile.getDepartment());
        }

        if (Strings.hasText(userProfile.getDisplayName())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_DISPLAY_NAME, userProfile.getDisplayName());
        }

        if (Strings.hasText(userProfile.getDivision())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_DIVISION, userProfile.getDivision());
        }

        if (Strings.hasText(userProfile.getCostCenter())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_COST_CENTER, userProfile.getCostCenter());
        }

        if (Strings.hasText(userProfile.getEmployeeNumber())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_EMPLOYEE_NUMBER, userProfile.getEmployeeNumber());
        }

        if (Strings.hasText(userProfile.getHonorificPrefix())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_HONORIFIC_PREFIX, userProfile.getHonorificPrefix());
        }

        if (Strings.hasText(userProfile.getHonorificSuffix())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_HONORIFIC_SUFFIX, userProfile.getHonorificSuffix());
        }

        if (Strings.hasText(userProfile.getManager())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_MANAGER, userProfile.getManager());
        }

        if (Strings.hasText(userProfile.getManagerId())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_MANAGER_ID, userProfile.getManagerId());
        }

        if (Strings.hasText(userProfile.getMiddleName())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_MIDDLE_NAME, userProfile.getMiddleName());
        }

        if (Strings.hasText(userProfile.getMobilePhone())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_MOBILE_PHONE, userProfile.getMobilePhone());
        }

        if (Strings.hasText(userProfile.getNickName())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_NICK_NAME, userProfile.getNickName());
        }

        if (Strings.hasText(userProfile.getOrganization())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_ORGANIZATION, userProfile.getOrganization());
        }

        if (Strings.hasText(userProfile.getPostalAddress())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_POSTAL_ADDRESS, userProfile.getPostalAddress());
        }

        if (Strings.hasText(userProfile.getPreferredLanguage())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_PREFERRED_LANGUAGE, userProfile.getPreferredLanguage());
        }

        if (Strings.hasText(userProfile.getPrimaryPhone())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_PRIMARY_PHONE, userProfile.getPrimaryPhone());
        }

        if (Strings.hasText(userProfile.getProfileUrl())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_PROFILE_URL, userProfile.getProfileUrl());
        }

        if (Strings.hasText(userProfile.getSecondEmail())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_SECOND_EMAIL, userProfile.getSecondEmail());
        }

        if (Strings.hasText(userProfile.getState())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_STATE, userProfile.getState());
        }

        if (Strings.hasText(userProfile.getStreetAddress())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_STREET_ADDRESS, userProfile.getStreetAddress());
        }

        if (Strings.hasText(userProfile.getTimezone())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_TIMEZONE, userProfile.getTimezone());
        }

        if (Strings.hasText(userProfile.getTitle())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_TITLE, userProfile.getTitle());
        }

        if (Strings.hasText(userProfile.getUserType())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_USER_TYPE, userProfile.getUserType());
        }

        if (Strings.hasText(userProfile.getZipCode())) {
            jgen.writeStringField(UserProfile.JSON_PROPERTY_ZIP_CODE, userProfile.getZipCode());
        }

        Map<String, Object> additionalProperties = userProfile.getAdditionalProperties();

        if (Objects.nonNull(additionalProperties) && !additionalProperties.isEmpty()) {
            for (Map.Entry<String, Object> entry : additionalProperties.entrySet()) {
                jgen.writeObjectField(entry.getKey(), entry.getValue());
            }
        }

        jgen.writeEndObject();
    }
}
