package org.motechproject.nms.ldapbrowser.ldap.ex;

public class LdapConnectionException extends RuntimeException {

    public LdapConnectionException() {
    }

    public LdapConnectionException(String message) {
        super(message);
    }

    public LdapConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
