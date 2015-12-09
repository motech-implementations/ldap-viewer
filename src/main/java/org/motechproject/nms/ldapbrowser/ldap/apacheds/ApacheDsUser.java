package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import org.apache.directory.api.ldap.model.name.Dn;
import org.codehaus.jackson.annotate.JsonIgnore;
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
