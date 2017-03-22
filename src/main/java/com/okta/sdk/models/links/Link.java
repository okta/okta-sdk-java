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

package com.okta.sdk.models.links;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.xml.bind.annotation.XmlAnyAttribute;
import java.util.List;
import java.util.Map;

@JsonDeserialize(using = JsonDeserializer.None.class) // use default
public class Link implements LinksUnion {

    /**
     * Helper variables
     */
    public static final String SELF = "self";
    public static final String NEXT = "next";
    public static final String PREV = "prev";

    /**
     * The relation of the link.
     */
    private String rel;
    /**
     * The HREF or source of the link.
     */
    private String href;

    /**
     * The method of the link.
     */
    private String method;

    /**
     * The name of the link.
     */
    private String name;

    /**
     * The title of the link.
     */
    private String title;

    /**
     * The type of the link.
     */
    private String type;

    /**
     * The hints nested inside of the link.
     */
    private Map<String, List<String>> hints;

    /**
     * Returns the rel.
     * @return {@link String}
     */
    public String getRel() {
        return rel;
    }

    /**
     * Sets the rel.
     * @param val {@link String}
     */
    public void setRel(String val) {
        this.rel = val;
    }

    /**
     * Returns the href.
     * @return {@link String}
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the href.
     * @param val {@link String}
     */
    public void setHref(String val) {
        this.href = val;
    }

    /**
     * Returns the method.
     * @return {@link String}
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the method.
     * @param val {@link String}
     */
    public void setMethod(String val) {
        this.method = val;
    }

    /**
     * Returns the name.
     * @return {@link String}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param val {@link String}
     */
    public void setName(String val) {
        this.name = val;
    }

    /**
     * Returns the type.
     * @return {@link String}
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     * @param val {@link String}
     */
    public void setType(String val) {
        this.type = val;
    }

    /**
     * Returns the hints object.
     * @return {@link Map}
     */
    @XmlAnyAttribute
    public Map<String, List<String>> getHints() {
        return hints;
    }

    /**
     * Sets the hints object.
     * @param val {@link Map}
     */
    public void setHints(Map<String, List<String>> val) {
        this.hints = val;
    }

    /**
     * Returns the title.
     * @return {@link String}
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     * @param val {@link String}
     */
    public void setTitle(String val) {
        this.title = val;
    }
}
