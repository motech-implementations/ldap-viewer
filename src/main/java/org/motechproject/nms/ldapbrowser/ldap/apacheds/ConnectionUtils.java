package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionUtils.class);

    public static void releaseConnection(LdapConnectionPool pool, LdapConnection connection) {
        try {
            pool.releaseConnection(connection);
        } catch (LdapException e) {
            LOG.error("Failed to release connection back to the connection pool. This might cause problem during next connection retrieval.");
        }
    }
}
