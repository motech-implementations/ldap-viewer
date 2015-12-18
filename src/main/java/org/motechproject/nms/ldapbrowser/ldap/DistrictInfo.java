package org.motechproject.nms.ldapbrowser.ldap;

public class DistrictInfo {

    private String district;
    private String state;

    public DistrictInfo(String state, String district) {
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
