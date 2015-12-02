package org.motechproject.nms.ldapbrowser.ldap;

import java.util.List;

public interface LdapUserService {

    User getUser(String username);

    List<User> getUsers(UsersQuery query);

    long countUsers(UsersQuery query);

    User saveUser(User user);

    void deleteUser(String username);
}
