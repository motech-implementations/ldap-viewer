package org.motechproject.nms.ldapbrowser.region;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Repository
@Transactional(readOnly = true)
public class RegionalRepository {

    @PersistenceContext
    private EntityManager em;

    public List<String> getStateNames() {
        return em.createNamedQuery(State.STATE_NAMES, String.class).getResultList();
    }

    public List<String> getDistrictNames(String stateName) {
        return em.createNamedQuery(District.DISTRICT_NAMES, String.class)
                .setParameter(District.STATE_NAME_PARAM, stateName)
                .getResultList();
    }

    public State getStateByName(String stateName) {
        return em.createQuery(State.BY_NAME, State.class)
                .setParameter(State.STATE_NAME_PARAM, stateName)
                .getSingleResult();
    }

    public District findDistrict(String stateName, String districtName) {
        return em.createQuery(District.FIND_DISTRICT, District.class)
                .setParameter(District.STATE_NAME_PARAM, stateName)
                .setParameter(District.DISTRICT_NAME_PARAM, districtName)
                .getSingleResult();
    }
}
