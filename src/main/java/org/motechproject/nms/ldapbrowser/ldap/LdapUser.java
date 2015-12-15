package org.motechproject.nms.ldapbrowser.ldap;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlRootElement
public class LdapUser {

    public static final String ALL = "ALL";

    private String username;
    private String password;
    private String name;
    private String email;
    private String district;
    private String state;
    private List<LdapRole> roles;
    private String workNumber;
    private String mobileNumber;
    private boolean uiEdit;

    public LdapUser() {
        this.roles = new ArrayList<>();
    }

    public LdapUser(String username, String password, String name, String email, String state, String district) {
        this();
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.state = state;
        this.district = district;
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

    public boolean isUiEdit() {
        return uiEdit;
    }

    public void setUiEdit(boolean uiEdit) {
        this.uiEdit = uiEdit;
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

    public List<LdapRole> getRoles() {
        return roles;
    }

    public void setRoles(List<LdapRole> roles) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LdapUser ldapUser = (LdapUser) o;
        return Objects.equals(username, ldapUser.username) &&
                Objects.equals(name, ldapUser.name) &&
                Objects.equals(email, ldapUser.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, name, email);
    }
}

