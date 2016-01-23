package org.motechproject.nms.ldapbrowser.ldap;

import java.util.List;

public interface LdapUserService {

    boolean isAdminUser(String username);

    LdapUser getLoggedUser();

    LdapUser getUser(String username);

    List<LdapUser> getUsers(UsersQuery query);

    void saveUser(LdapUser user);

    void deleteUser(String username);
}
