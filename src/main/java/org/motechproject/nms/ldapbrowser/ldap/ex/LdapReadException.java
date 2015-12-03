package org.motechproject.nms.ldapbrowser.ldap.ex;

public class LdapReadException extends RuntimeException {

    public LdapReadException() {
    }

    public LdapReadException(String message) {
        super(message);
    }

    public LdapReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
