package org.motechproject.nms.ldapbrowser.ldap.ex;

public class LdapWriteException extends RuntimeException {

    public LdapWriteException(String message) {
        super(message);
    }

    public LdapWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
