package org.motechproject.nms.ldapbrowser.ldap;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

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
        return name;
    }

    public String getEmail() {
        return email;
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
}

