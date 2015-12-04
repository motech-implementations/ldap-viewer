package org.motechproject.nms.ldapbrowser.ldap;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapAuthException;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapReadException;
import org.motechproject.nms.ldapbrowser.region.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// @Service
public class ApacheDsLdapService extends DummyLdapService implements LdapUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApacheDsLdapService.class);

    public static final String NAME = "name";
    public static final String EMAIL = "email";

    public static final String CN = "cn";

    @Value("${ldap.host}")
    private String ldapHost;

    @Value("${ldap.port}")
    private int ldapPort;

    @Value("${ldap.useSsl}")
    private boolean ldapUseSsl;

    @Inject
    private LdapConnectionPool adminConnectionPool;

    @Inject
    private RegionService regionService;

    @Override
    public LdapUser authenticate(String username, String password) {
        Entry userEntry = findUser(username);
        if (userEntry != null) {
            boolean auth = bindUnbindUser(userEntry, password);
            return auth ? buildUser(userEntry) : null;
        } else {
            return null;
        }
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
        setStateAndDistrict(entry, ldapUser);

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

    private void setStateAndDistrict(Entry entry, LdapUser user) {
        Dn dn = entry.getDn();
        List<Rdn> rdns = dn.getRdns();
        // remove the username rdn
        List<Rdn> rdnHolderList = new LinkedList<>(rdns.subList(1, rdns.size()));

        // leave only cn entries
        Iterator<Rdn> it = rdnHolderList.iterator();
        while (it.hasNext()) {
            Rdn rdn = it.next();
            if (!CN.equals(rdn.getAva().getType())) {
                it.remove();
            }
        }

        // determine rights

        if (rdnHolderList.size() == 1) {
            // national view
            user.setState(LdapUser.ALL);
            user.setDistrict(LdapUser.ALL);
        } else if (rdnHolderList.size() == 2) {
            // state view
            user.setState(parseRole(rdnHolderList.get(0).getValue()));
            user.setDistrict(LdapUser.ALL);
            validateState(user.getState());
        } else if (rdnHolderList.size() == 3) {
            // district view
            user.setState(parseRole(rdnHolderList.get(0).getValue()));
            user.setDistrict(parseRole(rdnHolderList.get(1).getValue()));
            validateDistrict(user.getState(), user.getDistrict());
        } else {
            throw new LdapAuthException("Illegal user state, number of cn entries: " + rdnHolderList.size());
        }
    }

    private boolean bindUnbindUser(Entry entry, String password) {
        try (LdapConnection connection = new LdapNetworkConnection(ldapHost, ldapPort, ldapUseSsl)) {
            connection.bind(entry.getDn(), password);
            connection.unBind();
            return true;
        } catch (IOException | LdapException e) {
            LOGGER.warn("Unable to authenticate user", e);
            return false;
        }
    }

    private String parseRole(String avaVal) {
        return avaVal.replace(" View", "");
    }

    private void validateState(String stateName) {
        if (!regionService.stateExists(stateName)) {
            throw new LdapReadException("Unknown state: " + stateName);
        }
    }

    private void validateDistrict(String stateName, String districtName) {
        if (!regionService.districtExists(stateName, districtName)) {
            throw new LdapReadException("Unknown district: " + districtName + " in state " + stateName);
        }
    }
}
