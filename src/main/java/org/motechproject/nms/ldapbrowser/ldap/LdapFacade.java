package org.motechproject.nms.ldapbrowser.ldap;

public interface LdapFacade {

    LdapUser findUser(String username);

    LdapUser findAndAuthenticate(String username, String password);

    void addLdapUserEntry(LdapUser user, String creatorUsername, String creatorPassword);
}
