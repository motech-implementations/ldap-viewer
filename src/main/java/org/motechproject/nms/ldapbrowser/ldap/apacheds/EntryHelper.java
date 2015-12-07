package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import org.apache.directory.api.ldap.model.cursor.CursorException;
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
import org.apache.directory.ldap.client.api.search.FilterBuilder;
import org.motechproject.nms.ldapbrowser.ldap.AttributeNames;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapAuthException;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapReadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.CN;
import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.DC;
import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.OBJECT_CLASS;
import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.OU;

@Component
public class EntryHelper {

    private static final int DISTRICT_RDN_COUNT = 5;

    @Value("${ldap.ou.roles}")
    private String rolesOu;

    @Value("${ldap.ou.users}")
    private String usersOu;

    @Value("${ldap.dc}")
    private String dc;

    @Value("${ldap.userClass}")
    private String userClass;

    @Value("${ldap.nationalRole}")
    private String nationalRole;

    @Value("${ldap.roleSuffix}")
    private String roleSuffix;

    @Value("${ldap.roleClass}")
    private String roleClass;

    @Value("${ldap.occupantAttrName}")
    private String occupantAttrName;

    public ApacheDsUser buildUser(Entry entry) {
        ApacheDsUser ldapUser = new ApacheDsUser();

        ldapUser.setUsername(entryName(entry));
        ldapUser.setName(getAttributeStrVal(entry, AttributeNames.NAME));
        ldapUser.setEmail(getAttributeStrVal(entry, AttributeNames.EMAIL));
        ldapUser.setMobileNumber(getAttributeStrVal(entry, AttributeNames.MOBILE_NUMBER));
        ldapUser.setWorkNumber(getAttributeStrVal(entry, AttributeNames.WORK_NUMBER));

        setStateAndDistrict(entry, ldapUser);

        ldapUser.setDn(entry.getDn());

        // TODO: improve
        ldapUser.setAdmin(entry.getDn().toString().contains("ou=" + rolesOu));

        return ldapUser;
    }

    public EntryCursor searchForAdmins(LdapConnection connection) throws LdapException {
        String baseDn = ouDcStr(rolesOu);
        String filter = FilterBuilder.equal(OBJECT_CLASS, userClass).toString();

        return connection.search(baseDn, filter, SearchScope.SUBTREE);
    }

    public List<LdapUser> getAllUsers(LdapConnection connection, String state, String district) throws LdapException, CursorException {
        List<LdapUser> users = new ArrayList<>();

        String stateDistrictPart = stateAndDistrictToDnPart(state, district);
        String oudc = ouDcStr(rolesOu);

        String baseDn = stateDistrictPart + oudc;
        String filter = FilterBuilder.equal(OBJECT_CLASS, roleClass).toString();

        EntryCursor roleCursor = connection.search(baseDn, filter, SearchScope.SUBTREE);

        while (roleCursor.next()) {
            Entry entry = roleCursor.get();
            List<String> dns = userDnsFromRole(entry);

            for (String dn : dns) {
                LdapUser user = getUser(connection, dn);
                if (user != null) {
                    users.add(user);
                }
            }
        }

        return users;
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

    public LdapUser getUser(LdapConnection connection, String dn) throws LdapException, CursorException {
        String filter = FilterBuilder.equal(OBJECT_CLASS, userClass).toString();
        EntryCursor cursor = connection.search(dn, filter, SearchScope.OBJECT);

        if (cursor.next()) {
            return buildUser(cursor.get());
        } else {
            return null;
        }
    }

    public List<String> stateNames(LdapConnection connection) throws LdapException, CursorException {
        List<String> names = new ArrayList<>();

        String nationalView = String.format("%s=%s, ", CN, nationalRole);
        String oudc = ouDcStr(rolesOu);

        String filter = FilterBuilder.equal(OBJECT_CLASS, roleClass).toString();
        String baseDn = nationalView + oudc;

        EntryCursor cursor = connection.search(baseDn, filter, SearchScope.ONELEVEL);

        while (cursor.next()) {
            Entry entry = cursor.get();
            String stateName = parseRole(entryName(entry));
            names.add(stateName);
        }

        return names;
    }

    public List<String> districtNames(LdapConnection connection, String stateName) throws LdapException, CursorException {
        List<String> names = new ArrayList<>();

        String districtStatePart = stateAndDistrictToDnPart(stateName, null);
        String oudc = ouDcStr(rolesOu);

        String filter = FilterBuilder.equal(OBJECT_CLASS, roleClass).toString();
        String baseDn = districtStatePart + oudc;

        EntryCursor cursor = connection.search(baseDn, filter, SearchScope.ONELEVEL);

        while (cursor.next()) {
            Entry entry = cursor.get();
            String distName = parseRole(entryName(entry));
            names.add(distName);
        }

        return names;
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

    private String stateAndDistrictToDnPart(String state, String district) {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(district) && !LdapUser.ALL.equals(district)) {
            sb.append(CN).append('=');
            appendRoleName(sb, district);
        }
        if (!StringUtils.isEmpty(state) && !LdapUser.ALL.equals(state)) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(CN).append('=');
            appendRoleName(sb, state);
        }
        if (sb.length() > 0) {
            sb.append(", ").append(CN).append('=').append(nationalRole).append(", ");
        }
        return sb.toString();
    }

    private List<String> userDnsFromRole(Entry roleEntry) {
        List<String> dns = new ArrayList<>();
        Attribute occupantAttr = roleEntry.get(occupantAttrName);
        if (occupantAttr != null) {
            for (org.apache.directory.api.ldap.model.entry.Value val : occupantAttr) {
                dns.add(val.getString());
            }
        }
        return dns;
    }

    private String ouDcStr(String ou) {
        return String.format("%s=%s, %s=%s", OU, ou, DC, dc);
    }

    private String entryName(Entry entry) {
        return entry.getDn().getRdns().get(0).getValue();
    }
}
