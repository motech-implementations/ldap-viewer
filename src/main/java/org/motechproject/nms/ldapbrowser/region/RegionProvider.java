package org.motechproject.nms.ldapbrowser.region;

import org.motechproject.nms.ldapbrowser.ldap.DistrictInfo;

import java.util.List;

public interface RegionProvider {

    List<String> getStateNames();

    List<String> getDistrictNames(String stateName);

    List<DistrictInfo> getAllAvailableDistricts();
}
