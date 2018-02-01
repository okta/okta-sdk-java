/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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
package com.okta.sdk.impl.http;

/**
 * Default implementation of {@link Link}.
 */
public class DefaultLink implements Link {

    private final String relationType;
    private final String href;

    public DefaultLink(String relationType, String href) {
        this.relationType = relationType;
        this.href = href;
    }

    @Override
    public String getRelationType() {
        return relationType;
    }

    @Override
    public String getHref() {
        return href;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultLink that = (DefaultLink) o;

        if (relationType != null ? !relationType.equals(that.relationType) : that.relationType != null) return false;
        return href != null ? href.equals(that.href) : that.href == null;
    }

    @Override
    public int hashCode() {
        int result = relationType != null ? relationType.hashCode() : 0;
        result = 31 * result + (href != null ? href.hashCode() : 0);
        return result;
    }
}
