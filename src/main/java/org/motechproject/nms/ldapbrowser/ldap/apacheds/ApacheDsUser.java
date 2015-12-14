package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import org.apache.directory.api.ldap.model.name.Dn;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class ApacheDsUser extends LdapUser {

    @JsonIgnore
    @XmlTransient
    private Dn dn;

    public Dn getDn() {
        return dn;
    }

    public void setDn(Dn dn) {
        this.dn = dn;
    }
}
