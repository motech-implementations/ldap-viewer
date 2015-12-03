package org.motechproject.nms.ldapbrowser.ldap;

import java.util.List;

public interface LdapUserService {

    LdapUser getUser(String username);

    List<LdapUser> getUsers(UsersQuery query, String currentUsername);

    long countUsers(String currentUsername);

    LdapUser saveUser(LdapUser user);

    void deleteUser(String username);
}
