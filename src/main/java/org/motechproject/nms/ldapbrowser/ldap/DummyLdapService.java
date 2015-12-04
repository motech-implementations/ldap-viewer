package org.motechproject.nms.ldapbrowser.ldap;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
public class DummyLdapService implements LdapUserService {

    private final List<LdapUser> users = new LinkedList<>();

    @PostConstruct
    public void init() {
        users.add(new LdapUser("admin", "password", "National Admin", "nadmin@motechproject.org",
                null, null, true));
        users.add(new LdapUser("nviewer", "password", "National Viewer", "nviewer@motechproject.org",
                null, null, false));
        users.add(new LdapUser("sadmin", "password", "DELHI Admin", "sadmin@motechproject.org",
                "DELHI", null, true));
        users.add(new LdapUser("sviewer", "password", "DELHI Viewer", "sviewer@motechproject.org",
                "DELHI", null, false));
        users.add(new LdapUser("dadmin", "password", "District Admin", "dadmin@motechproject.org",
                "DELHI", "Saket", true));
        users.add(new LdapUser("dviewer", "password", "District Viewer", "dviewer@motechproject.org",
                "DELHI", "Saket", false));
    }

    @Override
    public LdapUser authenticate(String username, String password) {
        LdapUser user = getUser(username);
        return user.getPassword().equals(password) ? user : null;
    }

    @Override
    public LdapUser getUser(String username) {
        for (LdapUser user : users) {
            if (Objects.equals(username, user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<LdapUser> getUsers(UsersQuery query, String currentUsername) {
        List<LdapUser> result = new ArrayList<>(users);

        LdapUser currentUser = getUser(currentUsername);
        if (currentUser == null) {
            throw new IllegalStateException("User not found: " + currentUsername);
        }

        filter(result, currentUser.getState(), currentUser.getDistrict());

        return paginate(result, query.getStart(), query.getPageSize());
    }

    @Override
    public long countUsers(String currentUsername) {
        List<LdapUser> result = new ArrayList<>(users);
        LdapUser currentUser = getUser(currentUsername);

        filter(result, currentUser.getState(), currentUser.getDistrict());

        return result.size();
    }


    @Override
    public LdapUser saveUser(LdapUser user) {
        LdapUser existing = getUser(user.getUsername());
        if (existing != null){
            // preserve password if not modified
            if (StringUtils.isEmpty(user.getPassword())) {
                user.setPassword(existing.getPassword());
            } else {
                // change password
                user.setPassword(user.getPassword());
            }

            // delete existing
            deleteUser(user.getUsername());
        } else {
            // new user, just encode the password
            user.setPassword(user.getPassword());
        }

        users.add(user);
        return user;
    }

    @Override
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

    private List<LdapUser> paginate(List<LdapUser> users, int start, int pageSize) {
        int from = start * pageSize;
        int to = Math.min(users.size(), ((start + 1) * pageSize) + 1);
        return users.subList(from, to);
    }
}
