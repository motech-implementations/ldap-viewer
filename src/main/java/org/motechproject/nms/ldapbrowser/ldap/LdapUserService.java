package org.motechproject.nms.ldapbrowser.ldap;

import java.util.List;

public interface LdapUserService {

    LdapUser authenticate(String username, String password);

    LdapUser getUser(String username);

    List<LdapUser> getUsers(UsersQuery query);

    LdapUser saveUser(LdapUser user);

    void deleteUser(String username);
}
