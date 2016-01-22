package org.motechproject.nms.ldapbrowser.ldap;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;

@XmlRootElement
public class DistrictInfo implements Serializable {

    private String district;
    private String state;

    public DistrictInfo() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DistrictInfo that = (DistrictInfo) o;
        return Objects.equals(district, that.district) &&
                Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(district, state);
    }
}
