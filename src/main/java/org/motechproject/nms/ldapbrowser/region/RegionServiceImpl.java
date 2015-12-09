package org.motechproject.nms.ldapbrowser.region;

import java.util.ArrayList;
import java.util.List;

public class RegionServiceImpl implements RegionService {

    private static final String ALL = "ALL";

    private RegionProvider regionProvider;

    @Override
    public List<String> availableStateNames() {
        List<String> names = new ArrayList<>(regionProvider.getStateNames());
        names.add(ALL);
        return names;
    }

    @Override
    public List<String> availableDistrictNames(String stateName) {
        List<String> names = new ArrayList<>(regionProvider.getDistrictNames(stateName));
        names.add(ALL);
        return names;
    }

    public void setRegionProvider(RegionProvider regionProvider) {
        this.regionProvider = regionProvider;
    }
}
