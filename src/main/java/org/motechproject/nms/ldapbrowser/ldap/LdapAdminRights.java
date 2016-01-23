package org.motechproject.nms.ldapbrowser.ldap;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains info about user's rights to assign admin roles to other users.
 */
public class LdapAdminRights {

    private List<String> states;
    private boolean editNationalRoles;
    private boolean nationalAdmin;
    private boolean masterAdmin;

    public LdapAdminRights() {
        this(false, false, false, new ArrayList<String>());
    }

    public LdapAdminRights(boolean editNationalRoles, boolean masterAdmin, boolean nationalAdmin, List<String> states) {
        this.editNationalRoles = editNationalRoles;
        this.masterAdmin = masterAdmin;
        this.nationalAdmin = nationalAdmin;
        this.states = states;
    }

    public List<String> getStates() {
        return states;
    }

    public void setStates(List<String> states) {
        this.states = states;
    }

    public boolean isMasterAdmin() {
        return masterAdmin;
    }

    public void setMasterAdmin(boolean masterAdmin) {
        this.masterAdmin = masterAdmin;
    }

    public boolean isNationalAdmin() {
        return nationalAdmin;
    }

    public void setNationalAdmin(boolean nationalAdmin) {
        this.nationalAdmin = nationalAdmin;
    }

    public boolean isEditNationalRoles() {
        return editNationalRoles;
    }

    public void setEditNationalRoles(boolean editNationalRoles) {
        this.editNationalRoles = editNationalRoles;
    }
}
