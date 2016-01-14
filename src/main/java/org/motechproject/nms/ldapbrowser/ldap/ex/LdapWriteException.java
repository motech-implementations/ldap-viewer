package org.motechproject.nms.ldapbrowser.ldap.ex;

import java.util.List;

public class LdapWriteException extends RuntimeException {

    private List<String> underlyingLdapReasons;

    public LdapWriteException(String message) {
        this(message, (List) null);
    }

    public LdapWriteException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public LdapWriteException(String message, List<String> errors) {
        super(message);
        this.underlyingLdapReasons = errors;
    }

    public LdapWriteException(String message, Throwable cause, List<String> errors) {
        super(message, cause);
        this.underlyingLdapReasons = errors;
    }

    public String getUnderlyingLdapReasons() {
        StringBuilder sb = new StringBuilder();
        if (underlyingLdapReasons != null) {
            for (String msg : underlyingLdapReasons) {
                sb.append(msg + " ");
            }
        }

        return sb.toString();
    }
}
