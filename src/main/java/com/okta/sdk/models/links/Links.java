package com.okta.sdk.models.links;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Collection;
import java.util.LinkedList;

@JsonDeserialize(using = JsonDeserializer.None.class) // use default
public class Links extends LinkedList<Link> implements LinksUnion {

    public Links() {
        super();
    }

    public Links(Collection<Link> list) {
        super(list);
    }
}
