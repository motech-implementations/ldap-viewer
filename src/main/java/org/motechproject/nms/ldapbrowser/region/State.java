package org.motechproject.nms.ldapbrowser.region;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "nms_states")
@NamedQuery(name = State.STATE_NAMES, query = "SELECT s.name from State s")
public class State {

    public static final String STATE_NAMES = "state_names";

    @Id
    private Long id;

    private Long code;

    private String name;

    @OneToMany(mappedBy = "state")
    private List<District> districts;

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

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }
}
