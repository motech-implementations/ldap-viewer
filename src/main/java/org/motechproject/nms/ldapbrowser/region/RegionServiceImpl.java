package org.motechproject.nms.ldapbrowser.region;

import org.motechproject.nms.ldapbrowser.ldap.DistrictInfo;

import java.util.ArrayList;
import java.util.List;

public class RegionServiceImpl implements RegionService {

    private static final String ALL = "ALL";

    private RegionProvider regionProvider;

    @Override
    public List<String> availableStateNames() {
        List<String> names = new ArrayList<>(regionProvider.getStateNames());
        return names;
    }

    @Override
    public List<String> availableDistrictNames(String stateName) {
        List<String> names = new ArrayList<>(regionProvider.getDistrictNames(stateName));
        return names;
    }

    @Override
    public List<DistrictInfo> allAvailableDistrictInfo() {
        List<DistrictInfo> districts = new ArrayList<>();
        for (String state : availableStateNames()) {
            for (String district : availableDistrictNames(state)) {
                districts.add(new DistrictInfo(state, district));
            }
        }

        return districts;
    }

    public void setRegionProvider(RegionProvider regionProvider) {
        this.regionProvider = regionProvider;
    }
}
