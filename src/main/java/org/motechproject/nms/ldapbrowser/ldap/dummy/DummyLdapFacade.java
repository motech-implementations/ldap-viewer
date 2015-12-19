package org.motechproject.nms.ldapbrowser.ldap.dummy;

import org.motechproject.nms.ldapbrowser.ldap.LdapFacade;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DummyLdapFacade implements LdapFacade {

    private final List<LdapUser> users = new LinkedList<>();

    @PostConstruct
    public void init() {
//        users.add(new LdapUser("admin", "password", "National Admin", "nadmin@motechproject.org",
//                null, null, true));
//        users.add(new LdapUser("nviewer", "password", "National Viewer", "nviewer@motechproject.org",
//                null, null, false));
//        users.add(new LdapUser("sadmin", "password", "DELHI Admin", "sadmin@motechproject.org",
//                "DELHI", null, true));
//        users.add(new LdapUser("sviewer", "password", "DELHI Viewer", "sviewer@motechproject.org",
//                "DELHI", null, false));
//        users.add(new LdapUser("dadmin", "password", "District Admin", "dadmin@motechproject.org",
//                "DELHI", "Saket", true));
//        users.add(new LdapUser("dviewer", "password", "District Viewer", "dviewer@motechproject.org",
//                "DELHI", "Saket", false));
    }

    @Override
    public LdapUser findAndAuthenticate(String username, String password) {
        LdapUser user = findUser(username);
        return user.getPassword().equals(password) ? user : null;
    }

    @Override
    public LdapUser findUser(String username) {
        for (LdapUser user : users) {
            if (Objects.equals(username, user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<LdapUser> getUsers(String adminUsername, String adminPassword) {
        List<LdapUser> result = new ArrayList<>(users);

        LdapUser currentUser = findUser(adminUsername);
        if (currentUser == null) {
            throw new IllegalStateException("User not found: " + adminUsername);
        }

        filter(result, currentUser.getState(), currentUser.getDistrict());

        return result;
    }

    @Override
    public void deleteUser(String username, String adminUsername, String adminPassword) {

    }

    @Override
    public void addLdapUserEntry(LdapUser user, String creatorUsername, String creatorPassword) {
        users.add(user);
    }

    //@Override
    public void deleteUser(String username) {
        Iterator<LdapUser> it = users.iterator();
        while (it.hasNext()) {
            LdapUser user = it.next();
            if (Objects.equals(username, user.getUsername())) {
                it.remove();
                return;
            }
        }
    }

    private void filter(List<LdapUser> users, String state, String district) {
        Iterator<LdapUser> it = users.iterator();
        while (it.hasNext()) {
            LdapUser user = it.next();
            if ((!LdapUser.ALL.equals(state) && !state.equals(user.getState())) ||
                    (!LdapUser.ALL.equals(district) && !district.equals(user.getDistrict()))) {
                it.remove();
            }
        }
    }
}
