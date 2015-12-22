package org.motechproject.nms.ldapbrowser.region;

import org.apache.commons.lang.StringUtils;
import org.motechproject.nms.ldapbrowser.ldap.DistrictInfo;

import java.util.ArrayList;
import java.util.List;

public class RegionServiceImpl implements RegionService {

    private static final String STATE_ADMIN = "State level";

    private RegionProvider regionProvider;

    @Override
    public List<String> availableStateNames() {
        List<String> names = new ArrayList<>(regionProvider.getStateNames());
        return names;
    }

    @Override
    public List<String> availableDistrictNames(String stateName) {
        List<String> names = new ArrayList<>();
        // If state is present, load all districts from this state
        if (StringUtils.isNotBlank(stateName)) {
            names.add(STATE_ADMIN); // State level admin
            names.addAll(regionProvider.getDistrictNames(stateName));
        } else { // Else include all districts user has access to
            for (DistrictInfo info : allAvailableDistrictInfo()) {
                names.add(info.getDistrict());
            }
        }

        return names;
    }

    @Override
    public List<DistrictInfo> allAvailableDistrictInfo() {
        return regionProvider.getAllAvailableDistricts();
    }

    public void setRegionProvider(RegionProvider regionProvider) {
        this.regionProvider = regionProvider;
    }
}
