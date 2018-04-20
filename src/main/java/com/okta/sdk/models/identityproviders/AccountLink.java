package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

import java.util.List;

public class AccountLink extends ApiObject {
    public static class Filter extends ApiObject {
        public static class Groups extends ApiObject {
            private List<String> include;

            public List<String> getInclude() {
                return include;
            }

            public void setInclude(List<String> include) {
                this.include = include;
            }
        }

        private Groups groups;

        public Groups getGroups() {
            return groups;
        }

        public void setGroups(Groups groups) {
            this.groups = groups;
        }
    }

    private Filter filter;
    private String action;

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
