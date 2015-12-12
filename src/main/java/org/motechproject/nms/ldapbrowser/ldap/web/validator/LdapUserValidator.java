package org.motechproject.nms.ldapbrowser.ldap.web.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.LdapUserService;

import java.util.ArrayList;
import java.util.List;

public class LdapUserValidator {

    private LdapUserService ldapUserService;

    public List<LdapValidatorError> validate(LdapUser user) {
        List<LdapValidatorError> errors = new ArrayList<>();

        if (!user.isUiEdit() && ldapUserService.getUser(user.getUsername()) != null) {
            errors.add(new LdapValidatorError("username", "user.save.error.username.taken"));
        }
        if (StringUtils.isEmpty(user.getUsername())) {
            errors.add(new LdapValidatorError("username", "user.save.error.username.req"));
        }
        if (!user.isUiEdit() && StringUtils.isEmpty(user.getPassword())) {
            errors.add(new LdapValidatorError("password", "user.save.error.password.req"));
        }
        if (!StringUtils.isEmpty(user.getPassword()) && user.getPassword().length() < 5) {
            errors.add(new LdapValidatorError("password", "user.save.error.password.short"));
        }

        return errors;
    }

    public void setLdapUserService(LdapUserService ldapUserService) {
        this.ldapUserService = ldapUserService;
    }
}
