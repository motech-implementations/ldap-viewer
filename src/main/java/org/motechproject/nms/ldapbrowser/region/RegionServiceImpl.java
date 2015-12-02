package org.motechproject.nms.ldapbrowser.region;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@Service
public class RegionServiceImpl implements RegionService {

    private static final Map<String, List<String>> REGION_DATA = new LinkedHashMap<>();

    static {
        REGION_DATA.put("Pomorskie", Arrays.asList("Gdynia", "Gdansk", "Sopot"));
        REGION_DATA.put("Mazowieckie", Arrays.asList("Mlawa", "Warszawa"));
    }

    @Override
    public List<String> availableStateNames() {
        return new ArrayList<>(new TreeSet<>(REGION_DATA.keySet()));
    }

    @Override
    public List<String> availableDistrictNames() {
        List<String> result = new ArrayList<>();
        for (List<String> districts : REGION_DATA.values()) {
            result.addAll(districts);
        }
        return result;
    }
}
