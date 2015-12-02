package org.motechproject.nms.ldapbrowser.ldap;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class LdapUser {

    @NotBlank
    private String username;
    @Length(min = 3)
    private String password;
    private String name;
    private String email;
    private String state;
    private String district;
    private boolean admin;

    public LdapUser() {
    }

    public LdapUser(String username, String password, String name, String email, String state, String district, boolean admin) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.state = state;
        this.district = district;
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
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
