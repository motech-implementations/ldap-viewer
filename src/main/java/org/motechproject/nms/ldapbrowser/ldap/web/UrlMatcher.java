package org.motechproject.nms.ldapbrowser.ldap.web;

import org.springframework.util.AntPathMatcher;

public class UrlMatcher {

    private static final String USERS_TABLE_PATH = "/**/ldap";
    private static final String USER_PATH = "/**/ldap/user";
    private static final String USER_EDIT_PATH = "/**/ldap/user/*";
    private static final String USER_DELETE_PATH = "/**/ldap/user/*/delete";

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public boolean isUserListReq(String path) {
        return antPathMatcher.match(USERS_TABLE_PATH, path);
    }

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
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public String extractUsernameForDelete(String path) {
        int end = path.length() - "/delete".length();
        int start = path.lastIndexOf('/', end - 1) + 1;
        return path.substring(start, end);
   }
}
