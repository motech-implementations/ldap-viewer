package org.motechproject.nms.ldapbrowser.ldap;

/**
 * Represents state and district
 */
public class LdapLocation {

    private String state;
    private String district;

    public LdapLocation() {
        this(null, null);
    }

    public LdapLocation(String state, String district) {
        this.state = state;
        this.district = district;
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
