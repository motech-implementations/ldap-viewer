package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapReadException;
import org.motechproject.nms.ldapbrowser.region.RegionProvider;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class ApacheDsRegionProvider implements RegionProvider {

    @Inject
    private LdapConnectionPool ldapPool;

    @Inject
    private EntryHelper entryHelper;

    @Override
    public List<String> getStateNames() {
        try (LdapConnection connection = ldapPool.getConnection()) {
            return entryHelper.stateNames(connection);
        } catch (LdapException | IOException | CursorException e) {
            throw new LdapReadException("Unable to read states from LDAP", e);
        }
    }

    @Override
    public List<String> getDistrictNames(String stateName) {
        try (LdapConnection connection = ldapPool.getConnection()) {
            return entryHelper.districtNames(connection, stateName);
        } catch (LdapException | IOException | CursorException e) {
            throw new LdapReadException("Unable to read districts from LDAP> State: " + stateName, e);
        }
    }
}
