package org.motechproject.nms.ldapbrowser.region;

import java.util.List;

public interface RegionService {

    List<String> availableStateNames();

    List<String> availableDistrictNames();
}
