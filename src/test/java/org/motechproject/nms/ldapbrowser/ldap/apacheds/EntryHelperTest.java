package org.motechproject.nms.ldapbrowser.ldap.apacheds;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motechproject.nms.ldapbrowser.ldap.RoleType;

import static junit.framework.TestCase.assertEquals;

public class EntryHelperTest {

    private EntryHelper entryHelper = new EntryHelper();

    @Before
    public void setUp() {
        entryHelper.setDc("nms");
        entryHelper.setNationalRole("National View");
        entryHelper.setNationalUserAdminRole("National User Administrator");
        entryHelper.setUsersOu("users");
        entryHelper.setRolesOu("roles");
        entryHelper.setAdminRoleSuffix("User Administrator");
        entryHelper.setRoleSuffix("View");
    }

    @Test
    public void shouldProperlyBuildDn() {
        String districtView = entryHelper.buildDn("Delhi", "Alipur", RoleType.VIEWER);
        assertEquals("cn=Alipur View,cn=Delhi View,cn=National View,ou=roles,dc=nms", districtView);

        String stateView = entryHelper.buildDn("Delhi", "", RoleType.VIEWER);
        assertEquals("cn=Delhi View,cn=National View,ou=roles,dc=nms", stateView);

        String nationalView = entryHelper.buildDn("", "", RoleType.VIEWER);
        assertEquals("cn=National View,ou=roles,dc=nms", nationalView);

        String districtAdmin = entryHelper.buildDn("Delhi", "Alipur", RoleType.USER_ADMIN);
        assertEquals("cn=Alipur User Administrator,cn=Delhi User Administrator,cn=National User Administrator,ou=roles,dc=nms", districtAdmin);

        String stateAdmin = entryHelper.buildDn("Delhi", "", RoleType.USER_ADMIN);
        assertEquals("cn=Delhi User Administrator,cn=National User Administrator,ou=roles,dc=nms", stateAdmin);

        String nationalAdmin = entryHelper.buildDn("", "", RoleType.USER_ADMIN);
        assertEquals("cn=National User Administrator,ou=roles,dc=nms", nationalAdmin);

        String districtLevelUser = entryHelper.buildDn("Delhi", "Alipur", RoleType.NONE);
        assertEquals("cn=Alipur,cn=Delhi,ou=users,dc=nms", districtLevelUser);

        String stateLevelUser = entryHelper.buildDn("Delhi", "", RoleType.NONE);
        assertEquals("cn=Delhi,ou=users,dc=nms", stateLevelUser);

        String nationalLevelUser = entryHelper.buildDn("", "", RoleType.NONE);
        assertEquals("ou=users,dc=nms", nationalLevelUser);
    }
}
