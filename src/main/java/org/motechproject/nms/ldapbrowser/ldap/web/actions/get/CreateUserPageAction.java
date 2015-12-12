package org.motechproject.nms.ldapbrowser.ldap.web.actions.get;

import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.web.Views;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.AbstractPageAction;

import java.io.IOException;

public class CreateUserPageAction extends AbstractPageAction {

    @Override
    public void execute() throws IOException {
        LdapUser editedUser = new LdapUser();
        LdapUser currentUser = getCurrentUser();

        setModelVariable(Views.USER_VAR, editedUser);
        addRegionalDataToModel(currentUser, editedUser);

        printView(Views.USER_EDIT_VIEW);
    }
}
