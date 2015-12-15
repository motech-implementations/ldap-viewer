package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import org.apache.commons.lang.StringUtils;
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
import org.motechproject.nms.ldapbrowser.ldap.LdapLocation;
import org.motechproject.nms.ldapbrowser.ldap.LdapRole;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapAuthException;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapReadException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.CN;
import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.DC;
import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.OBJECT_CLASS;
import static org.motechproject.nms.ldapbrowser.ldap.apacheds.LdapConstants.OU;

public class EntryHelper {

    private static final int DISTRICT_RDN_COUNT = 5;

    private String rolesOu;
    private String usersOu;
    private String dc;
    private String userClass;
    private String nationalRole;
    private String roleSuffix;
    private String roleClass;
    private String adminRoleClass;
    private String occupantAttrName;
    private String memberAttrName;

    public ApacheDsUser buildUser(Entry entry, EntryCursor roleCursor) throws CursorException, LdapException {
        ApacheDsUser ldapUser = new ApacheDsUser();

        ldapUser.setUsername(entryName(entry));
        ldapUser.setName(getAttributeStrVal(entry, AttributeNames.NAME));
        ldapUser.setEmail(getAttributeStrVal(entry, AttributeNames.EMAIL));
        ldapUser.setMobileNumber(getAttributeStrVal(entry, AttributeNames.MOBILE_NUMBER));
        ldapUser.setWorkNumber(getAttributeStrVal(entry, AttributeNames.WORK_NUMBER));

        setStateAndDistrict(entry, ldapUser, roleCursor);

        ldapUser.setDn(entry.getDn());

        return ldapUser;
    }

    public EntryCursor searchForAdmins(LdapConnection connection) throws LdapException {
        String baseDn = ouDcStr(usersOu);
        String filter = FilterBuilder.equal(OBJECT_CLASS, userClass).toString();

        return connection.search(baseDn, filter, SearchScope.SUBTREE);
    }

    public List<LdapUser> getAllUsers(LdapConnection connection) throws LdapException, CursorException {
        Set<LdapUser> users = new LinkedHashSet<>();

        String baseDn = ouDcStr(rolesOu);
        String filter = FilterBuilder.or(FilterBuilder.equal(OBJECT_CLASS, roleClass), FilterBuilder.equal(OBJECT_CLASS, adminRoleClass)).toString();

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

        return new ArrayList<>(users);
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
        sb.append(',').append(DC).append(dc);

        return sb.toString();
    }

    public LdapUser getUser(LdapConnection connection, String dn) throws LdapException, CursorException {
        String filter = FilterBuilder.equal(OBJECT_CLASS, userClass).toString();
        EntryCursor cursor = connection.search(dn, filter, SearchScope.OBJECT);
        EntryCursor roleCursor = getAllRolesCursor(connection);

        if (cursor.next()) {
            return buildUser(cursor.get(), roleCursor);
        } else {
            return null;
        }
    }

    public EntryCursor getAllRolesCursor(LdapConnection connection) throws LdapException, CursorException {
        String oudc = ouDcStr(rolesOu);
        String roleFilter = FilterBuilder.or(FilterBuilder.equal(OBJECT_CLASS, roleClass), FilterBuilder.equal(OBJECT_CLASS, adminRoleClass)).toString();
        return connection.search(oudc, roleFilter, SearchScope.SUBTREE);
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

    private void setStateAndDistrict(Entry entry, LdapUser user, EntryCursor roleCursor) throws LdapException, CursorException {
        Dn dn = entry.getDn();
        List<Rdn> rdns = dn.getRdns();
        // remove the username rdn
        List<Rdn> rdnHolderList = new LinkedList<>(rdns.subList(1, rdns.size()));

        while (roleCursor.next()) {
            Entry roleEntry = roleCursor.get();
            List<String> dns = userDnsFromRole(roleEntry);

            if (dns.contains(dn)) {
                LdapLocation location = getLocationFromRoleRdns(roleEntry.getDn().getRdns());
                user.getRoles().add(new LdapRole(location.getState(), location.getDistrict(), location.getState().contains("Admin")));
            }
        }

        // home district and state
        LdapLocation location = getLocationFromUserRdns(rdnHolderList);
        user.setDistrict(location.getDistrict());
        user.setState(location.getState());
    }

    private LdapLocation getLocationFromUserRdns(List<Rdn> rdnHolderList) {
        return getLocationFromRdns(rdnHolderList, 0);
    }

    private LdapLocation getLocationFromRoleRdns(List<Rdn> rdnHolderList) {
        return getLocationFromRdns(rdnHolderList, 1);
    }

    private LdapLocation getLocationFromRdns(List<Rdn> holderList, int offset) {
        // leave only cn entries
        List<Rdn> rdnHolderList = new ArrayList<>(holderList);
        Iterator<Rdn> it = rdnHolderList.iterator();
        while (it.hasNext()) {
            Rdn rdn = it.next();
            if (!LdapConstants.CN.equals(rdn.getAva().getType())) {
                it.remove();
            }
        }

        LdapLocation location = new LdapLocation();
        if (rdnHolderList.size() == 0 + offset) {
            // national view
            location.setState(LdapUser.ALL);
            location.setDistrict(LdapUser.ALL);
        } else if (rdnHolderList.size() == 1 + offset) {
            // state view
            location.setState(parseRole(rdnHolderList.get(0).getValue()));
            location.setDistrict(LdapUser.ALL);
        } else if (rdnHolderList.size() == 2 + offset) {
            // district view
            location.setDistrict(parseRole(rdnHolderList.get(0).getValue()));
            location.setState(parseRole(rdnHolderList.get(1).getValue()));
        } else {
            throw new LdapAuthException("Illegal user state, number of cn entries: " + rdnHolderList.size());
        }

        return location;
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
        Attribute memberAttr = roleEntry.get(memberAttrName);

        // View roles
        if (occupantAttr != null) {
            for (org.apache.directory.api.ldap.model.entry.Value val : occupantAttr) {
                dns.add(val.getString());
            }
        }

        // User roles
        if (memberAttr != null) {
            for (org.apache.directory.api.ldap.model.entry.Value val : memberAttr) {
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

    public void setRolesOu(String rolesOu) {
        this.rolesOu = rolesOu;
    }

    public void setUsersOu(String usersOu) {
        this.usersOu = usersOu;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public void setUserClass(String userClass) {
        this.userClass = userClass;
    }

    public void setNationalRole(String nationalRole) {
        this.nationalRole = nationalRole;
    }

    public void setRoleSuffix(String roleSuffix) {
        this.roleSuffix = roleSuffix;
    }

    public void setRoleClass(String roleClass) {
        this.roleClass = roleClass;
    }

    public void setOccupantAttrName(String occupantAttrName) {
        this.occupantAttrName = occupantAttrName;
    }

    public void setAdminRoleClass(String adminRoleClass) {
        this.adminRoleClass = adminRoleClass;
    }

    public void setMemberAttrName(String memberAttrName) {
        this.memberAttrName = memberAttrName;
    }
}
