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
