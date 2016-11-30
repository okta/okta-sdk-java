package com.okta.sdk.models.identityproviders;

/**
 *
 */
public class IdpObject {
    public enum Type {
        OKTA,
        ACTIVE_DIRECTORY,
        LDAP,
        FEDERATION,
        SOCIAL
    }

    /**
     * unique key for the identity provider
     */
    private String id;

    /**
     * The type of identity provider
     */
    private Type type;

    /**
     * Gets the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the type
     */
    public void setType(Type type) {
        this.type = type;
    }

}
