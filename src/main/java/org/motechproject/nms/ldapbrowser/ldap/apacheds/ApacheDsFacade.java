package org.motechproject.nms.ldapbrowser.ldap.apacheds;


import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.motechproject.nms.ldapbrowser.ldap.LdapFacade;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.io.IOException;

import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.CN;
import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.DC;
import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.OBJECT_CLASS;
import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.OU;

public class ApacheDsFacade implements LdapFacade {

    private static final Logger LOG = LoggerFactory.getLogger(ApacheDsFacade.class);

    @Value("${ldap.host}")
    private String ldapHost;

    @Value("${ldap.port}")
    private int ldapPort;

    @Value("${ldap.useSsl}")
    private boolean ldapUseSsl;

    @Value("${ldap.ou.roles")
    private String rolesOu;

    @Value("${ldap.dc")
    private String dc;

    @Value("$ldap.userClass")
    private String userClass;

    @Inject
    private LdapConnectionPool adminConnectionPool;

    @Override
    public LdapUser findUser(String username) {
        LdapConnection connection;

        try {
            connection = adminConnectionPool.getConnection();
        } catch (LdapException e) {
            throw new LdapAuthException("Unable to bind admin connection", e);
        }

        try {
            String baseDn = String.format("%s=%s, %s=%s", OU, rolesOu, DC, dc);
            String filter = String.format("(%s=%s)", OBJECT_CLASS, userClass);

            EntryCursor cursor = connection.search(baseDn, filter, SearchScope.SUBTREE);

            while (cursor.next()) {
                Entry entry = cursor.get();
                Attribute usernameAttr = entry.get(CN);
                String ldapUserName = usernameAttr.getString();
                if (username.equals(ldapUserName)) {
                    // TODO: Multi state
                    return EntryUtil.buildUser(entry);
                }
            }

            return null;
        } catch (LdapException | CursorException e) {
            throw new LdapAuthException("Unable to search for users in LDAP", e);
        } finally {
            unbind(connection);
        }
    }

    @Override
    public LdapUser findAndAuthenticate(String username, String password) {
        ApacheDsUser user = (ApacheDsUser) findUser(username);
        if (user != null && bindUnbindUser(user, password)) {
            // authenticated
            return user;
        } else {
            // can't auth or doesn't exist
            return null;
        }
    }

    private boolean bindUnbindUser(ApacheDsUser user, String password) {
        LOG.debug("Trying to bind/unbind user {}", user.getUsername());
        try (LdapConnection connection = new LdapNetworkConnection(ldapHost, ldapPort, ldapUseSsl)) {
            connection.bind(user.getDn(), password);
            connection.unBind();
            LOG.debug("Successfully authenticated user: {}", user.getUsername());
            return true;
        } catch (IOException | LdapException e) {
            LOG.warn("Unable to authenticate user", e);
            return false;
        }
    }

    private void unbind(LdapConnection connection) {
        if (connection != null && connection.isAuthenticated()) {
            try {
                connection.unBind();
            } catch (LdapException e) {
                LOG.error("Error while unbinding the connection", e);
            }
        }
    }
}
