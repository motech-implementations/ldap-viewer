package org.motechproject.nms.ldapbrowser.ldap.web.actions.get;

import org.apache.commons.lang.StringUtils;
import org.motechproject.nms.ldapbrowser.ldap.LdapRole;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.web.Views;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.AbstractPageAction;
import org.motechproject.nms.ldapbrowser.support.web.LdapUserDto;

import java.io.IOException;

public class EditUserPageAction extends AbstractPageAction {

    private static final String UI_EDIT = "uiEdit";

    private String username;

    @Override
    public void execute() throws IOException {
        LdapUser editedUser = getLdapUserService().getUser(username);
        LdapUser currentUser = getCurrentUser();

        setModelVariable(Views.USER_VAR, new LdapUserDto(editedUser));
        setModelVariable(UI_EDIT, true);
        setModelVariable(USER_ADMIN_MODE, currentUser.getRoles().contains(new LdapRole(StringUtils.EMPTY, StringUtils.EMPTY, true)) &&
                StringUtils.isBlank(editedUser.getDistrict()) && StringUtils.isBlank(editedUser.getState()));
        addRegionalDataToModel(currentUser, editedUser);

        printView(Views.USER_EDIT_VIEW);
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
