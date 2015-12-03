package org.motechproject.nms.ldapbrowser.region;

import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class RegionServiceImpl implements RegionService {

    private static final String ALL = "ALL";

    @Inject
    private RegionalRepository regionalRepository;

    @Override
    public List<String> availableStateNames() {
        List<String> names = regionalRepository.getStateNames();
        names.add(ALL);
        return names;
    }

    @Override
    public List<String> availableDistrictNames(String stateName) {
        List<String> names = regionalRepository.getDistrictNames(stateName);
        names.add(ALL);
        return names;
    }

    @Override
    public boolean stateExists(String stateName) {
        return regionalRepository.getStateByName(stateName) != null;
    }

    @Override
    public boolean districtExists(String stateName, String districtName) {
        return regionalRepository.findDistrict(stateName, districtName) != null;
    }
}
