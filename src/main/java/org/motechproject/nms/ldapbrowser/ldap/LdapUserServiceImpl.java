package org.motechproject.nms.ldapbrowser.ldap;

import org.motechproject.nms.ldapbrowser.region.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class LdapUserServiceImpl implements LdapUserService {

    private static final Logger LOG = LoggerFactory.getLogger(LdapUserServiceImpl.class);

    @Inject
    private RegionService regionService;

    @Inject
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
    public List<LdapUser> getUsers(UsersQuery query, String currentUsername) {
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
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String getCurrentPassword() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }
}
