package org.motechproject.nms.ldapbrowser.ldap.web.actions.post;

import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.web.Views;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.AbstractPageAction;
import org.motechproject.nms.ldapbrowser.ldap.web.validator.LdapUserValidator;
import org.motechproject.nms.ldapbrowser.ldap.web.validator.LdapValidatorError;
import org.motechproject.nms.ldapbrowser.support.web.MessageHelper;

import java.io.IOException;
import java.util.List;

public class SaveUserAction extends AbstractPageAction {

    private LdapUserValidator validator;

    @Override
    public void execute() throws IOException {
        LdapUser user = new ObjectMapper().readValue(getInputStream(), LdapUser.class);

        List<LdapValidatorError> errors = validator.validate(user);

        if (errors.isEmpty()) {
            getLdapUserService().saveUser(user);

            MessageHelper.addSuccessAttribute(getThymeleafContext(), "user.saved");
            printView(Views.USER_TABLE_VIEW);
        } else {
            setModelVariable(Views.USER_VAR, user);
            addRegionalDataToModel(getCurrentUser(), user);

            for (LdapValidatorError error : errors) {
                // TODO: multiple errors
                MessageHelper.addErrorAttribute(getThymeleafContext(), error.getMessage());
            }
        }
    }

    public void setValidator(LdapUserValidator validator) {
        this.validator = validator;
    }
}
