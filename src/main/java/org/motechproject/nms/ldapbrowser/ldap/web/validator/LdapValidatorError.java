package org.motechproject.nms.ldapbrowser.ldap.web.validator;

public class LdapValidatorError {

    private final String field;
    private final String message;

    public LdapValidatorError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
