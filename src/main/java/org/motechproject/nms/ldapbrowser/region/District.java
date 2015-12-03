package org.motechproject.nms.ldapbrowser.region;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "nms_districts")
@NamedQueries({
    @NamedQuery(name = District.DISTRICT_NAMES, query = "SELECT d.name from District d WHERE d.state.name = :state_name"),
    @NamedQuery(name = District.FIND_DISTRICT, query = "SELECT d from District d WHERE d.name = :district_name AND d.state.name = :state_name")
})
public class District {

    public static final String DISTRICT_NAMES = "all_district_names";
    public static final String FIND_DISTRICT = "find_district";
    public static final String STATE_NAME_PARAM = "state_name";
    public static final String DISTRICT_NAME_PARAM = "district_name";

    @Id
    private Long id;

    private Long code;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id_OID")
    private State state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
