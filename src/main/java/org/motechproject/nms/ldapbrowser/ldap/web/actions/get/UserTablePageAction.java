package org.motechproject.nms.ldapbrowser.ldap.web.actions.get;

import org.motechproject.nms.ldapbrowser.ldap.web.Views;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.AbstractPageAction;

public class UserTablePageAction extends AbstractPageAction {

    @Override
    public void execute() {
        printView(Views.USER_TABLE_VIEW);
    }
}
