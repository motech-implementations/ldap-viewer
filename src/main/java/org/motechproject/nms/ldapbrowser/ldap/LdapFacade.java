package org.motechproject.nms.ldapbrowser.ldap;

import org.apache.directory.ldap.client.api.LdapNetworkConnection;

import java.util.List;

public interface LdapFacade {

    LdapUser findUser(String username);

    LdapUser findAndAuthenticate(String username, String password);

    void addLdapUserEntry(LdapUser user, String creatorUsername, String creatorPassword);

    List<LdapUser> getUsers(String adminUsername, String adminPassword);

    void deleteUser(String username, String adminUsername, String adminPassword);

    LdapNetworkConnection getConnectionForUser(String username, String password);
}
