package org.motechproject.nms.ldapbrowser.ldap;

import java.util.List;

public interface LdapUserService {

    LdapUser getUser(String username);

    List<LdapUser> getUsers(UsersQuery query);

    long countUsers(UsersQuery query);

    LdapUser saveUser(LdapUser user);

    void deleteUser(String username);
}
