package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import org.apache.commons.io.IOUtils;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.AddRequest;
import org.apache.directory.api.ldap.model.message.AddRequestImpl;
import org.apache.directory.api.ldap.model.message.AddResponse;
import org.apache.directory.api.ldap.model.message.ModifyRequest;
import org.apache.directory.api.ldap.model.message.ResultCodeEnum;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.motechproject.nms.ldapbrowser.ldap.LdapFacade;
import org.motechproject.nms.ldapbrowser.ldap.LdapRole;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.RoleType;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapAuthException;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapReadException;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapWriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
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
            ConnectionUtils.releaseConnection(adminConnectionPool, connection);
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
        LdapConnection connection = null;

        try {
            connection = ConnectionUtils.getConnectionAndBindUser(ldapHost, ldapPort, ldapUseSsl, creatorUser.getDn(), creatorPassword);

            Entry userEntry = entryHelper.userToEntry(user);
            ApacheDsUser userPriorUpdate = (ApacheDsUser) findUser(user.getUsername());

            ResultCodeEnum resultCode = null;

            if (userPriorUpdate == null) {
                // Adds user entry
                AddRequest addRequest = new AddRequestImpl();
                addRequest.setEntry(userEntry);
                AddResponse addResponse = connection.add(addRequest);
                resultCode = addResponse.getLdapResult().getResultCode();
            } else {
                // Updates user entry
                List<Modification> modifications = entryHelper.prepareModificationRequests(userEntry, userPriorUpdate);

                for (Modification modification : modifications) {
                    connection.modify(userPriorUpdate.getDn(), modification);
                }
            }

            // Adds roles
            Iterator<LdapRole> it = user.getRoles().iterator();

            if (userPriorUpdate != null) {
                // UPDATE case
                List<LdapRole> userRolesPriorUpdate = userPriorUpdate.getRoles();
                while (it.hasNext()) {
                    LdapRole role = it.next();
                    if (userRolesPriorUpdate.contains(role)) {
                        //role present in both lists
                        it.remove();
                        userRolesPriorUpdate.remove(role);
                    }
                }

                for (LdapRole role : userRolesPriorUpdate) {
                    // Roles present in old user object but not present in current user object must be removed
                    String roleDn = entryHelper.buildDn(role.getState(), role.getDistrict(), role.isAdmin() ? RoleType.USER_ADMIN : RoleType.VIEWER);

                    Modification modification = new DefaultModification(ModificationOperation.REMOVE_ATTRIBUTE,
                            entryHelper.getAttributeName(role.isAdmin() ? RoleType.USER_ADMIN : RoleType.VIEWER),
                            entryHelper.buildUserDn(user.getUsername(), user.getState(), user.getDistrict())
                    );
                    connection.modify(roleDn, modification);
                }
            }

            // Roles remaining in lists must trigger udpates
            for (LdapRole role : user.getRoles()) {
                // Roles present in new user object but not present in previous user object must be added
                String roleDn = entryHelper.buildDn(role.getState(), role.getDistrict(), role.isAdmin() ? RoleType.USER_ADMIN : RoleType.VIEWER);

                Modification modification = new DefaultModification(ModificationOperation.ADD_ATTRIBUTE,
                        entryHelper.getAttributeName(role.isAdmin() ? RoleType.USER_ADMIN : RoleType.VIEWER),
                        entryHelper.buildUserDn(user.getUsername(), user.getState(), user.getDistrict()));
                connection.modify(roleDn, modification);
            }

            //TODO: result codes for modify
            if (resultCode != ResultCodeEnum.SUCCESS && userPriorUpdate == null) {
                throw new LdapWriteException(String.format("User %s failed to add user %s with dn %s. Error code: %s",
                        creatorUsername, user.getUsername(), userEntry.getDn(), resultCode));
            }

        } catch (LdapException e) {
            throw new LdapWriteException(String.format("User %s failed to add user %s",
                    creatorUsername, user.getUsername()), e);
        } finally {
            ConnectionUtils.unbind(connection);
        }
    }

    @Override
    public List<LdapUser> getUsers(String adminUsername, String adminPassword) {
        ApacheDsUser currentUser = getCurrentUser(adminUsername);

        LdapConnection connection = null;
        try {
            connection = ConnectionUtils.getConnectionAndBindUser(ldapHost, ldapPort, ldapUseSsl, currentUser.getDn(), adminPassword);

            return entryHelper.getAllUsers(connection);
        } catch (LdapException | CursorException e) {
            throw new LdapReadException("User " + adminUsername + " failed to retrieve users", e);
        } finally {
            ConnectionUtils.unbind(connection);
        }
    }

    @Override
    public void deleteUser(String username, String adminUsername, String adminPassword) {
        ApacheDsUser currentUser = getCurrentUser(adminUsername);

        LdapConnection connection = null;
        try {
            connection = ConnectionUtils.getConnectionAndBindUser(ldapHost, ldapPort, ldapUseSsl, currentUser.getDn(), adminPassword);

            entryHelper.deleteUser(connection, username);

            //TODO: Remove from role attributes
        } catch (LdapException | CursorException e) {
            throw new LdapReadException("User " + adminUsername + " failed to delete user", e);
        } finally {
            ConnectionUtils.unbind(connection);
        }
    }

    @Override
    public LdapNetworkConnection getConnectionForUser(String username, String password) {
        return ConnectionUtils.getConnectionAndBindUser(ldapHost, ldapPort, ldapUseSsl,
                ((ApacheDsUser) findUser(username)).getDn(), password);
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
