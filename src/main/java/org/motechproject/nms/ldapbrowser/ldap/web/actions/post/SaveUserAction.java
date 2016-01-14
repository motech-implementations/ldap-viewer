package org.motechproject.nms.ldapbrowser.ldap.web.actions.post;

import org.apache.commons.lang.StringUtils;
import org.motechproject.nms.ldapbrowser.ldap.LdapRole;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.RoleType;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapWriteException;
import org.motechproject.nms.ldapbrowser.ldap.web.Views;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.AbstractPageAction;
import org.motechproject.nms.ldapbrowser.ldap.web.validator.LdapUserValidator;
import org.motechproject.nms.ldapbrowser.ldap.web.validator.LdapValidatorError;
import org.motechproject.nms.ldapbrowser.support.web.LdapUserDto;
import org.motechproject.nms.ldapbrowser.support.web.MessageHelper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SaveUserAction extends AbstractPageAction {

    private LdapUserValidator validator;

    private static final String ROLE_PREFIX = "role_";
    private static final String DISTRICT = "district_";
    private static final String STATE = "state_";

    private static final String UI_EDIT = "uiEdit";

    private static final String NATIONAL_LEVEL = "National level";
    private static final String STATE_LEVEL = "State level";

    private static final String VIEWER = "V";
    private static final String USER_ADMIN = "UA";

    @Override
    public void execute() throws IOException {
        LdapUser user = mapParametersToUsers(getParametersMap());

        List<LdapValidatorError> errors = validator.validate(user);

        if (errors.isEmpty()) {
            try {
                getLdapUserService().saveUser(user);
            } catch (LdapWriteException e) {
                setModelVariable(Views.USER_VAR, new LdapUserDto(user));
                addRegionalDataToModel(getCurrentUser(), user);

                if (user.isUiEdit()) {
                    setModelVariable(UI_EDIT, true);
                }

                MessageHelper.addErrorAttribute(getThymeleafContext(), e.getMessage().concat(e.getUnderlyingLdapReasons()));
                printView(Views.USER_EDIT_VIEW);
                return;
            }

            MessageHelper.addSuccessAttribute(getThymeleafContext(), "user.saved");
            printView(Views.USER_TABLE_VIEW);
        } else {
            setModelVariable(Views.USER_VAR, new LdapUserDto(user));
            addRegionalDataToModel(getCurrentUser(), user);

            if (user.isUiEdit()) {
                setModelVariable(UI_EDIT, true);
            }

            for (LdapValidatorError error : errors) {
                // TODO: multiple errors
                MessageHelper.addErrorAttribute(getThymeleafContext(), error.getMessage());
            }
            printView(Views.USER_EDIT_VIEW);
        }
    }

    public void setValidator(LdapUserValidator validator) {
        this.validator = validator;
    }

    private LdapUser mapParametersToUsers(Map<String, String[]> parametersMap) {
        LdapUser user = new LdapUser();

        for (Map.Entry<String, String[]> entry : parametersMap.entrySet()) {
            switch (entry.getKey()) {
                case "username":
                    user.setUsername(entry.getValue()[0]);
                    break;
                case "name":
                    user.setName(entry.getValue()[0]);
                    break;
                case "password":
                    user.setPassword(entry.getValue()[0]);
                    break;
                case "state":
                    user.setState(NATIONAL_LEVEL.equals(entry.getValue()[0]) ? StringUtils.EMPTY : entry.getValue()[0]);
                    break;
                case "district":
                    user.setDistrict(STATE_LEVEL.equals(entry.getValue()[0]) ? StringUtils.EMPTY : entry.getValue()[0]);
                    break;
                case "email":
                    user.setEmail(entry.getValue()[0]);
                    break;
                case "phone":
                    user.setWorkNumber(entry.getValue()[0]);
                    break;
                case "mobile":
                    user.setMobileNumber(entry.getValue()[0]);
                    break;
                case "uiEdit":
                    user.setUiEdit(entry.getValue()[0].equals("true"));
                    break;
                case "nationalAdmin":
                    user.getRoles().add(new LdapRole(null ,null, true));
                    break;
                case "nationalView":
                    user.getRoles().add(new LdapRole(null ,null, false));
                    break;
                default:
                    parseUserRole(user, entry.getKey(), entry.getValue());
            }
        }

        return user;
    }

    private void parseUserRole(LdapUser user, String property, String[] values) {
        if (property.startsWith(ROLE_PREFIX)) {
            if (property.substring(ROLE_PREFIX.length()).startsWith(STATE)) {
                String state = extractStateDistrictName(property, STATE.length());
                RoleType type = extractRoleType(values[0]);
                if (type != RoleType.NONE) {
                    user.getRoles().add(new LdapRole(state, "", type == RoleType.USER_ADMIN));
                }
            } else if (property.substring(ROLE_PREFIX.length()).startsWith(DISTRICT)) {
                String stateDistrict = extractStateDistrictName(property, DISTRICT.length());
                String[] locations = stateDistrict.split("__");
                RoleType type = extractRoleType(values[0]);
                if (type != RoleType.NONE) {
                    user.getRoles().add(new LdapRole(locations[0], locations[1], type == RoleType.USER_ADMIN));
                }
            }
        }
    }

    private RoleType extractRoleType(String value) {
        if (VIEWER.toString().equals(value)) {
            return RoleType.VIEWER;
        } else if (USER_ADMIN.toString().equals(value)) {
            return RoleType.USER_ADMIN;
        } else {
            return RoleType.NONE;
        }
    }

    private String extractStateDistrictName(String expression, int offset) {
        return expression.substring(ROLE_PREFIX.length() + offset);
    }
}
