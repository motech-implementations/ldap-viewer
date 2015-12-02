package org.motechproject.nms.ldapbrowser.region;

import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class RegionServiceImpl implements RegionService {

    @Inject
    private RegionalRepository regionalRepository;

    @Override
    public List<String> availableStateNames() {
        return regionalRepository.getStateNames();
    }

    @Override
    public List<String> availableDistrictNames() {
        return regionalRepository.getDistrictNames();
    }
}
