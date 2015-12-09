package org.motechproject.nms.ldapbrowser.ldap.web.actions;

import org.motechproject.nms.ldapbrowser.ldap.LdapUser;

public class EditUserPageAction extends AbstractPageAction {

    private String username;

    @Override
    public void execute() throws Exception {
        LdapUser editedUser = getLdapUserService().getUser(username);
        LdapUser currentUser = getCurrentUser();

        //Map<String, Object>
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
