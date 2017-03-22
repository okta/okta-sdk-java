/*!
 * Copyright (c) 2015-2017, Okta, Inc. and/or its affiliates. All rights reserved.
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
     * The URL of the login page for this app.
     */
    private String url;

    /**
     * Question asking Okta to add an integration for this app.
     */
    private Boolean requestIntegration;

    /**
     * The URL of the authenticating site for this app.
     */
    private String authURL;

    /**
     * CSS selector for the username field in the login form.
     */
    private String usernameField;

    /**
     * CSS selector for the password field in the login form.
     */
    private String passwordField;

    /**
     * CSS selector for the login button in the login form.
     */
    private String buttonField;

    /**
     * CSS selector for the extra field in the form.
     */
    private String extraFieldSelector;

    /**
     * Value for extra field form field.
     */
    private String extraFieldValue;

    /**
     * Name of the optional parameter in the login form.
     */
    private String optionalField1;

    /**
     * Name of the optional value in the login form.
     */
    private String optionalField1Value;

    /**
     * Name of the optional parameter in the login form.
     */
    private String optionalField2;

    /**
     * Name of the optional value in the login form.
     */
    private String optionalField2Value;

    /**
     * Name of the optional parameter in the login form.
     */
    private String optionalField3;

    /**
     * Name of the optional value in the login form.
     */
    private String optionalField3Value;

    /**
     * Returns the url.
     * @return {@link String}
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Sets the url.
     * @param val {@link String}
     */
    public void setUrl(String val) {
        this.url = val;
    }

    /**
     * Returns the requestIntegration.
     * @return {@link Boolean}
     */
    public Boolean getRequestIntegration() {
        return this.requestIntegration;
    }

    /**
     * Sets the requestIntegration.
     * @param val {@link Boolean}
     */
    public void setRequestIntegration(Boolean val) {
        this.requestIntegration = val;
    }

    /**
     * Returns the authURL.
     * @return {@link String}
     */
    public String getAuthURL() {
        return this.authURL;
    }

    /**
     * Sets the authURL.
     * @param val {@link String}
     */
    public void setAuthURL(String val) {
        this.authURL = val;
    }

    /**
     * Returns the usernameField.
     * @return {@link String}
     */
    public String getUsernameField() {
        return this.usernameField;
    }

    /**
     * Sets the usernameField.
     * @param val {@link String}
     */
    public void setUsernameField(String val) {
        this.usernameField = val;
    }

    /**
     * Returns the passwordField.
     * @return {@link String}
     */
    public String getPasswordField() {
        return this.passwordField;
    }

    /**
     * Sets the passwordField.
     * @param val {@link String}
     */
    public void setPasswordField(String val) {
        this.passwordField = val;
    }

    /**
     * Returns the buttonField.
     * @return {@link String}
     */
    public String getButtonField() {
        return this.buttonField;
    }

    /**
     * Sets the buttonField.
     * @param val {@link String}
     */
    public void setButtonField(String val) {
        this.buttonField = val;
    }

    /**
     * Returns the extraFieldSelector.
     * @return {@link String}
     */
    public String getExtraFieldSelector() {
        return this.extraFieldSelector;
    }

    /**
     * Sets the extraFieldSelector.
     * @param val {@link String}
     */
    public void setExtraFieldSelector(String val) {
        this.extraFieldSelector = val;
    }

    /**
     * Returns the extraFieldValue.
     * @return {@link String}
     */
    public String getExtraFieldValue() {
        return this.extraFieldValue;
    }

    /**
     * Sets the extraFieldValue.
     * @param val {@link String}
     */
    public void setExtraFieldValue(String val) {
        this.extraFieldValue = val;
    }

    /**
     * Returns the optionalField1.
     * @return {@link String}
     */
    public String getOptionalField1() {
        return this.optionalField1;
    }

    /**
     * Sets the optionalField1.
     * @param val {@link String}
     */
    public void setOptionalField1(String val) {
        this.optionalField1 = val;
    }

    /**
     * Returns the optionalField1Value.
     * @return {@link String}
     */
    public String getOptionalField1Value() {
        return this.optionalField1Value;
    }

    /**
     * Sets the optionalField1Value.
     * @param val {@link String}
     */
    public void setOptionalField1Value(String val) {
        this.optionalField1Value = val;
    }

    /**
     * Returns the optionalField2.
     * @return {@link String}
     */
    public String getOptionalField2() {
        return this.optionalField2;
    }

    /**
     * Sets the optionalField2.
     * @param val {@link String}
     */
    public void setOptionalField2(String val) {
        this.optionalField2 = val;
    }

    /**
     * Returns the optionalField2Value.
     * @return {@link String}
     */
    public String getOptionalField2Value() {
        return this.optionalField2Value;
    }

    /**
     * Sets the optionalField2Value.
     * @param val {@link String}
     */
    public void setOptionalField2Value(String val) {
        this.optionalField2Value = val;
    }

    /**
     * Returns the optionalField3.
     * @return {@link String}
     */
    public String getOptionalField3() {
        return this.optionalField3;
    }

    /**
     * Sets the optionalField3.
     * @param val {@link String}
     */
    public void setOptionalField3(String val) {
        this.optionalField3 = val;
    }

    /**
     * Returns the optionalField3Value.
     * @return {@link String}
     */
    public String getOptionalField3Value() {
        return this.optionalField3Value;
    }

    /**
     * Sets the optionalField3Value.
     * @param val {@link String}
     */
    public void setOptionalField3Value(String val) {
        this.optionalField3Value = val;
    }
}