package org.motechproject.nms.ldapbrowser.ldap.web.actions.post;

import org.motechproject.nms.ldapbrowser.ldap.web.Views;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.AbstractPageAction;

import java.io.IOException;

public class DeleteUserAction extends AbstractPageAction {

    private String username;

    @Override
    public void execute() throws IOException {
        getLdapUserService().deleteUser(username);
        printView(Views.USER_TABLE_VIEW);
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
