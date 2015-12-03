package org.motechproject.nms.ldapbrowser.ldap;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapAuthException;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapReadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class ApacheDsLdapService extends DummyLdapService implements LdapUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApacheDsLdapService.class);

    public static final String NAME = "name";
    public static final String EMAIL = "email";

    @Inject
    private LdapConnectionPool adminConnectionPool;

    @Override
    public LdapUser authenticate(String username, String password) {
        Entry userEntry = findUser(username);
        return buildUser(userEntry);
    }

    private Entry findUser(String username) {
        LdapConnection connection;

        try {
            connection = adminConnectionPool.getConnection();
        } catch (LdapException e) {
            throw new LdapAuthException("Unable to bind admin connection", e);
        }

        try {
            EntryCursor cursor = connection.search("ou=roles, dc=nms", "(objectclass=person)", SearchScope.SUBTREE);
            while (cursor.next()) {
                Entry entry = cursor.get();
                Attribute usernameAttr = entry.get("cn");
                String ldapUserName = usernameAttr.getString();
                if (username.equals(ldapUserName)) {
                    return entry;
                }
            }

            return null;
        } catch (LdapException | CursorException e) {
            throw new LdapAuthException("Unable to search for users in LDAP", e);
        } finally {
            unbind(connection);
        }
    }

    private void unbind(LdapConnection connection) {
        if (connection != null && connection.isAuthenticated()) {
            try {
                connection.unBind();
            } catch (LdapException e) {
                LOGGER.error("Error while unbinding the connection", e);
            }
        }
    }

    private LdapUser buildUser(Entry entry) {
        LdapUser ldapUser = new LdapUser();

        ldapUser.setName(getAttributeStrVal(entry, NAME));
        ldapUser.setEmail(getAttributeStrVal(entry, EMAIL));

        return ldapUser;
    }

    private String getAttributeStrVal(Entry entry, String attrName) {
        try {
            Attribute attr = entry.get(attrName);
            return attr == null ? null : attr.getString();
        } catch (LdapInvalidAttributeValueException e) {
            throw new LdapReadException("Unable to read attribute: " + attrName, e);
        }
    }
}
