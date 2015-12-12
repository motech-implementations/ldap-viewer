package org.motechproject.nms.ldapbrowser.ldap.web;

import org.springframework.util.AntPathMatcher;

public class UrlMatcher {

    private static final String USER_PATH = "ldap/user";
    private static final String USER_EDIT_PATH = "ldap/user/*";
    private static final String USER_DELETE_PATH = "ldap/user/*/delete";

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public boolean isNewUserReq(String path) {
        return antPathMatcher.match(USER_PATH, path);
    }

    public boolean isEditUserReq(String path) {
        return antPathMatcher.match(USER_EDIT_PATH, path);
    }

    public boolean isDeleteUserReq(String path) {
        return antPathMatcher.match(USER_DELETE_PATH, path);
    }

    public boolean isSaveUserReq(String path) {
        return antPathMatcher.match(USER_PATH, path);
    }

    public String extractUsernameForEdit(String path) {
        return antPathMatcher.extractPathWithinPattern(USER_EDIT_PATH, path);
    }

    public String extractUsernameForDelete(String path) {
        return antPathMatcher.extractPathWithinPattern(USER_DELETE_PATH, path);
    }
}
