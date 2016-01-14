package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import org.apache.commons.lang.StringUtils;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultAttribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.search.FilterBuilder;
import org.motechproject.nms.ldapbrowser.ldap.AttributeNames;
import org.motechproject.nms.ldapbrowser.ldap.DistrictInfo;
import org.motechproject.nms.ldapbrowser.ldap.LdapLocation;
import org.motechproject.nms.ldapbrowser.ldap.LdapRole;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.RoleType;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapAuthException;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapReadException;

import java.util.ArrayList;
import java.util.HashSet;
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

    private static final int DISTRICT_DEPTH = 4;

    private String rolesOu;
    private String usersOu;
    private String dc;
    private String userClass;
    private String extendedUserClass;
    private String nationalRole;
    private String nationalUserAdminRole;
    private String roleSuffix;
    private String adminRoleSuffix;
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

        String baseUsersDn = ouDcStr(usersOu);
        String userFilter = FilterBuilder.or(FilterBuilder.equal(OBJECT_CLASS, userClass)).toString();

        EntryCursor userCursor = connection.search(baseUsersDn, userFilter, SearchScope.SUBTREE);

        while (userCursor.next()) {
            Entry entry = userCursor.get();
            LdapUser user = getUser(connection, entry.getDn().toString());
            users.add(user);
        }

        return new ArrayList<>(users);
    }

    public void deleteUser(LdapConnection connection, String username) throws CursorException, LdapException {
        ApacheDsUser user = (ApacheDsUser) findUserByUsername(connection, username);
        EntryCursor cursor = getAllRolesCursor(connection);

        // Remove user from all the role entries he is assigned to
        while (cursor.next()) {
            Entry role = cursor.get();
            if (role.contains(memberAttrName, user.getDn().toString())) {
                Modification removeRole = new DefaultModification(ModificationOperation.REMOVE_ATTRIBUTE, memberAttrName, user.getDn().toString());
                connection.modify(role.getDn(), removeRole);
            }
            if (role.contains(occupantAttrName, user.getDn().toString())) {
                Modification removeRole = new DefaultModification(ModificationOperation.REMOVE_ATTRIBUTE, occupantAttrName, user.getDn().toString());
                connection.modify(role.getDn(), removeRole);
            }
        }

        // Remove user entry
        connection.delete(user.getDn());
    }

    private LdapUser findUserByUsername(LdapConnection connection, String username) throws CursorException, LdapException {
        List<LdapUser> users = getAllUsers(connection);
        for (LdapUser user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }

    public String getUsername(Entry entry) {
        return getAttributeStrVal(entry, CN);
    }

    public Entry userToEntry(LdapUser user) throws LdapException {
        DefaultEntry userEntry =  new DefaultEntry(
            buildUserDn(user.getUsername(), user.getState(), user.getDistrict()),
            attrStr(OBJECT_CLASS, userClass),
            attrStr(OBJECT_CLASS, extendedUserClass),
            attrStr(AttributeNames.PASSWORD, user.getPassword()),
            attrStr(AttributeNames.NAME, user.getName())
        );

        // Optional attributes
        if (StringUtils.isNotBlank(user.getEmail())) {
            userEntry.add(new DefaultAttribute(AttributeNames.EMAIL, user.getEmail()));
        }
        if (StringUtils.isNotBlank(user.getMobileNumber())) {
            userEntry.add(new DefaultAttribute(AttributeNames.MOBILE_NUMBER, user.getMobileNumber()));
        }
        if (StringUtils.isNotBlank(user.getWorkNumber())) {
            userEntry.add(new DefaultAttribute(AttributeNames.WORK_NUMBER, user.getWorkNumber()));
        }

        return userEntry;
    }

    public String getAttributeName(RoleType type) {
        if (type == RoleType.USER_ADMIN) {
            return memberAttrName;
        } else if (type == RoleType.VIEWER) {
            return occupantAttrName;
        }

        return null;
    }

    public String buildUserDn(String username, String state, String district) {
        return buildCnPartForUser(username).concat(buildDn(state, district, RoleType.NONE));
    }

    public String buildDn(String state, String district, RoleType type) {
        StringBuilder sb = new StringBuilder();

        if (StringUtils.isNotBlank(district)) {
            appendCnEqual(sb);
            appendRoleName(sb, district, type);
            sb.append(",");
        }
        if (StringUtils.isNotBlank(state)) {
            appendCnEqual(sb);
            appendRoleName(sb, state, type);
            sb.append(",");
        }
        if (type == RoleType.USER_ADMIN) {
            appendCnEqual(sb);
            sb.append(nationalUserAdminRole);
            sb.append(",");
        } else if (type == RoleType.VIEWER) {
            appendCnEqual(sb);
            sb.append(nationalRole);
            sb.append(",");
        }

        sb.append(OU).append("=");
        if (type == RoleType.USER_ADMIN || type == RoleType.VIEWER) {
            sb.append(rolesOu);
        } else {
            sb.append(usersOu);
        }

        sb.append(',').append(DC).append("=").append(dc);

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
        String baseDn = buildDn(null, null, RoleType.NONE);

        String filter = FilterBuilder.equal(OBJECT_CLASS, roleClass).toString();
        EntryCursor cursor = connection.search(baseDn, filter, SearchScope.ONELEVEL);

        while (cursor.next()) {
            Entry entry = cursor.get();
            String stateName = entry.get(CN).getString();
            names.add(stateName);
        }

        return names;
    }

    public List<String> districtNames(LdapConnection connection, String stateName) throws LdapException, CursorException {
        Set<String> names = new HashSet<>();

        // Add all districts for state role
        String stateDn = buildDn(stateName, null, RoleType.NONE);
        String filter = FilterBuilder.equal(OBJECT_CLASS, roleClass).toString();
        EntryCursor cursor = connection.search(stateDn, filter, SearchScope.ONELEVEL);
        while (cursor.next()) {
            Entry entry = cursor.get();
            String distName = entry.get(CN).getString();
            names.add(distName);
        }

        return new ArrayList<>(names);
    }

    public List<DistrictInfo> allAvailableDistrictNames(LdapConnection connection) throws CursorException, LdapException {
        Set<DistrictInfo> names = new HashSet<>();

        String baseDn = buildDn(null, null, RoleType.NONE);
        String filter = FilterBuilder.equal(OBJECT_CLASS, roleClass).toString();
        EntryCursor cursor = connection.search(baseDn, filter, SearchScope.SUBTREE);
        while (cursor.next()) {
            Entry entry = cursor.get();
            if (entry.getDn().getRdns().size() == DISTRICT_DEPTH) {
                String distName = entry.get(CN).getString();
                String state = entry.getDn().getRdn(1).getValue();
                names.add(new DistrictInfo(state, distName));
            }
        }

        return new ArrayList<>(names);
    }

    public List<Modification> prepareModificationRequests(Entry userEntry, LdapUser userPriorUpdate) throws LdapInvalidAttributeValueException {
        List<Modification> modifications = new ArrayList<>();

        if (!userEntry.get(AttributeNames.NAME).contains(userPriorUpdate.getName())) {
            Modification modification = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, AttributeNames.NAME, userEntry.get(AttributeNames.NAME).getString());
            modifications.add(modification);
        }
        if (userEntry.containsAttribute(AttributeNames.EMAIL) && !userEntry.get(AttributeNames.EMAIL).contains(userPriorUpdate.getEmail())) {
            Modification modification = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, AttributeNames.EMAIL, userEntry.get(AttributeNames.EMAIL).getString());
            modifications.add(modification);
        }
        if (userEntry.containsAttribute(AttributeNames.MOBILE_NUMBER) && !userEntry.get(AttributeNames.MOBILE_NUMBER).contains(userPriorUpdate.getMobileNumber())) {
            Modification modification = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, AttributeNames.MOBILE_NUMBER, userEntry.get(AttributeNames.MOBILE_NUMBER).getString());
            modifications.add(modification);
        }
        if (userEntry.containsAttribute(AttributeNames.WORK_NUMBER) && !userEntry.get(AttributeNames.WORK_NUMBER).contains(userPriorUpdate.getWorkNumber())) {
            Modification modification = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, AttributeNames.WORK_NUMBER, userEntry.get(AttributeNames.WORK_NUMBER).getString());
            modifications.add(modification);
        }
        if (userEntry.containsAttribute(AttributeNames.PASSWORD) && StringUtils.isNotBlank(userEntry.get(AttributeNames.PASSWORD).getString())) {
            Modification modification = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, AttributeNames.PASSWORD, userEntry.get(AttributeNames.PASSWORD).getString());
            modifications.add(modification);
        }

        return modifications;
    }

    public String findStateForDistrict(LdapConnection connection, String district) throws CursorException, LdapException {
        List<DistrictInfo> districts = allAvailableDistrictNames(connection);
        for (DistrictInfo info : districts) {
            if (info.getDistrict().equals(district)) {
                return info.getState();
            }
        }

        // Not found oruser does not have access to the given district
        return null;
    }

    private String buildCnPartForUser(String username) {
        StringBuilder cnBuilder = new StringBuilder();
        appendCnEqual(cnBuilder);
        cnBuilder.append(username);
        cnBuilder.append(",");

        return cnBuilder.toString();
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
                user.getRoles().add(new LdapRole(location.getState(), location.getDistrict(), roleEntry.containsAttribute(memberAttrName)));
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
        if (rdnHolderList.size() == 1 + offset) {
            // state level
            location.setState(parseRole(rdnHolderList.get(0).getValue()));
        } else if (rdnHolderList.size() == 2 + offset) {
            // district level
            location.setDistrict(parseRole(rdnHolderList.get(0).getValue()));
            location.setState(parseRole(rdnHolderList.get(1).getValue()));
        } else if (rdnHolderList.size() != offset){
            throw new LdapAuthException("Illegal user state, number of cn entries: " + rdnHolderList.size());
        }

        return location;
    }

    private String parseRole(String avaVal) {
        return avaVal.replace(' ' + roleSuffix, "").replace(' ' + adminRoleSuffix, "");
    }

    private void appendRoleName(StringBuilder sb, String stateOrDistrict, RoleType type) {
        sb.append(stateOrDistrict);

        if (type == RoleType.USER_ADMIN) {
            sb.append(' ').append(adminRoleSuffix);
        } else if (type == RoleType.VIEWER) {
            sb.append(' ').append(roleSuffix);
        }
    }

    private void appendCnEqual(StringBuilder sb) {
        sb.append(CN).append('=');
    }

    private String attrStr(String attrName, String attrVal) {
        return String.format("%s: %s", attrName,  attrVal);
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

    public void setExtendedUserClass(String extendedUserClass) {
        this.extendedUserClass = extendedUserClass;
    }

    public void setNationalUserAdminRole(String nationalUserAdminRole) {
        this.nationalUserAdminRole = nationalUserAdminRole;
    }

    public void setAdminRoleSuffix(String adminRoleSuffix) {
        this.adminRoleSuffix = adminRoleSuffix;
    }
}
