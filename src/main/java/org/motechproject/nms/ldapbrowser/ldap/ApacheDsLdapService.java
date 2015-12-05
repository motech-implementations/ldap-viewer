package org.motechproject.nms.ldapbrowser.ldap;

import org.motechproject.nms.ldapbrowser.ldap.ex.LdapReadException;
import org.motechproject.nms.ldapbrowser.region.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

// @Service
public class ApacheDsLdapService extends DummyLdapService implements LdapUserService {

    private static final Logger LOG = LoggerFactory.getLogger(ApacheDsLdapService.class);

    @Inject
    private RegionService regionService;

    @Inject
    private LdapFacade ldapFacade;

    @Override
    public LdapUser authenticate(String username, String password) {
        LdapUser user = ldapFacade.findAndAuthenticate(username, password);

        validateState(user.getState());
        validateDistrict(user.getState(), user.getDistrict());

        return user;
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
