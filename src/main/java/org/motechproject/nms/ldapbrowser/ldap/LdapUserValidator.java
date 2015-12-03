package org.motechproject.nms.ldapbrowser.ldap;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.inject.Inject;

@Component
public class LdapUserValidator implements Validator {

    @Inject
    private LdapUserService ldapUserService;

    @Override
    public boolean supports(Class<?> clazz) {
        return LdapUser.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LdapUser user = (LdapUser) target;
        if (!user.isUiEdit() && ldapUserService.getUser(user.getUsername()) != null) {
            errors.rejectValue("username", "user.save.error.username.taken");
        }
        if (StringUtils.isEmpty(user.getUsername())) {
            errors.rejectValue("username", "user.save.error.username.req");
        }
        if (!user.isUiEdit() && StringUtils.isEmpty(user.getPassword())) {
            errors.rejectValue("password", "user.save.error.password.req");
        }
        if (!StringUtils.isEmpty(user.getPassword()) && user.getPassword().length() < 5) {
            errors.rejectValue("password", "user.save.error.password.short");
        }
    }
}
