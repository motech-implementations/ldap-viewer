package org.motechproject.nms.ldapbrowser.support.web;

import java.util.List;

public class DtData<T> {

    private List<T> data;
    //private int draw;
    private long recordsTotal;
    private int recordsFiltered;

    public DtData() {
    }

    public DtData(List<T> data, long recordsTotal) {
        this.data = data;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = data.size();
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public int getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(int recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }
}
