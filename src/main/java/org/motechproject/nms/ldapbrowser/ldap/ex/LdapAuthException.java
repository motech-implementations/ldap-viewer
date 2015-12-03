package org.motechproject.nms.ldapbrowser.ldap.ex;

public class LdapAuthException extends RuntimeException {

    public LdapAuthException() {
    }

    public LdapAuthException(String message) {
        super(message);
    }

    public LdapAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
