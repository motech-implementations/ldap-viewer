package org.motechproject.nms.ldapbrowser.ldap.apacheds;


import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.AddRequest;
import org.apache.directory.api.ldap.model.message.AddRequestImpl;
import org.apache.directory.api.ldap.model.message.AddResponse;
import org.apache.directory.api.ldap.model.message.ResultCodeEnum;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.motechproject.nms.ldapbrowser.ldap.LdapFacade;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapAuthException;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapReadException;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapWriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ApacheDsFacade implements LdapFacade {

    private static final Logger LOG = LoggerFactory.getLogger(ApacheDsFacade.class);

    private String ldapHost;
    private int ldapPort;
    private boolean ldapUseSsl;
    private LdapConnectionPool adminConnectionPool;
    private EntryHelper entryHelper;

    @Override
    public LdapUser findUser(String username) {
        LdapConnection connection;

        try {
            connection = adminConnectionPool.getConnection();
        } catch (LdapException e) {
            throw new LdapAuthException("Unable to bind admin connection", e);
    }

        try {
            EntryCursor cursor = entryHelper.searchForAdmins(connection);

            while (cursor.next()) {
                Entry entry = cursor.get();
                String ldapUserName = entryHelper.getUsername(entry);
                if (username.equals(ldapUserName)) {
                    return entryHelper.buildUser(entry, entryHelper.getAllRolesCursor(connection));
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

    @Override
    public void addLdapUserEntry(LdapUser user, String creatorUsername, String creatorPassword) {
        ApacheDsUser creatorUser = getCurrentUser(creatorUsername);

        try (LdapConnection connection = new LdapNetworkConnection(ldapHost, ldapPort, ldapUseSsl)) {
            connection.bind(creatorUser.getDn(), creatorPassword);

            Entry userEntry = entryHelper.userToEntry(user);
            AddRequest addRequest = new AddRequestImpl();
            addRequest.setEntry(userEntry);

            AddResponse addResponse = connection.add(addRequest);

            ResultCodeEnum resultCode = addResponse.getLdapResult().getResultCode();
            if (resultCode != ResultCodeEnum.SUCCESS) {
                throw new LdapWriteException(String.format("User %s failed to add user %s with dn %s. Error code: %s",
                        creatorUsername, user.getUsername(), userEntry.getDn(), resultCode));
            }
        } catch (IOException | LdapException e) {
            throw new LdapWriteException(String.format("User %s failed to add user %s",
                    creatorUsername, user.getUsername()), e);
        }
    }

    @Override
    public List<LdapUser> getUsers(String adminUsername, String adminPassword) {
        ApacheDsUser currentUser = getCurrentUser(adminUsername);

        try (LdapConnection connection = new LdapNetworkConnection(ldapHost, ldapPort, ldapUseSsl)) {
            connection.bind(currentUser.getDn(), adminPassword);

            return entryHelper.getAllUsers(connection);
        } catch (IOException | LdapException | CursorException e) {
            throw new LdapReadException("User " + adminPassword + " failed to retrieve users", e);
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

    private ApacheDsUser getCurrentUser(String username) {
        ApacheDsUser currentUser = (ApacheDsUser) findUser(username);
        if (currentUser == null) {
            throw new LdapAuthException("Unable to find user: " + username);
        }
        return currentUser;
    }

    public void setLdapHost(String ldapHost) {
        this.ldapHost = ldapHost;
    }

    public void setLdapPort(int ldapPort) {
        this.ldapPort = ldapPort;
    }

    public void setLdapUseSsl(boolean ldapUseSsl) {
        this.ldapUseSsl = ldapUseSsl;
    }

    public void setAdminConnectionPool(LdapConnectionPool adminConnectionPool) {
        this.adminConnectionPool = adminConnectionPool;
    }

    public void setEntryHelper(EntryHelper entryHelper) {
        this.entryHelper = entryHelper;
    }
}
