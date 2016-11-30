/*!
 * Copyright (c) 2015-2016, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.okta.sdk.models.apps;

import com.okta.sdk.framework.ApiObject;

public class AppSettings extends ApiObject {

    /**
     * The URL of the login page for this app
     */
    private String url;

    /**
     * Would you like Okta to add an integration for this app?
     */
    private Boolean requestIntegration;

    /**
     * The URL of the authenticating site for this app
     */
    private String authURL;

    /**
     * CSS selector for the username field in the login form
     */
    private String usernameField;

    /**
     * CSS selector for the password field in the login form
     */
    private String passwordField;

    /**
     * CSS selector for the login button in the login form
     */
    private String buttonField;

    /**
     * CSS selector for the extra field in the form
     */
    private String extraFieldSelector;

    /**
     * Value for extra field form field
     */
    private String extraFieldValue;

    /**
     * Name of the optional parameter in the login form
     */
    private String optionalField1;

    /**
     * Name of the optional value in the login form
     */
    private String optionalField1Value;

    /**
     * Name of the optional parameter in the login form
     */
    private String optionalField2;

    /**
     * Name of the optional value in the login form
     */
    private String optionalField2Value;

    /**
     * Name of the optional parameter in the login form
     */
    private String optionalField3;

    /**
     * Name of the optional value in the login form
     */
    private String optionalField3Value;

    /**
     * Gets url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Sets url
     */
    public void setUrl(String val) {
        this.url = val;
    }

    /**
     * Gets requestIntegration
     */
    public Boolean getRequestIntegration() {
        return this.requestIntegration;
    }

    /**
     * Sets requestIntegration
     */
    public void setRequestIntegration(Boolean val) {
        this.requestIntegration = val;
    }

    /**
     * Gets authURL
     */
    public String getAuthURL() {
        return this.authURL;
    }

    /**
     * Sets authURL
     */
    public void setAuthURL(String val) {
        this.authURL = val;
    }

    /**
     * Gets usernameField
     */
    public String getUsernameField() {
        return this.usernameField;
    }

    /**
     * Sets usernameField
     */
    public void setUsernameField(String val) {
        this.usernameField = val;
    }

    /**
     * Gets passwordField
     */
    public String getPasswordField() {
        return this.passwordField;
    }

    /**
     * Sets passwordField
     */
    public void setPasswordField(String val) {
        this.passwordField = val;
    }

    /**
     * Gets buttonField
     */
    public String getButtonField() {
        return this.buttonField;
    }

    /**
     * Sets buttonField
     */
    public void setButtonField(String val) {
        this.buttonField = val;
    }

    /**
     * Gets extraFieldSelector
     */
    public String getExtraFieldSelector() {
        return this.extraFieldSelector;
    }

    /**
     * Sets extraFieldSelector
     */
    public void setExtraFieldSelector(String val) {
        this.extraFieldSelector = val;
    }

    /**
     * Gets extraFieldValue
     */
    public String getExtraFieldValue() {
        return this.extraFieldValue;
    }

    /**
     * Sets extraFieldValue
     */
    public void setExtraFieldValue(String val) {
        this.extraFieldValue = val;
    }

    /**
     * Gets optionalField1
     */
    public String getOptionalField1() {
        return this.optionalField1;
    }

    /**
     * Sets optionalField1
     */
    public void setOptionalField1(String val) {
        this.optionalField1 = val;
    }

    /**
     * Gets optionalField1Value
     */
    public String getOptionalField1Value() {
        return this.optionalField1Value;
    }

    /**
     * Sets optionalField1Value
     */
    public void setOptionalField1Value(String val) {
        this.optionalField1Value = val;
    }

    /**
     * Gets optionalField2
     */
    public String getOptionalField2() {
        return this.optionalField2;
    }

    /**
     * Sets optionalField2
     */
    public void setOptionalField2(String val) {
        this.optionalField2 = val;
    }

    /**
     * Gets optionalField2Value
     */
    public String getOptionalField2Value() {
        return this.optionalField2Value;
    }

    /**
     * Sets optionalField2Value
     */
    public void setOptionalField2Value(String val) {
        this.optionalField2Value = val;
    }

    /**
     * Gets optionalField3
     */
    public String getOptionalField3() {
        return this.optionalField3;
    }

    /**
     * Sets optionalField3
     */
    public void setOptionalField3(String val) {
        this.optionalField3 = val;
    }

    /**
     * Gets optionalField3Value
     */
    public String getOptionalField3Value() {
        return this.optionalField3Value;
    }

    /**
     * Sets optionalField3Value
     */
    public void setOptionalField3Value(String val) {
        this.optionalField3Value = val;
    }
}