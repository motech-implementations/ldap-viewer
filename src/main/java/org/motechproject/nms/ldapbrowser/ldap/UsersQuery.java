package org.motechproject.nms.ldapbrowser.ldap;

public class UsersQuery {

    private int start = 0;
    private int pageSize = 10;

    public UsersQuery() {
    }

    public UsersQuery(int start, int pageSize) {
        this.start = start;
        this.pageSize = pageSize;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
