package org.motechproject.nms.ldapbrowser.region;

import java.util.List;

public interface RegionProvider {

    List<String> getStateNames();

    List<String> getDistrictNames(String stateName);
}
