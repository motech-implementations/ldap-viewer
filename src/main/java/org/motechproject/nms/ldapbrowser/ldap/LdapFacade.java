package org.motechproject.nms.ldapbrowser.ldap;

public interface LdapFacade {

    LdapUser findUser(String username);

    LdapUser findAndAuthenticate(String username, String password);
}
