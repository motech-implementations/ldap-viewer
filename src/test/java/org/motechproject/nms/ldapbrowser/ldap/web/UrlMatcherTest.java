package org.motechproject.nms.ldapbrowser.ldap.web;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UrlMatcherTest {

    private UrlMatcher urlMatcher = new UrlMatcher();

    @Test
    public void shouldMatchNewUserReq() {
        assertTrue(urlMatcher.isNewUserReq("ldap/user"));

        assertFalse(urlMatcher.isNewUserReq("/one/two"));
        assertFalse(urlMatcher.isNewUserReq("/"));
        assertFalse(urlMatcher.isNewUserReq(""));
        assertFalse(urlMatcher.isNewUserReq("ldap/user/test"));
        assertFalse(urlMatcher.isNewUserReq("something/ldap/user"));
    }

    @Test
    public void shouldMatchEditUserReq() {
        assertTrue(urlMatcher.isEditUserReq("ldap/user/test"));
        assertTrue(urlMatcher.isEditUserReq("ldap/user/somebody"));

        assertFalse(urlMatcher.isEditUserReq("ldap/user"));
        assertFalse(urlMatcher.isEditUserReq("ldap/user/test/test"));
        assertFalse(urlMatcher.isEditUserReq("/"));
        assertFalse(urlMatcher.isEditUserReq(""));
        assertFalse(urlMatcher.isEditUserReq("/one/two/three"));
    }

    @Test
    public void shouldMatchDeleteUserReq() {
        assertTrue(urlMatcher.isDeleteUserReq("ldap/user/test/delete"));
        assertTrue(urlMatcher.isDeleteUserReq("ldap/user/somebody/delete"));

        assertFalse(urlMatcher.isDeleteUserReq("ldap/user/test/delete/test"));
        assertFalse(urlMatcher.isDeleteUserReq("ldap/user/test/xdelete"));
        assertFalse(urlMatcher.isDeleteUserReq("ldap/user/delete"));
        assertFalse(urlMatcher.isDeleteUserReq("ldap/user/test"));
        assertFalse(urlMatcher.isDeleteUserReq("ldap/user"));
        assertFalse(urlMatcher.isDeleteUserReq("ldap/user/test/test"));
        assertFalse(urlMatcher.isDeleteUserReq("/"));
        assertFalse(urlMatcher.isDeleteUserReq(""));
        assertFalse(urlMatcher.isDeleteUserReq("/one/two/three"));
    }

    @Test
    public void shouldExtractUserForEdit() {
        assertEquals("test", urlMatcher.extractUsernameForEdit("ldap/user/test"));
    }

    @Test
    public void shouldExtractUserForDelete() {
        assertEquals("test", urlMatcher.extractUsernameForDelete("ldap/user/test/delete"));
    }
}
