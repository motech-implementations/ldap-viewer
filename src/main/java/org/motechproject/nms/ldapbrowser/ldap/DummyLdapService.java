package org.motechproject.nms.ldapbrowser.ldap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
public class DummyLdapService implements LdapUserService {

    private final List<LdapUser> users = new LinkedList<>();

    @Inject
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        users.add(new LdapUser("admin", passwordEncoder.encode("password"), "National Admin", "nadmin@motechproject.org",
                null, null, true));
        users.add(new LdapUser("nviewer", passwordEncoder.encode("password"), "National Viewer", "nviewer@motechproject.org",
                null, null, false));
        users.add(new LdapUser("sadmin", passwordEncoder.encode("password"), "Pomorskie Admin", "sadmin@motechproject.org",
                "Pomorskie", null, false));
        users.add(new LdapUser("sviewer", passwordEncoder.encode("password"), "Pomorskie Viewer", "sviewer@motechproject.org",
                "Pomorskie", null, false));
        users.add(new LdapUser("dadmin", passwordEncoder.encode("password"), "District Admin", "dadmin@motechproject.org",
                "Pomorskie", "Gdynia", false));
        users.add(new LdapUser("dviewer", passwordEncoder.encode("password"), "District Viewer", "dviewer@motechproject.org",
                "Pomorskie", "Gdynia", false));
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
    public List<LdapUser> getUsers(UsersQuery query) {
        List<LdapUser> result = new ArrayList<>(users);
        //filter(result, query.getState(), query.getDistrict());
        return paginate(result, query.getStart(), query.getPageSize());
    }

    @Override
    public long countUsers(UsersQuery query) {
        List<LdapUser> result = new ArrayList<>(users);
        //filter(result, query.getState(), query.getDistrict());
        return result.size();
    }


    @Override
    public LdapUser saveUser(LdapUser user) {
        LdapUser existing = getUser(user.getUsername());
        if (existing != null){
            // preserve password if not modified
            if (StringUtils.isEmpty(user.getPassword())) {
                user.setPassword(existing.getPassword());
            }
            deleteUser(user.getUsername());
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
            if (!Objects.equals(state, user.getState()) || !Objects.equals(district, user.getDistrict())) {
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
