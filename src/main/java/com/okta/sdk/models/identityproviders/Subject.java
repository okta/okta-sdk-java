package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Subject extends ApiObject {
    public static final String MATCH_TYPE_USERNAME = "USERNAME";
    public static final String SAML_SUBJECT_NAME = "idpuser.subjectNameId";

    private String matchType;
    private String filter;

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public void setUsernameTemplate(String template)
    {
        LinkedHashMap<String, Object> templateHashMap = new LinkedHashMap<String, Object>();
        templateHashMap.put("template", template);
        this.getUnmapped().put("userNameTemplate", templateHashMap);
    }

    public void setFormat(String format)
    {
        List<String> formatArray = new ArrayList<String>();
        formatArray.add(format);
        this.getUnmapped().put("format", formatArray);
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
