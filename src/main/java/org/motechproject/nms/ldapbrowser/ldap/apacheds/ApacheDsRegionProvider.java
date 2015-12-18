package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapReadException;
import org.motechproject.nms.ldapbrowser.region.RegionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ApacheDsRegionProvider implements RegionProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ApacheDsRegionProvider.class);

    private LdapConnectionPool ldapPool;
    private EntryHelper entryHelper;

    @Override
    public List<String> getStateNames() {
        LdapConnection connection = null;
        try {
            connection = ldapPool.getConnection();
            return entryHelper.stateNames(connection);
        } catch (LdapException | CursorException e) {
            throw new LdapReadException("Unable to read states from LDAP", e);
        } finally {
            ConnectionUtils.releaseConnection(ldapPool, connection);
        }
    }

    @Override
    public List<String> getDistrictNames(String stateName) {
        LdapConnection connection = null;
        try {
            connection = ldapPool.getConnection();
            return entryHelper.districtNames(connection, stateName);
        } catch (LdapException | CursorException e) {
            throw new LdapReadException("Unable to read districts from LDAP> State: " + stateName, e);
        } finally {
            ConnectionUtils.releaseConnection(ldapPool, connection);
        }
    }

    public void setLdapPool(LdapConnectionPool ldapPool) {
        this.ldapPool = ldapPool;
    }

    public void setEntryHelper(EntryHelper entryHelper) {
        this.entryHelper = entryHelper;
    }
}
