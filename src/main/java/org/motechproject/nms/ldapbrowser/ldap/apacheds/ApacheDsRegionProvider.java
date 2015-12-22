package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.motechproject.nms.ldapbrowser.ldap.DistrictInfo;
import org.motechproject.nms.ldapbrowser.ldap.LdapFacade;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapReadException;
import org.motechproject.nms.ldapbrowser.region.RegionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.context.SecurityContextHolder;

import java.util.List;

public class ApacheDsRegionProvider implements RegionProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ApacheDsRegionProvider.class);

    private LdapFacade apacheDsFacade;
    private EntryHelper entryHelper;

    @Override
    public List<String> getStateNames() {
        LdapConnection connection = null;
        try {
            connection = apacheDsFacade.getConnectionForUser(getCurrentUsername(), getCurrentPassword());
            return entryHelper.stateNames(connection);
        } catch (LdapException | CursorException e) {
            throw new LdapReadException("Unable to read states from LDAP", e);
        } finally {
            ConnectionUtils.unbind(connection);
        }
    }

    @Override
    public List<String> getDistrictNames(String stateName) {
        LdapConnection connection = null;
        try {
            connection = apacheDsFacade.getConnectionForUser(getCurrentUsername(), getCurrentPassword());
            return entryHelper.districtNames(connection, stateName);
        } catch (LdapException | CursorException e) {
            throw new LdapReadException("Unable to read districts from LDAP> State: " + stateName, e);
        } finally {
            ConnectionUtils.unbind(connection);
        }
    }

    @Override
    public List<DistrictInfo> getAllAvailableDistricts() {
        LdapConnection connection = null;
        try {
            connection = apacheDsFacade.getConnectionForUser(getCurrentUsername(), getCurrentPassword());
            return entryHelper.allAvailableDistrictNames(connection);
        } catch (LdapException | CursorException e) {
            throw new LdapReadException("Unable to read districts", e);
        } finally {
            ConnectionUtils.unbind(connection);
        }
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String getCurrentPassword() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }

    public void setEntryHelper(EntryHelper entryHelper) {
        this.entryHelper = entryHelper;
    }

    public void setApacheDsFacade(LdapFacade apacheDsFacade) {
        this.apacheDsFacade = apacheDsFacade;
    }
}
