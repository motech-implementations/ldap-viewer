package org.motechproject.nms.ldapbrowser.ldap.web.actions;

import org.motechproject.nms.ldapbrowser.ldap.LdapUser;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class CreateUserPageAction extends AbstractPageAction {

    private static final String VIEW_NAME = "ldap/user";

    @Override
    public void execute() {
        LdapUser editedUser = new LdapUser();
        LdapUser currentUser = getCurrentUser();

        Map<String, Object> model = new HashMap<>();
        model.put("user", editedUser);
        addRegionalDataToModel(model, currentUser, editedUser);

        getTemplateEngine().process(VIEW_NAME, getThymeleafContext(), new PrintWriter(getOutputStream()));
    }
}
