package org.motechproject.nms.ldapbrowser.ldap;

import org.codehaus.jackson.annotate.JsonIgnore;

public class LdapRole {

    public static final String ALL = "ALL";

    private String state;
    private String district;
    private boolean isAdmin;

    public LdapRole(String state, String district, boolean isAdmin) {
        this.isAdmin = isAdmin;
        this.district = district;
        this.state = state;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
