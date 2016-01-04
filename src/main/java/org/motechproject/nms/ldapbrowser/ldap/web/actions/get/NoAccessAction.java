package org.motechproject.nms.ldapbrowser.ldap.web.actions.get;

import org.motechproject.nms.ldapbrowser.ldap.web.Views;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.AbstractPageAction;

import java.io.IOException;

public class NoAccessAction extends AbstractPageAction {

    @Override
    public void execute() throws IOException {
        printView(Views.ACCESS_DENIED_ERROR);
    }
}
