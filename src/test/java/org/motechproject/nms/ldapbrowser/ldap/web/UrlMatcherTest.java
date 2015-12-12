package org.motechproject.nms.ldapbrowser.ldap.web;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UrlMatcherTest {

    private UrlMatcher urlMatcher = new UrlMatcher();

    @Test
    public void shouldMatchNewUserReq() {
        assertTrue(urlMatcher.isNewUserReq("/nms-users/ldap/user"));

        assertFalse(urlMatcher.isNewUserReq("/nms-users//one/two"));
        assertFalse(urlMatcher.isNewUserReq("/nms-users/"));
    }

    @Test
    public void shouldMatchEditUserReq() {
        assertTrue(urlMatcher.isEditUserReq("/nms-users/ldap/user/test"));
        assertTrue(urlMatcher.isEditUserReq("/nms-users/ldap/user/somebody"));

        assertFalse(urlMatcher.isEditUserReq("/nms-users/ldap/user"));
        assertFalse(urlMatcher.isEditUserReq("/nms-users/plugin/nms-users/ldap/user"));
        assertFalse(urlMatcher.isEditUserReq("/nms-users/ldap/user/test/test"));
        assertFalse(urlMatcher.isEditUserReq("/nms-users/plugin/nms-users/ldap/user/test/test"));
        assertFalse(urlMatcher.isEditUserReq("/nms-users/"));
        assertFalse(urlMatcher.isEditUserReq("/nms-users//one/two/three"));
    }

    @Test
    public void shouldMatchDeleteUserReq() {
        assertTrue(urlMatcher.isDeleteUserReq("/nms-users/ldap/user/test/delete"));
        assertTrue(urlMatcher.isDeleteUserReq("/nms-users/ldap/user/somebody/delete"));

        assertFalse(urlMatcher.isDeleteUserReq("/nms-users/ldap/user/test/delete/test"));
        assertFalse(urlMatcher.isDeleteUserReq("/nms-users/ldap/user/test/xdelete"));
        assertFalse(urlMatcher.isDeleteUserReq("/nms-users/ldap/user/delete"));
        assertFalse(urlMatcher.isDeleteUserReq("/nms-users/ldap/user/test"));
        assertFalse(urlMatcher.isDeleteUserReq("/nms-users/ldap/user"));
        assertFalse(urlMatcher.isDeleteUserReq("/nms-users/ldap/user/test/test"));
        assertFalse(urlMatcher.isDeleteUserReq("/nms-users//"));
        assertFalse(urlMatcher.isDeleteUserReq("/nms-users/"));
        assertFalse(urlMatcher.isDeleteUserReq("/nms-users//one/two/three"));
    }

    @Test
    public void shouldMatchUserTableReq() {
        assertTrue(urlMatcher.isUserListReq("/nms-users/ldap"));

        assertFalse(urlMatcher.isUserListReq("/nms-users/ldap/user"));
        assertFalse(urlMatcher.isUserListReq("/nms-users/ldap/delete"));
    }

    @Test
    public void shouldExtractUserForEdit() {
        assertEquals("test", urlMatcher.extractUsernameForEdit("/nms-users/ldap/user/test"));
    }

    @Test
    public void shouldExtractUserForDelete() {
        assertEquals("test", urlMatcher.extractUsernameForDelete("/nms-users/ldap/user/test/delete"));
    }
}
