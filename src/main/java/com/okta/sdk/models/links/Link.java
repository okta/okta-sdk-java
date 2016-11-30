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

package com.okta.sdk.models.links;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.xml.bind.annotation.XmlAnyAttribute;
import java.util.List;
import java.util.Map;

@JsonDeserialize(using = JsonDeserializer.None.class) // use default
public class Link implements LinksUnion {

    public static final String SELF = "self";
    public static final String NEXT = "next";
    public static final String PREV = "prev";

    private String rel;
    private String href;
    private String method;
    private String name;
    private String title;
    private String type;
    private Map<String, List<String>> hints;

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAnyAttribute
    public Map<String, List<String>> getHints() {
        return hints;
    }

    public void setHints(Map<String, List<String>> hints) {
        this.hints = hints;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
