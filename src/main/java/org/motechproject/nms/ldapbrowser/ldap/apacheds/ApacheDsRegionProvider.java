package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapReadException;
import org.motechproject.nms.ldapbrowser.region.RegionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ApacheDsRegionProvider implements RegionProvider {

    @Autowired
    private LdapConnectionPool ldapPool;

    @Autowired
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
