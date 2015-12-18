package org.motechproject.nms.ldapbrowser.region;

import org.motechproject.nms.ldapbrowser.ldap.DistrictInfo;

import java.util.List;

public interface RegionService {

    List<String> availableStateNames();

    List<String> availableDistrictNames(String stateName);

    List<DistrictInfo> allAvailableDistrictInfo();
}
