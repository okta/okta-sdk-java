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
package com.okta.sdk.impl.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.openapitools.client.model.UserProfile;

import java.io.IOException;
import java.util.Map;

public class UserProfileDeserializer extends StdDeserializer<UserProfile> {

    private static final long serialVersionUID = -6166716736969489408L;

    private final ObjectMapper mapper = new ObjectMapper();

    public UserProfileDeserializer() {
        this(null);
    }

    public UserProfileDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public UserProfile deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);

        Map<String, Object> profileMap = mapper.convertValue(node, new TypeReference<Map<String, Object>>(){});

        UserProfile userProfile = new UserProfile();

        for (Map.Entry<String, Object> entry : profileMap.entrySet()) {

            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());

            switch (key) {
                case UserProfile.JSON_PROPERTY_CITY:
                    userProfile.setCity(value);
                    break;

                case UserProfile.JSON_PROPERTY_COST_CENTER:
                    userProfile.setCostCenter(value);
                    break;

                case UserProfile.JSON_PROPERTY_COUNTRY_CODE:
                    userProfile.setCountryCode(value);
                    break;

                case UserProfile.JSON_PROPERTY_DEPARTMENT:
                    userProfile.setDepartment(value);
                    break;

                case UserProfile.JSON_PROPERTY_DISPLAY_NAME:
                    userProfile.setDisplayName(value);
                    break;

                case UserProfile.JSON_PROPERTY_DIVISION:
                    userProfile.setDivision(value);
                    break;

                case UserProfile.JSON_PROPERTY_EMAIL:
                    userProfile.setEmail(value);
                    break;

                case UserProfile.JSON_PROPERTY_EMPLOYEE_NUMBER:
                    userProfile.setEmployeeNumber(value);
                    break;

                case UserProfile.JSON_PROPERTY_FIRST_NAME:
                    userProfile.setFirstName(value);
                    break;

                case UserProfile.JSON_PROPERTY_HONORIFIC_PREFIX:
                    userProfile.setHonorificPrefix(value);
                    break;

                case UserProfile.JSON_PROPERTY_HONORIFIC_SUFFIX:
                    userProfile.setHonorificSuffix(value);
                    break;

                case UserProfile.JSON_PROPERTY_LAST_NAME:
                    userProfile.setLastName(value);
                    break;

                case UserProfile.JSON_PROPERTY_LOCALE:
                    userProfile.setLocale(value);
                    break;

                case UserProfile.JSON_PROPERTY_LOGIN:
                    userProfile.setLogin(value);
                    break;

                case UserProfile.JSON_PROPERTY_MANAGER:
                    userProfile.setManager(value);
                    break;

                case UserProfile.JSON_PROPERTY_MANAGER_ID:
                    userProfile.setManagerId(value);
                    break;

                case UserProfile.JSON_PROPERTY_MIDDLE_NAME:
                    userProfile.setMiddleName(value);
                    break;

                case UserProfile.JSON_PROPERTY_MOBILE_PHONE:
                    userProfile.setMobilePhone(value);
                    break;

                case UserProfile.JSON_PROPERTY_NICK_NAME:
                    userProfile.setNickName(value);
                    break;

                case UserProfile.JSON_PROPERTY_ORGANIZATION:
                    userProfile.setOrganization(value);
                    break;

                case UserProfile.JSON_PROPERTY_POSTAL_ADDRESS:
                    userProfile.setPostalAddress(value);
                    break;

                case UserProfile.JSON_PROPERTY_PREFERRED_LANGUAGE:
                    userProfile.setPreferredLanguage(value);
                    break;

                case UserProfile.JSON_PROPERTY_PRIMARY_PHONE:
                    userProfile.setPrimaryPhone(value);
                    break;

                case UserProfile.JSON_PROPERTY_PROFILE_URL:
                    userProfile.setProfileUrl(value);
                    break;

                case UserProfile.JSON_PROPERTY_SECOND_EMAIL:
                    userProfile.setSecondEmail(value);
                    break;

                case UserProfile.JSON_PROPERTY_STATE:
                    userProfile.setState(value);
                    break;

                case UserProfile.JSON_PROPERTY_STREET_ADDRESS:
                    userProfile.setStreetAddress(value);
                    break;

                case UserProfile.JSON_PROPERTY_TIMEZONE:
                    userProfile.setTimezone(value);
                    break;

                case UserProfile.JSON_PROPERTY_TITLE:
                    userProfile.setTitle(value);
                    break;

                case UserProfile.JSON_PROPERTY_USER_TYPE:
                    userProfile.setUserType(value);
                    break;

                case UserProfile.JSON_PROPERTY_ZIP_CODE:
                    userProfile.setZipCode(value);
                    break;

                default:
                    userProfile.getAdditionalProperties().put(key, value);
            }
        }

        return userProfile;
    }
}
