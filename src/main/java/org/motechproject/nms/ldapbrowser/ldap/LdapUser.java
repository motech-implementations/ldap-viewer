package org.motechproject.nms.ldapbrowser.ldap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.StringUtils;

public class LdapUser {

    public static final String ALL = "ALL";

    private String username;
    private String password;
    private String name;
    private String email;
    private String state;
    private String district;
    private String workNumber;
    private String mobileNumber;
    private boolean admin;
    private boolean uiEdit;

    public LdapUser() {
        this.state = ALL;
        this.district = ALL;
    }

    public LdapUser(String username, String password, String name, String email, String state, String district, boolean admin) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.state = StringUtils.isEmpty(state) ? ALL : state;
        this.district = StringUtils.isEmpty(district) ? ALL : district;
        this.admin = admin;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = StringUtils.isEmpty(state) ? ALL : state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = StringUtils.isEmpty(district) ? ALL : district;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
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

    @JsonIgnore
    public boolean isNationalLevel() {
        return ALL.equals(district) && ALL.equals(state);
    }

    @JsonIgnore
    public boolean isStateLevel() {
        return ALL.equals(district) && !ALL.equals(state);
    }

    @JsonIgnore
    public boolean isDistrictLevel() {
        return !ALL.equals(district) && !ALL.equals(state);
    }
}
