package org.motechproject.nms.ldapbrowser.ldap;

import org.motechproject.nms.ldapbrowser.ldap.ex.LdapReadException;
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
        LdapUser user = ldapFacade.findAndAuthenticate(username, password);

    /*    if (user != null) {
            validateState(user.getState());
            validateDistrict(user.getState(), user.getDistrict());
        }*/

        return user;
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

    private void validateState(String stateName) {
        if (!regionService.stateExists(stateName)) {
            throw new LdapReadException("Unknown state: " + stateName);
        }
    }

    private void validateDistrict(String stateName, String districtName) {
        if (!regionService.districtExists(stateName, districtName)) {
            throw new LdapReadException("Unknown district: " + districtName + " in state " + stateName);
        }
    }
}
