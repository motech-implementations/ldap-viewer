package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.motechproject.nms.ldapbrowser.ldap.AttributeNames;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapAuthException;
import org.motechproject.nms.ldapbrowser.ldap.ex.LdapReadException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class EntryUtil {

    public static ApacheDsUser buildUser(Entry entry) {
        ApacheDsUser ldapUser = new ApacheDsUser();

        ldapUser.setName(getAttributeStrVal(entry, AttributeNames.NAME));
        ldapUser.setEmail(getAttributeStrVal(entry, AttributeNames.EMAIL));
        setStateAndDistrict(entry, ldapUser);

        ldapUser.setDn(entry.getDn());

        return ldapUser;
    }

    private static String getAttributeStrVal(Entry entry, String attrName) {
        try {
            Attribute attr = entry.get(attrName);
            return attr == null ? null : attr.getString();
        } catch (LdapInvalidAttributeValueException e) {
            throw new LdapReadException("Unable to read attribute: " + attrName, e);
        }
    }

    private static void setStateAndDistrict(Entry entry, LdapUser user) {
        Dn dn = entry.getDn();
        List<Rdn> rdns = dn.getRdns();
        // remove the username rdn
        List<Rdn> rdnHolderList = new LinkedList<>(rdns.subList(1, rdns.size()));

        // leave only cn entries
        Iterator<Rdn> it = rdnHolderList.iterator();
        while (it.hasNext()) {
            Rdn rdn = it.next();
            if (!LdapConstants.CN.equals(rdn.getAva().getType())) {
                it.remove();
            }
        }

        // determine rights

        if (rdnHolderList.size() == 1) {
            // national view
            user.setState(LdapUser.ALL);
            user.setDistrict(LdapUser.ALL);
        } else if (rdnHolderList.size() == 2) {
            // state view
            user.setState(parseRole(rdnHolderList.get(0).getValue()));
            user.setDistrict(LdapUser.ALL);
        } else if (rdnHolderList.size() == 3) {
            // district view
            user.setState(parseRole(rdnHolderList.get(0).getValue()));
            user.setDistrict(parseRole(rdnHolderList.get(1).getValue()));
        } else {
            throw new LdapAuthException("Illegal user state, number of cn entries: " + rdnHolderList.size());
        }
    }

    private static String parseRole(String avaVal) {
        return avaVal.replace(" View", "");
    }

    private EntryUtil() {
    }
}
