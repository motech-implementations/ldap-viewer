package org.motechproject.nms.ldapbrowser.region.dummy;

import org.motechproject.nms.ldapbrowser.region.RegionProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DummyRegionProvider implements RegionProvider {

    @Override
    public List<String> getStateNames() {
        return Collections.singletonList("DELHI");
    }

    @Override
    public List<String> getDistrictNames(String stateName) {
        if ("DELHI".equals(stateName)) {
            return Arrays.asList("Saket", "DeLhi2");
        } else {
            return Arrays.asList("dist1", "dist2");
        }
    }
}
