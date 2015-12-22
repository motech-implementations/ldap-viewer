package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapConnectionException;
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

    public static LdapNetworkConnection getConnectionAndBindUser(String ldapHost, int ldapPort, boolean useSsl, Dn userDn, String password) {
        LdapNetworkConnection connection = new LdapNetworkConnection(ldapHost, ldapPort, useSsl);
        try {
            connection.bind(userDn, password);
        } catch (LdapException e) {
            throw new LdapConnectionException("Unable to establish connection to " + ldapHost + ":" + ldapPort + ", using provided user credentials.", e);
        }

        return connection;
    }


    public static void unbind(LdapConnection connection) {
        if (connection != null && connection.isAuthenticated()) {
            try {
                connection.unBind();
            } catch (LdapException e) {
                LOG.error("Error while unbinding the connection", e);
            }
        }
    }
}
