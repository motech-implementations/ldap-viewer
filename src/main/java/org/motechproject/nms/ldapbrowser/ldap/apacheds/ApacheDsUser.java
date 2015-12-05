package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.directory.api.ldap.model.name.Dn;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;

public class ApacheDsUser extends LdapUser {

    @JsonIgnore
    private Dn dn;

    public Dn getDn() {
        return dn;
    }

    public void setDn(Dn dn) {
        this.dn = dn;
    }
}
