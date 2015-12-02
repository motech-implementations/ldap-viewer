package org.motechproject.nms.ldapbrowser.ldap;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
public class DummyLdapService implements LdapUserService {

    private static final List<User> USERS = new LinkedList<>();

    static {
        USERS.add(new User("admin", "pass", "National Admin", "nadmin@motechproject.org", null, null, true));
        USERS.add(new User("nviewer", "pass", "National Viewer", "nviewer@motechproject.org", null, null, false));
        USERS.add(new User("sadmin", "pass", "Pomorskie Admin", "sadmin@motechproject.org", "Pomorskie", null, false));
        USERS.add(new User("sviewer", "pass", "Pomorskie Viewer", "sviewer@motechproject.org", "Pomorskie", null, false));
        USERS.add(new User("dadmin", "pass", "District Admin", "dadmin@motechproject.org", "Pomorskie", "Gdynia", false));
        USERS.add(new User("dviewer", "pass", "District Viewer", "dviewer@motechproject.org", "Pomorskie", "Gdynia", false));
    }

    @Override
    public User getUser(String username) {
        for (User user : USERS) {
            if (Objects.equals(username, user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> getUsers(UsersQuery query) {
        List<User> result = new ArrayList<>(USERS);
        //filter(result, query.getState(), query.getDistrict());
        return paginate(result, query.getStart(), query.getPageSize());
    }

    @Override
    public long countUsers(UsersQuery query) {
        List<User> result = new ArrayList<>(USERS);
        //filter(result, query.getState(), query.getDistrict());
        return result.size();
    }


    @Override
    public User saveUser(User user) {
        deleteUser(user.getUsername());
        USERS.add(user);
        return user;
    }

    @Override
    public void deleteUser(String username) {
        Iterator<User> it = USERS.iterator();
        while (it.hasNext()) {
            User user = it.next();
            if (Objects.equals(username, user.getUsername())) {
                it.remove();
                return;
            }
        }
    }

    private void filter(List<User> users, String state, String district) {
        Iterator<User> it = users.iterator();
        while (it.hasNext()) {
            User user = it.next();
            if (!Objects.equals(state, user.getState()) || !Objects.equals(district, user.getDistrict())) {
                it.remove();
            }
        }
    }

    private List<User> paginate(List<User> users, int start, int pageSize) {
        int from = start * pageSize;
        int to = Math.min(users.size(), ((start + 1) * pageSize) + 1);
        return users.subList(from, to);
    }
}
