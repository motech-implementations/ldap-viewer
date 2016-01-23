package org.motechproject.nms.ldapbrowser.ldap;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
public class LdapRole {

    private String state;
    private String district;
    private boolean admin;
    private boolean masterAdmin;

    public LdapRole() {
        this(null, null, false, false);
    }

    public LdapRole(String state, String district, boolean admin) {
        this(state, district, admin, false);
    }

    public LdapRole(String state, String district, boolean admin, boolean masterAdmin) {
        this.masterAdmin = masterAdmin;
        this.admin = admin;
        this.district = district == null ? StringUtils.EMPTY : district;
        this.state = state == null ? StringUtils.EMPTY : state;
    }

    public boolean isMasterAdmin() {
        return masterAdmin;
    }

    public void setMasterAdmin(boolean masterAdmin) {
        this.masterAdmin = masterAdmin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        admin = admin;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        if (district == null) {
            district = StringUtils.EMPTY;
        }
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        if (state == null) {
            state = StringUtils.EMPTY;
        }
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LdapRole ldapRole = (LdapRole) o;
        return admin == ldapRole.admin &&
                masterAdmin == ldapRole.masterAdmin &&
                Objects.equals(state, ldapRole.state) &&
                Objects.equals(district, ldapRole.district);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, district, admin, masterAdmin);
    }
}
