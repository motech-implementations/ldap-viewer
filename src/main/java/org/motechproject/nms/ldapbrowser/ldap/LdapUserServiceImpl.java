package org.motechproject.nms.ldapbrowser.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LdapUserServiceImpl implements LdapUserService {

    private static final Logger LOG = LoggerFactory.getLogger(LdapUserServiceImpl.class);

    private LdapFacade ldapFacade;

    @Override
    public LdapUser authenticate(String username, String password) {
        return ldapFacade.findAndAuthenticate(username, password);
    }

    @Override
    public LdapUser getUser(String username) {
        return ldapFacade.findUser(username);
    }

    @Override
    public List<LdapUser> getUsers(UsersQuery query) {
        return ldapFacade.getUsers(getCurrentUsername(), getCurrentPassword());
    }

    @Override
    public LdapUser saveUser(LdapUser user) {
        // TODO: implement
        return user;
    }

    @Override
    public void deleteUser(String username) {
        // TODO: implement
    }

    private String getCurrentUsername() {
        // TODO: plugin
        return "";
    }

    private String getCurrentPassword() {
        // TODO: plugin
        return "";
    }

    public void setLdapFacade(LdapFacade ldapFacade) {
        this.ldapFacade = ldapFacade;
    }
}
