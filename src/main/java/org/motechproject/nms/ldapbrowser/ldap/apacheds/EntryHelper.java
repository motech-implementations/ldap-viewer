package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.motechproject.nms.ldapbrowser.ldap.AttributeNames;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapAuthException;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapReadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.CN;
import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.DC;
import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.OBJECT_CLASS;
import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.OU;

@Component
public class EntryHelper {

    @Value("${ldap.ou.roles")
    private String rolesOu;

    @Value("{ldap.ou.users}")
    private String usersOu;

    @Value("${ldap.dc")
    private String dc;

    @Value("${ldap.userClass}")
    private String userClass;

    @Value("{$ldap.nationalRole}")
    private String nationalRole;

    @Value("${ldap.roleSuffix}")
    private String roleSuffix;

    public ApacheDsUser buildUser(Entry entry) {
        ApacheDsUser ldapUser = new ApacheDsUser();

        ldapUser.setName(getAttributeStrVal(entry, AttributeNames.NAME));
        ldapUser.setEmail(getAttributeStrVal(entry, AttributeNames.EMAIL));
        setStateAndDistrict(entry, ldapUser);

        ldapUser.setDn(entry.getDn());

        return ldapUser;
    }

    public EntryCursor searchForAdmins(LdapConnection connection) throws LdapException {
        String baseDn = String.format("%s=%s, %s=%s", OU, rolesOu, DC, dc);
        String filter = String.format("(%s=%s)", OBJECT_CLASS, userClass);

        return connection.search(baseDn, filter, SearchScope.SUBTREE);
    }

    public String getUsername(Entry entry) {
        return getAttributeStrVal(entry, CN);
    }

    public Entry userToEntry(LdapUser user) throws LdapException {
        return new DefaultEntry(
            buildDn(user),
            attrStr(OBJECT_CLASS, userClass),

            attrStr(AttributeNames.NAME, user.getName()),
            attrStr(AttributeNames.EMAIL, user.getEmail())

            // TODO: password
        );
    }

    public String buildDn(LdapUser user) {
        StringBuilder sb = new StringBuilder();

        appendCnEqual(sb);
        sb.append(user.getUsername());

        if (!LdapUser.ALL.equals(user.getDistrict())) {
            sb.append(',');
            appendCnEqual(sb);
            appendRoleName(sb, user.getDistrict());
        }
        if (!LdapUser.ALL.equals(user.getState())) {
            sb.append(',');
            appendCnEqual(sb);
            appendRoleName(sb, user.getState());
        }

        sb.append(',');
        appendCnEqual(sb);
        sb.append(nationalRole);

        sb.append(',').append(OU);
        sb.append(user.isAdmin() ? rolesOu : usersOu);

        sb.append(',').append(DC).append(dc);

        return sb.toString();
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
            if (!LdapConstants.CN.equals(rdn.getAva().getType())) {
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
        } else if (rdnHolderList.size() == 3) {
            // district view
            user.setState(parseRole(rdnHolderList.get(0).getValue()));
            user.setDistrict(parseRole(rdnHolderList.get(1).getValue()));
        } else {
            throw new LdapAuthException("Illegal user state, number of cn entries: " + rdnHolderList.size());
        }
    }

    private String parseRole(String avaVal) {
        return avaVal.replace(' ' + roleSuffix, "");
    }

    private void appendRoleName(StringBuilder sb, String stateOrDistrict) {
        sb.append(stateOrDistrict).append(' ').append(roleSuffix);
    }

    private void appendCnEqual(StringBuilder sb) {
        sb.append(CN).append('=');
    }

    private String attrStr(String attrName, String attrVal) {
        return String.format("%s: %s", attrName,  attrVal);
    }
}
