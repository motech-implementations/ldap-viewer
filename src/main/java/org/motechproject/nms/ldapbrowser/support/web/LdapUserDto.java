package org.motechproject.nms.ldapbrowser.support.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.nms.ldapbrowser.ldap.LdapRole;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlRootElement
public class LdapUserDto {

    private static final String USER_ADMIN_PREFIX = "UA-";
    private static final String VIEWER_PREFIX = "V-";

    private String username;
    private String password;
    private String name;
    private String email;
    private String district;
    private String state;
    private List<String> roles;
    private String workNumber;
    private String mobileNumber;
    private boolean nationalAdmin;
    private boolean nationalView;
    private boolean uiEdit;

    public LdapUserDto() {
        this.roles = new ArrayList<>();
    }

    public LdapUserDto(LdapUser ldapUser) {
        this();
        this.username = ldapUser.getUsername();
        this.password = ldapUser.getPassword();
        this.name = ldapUser.getName();
        this.email = ldapUser.getEmail();
        this.district = ldapUser.getDistrict();
        this.state = ldapUser.getState();
        this.workNumber = ldapUser.getWorkNumber();
        this.mobileNumber = ldapUser.getMobileNumber();
        this.uiEdit = ldapUser.isUiEdit();
        buildUserRolesForView(ldapUser.getRoles());
    }

    private void buildUserRolesForView(List<LdapRole> roles) {
        for (LdapRole role: roles) {
            if (StringUtils.isBlank(role.getDistrict()) && StringUtils.isBlank(role.getState())) {
                if (role.isAdmin()) {
                    nationalAdmin = true;
                    nationalView = false;
                } else if (!nationalAdmin) {
                    nationalView = true;
                }
                continue;
            }

            StringBuilder sb = new StringBuilder();
            if (role.isAdmin()) {
                sb.append(USER_ADMIN_PREFIX);
            } else {
                sb.append(VIEWER_PREFIX);
            }
            if (StringUtils.isNotBlank(role.getState())) {
                sb.append(role.getState());
            }
            if (StringUtils.isNotBlank(role.getDistrict())) {
                sb.append("__");
                sb.append(role.getDistrict());
            }

            addRole(sb.toString());
        }
    }

    private void addRole(String role) {
        if (role.startsWith(USER_ADMIN_PREFIX)) {
            roles.remove(VIEWER_PREFIX + role.substring(USER_ADMIN_PREFIX.length()));
        } else if (roles.contains(USER_ADMIN_PREFIX + role.substring(VIEWER_PREFIX.length()))) {
            return;
        }

        roles.add(role);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return StringUtils.defaultString(name);
    }

    public String getEmail() {
        return StringUtils.defaultString(email);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWorkNumber() {
        return workNumber;
    }

    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public boolean isUiEdit() {
        return uiEdit;
    }

    public void setUiEdit(boolean uiEdit) {
        this.uiEdit = uiEdit;
    }

    public boolean isNationalAdmin() {
        return nationalAdmin;
    }

    public void setNationalAdmin(boolean nationalAdmin) {
        this.nationalAdmin = nationalAdmin;
    }

    public boolean isNationalView() {
        return nationalView;
    }

    public void setNationalView(boolean nationalView) {
        this.nationalView = nationalView;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LdapUserDto that = (LdapUserDto) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, name, email);
    }
}

