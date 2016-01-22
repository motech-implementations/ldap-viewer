package org.motechproject.nms.ldapbrowser.ldap.web.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.nms.ldapbrowser.ldap.LdapRole;
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
        if (StringUtils.isEmpty(user.getName())) {
            errors.add(new LdapValidatorError("username", "user.save.error.name.req"));
        }
        if (!user.isUiEdit() && StringUtils.isEmpty(user.getPassword())) {
            errors.add(new LdapValidatorError("password", "user.save.error.password.req"));
        }
        if (!StringUtils.isEmpty(user.getPassword()) && user.getPassword().length() < 5) {
            errors.add(new LdapValidatorError("password", "user.save.error.password.short"));
        }
        if (StringUtils.isNotBlank(user.getPassword()) && StringUtils.contains(user.getUsername(), user.getPassword())) {
           errors.add(new LdapValidatorError("password", "user.save.error.password.identical"));
        }
        if (StringUtils.isNotBlank(user.getDistrict()) &&
                (user.getRoles().size() == 1 && !user.getRoles().get(0).getDistrict().equals(user.getDistrict())) || user.getRoles().size() > 1) {
            errors.add(new LdapValidatorError("districts", "user.save.error.district.roles"));
        }
        if (StringUtils.isNotBlank(user.getState())) {
            for (LdapRole role : user.getRoles()) {
                if (!role.getState().equals(user.getState())) {
                    errors.add(new LdapValidatorError("states", "user.save.error.state.roles"));
                }
            }
        }

        return errors;
    }

    public void setLdapUserService(LdapUserService ldapUserService) {
        this.ldapUserService = ldapUserService;
    }
}
